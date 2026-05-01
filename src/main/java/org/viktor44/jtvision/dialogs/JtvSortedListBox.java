/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvScrollBar;

import static org.viktor44.jtvision.core.CommandCodes.cmReleasedFocus;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;

import java.awt.event.KeyEvent;

/**
 * A list box that keeps its items in sorted order and supports incremental search.
 * <p>
 * JtvSortedListBox extends {@link JtvListBox} to maintain its item list in
 * case-insensitive sorted order. It provides incremental (type-ahead) searching:
 * as the user types characters the list scrolls to the first matching item.
 * <p>
 * The sort order and key extraction can be customised by overriding
 * {@link #compareItems(Object, Object)} and {@link #getKey(String)}.
 * The default implementation sorts objects by their {@link Object#toString()}
 * representation, case-insensitively.
 * <p>
 * Backspace removes the last typed search character. Typing a period ({@code .})
 * finds the dot in the currently focused item and truncates or extends the search
 * prefix to that position. The search prefix is reset when the focused item changes.
 */
public class JtvSortedListBox extends JtvListBox {

    /** The keyboard shift state at the time the incremental search began. */
    protected int shiftState;

    /** The character position of the search cursor within the search prefix (or -1 if no search is active). */
    protected int searchPos;

    /** The raw item objects in sorted order. */
    protected final List<Object> items = new ArrayList<Object>();

    /** The current incremental search prefix string. */
    protected String searchPrefix = "";

    /**
     * Creates a sorted list box with the given bounds, column count, and scroll bar.
     * A visible cursor is shown at column 1.
     *
     * @param bounds      the bounding rectangle
     * @param aNumCols    number of display columns
     * @param aScrollBar  vertical scroll bar (may be {@code null})
     */
    public JtvSortedListBox(JtvRect bounds, int aNumCols, JtvScrollBar aScrollBar) {
        super(bounds, aNumCols, null, aScrollBar);
        shiftState = 0;
        searchPos = -1;
        showCursor();
        setCursor(1, 0);
    }

    /**
     * Extracts the sort key from a search string.
     * Override to perform custom key extraction (e.g., strip a path prefix).
     *
     * @param s the raw search string typed by the user
     * @return the key object used to locate a matching item
     */
    protected Object getKey(String s) {
        return s;
    }

    /**
     * Compares two item objects for ordering.
     * The default implementation delegates to {@link #itemText(Object)} and compares
     * the resulting strings case-insensitively.
     *
     * @param a the first item
     * @param b the second item
     * @return negative, zero, or positive as {@code a} is less than, equal to, or greater than {@code b}
     */
    protected int compareItems(Object a, Object b) {
        return itemText(a).compareToIgnoreCase(itemText(b));
    }

    /**
     * Returns the display (and sort) text for an item object.
     * The default implementation calls {@link Object#toString()}.
     *
     * @param item the item object
     * @return the item's text representation
     */
    protected String itemText(Object item) {
        return item != null ? item.toString() : "";
    }

    /**
     * Returns the display text for item {@code item}, truncated to {@code maxLen} characters.
     *
     * @param item   the zero-based item index
     * @param maxLen the maximum number of characters to return
     * @return the item text, or an empty string if out of range
     */
    @Override
    public String getText(int item, int maxLen) {
        if (item < 0 || item >= items.size()) {
            return "";
        }
        String s = itemText(items.get(item));
        if (s.length() > maxLen) {
            return s.substring(0, maxLen);
        }
        return s;
    }

    /**
     * Returns the raw item list (in sorted order).
     *
     * @return the list of item objects
     */
    public List<Object> list() {
        return items;
    }

    /**
     * Replaces the current item list with {@code aList}, sorts it using
     * {@link #compareItems(Object, Object)}, resets the search state, and focuses
     * the first item.
     *
     * @param aList the new list of items; {@code null} is treated as an empty list
     */
    public void newListObjects(List<?> aList) {
        items.clear();
        if (aList != null) {
            items.addAll(aList);
            Collections.sort(items, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return compareItems(o1, o2);
                }
            });
        }
        setRange(items.size());
        searchPos = -1;
        searchPrefix = "";
        if (range > 0) {
            focusItemNum(0);
        }
        drawView();
    }

    /**
     * Extends the inherited event handler to add incremental (type-ahead) search.
     * <p>
     * Printable characters are appended to the search prefix and the list scrolls
     * to the first matching item. Backspace removes the last prefix character.
     * A period ({@code .}) navigates relative to a dot in the focused item's text.
     * The search prefix is reset whenever the focused item changes.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        int oldValue = focused;
        super.handleEvent(event);

        if (oldValue != focused || (event.getWhat() == evBroadcast && event.getMessage().getCommand() == cmReleasedFocus)) {
            searchPos = -1;
            searchPrefix = "";
        }

        if (event.getWhat() != evKeyDown) {
            return;
        }

        char keyChar = event.getKeyDown().getKeyChar();
        boolean isBack = event.getKeyDown().getKeyCode() == JtvKey.kbBack;
        if (!isBack && (keyChar == KeyEvent.CHAR_UNDEFINED || keyChar < 32)) {
            return;
        }

        int oldPos = searchPos;
        String curString;
        if (isBack) {
            if (searchPos == -1 || searchPrefix.isEmpty()) {
                return;
            }
            searchPrefix = searchPrefix.substring(0, searchPrefix.length() - 1);
            searchPos = searchPrefix.length() - 1;
            if (searchPos < 0) {
                shiftState = event.getKeyDown().getModifiers();
            }
            curString = searchPrefix;
        }
        else if (keyChar == '.') {
            int dot = focused < range ? getText(focused, 255).indexOf('.') : -1;
            if (dot < 0) {
                searchPos = -1;
                searchPrefix = "";
            }
            else {
                searchPos = dot;
                String base = focused < range ? getText(focused, 255) : "";
                searchPrefix = base.substring(0, Math.min(base.length(), dot + 1));
            }
            curString = searchPrefix;
        }
        else {
            if (searchPos == -1) {
                searchPrefix = "";
                shiftState = event.getKeyDown().getModifiers();
            }
            searchPos++;
            searchPrefix += keyChar;
            curString = searchPrefix;
        }

        int value = findByPrefix(curString);
        if (value >= 0 && value < range) {
            focusItemNum(value);
            setCursor(cursor.getX() + Math.max(0, searchPos - oldPos), cursor.getY());
        }
        else {
            searchPos = oldPos;
            if (oldPos < 0) {
                searchPrefix = "";
            }
            else if (searchPrefix.length() > oldPos + 1) {
                searchPrefix = searchPrefix.substring(0, oldPos + 1);
            }
        }

        clearEvent(event);
    }

    /**
     * Returns the index of the first item whose text starts with {@code prefix}
     * (case-insensitive), or {@code -1} if no match is found.
     *
     * @param prefix the prefix string to search for
     * @return the zero-based index of the matching item, or {@code -1}
     */
    protected int findByPrefix(String prefix) {
        if (prefix == null) {
            return -1;
        }
        String p = prefix.toUpperCase();
        for (int i = 0; i < items.size(); i++) {
            String t = itemText(items.get(i));
            if (t != null && t.toUpperCase().startsWith(p)) {
                return i;
            }
        }
        return -1;
    }
}
