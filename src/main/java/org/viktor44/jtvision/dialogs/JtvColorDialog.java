package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmNewColorIndex;
import static org.viktor44.jtvision.core.CommandCodes.cmNewColorItem;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;

import java.util.List;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * A full-featured colour selection dialog box.
 * <p>
 * TColorDialog gives users a visual interface for changing any combination of
 * foreground and background colours in the application's palette. It is titled
 * "Colors" and contains:
 * <ul>
 *   <li>A colour-group list showing top-level categories.</li>
 *   <li>A colour-item list showing the individual palette entries in the selected group.</li>
 *   <li>Foreground and background {@link JtvColorSelector} grids.</li>
 *   <li>A {@link JtvColorDisplay} preview showing sample text in the selected colours.</li>
 *   <li>A hidden {@link JtvMonoSelector} shown on monochrome displays.</li>
 *   <li>OK and Cancel buttons.</li>
 * </ul>
 * <p>
 * The palette being edited is passed to the constructor and a copy is held in
 * {@link #pal}. {@link #getDataTo(Object)} and {@link #setDataFrom(Object)} transfer
 * the palette to/from a caller-supplied {@link JtvPalette} record.
 * <p>
 * The static {@link #colorIndexes} field remembers the last focused group and
 * per-group item selections across dialog invocations.
 */
public class JtvColorDialog extends JtvDialog {

    /** Working copy of the application palette being edited. */
    private JtvPalette pal;

    /** Preview view showing text in the selected colours. */
    private JtvColorDisplay display;

    /** List viewer for colour group names. */
    private JtvColorGroupList groups;

    /** Label for the foreground colour selector. */
    private JtvLabel forLabel;

    /** Foreground colour selector grid. */
    private JtvColorSelector forSel;

    /** Label for the background colour selector. */
    private JtvLabel bakLabel;

    /** Background colour selector grid. */
    private JtvColorSelector bakSel;

    /** Label for the monochrome attribute selector (shown only on monochrome displays). */
    private JtvLabel monoLabel;

    /** Monochrome attribute selector (hidden on colour displays). */
    private JtvMonoSelector monoSel;

    /**
     * Index of the most recently focused colour group.
     * Persisted via the static {@link #colorIndexes} field.
     */
    private int groupIndex;

    /**
     * Static storage for the per-group selected-item indices, shared across
     * successive openings of the colour dialog.
     */
    private static JtvColorIndex colorIndexes;

    private static final String colors = "Colors";
    private static final String groupText = "~G~roups";
    private static final String itemText = "~I~tems";
    private static final String forText = "~F~ore";
    private static final String bakText = "~B~ack";
    private static final String textText = "Text";
    private static final String colorText = "~C~olor";
    private static final String okText = "~O~K";
    private static final String cancelText = "Cancel";

    /**
     * Creates the colour dialog box.
     * <p>
     * Builds and inserts a 61×18 centred dialog with a group list, item list,
     * foreground/background colour selectors, a preview display, a hidden monochrome
     * selector, and OK/Cancel buttons. If {@code aPalette} is non-null, the dialog
     * is pre-populated via {@link #setDataFrom(Object)}.
     *
     * @param aPalette the application palette to edit; a copy is kept internally
     * @param aGroups  the list of {@link JtvColorGroup} objects defining the palette categories
     */
    public JtvColorDialog(JtvPalette aPalette, List<JtvColorGroup> aGroups) {
        super(new JtvRect(0, 0, 61, 18), colors);
        options |= ofCentered;
        pal = aPalette != null ? new JtvPalette(aPalette) : null;

        JtvScrollBar sb = new JtvScrollBar(new JtvRect(18, 3, 19, 14));
        insert(sb);
        groups = new JtvColorGroupList(new JtvRect(3, 3, 18, 14), sb, aGroups);
        insert(groups);
        insert(new JtvLabel(new JtvRect(2, 2, 8, 3), groupText, groups));

        sb = new JtvScrollBar(new JtvRect(41, 3, 42, 14));
        insert(sb);
        JtvColorItemList itemList = new JtvColorItemList(new JtvRect(21, 3, 41, 14), sb, aGroups != null && !aGroups.isEmpty() ? aGroups.get(0).getItems() : null);
        insert(itemList);
        insert(new JtvLabel(new JtvRect(20, 2, 25, 3), itemText, itemList));

        forSel = new JtvColorSelector(new JtvRect(45, 3, 57, 7), JtvColorSelector.ColorSel.csForeground);
        insert(forSel);
        forLabel = new JtvLabel(new JtvRect(45, 2, 57, 3), forText, forSel);
        insert(forLabel);

        bakSel = new JtvColorSelector(new JtvRect(45, 9, 57, 11), JtvColorSelector.ColorSel.csBackground);
        insert(bakSel);
        bakLabel = new JtvLabel(new JtvRect(45, 8, 57, 9), bakText, bakSel);
        insert(bakLabel);

        display = new JtvColorDisplay(new JtvRect(44, 12, 58, 14), textText);
        insert(display);

        monoSel = new JtvMonoSelector(new JtvRect(44, 3, 59, 7));
        monoSel.hide();
        insert(monoSel);
        monoLabel = new JtvLabel(new JtvRect(43, 2, 49, 3), colorText, monoSel);
        monoLabel.hide();
        insert(monoLabel);

        insert(new JtvButton(new JtvRect(36, 15, 46, 17), okText, cmOK, bfDefault));
        insert(new JtvButton(new JtvRect(48, 15, 58, 17), cancelText, cmCancel, bfNormal));
        selectNext(false);

        if (pal != null) {
            setDataFrom(pal);
        }
    }

    /**
     * Extends the inherited event handler to:
     * <ul>
     *   <li>Save the focused group index on {@code cmNewColorItem} broadcasts.</li>
     *   <li>Update the colour display on {@code cmNewColorIndex} broadcasts.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        if (event.getWhat() == evBroadcast && event.getMessage().getCommand() == cmNewColorItem) {
            groupIndex = groups.getFocused();
        }
        super.handleEvent(event);
        if (event.getWhat() == evBroadcast && event.getMessage().getCommand() == cmNewColorIndex && event.getMessage().getInfoPtr() instanceof Number) {
            int idx = ((Number) event.getMessage().getInfoPtr()).intValue();
            if (pal != null && idx >= 1 && idx <= pal.length()) {
                display.setColor(pal.getData(), idx - 1);
            }
        }
    }

    /**
     * Returns the number of bytes needed to store the palette, used by data transfer.
     *
     * @return the palette length, or 0 if no palette
     */
    @Override
    public int dataSize() {
        return pal != null ? pal.length() : 0;
    }

    /**
     * Copies the current (edited) palette into the caller-supplied {@link JtvPalette} record
     * after saving the current group/item selection indices to the static
     * {@link #colorIndexes}.
     *
     * @param rec a {@link JtvPalette} to receive the edited palette data
     */
    @Override
    public void getDataTo(Object rec) {
        getIndexes();
        if (rec instanceof JtvPalette && pal != null) {
            JtvPalette dst = (JtvPalette) rec;
            for (int i = 0; i < Math.min(dst.getData().length, pal.getData().length); i++) {
                dst.getData()[i] = pal.getData()[i];
            }
        }
    }

    /**
     * Loads a new palette from {@code rec}, restores the saved group/item selection
     * indices, and updates the colour display.
     *
     * @param rec a {@link JtvPalette} containing the palette to edit
     */
    @Override
    public void setDataFrom(Object rec) {
        if (rec instanceof JtvPalette) {
            pal = new JtvPalette((JtvPalette) rec);
        }
        setIndexes();
        if (pal != null) {
            int idx = groups.getGroupIndex(groupIndex);
            idx = Math.max(0, Math.min(idx, pal.getData().length - 1));
            display.setColor(pal.getData(), idx);
        }
        groups.focusItemNum(groupIndex);
        groups.select();
    }

    /**
     * Restores the per-group item selection indices from the static
     * {@link #colorIndexes} into the group list.
     */
    private void setIndexes() {
        int numGroups = groups.getNumGroups();
        if (colorIndexes == null || colorIndexes.colorSize != numGroups) {
            colorIndexes = new JtvColorIndex();
            colorIndexes.colorSize = numGroups;
            colorIndexes.groupIndex = 0;
            for (int i = 0; i < numGroups; i++) {
                colorIndexes.colorIndex[i] = 0;
            }
        }
        for (int i = 0; i < numGroups; i++) {
            groups.setGroupIndex(i, colorIndexes.colorIndex[i]);
        }
        groupIndex = colorIndexes.groupIndex;
    }

    /**
     * Saves the current per-group item selection indices into the static
     * {@link #colorIndexes} for use in subsequent dialog openings.
     */
    private void getIndexes() {
        int n = groups.getNumGroups();
        if (colorIndexes == null || colorIndexes.colorSize != n) {
            colorIndexes = new JtvColorIndex();
            colorIndexes.colorSize = n;
        }
        colorIndexes.groupIndex = groupIndex;
        for (int i = 0; i < n; i++) {
            colorIndexes.colorIndex[i] = groups.getGroupIndex(i);
        }
    }
}
