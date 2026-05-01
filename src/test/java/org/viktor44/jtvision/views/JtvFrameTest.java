/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.views;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;

import org.junit.jupiter.api.Test;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvFrame;
import org.viktor44.jtvision.views.JtvWindow;

public class JtvFrameTest {

    @Test
    public void drawDoesNotThrowWhenFrameIsActive() {
        JtvWindow window = new JtvWindow(new JtvRect(0, 0, 40, 12), "Test", 1);
        JtvFrame frame = window.frame;

        frame.state |= sfActive;

        assertDoesNotThrow(frame::draw);
    }
}
