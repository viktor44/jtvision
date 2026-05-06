/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKeyStroke;
import org.viktor44.jtvision.core.KeyDownEvent;

/**
 * Verifies ANSI/VT escape-sequence parsing by injecting bytes directly into
 * the EventQueue input stream and asserting the resulting KeyDownEvent fields.
 * Restricted to Linux because the Unix reader/input-loop path is used.
 */
@EnabledOnOs(OS.LINUX)
class EventQueueKeyboardTest {

    private PipedOutputStream writer;

    @BeforeEach
    void setUp() throws IOException {
        PipedInputStream pipeIn = new PipedInputStream();
        writer = new PipedOutputStream(pipeIn);

        EventQueue.initInstance(new EventQueue() {
            @Override
            protected InputStream openInputStream() { return pipeIn; }
            @Override
            protected void enableRawTerminalMode() {}
            @Override
            protected void disableRawTerminalMode() {}
        });
        EventQueue.getInstance().init();
    }

    @AfterEach
    void tearDown() throws IOException {
        EventQueue.getInstance().shutdown();
        writer.close();
    }

    // ---- test data ----------------------------------------------------------

    static Stream<Arguments> keySequences() {
        List<Arguments> args = new ArrayList<>();

        // Printable ASCII a-z
        for (char c = 'a'; c <= 'z'; c++) {
            args.add(of(JtvKeyStroke.of(Character.toUpperCase(c)), c));
        }

        // Ctrl+A through Ctrl+Z (bytes 1-26), skipping bytes mapped to semantic keys
        for (char c = 'a'; c <= 'z'; c++) {
            if (c == 'i') continue; // byte 9  = Tab
            if (c == 'j') continue; // byte 10 = Enter (LF)
            if (c == 'm') continue; // byte 13 = Enter (CR)
            args.add(of(JtvKeyStroke.of(Character.toUpperCase(c), InputEvent.CTRL_DOWN_MASK),
                c - 'a' + 1));
        }

        // Alt+A through Alt+Z (ESC + letter)
        for (char c = 'a'; c <= 'z'; c++) {
            args.add(of(JtvKeyStroke.of(Character.toUpperCase(c), InputEvent.ALT_DOWN_MASK),
                0x1B, c));
        }

        // Special keys
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_BACK_SPACE), 	127));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_ESCAPE),     	0x1B));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_INSERT),     	0x1B, '[', '2', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DELETE),     	0x1B, '[', '3', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_TAB),   9));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_ENTER), 13));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_ENTER), 10));

        // Special keys + Shift
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DELETE,    InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '3', ';', '2', '~')); // FIXME wrong. Keyboard input [27, 91, 51, 126]
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_TAB,       InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', 'Z'));

// postpone
//args.add(of(JtvKeyStroke.of(KeyEvent.VK_INSERT,    InputEvent.SHIFT_DOWN_MASK), ???)); // Windows get this hotkey on system level. Check later
//args.add(of(JtvKeyStroke.of(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_DOWN_MASK), 	127)); // ??? the same as for KeyEvent.VK_BACK_SPACE
//args.add(of(JtvKeyStroke.of(KeyEvent.VK_ESCAPE,       InputEvent.SHIFT_DOWN_MASK), 27)); // ??? the same as for KeyEvent.VK_ESCAPE

        // Cursor keys
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_UP),    		0x1B, '[', 'A'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DOWN),  		0x1B, '[', 'B'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_RIGHT), 		0x1B, '[', 'C'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_LEFT),  		0x1B, '[', 'D'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_HOME),  		0x1B, '[', 'H'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_END),   		0x1B, '[', 'F'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_UP),   	0x1B, '[', '5', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_DOWN), 	0x1B, '[', '6', '~'));

        // Cursor keys + Shift (xterm: ESC [ 1 ; 2 letter)
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_UP,    InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', ';', '2', 'A')); 
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DOWN,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', ';', '2', 'B')); 
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', ';', '2', 'C')); 
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_LEFT,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', ';', '2', 'D')); 
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_HOME,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', ';', '2', 'H'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_END,   InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', ';', '2', 'F'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_UP,   InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '5', ';', '2', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_DOWN, InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '6', ';', '2', '~'));

        // Cursor keys + Ctrl (xterm: ESC [ 1 ; 5 letter)
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_UP,    InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', ';', '5', 'A'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DOWN,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', ';', '5', 'B'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', ';', '5', 'C'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_LEFT,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', ';', '5', 'D'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_HOME,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', ';', '5', 'H'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_END,   InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', ';', '5', 'F'));

        // Cursor keys + Ctrl (rxvt/urxvt: ESC O A/B/C/D)
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_UP,    InputEvent.CTRL_DOWN_MASK),  0x1B, 'O', 'A'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_DOWN,  InputEvent.CTRL_DOWN_MASK),  0x1B, 'O', 'B'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK),  0x1B, 'O', 'C'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_LEFT,  InputEvent.CTRL_DOWN_MASK),  0x1B, 'O', 'D'));

//        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_UP,   InputEvent.CTRL_DOWN_MASK), ???)); FIXME to add  
//        args.add(of(JtvKeyStroke.of(KeyEvent.VK_PAGE_DOWN, InputEvent.SHIFT_DOWN_MASK), ???)); FIXME to add

        // Function keys SS3: ESC O letter
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F1), 0x1B, 'O', 'P'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F2), 0x1B, 'O', 'Q'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F3), 0x1B, 'O', 'R'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F4), 0x1B, 'O', 'S'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F5),  0x1B, '[', '1', '5', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F6),  0x1B, '[', '1', '7', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F7),  0x1B, '[', '1', '8', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F8),  0x1B, '[', '1', '9', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F9),  0x1B, '[', '2', '0', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F10), 0x1B, '[', '2', '1', '~'));

        // Linux console function keys: ESC [ [ A-E
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F1), 0x1B, '[', '[', 'A'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F2), 0x1B, '[', '[', 'B'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F3), 0x1B, '[', '[', 'C'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F4), 0x1B, '[', '[', 'D'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F5), 0x1B, '[', '[', 'E'));

        // Function keys + Shift (ESC [ 1 ; 2 P/Q/R/S for F1-F4, ESC [ Pn ; 2 ~ for F5-F10)
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F1,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', ';', '2', 'P'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F2,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', ';', '2', 'Q'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F3,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', ';', '2', 'R'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F4,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', ';', '2', 'S'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F5,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', '5', ';', '2', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F6,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', '7', ';', '2', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F7,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', '8', ';', '2', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F8,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '1', '9', ';', '2', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F9,  InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '2', '0', ';', '2', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F10, InputEvent.SHIFT_DOWN_MASK), 0x1B, '[', '2', '1', ';', '2', '~'));

        // Function keys + Ctrl (ESC [ 1 ; 5 P/Q/R/S for F1-F4, ESC [ Pn ; 5 ~ for F5-F10)
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F1,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', ';', '5', 'P'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F2,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', ';', '5', 'Q'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F3,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', ';', '5', 'R'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F4,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', ';', '5', 'S'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F5,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', '5', ';', '5', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F6,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', '7', ';', '5', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F7,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', '8', ';', '5', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F8,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '1', '9', ';', '5', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F9,  InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '2', '0', ';', '5', '~'));
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_F10, InputEvent.CTRL_DOWN_MASK),  0x1B, '[', '2', '1', ';', '5', '~'));

        return args.stream();
    }

    private static Arguments of(JtvKeyStroke ks, int... bytes) {
        return Arguments.of(ks, bytes);
    }

    // ---- test ---------------------------------------------------------------

    @ParameterizedTest(name = "{0}")
    @MethodSource("keySequences")
    public void keySequence(JtvKeyStroke expected, int[] bytes) throws Exception {
        send(bytes);
        KeyDownEvent kd = nextKey();
        assertEquals(expected.getKeyCode(),  kd.getKeyCode(),  expected.toString());
        assertEquals(expected.getModifiers(), kd.getModifiers(), expected.toString());
    }

    // ---- helpers ------------------------------------------------------------

    private void send(int... bytes) throws IOException {
        for (int b : bytes) {
            writer.write(b);
        }
        writer.flush();
    }

    private KeyDownEvent nextKey() throws InterruptedException {
        JtvEvent event = new JtvEvent();
        long deadline = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < deadline) {
            EventQueue.getInstance().getKeyEvent(event);
            if (event.getWhat() == evKeyDown) {
                return event.getKeyDown();
            }
            Thread.sleep(10);
        }
        fail("No key event received within 2 s");
        return null;
    }
}
