/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.views;

import static org.viktor44.jtvision.core.CommandCodes.cmScrollBarChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmScrollBarClicked;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseAuto;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.EventCodes.evMouseWheel;
import static org.viktor44.jtvision.core.EventCodes.mwDown;
import static org.viktor44.jtvision.core.EventCodes.mwLeft;
import static org.viktor44.jtvision.core.EventCodes.mwRight;
import static org.viktor44.jtvision.core.EventCodes.mwUp;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowLoX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowLoY;
import static org.viktor44.jtvision.core.ViewFlags.sbDownArrow;
import static org.viktor44.jtvision.core.ViewFlags.sbIndicator;
import static org.viktor44.jtvision.core.ViewFlags.sbLeftArrow;
import static org.viktor44.jtvision.core.ViewFlags.sbPageDown;
import static org.viktor44.jtvision.core.ViewFlags.sbPageLeft;
import static org.viktor44.jtvision.core.ViewFlags.sbPageRight;
import static org.viktor44.jtvision.core.ViewFlags.sbPageUp;
import static org.viktor44.jtvision.core.ViewFlags.sbRightArrow;
import static org.viktor44.jtvision.core.ViewFlags.sbUpArrow;
import static org.viktor44.jtvision.core.ViewFlags.sfVisible;

import java.awt.event.KeyEvent;

import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;

import lombok.Getter;

/**
 * <p>
 * A horizontal or vertical scroll bar control.
 * <p>
 * JtvScrollBar provides a standard scrolling control that can be oriented
 * either horizontally (width &gt; 1) or vertically (width == 1). The
 * orientation is determined automatically from the bounding rectangle
 * supplied at construction time.
 * 
 * <h3>Visual layout</h3>
 * 
 * The scroll bar is rendered as:
 * <pre>
 *   [◄][░░░░░█░░░░][►]   (horizontal)
 *   [▲]                   (vertical)
 *   [░]
 *   [█]  ← indicator
 *   [░]
 *   [▼]
 * </pre>
 * When the range is zero (min == max) a neutral fill character is used
 * in place of the track and no indicator is shown.
 * 
 * <h3>Interaction</h3>
 * 
 * <ul>
 *   <li>Clicking an arrow button steps by {@link #arStep}.</li>
 *   <li>Clicking in the track (page area) steps by {@link #pgStep}.</li>
 *   <li>Dragging the indicator sets the value proportionally.</li>
 *   <li>Keyboard arrow keys step by {@link #arStep}; PgUp/PgDn step by
 *       {@link #pgStep}; Home/End jump to the minimum/maximum.</li>
 *   <li>Mouse wheel scrolls vertically (up/down) or horizontally
 *       (left/right) by 3 × {@link #arStep}.</li>
 * </ul>
 * 
 * <h3>Broadcasts</h3>
 * 
 * Whenever the value changes, {@link #scrollDraw()} broadcasts
 * {@code cmScrollBarChanged} to the owner. When any part of the scroll
 * bar is first clicked, {@code cmScrollBarClicked} is broadcast before
 * any value change.
 * 
 * <h3>Grow mode</h3>
 * 
 * Vertical bars grow to keep the left edge and right edge pinned and the
 * bottom edge pinned ({@code gfGrowLoX | gfGrowHiX | gfGrowHiY}).
 * Horizontal bars keep the top and bottom edges pinned and the right
 * edge pinned ({@code gfGrowLoY | gfGrowHiX | gfGrowHiY}).
 * 
 * <h3>Colour palette</h3>
 * 
 * The three-entry palette {@link #cpScrollBar} maps to:
 * <ol>
 *   <li>Track (page) area colour.</li>
 *   <li>Arrow button colour.</li>
 *   <li>Indicator colour.</li>
 * </ol>
 *
 * @see JtvScroller
 * @see JtvListViewer
 */
public class JtvScrollBar extends JtvView {

    /**
     * The current scroll position, clamped to {@code [minVal, maxVal]}.
     * Modified by {@link #setValue(int)} and {@link #setParams}.
     */
	@Getter
    private int value;

	/** The minimum allowed value. Set via {@link #setParams} or {@link #setRange}. */
    private int minVal;

    /** The maximum allowed value. Set via {@link #setParams} or {@link #setRange}. */
    private int maxVal;

    /**
     * The number of positions to scroll when the user clicks in the track (page area).
     * Set via {@link #setStep} or {@link #setParams}.
     */
	@Getter
    private int pgStep;

	/**
     * The number of positions to scroll when the user clicks an arrow button or presses an arrow key. 
     * Set via {@link #setStep} or {@link #setParams}.
     */
	@Getter
    private int arStep;

	/**
     * The five display characters for this scroll bar in order:
     * {@code [0]} decrease arrow, {@code [1]} increase arrow,
     * {@code [2]} track fill, {@code [3]} indicator, {@code [4]} neutral fill.
     * Copied from {@link #hChars} or {@link #vChars} at construction time.
     */
    private char[] chars = new char[5];

    /**
     * Display characters for a horizontal scroll bar:
     * {@code ◄} (decrease), {@code ►} (increase), {@code ░} (track),
     * {@code █} (indicator), {@code ▒} (neutral fill when range == 0).
     */
    private static final char[] hChars = {
        '◄', '►', '░', '█', '▒'
    };

    /**
     * Display characters for a vertical scroll bar:
     * {@code ▲} (decrease), {@code ▼} (increase), {@code ░} (track),
     * {@code █} (indicator), {@code ▒} (neutral fill when range == 0).
     */
    private static final char[] vChars = {
        '▲', '▼', '░', '█', '▒'
    };

    /**
     * Three-entry colour palette:
     * <ol>
     *   <li>{@code 4} — track area.</li>
     *   <li>{@code 5} — arrow buttons.</li>
     *   <li>{@code 5} — indicator.</li>
     * </ol>
     */
    private static final JtvPalette cpScrollBar = new JtvPalette(new int[] {4, 5, 5});

    /**
     * Constructs a scroll bar from the given bounds.
     * <p>
     * A width of exactly 1 produces a <em>vertical</em> scroll bar;
     * any other width produces a <em>horizontal</em> scroll bar.
     * Grow mode and display characters are selected accordingly.
     * Subscribes to {@code evMouseWheel} events.
     *
     * @param bounds the bounding rectangle — 1 column wide for vertical,
     *               1 row tall for horizontal
     */
    public JtvScrollBar(JtvRect bounds) {
        super(bounds);
        value = 0;
        minVal = 0;
        maxVal = 0;
        pgStep = 1;
        arStep = 1;

        if (size.getX() == 1) {
            growMode = gfGrowLoX | gfGrowHiX | gfGrowHiY;
            System.arraycopy(vChars, 0, chars, 0, 5);
        }
        else {
            growMode = gfGrowLoY | gfGrowHiX | gfGrowHiY;
            System.arraycopy(hChars, 0, chars, 0, 5);
        }
        eventMask |= evMouseWheel;
    }

    /**
     * Renders the scroll bar at the current position via {@link #drawPos(int)}.
     */
    @Override
    public void draw() {
        drawPos(getPos());
    }

    /**
     * Renders the scroll bar with the indicator at pixel position {@code pos}.
     * <p>
     * The bar is drawn as: decrease-arrow, track (with indicator at
     * {@code pos}), increase-arrow. When {@link #minVal} equals
     * {@link #maxVal}, the neutral fill character is used and no indicator
     * is drawn.
     *
     * @param pos the pixel position of the indicator (1-based within the track)
     */
    public void drawPos(int pos) {
        JtvDrawBuffer b = new JtvDrawBuffer();
        int s = scrollSize() - 1;
        b.moveChar(0, chars[0], getColor(2), 1);
        if (maxVal == minVal) {
            b.moveChar(1, chars[4], getColor(1), s - 1);
        }
        else {
            b.moveChar(1, chars[2], getColor(1), s - 1);
            b.moveChar(pos, chars[3], getColor(3), 1);
        }
        b.moveChar(s, chars[1], getColor(2), 1);
        writeBuf(0, 0, size.getX(), size.getY(), b);
    }

    /**
     * Returns the colour palette {@link #cpScrollBar}.
     *
     * @return the scroll bar's colour palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpScrollBar;
    }

    /**
     * Computes the pixel position of the indicator within the track,
     * proportional to the current value in the range {@code [minVal, maxVal]}.
     * Returns {@code 1} when the range is zero.
     *
     * @return the 1-based indicator position within the scroll track
     */
    public int getPos() {
        int r = maxVal - minVal;
        if (r == 0) {
            return 1;
        }
        return (int) (((long) (value - minVal) * (scrollSize() - 3) + (r >> 1)) / r) + 1;
    }

    /**
     * Returns the effective scroll bar length in cells, which is the
     * width (horizontal) or height (vertical), clamped to a minimum of 3.
     */
    private int scrollSize() {
        int s = (size.getX() == 1) ? size.getY() : size.getX();
        return Math.max(3, s);
    }

    /**
     * Handles mouse, keyboard, and wheel events for the scroll bar.
     * 
     * <ul>
     *   <li><b>Mouse wheel</b> — steps the value by 3 × {@link #arStep}
     *       in the appropriate direction and broadcasts
     *       {@code cmScrollBarClicked} then {@code cmScrollBarChanged}.</li>
     *   <li><b>Mouse down on arrow</b> — enters an auto-repeat loop stepping
     *       by the arrow step while the button is held.</li>
     *   <li><b>Mouse down on track</b> — allows dragging the indicator to
     *       set the value proportionally.</li>
     *   <li><b>Arrow key</b> — steps by {@link #arStep}.</li>
     *   <li><b>PgUp/PgDn</b> — steps by {@link #pgStep}.</li>
     *   <li><b>Home/End</b> — jumps to {@link #minVal} or {@link #maxVal}.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        int step = 0;

        switch (event.getWhat()) {
            case evMouseWheel:
                if ((state & sfVisible) != 0) {
                    if (size.getX() == 1) {
                        if (event.getMouse().getWheel() == mwUp) {
                        	step = -arStep;
                        }
                        else if (event.getMouse().getWheel() == mwDown) {
                        	step = arStep;
                        }
                    }
                    else {
                        if (event.getMouse().getWheel() == mwLeft) {
                        	step = -arStep;
                        }
                        else if (event.getMouse().getWheel() == mwRight) {
                        	step = arStep;
                        }
                    }
                }
                if (step != 0) {
                    message(owner, evBroadcast, cmScrollBarClicked, this);
                    setValue(value + 3 * step);
                    clearEvent(event);
                }
                break;

            case evMouseDown: {
                message(owner, evBroadcast, cmScrollBarClicked, this);
                JtvPoint mouse = makeLocal(event.getMouse().getWhere());
                JtvRect extent = getExtent();
                extent.grow(1, 1);
                int p = getPos();
                int s = scrollSize() - 1;
                int clickPart = getPartCode(mouse, extent, p, s);

                switch (clickPart) {
                    case sbLeftArrow:
                    case sbRightArrow:
                    case sbUpArrow:
                    case sbDownArrow:
                        do {
                            mouse = makeLocal(event.getMouse().getWhere());
                            if (getPartCode(mouse, extent, p, s) == clickPart) {
                                setValue(value + scrollStep(clickPart));
                            }
                        }
                        while (mouseEvent(event, evMouseAuto));
                        break;
                    default:
                        do {
                            mouse = makeLocal(event.getMouse().getWhere());
                            int i = (size.getX() == 1) ? mouse.getY() : mouse.getX();
                            i = Math.max(i, 1);
                            i = Math.min(i, s - 1);
                            p = i;
                            if (s > 2) {
                                setValue((int) (((long) (p - 1) * (maxVal - minVal) + ((s - 2) >> 1)) / (s - 2)) + minVal);
                            }
                            drawPos(p);
                        }
                        while (mouseEvent(event, evMouseMove));
                        break;
                }
                clearEvent(event);
                break;
            }

            case evKeyDown:
                if ((state & sfVisible) != 0) {
                    int clickPart = sbIndicator;
                    int i = value;
                    int kc = event.getKeyDown().getKeyStroke();
                    if (size.getY() == 1) {
                        if (kc == KeyEvent.VK_LEFT) {
                        	clickPart = sbLeftArrow;
                        }
                        else if (kc == KeyEvent.VK_RIGHT) {
                        	clickPart = sbRightArrow;
                        }
                        else if (kc == KeyEvent.VK_HOME) {
                        	i = minVal;
                        }
                        else if (kc == KeyEvent.VK_END) {
                        	i = maxVal;
                        }
                        else {
                        	return;
                        }
                    }
                    else {
                        if (kc == KeyEvent.VK_UP) {
                        	clickPart = sbUpArrow;
                        }
                        else if (kc == KeyEvent.VK_DOWN) {
                        	clickPart = sbDownArrow;
                        }
                        else if (kc == KeyEvent.VK_PAGE_UP) {
                        	clickPart = sbPageUp;
                        }
                        else if (kc == KeyEvent.VK_PAGE_DOWN) {
                        	clickPart = sbPageDown;
                        }
                        else {
                        	return;
                        }
                    }
                    message(owner, evBroadcast, cmScrollBarClicked, this);
                    if (clickPart != sbIndicator) {
                        i = value + scrollStep(clickPart);
                    }
                    setValue(i);
                    clearEvent(event);
                }
                break;
        }
    }

    /**
     * Determines which logical scroll-bar part was clicked based on the
     * mouse position relative to the indicator and arrow positions.
     *
     * @param mouse  the mouse position in view-local coordinates
     * @param extent the scroll bar extent (grown by 1 for hit-testing tolerance)
     * @param p      the current indicator pixel position
     * @param s      the scroll track size minus 1
     * @return one of {@code sbUpArrow}, {@code sbDownArrow},
     *         {@code sbLeftArrow}, {@code sbRightArrow},
     *         {@code sbPageUp}, {@code sbPageDown},
     *         {@code sbPageLeft}, {@code sbPageRight},
     *         {@code sbIndicator}, or {@code -1} if outside the bar
     */
    private int getPartCode(JtvPoint mouse, JtvRect extent, int p, int s) {
        int part = -1;
        if (extent.contains(mouse)) {
            int mark = (size.getX() == 1) ? mouse.getY() : mouse.getX();
            if (mark == p) {
                part = sbIndicator;
            }
            else if (mark < 1) {
                part = sbLeftArrow;
            }
            else if (mark < p) {
                part = sbPageLeft;
            }
            else if (mark < s) {
                part = sbPageRight;
            }
            else {
                part = sbRightArrow;
            }
            if (size.getX() == 1) {
                part += 4;
            }
        }
        return part;
    }

    /**
     * Broadcasts {@code cmScrollBarChanged} to the owner group, notifying
     * associated scrollers or list viewers that the value has changed.
     */
    public void scrollDraw() {
        message(owner, evBroadcast, cmScrollBarChanged, this);
    }

    /**
     * Returns the signed scroll step for the given scroll-bar part code.
     * <p>
     * Parts with bit 1 clear use {@link #arStep}; parts with bit 1 set use
     * {@link #pgStep}. Parts with bit 0 clear produce a negative (decrease)
     * step; parts with bit 0 set produce a positive (increase) step.
     *
     * @param part the scroll-bar part code ({@code sbXXXX} constant)
     * @return the step amount (positive = increase, negative = decrease)
     */
    public int scrollStep(int part) {
        int s;
        if ((part & 2) == 0) {
            s = arStep;
        }
        else {
            s = pgStep;
        }
        if ((part & 1) == 0) {
            return -s;
        }
        return s;
    }

    /**
     * Sets all scroll bar parameters atomically.
     * <p>
     * Clamps {@code aValue} to {@code [aMin, aMax]} and ensures
     * {@code aMax >= aMin}. Redraws the bar if the displayed position
     * changed. Broadcasts {@code cmScrollBarChanged} if the value changed.
     *
     * @param aValue   the new current value
     * @param aMin     the new minimum value
     * @param aMax     the new maximum value (clamped to at least {@code aMin})
     * @param aPgStep  the new page step size
     * @param aArStep  the new arrow step size
     */
    public void setParams(int aValue, int aMin, int aMax, int aPgStep, int aArStep) {
        aMax = Math.max(aMax, aMin);
        aValue = Math.max(aMin, aValue);
        aValue = Math.min(aMax, aValue);
        int sValue = value;
        if (sValue != aValue || minVal != aMin || maxVal != aMax) {
            value = aValue;
            minVal = aMin;
            maxVal = aMax;
            drawView();
            if (sValue != aValue) {
                scrollDraw();
            }
        }
        pgStep = aPgStep;
        arStep = aArStep;
    }

    /**
     * Changes the minimum and maximum values without changing the current
     * value, page step, or arrow step.
     *
     * @param aMin the new minimum value
     * @param aMax the new maximum value
     */
    public void setRange(int aMin, int aMax) {
        setParams(value, aMin, aMax, pgStep, arStep);
    }

    /**
     * Changes the page step and arrow step sizes without affecting the
     * current value or range.
     *
     * @param aPgStep the new page step size
     * @param aArStep the new arrow step size
     */
    public void setStep(int aPgStep, int aArStep) {
        setParams(value, minVal, maxVal, aPgStep, aArStep);
    }

    /**
     * Sets the current scroll bar value, clamped to the existing
     * {@code [minVal, maxVal]} range. Redraws and broadcasts if the
     * value changed.
     *
     * @param aValue the new value
     */
    public void setValue(int aValue) {
        setParams(aValue, minVal, maxVal, pgStep, arStep);
    }
}
