package org.viktor44.jtvision.dialogs;

import java.util.ArrayList;
import java.util.List;

/**
 * A named colour group used by {@link JtvColorDialog}.
 * <p>
 * TColorGroup represents a named category of palette entries (e.g., "Desktop",
 * "Menus"). Each group owns a list of {@link JtvColorItem} objects that
 * identify the individual palette slots within that category.
 * <p>
 * The {@link #index} field tracks which item in this group was most recently
 * focused in the colour item list, so the dialog can restore that position.
 *
 * @see JtvColorItem
 * @see JtvColorGroupList
 */
public class JtvColorGroup {

    /** The display name shown in the colour group list. */
    protected String name;

	/**
     * The index of the most recently focused item within this group's item list.
     * Used to restore the selection when switching between groups.
     */
    protected int index;

	/** The list of {@link JtvColorItem} objects belonging to this group. */
    protected List<JtvColorItem> items;

	/**
     * Creates a colour group with the given name and no items.
     *
     * @param nm the display name
     */
    public JtvColorGroup(String nm) {
        this(nm, null);
    }

    /**
     * Creates a colour group with a specified initial item.
     *
     * @param nm  the display name
     * @param itm an initial item to add to the group, or {@code null}
     */
    public JtvColorGroup(String nm, JtvColorItem itm) {
        name = nm;
        items = new ArrayList<>();
        if (itm != null) items.add(itm);
    }

    /**
     * Adds {@code item} to this group's item list and returns this group for chaining.
     *
     * @param item the item to add
     * @return this group
     */
    public JtvColorGroup addItem(JtvColorItem item) {
        items.add(item);
        return this;
    }

    /**
     * Adds item {@code i} to group {@code g}'s item list.
     *
     * @param g the group to add the item to; if {@code null} this is a no-op
     * @param i the item to add
     * @return {@code g} (unchanged reference)
     */
    public static JtvColorGroup appendItem(JtvColorGroup g, JtvColorItem i) {
        if (g == null) {
            return null;
        }
        g.addItem(i);
        return g;
    }

    /** The display name shown in the colour group list. */
    public String getName() {
		return name;
	}

	/**
     * The index of the most recently focused item within this group's item list.
     * Used to restore the selection when switching between groups.
     */
    public int getIndex() {
		return index;
	}

	/**
     * The index of the most recently focused item within this group's item list.
     * Used to restore the selection when switching between groups.
     */
	public void setIndex(int index) {
		this.index = index;
	}

	/** The list of {@link JtvColorItem} objects belonging to this group. */
    public List<JtvColorItem> getItems() {
		return items;
	}
}
