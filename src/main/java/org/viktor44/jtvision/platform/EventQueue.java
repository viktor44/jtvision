/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import static org.viktor44.jtvision.core.EventCodes.evKeyboard;
import static org.viktor44.jtvision.core.EventCodes.evMouse;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.EventCodes.evMouseUp;
import static org.viktor44.jtvision.core.EventCodes.evMouseWheel;
import static org.viktor44.jtvision.core.EventCodes.evNothing;
import static org.viktor44.jtvision.core.EventCodes.mbLeftButton;
import static org.viktor44.jtvision.core.EventCodes.mbMiddleButton;
import static org.viktor44.jtvision.core.EventCodes.mbRightButton;
import static org.viktor44.jtvision.core.EventCodes.meDoubleClick;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.internal.Kernel32.INPUT_RECORD;
import org.fusesource.jansi.internal.Kernel32.KEY_EVENT_RECORD;
import org.fusesource.jansi.internal.Kernel32.MOUSE_EVENT_RECORD;
import org.viktor44.jtvision.core.EventCodes;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.MouseEvent;
import org.viktor44.jtvision.util.SystemUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Platform-level input event queue.
 * <p>
 * EventQueue bridges raw terminal or Win32 console input into the
 * {@link JtvEvent} objects consumed by the JT Vision event loop
 * ({@code TProgram.getEvent}). It is a pure static utility class: all state
 * is held in static fields and all methods are static.
 * 
 * <h3>Architecture</h3>
 * 
 * <b>Unix/macOS path:</b> Two daemon threads cooperate:
 * <ol>
 *   <li>{@link #readerLoop()} — reads raw bytes from the terminal device
 *       ({@code /dev/tty} if available, otherwise {@code System.in}) and
 *       places them in {@link #byteQueue}.</li>
 *   <li>{@link #inputLoop()} — consumes bytes from {@link #byteQueue},
 *       parses ANSI escape sequences (CSI / SS3 / SGR mouse), and pushes
 *       fully decoded {@link JtvEvent} objects into {@link #eventQueue}.</li>
 * </ol>
 * <b>Windows path:</b> A single {@link #windowsEventLoop()} thread reads
 * {@code INPUT_RECORD} structures via Jansi's {@code Kernel32} binding and
 * translates them directly into {@link JtvEvent} objects.
 * 
 * <h3>Terminal raw mode</h3>
 * 
 * On Unix, {@link #enableRawTerminalMode()} invokes {@code stty raw -echo}
 * to suppress line-buffering and echo so individual keystrokes arrive
 * immediately. The original terminal state is saved with {@code stty -g} and
 * restored by {@link #disableRawTerminalMode()} on shutdown or suspend.
 * On Windows, the equivalent is done by clearing the
 * {@code ENABLE_LINE_INPUT}, {@code ENABLE_ECHO_INPUT}, and
 * {@code ENABLE_PROCESSED_INPUT} console mode flags via
 * {@code SetConsoleMode}.
 * 
 * <h3>Double-click detection</h3>
 * 
 * Two successive {@code evMouseDown} events on Unix (SGR mouse protocol)
 * are considered a double-click when they arrive within {@link #doubleDelay}
 * milliseconds of each other. The second event has the {@code meDoubleClick}
 * flag set in {@link MouseEvent#getEventFlags()}. On Windows, the OS reports
 * double-clicks directly via {@code DOUBLE_CLICK} in the mouse event flags.
 * 
 * <h3>Escape-sequence timeout</h3>
 * 
 * {@link #ESC_SEQUENCE_TIMEOUT_MS} controls how long the parser waits for
 * the next byte after receiving {@code ESC} (0x1B). If no byte arrives within
 * the timeout, the escape is treated as a plain Escape key rather than the
 * start of a multi-byte sequence.
 *
 * @see Screen
 * @see JtvEvent
 */
@Slf4j
public class EventQueue {

    /**
     * The main event queue shared between input-parsing threads and the
     * JT Vision event loop. Mouse and keyboard events are offered here
     * by the reader threads and dequeued by {@link #getMouseEvent} and
     * {@link #getKeyEvent}.
     */
    private static final LinkedBlockingQueue<JtvEvent> eventQueue = new LinkedBlockingQueue<>();

    /**
     * Intermediate byte queue (Unix only). {@link #readerLoop()} offers raw
     * bytes here; {@link #inputLoop()} polls them for escape-sequence parsing.
     * Decoupling the two loops allows {@link #readByteWithTimeout} to
     * implement the escape-sequence timeout without blocking the I/O thread.
     */
    private static final LinkedBlockingQueue<Integer> byteQueue = new LinkedBlockingQueue<>();

    /**
     * The event-decoding thread (Unix: {@link #inputLoop()};
     * Windows: {@link #windowsEventLoop()}).
     */
    private static Thread inputThread;

    /**
     * The raw-byte reader thread (Unix only: {@link #readerLoop()}).
     * On Windows this field is unused; the Windows event loop combines
     * reading and decoding.
     */
    private static Thread readerThread;

    /**
     * {@code true} while the input threads are running. Set to {@code false}
     * by {@link #stopInputThreads(boolean)} to request orderly shutdown.
     */
    private static volatile boolean running = false;

    /**
     * The terminal input stream (Unix only). Either {@code /dev/tty} or
     * {@code System.in}, opened by {@link #openInputStream()}.
     */
    private static InputStream inputStream;

    /**
     * {@code true} if {@link #inputStream} was opened by EventQueue and
     * should be closed on shutdown ({@code /dev/tty} case). {@code false}
     * when {@code System.in} is used — it must not be closed.
     */
    private static boolean closeInputStreamOnStop = false;

    /**
     * The Unix terminal device used for both raw input and {@code stty}
     * commands. Preferred over {@code System.in} because it always refers
     * to the controlling terminal even when stdin is redirected.
     */
    private static final File ttyDevice = new File("/dev/tty");

    /**
     * The terminal state string captured by {@code stty -g} before raw mode
     * is enabled. Restored verbatim by {@code stty} in
     * {@link #disableRawTerminalMode()}.
     */
    private static String savedTerminalState;

    /**
     * {@code true} after raw terminal mode has been successfully enabled.
     * Guards against double-enabling or double-disabling.
     */
    private static boolean rawTerminalEnabled = false;

    /**
     * The Windows console input mode value saved before raw mode is enabled.
     * Restored by {@link #restoreWindowsInputMode()}.
     */
    private static int savedWindowsInputMode;

    /**
     * {@code true} if {@link #savedWindowsInputMode} contains a valid
     * saved value that should be restored on shutdown.
     */
    private static boolean windowsInputModeSaved = false;

    /**
     * Maximum time in milliseconds to wait for the next byte after receiving
     * an ESC character (0x1B). If no byte arrives within this window the ESC
     * is treated as a standalone Escape keystroke rather than the opening of
     * an ANSI escape sequence.
     */
    private static final int ESC_SEQUENCE_TIMEOUT_MS = 40;

    // ------------------------------------------------------------------
    // Windows console mode flags (WinBase.h / docs.microsoft.com)
    // ------------------------------------------------------------------

    /**
     * Windows console mode flag: CTRL+C, CTRL+BREAK, and CTRL+CLOSE
     * generate control events rather than raw input records.
     * Cleared in raw mode so the application receives all keystrokes.
     */
    private static final int WIN_ENABLE_PROCESSED_INPUT = 0x0001;

    /**
     * Windows console mode flag: input is buffered until Enter is pressed.
     * Cleared in raw mode so each keystroke is available immediately.
     */
    private static final int WIN_ENABLE_LINE_INPUT = 0x0002;

    /**
     * Windows console mode flag: characters typed are echoed back.
     * Cleared in raw mode to suppress visible echo.
     */
    private static final int WIN_ENABLE_ECHO_INPUT = 0x0004;

    /**
     * Windows console mode flag: window resize events are placed in the
     * input queue. Set in raw mode so terminal resize can be detected.
     */
    private static final int WIN_ENABLE_WINDOW_INPUT = 0x0008;

    /**
     * Windows console mode flag: mouse input events are placed in the
     * input queue. Set in raw mode to receive mouse events.
     */
    private static final int WIN_ENABLE_MOUSE_INPUT = 0x0010;

    /**
     * Windows console mode flag: the console quick-edit mode is active
     * (text selection with the mouse). Cleared in raw mode so mouse events
     * are routed to the application instead.
     */
    private static final int WIN_ENABLE_QUICK_EDIT_MODE = 0x0040;

    /**
     * Windows console mode flag: must be set whenever
     * {@link #WIN_ENABLE_QUICK_EDIT_MODE} or
     * {@link #WIN_ENABLE_INSERT_MODE} is modified.
     */
    private static final int WIN_ENABLE_EXTENDED_FLAGS = 0x0080;

    /**
     * Windows console mode flag: virtual terminal input sequences
     * (ANSI escape codes) are processed by the console host rather than
     * arriving as raw {@code KEY_EVENT} records. Cleared in raw mode so
     * all keystrokes arrive as {@code INPUT_RECORD} structures.
     */
    private static final int WIN_ENABLE_VIRTUAL_TERMINAL_INPUT = 0x0200;

    // ------------------------------------------------------------------
    // Mouse state
    // ------------------------------------------------------------------

    /**
     * The most recently reported mouse position and button state. Used on
     * Windows to detect transitions between button-down and button-up, and
     * on Unix to track the last known cursor position for event coalescing.
     */
    private static MouseEvent lastMouse = new MouseEvent();

    /**
     * Timestamp (milliseconds since epoch) of the most recent
     * {@code evMouseDown} event. Used together with {@link #clickCount} to
     * detect double-clicks on Unix.
     */
    private static long lastClickTime = 0;

    /**
     * Running count of successive clicks within the {@link #doubleDelay}
     * window. Reset to 1 when a click arrives outside the window; incremented
     * to 2 on the second click, at which point the event receives
     * {@code meDoubleClick}.
     */
    private static int clickCount = 0;

    /**
     * Maximum time between two mouse-down events (in milliseconds) for them
     * to be counted as a double-click. Analogous to the original JT Vision
     * {@code DoubleDelay} variable (which measured eighteenths of a second;
     * the default of 8 ≈ 440 ms). Defaults to 400 ms.
     */
    public static int doubleDelay = 400; // ms

    /**
     * Initialises the event queue and starts the input thread(s).
     * <p>
     * If the queue is already running, returns immediately. Otherwise:
     * <ol>
     *   <li>Enables raw terminal mode via {@link #enableRawTerminalMode()}.</li>
     *   <li>On Windows, starts {@link #windowsEventLoop()} in a single daemon
     *       thread that reads {@code INPUT_RECORD} structures via Kernel32.</li>
     *   <li>On Unix/macOS, opens the terminal input stream, then starts
     *       {@link #readerLoop()} and {@link #inputLoop()} as two cooperating
     *       daemon threads.</li>
     * </ol>
     */
    public static void init() {
        if (running) return;
        enableRawTerminalMode();
        running = true;
        if (SystemUtils.IS_OS_WINDOWS) {
            readerThread = new Thread(EventQueue::windowsEventLoop, "jvision-reader");
            readerThread.setDaemon(true);
            readerThread.start();
            return;
        }
        inputStream = openInputStream();
        byteQueue.clear();
        readerThread = new Thread(EventQueue::readerLoop, "jvision-reader");
        readerThread.setDaemon(true);
        readerThread.start();
        inputThread = new Thread(EventQueue::inputLoop, "jvision-input");
        inputThread.setDaemon(true);
        inputThread.start();
    }

    /**
     * Shuts down the event queue and restores the terminal to its original
     * state. Stops all input threads and calls
     * {@link #disableRawTerminalMode()}.
     */
    public static void shutdown() {
        stopInputThreads(true);
        disableRawTerminalMode();
    }

    /**
     * Waits for at least one event to become available, up to
     * {@code timeoutMs} milliseconds.
     * <p>
     * Always flushes pending screen updates via {@link Screen#flushScreen()}
     * first, so the terminal displays the result of the previous event
     * iteration (e.g. a moved window) before blocking. Returns immediately
     * if events are already queued; otherwise sleeps briefly to yield the
     * CPU while the input threads produce new events. The actual sleep is
     * capped at 50 ms to ensure timely responsiveness.
     *
     * @param timeoutMs maximum wait time in milliseconds
     */
    public static void waitForEvents(int timeoutMs) {
        Screen.flushScreen();

        if (!eventQueue.isEmpty()) {
            return;
        }

        try {
            Thread.sleep(Math.max(1, Math.min(timeoutMs, 50)));
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Dequeues the next mouse event into {@code event}, or sets
     * {@code event.what} to {@code evNothing} if no mouse event is queued.
     * <p>
     * If the dequeued event is an {@code evMouseMove}, any subsequent
     * {@code evMouseMove} events already in the queue are consumed and the
     * last one's position is used. This coalesces rapid move events produced
     * by the OS so that drag loops stay in sync with the real cursor position
     * without falling behind.
     *
     * @param event the target event object to fill in
     */
    public static void getMouseEvent(JtvEvent event) {
        JtvEvent queued = peekEvent(evMouse);
        if (queued == null) {
            event.setWhat(evNothing);
            return;
        }
        event.copyFrom(queued);

        if (event.getWhat() == evMouseMove) {
            JtvEvent next;
            while ((next = peekEvent(evMouseMove)) != null) {
                event.copyFrom(next);
            }
        }
    }

    /**
     * Dequeues the next keyboard event into {@code event}, or sets
     * {@code event.what} to {@code evNothing} if no keyboard event is queued.
     *
     * @param event the target event object to fill in
     * @return {@code event} for convenience
     */
    public static JtvEvent getKeyEvent(JtvEvent event) {
        JtvEvent queued = peekEvent(evKeyboard);
        if (queued != null) {
            event.copyFrom(queued);
        }
        else {
            event.setWhat(evNothing);
        }
        return event;
    }

    /**
     * Removes and returns the first event in {@link #eventQueue} whose
     * {@code what} field has any bit in {@code mask} set, or returns
     * {@code null} if no matching event is found.
     *
     * @param mask the event type bitmask to match (e.g. {@code evMouse},
     *             {@code evKeyboard})
     * @return the first matching event, removed from the queue, or
     *         {@code null}
     */
    private static JtvEvent peekEvent(int mask) {
        for (java.util.Iterator<JtvEvent> it = eventQueue.iterator(); it.hasNext(); ) {
            JtvEvent e = it.next();
            if ((e.getWhat() & mask) != 0) {
                it.remove();
                return e;
            }
        }
        return null;
    }

    /**
     * No-op wake-up hint. The input threads run continuously, so no
     * explicit wake-up signal is needed.
     */
    public static void wakeUp() {
        // No-op, the input thread is always running
    }

    /**
     * Suspends event processing and restores the terminal to cooked mode.
     * Used before spawning a child process that needs normal terminal I/O.
     * Call {@link #resume()} to re-initialise afterwards.
     */
    public static void suspend() {
        stopInputThreads(false);
        disableRawTerminalMode();
    }

    /**
     * Re-initialises the event queue after a previous {@link #suspend()}.
     * If the queue is already running, does nothing.
     */
    public static void resume() {
        if (!running) {
            init();
        }
    }

    /**
     * Signals the input threads to stop, optionally closes the input stream,
     * and waits up to 250 ms for each thread to terminate. Clears the byte
     * queue and nulls the thread references.
     *
     * @param forceCloseInputStream {@code true} to unconditionally close the
     *                              stream; {@code false} to close only if
     *                              {@link #closeInputStreamOnStop} is set
     */
    private static void stopInputThreads(boolean forceCloseInputStream) {
        running = false;

        // Closing the stream unblocks readerLoop() even if no input arrives.
        closeInputStream(forceCloseInputStream);

        if (inputThread != null) {
            inputThread.interrupt();
        }
        if (readerThread != null) {
            readerThread.interrupt();
        }

        joinThread(inputThread);
        joinThread(readerThread);

        inputThread = null;
        readerThread = null;
        byteQueue.clear();
    }

    /**
     * Waits for {@code thread} to terminate, with a 250 ms timeout.
     * Returns immediately if {@code thread} is {@code null} or is the
     * calling thread itself.
     *
     * @param thread the thread to join
     */
    private static void joinThread(Thread thread) {
        if (thread == null || thread == Thread.currentThread()) {
            return;
        }
        try {
            thread.join(250);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Opens the terminal input stream, preferring {@code /dev/tty} over
     * {@code System.in} when it is available. {@code /dev/tty} always refers
     * to the controlling terminal even when stdin is redirected, and it can
     * be closed without affecting the JVM's standard streams.
     *
     * @return the opened {@link InputStream}
     */
    private static InputStream openInputStream() {
        if (ttyDevice.exists()) {
            try {
                closeInputStreamOnStop = true;
                return new FileInputStream(ttyDevice);
            } catch (IOException ignored) {
            }
        }

        closeInputStreamOnStop = false;
        return System.in;
    }

    /**
     * Closes {@link #inputStream} if appropriate. The stream is closed when
     * either {@link #closeInputStreamOnStop} is set (i.e. it was opened from
     * {@code /dev/tty}) or {@code forceClose} is {@code true}. After closing,
     * {@link #inputStream} is set to {@code null}.
     *
     * @param forceClose {@code true} to close regardless of
     *                   {@link #closeInputStreamOnStop}
     */
    private static void closeInputStream(boolean forceClose) {
        if (inputStream == null) {
            return;
        }

        if (closeInputStreamOnStop || forceClose) {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }

        inputStream = null;
        closeInputStreamOnStop = false;
    }

    /**
     * Enables raw terminal input mode so that keystrokes arrive immediately
     * without line buffering or echo.
     * <p>
     * On Unix, saves the current terminal settings with {@code stty -g} and
     * then applies {@code stty raw -echo min 1 time 0}.
     * On Windows, saves the current console input mode via
     * {@code GetConsoleMode} and sets raw-mode flags via
     * {@code SetConsoleMode}.
     * Does nothing if raw mode is already active.
     */
    private static void enableRawTerminalMode() {
        if (rawTerminalEnabled) {
            return;
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            if (enableWindowsRawInput()) {
                rawTerminalEnabled = true;
            }
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

    /**
     * Restores the terminal to its pre-raw-mode state.
     * <p>
     * On Unix, passes the saved {@link #savedTerminalState} string back to
     * {@code stty}. On Windows, calls {@code SetConsoleMode} with the saved
     * {@link #savedWindowsInputMode} value.
     * Does nothing if raw mode is not currently active.
     */
    private static void disableRawTerminalMode() {
        if (!rawTerminalEnabled) {
            return;
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            if (restoreWindowsInputMode()) {
                rawTerminalEnabled = false;
            }
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

    /**
     * Saves the current Windows console input mode and applies raw-mode
     * flags: clears {@link #WIN_ENABLE_LINE_INPUT},
     * {@link #WIN_ENABLE_ECHO_INPUT}, {@link #WIN_ENABLE_PROCESSED_INPUT},
     * and {@link #WIN_ENABLE_QUICK_EDIT_MODE}; sets
     * {@link #WIN_ENABLE_MOUSE_INPUT}, {@link #WIN_ENABLE_EXTENDED_FLAGS},
     * and {@link #WIN_ENABLE_WINDOW_INPUT}.
     *
     * @return {@code true} if the mode was set successfully
     */
    private static boolean enableWindowsRawInput() {
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
            newMode |= WIN_ENABLE_MOUSE_INPUT | WIN_ENABLE_EXTENDED_FLAGS
                | WIN_ENABLE_WINDOW_INPUT;

            return Kernel32.SetConsoleMode(handle, newMode) != 0;
        } catch (LinkageError ignored) {
            return false;
        }
    }

    /**
     * Restores the Windows console input mode to the value saved by
     * {@link #enableWindowsRawInput()}. Does nothing if no mode was saved.
     *
     * @return {@code true} if the mode was restored successfully or no save
     *         was pending
     */
    private static boolean restoreWindowsInputMode() {
        if (!windowsInputModeSaved) {
            return true;
        }
        try {
            long handle = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
            int result = Kernel32.SetConsoleMode(handle, savedWindowsInputMode);
            windowsInputModeSaved = false;
            return result != 0;
        } catch (LinkageError ignored) {
            return false;
        }
    }

    /**
     * Runs {@code stty} with the given arguments, redirecting input from
     * {@link #ttyDevice}. Returns {@code true} if the process exits with
     * code 0.
     *
     * @param args the arguments to pass to {@code stty}
     * @return {@code true} on success
     */
    private static boolean runStty(String... args) {
        return runSttyInternal(false, args) != null;
    }

    /**
     * Runs {@code stty} with the given arguments and returns its trimmed
     * stdout output, or {@code null} on failure. Used with {@code "-g"} to
     * capture the current terminal settings.
     *
     * @param args the arguments to pass to {@code stty}
     * @return the trimmed stdout string, or {@code null} on failure
     */
    private static String runSttyAndCapture(String... args) {
        String output = runSttyInternal(true, args);
        if (output == null) {
            return null;
        }
        return output.trim();
    }

    /**
     * Internal helper that runs {@code stty} as a subprocess, optionally
     * capturing stdout. Returns {@code null} if {@link #ttyDevice} does not
     * exist, the process could not be started, or it exits non-zero.
     *
     * @param captureOutput {@code true} to return stdout as a string;
     *                      {@code false} to return an empty string on success
     * @param args          arguments to append after "stty"
     * @return the captured output, {@code ""} when {@code captureOutput} is
     *         {@code false} and the process succeeded, or {@code null} on
     *         failure
     */
    private static String runSttyInternal(boolean captureOutput, String... args) {
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

            if (captureOutput) {
                return new String(output.toByteArray(), StandardCharsets.US_ASCII);
            }
            return "";
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return null;
        }
        catch (IOException ignored) {
            return null;
        }
    }

    /**
     * Unix reader thread body. Reads bytes from {@link #inputStream} one at
     * a time and offers each to {@link #byteQueue}. Exits when
     * {@link #running} becomes {@code false} or the stream reaches EOF
     * (returns -1), which happens when the stream is closed by
     * {@link #stopInputThreads}.
     */
    private static void readerLoop() {
        try {
            while (running) {
                int b = inputStream.read();
                if (b < 0) break;
                byteQueue.offer(b);
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Unix input-decoding thread body. Polls {@link #byteQueue} for bytes
     * (with a 50 ms timeout to allow orderly shutdown) and passes each
     * leading byte to {@link #processInput(int)} for escape-sequence parsing
     * and event generation.
     */
    private static void inputLoop() {
        try {
            while (running) {
                Integer b = byteQueue.poll(50, java.util.concurrent.TimeUnit.MILLISECONDS);
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

    /**
     * Parses one input byte (or the start of a multi-byte sequence) and
     * pushes zero or more {@link JtvEvent} objects into {@link #eventQueue}.
     * <p>
     * Dispatch table:
     * <ul>
     *   <li>{@code 0x1B} — start of an ANSI escape sequence or plain Escape.
     *       Reads the next byte with a {@link #ESC_SEQUENCE_TIMEOUT_MS}
     *       timeout: {@code [} → CSI sequence, {@code O} → SS3 sequence,
     *       timeout/other → plain Escape or Alt+key.</li>
     *   <li>{@code '\r'} / {@code '\n'} — Enter key.</li>
     *   <li>{@code '\t'} — Tab key.</li>
     *   <li>{@code 127} / {@code 8} — Backspace key.</li>
     *   <li>{@code < 32} — Ctrl+letter combination.</li>
     *   <li>High bit set ({@code >= 0x80}) — start of a UTF-8 multi-byte
     *       character, handled by {@link #handleUtf8Input}.</li>
     *   <li>All other values — printable ASCII character.</li>
     * </ul>
     *
     * @param firstByte the first byte of the input sequence
     * @throws IOException if reading continuation bytes fails
     */
    private static void processInput(int firstByte) throws IOException {
        if (firstByte == 0x1B) {
            // Escape sequence
            int second = readByteWithTimeout(ESC_SEQUENCE_TIMEOUT_MS);
            if (second < 0) {
                // Plain Escape
                pushKeyEvent(KeyEvent.VK_ESCAPE, 0, '\033');
            }
            else if (second == '[') {
                parseCSI();
            }
            else if (second == 'O') {
                parseSS3();
            }
            else {
                // Alt + key
                pushAltKey(second);
            }
        }
        else if (firstByte == '\r' || firstByte == '\n') {
            pushKeyEvent(KeyEvent.VK_ENTER, 0, '\n');
        }
        else if (firstByte == '\t') {
            pushKeyEvent(KeyEvent.VK_TAB, 0, '\t');
        }
        else if (firstByte == 127 || firstByte == 8) {
            pushKeyEvent(KeyEvent.VK_BACK_SPACE, 0, KeyEvent.CHAR_UNDEFINED);
        }
        else if (firstByte < 32) {
            // Ctrl+letter: ASCII control char (1..26) → VK_A..VK_Z
            int vk = (firstByte >= 1 && firstByte <= 26) ? (KeyEvent.VK_A - 1 + firstByte) : 0;
            pushKeyEvent(vk, InputEvent.CTRL_DOWN_MASK, (char) firstByte);
        }
        else {
            if ((firstByte & 0x80) != 0) {
                handleUtf8Input(firstByte);
                return;
            }

            // Regular ASCII character: VK is uppercase ASCII for letters/digits
            char c = (char) firstByte;
            int vk = Character.toUpperCase(c);
            pushKeyEvent(vk, 0, c);
        }
    }

    /**
     * Handles the first byte of a multi-byte UTF-8 sequence. Reads any
     * continuation bytes from {@link #byteQueue}, decodes the full character,
     * and pushes a key event. On macOS, checks whether the decoded code point
     * corresponds to an Option+key combination and, if so, converts it to the
     * appropriate Alt+key code.
     *
     * @param firstByte the high byte that signalled a UTF-8 sequence
     * @throws IOException if reading continuation bytes fails
     */
    private static void handleUtf8Input(int firstByte) throws IOException {
        String text = decodeUtf8Char(firstByte);
        if (text.isEmpty()) {
            return;
        }

        int codePoint = text.codePointAt(0);
        if (SystemUtils.IS_OS_MAC) {
            int altVk = macOptionCodeToAlt(codePoint);
            if (altVk != 0) {
                pushKeyEvent(altVk, InputEvent.ALT_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
                return;
            }
        }

        char keyChar = (codePoint <= Character.MAX_VALUE) ? (char) codePoint : KeyEvent.CHAR_UNDEFINED;
        int vk = (codePoint < 128) ? Character.toUpperCase(codePoint) : 0;
        pushKeyEvent(vk, 0, keyChar);
    }

    /**
     * Maps macOS Option+key Unicode code points to AWT virtual-key codes.
     * Only a small set of well-known macOS US keyboard Option combinations are
     * mapped; all others return {@code 0}.
     *
     * @param codePoint the Unicode code point to test
     * @return the corresponding AWT {@code VK_*} code, or {@code 0}
     */
    private static int macOptionCodeToAlt(int codePoint) {
        switch (codePoint) {
            case 0x2248: // Option+X on macOS US keyboard (≈)
            case 0x02DB: // Shift+Option+X on macOS US keyboard (˛)
                return KeyEvent.VK_X;
            default:
                return 0;
        }
    }

    /**
     * Reads a complete UTF-8 character starting from {@code firstByte},
     * fetching continuation bytes one at a time from {@link #byteQueue} via
     * {@link #readByteWithTimeout}. Falls back to ISO-8859-1 interpretation
     * if a continuation byte is missing or malformed.
     *
     * @param firstByte the leading byte of the UTF-8 sequence
     * @return the decoded character as a {@link String}
     * @throws IOException propagated from {@link #readByteWithTimeout}
     */
    private static String decodeUtf8Char(int firstByte) throws IOException {
        int extraBytes = utf8ExtraByteCount(firstByte);
        if (extraBytes <= 0) {
            return new String(new byte[] {(byte) firstByte}, StandardCharsets.ISO_8859_1);
        }

        byte[] utf8 = new byte[extraBytes + 1];
        utf8[0] = (byte) firstByte;
        for (int i = 1; i < utf8.length; i++) {
            // Bytes are consumed by readerLoop() into byteQueue; read continuation
            // bytes from the same queue to avoid racing on System.in.
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

    /**
     * Returns the number of UTF-8 continuation bytes expected after
     * {@code firstByte}: 1 for a two-byte sequence, 2 for three-byte,
     * 3 for four-byte, or 0 if {@code firstByte} is not a valid UTF-8
     * leading byte.
     *
     * @param firstByte the leading byte to inspect
     * @return the number of continuation bytes (0–3)
     */
    private static int utf8ExtraByteCount(int firstByte) {
        if ((firstByte & 0xE0) == 0xC0) return 1;
        if ((firstByte & 0xF0) == 0xE0) return 2;
        if ((firstByte & 0xF8) == 0xF0) return 3;
        return 0;
    }

    /**
     * Parses an ANSI CSI (Control Sequence Introducer) sequence starting
     * after the already-consumed {@code ESC [}. Reads parameter bytes and
     * the final byte, then dispatches on the final character.
     * <p>
     * Supported sequences:
     * <ul>
     *   <li>Cursor keys: {@code A/B/C/D} (Up/Down/Right/Left), {@code H/F}
     *       (Home/End), {@code Z} (Shift+Tab).</li>
     *   <li>VT tilde sequences ({@code ~}): Insert, Delete, Page Up/Down,
     *       F5–F10, Shift+Insert, Shift/Ctrl+Delete.</li>
     *   <li>Caret sequences ({@code ^}): F10.</li>
     *   <li>Function keys: {@code P/Q/R/S} (F1–F4).</li>
     *   <li>SGR mouse: parameter starting with {@code <}.</li>
     * </ul>
     *
     * @throws IOException if reading bytes fails
     */
    private static void parseCSI() throws IOException {
        StringBuilder params = new StringBuilder();
        int c;
        while (true) {
            c = readByteWithTimeout(ESC_SEQUENCE_TIMEOUT_MS);
            if (c < 0) return;
            if (c >= 0x40 && c <= 0x7E) break;
            params.append((char) c);
        }

        char finalChar = (char) c;
        String paramStr = params.toString();
        int modifiers = csiModifierToAwtModifiers(paramStr);

        // Mouse events: CSI < Pb ; Px ; Py M/m (SGR mouse)
        if (paramStr.startsWith("<")) {
            parseSGRMouse(paramStr.substring(1), finalChar);
            return;
        }

        // Cursor keys and function keys
        switch (finalChar) {
            case 'A': pushKeyEvent(KeyEvent.VK_UP, modifiers, KeyEvent.CHAR_UNDEFINED); break;
            case 'B': pushKeyEvent(KeyEvent.VK_DOWN, modifiers, KeyEvent.CHAR_UNDEFINED); break;
            case 'C': pushKeyEvent(KeyEvent.VK_RIGHT, modifiers, KeyEvent.CHAR_UNDEFINED); break;
            case 'D': pushKeyEvent(KeyEvent.VK_LEFT, modifiers, KeyEvent.CHAR_UNDEFINED); break;
            case 'H': pushKeyEvent(KeyEvent.VK_HOME, modifiers, KeyEvent.CHAR_UNDEFINED); break;
            case 'F': pushKeyEvent(KeyEvent.VK_END, modifiers, KeyEvent.CHAR_UNDEFINED); break;
            case 'Z': pushKeyEvent(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK, KeyEvent.CHAR_UNDEFINED); break;
            case '~':
                parseCsiTilde(paramStr);
                break;
            case '^':
                parseCsiCaret(paramStr);
                break;
            case 'P': pushKeyEvent(KeyEvent.VK_F1, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 'Q': pushKeyEvent(KeyEvent.VK_F2, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 'R':
                // Ignore cursor-position reports (ESC [ row ; col R) — only treat bare ESC [ R as F3
                if (paramStr.isEmpty()) {
                    pushKeyEvent(KeyEvent.VK_F3, 0, KeyEvent.CHAR_UNDEFINED);
                }
                break;
            case 'S': pushKeyEvent(KeyEvent.VK_F4, 0, KeyEvent.CHAR_UNDEFINED); break;
            case '[': {
                // Linux console F1-F5: ESC [ [ A-E
                int lc = readByteWithTimeout(ESC_SEQUENCE_TIMEOUT_MS);
                switch (lc) {
                    case 'A': pushKeyEvent(KeyEvent.VK_F1, 0, KeyEvent.CHAR_UNDEFINED); break;
                    case 'B': pushKeyEvent(KeyEvent.VK_F2, 0, KeyEvent.CHAR_UNDEFINED); break;
                    case 'C': pushKeyEvent(KeyEvent.VK_F3, 0, KeyEvent.CHAR_UNDEFINED); break;
                    case 'D': pushKeyEvent(KeyEvent.VK_F4, 0, KeyEvent.CHAR_UNDEFINED); break;
                    case 'E': pushKeyEvent(KeyEvent.VK_F5, 0, KeyEvent.CHAR_UNDEFINED); break;
                }
                break;
            }
        }
    }

    private static int csiModifierToAwtModifiers(String paramStr) {
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
            if ((modifier & 1) != 0) modifiers |= InputEvent.SHIFT_DOWN_MASK;
            if ((modifier & 2) != 0) modifiers |= InputEvent.ALT_DOWN_MASK;
            if ((modifier & 4) != 0) modifiers |= InputEvent.CTRL_DOWN_MASK;
            return modifiers;
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    /**
     * Handles VT tilde CSI sequences ({@code ESC [ Pn ~ }) that encode
     * navigation keys and function keys. An optional modifier field
     * ({@code Pn;modifier~}) is parsed to handle Shift/Ctrl variants such
     * as Shift+Insert and Ctrl+Delete.
     *
     * @param paramStr the parameter string before the {@code ~} final byte
     */
    private static void parseCsiTilde(String paramStr) {
        int code;
        int modifier = 1;
        int separator = paramStr.indexOf(';');
        String firstParam = (separator >= 0) ? paramStr.substring(0, separator) : paramStr;
        if (separator >= 0 && separator + 1 < paramStr.length()) {
            try {
                modifier = Integer.parseInt(paramStr.substring(separator + 1));
            } catch (NumberFormatException ignored) {
                modifier = 1;
            }
        }

        try {
            code = Integer.parseInt(firstParam);
        } catch (NumberFormatException ignored) {
            return;
        }

        switch (code) {
            case 1: pushKeyEvent(KeyEvent.VK_HOME, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 2: pushKeyEvent(KeyEvent.VK_INSERT, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 3: pushKeyEvent(KeyEvent.VK_DELETE, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 4: pushKeyEvent(KeyEvent.VK_END, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 5: pushKeyEvent(KeyEvent.VK_PAGE_UP, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 6: pushKeyEvent(KeyEvent.VK_PAGE_DOWN, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 11: pushKeyEvent(KeyEvent.VK_F1, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 12: pushKeyEvent(KeyEvent.VK_F2, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 13: pushKeyEvent(KeyEvent.VK_F3, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 14: pushKeyEvent(KeyEvent.VK_F4, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 15: pushKeyEvent(KeyEvent.VK_F5, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 17: pushKeyEvent(KeyEvent.VK_F6, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 18: pushKeyEvent(KeyEvent.VK_F7, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 19: pushKeyEvent(KeyEvent.VK_F8, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 20: pushKeyEvent(KeyEvent.VK_F9, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 21: pushKeyEvent(KeyEvent.VK_F10, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 105:
                if (modifier == 2) {
                    pushKeyEvent(KeyEvent.VK_INSERT, InputEvent.SHIFT_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
                }
                break;
            case 102:
                if (modifier == 2) {
                    pushKeyEvent(KeyEvent.VK_DELETE, InputEvent.SHIFT_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
                } else if (modifier == 5) {
                    pushKeyEvent(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
                }
                break;
        }
    }

    /**
     * Handles caret-terminated CSI sequences ({@code ESC [ Pn ^ }).
     * Currently maps code {@code 21} to F10.
     *
     * @param paramStr the numeric parameter before the {@code ^} final byte
     */
    private static void parseCsiCaret(String paramStr) {
        int code;
        try {
            code = Integer.parseInt(paramStr);
        } catch (NumberFormatException ignored) {
            return;
        }

        switch (code) {
            case 21: pushKeyEvent(KeyEvent.VK_F10, 0, KeyEvent.CHAR_UNDEFINED); break;
        }
    }

    /**
     * Parses an SS3 sequence ({@code ESC O <char>}) generated by some
     * terminals for function and cursor keys. Reads the single final byte
     * and maps it to F1–F4 or Home/End.
     *
     * @throws IOException if reading the final byte fails
     */
    private static void parseSS3() throws IOException {
        int c = readByteWithTimeout(ESC_SEQUENCE_TIMEOUT_MS);
        if (c < 0) {
            return;
        }
        switch (c) {
            case 'P': pushKeyEvent(KeyEvent.VK_F1, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 'Q': pushKeyEvent(KeyEvent.VK_F2, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 'R': pushKeyEvent(KeyEvent.VK_F3, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 'S': pushKeyEvent(KeyEvent.VK_F4, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 'H': pushKeyEvent(KeyEvent.VK_HOME, 0, KeyEvent.CHAR_UNDEFINED); break;
            case 'F': pushKeyEvent(KeyEvent.VK_END, 0, KeyEvent.CHAR_UNDEFINED); break;
        }
    }

    /**
     * Polls {@link #byteQueue} for up to {@code timeoutMs} milliseconds and
     * returns the byte, or {@code -1} if none arrives within the timeout or
     * the thread is interrupted.
     *
     * @param timeoutMs the maximum wait time in milliseconds
     * @return the byte value (0–255), or {@code -1} on timeout/interrupt
     * @throws IOException never; declared for uniformity with callers
     */
    private static int readByteWithTimeout(int timeoutMs) throws IOException {
        try {
            Integer b = byteQueue.poll(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
            return b == null ? -1 : b;
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    /**
     * Parses an SGR mouse event sequence ({@code ESC [ < Pb ; Px ; Py M/m}).
     * Decodes button number, position, and event type (press, release, move,
     * or wheel), constructs the appropriate {@link JtvEvent}, and offers it to
     * {@link #eventQueue}. Also performs double-click detection for button-down
     * events using {@link #lastClickTime} and {@link #doubleDelay}.
     *
     * @param params    the parameter string after {@code <} (format:
     *                  {@code "button;x;y"})
     * @param finalChar {@code 'M'} for press/move, {@code 'm'} for release
     */
    private static void parseSGRMouse(String params, char finalChar) {
        // params = "button;x;y"
        String[] parts = params.split(";");
        if (parts.length < 3) return;
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
                // Detect double-click
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
            eventQueue.offer(event);
        } catch (NumberFormatException ignored) {
        }
    }

    /**
     * Converts an SGR button number (0 = left, 1 = middle, 2 = right) to
     * the corresponding JT Vision {@code mbXXXX} button mask constant.
     *
     * @param sgrButton the SGR button index (bits 0–1 of the button byte)
     * @return {@code mbLeftButton}, {@code mbMiddleButton},
     *         {@code mbRightButton}, or {@code 0} for unknown buttons
     */
    private static int toButtonMask(int sgrButton) {
        switch (sgrButton) {
            case 0: return mbLeftButton;
            case 1: return mbMiddleButton;
            case 2: return mbRightButton;
            default: return 0;
        }
    }

    /**
     * Converts a character received after {@code ESC} (Alt+key on Unix) to
     * the corresponding AWT virtual-key code and pushes an Alt+key event.
     *
     * @param ch the character byte following the ESC byte
     */
    private static void pushAltKey(int ch) {
        char upper = Character.toUpperCase((char) ch);
        int vk = (upper >= 'A' && upper <= 'Z') ? upper : 0;
        pushKeyEvent(vk, InputEvent.ALT_DOWN_MASK, KeyEvent.CHAR_UNDEFINED);
    }

    /**
     * Constructs an {@code evKeyDown} {@link JtvEvent} and offers it to
     * {@link #eventQueue}.
     *
     * @param keyCode   AWT virtual-key code ({@link KeyEvent}{@code .VK_*})
     * @param modifiers AWT modifier mask
     *                  ({@link InputEvent}{@code .*_DOWN_MASK})
     * @param keyChar   Unicode character produced by the keystroke, or
     *                  {@link KeyEvent#CHAR_UNDEFINED} for non-printable keys
     */
    private static void pushKeyEvent(int keyCode, int modifiers, char keyChar) {
        JtvEvent event = new JtvEvent();
        event.setKeyDownEvent(keyCode, modifiers, keyChar);
        eventQueue.offer(event);
    }

    /**
     * Windows event-loop thread body. Waits on the console input handle via
     * {@code WaitForSingleObject} (50 ms timeout) and reads available
     * {@code INPUT_RECORD} structures. Dispatches each record to
     * {@link #handleWindowsKeyEvent} or {@link #handleWindowsMouseEvent}.
     * Exits when {@link #running} becomes {@code false} or a
     * {@code LinkageError} is thrown (Jansi unavailable).
     */
    private static void windowsEventLoop() {
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
        catch (IOException ignored) {
        }
        catch (LinkageError ignored) {
        }
    }

    /**
     * Translates a Windows {@code KEY_EVENT_RECORD} into a JT Vision
     * {@code evKeyDown} event and offers it to {@link #eventQueue}.
     * Key-down events only; key-up records are silently discarded.
     * <p>
     * Modifier detection: Shift, Ctrl, and Alt flags are read from
     * {@code controlKeyState}. AltGr (LeftCtrl + RightAlt) is treated
     * as a regular character modifier and not as separate Alt/Ctrl presses.
     * Non-ASCII characters (e.g. Cyrillic) have their scan-code portion
     * zeroed to avoid accidental matches with {@code kbAlt*} hotkeys.
     *
     * @param ev the Windows key event record
     */
    private static void handleWindowsKeyEvent(KEY_EVENT_RECORD ev) {
        if (!ev.keyDown) return;

        int vk = ev.keyCode & 0xFFFF;
        char uc = ev.uchar;
        int winCs = ev.controlKeyState;

        boolean shift = (winCs & KEY_EVENT_RECORD.SHIFT_PRESSED) != 0;
        boolean leftCtrl = (winCs & KEY_EVENT_RECORD.LEFT_CTRL_PRESSED) != 0;
        boolean rightCtrl = (winCs & KEY_EVENT_RECORD.RIGHT_CTRL_PRESSED) != 0;
        boolean leftAlt = (winCs & KEY_EVENT_RECORD.LEFT_ALT_PRESSED) != 0;
        boolean rightAlt = (winCs & KEY_EVENT_RECORD.RIGHT_ALT_PRESSED) != 0;
        // AltGr on Windows reports as LeftCtrl+RightAlt and produces a printable char.
        boolean altGr = leftCtrl && rightAlt;
        boolean ctrl = (leftCtrl || rightCtrl) && !altGr;
        boolean alt = (leftAlt || rightAlt) && !altGr;

        int modifiers = 0;
        if (shift) modifiers |= InputEvent.SHIFT_DOWN_MASK;
        if (ctrl)  modifiers |= InputEvent.CTRL_DOWN_MASK;
        if (alt)   modifiers |= InputEvent.ALT_DOWN_MASK;

        // Named special keys (navigation, function, control)
        int special = winSpecialVkToAwtVk(vk);
        if (special != 0) {
            pushKeyEvent(special, modifiers, KeyEvent.CHAR_UNDEFINED);
            return;
        }

        // Alt + letter / digit (Windows VK matches AWT VK for letters/digits)
        if (alt && ((vk >= 'A' && vk <= 'Z') || (vk >= '0' && vk <= '9'))) {
            pushKeyEvent(vk, modifiers, KeyEvent.CHAR_UNDEFINED);
            return;
        }

        // Ctrl + control char: Ctrl+A (uc=0x01) → VK_A (65)
        if (ctrl && uc > 0 && uc < 32) {
            int letterVk = (KeyEvent.VK_A - 1) + uc;
            pushKeyEvent(letterVk, modifiers, uc);
            return;
        }

        // Printable char
        if (uc >= 32 && uc != 127) {
            int charVk = (uc < 128) ? Character.toUpperCase((int) uc) : 0;
            pushKeyEvent(charVk, modifiers, uc);
        }
    }

    /**
     * Maps a Windows virtual-key code to its AWT {@code VK_*} equivalent for
     * navigation, function, and other named special keys.  Returns {@code 0}
     * if {@code winVk} is a printable-character key that should be handled by
     * the caller via {@link KEY_EVENT_RECORD#uchar}.
     *
     * @param winVk the Windows virtual-key code
     * @return the AWT {@code VK_*} constant, or {@code 0} if unmapped
     */
    private static int winSpecialVkToAwtVk(int winVk) {
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
            default: return 0;
        }
    }

    /**
     * Translates a Windows {@code MOUSE_EVENT_RECORD} into a JT Vision
     * mouse event and offers it to {@link #eventQueue}. Classifies the event
     * as {@code evMouseWheel}, {@code evMouseMove}, {@code evMouseDown}, or
     * {@code evMouseUp} based on the Windows event flags and button-state
     * transition relative to {@link #lastMouse}. Updates {@link #lastMouse}
     * after each event.
     *
     * @param ev the Windows mouse event record
     */
    private static void handleWindowsMouseEvent(MOUSE_EVENT_RECORD ev) {
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

        boolean wheeled = (ev.eventFlags & MOUSE_EVENT_RECORD.MOUSE_WHEELED) != 0;
        boolean moved = (ev.eventFlags & MOUSE_EVENT_RECORD.MOUSE_MOVED) != 0;
        boolean dblClick = (ev.eventFlags & MOUSE_EVENT_RECORD.DOUBLE_CLICK) != 0;

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
        lastMouse = new MouseEvent(new JtvPoint(where), 0, 0, buttons, 0);
        eventQueue.offer(event);
    }
}
