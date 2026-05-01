package org.viktor44.jtvision.views;

import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;

import java.util.ArrayList;
import java.util.List;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;

/**
 * A scrollable terminal view that accumulates text output in a line buffer.
 * <p>
 * TTerminal extends {@link JtvTextDevice} with a concrete ring-buffer
 * implementation. Incoming text is split on newline characters into a list
 * of completed lines ({@link #lines}) plus a {@link #currentLine} that is
 * still being assembled. When the total character count exceeds
 * {@link #bufSize} the oldest lines are discarded.
 * 
 * <h3>Rendering</h3>
 * 
 * {@link #draw()} renders the visible window of lines starting from
 * {@code delta.y}. A blinking text cursor is displayed at the end of
 * {@link #currentLine}; if the cursor position falls outside the visible
 * area it is placed off-screen at {@code (-1, -1)}.
 * 
 * <h3>Grow mode</h3>
 * 
 * The grow mode is set to {@code gfGrowHiX | gfGrowHiY} so the terminal
 * expands to fill its owner when the owner is resized.
 * 
 * <h3>Colour palette</h3>
 * 
 * The two-entry palette {@link #cpTerminal} maps to:
 * <ol>
 *   <li>{@code 6} — normal text colour.</li>
 *   <li>{@code 7} — highlighted text colour.</li>
 * </ol>
 */
public class JtvTerminal extends JtvTextDevice {

    /**
     * Maximum total character count across all stored lines. When exceeded,
     * the oldest lines are removed by {@link #trimToBufferSize()}.
     */
    private int bufSize;

    /**
     * Completed lines in the terminal's line buffer, in chronological order.
     * The oldest entry is index 0; it is the first to be discarded when the
     * buffer overflows.
     */
    private final List<String> lines = new ArrayList<String>();

    /**
     * The line currently being assembled. It becomes a completed entry in
     * {@link #lines} when a newline character is received.
     */
    private String currentLine = "";

    /**
     * Two-entry colour palette:
     * <ol>
     *   <li>{@code 6} — normal text.</li>
     *   <li>{@code 7} — highlighted text.</li>
     * </ol>
     */
    private static final JtvPalette cpTerminal = new JtvPalette(new int[] {6, 7});

    /**
     * Constructs a terminal with the given bounds, scroll bars, and buffer
     * size. Sets grow mode to {@code gfGrowHiX | gfGrowHiY}, initialises the
     * scroll limit to {@code (0, 1)}, positions the cursor at the origin,
     * and makes the cursor visible.
     *
     * @param bounds        the bounding rectangle
     * @param aHScrollBar   the horizontal scroll bar, or {@code null}
     * @param aVScrollBar   the vertical scroll bar, or {@code null}
     * @param aBufSize      maximum total character count; clamped to
     *                      {@code [1, 32000]}
     */
    public JtvTerminal(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, int aBufSize) {
        super(bounds, aHScrollBar, aVScrollBar);
        growMode = gfGrowHiX | gfGrowHiY;
        bufSize = Math.max(1, Math.min(32000, aBufSize));
        setLimit(0, 1);
        setCursor(0, 0);
        showCursor();
    }

    /**
     * Appends the string {@code s} to the line buffer, splitting on
     * {@code '\n'}. Carriage-return characters ({@code '\r'}) are silently
     * dropped. After appending, trims the buffer to {@link #bufSize},
     * recalculates the scroll limit, and scrolls to the bottom before
     * redrawing.
     *
     * @param s the string to append; {@code null} is a no-op
     * @return the number of characters in {@code s}, or {@code 0} for
     *         {@code null}
     */
    @Override
    public int doSputn(String s) {
        if (s == null) {
            return 0;
        }
        int count = s.length();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\r') {
                continue;
            }
            if (ch == '\n') {
                lines.add(currentLine);
                currentLine = "";
            } else {
                currentLine += ch;
            }
        }
        trimToBufferSize();

        int width = longestLineWidth();
        int height = Math.max(1, lines.size() + 1);

        drawLock++;
        setLimit(width, height);
        scrollTo(0, height + 1);
        drawLock--;
        drawView();
        return count;
    }

    /**
     * Removes the oldest completed lines until the total character count
     * (including newline separators and the current line) is within
     * {@link #bufSize}. If even after removing all completed lines the current
     * line is still too long, its leading characters are truncated.
     */
    private void trimToBufferSize() {
        int used = currentLine.length();
        for (int i = lines.size() - 1; i >= 0; i--) {
            used += lines.get(i).length() + 1;
        }
        while (used > bufSize && !lines.isEmpty()) {
            String first = lines.remove(0);
            used -= (first.length() + 1);
        }
        if (used > bufSize && currentLine.length() > 0) {
            int cut = used - bufSize;
            if (cut >= currentLine.length()) {
                currentLine = "";
            } else {
                currentLine = currentLine.substring(cut);
            }
        }
    }

    /**
     * Returns the length of the longest line across all completed lines and
     * the current line. Used to set the horizontal scroll limit.
     *
     * @return the maximum line length in characters
     */
    private int longestLineWidth() {
        int w = currentLine.length();
        for (String line : lines) {
            w = Math.max(w, line.length());
        }
        return w;
    }

    /**
     * Renders the visible portion of the line buffer. Lines are drawn
     * starting from {@code delta.y}; each row is padded to the full view
     * width with spaces. The horizontal scroll offset ({@code delta.x})
     * is applied via {@link JtvDrawBuffer#moveStr}. The text cursor is
     * positioned at the end of the current line; if that position is outside
     * the viewport it is placed off-screen.
     */
    @Override
    public void draw() {
        JtvDrawBuffer b = new JtvDrawBuffer();
        JtvColorAttr color = getColor(1);

        List<String> all = new ArrayList<String>(lines);
        all.add(currentLine);

        int start = Math.max(0, delta.getY());
        for (int y = 0; y < size.getY(); y++) {
            b.moveChar(0, ' ', color, size.getX());
            int lineIndex = start + y;
            if (lineIndex < all.size()) {
                String line = all.get(lineIndex);
                int indent = Math.max(0, delta.getX());
                b.moveStr(0, line, color, size.getX(), indent);
            }
            writeBuf(0, y, size.getX(), 1, b);
        }

        int cursorLine = all.size() - 1 - start;
        int cursorCol = Math.max(0, currentLine.length() - Math.max(0, delta.getX()));
        if (cursorLine >= 0 && cursorLine < size.getY() && cursorCol < size.getX()) {
            setCursor(cursorCol, cursorLine);
        } else {
            setCursor(-1, -1);
        }
    }

    /**
     * Returns {@code true} if both the completed line list and the current
     * line are empty, indicating the terminal has no pending output.
     *
     * @return {@code true} if the terminal buffer is empty
     */
    public boolean queEmpty() {
        return lines.isEmpty() && currentLine.isEmpty();
    }

    /**
     * Returns the terminal's colour palette {@link #cpTerminal}.
     *
     * @return the two-entry palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpTerminal;
    }
}
