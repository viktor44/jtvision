/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

import org.viktor44.jtvision.util.SystemUtils;

/**
 * Keyboard utility methods for the JT Vision UI framework.
 *
 * <p>Use {@link java.awt.event.KeyEvent} {@code VK_*} constants for key codes
 * and {@link java.awt.event.InputEvent} {@code *_DOWN_MASK} constants for
 * modifier masks directly in event handling code. Compound keystrokes are
 * encoded as {@code (modifiers << 16) | keyCode}, matching the value returned
 * by {@link KeyDownEvent#getKeyStroke()}.
 *
 * <p>This class retains only {@link #getMetaKeyLabel()}.
 */
public final class JtvKey {

    private JtvKey() {}

    /**
     * Returns the platform-appropriate label for the Alt/Option modifier key.
     * Returns {@code "Option"} on macOS and {@code "Alt"} on all other platforms.
     */
    public static String getMetaKeyLabel() {
        return SystemUtils.IS_OS_MAC ? "Option" : "Alt";
    }
}
