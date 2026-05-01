package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmColorBackgroundChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmColorForegroundChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmColorSet;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvRect;

/**
 * A radio-button cluster for selecting monochrome text attributes.
 * <p>
 * TMonoSelector is a specialised {@link JtvCluster} used by {@link JtvColorDialog} on
 * monochrome displays (when {@code showMarkers} is {@code true}). It offers five
 * predefined text attribute choices: Normal, Highlight, Underline, Inverse, and Bold.
 * <p>
 * Selecting an item sets {@link JtvCluster#value} to the corresponding CGA attribute
 * byte and broadcasts {@code cmColorForegroundChanged} and {@code cmColorBackgroundChanged}
 * so other views in the dialog update accordingly.
 * <p>
 * Responds to {@code cmColorSet} broadcasts by updating its selection to match the
 * closest predefined attribute.
 */
public class JtvMonoSelector extends JtvCluster {

    /**
     * CGA attribute bytes for each selectable monochrome style:
     * Normal (0x07), Highlight (0x0F), Underline (0x01), Inverse (0x70), Bold (0x09).
     */
    private static final int[] monoColors = {0x07, 0x0F, 0x01, 0x70, 0x09};

    /** Radio-button icon template. */
    private static final String button = " ( ) ";

    /** Item labels for the five monochrome attribute choices. */
    private static final String[] items = {
        "~N~ormal",
        "~H~ighlight",
        "~U~nderline",
        "~I~nverse",
        "~B~old"
    };

    /**
     * Creates a monochrome attribute selector with the given bounds.
     * Uses the predefined item labels and listens for {@code cmColorSet} broadcasts.
     *
     * @param bounds the bounding rectangle
     */
    public JtvMonoSelector(JtvRect bounds) {
        super(bounds, items);
        eventMask |= evBroadcast;
    }

    /**
     * Draws the monochrome attribute options as radio buttons.
     */
    @Override
    public void draw() {
        drawBox(button, '');
    }

    /**
     * Handles {@code cmColorSet} broadcasts by updating the selected item to the
     * one whose CGA attribute matches the received value.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evBroadcast && event.getMessage().getCommand() == cmColorSet) {
            int v = event.getMessage().getInfoPtr() instanceof Number ? ((Number) event.getMessage().getInfoPtr()).intValue() : 0;
            value = v;
            drawView();
        }
    }

    /**
     * Returns {@code true} if the item at {@code item} corresponds to the current
     * {@link JtvCluster#value} attribute.
     *
     * @param item the zero-based item index
     * @return {@code true} if this item's attribute matches the current value
     */
    @Override
    public boolean mark(int item) {
        return item >= 0 && item < monoColors.length && monoColors[item] == value;
    }

    /**
     * Broadcasts the foreground and background components of the current CGA attribute.
     * Called whenever the selected colour changes.
     */
    public void newColor() {
        message(owner, evBroadcast, cmColorForegroundChanged, Integer.valueOf(value & 0x0F));
        message(owner, evBroadcast, cmColorBackgroundChanged, Integer.valueOf((value >> 4) & 0x0F));
    }

    /**
     * Applies the CGA attribute for item {@code item} and broadcasts the colour change.
     *
     * @param item the zero-based item index that was pressed
     */
    @Override
    public void press(int item) {
        if (item >= 0 && item < monoColors.length) {
            value = monoColors[item];
            newColor();
        }
    }

    /**
     * Applies the CGA attribute for item {@code item} when the cursor moves to it,
     * and broadcasts the colour change.
     *
     * @param item the zero-based item index now under the cursor
     */
    @Override
    public void movedTo(int item) {
        if (item >= 0 && item < monoColors.length) {
            value = monoColors[item];
            newColor();
        }
    }
}
