package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmNewColorIndex;
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
 * A list viewer that displays the colour items within the active {@link JtvColorGroup}.
 * <p>
 * TColorItemList shows the individual palette entries (e.g., "Normal text",
 * "Highlighted text") for the currently selected group in a {@link JtvColorDialog}.
 * <p>
 * When the user focuses an item, this viewer broadcasts:
 * <ul>
 *   <li>{@code cmSaveColorIndex} — saves the focused item index for the current group.</li>
 *   <li>{@code cmNewColorIndex} — sends the palette index of the focused item so the
 *       colour selectors and display can update.</li>
 * </ul>
 * <p>
 * When a {@code cmNewColorItem} broadcast arrives (from {@link JtvColorGroupList}
 * when the active group changes), this viewer replaces its item list with the new
 * group's items and restores the previously saved focus position.
 */
public class JtvColorItemList extends JtvListViewer {

    /** The list of {@link JtvColorItem} objects currently displayed. */
    private List<JtvColorItem> items;

    /**
     * Creates a colour item list with the given bounds, scroll bar, and initial item list.
     *
     * @param bounds     the bounding rectangle
     * @param aScrollBar the vertical scroll bar
     * @param aItems     the initial item list (may be {@code null})
     */
    public JtvColorItemList(JtvRect bounds, JtvScrollBar aScrollBar, List<JtvColorItem> aItems) {
        super(bounds, 1, null, aScrollBar);
        items = aItems != null ? aItems : new ArrayList<>();
        eventMask |= evBroadcast;
        setRange(items.size());
    }

    /**
     * Focuses item {@code item} and broadcasts the palette index of that item
     * so colour selectors can reflect the current colour.
     *
     * @param item the zero-based item index to focus
     */
    @Override
    public void focusItem(int item) {
        super.focusItem(item);
        message(owner, evBroadcast, cmSaveColorIndex, Integer.valueOf(item));
        JtvColorItem it = itemAt(item);
        if (it != null) {
            message(owner, evBroadcast, cmNewColorIndex, Integer.valueOf(it.getIndex()));
        }
    }

    /**
     * Returns the display name of the item at position {@code item},
     * truncated to {@code maxLen} characters.
     *
     * @param item   the zero-based item index
     * @param maxLen the maximum number of characters to return
     * @return the item name, or an empty string if out of range
     */
    @Override
    public String getText(int item, int maxLen) {
        JtvColorItem it = itemAt(item);
        if (it == null || it.getName() == null) {
            return "";
        }
        return it.getName().length() > maxLen ? it.getName().substring(0, maxLen) : it.getName();
    }

    /** Returns the item at position {@code idx}, or {@code null} if out of range. */
    private JtvColorItem itemAt(int idx) {
        if (idx < 0 || idx >= items.size()) {
            return null;
        }
        return items.get(idx);
    }

    /**
     * Handles {@code cmNewColorItem} broadcasts from {@link JtvColorGroupList}.
     * Replaces the item list with the new group's items and restores the saved focus index.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evBroadcast && event.getMessage().getCommand() == cmNewColorItem && event.getMessage().getInfoPtr() instanceof JtvColorGroup) {
            JtvColorGroup g = (JtvColorGroup) event.getMessage().getInfoPtr();
            items = g.getItems();
            setRange(items.size());
            focusItemNum(g.getIndex());
            drawView();
        }
    }
}
