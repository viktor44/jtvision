/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.menus;

import org.viktor44.jtvision.core.JtvKeyStroke;

import lombok.Getter;

/**
 * A single entry in a {@link JtvStatusLine} item list.
 * <p>
 * JtvStatusItem combines a visual label, a keyboard hot key, and a command
 * into one record. Status items serve two distinct roles:
 * <ul>
 *   <li><b>Visual label</b> — when {@link #text} is non-{@code null} it is
 *       drawn on the status line. Clicking on the label area triggers the
 *       associated {@link #command}.</li>
 *   <li><b>Hot-key binding</b> — when a key event matching {@link #keyCode}
 *       arrives, {@link JtvStatusLine#handleEvent} converts it to an
 *       {@code evCommand} event with {@link #command}, regardless of whether
 *       the item is visually shown. An item with a {@code null} text can
 *       therefore act as a silent hot-key-to-command mapping.</li>
 * </ul>
 * Items are stored in insertion order in {@link JtvStatusDef} and rendered
 * by {@link JtvStatusLine} in that same order.
 *
 * @see JtvStatusDef
 * @see JtvStatusLine
 */
public class JtvStatusItem {

    /**
     * The label string displayed on the status line, such as {@code "~F1~ Help"},
     * or {@code null} if this item is invisible (hot-key binding only).
     */
	@Getter
    private final String text;

	/**
     * The keystroke that triggers this item's command, or {@code null} if the item has no hot key.
     */
	@Getter
    private final JtvKeyStroke keyStroke;

	/**
     * The {@code cmXXXX} command constant posted as an {@code evCommand}
     * event when the label is clicked or the hot key is pressed.
     */
	@Getter
    private final int command;

	/**
     * Creates a status item.
     *
     * @param aText       the label string, or {@code null} for a hidden hot-key item
     * @param aKeyStroke  the hot-key keystroke, or {@code null}
     * @param aCommand    the command to generate on activation
     */
    public JtvStatusItem(String aText, JtvKeyStroke aKeyStroke, int aCommand) {
        text = aText;
        keyStroke = aKeyStroke;
        command = aCommand;
    }
}
