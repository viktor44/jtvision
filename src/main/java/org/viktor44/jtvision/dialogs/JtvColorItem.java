/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

/**
 * A named colour-palette item within a {@link JtvColorGroup}.
 * <p>
 * TColorItem represents a single named entry within a {@link JtvColorGroup}.
 * Each item has a display name and a palette index that identifies the
 * specific colour entry it controls in the application palette.
 *
 * @see JtvColorGroup
 * @see JtvColorItemList
 */
public class JtvColorItem {

    /** The display name shown in the colour item list. */
    private String name;

	/** The index into the application palette that this item controls. */
    private int index;

    /**
     * Creates a colour item.
     *
     * @param nm  the display name
     * @param idx the palette index this item controls
     */
    public JtvColorItem(String nm, int idx) {
        name = nm;
        index = idx;
    }

    /** The display name shown in the colour item list. */
    public String getName() {
		return name;
	}

	/** The index into the application palette that this item controls. */
	public int getIndex() {
		return index;
	}
}
