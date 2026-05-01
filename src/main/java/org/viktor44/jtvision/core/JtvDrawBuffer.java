package org.viktor44.jtvision.core;

import lombok.Getter;

/**
 * A line-buffer of {@link JtvScreenCell} objects used by view {@code draw()} methods.
 * <p>
 * Data and color attributes are assembled line-by-line in a {@code TDrawBuffer},
 * then written to the screen via {@code TView.writeLine()} or {@code TView.writeBuf()}.
 * Typical draw pattern:
 * <pre>
 * {@code
 * TDrawBuffer b = new TDrawBuffer();
 * b.moveChar(0, ' ', getColor(1), size.getX()); // fill with spaces
 * b.moveStr(0, label, getColor(2));              // overlay text
 * writeLine(0, row, size.getX(), 1, b);
 * }</pre>
 * <p>
 * The Pascal equivalent was a fixed {@code array[0..MaxViewWidth-1] of Word} where
 * each word packed a character (low byte) and attribute (high byte).  This Java version
 * uses an array of {@link JtvScreenCell} objects instead.
 */
public class JtvDrawBuffer {

    /** The underlying array of screen cells. */
	@Getter
    private JtvScreenCell[] data;

	/** Constructs a buffer with the default capacity of 256 cells. */
    public JtvDrawBuffer() {
        this(256);
    }

    /**
     * Constructs a buffer with the given capacity.
     *
     * @param size number of cells to allocate
     */
    public JtvDrawBuffer(int size) {
        data = new JtvScreenCell[size];
        for (int i = 0; i < size; i++) {
            data[i] = new JtvScreenCell();
        }
    }

    /**
     * Wraps an existing {@link JtvScreenCell} array.  The array is adopted by reference — no
     * copy is made — so mutations to the buffer are visible through the original array and
     * vice versa.  Used by the root group to share its buffer with the platform screen buffer.
     *
     * @param data backing cell array
     */
    public JtvDrawBuffer(JtvScreenCell[] data) {
        this.data = data;
    }

    /**
     * Fills {@code count} cells starting at {@code indent} with copies of character {@code c}
     * using the given color attribute.
     *
     * <p>If {@code c} is {@code 0} the character of each cell is left unchanged.
     * If {@code attr} is {@code null} the attribute of each cell is left unchanged.
     *
     * @param indent starting cell index
     * @param c      character to write, or {@code 0} to leave characters unchanged
     * @param attr   color attribute to write, or {@code null} to leave attributes unchanged
     * @param count  number of cells to fill
     */
    public void moveChar(int indent, char c, JtvColorAttr attr, int count) {
        for (int i = 0; i < count && indent + i < data.length; i++) {
            if (c != 0 || attr != null) {
                char newCh = (c != 0) ? c : data[indent + i].getCh();
                JtvColorAttr newAttr = (attr != null) ? attr : data[indent + i].getAttr();
                data[indent + i] = new JtvScreenCell(newCh, newAttr);
            }
        }
    }

    /**
     * Copies the characters of {@code str} into the buffer at {@code indent} using
     * the given color attribute.  If {@code attr} is {@code null} the existing cell
     * attributes are left unchanged.
     *
     * @param indent starting cell index
     * @param str    text to copy
     * @param attr   color attribute, or {@code null} to leave attributes unchanged
     * @return number of display columns written
     */
    public int moveStr(int indent, String str, JtvColorAttr attr) {
        return moveStr(indent, str, attr, Integer.MAX_VALUE, 0);
    }

    /**
     * Copies at most {@code maxWidth} columns of {@code str} into the buffer at {@code indent},
     * optionally skipping the first {@code strIndent} display columns of the string.
     *
     * @param indent    starting cell index in the buffer
     * @param str       text to copy
     * @param attr      color attribute, or {@code null} to leave attributes unchanged
     * @param maxWidth  maximum number of columns to write
     * @param strIndent number of leading display columns in {@code str} to skip
     * @return number of display columns written
     */
    public int moveStr(int indent, String str, JtvColorAttr attr, int maxWidth, int strIndent) {
        if (str == null) {
        	return 0;
        }
        int written = 0;
        int skipped = 0;
        for (int i = 0; i < str.length() && written < maxWidth && indent + written < data.length; i++) {
            char c = str.charAt(i);
            if (skipped < strIndent) {
                skipped++;
                continue;
            }
            JtvColorAttr newAttr = (attr != null) ? attr : data[indent + written].getAttr();
            data[indent + written] = new JtvScreenCell(c, newAttr);
            written++;
        }
        return written;
    }

    /**
     * Copies a hotkey-marked string (a <em>CStr</em>) into the buffer at {@code indent}.
     *
     * <p>Tilde characters ({@code ~}) in the string toggle between {@code normalAttr} and
     * {@code highlightAttr}.  The tilde characters themselves are not written.
     *
     * @param indent        starting cell index
     * @param str           CStr text with {@code ~} delimiters around hotkey characters
     * @param normalAttr    color attribute for normal characters
     * @param highlightAttr color attribute for hotkey characters
     * @return number of display columns written
     */
    public int moveCStr(int indent, String str, JtvColorAttr normalAttr, JtvColorAttr highlightAttr) {
        return moveCStr(indent, str, normalAttr, highlightAttr, Integer.MAX_VALUE, 0);
    }

    /**
     * Copies at most {@code maxWidth} columns of a hotkey-marked string into the buffer,
     * optionally skipping the first {@code strIndent} display columns.
     *
     * @param indent        starting cell index in the buffer
     * @param str           CStr text with {@code ~} delimiters
     * @param normalAttr    color attribute for normal characters
     * @param highlightAttr color attribute for hotkey characters
     * @param maxWidth      maximum number of columns to write
     * @param strIndent     number of leading display columns in {@code str} to skip
     * @return number of display columns written
     */
    public int moveCStr(int indent, String str, JtvColorAttr normalAttr, JtvColorAttr highlightAttr, int maxWidth, int strIndent) {
        if (str == null) {
        	return 0;
        }
        int written = 0;
        int skipped = 0;
        boolean highlight = false;

        for (int i = 0; i < str.length() && written < maxWidth && indent + written < data.length; i++) {
            char c = str.charAt(i);
            if (c == '~') {
                highlight = !highlight;
                continue;
            }
            if (skipped < strIndent) {
                skipped++;
                continue;
            }
            data[indent + written] = new JtvScreenCell(c, highlight ? highlightAttr : normalAttr);
            written++;
        }
        return written;
    }

    /**
     * Copies {@code count} cells from the {@code source} array into this buffer at {@code indent}.
     *
     * <p>If {@code attr} is non-null it overrides the source attribute for every copied cell;
     * otherwise each source cell's original attribute is preserved.
     *
     * @param indent starting cell index in this buffer
     * @param source array of source cells
     * @param attr   attribute override, or {@code null} to use source attributes
     * @param count  number of cells to copy
     */
    public void moveBuf(int indent, JtvScreenCell[] source, JtvColorAttr attr, int count) {
        for (int i = 0; i < count && indent + i < data.length && i < source.length; i++) {
            data[indent + i] = attr != null
                    ? new JtvScreenCell(source[i].getCh(), attr)
                    : source[i];
        }
    }

    /**
     * Sets the color attribute of the single cell at {@code indent}, leaving its character unchanged.
     *
     * @param indent cell index
     * @param attr   new color attribute
     */
    public void putAttribute(int indent, JtvColorAttr attr) {
        if (indent >= 0 && indent < data.length) {
            data[indent] = new JtvScreenCell(data[indent].getCh(), attr);
        }
    }

    /**
     * Sets the character of the single cell at {@code indent}, leaving its attribute unchanged.
     *
     * @param indent cell index
     * @param c      new display character
     */
    public void putChar(int indent, char c) {
        if (indent >= 0 && indent < data.length) {
            data[indent] = new JtvScreenCell(c, data[indent].getAttr());
        }
    }
}
