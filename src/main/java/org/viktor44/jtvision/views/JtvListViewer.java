/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.views;

import static org.viktor44.jtvision.core.CommandCodes.cmListItemSelected;
import static org.viktor44.jtvision.core.CommandCodes.cmScrollBarChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmScrollBarClicked;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseAuto;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.EventCodes.meDoubleClick;
import static org.viktor44.jtvision.core.ViewFlags.ofFirstClick;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;
import static org.viktor44.jtvision.core.ViewFlags.sfVisible;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;

/**
 * A scrollable list view that displays a range of selectable items in one or
 * more columns.
 * <p>
 * JtvListViewer provides a general-purpose list control backed by an abstract
 * {@link #getText(int, int)} method that subclasses override to supply each
 * item's display string. The viewer handles keyboard navigation (arrow keys,
 * Page Up/Down, Home, End), mouse clicks and drag-selection, and optional
 * horizontal and vertical scroll bars.
 * 
 * <h3>Multi-column layout</h3>
 * 
 * When {@code aNumCols > 1}, items are distributed across columns top-to-bottom
 * within each column: item {@code j * viewHeight + i} appears at column
 * {@code j}, row {@code i}. A vertical separator character ({@code │})
 * is drawn between columns.
 * 
 * <h3>Selection markers</h3>
 * 
 * When {@link JtvView#showMarkers} is {@code true}, the characters from
 * {@link #specialChars} are placed at the left and right edges of each item:
 * {@code »} and {@code «} for selected items, {@code •} for focused items,
 * and spaces otherwise.
 * 
 * <h3>Colour palette</h3>
 * 
 * The five-entry palette {@link #cpListViewer} maps to:
 * <ol>
 *   <li>{@code 0x1A} — normal colour (active).</li>
 *   <li>{@code 0x1A} — normal colour (inactive).</li>
 *   <li>{@code 0x1B} — focused item colour.</li>
 *   <li>{@code 0x1C} — selected item colour.</li>
 *   <li>{@code 0x1D} — column-separator colour.</li>
 * </ol>
 *
 * @see JtvScrollBar
 */
public class JtvListViewer extends JtvView {

    /**
     * The number of display columns the list is divided into.
     * Items are distributed top-to-bottom within each column.
     */
    protected int numCols;

    /**
     * The index of the first item visible in the topmost row of the view.
     */
    protected int topItem;

    /**
     * The index of the currently focused (keyboard-cursor) item.
     */
    protected int focused;

	/**
     * The total number of items in the list. Items at indices
     * {@code [0, range)} are valid; indices &ge; {@code range} are blank.
     */
    protected int range;

    /**
     * The optional horizontal scroll bar. {@code null} if not used.
     */
    protected JtvScrollBar hScrollBar;

    /**
     * The optional vertical scroll bar. {@code null} if not used.
     */
    protected JtvScrollBar vScrollBar;

    /**
     * Characters used to mark list items when {@link JtvView#showMarkers} is
     * {@code true}. Layout: {@code [focLeft, focRight, selLeft, selRight,
     * normLeft, normRight]}, where the pairs are the left/right edge markers
     * for focused, selected, and normal items respectively.
     * <ul>
     *   <li>{@code »} (») — left marker for selected items.</li>
     *   <li>{@code «} («) — right marker for selected items.</li>
     *   <li>{@code •} (•) — left/right marker for focused items.</li>
     * </ul>
     */
    public static final char[] specialChars = {' ', ' ', '»', '«', '•', ' '};

    /**
     * Text displayed in the first cell when the list is empty ({@code range == 0}).
     */
    protected String emptyText = "";

    /**
     * Five-entry colour palette:
     * <ol>
     *   <li>{@code 0x1A} — normal (active).</li>
     *   <li>{@code 0x1A} — normal (inactive).</li>
     *   <li>{@code 0x1B} — focused item.</li>
     *   <li>{@code 0x1C} — selected item.</li>
     *   <li>{@code 0x1D} — column separator.</li>
     * </ol>
     */
    private static final JtvPalette cpListViewer = new JtvPalette(new int[] {0x1A, 0x1A, 0x1B, 0x1C, 0x1D});

    /**
     * Constructs a list viewer with the given bounds, column count, and
     * optional scroll bars.
     * <p>
     * Sets {@code ofFirstClick} and {@code ofSelectable} on options so the
     * viewer takes focus on the first mouse click and accepts keyboard input.
     * Adds {@code evBroadcast} to the event mask to receive scroll-bar
     * notifications. Configures scroll bar page and arrow step sizes based on
     * the view height and column count.
     *
     * @param bounds        the bounding rectangle
     * @param aNumCols      number of display columns (&ge; 1)
     * @param aHScrollBar   horizontal scroll bar, or {@code null}
     * @param aVScrollBar   vertical scroll bar, or {@code null}
     */
    public JtvListViewer(JtvRect bounds, int aNumCols,
                       JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar) {
        super(bounds);
        numCols = aNumCols;
        topItem = 0;
        focused = 0;
        range = 0;

        options |= ofFirstClick | ofSelectable;
        eventMask |= evBroadcast;

        if (aVScrollBar != null) {
            int pgStep, arStep;
            if (numCols == 1) {
                pgStep = size.getY() - 1;
                arStep = 1;
            }
            else {
                pgStep = size.getY() * numCols;
                arStep = size.getY();
            }
            aVScrollBar.setStep(pgStep, arStep);
        }

        if (aHScrollBar != null) {
            aHScrollBar.setStep(size.getX() / numCols, 1);
        }

        hScrollBar = aHScrollBar;
        vScrollBar = aVScrollBar;
    }

    /**
     * Adjusts scroll bar step sizes after a bounds change to match the new
     * view dimensions.
     *
     * @param bounds the new bounding rectangle
     */
    @Override
    public void changeBounds(JtvRect bounds) {
        super.changeBounds(bounds);
        if (hScrollBar != null) {
            hScrollBar.setStep(size.getX() / numCols, hScrollBar.getArStep());
        }
        if (vScrollBar != null) {
            vScrollBar.setStep(size.getY(), vScrollBar.getArStep());
        }
    }

    /**
     * Renders all visible items across all columns.
     * <p>
     * Each row-column cell computes the item index as
     * {@code col * viewHeight + row + topItem}. Items at or beyond
     * {@link #range} are drawn blank. When the list is empty the first cell
     * shows {@link #emptyText}. A vertical separator is drawn at the right
     * edge of each column.
     * <p>
     * Colour selection:
     * <ul>
     *   <li>Focused and active — palette entry 3 (focused colour).</li>
     *   <li>Selected — palette entry 4.</li>
     *   <li>Normal — palette entry 1 (active) or 2 (inactive).</li>
     * </ul>
     * The text cursor is moved to the focused item's cell; if the focused item
     * is not visible it is placed off-screen at {@code (-1, -1)}.
     */
    @Override
    public void draw() {
        JtvColorAttr normalColor, selectedColor, focusedColor, color;

        if ((state & (sfSelected | sfActive)) == (sfSelected | sfActive)) {
            normalColor = getColor(1);
            focusedColor = getColor(3);
            selectedColor = getColor(4);
        }
        else {
            normalColor = getColor(2);
            selectedColor = getColor(4);
            focusedColor = null;
        }

        int indent = (hScrollBar != null) ? hScrollBar.getValue() : 0;
        boolean focusedVis = false;
        int colWidth = size.getX() / numCols + 1;
        JtvDrawBuffer b = new JtvDrawBuffer();

        for (int i = 0; i < size.getY(); i++) {
            for (int j = 0; j < numCols; j++) {
                int item = j * size.getY() + i + topItem;
                int curCol = j * colWidth;
                int scOff;

                if ((state & (sfSelected | sfActive)) == (sfSelected | sfActive)
                		&& focused == item && range > 0) {
                    color = focusedColor;
                    setCursor(curCol + 1, i);
                    scOff = 0;
                    focusedVis = true;
                }
                else if (item < range && isSelected(item)) {
                    color = selectedColor;
                    scOff = 2;
                }
                else {
                    color = normalColor;
                    scOff = 4;
                }

                b.moveChar(curCol, ' ', color, colWidth);
                if (item < range) {
                    String text = getText(item, 255);
                    if (text != null) {
                        b.moveStr(curCol + 1, text, color, colWidth - 2, indent);
                    }
                    if (showMarkers) {
                        b.putChar(curCol, specialChars[scOff]);
                        b.putChar(curCol + colWidth - 2, specialChars[scOff + 1]);
                    }
                }
                else if (i == 0 && j == 0) {
                    b.moveStr(curCol + 1, emptyText, getColor(1));
                }

                b.moveChar(curCol + colWidth - 1, '│', getColor(5), 1);
            }
            writeLine(0, i, size.getX(), 1, b);
        }

        if (!focusedVis)
            setCursor(-1, -1);
    }

    /**
     * Moves the focus to {@code item} and scrolls the vertical scroll bar (or
     * redraws directly) so the item is visible. Updates {@link #topItem} if
     * the item has scrolled out of the visible window.
     *
     * @param item the item index to focus; must be in {@code [0, range)}
     */
    public void focusItem(int item) {
        focused = item;
        if (vScrollBar != null) {
            vScrollBar.setValue(item);
        }
        else {
            drawView();
        }
        if (size.getY() > 0) {
            if (item < topItem) {
                if (numCols == 1) {
                    topItem = item;
                }
                else {
                    topItem = item - item % size.getY();
                }
            }
            else if (item >= topItem + size.getY() * numCols) {
                if (numCols == 1) {
                    topItem = item - size.getY() + 1;
                }
                else {
                    topItem = item - item % size.getY() - (size.getY() * (numCols - 1));
                }
            }
        }
    }

    /**
     * Clamps {@code item} to the valid range {@code [0, range - 1]} and
     * delegates to {@link #focusItem(int)}. Does nothing if {@link #range}
     * is zero.
     *
     * @param item the desired focus index (clamped automatically)
     */
    public void focusItemNum(int item) {
        if (item < 0) {
            item = 0;
        }
        else if (item >= range && range > 0) {
            item = range - 1;
        }
        if (range != 0) {
            focusItem(item);
        }
    }

    /**
     * Returns the list viewer's colour palette {@link #cpListViewer}.
     *
     * @return the five-entry palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpListViewer;
    }

    /**
     * Returns the display text for the given item index, truncated to
     * {@code maxLen} characters.
     * <p>
     * The base implementation returns an empty string. Subclasses must
     * override this method to provide meaningful content.
     *
     * @param item   the zero-based item index
     * @param maxLen the maximum number of characters to return
     * @return the item's display text, or an empty string
     */
    public String getText(int item, int maxLen) {
        return "";
    }

    /**
     * Returns {@code true} if {@code item} is the currently focused item.
     * Subclasses with multi-selection can override this to reflect a richer
     * selection model.
     *
     * @param item the zero-based item index to test
     * @return {@code true} if the item is selected/focused
     */
    public boolean isSelected(int item) {
        return item == focused;
    }

    /**
     * Handles mouse clicks, drag-selection, and keyboard navigation.
     * <p>
     * <b>Mouse ({@code evMouseDown}):</b> computes the item under the pointer,
     * calls {@link #focusItemNum(int)}, and loops while the button is held
     * ({@code evMouseMove | evMouseAuto}). Auto-scroll moves the focus when
     * the pointer is dragged above or below the view. A double-click triggers
     * {@link #selectItem(int)}.
     * <p>
     * <b>Keyboard ({@code evKeyDown}):</b> arrow keys, Page Up/Down, Home,
     * and End adjust {@link #focused} via {@link #focusItemNum(int)}.
     * Right/Left arrows move by a full column ({@code size.getY()}) when
     * {@code numCols > 1}.
     * <p>
     * <b>Broadcast ({@code evBroadcast}):</b> {@code cmScrollBarClicked}
     * selects this view; {@code cmScrollBarChanged} from the vertical bar
     * updates the focused item, from the horizontal bar redraws.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);

        int newItem = focused;

        if (event.getWhat() == evMouseDown) {
            int colWidth = size.getX() / numCols + 1;
            int oldItem = focused;
            int count = 0;
            int mouseAutosToSkip = 4;
            do {
                JtvPoint mouse = makeLocal(event.getMouse().getWhere());
                if (mouseInView(event.getMouse().getWhere())) {
                    newItem = mouse.getY() + (size.getY() * (mouse.getX() / colWidth)) + topItem;
                }
                else {
                    if (numCols == 1) {
                        if (event.getWhat() == evMouseAuto) {
                        	count++;
                        }
                        if (count == mouseAutosToSkip) {
                            count = 0;
                            if (mouse.getY() < 0) {
                            	newItem = focused - 1;
                            }
                            else if (mouse.getY() >= size.getY()) {
                            	newItem = focused + 1;
                            }
                        }
                    }
                }
                if (newItem != oldItem) {
                    focusItemNum(newItem);
                    drawView();
                }
                oldItem = newItem;
                if ((event.getMouse().getEventFlags() & meDoubleClick) != 0) {
                    break;
                }
            }
            while (mouseEvent(event, evMouseMove | evMouseAuto));
            focusItemNum(newItem);
            drawView();
            if ((event.getMouse().getEventFlags() & meDoubleClick) != 0 && range > newItem) {
                selectItem(newItem);
            }
            clearEvent(event);
        }
        else if (event.getWhat() == evKeyDown) {
            int kc = JtvKey.ctrlToArrow(event.getKeyDown().getKeyStroke()) & 0xFFFF;
            if (kc == JtvKey.kbUp) {
            	newItem = focused - 1;
            }
            else if (kc == JtvKey.kbDown) {
            	newItem = focused + 1;
            }
            else if (kc == JtvKey.kbRight && numCols > 1) {
            	newItem = focused + size.getY();
            }
            else if (kc == JtvKey.kbLeft && numCols > 1) {
            	newItem = focused - size.getY();
            }
            else if (kc == JtvKey.kbPgDn) {
            	newItem = focused + size.getY() * numCols;
            }
            else if (kc == JtvKey.kbPgUp) {
            	newItem = focused - size.getY() * numCols;
            }
            else if (kc == JtvKey.kbHome) {
            	newItem = topItem;
            }
            else if (kc == JtvKey.kbEnd) {
            	newItem = topItem + (size.getY() * numCols) - 1;
            }
            else {
            	return;
            }

            focusItemNum(newItem);
            drawView();
            clearEvent(event);
        }
        else if (event.getWhat() == evBroadcast) {
            if ((options & ofSelectable) != 0) {
                if (event.getMessage().getCommand() == cmScrollBarClicked
                		&& (event.getMessage().getInfoPtr() == hScrollBar || event.getMessage().getInfoPtr() == vScrollBar)) {
                    select();
                }
                else if (event.getMessage().getCommand() == cmScrollBarChanged) {
                    if (vScrollBar == event.getMessage().getInfoPtr()) {
                        focusItemNum(vScrollBar.getValue());
                        drawView();
                    }
                    else if (hScrollBar == event.getMessage().getInfoPtr()) {
                        drawView();
                    }
                }
            }
        }
    }

    /**
     * Notifies the owner that the user has selected {@code item} by
     * broadcasting {@code cmListItemSelected} with this viewer as the info
     * pointer.
     *
     * @param item the zero-based index of the selected item
     */
    public void selectItem(int item) {
        message(owner, evBroadcast, cmListItemSelected, this);
    }

    /**
     * Sets the total number of items and updates the vertical scroll bar
     * range. If the currently focused item falls outside the new range,
     * resets the focus to zero.
     *
     * @param aRange the new item count; pass {@code 0} for an empty list
     */
    public void setRange(int aRange) {
        range = aRange;
        if (focused >= aRange) {
            focused = 0;
        }
        if (vScrollBar != null) {
            vScrollBar.setParams(focused, 0, aRange - 1, vScrollBar.getPgStep(), vScrollBar.getArStep());
        }
        else {
            drawView();
        }
    }

    /**
     * Propagates the state change to the parent, then shows or hides the
     * scroll bars depending on whether the viewer is both active and visible.
     * Redraws the view when {@code sfSelected}, {@code sfActive}, or
     * {@code sfVisible} change.
     *
     * @param aState the state bits to modify
     * @param enable {@code true} to set; {@code false} to clear
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);
        if ((aState & (sfSelected | sfActive | sfVisible)) != 0) {
            if (hScrollBar != null) {
                if (hasState(sfActive) && hasState(sfVisible)) {
                    hScrollBar.show();
                }
                else {
                    hScrollBar.hide();
                }
            }
            if (vScrollBar != null) {
                if (hasState(sfActive) && hasState(sfVisible)) {
                    vScrollBar.show();
                }
                else {
                    vScrollBar.hide();
                }
            }
            drawView();
        }
    }

    /**
     * Clears the scroll bar references and calls the inherited
     * {@link JtvView#shutDown()}.
     */
    @Override
    public void shutDown() {
        hScrollBar = null;
        vScrollBar = null;
        super.shutDown();
    }

    /**
     * The index of the currently focused (keyboard-cursor) item.
     */
    public int getFocused() {
		return focused;
	}
}
