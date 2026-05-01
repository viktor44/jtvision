/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

import lombok.Getter;

/**
 * An array of color-attribute indices used by the JT Vision palette system.
 *
 * <p>Each view returns a palette from its {@code getPalette()} method.  The palette maps
 * logical color indices (1-based) to physical color attributes.  These attributes are
 * looked up through the palette chain from the view up to the application, ultimately
 * producing the 8-bit CGA color byte that is stored in each {@link JtvScreenCell}.
 *
 * <p>Storage format: {@code data} holds the colour entries directly, with no length
 * prefix.  Consumers use {@link #get(int)} with a 1-based index to retrieve entries.
 */
public class JtvPalette {

    /** Internal storage array holding the colour entries. */
	@Getter
    private JtvColorAttr[] data;

    /**
     * Constructs a palette from a raw array of color attribute values.
     * The resulting palette length is {@code source.length}.
     *
     * @param source array of color attribute bytes (1-based palette entries)
     */
    public JtvPalette(int[] source) {
        data = new JtvColorAttr[source.length];
        for (int i = 0; i < source.length; i++) {
            data[i] = new JtvColorAttr(source[i]);
        }
    }

    /**
     * Constructs a palette from a string whose characters encode color attribute bytes.
     * Each character's low byte is stored as a palette entry.
     *
     * @param source palette encoded as a string of attribute characters
     */
    public JtvPalette(String source) {
        data = new JtvColorAttr[source.length()];
        for (int i = 0; i < source.length(); i++) {
            data[i] = new JtvColorAttr(source.charAt(i) & 0xFF);
        }
    }

    /**
     * Copy constructor.
     *
     * @param other the palette to copy
     */
    public JtvPalette(JtvPalette other) {
        data = other.data.clone();
    }

    /**
     * Returns the palette entry at the given 1-based index.
     *
     * @param index 1-based palette entry index ({@code 1..length()})
     * @return the value at {@code index}, or a zero-value entry if out of bounds
     */
    public JtvColorAttr get(int index) {
        if (index >= 1 && index <= data.length) {
            return data[index - 1];
        }
        return new JtvColorAttr(0);
    }

    /**
     * Sets the palette entry at the given 1-based index.
     *
     * @param index 1-based palette entry index
     * @param value new value
     */
    public void set(int index, JtvColorAttr value) {
        if (index >= 1 && index <= data.length) {
            data[index - 1] = value;
        }
    }

    /**
     * Returns the number of color entries in this palette.
     *
     * @return palette entry count
     */
    public int length() {
        return data.length;
    }
}
