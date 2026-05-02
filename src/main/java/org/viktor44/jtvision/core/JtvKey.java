/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

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
 * <p>This class retains only utility methods: {@link #getAltCode(char)},
 * {@link #getAltChar(int)}, {@link #ctrlToArrow(int)}, and
 * {@link #getMetaKeyLabel()}.
 */
public final class JtvKey {

    private JtvKey() {}

    /**
     * Returns the Alt+letter keystroke code for the given character.
     *
     * @param c letter character; case-insensitive, must be {@code A}–{@code Z}
     * @return {@code (InputEvent.ALT_DOWN_MASK << 16) | vkCode}, or {@code 0} if not a letter
     */
    public static int getAltCode(char c) {
        c = Character.toUpperCase(c);
        if (c >= 'A' && c <= 'Z') {
            return (InputEvent.ALT_DOWN_MASK << 16) | c;
        }
        return 0;
    }

    /**
     * Returns the letter or digit character corresponding to an Alt+key stroke.
     *
     * @param keyStroke an Alt+letter/digit keystroke value
     * @return the uppercase letter ({@code A}–{@code Z}) or digit ({@code 0}–{@code 9}),
     *         or {@code '\0'} if the value is not a recognised Alt+letter/digit combo
     */
    public static char getAltChar(int keyStroke) {
        if ((keyStroke >>> 16) != InputEvent.ALT_DOWN_MASK) {
            return '\0';
        }
        int vk = keyStroke & 0xFFFF;
        if (vk >= KeyEvent.VK_A && vk <= KeyEvent.VK_Z) {
            return (char) vk;
        }
        if (vk >= KeyEvent.VK_0 && vk <= KeyEvent.VK_9) {
            return (char) vk;
        }
        return '\0';
    }

//    /**
//     * Translates WordStar-style Ctrl key sequences to their cursor-movement equivalents.
//     *
//     * <p>Supports the classic WordStar diamond: Ctrl+E (up), Ctrl+X (down), Ctrl+S (left),
//     * Ctrl+D (right) and several related commands.  Returns the original code unchanged if
//     * there is no translation.  Inputs are AWT-style key strokes (from
//     * {@link KeyDownEvent#getKeyStroke()}); outputs are plain VK codes.
//     *
//     * @param keyStroke the raw keystroke to translate
//     * @return the equivalent cursor-key code, or {@code keyStroke} if no mapping exists
//     */
//    public static int ctrlToArrow(int keyStroke) {
//        if ((keyStroke >>> 16) != InputEvent.CTRL_DOWN_MASK) {
//            return keyStroke;
//        }
//        int vk = keyStroke & 0xFFFF;
//        switch (vk) {
//            case KeyEvent.VK_S: return KeyEvent.VK_LEFT;   // Ctrl+S
//            case KeyEvent.VK_D: return KeyEvent.VK_RIGHT;  // Ctrl+D
//            case KeyEvent.VK_E: return KeyEvent.VK_UP;     // Ctrl+E
//            case KeyEvent.VK_X: return KeyEvent.VK_DOWN;   // Ctrl+X
//            case KeyEvent.VK_A: return KeyEvent.VK_HOME;   // Ctrl+A (word left)
//            case KeyEvent.VK_F: return KeyEvent.VK_END;    // Ctrl+F (word right)
//            case KeyEvent.VK_G: return KeyEvent.VK_DELETE; // Ctrl+G
//            case KeyEvent.VK_V: return KeyEvent.VK_INSERT; // Ctrl+V
//            default: return keyStroke;
//        }
//    }

    /**
     * Returns the platform-appropriate label for the Alt/Option modifier key.
     * Returns {@code "Option"} on macOS and {@code "Alt"} on all other platforms.
     */
    public static String getMetaKeyLabel() {
        return SystemUtils.IS_OS_MAC ? "Option" : "Alt";
    }
}
