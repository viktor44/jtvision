/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.demo;

import static org.viktor44.jtvision.core.ViewFlags.wfGrow;
import static org.viktor44.jtvision.core.ViewFlags.wfZoom;
import static org.viktor44.jtvision.core.ViewFlags.wnNoNumber;
import static org.viktor44.jtvision.core.ViewFlags.wpCyanWindow;

import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvWindow;

public class CalendarWindow extends JtvWindow {

    public CalendarWindow() {
        super(new JtvRect(1, 1, 23, 11), "Calendar", wnNoNumber);
        flags &= ~(wfZoom | wfGrow);
        growMode = 0;
        paletteIndex = wpCyanWindow;
        JtvRect r = getExtent();
        r.grow(-1, -1);
        insert(new CalendarView(r));
    }
}
