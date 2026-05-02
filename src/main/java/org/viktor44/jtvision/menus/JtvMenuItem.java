/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.menus;

import org.viktor44.jtvision.core.JtvKeyStroke;
import org.viktor44.jtvision.views.JtvView;

import lombok.Getter;
import lombok.Setter;

/**
 * A single menu item record.
 * <p>
 * JtvMenuItem represents one entry in a {@link JtvMenu}: a selectable action,
 * a submenu trigger, or a visual separator (divider line).
 * 
 * <h3>Item kinds</h3>
 * 
 * <ul>
 *   <li><b>Command item</b> — {@link #command} is non-zero, {@link #subMenu}
 *       is {@code null}. Selecting the item posts a {@code cmXXXX} command
 *       event. An optional {@link #keyCode} hotkey and {@link #param} label
 *       (e.g. {@code "Ctrl+X"}) may be supplied.</li>
 *   <li><b>Submenu item</b> — {@link #command} is zero, {@link #subMenu}
 *       points to a nested {@link JtvMenu}. Selecting opens a child
 *       {@link JtvMenuBox}.</li>
 *   <li><b>Separator</b> — {@link #name} is {@code null}. Rendered as a
 *       horizontal divider line inside a {@link JtvMenuBox}.</li>
 * </ul>
 * 
 * <h3>Hot keys</h3>
 * 
 * {@link #keyCode} holds the scan code of a keyboard shortcut that triggers
 * the item directly (typically a function key or Alt+letter combination).
 * {@link JtvMenuView} scans the key code during {@code evKeyDown} events.
 * 
 * <h3>Enabled state</h3>
 * 
 * {@link #disabled} is initialised from the current command set via
 * {@link JtvView#commandEnabled(int)}, and is updated whenever a
 * {@code cmCommandSetChanged} broadcast is received.
 *
 * @see JtvMenu
 * @see JtvSubMenu
 * @see JtvMenuView
 */
public class JtvMenuItem {

    /**
     * The display label for this menu item, with an optional tilde
     * ({@code ~}) surrounding the hot-key letter, e.g. {@code "~F~ile"}.
     * {@code null} for separator lines.
     */
	@Getter
	@Setter
    private String name;

	/**
     * The {@code cmXXXX} command constant posted when the item is selected,
     * or {@code 0} for submenu items.
     */
	@Getter
	@Setter
    private int command;

	/**
     * {@code true} if this item is currently disabled and cannot be selected.
     * Reflects {@link JtvView#commandEnabled(int)} at construction time and is
     * refreshed on {@code cmCommandSetChanged} broadcasts.
     */
	@Getter
	@Setter
    private boolean disabled;

	/**
     * The keystroke shortcut that triggers this item directly, or {@code null}
     * if there is no shortcut.
     */
	@Getter
	@Setter
    private JtvKeyStroke keyStroke;

	/**
     * The help context number for this item.
     * {@link org.viktor44.jtvision.core.ViewFlags#hcNoContext} indicates no context is assigned.
     */
	@Getter
	@Setter
    private int helpCtx;

	/**
     * The keyboard shortcut display string shown to the right of the item
     * inside a {@link JtvMenuBox}, e.g. {@code "Ctrl+X"}. {@code null} for
     * submenu items or items with no displayable shortcut.
     */
	@Getter
	@Setter
    private String param;

	/**
     * For submenu items ({@link #command} == 0), points to the nested
     * {@link JtvMenu} that is opened when the item is selected. {@code null}
     * for command items and separators.
     */
	@Getter
	@Setter
    private JtvMenu subMenu;

	/**
     * Creates a command menu item.
     *
     * @param name    the display label (may contain {@code ~} hot-key markers)
     * @param command the {@code cmXXXX} command to post on selection
     */
    public JtvMenuItem(String name, int command) {
        this(name, command, null, 0, null);
    }

	/**
     * Creates a command menu item with no help context and no param string.
     *
     * @param name      the display label (may contain {@code ~} hot-key markers)
     * @param command   the {@code cmXXXX} command to post on selection
     * @param keyStroke the shortcut keystroke, or {@code null}
     */
    public JtvMenuItem(String name, int command, JtvKeyStroke keyStroke) {
        this(name, command, keyStroke, 0, null);
    }

    /**
     * Creates a submenu item with no help context.
     *
     * @param name      the display label
     * @param keyStroke the hot-key keystroke (Alt+letter), or {@code null}
     * @param subMenu   the nested {@link JtvMenu} to open on selection
     */
    public JtvMenuItem(String name, JtvKeyStroke keyStroke, JtvMenu subMenu) {
        this(name, keyStroke, subMenu, 0);
    }

    /**
     * Creates a command menu item with all fields specified.
     * {@link #disabled} is set from the current command set.
     *
     * @param name      the display label
     * @param command   the {@code cmXXXX} command to post on selection
     * @param keyStroke the shortcut keystroke, or {@code null}
     * @param helpCtx   the help context number
     * @param param     the shortcut display string, or {@code null}
     */
    public JtvMenuItem(String name, int command, JtvKeyStroke keyStroke, int helpCtx, String param) {
        this.name = name;
        this.command = command;
        this.disabled = !JtvView.commandEnabled(command);
        this.keyStroke = keyStroke;
        this.helpCtx = helpCtx;
        this.param = param;
        this.subMenu = null;
    }

    /**
     * Creates a submenu item. {@link #command} is set to {@code 0} and
     * {@link #disabled} is always {@code false} — submenu items are never
     * disabled through the command set.
     *
     * @param name      the display label
     * @param keyStroke the hot-key keystroke (Alt+letter), or {@code null}
     * @param subMenu   the nested {@link JtvMenu}
     * @param helpCtx   the help context number
     */
    public JtvMenuItem(String name, JtvKeyStroke keyStroke, JtvMenu subMenu, int helpCtx) {
    	this.name = name;
    	this.keyStroke = keyStroke;
    	this.helpCtx = helpCtx;
    	this.subMenu = subMenu;
    }
}
