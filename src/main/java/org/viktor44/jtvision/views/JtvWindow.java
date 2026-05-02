/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.views;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmNext;
import static org.viktor44.jtvision.core.CommandCodes.cmPrev;
import static org.viktor44.jtvision.core.CommandCodes.cmResize;
import static org.viktor44.jtvision.core.CommandCodes.cmSelectWindowNum;
import static org.viktor44.jtvision.core.CommandCodes.cmZoom;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowAll;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowRel;
import static org.viktor44.jtvision.core.ViewFlags.ofPostProcess;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.ofTopSelect;
import static org.viktor44.jtvision.core.ViewFlags.sbHandleKeyboard;
import static org.viktor44.jtvision.core.ViewFlags.sbVertical;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;
import static org.viktor44.jtvision.core.ViewFlags.sfModal;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;
import static org.viktor44.jtvision.core.ViewFlags.sfShadow;
import static org.viktor44.jtvision.core.ViewFlags.wfClose;
import static org.viktor44.jtvision.core.ViewFlags.wfGrow;
import static org.viktor44.jtvision.core.ViewFlags.wfMove;
import static org.viktor44.jtvision.core.ViewFlags.wfZoom;
import static org.viktor44.jtvision.core.ViewFlags.wpBlueWindow;
import static org.viktor44.jtvision.core.ViewFlags.wpCyanWindow;
import static org.viktor44.jtvision.core.ViewFlags.wpGrayWindow;

import java.util.function.Function;

import org.viktor44.jtvision.core.JtvCommandSet;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;

import lombok.Getter;
import lombok.Setter;

/**
 * A bordered, titled window with optional close, zoom, and resize controls.
 * <p>
 * JtvWindow extends {@link JtvGroup} to provide a standard JT Vision window.
 * It automatically creates and inserts a {@link JtvFrame} that draws the
 * border, title bar, close button, zoom button, and resize corner icons.
 * 
 * <h3>Window flags</h3>
 * 
 * The {@link #flags} field is a bitmask of {@code wfXXXX} constants that
 * control which interactive features are enabled:
 * <ul>
 *   <li>{@code wfMove} — the window can be dragged by its title bar.</li>
 *   <li>{@code wfGrow} — the window can be resized from its bottom corners.</li>
 *   <li>{@code wfClose} — a close button is shown; clicking it or pressing
 *       Ctrl+F4 sends {@code cmClose}.</li>
 *   <li>{@code wfZoom} — a zoom button is shown; clicking it or pressing F5
 *       toggles the window between its normal and maximum size.</li>
 * </ul>
 * All four flags are set by default. When selected, the corresponding commands
 * ({@code cmResize}, {@code cmClose}, {@code cmZoom}, {@code cmNext},
 * {@code cmPrev}) are enabled in the global command set and disabled again
 * when the window loses focus.
 * 
 * <h3>Colour palettes</h3>
 * 
 * Three built-in palettes are available, selected by the {@link #paletteIndex}
 * field: {@code wpBlueWindow} (default), {@code wpCyanWindow}, and
 * {@code wpGrayWindow}.
 * 
 * <h3>Colour palette</h3>
 * 
 * Each window palette has 8 entries:
 * <ol>
 *   <li>Passive (inactive) frame colour.</li>
 *   <li>Active frame colour.</li>
 *   <li>Frame icons colour (close, zoom, resize).</li>
 *   <li>Scroll bar page area colour.</li>
 *   <li>Scroll bar arrow controls colour.</li>
 *   <li>Scroller normal text colour.</li>
 *   <li>Scroller selected text colour.</li>
 *   <li>Reserved.</li>
 * </ol>
 * 
 * <h3>Scroll bars</h3>
 * 
 * Use {@link #standardScrollBar(int)} to create a keyboard-driven scroll bar
 * attached to the window frame. Pass {@code sbVertical} or {@code sbHorizontal}
 * (optionally OR-ed with {@code sbHandleKeyboard}) to control orientation.
 * 
 * <h3>Zoom</h3>
 * 
 * Calling {@link #zoom()} toggles between the maximum size and the size
 * stored in {@link #zoomRect}. {@link #zoomRect} is updated whenever the
 * window is at a non-maximum size when zoom is activated.
 *
 * @see JtvGroup
 * @see JtvFrame
 */
public class JtvWindow extends JtvGroup {

    /**
     * Bitmask of {@code wfXXXX} window-feature flags ({@code wfMove}, {@code wfGrow}, {@code wfClose}, {@code wfZoom}).
     */
	@Getter
	@Setter
    protected int flags;

	/**
     * The bounding rectangle to restore when the window is un-zoomed.
     * Updated by {@link #zoom()} before maximising.
     */
	@Getter
	@Setter
    protected JtvRect zoomRect;

	/**
     * The window's sequence number displayed in the title bar (e.g. {@code :1}).
     * Use {@code wnNoNumber} to suppress the number display.
     */
	@Getter
	@Setter
    protected int number;

	/**
     * Selects the window colour palette: {@code wpBlueWindow}, {@code wpCyanWindow}, or {@code wpGrayWindow}.
     */
	@Getter
	@Setter
    protected int paletteIndex;

	/**
     * The text displayed in the window's title bar.
     * Accessed by the frame via {@link #getTitle(int)}.
     */
	@Getter
	@Setter
    protected String title;

	/**
     * The {@link JtvFrame} child view that draws this window's border, title, and control icons. 
     * Set to {@code null} during {@link #shutDown()}.
     */
    protected JtvFrame frame;

	/**
     * The minimum allowed window size: 16 columns × 6 rows.
     * Enforced by {@link #sizeLimits()}.
     */
    public static final JtvPoint minWinSize = new JtvPoint(16, 6);

    /**
     * Blue-window palette — 8 entries, used when {@link #paletteIndex} is
     * {@code wpBlueWindow}:
     * <ol>
     *   <li>{@code 0x08} — passive frame.</li>
     *   <li>{@code 0x09} — active frame.</li>
     *   <li>{@code 0x0A} — frame icons (close, zoom, resize).</li>
     *   <li>{@code 0x0B} — scroll bar page area.</li>
     *   <li>{@code 0x0C} — scroll bar arrow controls.</li>
     *   <li>{@code 0x0D} — scroller normal text.</li>
     *   <li>{@code 0x0E} — scroller selected text.</li>
     *   <li>{@code 0x0F} — reserved.</li>
     * </ol>
     */
    public static final JtvPalette cpBlueWindow = new JtvPalette(
    		new int[] {0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F}
    );

    /**
     * Cyan-window palette — 8 entries, used when {@link #paletteIndex} is
     * {@code wpCyanWindow}:
     * <ol>
     *   <li>{@code 0x10} — passive frame.</li>
     *   <li>{@code 0x11} — active frame.</li>
     *   <li>{@code 0x12} — frame icons (close, zoom, resize).</li>
     *   <li>{@code 0x13} — scroll bar page area.</li>
     *   <li>{@code 0x14} — scroll bar arrow controls.</li>
     *   <li>{@code 0x15} — scroller normal text.</li>
     *   <li>{@code 0x16} — scroller selected text.</li>
     *   <li>{@code 0x17} — reserved.</li>
     * </ol>
     */
    public static final JtvPalette cpCyanWindow = new JtvPalette(
    		new int[] {0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17}
    );

    /**
     * Gray-window palette — 8 entries, used when {@link #paletteIndex} is
     * {@code wpGrayWindow}:
     * <ol>
     *   <li>{@code 0x18} — passive frame.</li>
     *   <li>{@code 0x19} — active frame.</li>
     *   <li>{@code 0x1A} — frame icons (close, zoom, resize).</li>
     *   <li>{@code 0x1B} — scroll bar page area.</li>
     *   <li>{@code 0x1C} — scroll bar arrow controls.</li>
     *   <li>{@code 0x1D} — scroller normal text.</li>
     *   <li>{@code 0x1E} — scroller selected text.</li>
     *   <li>{@code 0x1F} — reserved.</li>
     * </ol>
     */
    public static final JtvPalette cpGrayWindow = new JtvPalette(
    		new int[] {0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F}
    );

    /**
     * Creates a window.
     * <p>
     * Initialises flags to {@code wfMove | wfGrow | wfClose | wfZoom},
     * palette to {@code wpBlueWindow}, and state includes {@code sfShadow}.
     * Options include {@code ofSelectable | ofTopSelect} (the window floats
     * to the front when selected) and grow-mode is {@code gfGrowAll | gfGrowRel}.
     *
     * @param bounds       the initial bounding rectangle
     * @param aTitle       the title bar text
     * @param aNumber      the window number (use {@code wnNoNumber} to omit)
     */
    public JtvWindow(JtvRect bounds, String aTitle, int aNumber) {
        super(bounds);
        flags = wfMove | wfGrow | wfClose | wfZoom;
        zoomRect = getBounds();
        number = aNumber;
        paletteIndex = wpBlueWindow;
        title = aTitle;

        state |= sfShadow;
        options |= ofSelectable | ofTopSelect;
        growMode = gfGrowAll | gfGrowRel;

        frame = initFrame(getExtent());
        if (frame != null) {
            insert(frame);
        }
    }

    /**
     * Validates and removes this window from its owner if
     * {@link #valid(int)} approves {@code cmClose}.
     * Sets {@link #frame} to {@code null} before removal.
     */
    public void close() {
        if (valid(cmClose)) {
            frame = null;
            if (owner != null) {
                owner.destroy(this);
            }
        }
    }

    /**
     * Clears {@link #frame} and calls the inherited {@link JtvGroup#shutDown()}.
     */
    @Override
    public void shutDown() {
        frame = null;
        super.shutDown();
    }

    /**
     * Returns the colour palette for the current {@link #paletteIndex} choice.
     * Defaults to {@link #cpBlueWindow} for any unrecognised palette value.
     *
     * @return the active window colour palette
     */
    @Override
    public JtvPalette getPalette() {
        switch (paletteIndex) {
            case wpBlueWindow: 
            	return cpBlueWindow;
            case wpCyanWindow: 
            	return cpCyanWindow;
            case wpGrayWindow: 
            	return cpGrayWindow;
        }
        return cpBlueWindow;
    }

    /**
     * Returns the title bar text, truncated to {@code maxSize} characters.
     * The frame uses this method when rendering the title.
     *
     * @param maxSize the maximum number of characters to return
     * @return the window title (currently ignores the maxSize limit)
     */
    public String getTitle(int maxSize) {
        return title;
    }

    /**
     * Handles window-specific commands and keyboard shortcuts:
     * <ul>
     *   <li>{@code cmResize} — starts an interactive drag/resize session
     *       when {@code wfMove} or {@code wfGrow} is set.</li>
     *   <li>{@code cmClose} — closes the window (non-modal) or sends
     *       {@code cmCancel} (modal).</li>
     *   <li>{@code cmZoom} — toggles the zoom state when {@code wfZoom} is set.</li>
     *   <li>Tab / Shift+Tab — moves focus forward or backward among children.</li>
     *   <li>{@code cmSelectWindowNum} broadcast — selects this window if its
     *       {@link #number} matches.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);

        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmResize:
                    if ((flags & (wfMove | wfGrow)) != 0) {
                        JtvRect limits = owner.getExtent();
                        dragView(event, dragMode | (flags & (wfMove | wfGrow)),
                                 limits, getMinimumSize(), getMaximumSize());
                        clearEvent(event);
                    }
                    break;
                case cmClose:
                    if ((flags & wfClose) != 0 && (event.getMessage().getInfoPtr() == null || event.getMessage().getInfoPtr() == this)) {
                        clearEvent(event);
                        if ((state & sfModal) == 0) {
                            close();
                        }
                        else {
                            event.setWhat(evCommand);
                            event.getMessage().setCommand(cmCancel);
                            putEvent(event);
                            clearEvent(event);
                        }
                    }
                    break;
                case cmZoom:
                    if ((flags & wfZoom) != 0 && (event.getMessage().getInfoPtr() == null || event.getMessage().getInfoPtr() == this)) {
                        zoom();
                        clearEvent(event);
                    }
                    break;
            }
        }
        else if (event.getWhat() == evKeyDown) {
            switch (event.getKeyDown().getKeyStroke()) {
                case JtvKey.kbTab:
                    focusNext(false);
                    clearEvent(event);
                    break;
                case JtvKey.kbShiftTab:
                    focusNext(true);
                    clearEvent(event);
                    break;
            }
        }
        else if (event.getWhat() == evBroadcast
        		&& event.getMessage().getCommand() == cmSelectWindowNum
        		&& event.getMessage().getInfoInt() == number
        		&& (options & ofSelectable) != 0) {
            select();
            clearEvent(event);
        }
    }

    /**
     * Constructs {@link JtvFrame} for the given bounds.
     *
     * @param r the frame's bounding rectangle (the window's extent)
     * @return a new {@link JtvFrame}
     */
    protected JtvFrame initFrame(JtvRect r) {
        return new JtvFrame(r);
    }

    /**
     * Propagates the {@code sfSelected} state change by toggling
     * {@code sfActive} on the window and its frame, and enabling or
     * disabling the window-management command set ({@code cmNext},
     * {@code cmPrev}, {@code cmResize}, {@code cmClose}, {@code cmZoom}).
     *
     * @param aState the state bits to modify
     * @param enable {@code true} to set; {@code false} to clear
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);
        if ((aState & sfSelected) != 0) {
            setState(sfActive, enable);
            if (frame != null) {
                frame.setState(sfActive, enable);
            }
            JtvCommandSet windowCommands = new JtvCommandSet();
            windowCommands.enableCmd(cmNext);
            windowCommands.enableCmd(cmPrev);
            if ((flags & (wfGrow | wfMove)) != 0) {
                windowCommands.enableCmd(cmResize);
            }
            if ((flags & wfClose) != 0) {
                windowCommands.enableCmd(cmClose);
            }
            if ((flags & wfZoom) != 0) {
                windowCommands.enableCmd(cmZoom);
            }
            if (enable) {
                enableCommands(windowCommands);
            }
            else {
                disableCommands(windowCommands);
            }
        }
    }

    /**
     * Creates and inserts a scroll bar aligned to one of the window's
     * interior edges.
     * <p>
     * For a vertical scroll bar the bar occupies the rightmost column;
     * for a horizontal scroll bar it occupies the bottom row. If
     * {@code sbHandleKeyboard} is included in {@code aOptions}, the
     * scroll bar receives keyboard events via {@code ofPostProcess}.
     *
     * @param aOptions {@code sbVertical} or {@code sbHorizontal}, optionally
     *                 OR-ed with {@code sbHandleKeyboard}
     * @return the newly created and inserted {@link JtvScrollBar}
     */
    public JtvScrollBar standardScrollBar(int aOptions) {
        JtvRect r = getExtent();
        if ((aOptions & sbVertical) != 0) {
            r = new JtvRect(r.getB().getX() - 1, r.getA().getY() + 1, r.getB().getX(), r.getB().getY() - 1);
        }
        else {
            r = new JtvRect(r.getA().getX() + 2, r.getB().getY() - 1, r.getB().getX() - 2, r.getB().getY());
        }

        JtvScrollBar s = new JtvScrollBar(r);
        insert(s);
        if ((aOptions & sbHandleKeyboard) != 0) {
            s.options |= ofPostProcess;
        }
        return s;
    }

    /**
     * Enforces the minimum window size of {@link #minWinSize}.
     */
    @Override
    public JtvPoint getMinimumSize() {
        return new JtvPoint(minWinSize);
    }

    /**
     * Toggles between the maximum size and the pre-zoom size stored in
     * {@link #zoomRect}.
     * <p>
     * If the window is not already at maximum size, {@link #zoomRect} is
     * updated with the current bounds before the window is expanded to
     * the maximum. If the window is already at maximum size,
     * {@link #zoomRect} is restored.
     */
    public void zoom() {
        JtvPoint max = getMaximumSize();
        if (!size.equals(max)) {
            zoomRect = getBounds();
            JtvRect r = new JtvRect(0, 0, max.getX(), max.getY());
            locate(r);
        }
        else {
            locate(zoomRect);
        }
    }
}
