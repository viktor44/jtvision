/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.menus;

import static org.viktor44.jtvision.core.CommandCodes.cmCommandSetChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.EventCodes.evMouseUp;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.util.StringUtils;
import org.viktor44.jtvision.views.JtvView;

/**
 * Abstract base class for all menu view objects.
 * <p>
 * JtvMenuView provides the core event loop, keyboard navigation, mouse
 * tracking, hot-key dispatch, and command-set update logic shared by
 * {@link JtvMenuBar} (horizontal bar) and {@link JtvMenuBox} (drop-down or
 * pop-up box). Direct instances of JtvMenuView are not useful; use one of
 * the concrete subclasses instead.
 * 
 * <h3>Menu ownership chain</h3>
 * 
 * Menu views are nested via {@link #parentMenu}: a {@link JtvMenuBox} opened
 * from a bar entry knows its parent is the {@link JtvMenuBar}. Mouse and
 * keyboard events propagate back up the chain so that moving the pointer
 * between siblings or pressing Escape correctly closes child menus and
 * returns focus to the parent.
 * 
 * <h3>Event loop — {@link #execute()}</h3>
 * 
 * {@code execute()} runs a modal loop that processes mouse and keyboard
 * events until either a command item is selected or the menu is cancelled.
 * It is always called via {@link org.viktor44.jtvision.views.JtvGroup#execView} and must never be invoked
 * directly. The loop:
 * <ul>
 *   <li>Tracks the mouse to update {@link #current}.</li>
 *   <li>Handles arrow-key navigation via {@link #trackKey(boolean)}.</li>
 *   <li>Opens submenus into a child {@link JtvMenuBox} via {@code execView}.</li>
 *   <li>Returns the selected command, or {@code 0} if cancelled.</li>
 * </ul>
 * 
 * <h3>Hot keys and shortcuts</h3>
 * 
 * {@link #handleEvent(JtvEvent)} intercepts {@code evKeyDown} events before
 * the event loop and tries to match them against shortcut key codes stored
 * in the menu items ({@link JtvMenuItem#keyCode}). A match causes an
 * {@code evCommand} event to be posted without opening the menu visually.
 * 
 * <h3>Colour palette</h3>
 * 
 * The six-entry palette {@link #cpMenuView} maps to:
 * <ol>
 *   <li>{@code 2} — normal text.</li>
 *   <li>{@code 3} — disabled text.</li>
 *   <li>{@code 4} — shortcut letter.</li>
 *   <li>{@code 5} — selected shortcut.</li>
 *   <li>{@code 6} — selected normal.</li>
 *   <li>{@code 7} — selected disabled.</li>
 * </ol>
 *
 * @see JtvMenuBar
 * @see JtvMenuBox
 * @see JtvMenu
 * @see JtvMenuItem
 */
public class JtvMenuView extends JtvView {

    /**
     * The {@link JtvMenu} record whose items this view renders and processes.
     * {@code null} until assigned by a subclass constructor.
     */
    protected JtvMenu menu;

    /**
     * The parent menu view in the ownership chain, or {@code null} if this
     * is the top-level menu. Used to route mouse events and Escape-key
     * handling back to the owning bar or box.
     */
    protected JtvMenuView parentMenu;

    /**
     * The currently highlighted (focused) menu item, or {@code null} if
     * no item is highlighted. Updated by mouse tracking and keyboard
     * navigation; saved to {@link JtvMenu#defaultItem} when the menu closes.
     */
    protected JtvMenuItem current;

    /**
     * Six-entry colour palette shared by all menu view descendants:
     * <ol>
     *   <li>{@code 2} — normal text.</li>
     *   <li>{@code 3} — disabled text.</li>
     *   <li>{@code 4} — shortcut letter.</li>
     *   <li>{@code 5} — selected shortcut.</li>
     *   <li>{@code 6} — selected normal.</li>
     *   <li>{@code 7} — selected disabled.</li>
     * </ol>
     */
    private static final JtvPalette cpMenuView = new JtvPalette(new int[]{2, 3, 4, 5, 6, 7});

    /**
     * Constructs a menu view with the given bounds and no menu assigned.
     * Adds {@code evBroadcast} to the event mask so the view receives
     * {@code cmCommandSetChanged} broadcasts.
     *
     * @param bounds the bounding rectangle
     */
    public JtvMenuView(JtvRect bounds) {
        super(bounds);
        eventMask |= evBroadcast;
    }

    /**
     * Constructs a menu view with the given bounds and an initial menu.
     * Adds {@code evBroadcast} to the event mask.
     *
     * @param bounds the bounding rectangle
     * @param aMenu  the {@link JtvMenu} whose items to display
     */
    public JtvMenuView(JtvRect bounds, JtvMenu aMenu) {
        super(bounds);
        menu = aMenu;
        eventMask |= evBroadcast;
    }

    /**
     * Returns the menu view's shared colour palette {@link #cpMenuView}.
     *
     * @return the six-entry palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpMenuView;
    }

    /**
     * Returns the bounding rectangle of {@code item} within this view.
     * The base implementation returns an empty rectangle. Concrete
     * subclasses ({@link JtvMenuBar}, {@link JtvMenuBox}) must override this
     * to support mouse hit-testing.
     *
     * @param item the menu item whose position to compute
     * @return the rectangle occupied by the item in view-local coordinates
     */
    public JtvRect getItemRect(JtvMenuItem item) {
        return new JtvRect();
    }

    /**
     * Searches {@link #menu} (and recursively its submenus) for an item
     * whose {@link JtvMenuItem#keyCode} matches the key code in {@code e}.
     * If found and the item is enabled, posts an {@code evCommand} event
     * with the item's command and clears the key event.
     *
     * @param e the key-down event to match
     * @return {@code true} if a shortcut was matched and consumed
     */
    protected boolean tryCommandByShortcut(JtvEvent e) {
        if (menu == null || e.getWhat() != evKeyDown) {
            return false;
        }
        int keyCode = e.getKeyDown().getKeyStroke();
        for (JtvMenuItem p : menu.getItems()) {
            if (p.getName() != null) {
                if (p.getCommand() != 0 && p.getKeyStroke() != null && p.getKeyStroke().getKeyStroke() == keyCode && !p.isDisabled()) {
                    putEvent(e);
                    if (owner != null) {
                        JtvEvent commandEvent = new JtvEvent();
                        commandEvent.setWhat(evCommand);
                        commandEvent.getMessage().setCommand(p.getCommand());
                        commandEvent.getMessage().setInfoPtr(null);
                        putEvent(commandEvent);
                    }
                    clearEvent(e);
                    return true;
                }
                if (p.getCommand() == 0 && p.getSubMenu() != null) {
                    JtvMenuView top = topMenu();
                    if (top != null && top.tryCommandByShortcutInMenu(p.getSubMenu(), keyCode, e)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Recursively searches {@code searchMenu} for an item with the given
     * {@code keyCode}. Posts an {@code evCommand} if found and enabled.
     *
     * @param searchMenu  the menu to search
     * @param keyCode     the scan code to match
     * @param sourceEvent the original key-down event (cleared on match)
     * @return {@code true} if a match was found and the event consumed
     */
    protected boolean tryCommandByShortcutInMenu(JtvMenu searchMenu, int keyCode, JtvEvent sourceEvent) {
        if (searchMenu == null) {
            return false;
        }
        for (JtvMenuItem p : searchMenu.getItems()) {
            if (p.getName() != null) {
                if (p.getCommand() != 0 && p.getKeyStroke() != null && p.getKeyStroke().getKeyStroke() == keyCode && !p.isDisabled()) {
                    putEvent(sourceEvent);
                    if (owner != null) {
                        JtvEvent commandEvent = new JtvEvent();
                        commandEvent.setWhat(evCommand);
                        commandEvent.getMessage().setCommand(p.getCommand());
                        commandEvent.getMessage().setInfoPtr(null);
                        putEvent(commandEvent);
                    }
                    clearEvent(sourceEvent);
                    return true;
                }
                if (p.getCommand() == 0 && p.getSubMenu() != null && tryCommandByShortcutInMenu(p.getSubMenu(), keyCode, sourceEvent)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Runs the modal menu event loop.
     * <p>
     * Processes events until the user selects a command item (returns its
     * command code), presses Escape, or clicks outside all menus (returns
     * {@code 0}). This method must only be called via
     * {@link org.viktor44.jtvision.views.JtvGroup#execView}.
     * <p>
     * The loop handles:
     * <ul>
     *   <li>{@code evMouseDown/Up/Move} — track the pointer, auto-open
     *       submenus, and select the item on mouse-up.</li>
     *   <li>{@code evKeyDown} — Up/Down navigate within a box; Left/Right
     *       navigate across bar items; Enter selects; Escape exits; letter
     *       keys match hot-key characters.</li>
     *   <li>{@code evCommand cmMenu} — auto-opens the currently focused
     *       submenu.</li>
     * </ul>
     *
     * @return the command code of the selected item, or {@code 0} if
     *         the menu was cancelled
     */
    @Override
    public int execute() {
        boolean autoSelect = false;
        boolean mouseActive = false;
        boolean exitRequested = false;
        int result = 0;
        JtvMenuItem itemShown = null;

        current = (menu != null && menu.getDefaultItem() != null) ? menu.getDefaultItem() : firstItem(menu);

        do {
            JtvEvent e = getEvent(new JtvEvent());

            switch (e.getWhat()) {
                case evMouseDown:
                    if (mouseInView(e.getMouse().getWhere()) || mouseInOwner(e)) {
                        trackMouse(e, true);
                        mouseActive = true;
                        if (size.getY() == 1) {
                            autoSelect = true;
                        }
                    } else if (mouseInMenus(e)) {
                        return 0;
                    } else {
                        putEvent(e);
                        exitRequested = true;
                    }
                    break;

                case evMouseUp:
                    trackMouse(e, true);
                    if (mouseInOwner(e))
                        current = menu.getDefaultItem();
                    else if (current != null && current.getName() != null) {
                        if (current.getCommand() == 0) {
                            // Submenu
                            if (current != itemShown) {
                                autoSelect = true;
                            }
                        } else if (mouseActive) {
                            result = current.getCommand();
                            break;
                        }
                    }
                    mouseActive = false;
                    break;

                case evMouseMove:
                    if (mouseInView(e.getMouse().getWhere())) {
                        trackMouse(e, true);
                        mouseActive = true;
                    }
                    break;

                case evKeyDown:
                    switch (e.getKeyDown().getKeyCode()) {
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_DOWN:
                            if (size.getY() != 1) {
                                trackKey(e.getKeyDown().getKeyCode() == KeyEvent.VK_DOWN);
                            } else if (e.getKeyDown().getKeyCode() == KeyEvent.VK_DOWN) {
                                autoSelect = true;
                            }
                            break;
                        case KeyEvent.VK_LEFT:
                        case KeyEvent.VK_RIGHT:
                            if (size.getY() == 1) {
                                trackKey(e.getKeyDown().getKeyCode() == KeyEvent.VK_RIGHT);
                                autoSelect = true;
                            } else if (parentMenu != null) {
                                putEvent(e);
                                return 0;
                            }
                            break;
                        case KeyEvent.VK_HOME:
                        case KeyEvent.VK_END:
                            if (size.getY() != 1) {
                                current = firstItem(menu);
                                if (e.getKeyDown().getKeyCode() == KeyEvent.VK_END) {
                                    trackKey(false);
                                }
                            }
                            break;
                        case KeyEvent.VK_ENTER:
                            if (current != null) {
                                if (current.getCommand() != 0) {
                                    result = current.getCommand();
                                } else {
                                    autoSelect = true;
                                }
                            }
                            break;
                        case KeyEvent.VK_ESCAPE:
                            if (parentMenu != null && parentMenu.size.getY() == 1) {
                                putEvent(e);
                            } else {
                                clearEvent(e);
                            }
                            exitRequested = true;
                            break;
                        default:
                            if (menu != null) {
                                for (JtvMenuItem p : menu.getItems()) {
                                    if (p.getName() == null || p.isDisabled()) continue;
                                    char c = StringUtils.hotKey(p.getName());
                                    if (c == 0) continue;
                                    boolean match;
                                    if (size.getY() == 1) {
                                        // Menu bar: Alt+letter only
                                        match = e.getKeyDown().getModifiers() == InputEvent.ALT_DOWN_MASK
                                             && Character.toUpperCase(c) == e.getKeyDown().getKeyCode();
                                    } else {
                                        // Submenu box: bare letter (no modifiers) or Alt+letter
                                        match = (e.getKeyDown().getModifiers() == InputEvent.ALT_DOWN_MASK
                                              || e.getKeyDown().getModifiers() == 0)
                                             && Character.toUpperCase(c) == e.getKeyDown().getKeyCode();
                                    }
                                    if (match) {
                                        current = p;
                                        if (p.getCommand() != 0)
                                            result = p.getCommand();
                                        else
                                            autoSelect = true;
                                        break;
                                    }
                                }
                            }
                    }
                    break;

                case evCommand:
                    if (e.getMessage().getCommand() == cmMenu) {
                        autoSelect = true;
                    }
                    break;
            }

            if (current != null && current != itemShown && current.getName() != null) {
                itemShown = current;
                drawView();
            }

            // Handle submenu
            if (autoSelect && current != null && current.getCommand() == 0 && current.getSubMenu() != null) {
                autoSelect = false;
                JtvRect r = getItemRect(current);
                JtvMenuBox box = new JtvMenuBox(
	                    new JtvRect(r.getA().getX(), r.getB().getY(), r.getA().getX() + 20, r.getB().getY() + 10),
	                    current.getSubMenu(), this
                );
                if (owner != null) {
                    box.adjustBounds();
                    result = owner.execView(box);
                    if (menu != null) {
                        menu.setDefaultItem(current);
                    }
                }
            }

            if (result != 0) break;
            if (exitRequested) break;

        } while (true);

        if (current != null) {
            if (menu != null) {
                menu.setDefaultItem(current);
            }
            current = null;
            drawView();
        }
        return result;
    }

    /**
     * Updates {@link #current} to the item whose bounding rectangle
     * (from {@link #getItemRect(JtvMenuItem)}) contains the mouse pointer
     * position reported in {@code e}.
     *
     * @param e      the mouse event providing the pointer position
     * @param active unused; reserved for subclass extensions
     */
    protected void trackMouse(JtvEvent e, boolean active) {
        JtvPoint mouse = makeLocal(e.getMouse().getWhere());
        if (menu == null) {
            return;
        }
        for (JtvMenuItem p : menu.getItems()) {
            JtvRect r = getItemRect(p);
            if (r.contains(mouse)) {
                current = p;
                return;
            }
        }
    }

    /**
     * Advances or retreats {@link #current} by one non-separator item,
     * wrapping around at the ends of the list.
     *
     * @param findNext {@code true} to move forward (Down/Right),
     *                 {@code false} to move backward (Up/Left)
     */
    protected void trackKey(boolean findNext) {
        if (current == null) {
            current = firstItem(menu);
            if (!findNext) prevItem();
            if (current != null && current.getName() != null) return;
        }
        int maxSteps = (menu != null) ? menu.getItems().size() : 0;
        do {
            if (findNext) nextItem();
            else prevItem();
        } while (maxSteps-- > 0 && current != null && current.getName() == null);
    }

    /**
     * Advances {@link #current} to the next item in the list, wrapping
     * back to {@link JtvMenu#items} after the last item.
     */
    protected void nextItem() {
        if (menu == null || menu.getItems().isEmpty()) {
            current = null;
            return;
        }
        if (current == null) {
            current = menu.getItems().get(0);
            return;
        }
        int idx = menu.getItems().indexOf(current);
        if (idx < 0 || idx >= menu.getItems().size() - 1) {
            current = menu.getItems().get(0);
        }
        else {
            current = menu.getItems().get(idx + 1);
        }
    }

    /**
     * Retreats {@link #current} by one position, wrapping to the last
     * item when currently at the first item.
     */
    protected void prevItem() {
        if (menu == null || menu.getItems().isEmpty()) {
            current = null;
            return;
        }
        if (current == null) {
            current = menu.getItems().get(menu.getItems().size() - 1);
            return;
        }
        int idx = menu.getItems().indexOf(current);
        if (idx <= 0)
            current = menu.getItems().get(menu.getItems().size() - 1);
        else
            current = menu.getItems().get(idx - 1);
    }

    /**
     * Returns {@code true} if the mouse pointer reported in {@code e} is
     * within the bounding rectangle of the item currently highlighted in
     * {@link #parentMenu}. Used to detect whether a mouse-up on the parent
     * bar should close the child box rather than selecting an item.
     *
     * @param e the mouse event
     * @return {@code true} if the pointer is over the parent's current item
     */
    protected boolean mouseInOwner(JtvEvent e) {
        if (parentMenu == null) return false;
        JtvPoint mouse = parentMenu.makeLocal(e.getMouse().getWhere());
        JtvRect r = parentMenu.getItemRect(parentMenu.current);
        return r.contains(mouse);
    }

    /**
     * Returns {@code true} if the mouse pointer is within any ancestor
     * menu view in the {@link #parentMenu} chain. Used to route a click
     * that lands on a sibling menu entry back to the top-level handler.
     *
     * @param e the mouse event
     * @return {@code true} if the pointer is inside an ancestor menu
     */
    protected boolean mouseInMenus(JtvEvent e) {
        JtvMenuView p = parentMenu;
        while (p != null && !p.mouseInView(e.getMouse().getWhere()))
            p = p.parentMenu;
        return p != null;
    }

    /**
     * Walks the {@link #parentMenu} chain and returns the root (top-level)
     * menu view, which is the one with no parent.
     *
     * @return the top-most menu view in the chain
     */
    public JtvMenuView topMenu() {
        JtvMenuView p = this;
        while (p.parentMenu != null)
            p = p.parentMenu;
        return p;
    }

    /**
     * Executes this menu view as a modal sub-view via the owner group.
     * Posts {@code event} to signal activation, calls
     * {@link org.viktor44.jtvision.views.JtvGroup#execView}, and if a
     * non-zero command is returned and is enabled, posts an {@code evCommand}
     * event with that command. Clears the original event on return.
     *
     * @param event the activating event (typically {@code evMouseDown} or
     *              {@code evCommand cmMenu})
     */
    protected void doASelect(JtvEvent event) {
        putEvent(event);
        int command = (owner != null) ? owner.execView(this) : 0;
        if (command != 0 && commandEnabled(command)) {
            event.setWhat(evCommand);
            event.getMessage().setCommand(command);
            event.getMessage().setInfoPtr(null);
            putEvent(event);
        }
        clearEvent(event);
    }

    /**
     * Walks the entire menu tree rooted at {@code aMenu} and refreshes the
     * {@link JtvMenuItem#disabled} flag on each command item by calling
     * {@link JtvView#commandEnabled(int)}.
     *
     * @param aMenu the menu to update recursively
     * @return {@code true} if any item's enabled state changed
     */
    private boolean updateMenu(JtvMenu aMenu) {
        boolean changed = false;
        if (aMenu == null) return false;
        for (JtvMenuItem p : aMenu.getItems()) {
            if (p.getName() != null) {
                if (p.getCommand() == 0) {
                    if (updateMenu(p.getSubMenu())) changed = true;
                } else {
                    boolean commandState = commandEnabled(p.getCommand());
                    if (p.isDisabled() == commandState) {
                        p.setDisabled(!commandState);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    protected JtvMenuItem firstItem(JtvMenu aMenu) {
        if (aMenu == null || aMenu.getItems().isEmpty()) {
            return null;
        }
        return aMenu.getItems().get(0);
    }

    /**
     * Handles events for the menu view.
     * <p>
     * First tries shortcut keys via {@link #tryCommandByShortcut(JtvEvent)}.
     * Then:
     * <ul>
     *   <li>{@code evMouseDown} — activates the menu via {@link #doASelect}.</li>
     *   <li>{@code evCommand cmMenu} — activates the menu.</li>
     *   <li>{@code evBroadcast cmCommandSetChanged} — refreshes disabled
     *       flags via {@link #updateMenu} and redraws if changed.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        if (menu == null) return;
        if (event.getWhat() == evKeyDown && tryCommandByShortcut(event)) {
            return;
        }
        switch (event.getWhat()) {
            case evMouseDown:
                doASelect(event);
                break;
            case evCommand:
                if (event.getMessage().getCommand() == cmMenu)
                    doASelect(event);
                break;
            case evBroadcast:
                if (event.getMessage().getCommand() == cmCommandSetChanged) {
                    if (updateMenu(menu))
                        drawView();
                }
                break;
        }
    }
}
