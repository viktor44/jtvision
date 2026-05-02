/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import lombok.Getter;

public class JtvKeyStroke {

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

    public JtvKeyStroke(int keyCode, int modifiers) {
        this.keyCode = keyCode;
        this.modifiers = modifiers;
    }
    
    public static JtvKeyStroke of(int keyCode, int modifiers) {
    	return new JtvKeyStroke(keyCode, modifiers);
    }

    public static JtvKeyStroke of(int keyCode) {
    	return new JtvKeyStroke(keyCode, 0);
    }

	public int getKeyStroke() {
		return (modifiers << 16) | keyCode;
	}

}
