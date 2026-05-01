/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.core.ViewFlags;
import org.viktor44.jtvision.views.JtvView;

/**
 * A non-interactive view that displays a fixed text string.
 * <p>
 * JtvStaticText encapsulates a text string in a view, displaying it within its
 * bounding rectangle. It is not selectable, so the user never actively interacts
 * with it. Use this view for labels, instructions, or other fixed text elements
 * in dialog boxes.
 * <p>
 * The text may span multiple lines by embedding newline characters ({@code \n}).
 * A line that begins with ASCII character 3 (Ctrl+C, {@code \x03}) is centred
 * horizontally within the view.
 * <p>
 * The text is retrieved via the virtual method {@link #getText()}, which subclasses
 * such as {@link JtvParamText} can override to supply dynamic content.
 * 
 * <h3>Colour palette</h3>
 * 
 * The single-entry palette {@link #cpStaticText} maps to:
 * <ol>
 *   <li>Static text colour.</li>
 * </ol>
 */
public class JtvStaticText extends JtvView {

    /** The text string to display. Lines ending with {@code \n} are wrapped. */
    protected String text;

    /**
     * Single-entry colour palette:
     * <ol>
     *   <li>{@code 6} — static text colour.</li>
     * </ol>
     */
    private static final JtvPalette cpStaticText = new JtvPalette(new int[] {6});

    /**
     * Creates a static text view with the given bounds and text.
     * <p>
     * The view is fixed (does not grow) and non-selectable.
     *
     * @param bounds the bounding rectangle; must be large enough to display the entire text
     * @param aText  the text to display; use {@code \n} for line breaks and
     *               ASCII 3 at the start of a line to centre it
     */
    public JtvStaticText(JtvRect bounds, String aText) {
        super(bounds);
        text = aText;
        growMode |= ViewFlags.gfFixed;
    }

    /**
     * Draws the text content returned by {@link #getText()} line by line.
     * Lines are word-wrapped to the view width. Lines beginning with ASCII 3 are centred.
     */
    @Override
    public void draw() {
        JtvColorAttr color = getColor(1);
        String s = getText();
        int l = s.length();
        int p = 0;
        boolean center = false;
        JtvDrawBuffer b = new JtvDrawBuffer();

        for (int y = 0; y < size.getY(); y++) {
            b.moveChar(0, ' ', color, size.getX());
            if (p < l) {
                if (s.charAt(p) == 3) {
                    center = true;
                    p++;
                }
                int i = p;
                // Find end of line that fits in width
                int lineEnd = p;
                int lastSpace = p;
                int width = 0;
                while (lineEnd < l && s.charAt(lineEnd) != '\n' && width < size.getX()) {
                    if (s.charAt(lineEnd) == ' ') {
                        lastSpace = lineEnd;
                    }
                    lineEnd++;
                    width++;
                }
                if (width >= size.getX() && lastSpace > i) {
                    lineEnd = lastSpace + 1;
                }
                else if (lineEnd < l && s.charAt(lineEnd) == '\n') {
                    lineEnd++;
                }

                String line = s.substring(i, Math.min(lineEnd, l)).replace("\n", "");
                int lineWidth = line.length();
                int j = center ? (size.getX() - lineWidth) / 2 : 0;
                if (j < 0) {
                	j = 0;
                }
                b.moveStr(j, line, color);
                p = lineEnd;
                center = false;
            }
            writeLine(0, y, size.getX(), 1, b);
        }
    }

    /**
     * Returns the default color palette {@code cpStaticText}.
     *
     * @return the static text's color palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpStaticText;
    }

    /**
     * Returns the text string to be displayed.
     * Subclasses can override this to supply dynamic or formatted text.
     *
     * @return the display text; never {@code null}
     */
    public String getText() {
        return text != null ? text : "";
    }
}
