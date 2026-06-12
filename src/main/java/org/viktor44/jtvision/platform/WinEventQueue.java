/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.EventCodes.evMouseUp;
import static org.viktor44.jtvision.core.EventCodes.evMouseWheel;
import static org.viktor44.jtvision.core.EventCodes.mbLeftButton;
import static org.viktor44.jtvision.core.EventCodes.mbMiddleButton;
import static org.viktor44.jtvision.core.EventCodes.mbRightButton;
import static org.viktor44.jtvision.core.EventCodes.meDoubleClick;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.internal.Kernel32.INPUT_RECORD;
import org.fusesource.jansi.internal.Kernel32.KEY_EVENT_RECORD;
import org.fusesource.jansi.internal.Kernel32.MOUSE_EVENT_RECORD;
import org.viktor44.jtvision.core.EventCodes;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.MouseEvent;

/**
 * Windows implementation of {@link EventQueue}.
 * <p>
 * A single daemon thread reads {@code INPUT_RECORD} structures via Jansi's
 * {@code Kernel32} binding and translates them into {@link JtvEvent} objects.
 * Raw mode is managed via {@code SetConsoleMode}.
 *
 * <h3>Console mode flags</h3>
 *
 * Raw mode clears {@code ENABLE_LINE_INPUT}, {@code ENABLE_ECHO_INPUT},
 * {@code ENABLE_PROCESSED_INPUT}, and {@code ENABLE_QUICK_EDIT_MODE};
 * sets {@code ENABLE_MOUSE_INPUT}, {@code ENABLE_EXTENDED_FLAGS}, and
 * {@code ENABLE_WINDOW_INPUT}.
 */
class WinEventQueue extends EventQueue {

    // ------------------------------------------------------------------
    // Windows console mode flags (WinBase.h)
    // ------------------------------------------------------------------

    private static final int WIN_ENABLE_PROCESSED_INPUT       = 0x0001;
    private static final int WIN_ENABLE_LINE_INPUT             = 0x0002;
    private static final int WIN_ENABLE_ECHO_INPUT             = 0x0004;
    private static final int WIN_ENABLE_WINDOW_INPUT           = 0x0008;
    private static final int WIN_ENABLE_MOUSE_INPUT            = 0x0010;
    private static final int WIN_ENABLE_QUICK_EDIT_MODE        = 0x0040;
    private static final int WIN_ENABLE_EXTENDED_FLAGS         = 0x0080;
    private static final int WIN_ENABLE_VIRTUAL_TERMINAL_INPUT = 0x0200;

    /** Console input mode saved before raw mode is enabled. */
    private int savedWindowsInputMode;

    /** {@code true} if {@link #savedWindowsInputMode} holds a valid saved value. */
    private boolean windowsInputModeSaved = false;

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    @Override
    protected void init() {
        if (running) {
        	return;
        }
        enableRawTerminalMode();
        running = true;
        readerThread = new Thread(this::windowsEventLoop, "jvision-reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }

    /**
     * Saves the current console input mode and switches to raw mode via
     * {@code SetConsoleMode}.
     */
    @Override
    protected void enableRawTerminalMode() {
        if (rawTerminalEnabled) {
        	return;
        }
        if (enableWindowsRawInput()) {
            rawTerminalEnabled = true;
        }
    }

    /**
     * Restores the console input mode saved by {@link #enableRawTerminalMode()}.
     */
    @Override
    protected void disableRawTerminalMode() {
        if (!rawTerminalEnabled) {
        	return;
        }
        if (restoreWindowsInputMode()) {
            rawTerminalEnabled = false;
        }
    }

    private boolean enableWindowsRawInput() {
        try {
            long handle = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
            int[] mode = new int[1];
            if (Kernel32.GetConsoleMode(handle, mode) == 0) {
            	return false;
            }
            savedWindowsInputMode = mode[0];
            windowsInputModeSaved = true;
            int newMode = mode[0];
            newMode &= ~(WIN_ENABLE_LINE_INPUT | WIN_ENABLE_ECHO_INPUT
            				| WIN_ENABLE_PROCESSED_INPUT | WIN_ENABLE_QUICK_EDIT_MODE
            				| WIN_ENABLE_VIRTUAL_TERMINAL_INPUT);
            newMode |= WIN_ENABLE_MOUSE_INPUT | WIN_ENABLE_EXTENDED_FLAGS | WIN_ENABLE_WINDOW_INPUT;
            return Kernel32.SetConsoleMode(handle, newMode) != 0;
        } catch (LinkageError ignored) {
            return false;
        }
    }

    private boolean restoreWindowsInputMode() {
        if (!windowsInputModeSaved) {
        	return true;
        }
        try {
            long handle = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
            int result = Kernel32.SetConsoleMode(handle, savedWindowsInputMode);
            windowsInputModeSaved = false;
            return result != 0;
        }
        catch (LinkageError ignored) {
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Windows event loop
    // ------------------------------------------------------------------

    /**
     * Windows event-loop thread body. Waits on the console input handle via
     * {@code WaitForSingleObject} (50 ms timeout) and reads available
     * {@code INPUT_RECORD} structures. Dispatches each record to
     * {@link #handleWindowsKeyEvent} or {@link #handleWindowsMouseEvent}.
     */
    private void windowsEventLoop() {
        try {
            long handle = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
            int[] count = new int[1];
            while (running) {
                int wait = Kernel32.WaitForSingleObject(handle, 50);
                if (wait != 0) {
                	continue;
                }
                if (Kernel32.GetNumberOfConsoleInputEvents(handle, count) == 0 || count[0] == 0) {
                	continue;
                }
                INPUT_RECORD[] records = Kernel32.readConsoleInputHelper(handle, count[0], false);
                for (INPUT_RECORD rec : records) {
                    if (rec.eventType == INPUT_RECORD.KEY_EVENT) {
                        handleWindowsKeyEvent(rec.keyEvent);
                    }
                    else if (rec.eventType == INPUT_RECORD.MOUSE_EVENT) {
                        handleWindowsMouseEvent(rec.mouseEvent);
                    }
                }
            }
        }
        catch (IOException | LinkageError ignored) {
        }
    }

    /**
     * Translates a Windows {@code KEY_EVENT_RECORD} into a JT Vision
     * {@code evKeyDown} event. Key-up records are silently discarded.
     * <p>
     * AltGr (LeftCtrl + RightAlt) is treated as a regular character modifier
     * and not as separate Alt/Ctrl presses.
     *
     * @param ev the Windows key event record
     */
    void handleWindowsKeyEvent(KEY_EVENT_RECORD ev) {
        if (!ev.keyDown) {
        	return;
        }
        int vk = ev.keyCode & 0xFFFF;
        char uc = ev.uchar;
        int winCs = ev.controlKeyState;
        boolean shift     = (winCs & KEY_EVENT_RECORD.SHIFT_PRESSED)      != 0;
        boolean leftCtrl  = (winCs & KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)  != 0;
        boolean rightCtrl = (winCs & KEY_EVENT_RECORD.RIGHT_CTRL_PRESSED) != 0;
        boolean leftAlt   = (winCs & KEY_EVENT_RECORD.LEFT_ALT_PRESSED)   != 0;
        boolean rightAlt  = (winCs & KEY_EVENT_RECORD.RIGHT_ALT_PRESSED)  != 0;
        boolean altGr = leftCtrl && rightAlt;
        boolean ctrl  = (leftCtrl || rightCtrl) && !altGr;
        boolean alt   = (leftAlt  || rightAlt)  && !altGr;
        int modifiers = 0;
        if (shift) {
        	modifiers |= InputEvent.SHIFT_DOWN_MASK;
        }
        if (ctrl) {
        	modifiers |= InputEvent.CTRL_DOWN_MASK;
        }
        if (alt) {
        	modifiers |= InputEvent.ALT_DOWN_MASK;
        }

        int special = winSpecialVkToAwtVk(vk);
        if (special != 0) {
            pushKeyEvent(special, modifiers, KeyEvent.CHAR_UNDEFINED);
            return;
        }
        if (alt && ((vk >= 'A' && vk <= 'Z') || (vk >= '0' && vk <= '9'))) {
            pushKeyEvent(vk, modifiers, KeyEvent.CHAR_UNDEFINED);
            return;
        }
        if (ctrl && uc > 0 && uc < 32) {
            int letterVk = (KeyEvent.VK_A - 1) + uc;
            pushKeyEvent(letterVk, modifiers, uc);
            return;
        }
        if (uc >= 32 && uc != 127) {
            int charVk = (uc < 128) ? Character.toUpperCase((int) uc) : 0;
            pushKeyEvent(charVk, modifiers, uc);
        }
    }

    /**
     * Maps a Windows virtual-key code to the corresponding AWT {@code VK_*}
     * constant for navigation, function, and other named special keys.
     * Returns {@code 0} for printable-character keys.
     *
     * @param winVk the Windows virtual-key code
     * @return the AWT {@code VK_*} constant, or {@code 0} if unmapped
     */
    private int winSpecialVkToAwtVk(int winVk) {
        switch (winVk) {
            case 0x08: return KeyEvent.VK_BACK_SPACE;
            case 0x09: return KeyEvent.VK_TAB;
            case 0x0D: return KeyEvent.VK_ENTER;
            case 0x1B: return KeyEvent.VK_ESCAPE;
            case 0x21: return KeyEvent.VK_PAGE_UP;
            case 0x22: return KeyEvent.VK_PAGE_DOWN;
            case 0x23: return KeyEvent.VK_END;
            case 0x24: return KeyEvent.VK_HOME;
            case 0x25: return KeyEvent.VK_LEFT;
            case 0x26: return KeyEvent.VK_UP;
            case 0x27: return KeyEvent.VK_RIGHT;
            case 0x28: return KeyEvent.VK_DOWN;
            case 0x2D: return KeyEvent.VK_INSERT;
            case 0x2E: return KeyEvent.VK_DELETE;
            case 0x70: return KeyEvent.VK_F1;
            case 0x71: return KeyEvent.VK_F2;
            case 0x72: return KeyEvent.VK_F3;
            case 0x73: return KeyEvent.VK_F4;
            case 0x74: return KeyEvent.VK_F5;
            case 0x75: return KeyEvent.VK_F6;
            case 0x76: return KeyEvent.VK_F7;
            case 0x77: return KeyEvent.VK_F8;
            case 0x78: return KeyEvent.VK_F9;
            case 0x79: return KeyEvent.VK_F10;
            default:   return 0;
        }
    }

    /**
     * Translates a Windows {@code MOUSE_EVENT_RECORD} into a JT Vision mouse
     * event. Classifies as wheel, move, down, or up based on flags and button
     * state transition relative to {@link #lastMouse}.
     *
     * @param ev the Windows mouse event record
     */
    private void handleWindowsMouseEvent(MOUSE_EVENT_RECORD ev) {
        int btnState = ev.buttonState;
        int buttons = 0;
        if ((btnState & MOUSE_EVENT_RECORD.FROM_LEFT_1ST_BUTTON_PRESSED) != 0) {
        	buttons |= mbLeftButton;
        }
        if ((btnState & MOUSE_EVENT_RECORD.RIGHTMOST_BUTTON_PRESSED) != 0) {
        	buttons |= mbRightButton;
        }
        if ((btnState & MOUSE_EVENT_RECORD.FROM_LEFT_2ND_BUTTON_PRESSED) != 0) {
        	buttons |= mbMiddleButton;
        }

        boolean wheeled  = (ev.eventFlags & MOUSE_EVENT_RECORD.MOUSE_WHEELED) != 0;
        boolean moved    = (ev.eventFlags & MOUSE_EVENT_RECORD.MOUSE_MOVED)   != 0;
        boolean dblClick = (ev.eventFlags & MOUSE_EVENT_RECORD.DOUBLE_CLICK)  != 0;

        JtvPoint where = new JtvPoint(ev.mousePosition.x, ev.mousePosition.y);
        int eventWhat;
        MouseEvent mouse;

        if (wheeled) {
            short delta = (short) ((btnState >> 16) & 0xFFFF);
            eventWhat = evMouseWheel;
            mouse = new MouseEvent(where, 0, 0, 0, (delta > 0) ? EventCodes.mwUp : EventCodes.mwDown);
        }
        else if (moved) {
            eventWhat = evMouseMove;
            mouse = new MouseEvent(where, 0, 0, buttons, 0);
        }
        else if (buttons != 0 && lastMouse.getButtons() == 0) {
            eventWhat = evMouseDown;
            mouse = new MouseEvent(where, dblClick ? meDoubleClick : 0, 0, buttons, 0);
        }
        else if (buttons == 0 && lastMouse.getButtons() != 0) {
            eventWhat = evMouseUp;
            mouse = new MouseEvent(where, 0, 0, 0, 0);
        }
        else {
            return;
        }

        JtvEvent event = new JtvEvent();
        event.setWhat(eventWhat);
        event.setMouse(mouse);
        lastMouse = new MouseEvent(where, 0, 0, buttons, 0);
        offerEvent(event);
    }
}
