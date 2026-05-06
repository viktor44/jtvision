/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.EventCodes.evMouseUp;
import static org.viktor44.jtvision.core.EventCodes.evMouseWheel;
import static org.viktor44.jtvision.core.EventCodes.meDoubleClick;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.viktor44.jtvision.core.EventCodes;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.MouseEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Unix/macOS base implementation of {@link EventQueue}.
 * <p>
 * Two daemon threads cooperate:
 * <ol>
 *   <li>{@link #readerLoop()} — reads raw bytes from the terminal device
 *       ({@code /dev/tty} if available, otherwise {@code System.in}) and
 *       places them in {@link #byteQueue}.</li>
 *   <li>{@link #inputLoop()} — consumes bytes from {@link #byteQueue},
 *       parses ANSI escape sequences (CSI / SS3 / SGR mouse), and pushes
 *       fully decoded {@link JtvEvent} objects into the shared event queue.</li>
 * </ol>
 * Raw mode is managed via {@code stty}. Subclasses: {@link LinuxEventQueue}
 * (no extra behaviour) and {@link MacosEventQueue} (Option-key mapping).
 */
@Slf4j
abstract class UnixEventQueue extends EventQueue {

    /**
     * Intermediate byte queue. {@link #readerLoop()} offers raw bytes here;
     * {@link #inputLoop()} polls them for escape-sequence parsing.
     */
    private final LinkedBlockingQueue<Integer> byteQueue = new LinkedBlockingQueue<>();

    /**
     * The terminal input stream; either {@code /dev/tty} or {@code System.in},
     * opened by {@link #openInputStream()}.
     */
    private InputStream inputStream;

    /**
     * {@code true} if {@link #inputStream} was opened by this class and must
     * be closed on shutdown ({@code /dev/tty} case).
     */
    private boolean closeInputStreamOnStop = false;

    /**
     * The Unix terminal device used for both raw input and {@code stty} commands.
     */
    private static final File ttyDevice = new File("/dev/tty");

    /**
     * Terminal state string captured by {@code stty -g} before raw mode is
     * enabled. Restored verbatim on shutdown.
     */
    private String savedTerminalState;

    /**
     * Maximum time in milliseconds to wait for the next byte after receiving
     * an ESC character (0x1B). If no byte arrives within this window, the ESC
     * is treated as a standalone Escape keystroke.
     */
    private static final int ESC_SEQUENCE_TIMEOUT_MS = 40;

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
        inputStream = openInputStream();
        byteQueue.clear();
        readerThread = new Thread(this::readerLoop, "jvision-reader");
        readerThread.setDaemon(true);
        readerThread.start();
        inputThread = new Thread(this::inputLoop, "jvision-input");
        inputThread.setDaemon(true);
        inputThread.start();
    }

    @Override
    protected void enableRawTerminalMode() {
        if (rawTerminalEnabled) {
        	return;
        }
        String terminalState = runSttyAndCapture("-g");
        if (terminalState == null || terminalState.isEmpty()) {
        	return;
        }
        if (runStty("raw", "-echo", "min", "1", "time", "0")) {
            savedTerminalState = terminalState;
            rawTerminalEnabled = true;
        }
    }

    @Override
    protected void disableRawTerminalMode() {
        if (!rawTerminalEnabled) {
        	return;
        }
        if (savedTerminalState == null || savedTerminalState.isEmpty()) {
        	return;
        }
        if (runStty(savedTerminalState)) {
            rawTerminalEnabled = false;
            savedTerminalState = null;
        }
    }

    @Override
    protected void beforeStopThreads(boolean forceClose) {
        closeInputStream(forceClose);
    }

    @Override
    protected void afterStopThreads() {
        byteQueue.clear();
    }

    // ------------------------------------------------------------------
    // Input stream
    // ------------------------------------------------------------------

    /**
     * Opens the terminal input stream, preferring {@code /dev/tty} over
     * {@code System.in}. {@code /dev/tty} always refers to the controlling
     * terminal even when stdin is redirected.
     *
     * @return the opened {@link InputStream}
     */
    protected InputStream openInputStream() {
        if (ttyDevice.exists()) {
            try {
                closeInputStreamOnStop = true;
                return new FileInputStream(ttyDevice);
            }
            catch (IOException ignored) {
            }
        }
        closeInputStreamOnStop = false;
        return System.in;
    }

    private void closeInputStream(boolean forceClose) {
        if (inputStream == null) {
        	return;
        }
        if (closeInputStreamOnStop || forceClose) {
            try {
                inputStream.close();
            }
            catch (IOException ignored) {
            }
        }
        inputStream = null;
        closeInputStreamOnStop = false;
    }

    // ------------------------------------------------------------------
    // stty helpers
    // ------------------------------------------------------------------

    private boolean runStty(String... args) {
        return runSttyInternal(false, args) != null;
    }

    private String runSttyAndCapture(String... args) {
        String output = runSttyInternal(true, args);
        return output == null ? null : output.trim();
    }

    private String runSttyInternal(boolean captureOutput, String... args) {
        if (!ttyDevice.exists()) {
        	return null;
        }
        String[] command = new String[args.length + 1];
        command[0] = "stty";
        System.arraycopy(args, 0, command, 1, args.length);
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectInput(ttyDevice);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            InputStream processOutput = process.getInputStream();
            byte[] chunk = new byte[128];
            int read;
            while ((read = processOutput.read(chunk)) != -1) {
                output.write(chunk, 0, read);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
            	return null;
            }
            return captureOutput
                ? new String(output.toByteArray(), StandardCharsets.US_ASCII)
                : "";
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return null;
        }
        catch (IOException ignored) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // Reader / input loops
    // ------------------------------------------------------------------

    /**
     * Unix reader thread body. Reads bytes from {@link #inputStream} one at
     * a time and offers each to {@link #byteQueue}. Exits when {@link #running}
     * becomes {@code false} or the stream reaches EOF.
     */
    private void readerLoop() {
        try {
            while (running) {
                int b = inputStream.read();
                if (b < 0) {
                	break;
                }
                byteQueue.offer(b);
            }
        }
        catch (IOException ignored) {
        }
    }

    /**
     * Unix input-decoding thread body. Polls {@link #byteQueue} for bytes
     * (50 ms timeout) and passes each to {@link #processInput}.
     */
    private void inputLoop() {
        try {
            while (running) {
                Integer b = byteQueue.poll(50, TimeUnit.MILLISECONDS);
                if (b == null) {
                	continue;
                }
                processInput(b);
            }
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        catch (IOException ignored) {
        }
    }

    @SuppressWarnings("unused")
    private void printInput(int first) throws IOException {
        List<Integer> input = new ArrayList<>();
        input.add(first);
        while (true) {
            int next = readByteWithTimeout(ESC_SEQUENCE_TIMEOUT_MS);
            if (next < 0) break;
            input.add(next);
        }
        log.info("Keyboard input {}", input);
        if (first == 'q' - 'a' + 1) {
            log.info("(exit)");
            System.exit(0);
        }
    }

    // ------------------------------------------------------------------
    // Input parsing
    // ------------------------------------------------------------------

    /**
     * Parses one input byte (or the start of a multi-byte sequence) and
     * pushes zero or more {@link JtvEvent} objects.
     * <p>
     * Dispatch:
     * <ul>
     *   <li>{@code 0x1B} — ANSI escape sequence or plain Escape.</li>
     *   <li>{@code 9} — Tab.</li>
     *   <li>{@code 13} / {@code 10} — Enter.</li>
     *   <li>{@code 127} — Backspace.</li>
     *   <li>{@code < 32} (except 9, 10, 13) — Ctrl+letter.</li>
     *   <li>High bit set — UTF-8 multi-byte character.</li>
     *   <li>All other values — printable ASCII.</li>
     * </ul>
     *
     * @param firstByte the first byte of the input sequence
     * @throws IOException if reading continuation bytes fails
     */
    private void processInput(int firstByte) throws IOException {
        if (firstByte == 0x1B) {
            int second = readByteWithTimeout(ESC_SEQUENCE_TIMEOUT_MS);
            if (second < 0) {
                pushKeyEvent(KeyEvent.VK_ESCAPE, 0, '\033');
            }
            else if (second == '[') {
                parseCSI();
            }
            else if (second == 'O') {
                parseSS3();
            }
            else {
                pushAltKey(second);
            }
        }
        else if (firstByte == 9) {
            pushKeyEvent(KeyEvent.VK_TAB, 0, '\t');
        }
        else if (firstByte == 13 || firstByte == 10) {
            pushKeyEvent(KeyEvent.VK_ENTER, 0, '\r');
        }
        else if (firstByte == 127) {
            pushKeyEvent(KeyEvent.VK_BACK_SPACE, 0, KeyEvent.CHAR_UNDEFINED);
        }
        else if (firstByte < 32) {
            int vk = (firstByte >= 1 && firstByte <= 26) ? (KeyEvent.VK_A - 1 + firstByte) : 0;
            pushKeyEvent(vk, InputEvent.CTRL_DOWN_MASK, (char) firstByte);
        }
        else {
            if ((firstByte & 0x80) != 0) {
                handleUtf8Input(firstByte);
                return;
            }
            char c = (char) firstByte;
            int vk = Character.toUpperCase(c);
            pushKeyEvent(vk, 0, c);
        }
    }

    /**
     * Handles the first byte of a multi-byte UTF-8 sequence. Decodes the full
     * character and, if {@link #macOptionCodeToAlt} returns a non-zero VK,
     * emits an Alt+key event instead.
     *
     * @param firstByte the high byte that signalled a UTF-8 sequence
     * @throws IOException if reading continuation bytes fails
     */
    protected void handleUtf8Input(int firstByte) throws IOException {
        String text = decodeUtf8Char(firstByte);
        if (text.isEmpty()) {
        	return;
        }
        int codePoint = text.codePointAt(0);
        int altVk = macOptionCodeToAlt(codePoint);
        if (altVk != 0) {
            pushKeyEvent(altVk, InputEvent.ALT_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
            return;
        }
        char keyChar = (codePoint <= Character.MAX_VALUE) ? (char) codePoint : KeyEvent.CHAR_UNDEFINED;
        int vk = (codePoint < 128) ? Character.toUpperCase(codePoint) : 0;
        pushKeyEvent(vk, 0, keyChar);
    }

    /**
     * Maps a Unicode code point to an AWT virtual-key code for macOS
     * Option+key combinations. Returns {@code 0} on non-macOS platforms.
     * Overridden by {@link MacosEventQueue}.
     *
     * @param codePoint the Unicode code point to test
     * @return the AWT {@code VK_*} code, or {@code 0}
     */
    protected int macOptionCodeToAlt(int codePoint) {
        return 0;
    }

    private String decodeUtf8Char(int firstByte) throws IOException {
        int extraBytes = utf8ExtraByteCount(firstByte);
        if (extraBytes <= 0) {
            return new String(new byte[]{(byte) firstByte}, StandardCharsets.ISO_8859_1);
        }
        byte[] utf8 = new byte[extraBytes + 1];
        utf8[0] = (byte) firstByte;
        for (int i = 1; i < utf8.length; i++) {
            int next = readByteWithTimeout(ESC_SEQUENCE_TIMEOUT_MS);
            if (next < 0) {
            	return new String(utf8, 0, i, StandardCharsets.ISO_8859_1);
            }
            utf8[i] = (byte) next;
            if ((next & 0xC0) != 0x80) {
            	return new String(utf8, 0, i + 1, StandardCharsets.ISO_8859_1);
            }
        }
        return new String(utf8, StandardCharsets.UTF_8);
    }

    private int utf8ExtraByteCount(int firstByte) {
        if ((firstByte & 0xE0) == 0xC0) {
        	return 1;
        }
        if ((firstByte & 0xF0) == 0xE0) {
        	return 2;
        }
        if ((firstByte & 0xF8) == 0xF0) {
        	return 3;
        }
        return 0;
    }

    /**
     * Parses an ANSI CSI sequence ({@code ESC [}) and dispatches on the final
     * character. Supports cursor/function keys, VT tilde sequences, and SGR mouse.
     *
     * @throws IOException if reading bytes fails
     */
    private void parseCSI() throws IOException {
        StringBuilder params = new StringBuilder();
        int c;
        while (true) {
            c = readByteWithTimeout(ESC_SEQUENCE_TIMEOUT_MS);
            if (c < 0) {
            	return;
            }
            if (c >= 0x40 && c <= 0x7E) {
            	break;
            }
            params.append((char) c);
        }
        char finalChar = (char) c;
        String paramStr = params.toString();
        int modifiers = csiModifierToAwtModifiers(paramStr);

        if (paramStr.startsWith("<")) {
            parseSGRMouse(paramStr.substring(1), finalChar);
            return;
        }

        switch (finalChar) {
            case 'A': 
            	pushKeyEvent(KeyEvent.VK_UP,    modifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'B':
            	pushKeyEvent(KeyEvent.VK_DOWN,  modifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'C':
            	pushKeyEvent(KeyEvent.VK_RIGHT, modifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'D':
            	pushKeyEvent(KeyEvent.VK_LEFT,  modifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'H':
            	pushKeyEvent(KeyEvent.VK_HOME,  modifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'F':
            	pushKeyEvent(KeyEvent.VK_END,   modifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'Z':
            	pushKeyEvent(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
            	break;
            case '~':
            	parseCsiTilde(paramStr);
            	break;
            case '^':
            	parseCsiCaret(paramStr);
            	break;
            case 'P':
            	pushKeyEvent(KeyEvent.VK_F1, modifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'Q':
            	pushKeyEvent(KeyEvent.VK_F2, modifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'R':
                if (paramStr.isEmpty()) {
                    pushKeyEvent(KeyEvent.VK_F3, 0, KeyEvent.CHAR_UNDEFINED);
                }
                else if (paramStr.startsWith("1;")) {
                    pushKeyEvent(KeyEvent.VK_F3, modifiers, KeyEvent.CHAR_UNDEFINED);
                }
                break;
            case 'S':
            	pushKeyEvent(KeyEvent.VK_F4, modifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case '[': {
                // Linux console F1–F5: ESC [ [ A–E
                int lc = readByteWithTimeout(ESC_SEQUENCE_TIMEOUT_MS);
                switch (lc) {
                    case 'A':
                    	pushKeyEvent(KeyEvent.VK_F1, 0, KeyEvent.CHAR_UNDEFINED);
                    	break;
                    case 'B':
                    	pushKeyEvent(KeyEvent.VK_F2, 0, KeyEvent.CHAR_UNDEFINED);
                    	break;
                    case 'C':
                    	pushKeyEvent(KeyEvent.VK_F3, 0, KeyEvent.CHAR_UNDEFINED);
                    	break;
                    case 'D':
                    	pushKeyEvent(KeyEvent.VK_F4, 0, KeyEvent.CHAR_UNDEFINED);
                    	break;
                    case 'E':
                    	pushKeyEvent(KeyEvent.VK_F5, 0, KeyEvent.CHAR_UNDEFINED);
                    	break;
                }
                break;
            }
        }
    }

    private int csiModifierToAwtModifiers(String paramStr) {
        int separator = paramStr.indexOf(';');
        if (separator < 0 || separator + 1 >= paramStr.length()) {
        	return 0;
        }
        try {
            int modifier = Integer.parseInt(paramStr.substring(separator + 1)) - 1;
            if (modifier <= 0) {
            	return 0;
            }
            int modifiers = 0;
            if ((modifier & 1) != 0) {
            	modifiers |= InputEvent.SHIFT_DOWN_MASK;
            }
            if ((modifier & 2) != 0) {
            	modifiers |= InputEvent.ALT_DOWN_MASK;
            }
            if ((modifier & 4) != 0) {
            	modifiers |= InputEvent.CTRL_DOWN_MASK;
            }
            return modifiers;
        }
        catch (NumberFormatException ignored) {
            return 0;
        }
    }

    /**
     * Handles VT tilde sequences ({@code ESC [ Pn ~ }) for navigation and
     * function keys, with optional modifier field ({@code Pn;modifier~}).
     *
     * @param paramStr the parameter string before the {@code ~} final byte
     */
    private void parseCsiTilde(String paramStr) {
        int code;
        int modifier = 1;
        int separator = paramStr.indexOf(';');
        String firstParam = (separator >= 0) ? paramStr.substring(0, separator) : paramStr;
        if (separator >= 0 && separator + 1 < paramStr.length()) {
            try {
                modifier = Integer.parseInt(paramStr.substring(separator + 1));
            }
            catch (NumberFormatException ignored) {
                modifier = 1;
            }
        }
        try {
            code = Integer.parseInt(firstParam);
        }
        catch (NumberFormatException ignored) {
            return;
        }
        int m = modifier - 1;
        int awtModifiers = 0;
        if (m > 0) {
            if ((m & 1) != 0) {
            	awtModifiers |= InputEvent.SHIFT_DOWN_MASK;
            }
            if ((m & 2) != 0) {
            	awtModifiers |= InputEvent.ALT_DOWN_MASK;
            }
            if ((m & 4) != 0) {
            	awtModifiers |= InputEvent.CTRL_DOWN_MASK;
            }
        }
        switch (code) {
            case 1:
            	pushKeyEvent(KeyEvent.VK_HOME, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 2:
            	pushKeyEvent(KeyEvent.VK_INSERT, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 3:
            	pushKeyEvent(KeyEvent.VK_DELETE, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 4:
            	pushKeyEvent(KeyEvent.VK_END, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 5:
            	pushKeyEvent(KeyEvent.VK_PAGE_UP, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 6:
            	pushKeyEvent(KeyEvent.VK_PAGE_DOWN, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 11:
            	pushKeyEvent(KeyEvent.VK_F1, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 12:
            	pushKeyEvent(KeyEvent.VK_F2, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 13:
            	pushKeyEvent(KeyEvent.VK_F3, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 14:
            	pushKeyEvent(KeyEvent.VK_F4, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 15:
            	pushKeyEvent(KeyEvent.VK_F5, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 17:
            	pushKeyEvent(KeyEvent.VK_F6, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 18:
            	pushKeyEvent(KeyEvent.VK_F7, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 19:
            	pushKeyEvent(KeyEvent.VK_F8, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 20:
            	pushKeyEvent(KeyEvent.VK_F9, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 21:
            	pushKeyEvent(KeyEvent.VK_F10, awtModifiers, KeyEvent.CHAR_UNDEFINED);
            	break;
        }
    }

    /**
     * Handles caret-terminated CSI sequences ({@code ESC [ Pn ^ }).
     * Currently maps code {@code 21} to F10.
     *
     * @param paramStr the numeric parameter before the {@code ^} final byte
     */
    private void parseCsiCaret(String paramStr) {
        int code;
        try {
            code = Integer.parseInt(paramStr);
        }
        catch (NumberFormatException ignored) {
            return;
        }
        switch (code) {
            case 21: pushKeyEvent(KeyEvent.VK_F10, 0, KeyEvent.CHAR_UNDEFINED); break;
        }
    }

    /**
     * Parses an SS3 sequence ({@code ESC O <char>}) for function and cursor
     * keys. Also handles rxvt-style Ctrl+cursor: {@code ESC O A/B/C/D}.
     *
     * @throws IOException if reading the final byte fails
     */
    private void parseSS3() throws IOException {
        int c = readByteWithTimeout(ESC_SEQUENCE_TIMEOUT_MS);
        if (c < 0) {
        	return;
        }
        switch (c) {
            case 'P':
            	pushKeyEvent(KeyEvent.VK_F1, 0, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'Q':
            	pushKeyEvent(KeyEvent.VK_F2, 0, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'R':
            	pushKeyEvent(KeyEvent.VK_F3, 0, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'S':
            	pushKeyEvent(KeyEvent.VK_F4, 0, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'H':
            	pushKeyEvent(KeyEvent.VK_HOME, 0, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'F':
            	pushKeyEvent(KeyEvent.VK_END, 0, KeyEvent.CHAR_UNDEFINED);
            	break;
            // Ctrl+cursor keys — rxvt/urxvt convention: ESC O A/B/C/D
            case 'A':
            	pushKeyEvent(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'B':
            	pushKeyEvent(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'C':
            	pushKeyEvent(KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
            	break;
            case 'D':
            	pushKeyEvent(KeyEvent.VK_LEFT, InputEvent.CTRL_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
            	break;
        }
    }

    /**
     * Polls {@link #byteQueue} for up to {@code timeoutMs} milliseconds.
     *
     * @param timeoutMs maximum wait time in milliseconds
     * @return the byte value (0–255), or {@code -1} on timeout/interrupt
     * @throws IOException never; declared for uniformity with callers
     */
    private int readByteWithTimeout(int timeoutMs) throws IOException {
        try {
            Integer b = byteQueue.poll(timeoutMs, TimeUnit.MILLISECONDS);
            return b == null ? -1 : b;
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    /**
     * Parses an SGR mouse event ({@code ESC [ < Pb ; Px ; Py M/m}).
     *
     * @param params    parameter string after {@code <} ({@code "button;x;y"})
     * @param finalChar {@code 'M'} for press/move, {@code 'm'} for release
     */
    private void parseSGRMouse(String params, char finalChar) {
        String[] parts = params.split(";");
        if (parts.length < 3) {
        	return;
        }
        try {
            int button = Integer.parseInt(parts[0]);
            int x = Integer.parseInt(parts[1]) - 1;
            int y = Integer.parseInt(parts[2]) - 1;
            boolean release = (finalChar == 'm');
            boolean move = (button & 32) != 0;
            int wheel = (button & 64) != 0 ? 1 : 0;
            int btn = button & 3;

            JtvPoint where = new JtvPoint(x, y);
            int eventWhat;
            MouseEvent mouse;

            if (wheel != 0) {
                eventWhat = evMouseWheel;
                mouse = new MouseEvent(where, 0, 0, 0, (btn == 0) ? EventCodes.mwUp : EventCodes.mwDown);
            }
            else if (release) {
                eventWhat = evMouseUp;
                mouse = new MouseEvent(where, 0, 0, 0, 0);
            }
            else if (move) {
                eventWhat = evMouseMove;
                mouse = new MouseEvent(where, 0, 0, toButtonMask(btn), 0);
            }
            else {
                int flags = 0;
                long now = System.currentTimeMillis();
                if (now - lastClickTime < doubleDelay) {
                    clickCount++;
                    if (clickCount == 2) {
                    	flags = meDoubleClick;
                    }
                }
                else {
                    clickCount = 1;
                }
                lastClickTime = now;
                eventWhat = evMouseDown;
                mouse = new MouseEvent(where, flags, 0, toButtonMask(btn), 0);
            }

            JtvEvent event = new JtvEvent();
            event.setWhat(eventWhat);
            event.setMouse(mouse);
            lastMouse = new MouseEvent(new JtvPoint(where), 0, 0, mouse.getButtons(), 0);
            offerEvent(event);
        } 
        catch (NumberFormatException ignored) {
        }
    }

    /**
     * Converts a character received after {@code ESC} (Alt+key) to the
     * corresponding AWT virtual-key code and pushes an Alt+key event.
     *
     * @param ch the character byte following the ESC byte
     */
    private void pushAltKey(int ch) {
        char upper = Character.toUpperCase((char) ch);
        int vk = (upper >= 'A' && upper <= 'Z') ? upper : 0;
        pushKeyEvent(vk, InputEvent.ALT_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
    }
}
