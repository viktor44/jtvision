/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmNewColorItem;
import static org.viktor44.jtvision.core.CommandCodes.cmSaveColorIndex;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;

import java.util.ArrayList;
import java.util.List;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvListViewer;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * A list viewer that displays the colour groups in a {@link JtvColorDialog}.
 * <p>
 * TColorGroupList shows the top-level colour categories (e.g., "Desktop",
 * "Menus") from a list of {@link JtvColorGroup} objects. When the user
 * focuses a group, this viewer broadcasts {@code cmNewColorItem} so the companion
 * {@link JtvColorItemList} can update itself to show that group's items.
 * <p>
 * When a {@code cmSaveColorIndex} broadcast is received, the viewer stores the
 * currently focused item index back into the active group's {@link JtvColorGroup#index}
 * so the selection can be restored later.
 */
public class JtvColorGroupList extends JtvListViewer {

    /** The list of {@link JtvColorGroup} objects to display. */
    private List<JtvColorGroup> groups;

    /**
     * Creates a colour group list with the given bounds, scroll bar, and group list.
     *
     * @param bounds     the bounding rectangle
     * @param aScrollBar the vertical scroll bar
     * @param aGroups    the list of colour groups
     */
    public JtvColorGroupList(JtvRect bounds, JtvScrollBar aScrollBar, List<JtvColorGroup> aGroups) {
        super(bounds, 1, null, aScrollBar);
        groups = aGroups != null ? aGroups : new ArrayList<>();
        setRange(groups.size());
    }

    /**
     * Adds {@code item} to the group list, updates the range, and returns this list for chaining.
     *
     * @param item the group to add
     * @return this list
     */
    public JtvColorGroupList addGroup(JtvColorGroup item) {
        groups.add(item);
        setRange(groups.size());
        return this;
    }

    /**
     * Focuses item {@code item} and broadcasts {@code cmNewColorItem} with the
     * corresponding {@link JtvColorGroup} so the item list updates.
     *
     * @param item the zero-based group index to focus
     */
    @Override
    public void focusItem(int item) {
        super.focusItem(item);
        JtvColorGroup g = getGroup(item);
        if (g != null) {
            message(owner, evBroadcast, cmNewColorItem, g);
        }
    }

    /**
     * Returns the display name of the group at position {@code item},
     * truncated to {@code maxLen} characters.
     *
     * @param item   the zero-based group index
     * @param maxLen the maximum number of characters to return
     * @return the group name, or an empty string if out of range
     */
    @Override
    public String getText(int item, int maxLen) {
        JtvColorGroup g = getGroup(item);
        if (g == null || g.getName() == null) {
            return "";
        }
        return g.getName().length() > maxLen ? g.getName().substring(0, maxLen) : g.getName();
    }

    /**
     * Handles {@code cmSaveColorIndex} broadcasts by persisting the item index
     * from the event into the currently focused group.
     *
     * @param ev the incoming event
     */
    @Override
    public void handleEvent(JtvEvent ev) {
        super.handleEvent(ev);
        if (ev.getWhat() == evBroadcast && ev.getMessage().getCommand() == cmSaveColorIndex && ev.getMessage().getInfoPtr() instanceof Number) {
            setGroupIndex(focused, ((Number) ev.getMessage().getInfoPtr()).intValue());
        }
    }

    /**
     * Stores item index {@code itemNum} into the {@link JtvColorGroup#index} of group
     * {@code groupNum}.
     *
     * @param groupNum the zero-based group index
     * @param itemNum  the item index to store
     */
    public void setGroupIndex(int groupNum, int itemNum) {
        JtvColorGroup g = getGroup(groupNum);
        if (g != null) {
            g.setIndex(itemNum);
        }
    }

    /**
     * Returns the {@link JtvColorGroup} at position {@code groupNum} in the list.
     *
     * @param groupNum the zero-based group index
     * @return the group, or {@code null} if out of range
     */
    public JtvColorGroup getGroup(int groupNum) {
        if (groupNum < 0 || groupNum >= groups.size()) {
            return null;
        }
        return groups.get(groupNum);
    }

    /**
     * Returns the stored focused-item index for the group at {@code groupNum}.
     *
     * @param groupNum the zero-based group index
     * @return the saved item index, or 0 if the group does not exist
     */
    public int getGroupIndex(int groupNum) {
        JtvColorGroup g = getGroup(groupNum);
        return g != null ? g.getIndex() : 0;
    }

    /**
     * Returns the total number of groups in the list.
     *
     * @return the group count
     */
    public int getNumGroups() {
        return groups.size();
    }
}
