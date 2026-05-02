/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.menus;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents one level of a menu tree.
 * <p>
 * JtvMenu holds the item list of {@link JtvMenuItem} records that form a single
 * menu level — either the top-level menu bar or one drop-down submenu.
 * A {@link JtvMenuView} (and its concrete descendants {@link JtvMenuBar} and
 * {@link JtvMenuBox}) owns a TMenu and iterates over {@link #items} to render
 * and process the menu.
 * <p>
 * {@link #defaultItem} remembers the last item that was focused so that reopening
 * the menu restores the previous position. It is updated by
 * {@link JtvMenuView#execute()} after the user dismisses the menu.
 * <p>
 * TMenu objects are typically constructed by helper code (e.g. application
 * {@code initMenuBar}) and passed to a {@link JtvMenuBar} or
 * {@link JtvMenuBox} constructor.
 *
 * @see JtvMenuItem
 * @see JtvMenuView
 * @see JtvMenuBar
 * @see JtvMenuBox
 */
public class JtvMenu {

    /**
     * The ordered list of items in this menu.
     */
	@Getter
    private List<JtvMenuItem> items = new ArrayList<>();

	/**
     * Pointer to the default (last focused) item in the list. Used to
     * restore the highlighted position when the menu is re-opened.
     * Initially set to {@link #items} and updated by {@link JtvMenuView#execute()} after each interaction.
     */
	@Getter
	@Setter
    private JtvMenuItem defaultItem;

	/**
     * Constructs an empty menu with no items and no default.
     */
    public JtvMenu() {
        defaultItem = null;
    }

    /**
     * Constructs a menu and adds {@code aItems} (and any chained items).
     * The default item is set to the first inserted item.
     *
     * @param aItems the first {@link JtvMenuItem} to add
     */
    public JtvMenu(JtvMenuItem aItems) {
        defaultItem = null;
        addItem(aItems);
    }

    public JtvMenu addItem(JtvMenuItem item) {
        if (item != null) {
            items.add(item);
            if (defaultItem == null && !items.isEmpty()) {
                defaultItem = items.get(0);
            }
        }
        return this;
    }

    public JtvMenu addSeparator() {
        return addItem(new JtvMenuItem(null, 0, null, 0, null));
    }
}
