/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evNothing;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.fusesource.jansi.internal.Kernel32.KEY_EVENT_RECORD;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKeyStroke;
import org.viktor44.jtvision.core.KeyDownEvent;

/**
 * Verifies Windows console KEY_EVENT_RECORD translation by feeding records
 * directly to the WinEventQueue handler and asserting the resulting
 * KeyDownEvent fields. Restricted to Windows because KEY_EVENT_RECORD is a
 * native Win32 structure populated via JNI on class load.
 */
@EnabledOnOs(OS.WINDOWS)
class WinEventQueueKeyboardTest {

    private WinEventQueue queue;

    @BeforeEach
    void setUp() {
        queue = new WinEventQueue() {
            @Override protected void init() {}
            @Override protected void enableRawTerminalMode() {}
            @Override protected void disableRawTerminalMode() {}
        };
        EventQueue.initInstance(queue);
    }

    @AfterEach
    void tearDown() {
        EventQueue.getInstance().shutdown();
    }

    // ---- test data ----------------------------------------------------------

    static Stream<Arguments> keySequences() {
        List<Arguments> args = new ArrayList<>();

        // Printable ASCII a-z: vk = upper-case letter, uchar = lower-case letter
        for (char c = 'a'; c <= 'z'; c++) {
            args.add(of(JtvKeyStroke.of(Character.toUpperCase(c)),
                makeKey(Character.toUpperCase(c), c, 0)));
        }

        // Printable ASCII 0-9
        for (char c = '0'; c <= '9'; c++) {
            args.add(of(JtvKeyStroke.of(c), makeKey(c, c, 0)));
        }

        // Ctrl+A through Ctrl+Z (LeftCtrl): uchar carries the control byte 1-26
        for (char c = 'a'; c <= 'z'; c++) {
            args.add(of(JtvKeyStroke.of(Character.toUpperCase(c), InputEvent.CTRL_DOWN_MASK),
                makeKey(Character.toUpperCase(c), (char) (c - 'a' + 1),
                        KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        }

        // RightCtrl variant must also produce CTRL modifier
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK),
            makeKey(KeyEvent.VK_A, (char) 1, KEY_EVENT_RECORD.RIGHT_CTRL_PRESSED)));

        // Alt+A through Alt+Z (LeftAlt): vk-only path, uchar typically 0
        for (char c = 'a'; c <= 'z'; c++) {
            args.add(of(JtvKeyStroke.of(Character.toUpperCase(c), InputEvent.ALT_DOWN_MASK),
                makeKey(Character.toUpperCase(c), '\0', KEY_EVENT_RECORD.LEFT_ALT_PRESSED)));
        }

        // Alt+0 through Alt+9
        for (char c = '0'; c <= '9'; c++) {
            args.add(of(JtvKeyStroke.of(c, InputEvent.ALT_DOWN_MASK),
                makeKey(c, '\0', KEY_EVENT_RECORD.LEFT_ALT_PRESSED)));
        }

        // RightAlt variant must also produce ALT modifier
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK),
            makeKey(KeyEvent.VK_A, '\0', KEY_EVENT_RECORD.RIGHT_ALT_PRESSED)));

        // Special keys (Win VK codes)
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_BACK_SPACE), makeKey(0x08, '\b', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_TAB),         makeKey(0x09, '\t', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_ENTER),       makeKey(0x0D, '\r', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_ESCAPE),      makeKey(0x1B, (char) 0x1B, 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_UP),     makeKey(0x21, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_DOWN),   makeKey(0x22, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_END),         makeKey(0x23, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_HOME),        makeKey(0x24, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_LEFT),        makeKey(0x25, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_UP),          makeKey(0x26, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_RIGHT),       makeKey(0x27, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DOWN),        makeKey(0x28, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_INSERT),      makeKey(0x2D, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DELETE),      makeKey(0x2E, '\0', 0)));

        // Special keys + Shift
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_TAB,    InputEvent.SHIFT_DOWN_MASK),
            makeKey(0x09, '\t', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DELETE, InputEvent.SHIFT_DOWN_MASK),
            makeKey(0x2E, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));

        // Cursor keys + Shift
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_UP,        InputEvent.SHIFT_DOWN_MASK), makeKey(0x26, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DOWN,      InputEvent.SHIFT_DOWN_MASK), makeKey(0x28, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_RIGHT,     InputEvent.SHIFT_DOWN_MASK), makeKey(0x27, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_LEFT,      InputEvent.SHIFT_DOWN_MASK), makeKey(0x25, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_HOME,      InputEvent.SHIFT_DOWN_MASK), makeKey(0x24, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_END,       InputEvent.SHIFT_DOWN_MASK), makeKey(0x23, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_UP,   InputEvent.SHIFT_DOWN_MASK), makeKey(0x21, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_DOWN, InputEvent.SHIFT_DOWN_MASK), makeKey(0x22, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));

        // Cursor keys + Ctrl
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_UP,        InputEvent.CTRL_DOWN_MASK), makeKey(0x26, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DOWN,      InputEvent.CTRL_DOWN_MASK), makeKey(0x28, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_RIGHT,     InputEvent.CTRL_DOWN_MASK), makeKey(0x27, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_LEFT,      InputEvent.CTRL_DOWN_MASK), makeKey(0x25, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_HOME,      InputEvent.CTRL_DOWN_MASK), makeKey(0x24, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_END,       InputEvent.CTRL_DOWN_MASK), makeKey(0x23, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_UP,   InputEvent.CTRL_DOWN_MASK), makeKey(0x21, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_DOWN_MASK), makeKey(0x22, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));

        // Function keys F1-F10
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F1),  makeKey(0x70, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F2),  makeKey(0x71, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F3),  makeKey(0x72, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F4),  makeKey(0x73, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F5),  makeKey(0x74, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F6),  makeKey(0x75, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F7),  makeKey(0x76, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F8),  makeKey(0x77, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F9),  makeKey(0x78, '\0', 0)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F10), makeKey(0x79, '\0', 0)));

        // F1-F10 + Shift
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F1,  InputEvent.SHIFT_DOWN_MASK), makeKey(0x70, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F2,  InputEvent.SHIFT_DOWN_MASK), makeKey(0x71, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F3,  InputEvent.SHIFT_DOWN_MASK), makeKey(0x72, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F4,  InputEvent.SHIFT_DOWN_MASK), makeKey(0x73, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F5,  InputEvent.SHIFT_DOWN_MASK), makeKey(0x74, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F6,  InputEvent.SHIFT_DOWN_MASK), makeKey(0x75, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F7,  InputEvent.SHIFT_DOWN_MASK), makeKey(0x76, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F8,  InputEvent.SHIFT_DOWN_MASK), makeKey(0x77, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F9,  InputEvent.SHIFT_DOWN_MASK), makeKey(0x78, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F10, InputEvent.SHIFT_DOWN_MASK), makeKey(0x79, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED)));

        // F1-F10 + Ctrl
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F1,  InputEvent.CTRL_DOWN_MASK), makeKey(0x70, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F2,  InputEvent.CTRL_DOWN_MASK), makeKey(0x71, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F3,  InputEvent.CTRL_DOWN_MASK), makeKey(0x72, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F4,  InputEvent.CTRL_DOWN_MASK), makeKey(0x73, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F5,  InputEvent.CTRL_DOWN_MASK), makeKey(0x74, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F6,  InputEvent.CTRL_DOWN_MASK), makeKey(0x75, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F7,  InputEvent.CTRL_DOWN_MASK), makeKey(0x76, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F8,  InputEvent.CTRL_DOWN_MASK), makeKey(0x77, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F9,  InputEvent.CTRL_DOWN_MASK), makeKey(0x78, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F10, InputEvent.CTRL_DOWN_MASK), makeKey(0x79, '\0', KEY_EVENT_RECORD.LEFT_CTRL_PRESSED)));

        // AltGr (LeftCtrl + RightAlt) is treated as a plain character, no Ctrl/Alt modifier
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_A, 0),
            makeKey(KeyEvent.VK_A, 'a',
                    KEY_EVENT_RECORD.LEFT_CTRL_PRESSED | KEY_EVENT_RECORD.RIGHT_ALT_PRESSED)));

        return args.stream();
    }

    private static KEY_EVENT_RECORD makeKey(int vk, char uchar, int controlKeyState) {
        KEY_EVENT_RECORD rec = new KEY_EVENT_RECORD();
        rec.keyDown = true;
        rec.repeatCount = 1;
        rec.keyCode = (short) vk;
        rec.scanCode = 0;
        rec.uchar = uchar;
        rec.controlKeyState = controlKeyState;
        return rec;
    }

    private static Arguments of(JtvKeyStroke ks, KEY_EVENT_RECORD record) {
        return Arguments.of(ks, record);
    }

    // ---- tests --------------------------------------------------------------

    @ParameterizedTest(name = "{0}")
    @MethodSource("keySequences")
    public void keyEvent(JtvKeyStroke expected, KEY_EVENT_RECORD record) {
        queue.handleWindowsKeyEvent(record);
        KeyDownEvent kd = nextKey();
        assertEquals(expected.getKeyCode(),  kd.getKeyCode(),  expected.toString());
        assertEquals(expected.getModifiers(), kd.getModifiers(), expected.toString());
    }

    @Test
    public void keyUpRecordIsDiscarded() {
        KEY_EVENT_RECORD rec = makeKey(KeyEvent.VK_A, 'a', 0);
        rec.keyDown = false;
        queue.handleWindowsKeyEvent(rec);

        JtvEvent event = new JtvEvent();
        EventQueue.getInstance().getKeyEvent(event);
        assertEquals(evNothing, event.getWhat());
    }

    @Test
    public void modifierOnlyKeyDownProducesNoEvent() {
        // VK_SHIFT (0x10) alone, uchar=0, no other modifiers → handler emits nothing
        queue.handleWindowsKeyEvent(makeKey(0x10, '\0', KEY_EVENT_RECORD.SHIFT_PRESSED));

        JtvEvent event = new JtvEvent();
        EventQueue.getInstance().getKeyEvent(event);
        assertEquals(evNothing, event.getWhat());
    }

    // ---- helpers ------------------------------------------------------------

    private static KeyDownEvent nextKey() {
        JtvEvent event = new JtvEvent();
        EventQueue.getInstance().getKeyEvent(event);
        if (event.getWhat() != evKeyDown) {
            fail("No key event received");
        }
        return event.getKeyDown();
    }
}
