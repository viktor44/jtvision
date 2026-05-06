/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import java.awt.event.KeyEvent;

/**
 * macOS implementation of {@link EventQueue}.
 * Extends {@link UnixEventQueue} with macOS Option-key → Alt-key mapping.
 */
class MacosEventQueue extends UnixEventQueue {

    /**
     * Maps macOS Option+key Unicode code points to AWT virtual-key codes.
     * Only a small set of well-known US keyboard Option combinations are
     * mapped; all others return {@code 0}.
     *
     * @param codePoint the Unicode code point to test
     * @return the AWT {@code VK_*} code, or {@code 0}
     */
    @Override
    protected int macOptionCodeToAlt(int codePoint) {
        switch (codePoint) {
            case 0x2248: // Option+X on macOS US keyboard (≈)
            case 0x02DB: // Shift+Option+X on macOS US keyboard (˛)
                return KeyEvent.VK_X;
            default:
                return 0;
        }
    }
}
