package org.viktor44.jtvision.menus;

/**
 * A {@link JtvMenuItem} that represents a submenu entry in a menu bar or
 * another menu box.
 * <p>
 * TSubMenu is a convenience subclass that automatically creates an empty
 * {@link JtvMenu} as its {@link JtvMenuItem#subMenu} and provides a builder API
 * for populating it. This makes it easy to construct nested menu structures
 * in a readable, chained style:
 * <pre>
 * TSubMenu file = new TSubMenu("~F~ile", TKey.kbAltF)
 *     .addItem(new TMenuItem("~O~pen", cmFileOpen, TKey.kbF3))
 *     .addItem(new TMenuItem("~S~ave", cmFileSave, TKey.kbF2))
 *     .addSeparator()
 *     .addItem(new TMenuItem("E~x~it", cmQuit, TKey.kbAltX));
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
