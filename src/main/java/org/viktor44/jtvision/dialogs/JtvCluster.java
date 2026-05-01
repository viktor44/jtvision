/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.ViewFlags.hcNoContext;
import static org.viktor44.jtvision.core.ViewFlags.ofFirstClick;
import static org.viktor44.jtvision.core.ViewFlags.ofPostProcess;
import static org.viktor44.jtvision.core.ViewFlags.ofPreProcess;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.sfFocused;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;

import java.util.ArrayList;
import java.util.List;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.util.StringUtils;
import org.viktor44.jtvision.views.JtvView;

/**
 * Abstract base class for clustered controls such as check boxes and radio buttons.
 * <p>
 * A cluster is a group of labelled controls that all respond in a similar way.
 * The user can select items with mouse clicks, cursor movement (Up/Down),
 * and Alt+letter hot-keys. Each item can be enabled or disabled independently
 * via the {@link #enableMask} field.
 * <p>
 * The current state of the cluster is held in the {@link #value} field; the
 * exact semantics of {@code value} are defined by concrete subclasses
 * ({@link JtvCheckBoxes} toggles bits; {@link JtvRadioButtons} stores the index of
 * the selected item).
 * <p>
 * When the bounding rectangle is wide enough, items wrap into multiple columns.
 * 
 * <h3>Colour palette</h3>
 * 
 * The five-entry palette {@link #cpCluster} maps to:
 * <ol>
 *   <li>Normal item text.</li>
 *   <li>Selected item text.</li>
 *   <li>Shortcut key (normal state).</li>
 *   <li>Shortcut key (selected state).</li>
 *   <li>Disabled item text.</li>
 * </ol>
 */
public class JtvCluster extends JtvView {

    /**
     * Five-entry colour palette:
     * <ol>
     *   <li>{@code 0x10} — normal item text.</li>
     *   <li>{@code 0x11} — selected item text.</li>
     *   <li>{@code 0x12} — shortcut key (normal state).</li>
     *   <li>{@code 0x12} — shortcut key (selected state).</li>
     *   <li>{@code 0x1F} — disabled item text.</li>
     * </ol>
     */
    private static final JtvPalette cpCluster = new JtvPalette(
    		new int[] {0x10, 0x11, 0x12, 0x12, 0x1F}
    );

    /**
     * The current value of the cluster.
     * For {@link JtvCheckBoxes} each bit corresponds to a checked item.
     * For {@link JtvRadioButtons} this is the zero-based index of the selected item.
     */
    protected int value;

	/** The index of the currently highlighted (cursor-over) item within the cluster. */
    protected int sel;

    /** Ordered list of item label strings (may contain tilde-delimited hot-key characters). */
    protected List<String> strings;

    /**
     * Bitmask controlling which items are enabled.
     * Bit {@code i} (0-based) enables item {@code i}; a cleared bit disables the item.
     * Initialised to all-ones so all items start enabled.
     */
    protected long enableMask;

    /**
     * Creates a cluster with the given bounds and item labels.
     *
     * @param bounds the bounding rectangle
     * @param items  the item label strings; {@code null} entries are silently skipped
     */
    public JtvCluster(JtvRect bounds, String[] items) {
        super(bounds);
        value = 0;
        sel = 0;
        strings = new ArrayList<>();
        if (items != null) {
            for (String item : items)
                strings.add(item);
        }
        options |= ofSelectable | ofFirstClick | ofPreProcess | ofPostProcess;
        setCursor(2, 0);
        showCursor();
        enableMask = 0xFFFFFFFFL;
    }

    /**
     * Returns the size in bytes of the data transferred by {@link #getDataTo} and
     * {@link #setDataFrom}. Returns 2 (short) for compatibility with the original
     * Turbo Vision Pascal implementation.
     *
     * @return data record size in bytes
     */
    @Override
    public int dataSize() {
        return 2;
    }

    /**
     * Reads the cluster's current value into a record object.
     * Callers should read the {@link #value} field directly in this implementation.
     *
     * @param rec the destination record (unused in base implementation)
     */
    @Override
    public void getDataTo(Object rec) {
        // Caller should read value field directly
    }

    /**
     * Sets the cluster value from a record object and redraws the view.
     *
     * @param rec an {@link Integer} containing the new {@link #value}
     */
    @Override
    public void setDataFrom(Object rec) {
        if (rec instanceof Integer)
            value = (Integer) rec;
        drawView();
    }

    /**
     * Draws all items using a simple two-state (unmarked/marked) icon.
     * Delegates to {@link #drawMultiBox(String, String)} with a two-character marker string.
     *
     * @param icon   the icon template (e.g., {@code " [ ] "} or {@code " ( ) "})
     * @param marker the character placed inside the icon when an item is marked
     */
    public void drawBox(String icon, char marker) {
        String markers = " " + marker;
        drawMultiBox(icon, markers);
    }

    /**
     * Draws all cluster items in columns across the view.
     * Each item is rendered with the supplied icon; the character from {@code markers}
     * at the index returned by {@link #multiMark(int)} is placed in the icon's slot.
     *
     * @param icon    the icon template
     * @param markers string whose characters correspond to possible mark states
     */
    public void drawMultiBox(String icon, String markers) {
        JtvDrawBuffer b = new JtvDrawBuffer();
        JtvColorAttr cNorm = getColor(1), cNormHot = getColor(3);
        JtvColorAttr cSel = getColor(2), cSelHot = getColor(4);
        JtvColorAttr cDis = getColor(5), cDisHot = getColor(5);

        for (int i = 0; i < size.getY(); i++) {
            b.moveChar(0, ' ', cNorm, size.getX());
            for (int j = 0; j <= (strings.size() - 1) / size.getY() + 1; j++) {
                int cur = j * size.getY() + i;
                if (cur < strings.size()) {
                    int col = column(cur);
                    if (col < size.getX()) {
                        JtvColorAttr color, colorHot;
                        if (!buttonState(cur)) {
                            color = cDis; colorHot = cDisHot;
                        }
                        else if (cur == sel && (state & sfSelected) != 0) {
                            color = cSel; colorHot = cSelHot;
                        }
                        else {
                            color = cNorm; colorHot = cNormHot;
                        }
                        b.moveChar(col, ' ', color, size.getX() - col);
                        b.moveCStr(col, icon, color, colorHot);
                        int markIdx = multiMark(cur);
                        if (markIdx >= 0 && markIdx < markers.length())
                            b.putChar(col + 2, markers.charAt(markIdx));
                        b.moveCStr(col + 5, strings.get(cur), color, colorHot);
                    }
                }
            }
            writeBuf(0, i, size.getX(), 1, b);
        }
        setCursor(column(sel) + 2, row(sel));
    }

    /**
     * Returns the default color palette {@code cpCluster}.
     *
     * @return the cluster's color palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpCluster;
    }

    /**
     * Returns the help context for the currently highlighted item.
     * If the base help context is {@code hcNoContext} it is returned unchanged;
     * otherwise the item index is added to give per-item help.
     *
     * @return the help context identifier
     */
    @Override
    public int getHelpCtx() {
        if (helpCtx == hcNoContext)
            return hcNoContext;
        return helpCtx + sel;
    }

    /**
     * Handles mouse and keyboard events for the cluster.
     * <p>
     * Mouse clicks highlight and optionally press the item under the cursor.
     * Up/Down arrow keys move the selection. Alt+letter hot-keys and Space
     * (when focused) activate the corresponding item.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if ((options & ofSelectable) == 0) return;

        if (event.getWhat() == evMouseDown) {
            JtvPoint mouse = makeLocal(event.getMouse().getWhere());
            int i = findSel(mouse);
            if (i != -1 && buttonState(i))
                sel = i;
            drawView();
            do {
                mouse = makeLocal(event.getMouse().getWhere());
                if (findSel(mouse) == sel && buttonState(sel))
                    showCursor();
                else
                    hideCursor();
            } while (mouseEvent(event, evMouseMove));
            showCursor();
            mouse = makeLocal(event.getMouse().getWhere());
            if (findSel(mouse) == sel) {
                press(sel);
                drawView();
            }
            clearEvent(event);
        } else if (event.getWhat() == evKeyDown) {
            int s = sel;
            int kc = JtvKey.ctrlToArrow(event.getKeyDown().getKeyStroke()) & 0xFFFF;

            switch (kc) {
                case JtvKey.kbUp:
                    if ((state & sfFocused) != 0) {
                        int count = 0;
                        do {
                            count++;
                            s--;
                            if (s < 0) s = strings.size() - 1;
                        } while (!(buttonState(s) || count > strings.size()));
                        moveSel(count, s);
                        clearEvent(event);
                    }
                    break;
                case JtvKey.kbDown:
                    if ((state & sfFocused) != 0) {
                        int count = 0;
                        do {
                            count++;
                            s++;
                            if (s >= strings.size()) s = 0;
                        } while (!(buttonState(s) || count > strings.size()));
                        moveSel(count, s);
                        clearEvent(event);
                    }
                    break;
                default:
                    // Check hotkeys
                    for (int i = 0; i < strings.size(); i++) {
                        char c = StringUtils.hotKey(strings.get(i));
                        if (event.getKeyDown().getKeyCode() != 0 && c != 0 &&
                            (JtvKey.getAltCode(c) == event.getKeyDown().getKeyStroke() ||
                             ((state & sfFocused) != 0 &&
                              c == Character.toUpperCase(event.getKeyDown().getKeyChar())))) {
                            if (buttonState(i)) {
                                if (focus()) {
                                    sel = i;
                                    movedTo(sel);
                                    press(sel);
                                    drawView();
                                }
                                clearEvent(event);
                            }
                            return;
                        }
                    }
                    if (event.getKeyDown().getKeyChar() == ' ' && (state & sfFocused) != 0) {
                        press(sel);
                        drawView();
                        clearEvent(event);
                    }
            }
        }
    }

    /**
     * Moves the selection cursor to item {@code s} (if within range) and redraws.
     *
     * @param count the number of steps taken; must be {@code <= strings.size()}
     * @param s     the target item index
     */
    public void moveSel(int count, int s) {
        if (count <= strings.size()) {
            sel = s;
            movedTo(sel);
            drawView();
        }
    }

    /**
     * Returns whether the given item is currently marked (checked/selected).
     * The base implementation always returns {@code false}; subclasses override this.
     *
     * @param item the zero-based item index
     * @return {@code true} if the item is marked
     */
    public boolean mark(int item) {
        return false;
    }

    /**
     * Returns the mark-state index used by {@link #drawMultiBox} to select the
     * display character from the markers string.
     * Returns {@code 1} if {@link #mark(int)} is true, {@code 0} otherwise.
     *
     * @param item the zero-based item index
     * @return the index into the markers string
     */
    public int multiMark(int item) {
        return mark(item) ? 1 : 0;
    }

    /**
     * Called when the cursor moves to a new item without pressing it.
     * The base implementation does nothing; subclasses (e.g., {@link JtvRadioButtons})
     * override this to update the value.
     *
     * @param item the zero-based index of the item now under the cursor
     */
    public void movedTo(int item) {
    }

    /**
     * Called when the user presses the item at {@code item}.
     * The base implementation does nothing; subclasses override this to change
     * {@link #value}.
     *
     * @param item the zero-based index of the pressed item
     */
    public void press(int item) {
    }

    /**
     * Returns the X-coordinate of the left edge of the column containing {@code item}.
     * Items are laid out in rows of height {@code size.y}; this calculates the pixel
     * column offset for the given item index.
     *
     * @param item the zero-based item index
     * @return the column offset in character cells
     */
    public int column(int item) {
        if (item < size.getY())
            return 0;
        int width = 0;
        int col = -6;
        int l = 0;
        for (int i = 0; i <= item; i++) {
            if (i % size.getY() == 0) {
                col += width + 6;
                width = 0;
            }
            if (i < strings.size())
                l = StringUtils.cstrLen(strings.get(i));
            if (l > width) width = l;
        }
        return col;
    }

    /**
     * Returns the item index at mouse position {@code p}, or {@code -1} if the
     * position is outside the cluster or does not correspond to a valid item.
     *
     * @param p the mouse position in local coordinates
     * @return zero-based item index, or {@code -1}
     */
    public int findSel(JtvPoint p) {
        JtvRect r = getExtent();
        if (!r.contains(p)) return -1;
        int i = 0;
        while (p.getX() >= column(i + size.getY()))
            i += size.getY();
        int s = i + p.getY();
        if (s >= strings.size()) return -1;
        return s;
    }

    /**
     * Returns the row (Y offset) of item {@code item} within the cluster view.
     *
     * @param item the zero-based item index
     * @return the row index (0-based)
     */
    public int row(int item) {
        return item % size.getY();
    }

    /**
     * Returns whether item {@code item} is currently enabled.
     * An item is enabled when its corresponding bit in {@link #enableMask} is set.
     * Items beyond index 31 are always considered disabled.
     *
     * @param item the zero-based item index
     * @return {@code true} if the item is enabled
     */
    public boolean buttonState(int item) {
        if (item < 32)
            return (enableMask & (1L << item)) != 0;
        return false;
    }

    /**
     * Enables or disables the items selected by {@code aMask}.
     * Also updates {@link org.viktor44.jtvision.core.ViewFlags#ofSelectable} based on whether any items remain enabled.
     *
     * @param mask  bitmask of items to enable/disable (bit {@code i} = item {@code i})
     * @param enable {@code true} to enable the masked items, {@code false} to disable them
     */
    public void setButtonState(long mask, boolean enable) {
        if (!enable)
            enableMask &= ~mask;
        else
            enableMask |= mask;
        int n = strings.size();
        if (n < 32) {
            long testMask = (1L << n) - 1;
            if ((enableMask & testMask) != 0)
                options |= ofSelectable;
            else
                options &= ~ofSelectable;
        }
    }

    /**
     * Extends the inherited {@code setState} to redraw when the selected state changes.
     *
     * @param aState the state flag(s) being changed
     * @param enable {@code true} to set, {@code false} to clear
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);
        if (aState == sfSelected)
            drawView();
    }

    /**
     * The current value of the cluster.
     * For {@link JtvCheckBoxes} each bit corresponds to a checked item.
     * For {@link JtvRadioButtons} this is the zero-based index of the selected item.
     */
    public int getValue() {
		return value;
	}

    /**
     * The current value of the cluster.
     * For {@link JtvCheckBoxes} each bit corresponds to a checked item.
     * For {@link JtvRadioButtons} this is the zero-based index of the selected item.
     */
	public void setValue(int value) {
		this.value = value;
	}
}
