/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.palette;

import static org.viktor44.jtvision.core.ViewFlags.ofCentered;
import static org.viktor44.jtvision.core.ViewFlags.wfClose;
import static org.viktor44.jtvision.core.ViewFlags.wfMove;
import static org.viktor44.jtvision.core.ViewFlags.wnNoNumber;
import static org.viktor44.jtvision.core.ViewFlags.wpBlueWindow;
import static org.viktor44.jtvision.core.ViewFlags.wpCyanWindow;
import static org.viktor44.jtvision.core.ViewFlags.wpGrayWindow;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvWindow;

public class PaletteWindow extends JtvWindow {

    public static final int TEST_WIDTH = 42;
    public static final int TEST_HEIGHT = 11;

    // Six new colors appended to each of the original blue/cyan/gray sets
    private static final int[] cpTestWindowExt = {0x88, 0x89, 0x8A, 0x8B, 0x8C, 0x8D};

    private static final JtvPalette cpBlueWindowExt = new JtvPalette(
    		concat(toIntArray(cpBlueWindow.getData()), cpTestWindowExt)
    );
    private static final JtvPalette cpCyanWindowExt = new JtvPalette(
    		concat(toIntArray(cpCyanWindow.getData()), cpTestWindowExt)
    );
    private static final JtvPalette cpGrayWindowExt = new JtvPalette(
    		concat(toIntArray(cpGrayWindow.getData()), cpTestWindowExt)
    );

    private static int[] toIntArray(JtvColorAttr[] attrs) {
        int[] result = new int[attrs.length];
        for (int i = 0; i < attrs.length; i++) result[i] = attrs[i].getValue();
        return result;
    }

    private static int[] concat(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public PaletteWindow() {
        super(new JtvRect(0, 0, TEST_WIDTH, TEST_HEIGHT), null, wnNoNumber);
        JtvRect r = getExtent();
        r.grow(-2, -2);
        insert(new PaletteView(r));
        options |= ofCentered;
        flags = wfMove | wfClose;
    }

    @Override
    public JtvPalette getPalette() {
        switch (paletteIndex) {
            case wpBlueWindow: 
            	return cpBlueWindowExt;
            case wpCyanWindow: 
            	return cpCyanWindowExt;
            case wpGrayWindow: 
            	return cpGrayWindowExt;
        }
        return cpBlueWindowExt;
    }

    @Override
    public JtvPoint getMinimumSize() {
        return new JtvPoint(TEST_WIDTH, TEST_HEIGHT);
    }

    @Override
    public JtvPoint getMaximumSize() {
        return new JtvPoint(TEST_WIDTH, TEST_HEIGHT);
    }
}
