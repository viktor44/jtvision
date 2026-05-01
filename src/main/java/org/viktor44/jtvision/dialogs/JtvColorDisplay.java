/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmColorBackgroundChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmColorForegroundChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmColorSet;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvView;

/**
 * A preview view that renders sample text in the currently selected colours.
 * <p>
 * TColorDisplay is used inside {@link JtvColorDialog} to give the user an immediate
 * preview of how the selected foreground and background colours look. It displays
 * its {@link #text} string (repeated to fill the view) rendered with the colour
 * attribute stored in the active entry of {@link #colorArray}.
 * <p>
 * The colour is updated by:
 * <ul>
 *   <li>Calling {@link #setColor(JtvColorAttr[], int)} — points to a palette entry and
 *       broadcasts {@code cmColorSet}.</li>
 *   <li>{@code cmColorForegroundChanged} broadcasts — update the foreground nibble.</li>
 *   <li>{@code cmColorBackgroundChanged} broadcasts — update the background nibble.</li>
 * </ul>
 */
public class JtvColorDisplay extends JtvView {

    /** Palette array that contains the currently edited colour entry. */
    private JtvColorAttr[] colorArray;

    /** Index within {@link #colorArray} of the currently edited colour entry. */
    private int colorIndex = -1;

    /** The sample text repeated across the view to demonstrate the colour combination. */
    private String text;

    /**
     * Creates a colour display view with the given bounds and sample text.
     *
     * @param bounds the bounding rectangle
     * @param aText  the sample text to render (e.g., {@code "Text"})
     */
    public JtvColorDisplay(JtvRect bounds, String aText) {
        super(bounds);
        text = aText != null ? aText : "";
        eventMask |= evBroadcast;
    }

    /**
     * Draws the sample text repeatedly across the view using the current colour attribute.
     * Falls back to {@code errorAttr} when no colour has been set or the colour is 0.
     */
    @Override
    public void draw() {
        JtvColorAttr c = hasColorRef() ? colorArray[colorIndex] : new JtvColorAttr(errorAttr);
        if (c.getValue() == 0) {
            c = new JtvColorAttr(errorAttr);
        }
        JtvDrawBuffer b = new JtvDrawBuffer();
        int len = Math.max(1, text.length());
        for (int i = 0; i <= size.getX() / len; i++) {
            b.moveStr(i * len, text, c);
        }
        writeLine(0, 0, size.getX(), size.getY(), b);
    }

    /**
     * Handles {@code cmColorForegroundChanged} and {@code cmColorBackgroundChanged}
     * broadcasts to update the respective nibble of the stored colour attribute.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() != evBroadcast || !hasColorRef()) {
            return;
        }
        switch (event.getMessage().getCommand()) {
            case cmColorBackgroundChanged:
                if (event.getMessage().getInfoPtr() instanceof Number) {
                    int bg = ((Number) event.getMessage().getInfoPtr()).intValue() & 0x0F;
                    int v = colorArray[colorIndex].getValue();
                    colorArray[colorIndex] = new JtvColorAttr((v & 0x0F) | ((bg << 4) & 0xF0));
                    drawView();
                }
                break;
            case cmColorForegroundChanged:
                if (event.getMessage().getInfoPtr() instanceof Number) {
                    int fg = ((Number) event.getMessage().getInfoPtr()).intValue() & 0x0F;
                    int v = colorArray[colorIndex].getValue();
                    colorArray[colorIndex] = new JtvColorAttr((v & 0xF0) | fg);
                    drawView();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Sets a new colour reference and broadcasts {@code cmColorSet} to update the
     * colour selectors. Redraws the view.
     *
     * @param colors palette data array that owns the colour entry
     * @param index  index in {@code colors} of the active colour attribute byte
     */
    public void setColor(JtvColorAttr[] colors, int index) {
        colorArray = colors;
        colorIndex = index;
        int v = hasColorRef() ? colorArray[colorIndex].getValue() : 0;
        message(owner, evBroadcast, cmColorSet, Integer.valueOf(v));
        drawView();
    }

    /**
     * Overload for callers that pass a one-element mutable array.
     *
     * @param aColor one-element colour reference array
     */
    public void setColor(JtvColorAttr[] aColor) {
        setColor(aColor, (aColor != null && aColor.length > 0) ? 0 : -1);
    }

    private boolean hasColorRef() {
        return colorArray != null && colorIndex >= 0 && colorIndex < colorArray.length;
    }
}
