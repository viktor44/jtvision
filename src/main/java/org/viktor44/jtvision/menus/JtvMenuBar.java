/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.menus;

import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.ofPreProcess;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.util.StringUtils;

/**
 * The horizontal menu bar displayed at the top of the application screen.
 * <p>
 * JtvMenuBar renders its top-level {@link JtvMenuItem} list in a single
 * horizontal row, with each item preceded and followed by a padding space.
 * The currently highlighted item ({@link JtvMenuView#current}) is drawn in
 * the selected colour; disabled items use their own colour pair.
 * 
 * <h3>Activation</h3>
 * 
 * The menu bar can be activated in three ways:
 * <ul>
 *   <li><b>Mouse click</b> on any bar item triggers {@link #doASelect}.</li>
 *   <li><b>F10</b> sets {@link JtvMenuView#current} to the first item and
 *       posts a {@code cmMenu} command to start the modal loop.</li>
 *   <li><b>Alt+letter</b> matching a top-level item's hot key activates
 *       that item directly.</li>
 * </ul>
 * When {@code cmMenu} is received as a broadcast or command, the bar
 * activates and executes its modal loop, opening a child {@link JtvMenuBox}
 * for submenus.
 * 
 * <h3>Grow mode</h3>
 * 
 * {@code growMode} is set to {@code gfGrowHiX} so the bar stretches
 * horizontally when the application window is resized.
 * <p>
 * {@code ofPreProcess} is added to {@code options} so hot keys (Alt+letter,
 * F10) are processed before focus views see the event.
 * <p>
 * The bar uses the inherited {@link JtvMenuView#cpMenuView} palette.
 *
 * @see JtvMenuView
 * @see JtvMenuBox
 * @see JtvMenu
 */
public class JtvMenuBar extends JtvMenuView {

    /**
     * Constructs a menu bar with the given bounds and menu.
     * Sets {@code growMode} to {@code gfGrowHiX} and adds
     * {@code ofPreProcess} to options.
     *
     * @param bounds the bounding rectangle (typically the full top row)
     * @param aMenu  the {@link JtvMenu} whose items to display
     */
    public JtvMenuBar(JtvRect bounds, JtvMenu aMenu) {
        super(bounds, aMenu);
        growMode = gfGrowHiX;
        options |= ofPreProcess;
    }

    /**
     * Constructs a menu bar from a {@link JtvSubMenu} chain by wrapping it
     * in a new {@link JtvMenu}. Convenience overload for builder-style setup.
     *
     * @param bounds the bounding rectangle
     * @param aMenu  the root of a {@link JtvSubMenu} chain
     */
    public JtvMenuBar(JtvRect bounds, JtvSubMenu aMenu) {
        this(bounds, new JtvMenu().addItem(aMenu));
    }

    /**
     * Draws the menu bar in a single row.
     * <p>
     * Iterates the item list, writing each item's label with surrounding
     * spaces and the appropriate colour:
     * <ul>
     *   <li>Active and current — selected normal ({@code cSelect}).</li>
     *   <li>Disabled and current — selected disabled ({@code cSelDisabled}).</li>
     *   <li>Disabled, not current — normal disabled ({@code cNormDisabled}).</li>
     *   <li>Normal — {@code cNormal}.</li>
     * </ul>
     * Separator items (null name) are skipped.
     */
    @Override
    public void draw() {
        JtvDrawBuffer b = new JtvDrawBuffer();
        JtvColorAttr cNormal = getColor(1), cNormalHot = getColor(3);
        JtvColorAttr cSelect = getColor(4), cSelectHot = getColor(6);
        JtvColorAttr cNormDisabled = getColor(2), cNormDisabledHot = getColor(2);
        JtvColorAttr cSelDisabled = getColor(5), cSelDisabledHot = getColor(5);

        b.moveChar(0, ' ', cNormal, size.getX());

        if (menu != null) {
            int x = 1;
            for (JtvMenuItem p : menu.getItems()) {
                if (p.getName() != null) {
                    int l = StringUtils.cstrLen(p.getName());
                    JtvColorAttr color, colorHot;
                    if (p.isDisabled()) {
                        if (p == current) { color = cSelDisabled; colorHot = cSelDisabledHot; }
                        else { color = cNormDisabled; colorHot = cNormDisabledHot; }
                    } else {
                        if (p == current) { color = cSelect; colorHot = cSelectHot; }
                        else { color = cNormal; colorHot = cNormalHot; }
                    }
                    b.moveChar(x, ' ', color, 1);
                    b.moveCStr(x + 1, p.getName(), color, colorHot);
                    b.moveChar(x + l + 1, ' ', color, 1);
                    x += l + 2;
                }
            }
        }

        writeLine(0, 0, size.getX(), 1, b);
    }

    /**
     * Returns the bounding rectangle of {@code item} within the bar row.
     * Items are laid out sequentially from column 1; each item occupies
     * {@code name_length + 2} columns (one padding space on each side).
     *
     * @param item the menu item to locate
     * @return the item's rectangle in view-local coordinates, or a
     *         one-column rect at the current end position if not found
     */
    @Override
    public JtvRect getItemRect(JtvMenuItem item) {
        int x = 1;
        if (menu != null) {
            for (JtvMenuItem p : menu.getItems()) {
            if (p.getName() != null) {
                int l = StringUtils.cstrLen(p.getName()) + 2;
                if (p == item)
                    return new JtvRect(x, 0, x + l, 1);
                x += l;
            }
            }
        }
        return new JtvRect(x, 0, x + 1, 1);
    }

    /**
     * Handles menu bar activation events.
     * <p>
     * In addition to the inherited {@link JtvMenuView#handleEvent} processing:
     * <ul>
     *   <li><b>Alt+letter</b> — if the letter matches a top-level item's
     *       hot key, sets that item as {@link JtvMenuView#current}, converts
     *       the event to {@code evCommand cmMenu}, and activates the bar.</li>
     *   <li><b>F10</b> — focuses the first item and activates the bar.</li>
     *   <li><b>evCommand cmMenu</b> — activates the bar if items are
     *       available, using the previously remembered current item.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);

        if (event.getWhat() == evKeyDown) {
            // Alt+letter activates menu
            if (menu != null) {
                for (JtvMenuItem p : menu.getItems()) {
                    if (p.getName() != null) {
                        char c = StringUtils.hotKey(p.getName());
                        int altCode = JtvKey.getAltCode(c);
                        if (altCode != 0 && altCode == event.getKeyDown().getKeyStroke()) {
                            current = p;
                            menu.setDefaultItem(p);
                            event.setWhat(evCommand);
                            event.getMessage().setCommand(cmMenu);
                            event.getMessage().setInfoPtr(this);
                            activateMenu(event);
                            return;
                        }
                    }
                }
            }
            // F10 activates menu bar
            if (event.getKeyDown().getKeyCode() == JtvKey.kbF10) {
                if (menu != null && !menu.getItems().isEmpty()) {
                    current = menu.getItems().get(0);
                    event.setWhat(evCommand);
                    event.getMessage().setCommand(cmMenu);
                    event.getMessage().setInfoPtr(this);
                    activateMenu(event);
                }
            }
        } else if (event.getWhat() == evCommand && event.getMessage().getCommand() == cmMenu) {
            if (menu != null && !menu.getItems().isEmpty()) {
                if (current == null) {
                    current = menu.getItems().get(0);
                }
                activateMenu(event);
            }
        }
    }

    /**
     * Posts the activating event and executes the bar as a modal view via
     * the owner group. If a non-zero command is returned and is enabled,
     * posts an {@code evCommand} event. Clears the activating event.
     *
     * @param event the event that triggered activation
     */
    private void activateMenu(JtvEvent event) {
        putEvent(event);
        if (owner != null) {
            int result = owner.execView(this);
            if (result != 0 && commandEnabled(result)) {
                JtvEvent commandEvent = new JtvEvent();
                commandEvent.setWhat(evCommand);
                commandEvent.getMessage().setCommand(result);
                commandEvent.getMessage().setInfoPtr(null);
                putEvent(commandEvent);
            }
        }
        clearEvent(event);
    }
}
