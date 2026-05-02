/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.menus;

/**
 * A {@link JtvMenuItem} that represents a submenu entry in a menu bar or
 * another menu box.
 * <p>
 * JtvSubMenu is a convenience subclass that automatically creates an empty
 * {@link JtvMenu} as its {@link JtvMenuItem#subMenu} and provides a builder API
 * for populating it. This makes it easy to construct nested menu structures
 * in a readable, chained style:
 * <pre>
 * JtvSubMenu file = new JtvSubMenu("~F~ile", JtvKey.kbAltF)
 *     .addItem(new JtvMenuItem("~O~pen", cmFileOpen, JtvKey.kbF3))
 *     .addItem(new JtvMenuItem("~S~ave", cmFileSave, JtvKey.kbF2))
 *     .addSeparator()
 *     .addItem(new JtvMenuItem("E~x~it", cmQuit, JtvKey.kbAltX));
 * </pre>
 * @see JtvMenuItem
 * @see JtvMenu
 * @see JtvMenuBar
 */
public class JtvSubMenu extends JtvMenuItem {

    /**
     * Creates a submenu item with no help context.
     * Equivalent to {@link #TSubMenu(String, int, int) TSubMenu(aName, aKeyCode, 0)}.
     *
     * @param aName    the display label for this top-level menu entry
     *                 (may contain {@code ~} hot-key markers, e.g. {@code "~F~ile"})
     */
    public JtvSubMenu(String aName) {
        this(aName, 0, 0);
    }

    /**
     * Creates a submenu item with no help context.
     * Equivalent to {@link #TSubMenu(String, int, int) TSubMenu(aName, aKeyCode, 0)}.
     *
     * @param aName    the display label for this top-level menu entry
     *                 (may contain {@code ~} hot-key markers, e.g. {@code "~F~ile"})
     * @param aKeyCode the Alt+letter scan code that opens this submenu
     */
    public JtvSubMenu(String aName, int aKeyCode) {
        this(aName, aKeyCode, 0);
    }

    /**
     * Creates a submenu item with a help context. An empty {@link JtvMenu} is
     * created and assigned to {@link JtvMenuItem#subMenu}; items are added
     * via {@link #addItem(JtvMenuItem)}.
     *
     * @param aName    the display label
     * @param aKeyCode the Alt+letter scan code
     * @param aHelpCtx the help context number
     */
    public JtvSubMenu(String aName, int aKeyCode, int aHelpCtx) {
        super(aName, aKeyCode, new JtvMenu(), aHelpCtx);
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
