/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Payload of a keyboard event ({@code evKeyDown}).
 *
 * <p>The {@link #keyCode}, {@link #modifiers} and {@link #keyChar} fields carry
 * the same meaning and values as the corresponding fields of
 * {@link java.awt.event.KeyEvent}.
 *
 * @see JtvEvent
 * @see EventCodes#evKeyDown
 */
@NoArgsConstructor
@AllArgsConstructor
public class KeyDownEvent {

	/**
     * AWT virtual-key code of the pressed key
     * (e.g. {@link KeyEvent#VK_F1}, {@link KeyEvent#VK_ESCAPE}).
     */
	@Getter
    private int keyCode;

    /**
     * Modifier-key state at the time of the keystroke.
     *
     * <p>A bitmask of {@link InputEvent} extended modifier flags such as
     * {@link InputEvent#SHIFT_DOWN_MASK}, {@link InputEvent#CTRL_DOWN_MASK} and
     * {@link InputEvent#ALT_DOWN_MASK}.
     */
	@Getter
    private int modifiers;

    /**
     * Unicode character produced by the keystroke.
     *
     * <p>{@link KeyEvent#CHAR_UNDEFINED} for non-printable keys (function keys,
     * cursor keys, etc.).
     */
	@Getter
    private char keyChar = KeyEvent.CHAR_UNDEFINED;

    /**
     * Returns the combined keystroke encoding {@code (modifiers << 16) | keyCode}.
     *
     * <p>Useful for matching against menu / status-line shortcut codes that pack
     * a virtual key together with its required modifier mask in a single
     * integer (see {@link JtvKey}).
     *
     * @return packed (modifiers, keyCode) value
     */
    public int getKeyStroke() {
        return (modifiers << 16) | (keyCode & 0xFFFF);
    }
}
