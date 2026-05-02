/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.meDoubleClick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.awt.event.KeyEvent;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvListViewer;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * The list-viewer component inside a {@link JtvHistoryWindow}.
 * <p>
 * JtvHistoryViewer displays the history entries for a given history ID in
 * reverse-chronological order (most recent first). Selecting an entry — either
 * by pressing Enter, double-clicking, or calling {@link #selectItem(int)} —
 * ends the modal state with {@code cmOK}. Pressing Escape or receiving a
 * {@code cmCancel} command ends it with {@code cmCancel}.
 * <p>
 * This class is managed automatically by {@link JtvHistory} and does not normally
 * need to be used directly.
 * 
 * <h3>Colour palette</h3>
 * 
 * The five-entry palette {@link #cpHistoryViewer} maps to:
 * <ol>
 *   <li>Active (focused list) item text.</li>
 *   <li>Inactive (unfocused list) item text.</li>
 *   <li>Focused item highlight.</li>
 *   <li>Selected item text.</li>
 *   <li>Divider row text.</li>
 * </ol>
 */
public class JtvHistoryViewer extends JtvListViewer {

    /** The history ID whose entries this viewer displays. */
    private int historyId;

    /** The entries displayed, in reverse-chronological order (most recent first). */
    private List<String> entries = new ArrayList<String>();

    /**
     * Five-entry colour palette:
     * <ol>
     *   <li>{@code 0x06} — active (focused list) item text.</li>
     *   <li>{@code 0x06} — inactive (unfocused list) item text.</li>
     *   <li>{@code 0x07} — focused item highlight.</li>
     *   <li>{@code 0x06} — selected item text.</li>
     *   <li>{@code 0x06} — divider row text.</li>
     * </ol>
     */
    private static final JtvPalette cpHistoryViewer = new JtvPalette(new int[] {0x06, 0x06, 0x07, 0x06, 0x06});

    /**
     * Creates a history viewer for the given bounds, scroll bars, and history ID.
     * Loads entries from the global history and focuses the most recent one.
     *
     * @param bounds       the bounding rectangle
     * @param aHScrollBar  horizontal scroll bar (may be {@code null})
     * @param aVScrollBar  vertical scroll bar (may be {@code null})
     * @param aHistoryId   the ID of the history list to display
     */
    public JtvHistoryViewer(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, int aHistoryId) {
        super(bounds, 1, aHScrollBar, aVScrollBar);
        historyId = aHistoryId;
        refreshEntries();
    }

    /**
     * Reloads entries from the global history store in reverse order
     * and resets the list range and horizontal scroll bar.
     */
    protected void refreshEntries() {
        entries.clear();
        List<String> source = JtvHistory.getHistory(historyId);
        if (source != null) {
            entries.addAll(source);
            Collections.reverse(entries);
        }
        setRange(entries.size());
        if (range > 1) {
            focusItemNum(1);
        } else if (range == 1) {
            focusItemNum(0);
        }
        if (hScrollBar != null) {
            hScrollBar.setRange(0, Math.max(0, historyWidth() - size.getX() + 3));
        }
    }

    /**
     * Returns the default color palette {@code cpHistoryViewer}.
     *
     * @return the history viewer's color palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpHistoryViewer;
    }

    /**
     * Returns the display text for the entry at position {@code item},
     * truncated to {@code maxLen} characters.
     *
     * @param item   the zero-based item index
     * @param maxLen the maximum number of characters to return
     * @return the entry text, or an empty string if out of range
     */
    @Override
    public String getText(int item, int maxLen) {
        if (item < 0 || item >= entries.size()) {
            return "";
        }
        String s = entries.get(item);
        if (s.length() > maxLen) {
            return s.substring(0, maxLen);
        }
        return s;
    }

    /**
     * Returns the length of the longest entry, used to set the horizontal scroll range.
     *
     * @return the maximum entry width in characters
     */
    public int historyWidth() {
        int width = 0;
        for (String s : entries) {
            if (s != null && s.length() > width) {
                width = s.length();
            }
        }
        return width;
    }

    /**
     * Returns the text of the currently focused entry.
     *
     * @return the focused entry's text, or an empty string if nothing is focused
     */
    public String selectedText() {
        if (focused >= 0 && focused < entries.size()) {
            return entries.get(focused);
        }
        return "";
    }

    /**
     * Handles Enter (or double-click) to confirm a selection, and Escape (or
     * {@code cmCancel}) to dismiss the picker without making a selection.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        if ((event.getWhat() == evMouseDown && (event.getMouse().getEventFlags() & meDoubleClick) != 0)
            || (event.getWhat() == evKeyDown && event.getKeyDown().getKeyCode() == KeyEvent.VK_ENTER)) {
            endModal(cmOK);
            clearEvent(event);
            return;
        }
        if ((event.getWhat() == evKeyDown && event.getKeyDown().getKeyCode() == KeyEvent.VK_ESCAPE)
            || (event.getWhat() == evCommand && event.getMessage().getCommand() == cmCancel)) {
            endModal(cmCancel);
            clearEvent(event);
            return;
        }
        super.handleEvent(event);
    }

    /**
     * Selects the item at {@code item} and ends the modal state with {@code cmOK}.
     *
     * @param item the zero-based index of the selected entry
     */
    @Override
    public void selectItem(int item) {
        super.selectItem(item);
        endModal(cmOK);
    }
}
