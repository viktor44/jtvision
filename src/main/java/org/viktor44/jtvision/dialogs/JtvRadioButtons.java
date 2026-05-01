/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import org.viktor44.jtvision.core.JtvRect;

/**
 * A cluster of mutually exclusive radio-button controls.
 * <p>
 * JtvRadioButtons is a specialised {@link JtvCluster} where only one item can be
 * selected at a time. The inherited {@link JtvCluster#value} field stores the
 * zero-based index of the currently selected item. Pressing or moving to an item
 * sets {@code value} to that item's index.
 * <p>
 * A bullet character ({@code •}) appears inside the radio button ({@code (•)}) when
 * the item is selected. Radio-button clusters are often accompanied by a
 * {@link JtvLabel} that describes the group.
 */
public class JtvRadioButtons extends JtvCluster {

    /** Icon template drawn for each item: {@code " ( ) "} (space, open paren, space, close paren, space). */
    private static final String radioIcon = " ( ) ";

    /**
     * Creates a radio-button cluster with the given bounds and item labels.
     *
     * @param bounds the bounding rectangle
     * @param items  the label strings for each radio button
     */
    public JtvRadioButtons(JtvRect bounds, String[] items) {
        super(bounds, items);
    }

    /**
     * Draws all radio buttons using the {@code ( )} / {@code (•)} icon.
     */
    @Override
    public void draw() {
        drawBox(radioIcon, '•'); // bullet character for selected
    }

    /**
     * Returns {@code true} if the radio button at {@code item} is the selected one.
     *
     * @param item the zero-based item index
     * @return {@code true} if {@code item == value}
     */
    @Override
    public boolean mark(int item) {
        return item == value;
    }

    /**
     * Updates {@link JtvCluster#value} when the cursor moves to item {@code item}
     * without an explicit press, keeping the selection in sync with cursor movement.
     *
     * @param item the zero-based item index now under the cursor
     */
    @Override
    public void movedTo(int item) {
        value = item;
    }

    /**
     * Selects item {@code item} by setting {@link JtvCluster#value} to its index.
     *
     * @param item the zero-based item index to select
     */
    @Override
    public void press(int item) {
        value = item;
    }

    /**
     * Extends the inherited {@code setDataFrom} to synchronise {@link JtvCluster#sel}
     * with the newly loaded {@link JtvCluster#value}.
     *
     * @param rec an {@link Integer} containing the index of the item to select
     */
    @Override
    public void setDataFrom(Object rec) {
        super.setDataFrom(rec);
        sel = value;
    }
}
