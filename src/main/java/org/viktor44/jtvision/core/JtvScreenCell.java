/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

/**
 * A single terminal screen cell holding a display character and a color attribute.
 *
 * <p>The color attribute follows the classic CGA/BIOS format:
 * the high nibble encodes the background color and the low nibble the foreground color.
 * Color constants are defined in {@link JtvColorAttr}.
 * The default attribute {@code 0x07} is light-gray text on a black background.
 *
 * <p>{@code TScreenCell} arrays form the backing store used by {@link JtvDrawBuffer}.
 */
public class JtvScreenCell {

    /** The character to display in this cell. */
    private final char ch;

	/** The color attribute: high nibble = background, low nibble = foreground. */
    private final JtvColorAttr attr;

    /** Constructs a cell with a space character and the default attribute (light-gray on black). */
    public JtvScreenCell() {
        this.ch = ' ';
        this.attr = new JtvColorAttr(0x07);
    }

    /**
     * Constructs a cell with the given character and color attribute.
     *
     * @param ch   display character
     * @param attr color attribute
     */
    public JtvScreenCell(char ch, JtvColorAttr attr) {
        this.ch = ch;
        this.attr = attr;
    }

    /** The character to display in this cell. */
    public char getCh() {
		return ch;
	}

	/** The color attribute: high nibble = background, low nibble = foreground. */
	public JtvColorAttr getAttr() {
		return attr;
	}
}
