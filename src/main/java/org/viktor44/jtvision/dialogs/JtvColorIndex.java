package org.viktor44.jtvision.dialogs;

/**
 * Stores the per-group selected-item indices for a {@link JtvColorDialog}.
 * <p>
 * TColorIndex is a data transfer object used by {@link JtvColorDialog} to save and
 * restore the focused item within each colour group. This allows the dialog to
 * remember which palette entry was last selected in each group across successive
 * opens.
 * <p>
 * The arrays are pre-allocated to a generous upper bound (256 groups); only the
 * first {@link #colorSize} entries are valid.
 */
public class JtvColorIndex {

    /** The index of the most recently active colour group. */
    protected int groupIndex;

    /** The number of valid entries in {@link #colorIndex}. */
    protected int colorSize;

    /**
     * Per-group focused-item indices. Entry {@code i} holds the index of the
     * focused {@link JtvColorItem} within group {@code i}.
     */
    protected int[] colorIndex = new int[256];
}
