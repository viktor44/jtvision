/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import org.viktor44.jtvision.core.JtvRect;

/**
 * A cluster of multi-state check-box controls.
 * <p>
 * JtvMultiCheckBoxes extends {@link JtvCluster} to support items with more than two
 * states (checked/unchecked). Each item occupies a fixed number of bits in the
 * inherited 32-bit {@link JtvCluster#value} field. Pressing an item cycles it
 * through {@code selRange} states.
 * <p>
 * The display character for each state is taken from the {@code states} string
 * at the corresponding index (state 0 = {@code states.charAt(0)}, etc.).
 * <p>
 * The {@code flags} parameter encodes the bit layout:
 * <ul>
 *   <li>Low byte — a bitmask covering the bits used per item ({@code mask}).</li>
 *   <li>High byte — the number of bits allocated per item ({@code bits}).</li>
 * </ul>
 */
public class JtvMultiCheckBoxes extends JtvCluster {

    /** Number of distinct states each item can cycle through. */
    private int selRange;

    /**
     * Bit layout descriptor: low byte is the per-item bit mask, high byte is
     * the number of bits per item.
     */
    private int flags;

    /** Characters used to render each state index inside the check-box icon. */
    private String states;

    /** Icon template drawn for each item: {@code " [ ] "}. */
    private static final String multiCheckIcon = " [ ] ";

    /**
     * Creates a multi-state check-box cluster.
     *
     * @param bounds     the bounding rectangle
     * @param items      the label strings for each item
     * @param aSelRange  the number of states each item cycles through
     * @param aFlags     bit layout: low byte = mask, high byte = bits per item
     * @param aStates    characters for each state (index 0 = first state, etc.);
     *                   defaults to {@code " X"} if {@code null}
     */
    public JtvMultiCheckBoxes(JtvRect bounds, String[] items,
                            int aSelRange, int aFlags, String aStates) {
        super(bounds, items);
        selRange = aSelRange;
        flags = aFlags;
        states = aStates != null ? aStates : " X";
    }

    /**
     * Draws all multi-state check boxes using the {@code [ ]} icon with per-state characters.
     */
    @Override
    public void draw() {
        drawMultiBox(multiCheckIcon, states);
    }

    /**
     * Returns 4 — data is stored as a 32-bit integer to accommodate multi-bit items.
     *
     * @return 4
     */
    @Override
    public int dataSize() {
        return 4;
    }

    /**
     * Returns the current state index for item {@code item} by extracting the relevant
     * bits from {@link JtvCluster#value}.
     *
     * @param item the zero-based item index
     * @return the state index (0 to {@code selRange - 1})
     */
    @Override
    public int multiMark(int item) {
        int bits = (flags >> 8) & 0xFF;
        int mask = flags & 0xFF;
        return (int) ((value >> (item * bits)) & mask);
    }

    /**
     * Advances item {@code item} to its next state by incrementing its field in
     * {@link JtvCluster#value} modulo {@link #selRange}.
     *
     * @param item the zero-based item index
     */
    @Override
    public void press(int item) {
        int bits = (flags >> 8) & 0xFF;
        int mask = flags & 0xFF;
        int shift = item * bits;
        int current = (value >> shift) & mask;
        current = (current + 1) % selRange;
        value = (value & ~(mask << shift)) | (current << shift);
    }
}
