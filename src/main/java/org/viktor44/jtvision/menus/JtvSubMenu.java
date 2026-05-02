/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.menus;

import org.viktor44.jtvision.core.JtvKeyStroke;

/**
 * A {@link JtvMenuItem} that represents a submenu entry in a menu bar or
 * another menu box.
 * <p>
 * JtvSubMenu is a convenience subclass that automatically creates an empty
 * {@link JtvMenu} as its {@link JtvMenuItem#subMenu} and provides a builder API
 * for populating it. This makes it easy to construct nested menu structures
 * in a readable, chained style:
 * <pre>
 * JtvSubMenu file = new JtvSubMenu("~F~ile", JtvKeyStroke.altKey(KeyEvent.VK_F))
 *     .addItem(new JtvMenuItem("~O~pen", cmFileOpen, JtvKeyStroke.of(KeyEvent.VK_F3)))
 *     .addItem(new JtvMenuItem("~S~ave", cmFileSave, JtvKeyStroke.of(KeyEvent.VK_F2)))
 *     .addSeparator()
 *     .addItem(new JtvMenuItem("E~x~it", cmQuit, JtvKeyStroke.altKey(KeyEvent.VK_X)));
 * </pre>
 * @see JtvMenuItem
 * @see JtvMenu
 * @see JtvMenuBar
 */
public class JtvSubMenu extends JtvMenuItem {

    /**
     * Creates a submenu item with no help context.
     *
     * @param aName the display label for this top-level menu entry
     *              (may contain {@code ~} hot-key markers, e.g. {@code "~F~ile"})
     */
    public JtvSubMenu(String aName) {
        this(aName, null, 0);
    }

    /**
     * Creates a submenu item with no help context.
     *
     * @param aName       the display label (may contain {@code ~} hot-key markers)
     * @param aKeyStroke  the Alt+letter keystroke that opens this submenu, or {@code null}
     */
    public JtvSubMenu(String aName, JtvKeyStroke aKeyStroke) {
        this(aName, aKeyStroke, 0);
    }

    /**
     * Creates a submenu item with a help context. An empty {@link JtvMenu} is
     * created and assigned to {@link JtvMenuItem#subMenu}; items are added
     * via {@link #addItem(JtvMenuItem)}.
     *
     * @param aName       the display label
     * @param aKeyStroke  the Alt+letter keystroke, or {@code null}
     * @param aHelpCtx    the help context number
     */
    public JtvSubMenu(String aName, JtvKeyStroke aKeyStroke, int aHelpCtx) {
        super(aName, aKeyStroke, new JtvMenu(), aHelpCtx);
    }

    /**
     * Appends {@code item} to this submenu's item list and returns
     * {@code this} for builder-style chaining.
     * <p>
     * If the submenu is currently empty, {@code item} becomes the first
     * (and default) entry. Otherwise it is appended at the end of the
     * existing list.
     *
     * @param item the {@link JtvMenuItem} to add; must not already be in a list
     * @return {@code this}
     */
    public JtvSubMenu addItem(JtvMenuItem item) {
        getSubMenu().addItem(item);
        return this;
    }

    public JtvSubMenu addSeparator() {
        getSubMenu().addSeparator();
        return this;
    }

}
