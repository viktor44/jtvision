/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmReceivedFocus;
import static org.viktor44.jtvision.core.CommandCodes.cmReleasedFocus;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.ViewFlags.ofPostProcess;
import static org.viktor44.jtvision.core.ViewFlags.ofPreProcess;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import java.awt.event.InputEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.util.StringUtils;
import org.viktor44.jtvision.views.JtvView;

/**
 * A text label that acts as a proxy for another control.
 * <p>
 * JtvLabel serves two purposes: it displays an identifying text string next to
 * a linked control, and it allows the user to focus the linked control either
 * by clicking the label or by pressing its Alt+letter hot-key.
 * <p>
 * The label highlights (changes colour) when its linked control has focus,
 * by listening to {@code cmReceivedFocus} and {@code cmReleasedFocus} broadcasts.
 * <p>
 * Use {@link JtvStaticText} or {@link JtvParamText} instead of TLabel when you only
 * need to display text without linking it to another control.
 * 
 * <h3>Colour palette</h3>
 * 
 * The four-entry palette {@link #cpLabel} maps to:
 * <ol>
 *   <li>Normal label text.</li>
 *   <li>Highlighted label text (linked control has focus).</li>
 *   <li>Shortcut key (normal state).</li>
 *   <li>Shortcut key (highlighted state).</li>
 * </ol>
 */
public class JtvLabel extends JtvView {

    /** The label text (may contain a tilde-delimited hot-key character). */
    private String text;

    /** The view that receives focus when the label is clicked or its hot-key is pressed. */
    private JtvView link;

    /**
     * Whether the label is currently "lit" (linked control has focus).
     * {@code true} causes the label to render with the highlighted colour.
     */
    private boolean light;

    /**
     * Four-entry colour palette:
     * <ol>
     *   <li>{@code 7} — normal label text.</li>
     *   <li>{@code 8} — highlighted label text (linked control has focus).</li>
     *   <li>{@code 9} — shortcut key (normal state).</li>
     *   <li>{@code 9} — shortcut key (highlighted state).</li>
     * </ol>
     */
    private static final JtvPalette cpLabel = new JtvPalette(new int[] {7, 8, 9, 9});

    /**
     * Creates a label with the given bounds, text, and linked control.
     *
     * @param bounds the bounding rectangle
     * @param aText  the label text; enclose a character in tildes to define a hot-key
     * @param aLink  the view to focus when the label is clicked or hot-key pressed
     */
    public JtvLabel(JtvRect bounds, String aText, JtvView aLink) {
        super(bounds);
        text = aText;
        link = aLink;
        light = false;
        options |= ofPreProcess | ofPostProcess;
        eventMask |= evBroadcast;
    }

    /**
     * Draws the label text in either the normal or highlighted colour
     * depending on whether the linked control currently has focus.
     */
    @Override
    public void draw() {
        JtvColorAttr color, hotColor;
        JtvDrawBuffer b = new JtvDrawBuffer();

        if (light) {
            color = getColor(2);
            hotColor = getColor(4);
        }
        else {
            color = getColor(1);
            hotColor = getColor(3);
        }

        b.moveChar(0, ' ', color, size.getX());
        if (text != null) {
            b.moveCStr(1, text, color, hotColor);
        }
        writeLine(0, 0, size.getX(), 1, b);
    }

    /**
     * Returns the default color palette {@code cpLabel}.
     *
     * @return the label's color palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpLabel;
    }

    /**
     * Handles events to proxy focus to the linked control.
     * <ul>
     *   <li>Mouse click — focuses the linked control.</li>
     *   <li>Alt+hot-key — focuses the linked control.</li>
     *   <li>{@code cmReceivedFocus} / {@code cmReleasedFocus} broadcast for the linked
     *       view — toggles the {@code light} flag and redraws.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evMouseDown) {
            if (link != null) {
                link.focus();
            }
            clearEvent(event);
        }
        else if (event.getWhat() == evKeyDown) {
            char c = StringUtils.hotKey(text);
            if (c != 0 && event.getKeyDown().getKeyCode() != 0 &&
                event.getKeyDown().getModifiers() == InputEvent.ALT_DOWN_MASK
                    && Character.toUpperCase(c) == event.getKeyDown().getKeyCode()) {
                if (link != null) {
                    link.focus();
                }
                clearEvent(event);
            }
        }
        else if (event.getWhat() == evBroadcast) {
            if (event.getMessage().getCommand() == cmReceivedFocus || event.getMessage().getCommand() == cmReleasedFocus) {
                if (link != null && event.getMessage().getInfoPtr() == link) {
                    light = event.getMessage().getCommand() == cmReceivedFocus;
                    drawView();
                }
            }
        }
    }
}
