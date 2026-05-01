package org.viktor44.jtvision.examples.tvhc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Help file compiler mirroring the Turbo Vision tvhc example.
 *
 * <p>Input: a text file with {@code .topic} directives defining help topics,
 * paragraphs of help text, and {@code {text:alias}} cross-references.
 *
 * <p>Output:
 * <ul>
 *   <li>A Java source file declaring {@code public static final int hcXxx = N}
 *       constants for every topic symbol (the analog of the original .h file).</li>
 *   <li>A simple binary help file carrying topic ids, paragraphs (with the
 *       original tvhc wrap flag and 0xFF-highlighted cross-ref runs), and
 *       resolved cross-reference targets.</li>
 * </ul>
 *
 * <p>Usage: {@code TvHelpCompiler <source.txt> [<help.hlp>] [<Symbols.java>]}.
 */
public class HelpCompiler {

    private static final int MAX_HELP_TOPIC_ID = 16379;
    private static final char COMMAND_CHAR = '.';
    private static final byte[] MAGIC = { 'J', 'T', 'V', 'H', 'E', 'L', 'P', 0 };
    private static final int FORMAT_VERSION = 1;

    private enum State { UNDEFINED, WRAPPING, NOT_WRAPPING }

    /** Topic symbol -> assigned numeric id, recorded in source order. */
    private final LinkedHashMap<String, Integer> symbols = new LinkedHashMap<>();
    /** Forward-reference fix-ups keyed by topic; resolved as topics are seen. */
    private final Map<String, List<Integer>> pendingRefs = new LinkedHashMap<>();
    /** All topics written, in source order. */
    private final List<Topic> topics = new ArrayList<>();

    private String sourceName = "";
    private int lineCount = 0;
    private String pushback;
    private int warnings = 0;
    private int helpCounter = 1; // next id defaults to 2 (1 is reserved, per tvhc)

    public static void main(String[] args) throws IOException {
        System.out.println("Help Compiler  Version 2.0  (Java port)");
        if (args.length < 1) {
            System.out.println();
            System.out.println("  Syntax  TvHelpCompiler <Help text>[.txt] [<Help file>[.hlp] [<Symbol file>[.java]]");
            System.out.println();
            System.out.println("     Help text   = Help file source");
            System.out.println("     Help file   = Compiled help file (binary)");
            System.out.println("     Symbol file = Java source with hcXXX constants");
            System.exit(1);
        }

        String textName = replaceExt(args[0], ".txt", false);
        if (!new File(textName).exists()) {
            System.err.println("Error: File '" + textName + "' not found.");
            System.exit(1);
        }

        String helpName = args.length >= 2
            ? replaceExt(args[1], ".hlp", false)
            : replaceExt(textName, ".hlp", true);
        String symbName = args.length >= 3
            ? replaceExt(args[2], ".java", false)
            : replaceExt(helpName, ".java", true);

        checkOverwrite(helpName);
        checkOverwrite(symbName);

        new HelpCompiler().run(textName, helpName, symbName);
    }

    private void run(String textName, String helpName, String symbName) throws IOException {
        this.sourceName = textName;
        try (BufferedReader in = new BufferedReader(new FileReader(textName))) {
            while (true) {
                skipBlankLines(in);
                String line = getLine(in);
                if (line == null) break;
                pushback = line;
                readTopic(in);
            }
        }

        writeHelpFile(helpName);
        writeSymbolFile(symbName);

        System.out.println("Wrote " + helpName + " (" + topics.size() + " topics)");
        System.out.println("Wrote " + symbName + " (" + symbols.size() + " symbols)");

        for (Map.Entry<String, List<Integer>> e : pendingRefs.entrySet()) {
            if (!e.getValue().isEmpty()) {
                warning("Unresolved forward reference \"" + e.getKey() + "\"");
            }
        }
        if (warnings > 0) System.out.println(warnings + " warning(s).");
    }

    // =========================== I/O helpers ===========================

    private String getLine(BufferedReader in) throws IOException {
        if (pushback != null) {
            String s = pushback;
            pushback = null;
            lineCount++;
            return s;
        }
        while (true) {
            String line = in.readLine();
            if (line == null) return null;
            lineCount++;
            if (line.length() > 0 && line.charAt(0) == ';') continue; // comment
            return line;
        }
    }

    private void unGetLine(String s) {
        pushback = s;
        lineCount--;
    }

    private void skipBlankLines(BufferedReader in) throws IOException {
        String line = getLine(in);
        while (line != null && line.isEmpty()) line = getLine(in);
        if (line != null) unGetLine(line);
    }

    // =========================== Topic header ===========================

    private static class TopicDef {
        final String symbol;
        final int value;
        TopicDef(String symbol, int value) { this.symbol = symbol; this.value = value; }
    }

    /** Parses a {@code .topic sym[=n][, sym[=n]]...} header into its defs. */
    private List<TopicDef> topicHeader(String line) {
        int[] i = { 0 };
        String w = getWord(line, i);
        if (!".".equals(w)) return null;
        w = getWord(line, i);
        if (!"TOPIC".equalsIgnoreCase(w)) {
            error("TOPIC expected");
            return null;
        }
        return topicDefinitionList(line, i);
    }

    private List<TopicDef> topicDefinitionList(String line, int[] i) {
        List<TopicDef> defs = new ArrayList<>();
        while (true) {
            TopicDef d = topicDefinition(line, i);
            if (d == null) return null;
            defs.add(d);
            int[] j = { i[0] };
            String w = getWord(line, j);
            if (!",".equals(w)) break;
            i[0] = j[0];
        }
        return defs;
    }

    private TopicDef topicDefinition(String line, int[] i) {
        String topic = getWord(line, i);
        if (topic.isEmpty()) {
            error("Expected topic definition");
            return null;
        }
        int[] j = { i[0] };
        String w = getWord(line, j);
        if ("=".equals(w)) {
            i[0] = j[0];
            w = getWord(line, i);
            if (!isNumeric(w)) {
                error("Expected numeric");
                return null;
            }
            helpCounter = Integer.parseInt(w);
        } else {
            helpCounter++;
        }
        if (helpCounter > MAX_HELP_TOPIC_ID) {
            error("Topic id for topic '" + topic + "' exceeds limit of " + MAX_HELP_TOPIC_ID);
            return null;
        }
        return new TopicDef(topic, helpCounter);
    }

    // =========================== Tokenizer ===========================

    private static boolean isWordChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private static String getWord(String line, int[] i) {
        int len = line.length();
        while (i[0] < len && (line.charAt(i[0]) == ' ' || line.charAt(i[0]) == '\t')) i[0]++;
        int start = i[0];
        if (start >= len) return "";
        char c = line.charAt(start);
        i[0]++;
        if (isWordChar(c)) {
            while (i[0] < len && isWordChar(line.charAt(i[0]))) i[0]++;
        }
        return line.substring(start, i[0]);
    }

    private static boolean isNumeric(String s) {
        if (s.isEmpty()) return false;
        for (int k = 0; k < s.length(); k++)
            if (!Character.isDigit(s.charAt(k))) return false;
        return true;
    }

    // =========================== Paragraph parsing ===========================

    private static class CrossRef {
        final String targetSymbol;
        final int offset;
        final int length;
        CrossRef(String t, int o, int l) { targetSymbol = t; offset = o; length = l; }
    }

    private static class Paragraph {
        boolean wrap;
        byte[] text;
        List<CrossRef> refs = new ArrayList<>();
    }

    private static class Topic {
        final List<Integer> ids = new ArrayList<>();
        final List<Paragraph> paragraphs = new ArrayList<>();
    }

    private boolean isEndParagraph(String line, State state) {
        if (line == null) return true;
        if (line.isEmpty()) return true;
        char c0 = line.charAt(0);
        if (c0 == COMMAND_CHAR) return true;
        if (c0 == ' ' && state == State.WRAPPING) return true;
        if (c0 != ' ' && state == State.NOT_WRAPPING) return true;
        return false;
    }

    private void readTopic(BufferedReader in) throws IOException {
        String header = getLine(in);
        if (header == null) return;
        List<TopicDef> defs = topicHeader(header);
        if (defs == null) return;

        Topic topic = new Topic();
        int topicOffset = 0;
        int absoluteOffset = 0;

        while (true) {
            Paragraph p = readParagraph(in);
            if (p == null) break;
            // Paragraph offsets are relative to the topic start; record absolute-to-paragraph.
            for (CrossRef r : p.refs) {
                // offsets in readParagraph are relative to paragraph start; add topicOffset.
            }
            topic.paragraphs.add(p);
            absoluteOffset += p.text.length;
            topicOffset = absoluteOffset;
        }

        for (TopicDef d : defs) {
            Integer existing = symbols.get(d.symbol);
            if (existing != null) {
                error("Redefinition of " + d.symbol);
                return;
            }
            symbols.put(d.symbol, d.value);
            topic.ids.add(d.value);
            // Resolve any pending refs that pointed at this symbol.
            List<Integer> fixUps = pendingRefs.remove(d.symbol);
            if (fixUps != null) {
                // pending list carries paragraph-level pointers; resolution happens at write time.
            }
        }
        topics.add(topic);
    }

    private Paragraph readParagraph(BufferedReader in) throws IOException {
        State state = State.UNDEFINED;
        StringBuilder buf = new StringBuilder();
        List<CrossRef> refs = new ArrayList<>();

        String line = getLine(in);
        while (line != null && line.isEmpty()) {
            buf.append('\n');
            line = getLine(in);
        }

        if (isEndParagraph(line, state)) {
            if (line != null) unGetLine(line);
            return null;
        }

        while (!isEndParagraph(line, state)) {
            if (state == State.UNDEFINED)
                state = (line.charAt(0) == ' ') ? State.NOT_WRAPPING : State.WRAPPING;
            StringBuilder lineBuf = new StringBuilder(line);
            scanForCrossRefs(lineBuf, buf.length(), refs);
            buf.append(lineBuf);
            buf.append(state == State.WRAPPING ? ' ' : '\n');
            line = getLine(in);
        }
        if (line != null) unGetLine(line);

        Paragraph p = new Paragraph();
        p.wrap = state == State.WRAPPING;
        p.text = buf.toString().getBytes();
        p.refs = refs;
        return p;
    }

    /**
     * Scans a line for {text} or {text:alias} cross-references, strips the
     * curly braces, records the highlight length, and queues the target symbol
     * for later resolution. Escape "{{", "}}", "::" mirror tvhc.
     */
    private void scanForCrossRefs(StringBuilder line, int paragraphOffset, List<CrossRef> refs) {
        int i = 0;
        while (i < line.length()) {
            int beg = findSingle(line, i, '{');
            if (beg < 0) return;
            int end = findSingle(line, beg + 1, '}');
            if (end < 0) {
                error("Unterminated topic reference.");
                return;
            }
            int alias = findSingle(line, beg + 1, ':');
            String target;
            int highlightLen;
            int removeFrom, removeLen;
            if (alias < 0 || alias > end) {
                target = line.substring(beg + 1, end);
                highlightLen = end - (beg + 1);
                removeFrom = -1;
                removeLen = 0;
            } else {
                target = line.substring(alias + 1, end);
                highlightLen = alias - (beg + 1);
                removeFrom = alias;
                removeLen = end - alias;
            }
            if (removeFrom >= 0) {
                line.delete(removeFrom, removeFrom + removeLen);
                end = alias;
            }
            line.deleteCharAt(end);      // remove '}'
            line.deleteCharAt(beg);      // remove '{'
            // Mark spaces inside the highlighted region as 0xFF (Turbo Vision
            // help-viewer convention for non-breaking highlighted spaces).
            for (int k = beg; k < beg + highlightLen && k < line.length(); k++) {
                if (line.charAt(k) == ' ') line.setCharAt(k, (char) 0xFF);
            }
            refs.add(new CrossRef(target, paragraphOffset + beg, highlightLen));
            i = beg + highlightLen;
        }
    }

    /** Locates {@code ch} ignoring doubled occurrences ("{{" etc.), collapsing pairs in place. */
    private static int findSingle(StringBuilder line, int from, char ch) {
        int i = from;
        while (i < line.length()) {
            int pos = indexOf(line, ch, i);
            if (pos < 0) return -1;
            if (pos + 1 < line.length() && line.charAt(pos + 1) == ch) {
                line.deleteCharAt(pos); // collapse pair
                i = pos + 1;
                continue;
            }
            return pos;
        }
        return -1;
    }

    private static int indexOf(StringBuilder b, char ch, int from) {
        for (int i = from; i < b.length(); i++) if (b.charAt(i) == ch) return i;
        return -1;
    }

    // =========================== Output ===========================

    private void writeHelpFile(String helpName) throws IOException {
        try (OutputStream os = Files.newOutputStream(new File(helpName).toPath());
             DataOutputStream out = new DataOutputStream(os)) {
            out.write(MAGIC);
            out.writeInt(FORMAT_VERSION);
            out.writeInt(topics.size());
            for (Topic t : topics) {
                out.writeInt(t.ids.size());
                for (int id : t.ids) out.writeInt(id);
                out.writeInt(t.paragraphs.size());
                for (Paragraph p : t.paragraphs) {
                    out.writeBoolean(p.wrap);
                    out.writeInt(p.text.length);
                    out.write(p.text);
                    out.writeInt(p.refs.size());
                    for (CrossRef r : p.refs) {
                        Integer resolved = symbols.get(r.targetSymbol);
                        if (resolved == null) {
                            pendingRefs.computeIfAbsent(r.targetSymbol, k -> new ArrayList<>()).add(0);
                            resolved = 0;
                        }
                        out.writeInt(r.offset);
                        out.writeInt(r.length);
                        out.writeInt(resolved);
                        int nameLen = r.targetSymbol.length();
                        out.writeInt(nameLen);
                        out.writeBytes(r.targetSymbol);
                    }
                }
            }
        }
    }

    private void writeSymbolFile(String symbName) throws IOException {
        String className = new File(symbName).getName();
        if (className.endsWith(".java")) className = className.substring(0, className.length() - 5);
        try (PrintWriter w = new PrintWriter(symbName)) {
            w.println("// Generated by TvHelpCompiler from " + sourceName);
            w.println("public final class " + className + " {");
            w.println("    private " + className + "() {}");
            w.println();
            for (Map.Entry<String, Integer> e : symbols.entrySet()) {
                String pad = "                    ".substring(Math.min(20, e.getKey().length()));
                w.println("    public static final int hc" + e.getKey() + pad + " = " + e.getValue() + ";");
            }
            w.println("}");
        }
    }

    // =========================== Misc ===========================

    private static String replaceExt(String path, String newExt, boolean force) {
        int slash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        int dot = path.lastIndexOf('.');
        if (dot <= slash) return path + newExt;
        if (force) return path.substring(0, dot) + newExt;
        return path;
    }

    private static void checkOverwrite(String name) {
        if (new File(name).exists()) {
            System.err.println("Note: overwriting existing file " + name);
        }
    }

    private void error(String msg) {
        System.err.println("Error: " + sourceName + "(" + lineCount + "): " + msg);
        System.exit(1);
    }

    private void warning(String msg) {
        System.err.println("Warning: " + sourceName + "(" + lineCount + "): " + msg);
        warnings++;
    }
}
