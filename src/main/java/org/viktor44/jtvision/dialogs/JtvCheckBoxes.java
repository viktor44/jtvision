package org.viktor44.jtvision.dialogs;

import org.viktor44.jtvision.core.JtvRect;

/**
 * A cluster of independent check-box controls.
 * <p>
 * TCheckBoxes is a specialised {@link JtvCluster} that allows any combination of
 * items to be checked simultaneously. Each item corresponds to one bit in the
 * inherited {@link JtvCluster#value} field; pressing an item toggles its bit.
 * An {@code X} is displayed inside the box ({@code [X]}) when the item is checked.
 * <p>
 * Up to 16 items (bits 0–15) are supported by the default data-size of 2 bytes.
 * Check-box clusters are often accompanied by a {@link JtvLabel} that describes
 * the group.
 */
public class JtvCheckBoxes extends JtvCluster {

    /** Icon template drawn for each item: {@code " [ ] "} (space, open bracket, space, close bracket, space). */
    private static final String checkIcon = " [ ] ";

    /**
     * Creates a check-box cluster with the given bounds and item labels.
     *
     * @param bounds the bounding rectangle
     * @param items  the label strings for each check box
     */
    public JtvCheckBoxes(JtvRect bounds, String[] items) {
        super(bounds, items);
    }

    /**
     * Draws all check boxes using the {@code [ ]} / {@code [X]} icon.
     */
    @Override
    public void draw() {
        drawBox(checkIcon, 'X');
    }

    /**
     * Returns {@code true} if the check box at {@code item} is checked.
     * A check box is checked when bit {@code item} of {@link JtvCluster#value} is set.
     *
     * @param item the zero-based item index (0–15)
     * @return {@code true} if the item is checked
     */
    @Override
    public boolean mark(int item) {
        return (value & (1 << item)) != 0;
    }

    /**
     * Toggles the checked state of item {@code item} by flipping its bit in
     * {@link JtvCluster#value}.
     *
     * @param item the zero-based item index (0–15)
     */
    @Override
    public void press(int item) {
        value ^= (1 << item);
    }
}
