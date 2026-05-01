/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmColorBackgroundChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmColorForegroundChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmColorSet;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.ViewFlags.ofFirstClick;
import static org.viktor44.jtvision.core.ViewFlags.ofFramed;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvView;

/**
 * An interactive colour-swatch grid used within {@link JtvColorDialog}.
 * <p>
 * TColorSelector displays a grid of coloured blocks from which the user selects
 * either a foreground or background colour. The type is determined by the
 * {@link ColorSel} enum passed at construction:
 * <ul>
 *   <li>{@link ColorSel#csForeground} — shows all 16 foreground colours.</li>
 *   <li>{@link ColorSel#csBackground} — shows the first 8 background colours.</li>
 * </ul>
 * <p>
 * The currently selected colour is marked with an {@code x} inside its block.
 * Mouse clicks and arrow key navigation change the selection. When the colour
 * changes, this view broadcasts {@code cmColorForegroundChanged} or
 * {@code cmColorBackgroundChanged} with the new colour value.
 * <p>
 * Responds to {@code cmColorSet} broadcasts to update its selection from the
 * current palette entry.
 */
public class JtvColorSelector extends JtvView {

    /**
     * Determines whether this selector controls the foreground or background colour.
     */
    public enum ColorSel {
        /** This selector controls the background colour (8 colours). */
        csBackground,
        /** This selector controls the foreground colour (16 colours). */
        csForeground
    }

    /** The currently selected colour index. */
    private int color;

    /** Whether this selector controls the foreground or background colour. */
    private ColorSel selType;

    /** Unicode full-block character used to render each colour swatch. */
    private static final char icon = '█';

    /**
     * Creates a colour selector of the given type.
     *
     * @param bounds   the bounding rectangle (should accommodate a 4-column × 2- or 4-row grid)
     * @param aSelType {@link ColorSel#csForeground} or {@link ColorSel#csBackground}
     */
    public JtvColorSelector(JtvRect bounds, ColorSel aSelType) {
        super(bounds);
        options |= ofSelectable | ofFirstClick | ofFramed;
        eventMask |= evBroadcast;
        selType = aSelType;
        color = 0;
    }

    /**
     * Draws the colour swatch grid. Each colour swatch is rendered as three
     * full-block characters. The currently selected swatch is marked with {@code x}.
     */
    @Override
    public void draw() {
        JtvDrawBuffer b = new JtvDrawBuffer();
        b.moveChar(0, ' ', new JtvColorAttr(0x70), size.getX());
        for (int i = 0; i < size.getY(); i++) {
            for (int j = 0; j < 4; j++) {
                int c = i * 4 + j;
                if (c > (selType == ColorSel.csBackground ? 7 : 15)) {
                    continue;
                }
                b.moveChar(j * 3, icon, new JtvColorAttr(c), 3);
                if (c == color) {
                    b.putChar(j * 3 + 1, 'x');
                    if (c == 0) {
                        b.putAttribute(j * 3 + 1, new JtvColorAttr(0x70));
                    }
                }
            }
            writeLine(0, i, size.getX(), 1, b);
        }
    }

    /**
     * Broadcasts the colour change command ({@code cmColorForegroundChanged} or
     * {@code cmColorBackgroundChanged}) with the current colour value.
     */
    private void colorChanged() {
        int msg = selType == ColorSel.csForeground ? cmColorForegroundChanged : cmColorBackgroundChanged;
        message(owner, evBroadcast, msg, Integer.valueOf(color));
    }

    /**
     * Handles mouse, keyboard, and broadcast events.
     * <p>
     * <ul>
     *   <li>Mouse click/drag — selects the colour under the pointer.</li>
     *   <li>Arrow keys — move the colour selection in the grid.</li>
     *   <li>{@code cmColorSet} broadcast — updates the selection from the packed colour byte.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        int oldColor = color;
        int maxCol = selType == ColorSel.csBackground ? 7 : 15;

        switch (event.getWhat()) {
            case evMouseDown:
                do {
                    if (mouseInView(event.getMouse().getWhere())) {
                        org.viktor44.jtvision.core.JtvPoint mouse = makeLocal(event.getMouse().getWhere());
                        color = mouse.getY() * 4 + mouse.getX() / 3;
                        color = Math.max(0, Math.min(maxCol, color));
                    } else {
                        color = oldColor;
                    }
                    colorChanged();
                    drawView();
                } while (mouseEvent(event, evMouseMove));
                clearEvent(event);
                return;

            case evKeyDown:
                switch (JtvKey.ctrlToArrow(event.getKeyDown().getKeyStroke()) & 0xFFFF) {
                    case JtvKey.kbLeft:
                        color = color > 0 ? color - 1 : maxCol;
                        break;
                    case JtvKey.kbRight:
                        color = color < maxCol ? color + 1 : 0;
                        break;
                    case JtvKey.kbUp:
                        if (color > 3) {
                            color -= 4;
                        } else if (color == 0) {
                            color = maxCol;
                        } else {
                            color += maxCol - 4;
                        }
                        break;
                    case JtvKey.kbDown:
                        if (color < maxCol - 3) {
                            color += 4;
                        } else if (color == maxCol) {
                            color = 0;
                        } else {
                            color -= maxCol - 4;
                        }
                        break;
                    default:
                        return;
                }
                drawView();
                colorChanged();
                clearEvent(event);
                return;

            case evBroadcast:
                if (event.getMessage().getCommand() == cmColorSet) {
                    int v = 0;
                    if (event.getMessage().getInfoPtr() instanceof Number) {
                        v = ((Number) event.getMessage().getInfoPtr()).intValue();
                    }
                    color = selType == ColorSel.csBackground ? ((v >> 4) & 0x0F) : (v & 0x0F);
                    drawView();
                }
                return;

            default:
                return;
        }
    }
}
