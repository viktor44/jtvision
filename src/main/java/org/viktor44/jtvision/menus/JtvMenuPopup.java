package org.viktor44.jtvision.menus;

import org.viktor44.jtvision.core.JtvRect;

/**
 * A standalone pop-up menu box with no parent menu.
 * <p>
 * TMenuPopup is a thin convenience subclass of {@link JtvMenuBox} that sets
 * {@link JtvMenuView#parentMenu} to {@code null}. It is used when a context
 * menu or free-floating menu needs to be shown without being anchored to a
 * {@link JtvMenuBar} or another {@link JtvMenuBox}.
 * <p>
 * Because there is no parent menu, pressing Escape or clicking outside the
 * box unconditionally dismisses it rather than returning focus to a parent
 * menu level.
 *
 * @see JtvMenuBox
 * @see JtvMenuView
 */
public class JtvMenuPopup extends JtvMenuBox {

    /**
     * Constructs a pop-up menu with the given bounds and menu.
     * Passes {@code null} as the parent menu to
     * {@link JtvMenuBox#TMenuBox(JtvRect, JtvMenu, JtvMenuView)}.
     *
     * @param bounds the initial bounding rectangle (adjusted by
     *               {@link JtvMenuBox#adjustBounds()} before display)
     * @param aMenu  the {@link JtvMenu} whose items to display
     */
    public JtvMenuPopup(JtvRect bounds, JtvMenu aMenu) {
        super(bounds, aMenu, null);
    }
}
