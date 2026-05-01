package org.viktor44.jtvision.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvListViewer;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * A scrollable list-box control backed by a {@link List} of strings.
 * <p>
 * TListBox is a concrete descendant of {@code TListViewer} that stores its
 * items in a {@link List}. For many use cases the default implementation
 * requires no subclassing. When items are not plain strings, override
 * {@link #getText(int, int)} to convert items to display text.
 * <p>
 * A new list is assigned at any time with {@link #newList(List)}, which
 * replaces the current list, resets the scroll range, and focuses the first item.
 * <p>
 * The data record for setting/reading this control is a single {@code int}
 * representing the index of the focused item.
 */
public class JtvListBox extends JtvListViewer {

    /** The list of string items displayed in the list box. */
    protected List<String> list;

    /**
     * Creates a list box with the given bounds, column count, and optional scroll bars.
     *
     * @param bounds       the bounding rectangle
     * @param numCols     number of display columns (usually 1)
     * @param hScrollBar  optional horizontal scroll bar (may be {@code null})
     * @param vScrollBar  optional vertical scroll bar (may be {@code null})
     */
    public JtvListBox(JtvRect bounds, int numCols,
                    JtvScrollBar hScrollBar, JtvScrollBar vScrollBar) {
        super(bounds, numCols, hScrollBar, vScrollBar);
        list = new ArrayList<>();
    }

    /**
     * Returns the display text for the item at position {@code item},
     * truncated to {@code maxLen} characters.
     *
     * @param item   the zero-based item index
     * @param maxLen the maximum number of characters to return
     * @return the item's display text, or an empty string if out of range
     */
    @Override
    public String getText(int item, int maxLen) {
        if (list != null && item >= 0 && item < list.size()) {
            String s = list.get(item);
            if (s.length() > maxLen)
                return s.substring(0, maxLen);
            return s;
        }
        return "";
    }

    /**
     * Replaces the current list with {@code aList}, updates the scroll range, and
     * focuses the first item.
     *
     * @param aList the new list of items; if {@code null} an empty list is used
     */
    public void newList(List<String> aList) {
        if (aList != null)
            list = aList;
        else
            list = new ArrayList<>();
        setRange(list.size());
        if (range > 0)
            focusItem(0);
        drawView();
    }

    /**
     * Returns the current list of items.
     *
     * @return the backing list
     */
    public List<String> getList() {
        return list;
    }

    /**
     * Returns 2 — the data record size (the focused-item index is stored as a short).
     *
     * @return 2
     */
    @Override
    public int dataSize() {
        return 2;
    }

    /**
     * Reads the focused item index. Callers should read the {@code focused} field directly.
     *
     * @param rec the destination record (unused)
     */
    @Override
    public void getDataTo(Object rec) {
        // Caller should read focused field
    }

    /**
     * Focuses the item at the given index and redraws.
     *
     * @param rec an {@link Integer} containing the item index to focus
     */
    @Override
    public void setDataFrom(Object rec) {
        if (rec instanceof Integer) {
            focusItem((Integer) rec);
            drawView();
        }
    }
}
