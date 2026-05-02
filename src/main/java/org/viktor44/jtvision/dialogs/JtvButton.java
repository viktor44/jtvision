/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmCommandSetChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmDefault;
import static org.viktor44.jtvision.core.CommandCodes.cmGrabDefault;
import static org.viktor44.jtvision.core.CommandCodes.cmRecordHistory;
import static org.viktor44.jtvision.core.CommandCodes.cmReleaseDefault;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.ViewFlags.bfBroadcast;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.bfGrabFocus;
import static org.viktor44.jtvision.core.ViewFlags.bfLeftJust;
import static org.viktor44.jtvision.core.ViewFlags.ofFirstClick;
import static org.viktor44.jtvision.core.ViewFlags.ofPostProcess;
import static org.viktor44.jtvision.core.ViewFlags.ofPreProcess;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;
import static org.viktor44.jtvision.core.ViewFlags.sfDisabled;
import static org.viktor44.jtvision.core.ViewFlags.sfFocused;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;

import java.awt.event.InputEvent;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.platform.Screen;
import org.viktor44.jtvision.util.StringUtils;
import org.viktor44.jtvision.views.JtvView;

/**
 * A titled push-button view that generates a command event when pressed.
 * <p>
 * TButton is a terminal view most often found inside dialog boxes.
 * Users can activate a button by clicking it with the mouse, pressing its
 * highlighted shortcut letter (Alt+letter), pressing Space when the button has
 * focus, or pressing Enter when the button is the default button.
 * <p>
 * On color and black-and-white displays the button renders with a 3-D shadow;
 * on monochrome displays bracket markers are used instead.
 * <p>
 * There can be only one default button in a window at a time. Buttons negotiate
 * the default state by broadcasting {@code cmGrabDefault} and
 * {@code cmReleaseDefault}. Enabling or disabling the button's bound command
 * also enables or disables the button itself via {@code cmCommandSetChanged}.
 * 
 * <h3>Colour palette</h3>
 * 
 * The eight-entry palette {@link #cpButton} maps to:
 * <ol>
 *   <li>Normal button text.</li>
 *   <li>Default button text.</li>
 *   <li>Selected button text.</li>
 *   <li>Disabled button text.</li>
 *   <li>Shortcut key (normal state).</li>
 *   <li>Shortcut key (default state).</li>
 *   <li>Shortcut key (selected state).</li>
 *   <li>Button shadow.</li>
 * </ol>
 */
public class JtvButton extends JtvView {

    /**
     * Eight-entry colour palette:
     * <ol>
     *   <li>{@code 0x0A} — normal button text.</li>
     *   <li>{@code 0x0B} — default button text.</li>
     *   <li>{@code 0x0C} — selected button text.</li>
     *   <li>{@code 0x0D} — disabled button text.</li>
     *   <li>{@code 0x0E} — shortcut key (normal state).</li>
     *   <li>{@code 0x0E} — shortcut key (default state).</li>
     *   <li>{@code 0x0E} — shortcut key (selected state).</li>
     *   <li>{@code 0x0F} — button shadow.</li>
     * </ol>
     */
    private static final JtvPalette cpButton = new JtvPalette(
    		new int[] {0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0E, 0x0E, 0x0F}
    );

    /** Unicode block characters used to render the 3-D button shadow (lower-half, full, upper-half block). */
    private static final char[] shadows = {'▄', '█', '▀'};
    /** Bracket characters used in monochrome ({@code showMarkers}) mode to frame the button. */
    private static final char[] markers = {'[', ']'};
    /** Special edge characters used in monochrome mode to indicate selected, default, or normal state. */
    private static final char[] specialChars = {'»', ' ', '«', ' ', ' ', ' '};

    /** The text label of the button. Enclose a character in tildes to define a hot-key (e.g., {@code "~O~K"}). */
    private String title;

	/** The command code posted when the button is pressed. */
    private int command;

	/**
     * Bitmapped field controlling button behaviour and label alignment.
     * See {@code bfXXXX} flag constants ({@code bfDefault}, {@code bfLeftJust},
     * {@code bfBroadcast}, {@code bfGrabFocus}).
     */
    private int flags;

	/** {@code true} when this button is currently acting as the default button (pressed on Enter). */
    private boolean amDefault;

	/**
     * Creates a button with the given bounds, label, command, and flags.
     * <p>
     * If {@code aFlags} contains {@code bfDefault}, the button starts as the default.
     * If the bound command is currently disabled, the button is initialised in a disabled state.
     *
     * @param bounds   the bounding rectangle of the button
     * @param aTitle   the button label; enclose a character in tildes to make it a hot-key
     * @param aCommand the command generated when the button is pressed
     * @param aFlags   combination of {@code bfXXXX} flag constants
     */
    public JtvButton(JtvRect bounds, String aTitle, int aCommand, int aFlags) {
        super(bounds);
        title = aTitle;
        command = aCommand;
        flags = aFlags;
        amDefault = (aFlags & bfDefault) != 0;
        options |= ofSelectable | ofFirstClick | ofPreProcess | ofPostProcess;
        eventMask |= evBroadcast;
        if (!commandEnabled(aCommand)) {
            state |= sfDisabled;
        }
    }

    /**
     * Draws the button in its current (unpressed) state by delegating to {@link #drawState(boolean)}.
     */
    @Override
    public void draw() {
        drawState(false);
    }

    /**
     * Draws the button in the pressed or unpressed visual state.
     * <p>
     * Selects color attributes based on enabled/disabled, active, selected, and default states.
     * When {@code down} is {@code true} the button renders as pressed (shifted shadow).
     *
     * @param down {@code true} to draw the button as pressed, {@code false} for normal
     */
    public void drawState(boolean down) {
        JtvColorAttr cButton, cButtonHot, cShadow;
        JtvDrawBuffer drawBuffer = new JtvDrawBuffer();

        if ((state & sfDisabled) != 0) {
            cButton = getColor(4); cButtonHot = getColor(4);
        }
        else {
            cButton = getColor(1); cButtonHot = getColor(5);
            if ((state & sfActive) != 0) {
                if ((state & sfSelected) != 0) {
                    cButton = getColor(3); cButtonHot = getColor(7);
                }
                else if (amDefault) {
                    cButton = getColor(2); cButtonHot = getColor(6);
                }
            }
        }
        cShadow = getColor(8);
        int s = size.getX() - 1;
        int t = size.getY() / 2 - 1;
        char ch = ' ';

        for (int y = 0; y <= size.getY() - 2; y++) {
            int i;
            drawBuffer.moveChar(0, ' ', cButton, size.getX());
            drawBuffer.putAttribute(0, cShadow);
            if (down) {
                drawBuffer.putAttribute(1, cShadow);
                ch = ' ';
                i = 2;
            }
            else {
                drawBuffer.putAttribute(s, cShadow);
                if (showMarkers) {
                    ch = ' ';
                }
                else {
                    if (y == 0) {
                        drawBuffer.putChar(s, shadows[0]);
                    }
                    else {
                        drawBuffer.putChar(s, shadows[1]);
                    }
                    ch = shadows[2];
                }
                i = 1;
            }

            if (y == t && title != null) {
                drawTitle(drawBuffer, s, i, cButton, cButtonHot, down);
            }

            if (showMarkers && !down) {
                drawBuffer.putChar(1, markers[0]);
                drawBuffer.putChar(s - 1, markers[1]);
            }
            writeLine(0, y, size.getX(), 1, drawBuffer);
        }
        drawBuffer.moveChar(0, ' ', cShadow, 2);
        drawBuffer.moveChar(2, ch, cShadow, s - 1);
        writeLine(0, size.getY() - 1, size.getX(), 1, drawBuffer);
    }

    /**
     * Renders the button's title string into the draw buffer, centred or left-justified
     * according to the {@code bfLeftJust} flag. In monochrome mode, adds state indicator
     * characters at the button edges.
     */
    private void drawTitle(JtvDrawBuffer b, int s, int i, JtvColorAttr cButton, JtvColorAttr cButtonHot, boolean down) {
        int l;
        if ((flags & bfLeftJust) != 0) {
            l = 1;
        }
        else {
            l = (s - StringUtils.cstrLen(title) - 1) / 2;
            if (l < 1) {
            	l = 1;
            }
        }
        b.moveCStr(i + l, title, cButton, cButtonHot);

        if (showMarkers && !down) {
            int scOff;
            if ((state & sfSelected) != 0) {
                scOff = 0;
            }
            else if (amDefault) {
                scOff = 2;
            }
            else {
                scOff = 4;
            }
            b.putChar(0, specialChars[scOff]);
            b.putChar(s, specialChars[scOff + 1]);
        }
    }

    /**
     * Returns the default color palette {@code cpButton}.
     *
     * @return the button's color palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpButton;
    }

    /**
     * Handles mouse, keyboard, and broadcast events.
     * <p>
     * Responds to:
     * <ul>
     *   <li>Mouse clicks — tracks drag-over-button state, calls {@link #press()} on release.</li>
     *   <li>Alt+hotkey or Space (when focused) — calls {@link #animatePress()}.</li>
     *   <li>{@code cmDefault} broadcast — presses the button if it is the default.</li>
     *   <li>{@code cmGrabDefault}/{@code cmReleaseDefault} — updates default status.</li>
     *   <li>{@code cmCommandSetChanged} — enables/disables the button.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        JtvRect clickRect = getExtent();
        clickRect.setA(new JtvPoint(clickRect.getA().getX() + 1, clickRect.getA().getY()));
        clickRect.setB(new JtvPoint(clickRect.getB().getX() - 1, clickRect.getB().getY() - 1));

        if (event.getWhat() == evMouseDown) {
            JtvPoint mouse = makeLocal(event.getMouse().getWhere());
            if (!clickRect.contains(mouse)) {
                clearEvent(event);
            }
        }
        if ((flags & bfGrabFocus) != 0)
            super.handleEvent(event);

        char c = StringUtils.hotKey(title);

        switch (event.getWhat()) {
            case evMouseDown:
                if ((state & sfDisabled) == 0) {
                    clickRect.setB(new JtvPoint(clickRect.getB().getX() + 1, clickRect.getB().getY()));
                    boolean down = false;
                    do {
                        JtvPoint mouse = makeLocal(event.getMouse().getWhere());
                        if (down != clickRect.contains(mouse)) {
                            down = !down;
                            drawState(down);
                        }
                    }
                    while (mouseEvent(event, evMouseMove));
                    if (down) {
                        press();
                        drawState(false);
                    }
                }
                clearEvent(event);
                break;

            case evKeyDown:
                if (event.getKeyDown().getKeyCode() != 0
                		&&
                    ((event.getKeyDown().getModifiers() == InputEvent.ALT_DOWN_MASK
                      && Character.toUpperCase(c) == event.getKeyDown().getKeyCode()) ||
                     ((state & sfFocused) != 0 &&
                      event.getKeyDown().getKeyChar() == ' '))) {
                    animatePress();
                    clearEvent(event);
                }
                break;

            case evBroadcast:
                switch (event.getMessage().getCommand()) {
                    case cmDefault:
                        if (amDefault && (state & sfDisabled) == 0) {
                            animatePress();
                            clearEvent(event);
                        }
                        break;
                    case cmGrabDefault:
                    case cmReleaseDefault:
                        if ((flags & bfDefault) != 0) {
                            amDefault = event.getMessage().getCommand() == cmReleaseDefault;
                            drawView();
                        }
                        break;
                    case cmCommandSetChanged:
                        setState(sfDisabled, !commandEnabled(command));
                        drawView();
                        break;
                }
                break;
        }
    }

    /**
     * Makes this button the default (or releases it), unless the button already
     * owns the {@code bfDefault} flag (in which case it is permanently default).
     * Broadcasts {@code cmGrabDefault} or {@code cmReleaseDefault} to notify peers.
     *
     * @param enable {@code true} to grab the default, {@code false} to release it
     */
    public void makeDefault(boolean enable) {
        if ((flags & bfDefault) == 0) {
            message(owner, evBroadcast,
                    enable ? cmGrabDefault : cmReleaseDefault, this);
            amDefault = enable;
            drawView();
        }
    }

    /**
     * Extends the inherited {@code setState} to redraw when selected or activated,
     * and to call {@link #makeDefault(boolean)} when the button gains or loses focus.
     *
     * @param aState the state flag(s) being changed
     * @param enable {@code true} to set the flag, {@code false} to clear it
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);
        if ((aState & (sfSelected | sfActive)) != 0)
            drawView();
        if ((aState & sfFocused) != 0)
            makeDefault(enable);
    }

    /** Duration of the press animation in milliseconds. */
    private static final int animationDurationMs = 100;

    /**
     * Briefly renders the button as pressed, flushes the screen, waits
     * {@value #animationDurationMs} ms, then restores the normal state and calls
     * {@link #press()}.
     */
    private void animatePress() {
        drawState(true);
        Screen.flushScreen();
        try {
            Thread.sleep(animationDurationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        drawState(false);
        Screen.flushScreen();
        press();
    }

    /**
     * Generates the event associated with pressing this button.
     * <p>
     * First broadcasts {@code cmRecordHistory} so that linked {@link JtvHistory} views
     * record their input line contents. Then either broadcasts the button's command
     * (if {@code bfBroadcast} is set) or posts a {@code evCommand} event.
     */
    public void press() {
        message(owner, evBroadcast, cmRecordHistory, null);
        if ((flags & bfBroadcast) != 0) {
            message(owner, evBroadcast, command, this);
        } else {
            JtvEvent e = new JtvEvent();
            e.setWhat(evCommand);
            e.getMessage().setCommand(command);
            e.getMessage().setInfoPtr(this);
            putEvent(e);
        }
    }

    /** The text label of the button. Enclose a character in tildes to define a hot-key (e.g., {@code "~O~K"}). */
    public String getTitle() {
		return title;
	}

	/** The command code posted when the button is pressed. */
    public int getCommand() {
		return command;
	}

	/**
     * Bitmapped field controlling button behaviour and label alignment.
     * See {@code bfXXXX} flag constants ({@code bfDefault}, {@code bfLeftJust},
     * {@code bfBroadcast}, {@code bfGrabFocus}).
     */
    public int getFlags() {
		return flags;
	}

	/** {@code true} when this button is currently acting as the default button (pressed on Enter). */
    public boolean isAmDefault() {
		return amDefault;
	}
}
