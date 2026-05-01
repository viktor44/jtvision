package org.viktor44.jtvision.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

/**
 * A single help topic containing formatted text and optional cross-references.
 * <p>
 * A help topic stores its content as a list of raw text lines and a parallel
 * list of word-wrapped lines computed at the current display {@link #width}.
 * Wrapped lines are recomputed whenever the width changes via
 * {@link #setWidth(int)}.
 * <p>
 * Cross-references (hyperlinks to other topics) are stored as
 * {@link CrossRef} records. Each cross-reference identifies a rectangular
 * span within the word-wrapped view — a column, a 1-based line number, a
 * display length, and the target help-context ID — so that
 * {@link JtvHelpViewer} can highlight the spans and navigate between them.
 * <p>
 * Topics are normally created via {@link #fromText(String)} or loaded
 * through {@link JtvHelpFile}. The copy constructor creates an independent
 * deep copy of all mutable state.
 *
 * @see JtvHelpFile
 * @see JtvHelpViewer
 */
public class JtvHelpTopic {

    /**
     * A hyperlink within a help topic that points to another topic.
     * <p>
     * A {@code CrossRef} describes the position, size, and destination of a
     * single clickable keyword inside the word-wrapped view of its parent
     * topic. Coordinates are in the wrapped (post-wrap) coordinate space.
     */
    public static final class CrossRef {
        /**
         * The 0-based column at which the cross-reference text starts within its wrapped line.
         */
    	@Getter
        private final int x;

		/**
         * The 1-based line number of the wrapped line that contains this cross-reference.
         */
    	@Getter
        private final int y;

		/**
         * The number of display columns spanned by the cross-reference text.
         */
    	@Getter
        private final int length;

		/**
         * The help-context ID of the target topic that this cross-reference links to.
         */
    	@Getter
        private final int ref;

		/**
         * Creates a cross-reference with the given position, length, and
         * target context ID.
         *
         * @param x      0-based column of the link text
         * @param y      1-based wrapped-line number of the link text
         * @param length display width of the link text in columns
         * @param ref    help-context ID of the target topic
         */
        public CrossRef(int x, int y, int length, int ref) {
            this.x = x;
            this.y = y;
            this.length = length;
            this.ref = ref;
        }
    }

    /**
     * The original unmodified lines of the topic text, split on newlines.
     * Used as the source for re-wrapping when the display width changes.
     */
    private List<String> rawLines = new ArrayList<String>();

    /**
     * The word-wrapped lines computed from {@link #rawLines} at the current
     * {@link #width}. These are the lines rendered by {@link JtvHelpViewer}.
     */
    private List<String> wrapped = new ArrayList<String>();

    /** Cross-references (hyperlinks) embedded in this topic's text. */
    private final List<CrossRef> refs = new ArrayList<CrossRef>();

    /**
     * The current word-wrap column width.
     * Lines wider than this value are split at word boundaries.
     * Defaults to {@code 60}.
     */
    private int width = 60;

    /** Creates an empty help topic with no text and no cross-references. */
    public JtvHelpTopic() {
    }

    /**
     * Copy constructor — creates an independent deep copy of {@code other}.
     * The raw lines, wrapped lines, cross-references, and width are all
     * copied; subsequent modifications to either topic do not affect the
     * other.
     *
     * @param other the topic to copy
     */
    public JtvHelpTopic(JtvHelpTopic other) {
        rawLines = new ArrayList<String>(other.rawLines);
        wrapped = new ArrayList<String>(other.wrapped);
        width = other.width;
        refs.addAll(other.refs);
    }

    /**
     * Creates a new topic by splitting {@code text} on line boundaries and
     * computing an initial word-wrap at the default width of 60 columns.
     * {@code null} is treated as an empty string.
     *
     * @param text the plain-text content of the topic
     * @return a new {@code THelpTopic} populated from {@code text}
     */
    public static JtvHelpTopic fromText(String text) {
        JtvHelpTopic t = new JtvHelpTopic();
        if (text == null) {
            text = "";
        }
        String[] lines = text.split("\\r?\\n", -1);
        for (String l : lines) {
            t.rawLines.add(l);
        }
        t.rewrap();
        return t;
    }

    /**
     * Sets the word-wrap width and immediately recomputes the wrapped lines.
     * The minimum accepted width is 1.
     *
     * @param w the new column width; clamped to a minimum of {@code 1}
     */
    public void setWidth(int w) {
        width = Math.max(1, w);
        rewrap();
    }

    /**
     * Returns the length of the longest wrapped line, in characters.
     * Used by {@link JtvHelpViewer} to set the horizontal scroll limit.
     *
     * @return the maximum wrapped-line length, or {@code 0} if there are
     *         no lines
     */
    public int longestLineWidth() {
        int m = 0;
        for (String s : wrapped) {
            m = Math.max(m, s.length());
        }
        return m;
    }

    /**
     * Returns the total number of word-wrapped lines in this topic.
     * Used by {@link JtvHelpViewer} to set the vertical scroll limit.
     *
     * @return the wrapped line count; always at least {@code 1}
     */
    public int numLines() {
        return wrapped.size();
    }

    /**
     * Returns the wrapped line at the given 1-based line number.
     * Returns an empty string if {@code oneBased} is out of range.
     *
     * @param oneBased 1-based line index (line 1 is the first line)
     * @return the wrapped line text, or {@code ""} if out of range
     */
    public String getLine(int oneBased) {
        int i = oneBased - 1;
        if (i < 0 || i >= wrapped.size()) {
            return "";
        }
        return wrapped.get(i);
    }

    /**
     * Returns the number of cross-references registered in this topic.
     *
     * @return the cross-reference count
     */
    public int getNumCrossRefs() {
        return refs.size();
    }

    /**
     * Returns the cross-reference at the given zero-based index, or
     * {@code null} if {@code idx} is out of range.
     *
     * @param idx zero-based cross-reference index
     * @return the {@link CrossRef}, or {@code null} if out of range
     */
    public CrossRef getCrossRef(int idx) {
        if (idx < 0 || idx >= refs.size()) {
            return null;
        }
        return refs.get(idx);
    }

    /**
     * Appends a new cross-reference to this topic.
     *
     * @param x      0-based column of the link text in the wrapped view
     * @param y      1-based line number of the link text in the wrapped view
     * @param length display width of the link text in columns
     * @param ref    help-context ID of the target topic
     */
    public void addCrossRef(int x, int y, int length, int ref) {
        refs.add(new CrossRef(x, y, length, ref));
    }

    /**
     * Returns an unmodifiable view of all cross-references in this topic.
     *
     * @return an unmodifiable list of {@link CrossRef} objects
     */
    public List<CrossRef> crossRefs() {
        return Collections.unmodifiableList(refs);
    }

    /**
     * Recomputes {@link #wrapped} from {@link #rawLines} using the current
     * {@link #width}. Lines that fit within the width are kept as-is; longer
     * lines are split at word boundaries (the last space at or before the
     * wrap column), falling back to a hard split if no space is found.
     * The result always contains at least one (possibly empty) line.
     */
    private void rewrap() {
        wrapped.clear();
        for (String line : rawLines) {
            if (line == null) {
                line = "";
            }
            if (line.length() <= width) {
                wrapped.add(line);
                continue;
            }
            int p = 0;
            while (p < line.length()) {
                int end = Math.min(line.length(), p + width);
                int split = end;
                if (end < line.length()) {
                    int space = line.lastIndexOf(' ', end - 1);
                    if (space > p) {
                        split = space + 1;
                    }
                }
                wrapped.add(line.substring(p, split).replace("\n", ""));
                p = split;
            }
        }
        if (wrapped.isEmpty()) {
            wrapped.add("");
        }
    }
}
