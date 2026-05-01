/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.menus;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * A help-context-sensitive status line definition.
 * <p>
 * JtvStatusDef pairs a range of help context values ({@link #min} to
 * {@link #max}) with a list of {@link JtvStatusItem} records. A
 * {@link JtvStatusLine} holds an ordered list of TStatusDef records and
 * displays the items belonging to the <em>first</em> definition whose
 * range contains the current help context of the focused view.
 * <p>
 * <h3>How context selection works</h3>
 * {@link JtvStatusLine#update()} queries the focused view's help context,
 * then walks the {@link JtvStatusLine}'s definition list and selects the
 * first JtvStatusDef for which
 * {@code helpCtx >= min && helpCtx <= max}. The matched definition's
 * {@link #items} list is then rendered on the status line.
 * <p>
 * A catch-all definition covering all contexts can be created with
 * {@code min = 0} and {@code max = Integer.MAX_VALUE}.
 * <p>
 * JtvStatusDef records are typically added to a status line in sequence:
 * <pre>
 * JtvStatusLine line = new JtvStatusLine(bounds,
 *     new JtvStatusDef(hcNoContext, hcNoContext, defaultItems))
 *         .addDefinition(new JtvStatusDef(hcFileOpen, hcFileOpen, fileOpenItems));
 * </pre>
 *
 * @see JtvStatusItem
 * @see JtvStatusLine
 */
public class JtvStatusDef {

    /**
     * The minimum help context value (inclusive) for which this definition is selected.
     */
	@Getter
    private final int min;

	/**
     * The maximum help context value (inclusive) for which this definition is selected.
     */
	@Getter
    private final int max;

	/**
     * The status items to be displayed when this definition is active.
     * Items are rendered in list order.
     */
	@Getter
    private final List<JtvStatusItem> items = new ArrayList<>();

	/**
     * Creates a status definition.
     *
     * @param min   the minimum help context (inclusive)
     * @param max   the maximum help context (inclusive)
     * @param items the item list for this context range
     */
    public JtvStatusDef(int min, int max, List<JtvStatusItem> items) {
        this.min = min;
        this.max = max;
        if (items != null) {
        	this.items.addAll(items);
        }
    }

    public JtvStatusDef addItem(JtvStatusItem item) {
        if (item != null) {
            items.add(item);
        }
        return this;
    }
}
