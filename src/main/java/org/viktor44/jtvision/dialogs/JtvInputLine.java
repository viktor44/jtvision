package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmCopy;
import static org.viktor44.jtvision.core.CommandCodes.cmCut;
import static org.viktor44.jtvision.core.CommandCodes.cmPaste;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseAuto;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.EventCodes.meDoubleClick;
import static org.viktor44.jtvision.core.ViewFlags.ofFirstClick;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;
import static org.viktor44.jtvision.core.ViewFlags.sfCursorIns;
import static org.viktor44.jtvision.core.ViewFlags.sfCursorVis;
import static org.viktor44.jtvision.core.ViewFlags.sfFocused;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvProgram;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvView;

import lombok.Getter;
import lombok.Setter;

/**
 * A single-line text editor control.
 * <p>
 * TInputLine provides a basic editable string field. It handles cursor movement,
 * text insertion and deletion, text selection (including Shift+arrow), overwrite
 * mode (Ins), and scrolling when the text is longer than the visible area.
 * Left and right arrow indicators ({@code ◄} / {@code ►}) appear at the edges
 * when the text can be scrolled.
 * <p>
 * The maximum number of characters the field may hold is set at construction time
 * ({@code aMaxLen}) and must not be changed afterwards. Double-clicking selects
 * all text. When the field receives or loses selection, all text is selected or deselected.
 * <p>
 * History lists can be attached via a {@link JtvHistory} view placed beside the input line.
 * 
 * <h3>Colour palette</h3>
 * 
 * The four-entry palette {@link #cpInputLine} maps to:
 * <ol>
 *   <li>Unfocused field text.</li>
 *   <li>Focused field text.</li>
 *   <li>Selected text highlight.</li>
 *   <li>Scroll arrow colour.</li>
 * </ol>
 */
public class JtvInputLine extends JtvView {

    /** The current text content of the input field. */
	@Getter
	@Setter
    protected String data;

	/** Maximum number of characters the field may hold (set at construction; do not change). */
	@Getter
	@Setter
    protected int maxLen;

	/** Current cursor position (character index within {@link #data}). */
    protected int curPos;

    /** Index of the first visible character (non-zero when the text is scrolled). */
    protected int firstPos;

    /** Start of the current text selection (inclusive, character index). */
    protected int selStart;

    /** End of the current text selection (exclusive, character index). */
    protected int selEnd;

    /** The anchor point used when extending a selection with Shift+arrow keys. */
    protected int anchor;

    /** Arrow character shown on the left when the text can be scrolled left. */
    private static final char leftArrow = '◄';
    /** Arrow character shown on the right when the text can be scrolled right. */
    private static final char rightArrow = '►';

    /**
     * Four-entry colour palette:
     * <ol>
     *   <li>{@code 0x13} — unfocused field text.</li>
     *   <li>{@code 0x13} — focused field text.</li>
     *   <li>{@code 0x14} — selected text highlight.</li>
     *   <li>{@code 0x15} — scroll arrow colour.</li>
     * </ol>
     */
    private static final JtvPalette cpInputLine = new JtvPalette(new int[]{0x13, 0x13, 0x14, 0x15});

    /**
     * Creates an input line with the given bounds and maximum text length.
     *
     * @param bounds   the bounding rectangle (should be at least 3 characters wide to
     *                 accommodate scroll arrows and at least one character of text)
     * @param aMaxLen  the maximum number of characters the field may hold
     */
    public JtvInputLine(JtvRect bounds, int aMaxLen) {
        super(bounds);
        maxLen = Math.max(aMaxLen - 1, 0);
        data = "";
        curPos = 0;
        firstPos = 0;
        selStart = 0;
        selEnd = 0;
        anchor = 0;
        state |= sfCursorVis;
        options |= ofSelectable | ofFirstClick;
        eventMask |= evCommand;
    }

    /**
     * Draws the input field, rendering the visible portion of {@link #data},
     * selection highlight, and scroll-arrow indicators.
     */
    @Override
    public void draw() {
        JtvDrawBuffer b = new JtvDrawBuffer();
        JtvColorAttr color = getColor((state & sfFocused) != 0 ? 2 : 1);

        b.moveChar(0, ' ', color, size.getX());
        if (size.getX() > 1) {
            b.moveStr(1, data, color, size.getX() - 1, firstPos);
        }
        if (canScroll(1)) {
            b.moveChar(size.getX() - 1, rightArrow, getColor(4), 1);
        }
        if (canScroll(-1)) {
            b.moveChar(0, leftArrow, getColor(4), 1);
        }
        if ((state & sfSelected) != 0) {
            int l = Math.max(0, selStart - firstPos);
            int r = Math.min(size.getX() - 2, selEnd - firstPos);
            if (l < r) {
                b.moveChar(l + 1, (char) 0, getColor(3), r - l);
            }
        }
        writeLine(0, 0, size.getX(), size.getY(), b);
        setCursor(curPos - firstPos + 1, 0);
    }

    /**
     * Returns the default color palette {@code cpInputLine}.
     *
     * @return the input line's color palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpInputLine;
    }

    /**
     * Returns the data record size: {@code maxLen + 1} bytes (one byte per character plus NUL).
     *
     * @return data record size in bytes
     */
    @Override
    public int dataSize() {
        return maxLen + 1;
    }

    /**
     * Reads the field's current text. Callers should read the {@link #data} field directly
     * in this implementation.
     *
     * @param rec the destination record (unused)
     */
    @Override
    public void getDataTo(Object rec) {
        // Caller should read data field directly
    }

    /**
     * Sets the field's text from a {@link String} record, truncating to {@link #maxLen}
     * if necessary, then selects all text.
     *
     * @param rec a {@link String} with the new text value
     */
    @Override
    public void setDataFrom(Object rec) {
        if (rec instanceof String) {
            data = (String) rec;
            if (data.length() > maxLen) {
                data = data.substring(0, maxLen);
            }
        }
        selectAll(true);
    }

    /**
     * Returns whether the text can be scrolled in the given direction.
     *
     * @param delta negative to check left-scroll, positive to check right-scroll
     * @return {@code true} if scrolling in the given direction is possible
     */
    public boolean canScroll(int delta) {
        if (delta < 0) {
            return firstPos > 0;
        }
        if (delta > 0) {
            return data.length() - firstPos + 2 > size.getX();
        }
        return false;
    }

    /**
     * Handles mouse and keyboard events for the input field.
     * <p>
     * Supported operations:
     * <ul>
     *   <li>Mouse clicks — move cursor; double-click selects all text.</li>
     *   <li>Shift+arrow keys — extend text selection.</li>
     *   <li>Arrow keys, Home, End — move cursor.</li>
     *   <li>Backspace, Delete — delete character or selection.</li>
     *   <li>Ins — toggle insert/overwrite mode.</li>
     *   <li>Printable characters — insert or overwrite text.</li>
     *   <li>Ctrl+Y — clear the entire field.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if ((state & sfSelected) == 0) {
        	return;
        }

        switch (event.getWhat()) {
            case evMouseDown: {
                int delta = mouseDelta(event);
                if (canScroll(delta)) {
                    do {
                        if (canScroll(delta)) {
                            firstPos += delta;
                            drawView();
                        }
                    }
                    while (mouseEvent(event, evMouseAuto));
                }
                else if ((event.getMouse().getEventFlags() & meDoubleClick) != 0) {
                    selectAll(true);
                }
                else {
                    anchor = mousePos(event);
                    do {
                        curPos = mousePos(event);
                        adjustSelectBlock();
                        if (firstPos > curPos) {
                            firstPos = curPos;
                        }
                        int minFirst = curPos - size.getX() + 2;
                        if (firstPos < minFirst) {
                            firstPos = minFirst;
                        }
                        if (firstPos < 0) {
                            firstPos = 0;
                        }
                        drawView();
                    }
                    while (mouseEvent(event, evMouseMove | evMouseAuto));
                }
                clearEvent(event);
                break;
            }
            case evCommand: {
                switch (event.getMessage().getCommand()) {
                    case cmCopy: clipCopy(); break;
                    case cmCut: clipCut(); break;
                    case cmPaste: clipPaste(); break;
                    default: return;
                }
                drawView();
                clearEvent(event);
                break;
            }
            case evKeyDown: {
                int fullStroke = JtvKey.ctrlToArrow(event.getKeyDown().getKeyStroke());
                int kc = fullStroke & 0xFFFF;
                boolean extendBlock = false;

                if (fullStroke == JtvKey.kbShiftDel) {
                    clipCut();
                } else if (fullStroke == JtvKey.kbCtrlIns) {
                    clipCopy();
                } else if (fullStroke == JtvKey.kbShiftIns) {
                    clipPaste();
                } else {
                    // Check for shift+cursor keys
                    if ((event.getKeyDown().getModifiers() & JtvKey.kbShiftMask) != 0
                    		&& (kc == JtvKey.kbLeft || kc == JtvKey.kbRight || kc == JtvKey.kbHome || kc == JtvKey.kbEnd)) {
                        if (curPos == selEnd) {
                            anchor = selStart;
                        }
                        else if (selStart == selEnd) {
                            anchor = curPos;
                        }
                        else {
                            anchor = selEnd;
                        }
                        extendBlock = true;
                    }

                    switch (kc) {
                        case JtvKey.kbLeft:
                            if (curPos > 0) curPos--;
                            break;
                        case JtvKey.kbRight:
                            if (curPos < data.length()) curPos++;
                            break;
                        case JtvKey.kbHome:
                            curPos = 0;
                            break;
                        case JtvKey.kbEnd:
                            curPos = data.length();
                            break;
                        case JtvKey.kbBack:
                            if (selStart == selEnd) {
                                if (curPos > 0) {
                                    selStart = curPos - 1;
                                    selEnd = curPos;
                                }
                            }
                            deleteSelect();
                            break;
                        case JtvKey.kbDel:
                            if (selStart == selEnd) {
                                if (curPos < data.length()) {
                                    selStart = curPos;
                                    selEnd = curPos + 1;
                                }
                            }
                            deleteSelect();
                            break;
                        case JtvKey.kbIns:
                            setState(sfCursorIns, (state & sfCursorIns) == 0);
                            break;
                        default:
                            char keyChar = event.getKeyDown().getKeyChar();
                            if (keyChar != KeyEvent.CHAR_UNDEFINED && keyChar >= 32) {
                                deleteSelect();
                                if ((state & sfCursorIns) != 0 && curPos < data.length()) {
                                    // Overwrite mode
                                    data = data.substring(0, curPos) + data.substring(curPos + 1);
                                }
                                if (data.length() + 1 <= maxLen) {
                                    if (firstPos > curPos) {
                                        firstPos = curPos;
                                    }
                                    data = data.substring(0, curPos) + keyChar + data.substring(curPos);
                                    curPos++;
                                }
                            }
                            else {
                                // Ctrl+Y - clear line
                                if (event.getKeyDown().getKeyChar() == 25) {
                                    data = "";
                                    curPos = 0;
                                }
                                else {
                                    return; // Not handled
                                }
                            }
                    }

                    if (extendBlock) {
                        adjustSelectBlock();
                    }
                    else {
                        selStart = selEnd = 0;
                    }

                    if (firstPos > curPos) {
                        firstPos = curPos;
                    }
                    int i = curPos - size.getX() + 2;
                    if (firstPos < i) {
                        firstPos = i;
                    }
                }

                drawView();
                clearEvent(event);
                break;
            }
        }
    }

    /** Copies the selected text to the clipboard. */
    private void clipCopy() {
        if (selStart < selEnd) {
            JtvProgram.getClipboard().setContents(new StringSelection(data.substring(selStart, selEnd)), null);
        }
    }

    /** Copies the selected text to the clipboard then deletes it. */
    private void clipCut() {
        if (selStart < selEnd) {
            clipCopy();
            deleteSelect();
        }
    }

    /** Inserts the clipboard text at the cursor position, replacing any current selection. */
    private void clipPaste() {
        try {
            Transferable t = JtvProgram.getClipboard().getContents(null);
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                deleteSelect();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (c >= 32) {
                    	sb.append(c);
                    }
                }
                String filtered = sb.toString();
                int available = maxLen - data.length();
                if (available > 0) {
                    String insert = filtered.length() > available ? filtered.substring(0, available) : filtered;
                    data = data.substring(0, curPos) + insert + data.substring(curPos);
                    curPos += insert.length();
                }
                selStart = selEnd = 0;
                if (firstPos > curPos) {
                    firstPos = curPos;
                }
                int minFirst = curPos - size.getX() + 2;
                if (firstPos < minFirst) {
                    firstPos = minFirst;
                }
                if (firstPos < 0) {
                    firstPos = 0;
                }
            }
        }
        catch (Exception e) {
            // ignore
        }
    }

    /** Deletes the currently selected text range and moves the cursor to {@link #selStart}. */
    private void deleteSelect() {
        if (selStart < selEnd) {
            data = data.substring(0, selStart) + data.substring(selEnd);
            curPos = selStart;
            selStart = selEnd = 0;
        }
    }

    /** Updates {@link #selStart} and {@link #selEnd} based on {@link #curPos} and {@link #anchor}. */
    private void adjustSelectBlock() {
        if (curPos < anchor) {
            selStart = curPos;
            selEnd = anchor;
        }
        else {
            selStart = anchor;
            selEnd = curPos;
        }
    }

    /**
     * Selects all text (positions cursor at end, sets selection to the whole string) or
     * clears the selection (positions cursor and selection start at 0).
     *
     * @param enable {@code true} to select all, {@code false} to deselect all
     */
    public void selectAll(boolean enable) {
        selStart = 0;
        if (enable) {
            curPos = selEnd = data.length();
        }
        else {
            curPos = selEnd = 0;
        }
        firstPos = Math.max(0, curPos - size.getX() + 2);
        drawView();
    }

    /**
     * Returns the scroll direction implied by the current mouse position:
     * {@code -1} if the mouse is at or left of the left edge, {@code 1} if at or right
     * of the right edge, {@code 0} otherwise.
     */
    private int mouseDelta(JtvEvent event) {
        JtvPoint mouse = makeLocal(event.getMouse().getWhere());
        if (mouse.getX() <= 0) {
        	return -1;
        }
        if (mouse.getX() >= size.getX() - 1) {
        	return 1;
        }
        return 0;
    }

    /**
     * Converts a mouse event position into a character index within {@link #data}.
     */
    private int mousePos(JtvEvent event) {
        JtvPoint mouse = makeLocal(event.getMouse().getWhere());
        int pos = Math.max(mouse.getX(), 1) + firstPos - 1;
        return Math.max(0, Math.min(pos, data.length()));
    }

    /**
     * Extends the inherited {@code setState} to select or deselect all text when
     * the field is selected or activated.
     *
     * @param aState the state flag(s) being changed
     * @param enable {@code true} to set, {@code false} to clear
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);
        if (aState == sfSelected || (aState == sfActive && (state & sfSelected) != 0)) {
            selectAll(enable);
        }
    }

    /**
     * Always returns {@code true} — input lines are always valid regardless of command.
     *
     * @param cmd the command to validate
     * @return {@code true}
     */
    @Override
    public boolean valid(int cmd) {
        return true;
    }
}
