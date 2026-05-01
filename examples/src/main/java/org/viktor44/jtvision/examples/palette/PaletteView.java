/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.palette;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvView;

public class PaletteView extends JtvView {

    private static final JtvPalette cpTestView = new JtvPalette(
    		new int[] {0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E}
    );

    public PaletteView(JtvRect r) {
        super(r);
    }

    @Override
    public void draw() {
        JtvDrawBuffer buf = new JtvDrawBuffer();
        for (int i = 1; i <= 6; i++) {
            JtvColorAttr textAttr = getColor(i);
            String text = String.format(" This line uses index %02X, color is %02X ", i, textAttr.getValue());
            buf.moveStr(0, text, textAttr);
            writeLine(0, i - 1, getSize().getX(), 1, buf);
        }
        buf.moveStr(0, "   This line bypasses the palettes!    ", new JtvColorAttr(5));
        writeLine(0, 6, getSize().getX(), 7, buf);
    }

    @Override
    public JtvPalette getPalette() {
        return cpTestView;
    }
}
