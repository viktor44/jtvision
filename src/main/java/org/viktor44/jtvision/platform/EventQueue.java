/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import static org.viktor44.jtvision.core.EventCodes.evKeyboard;
import static org.viktor44.jtvision.core.EventCodes.evMouse;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.EventCodes.evNothing;
import static org.viktor44.jtvision.core.EventCodes.evPaste;
import static org.viktor44.jtvision.core.EventCodes.mbLeftButton;
import static org.viktor44.jtvision.core.EventCodes.mbMiddleButton;
import static org.viktor44.jtvision.core.EventCodes.mbRightButton;

import java.util.concurrent.LinkedBlockingQueue;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.MouseEvent;
import org.viktor44.jtvision.util.SystemUtils;

/**
 * Platform-level input event queue (singleton).
 * <p>
 * The concrete implementation is selected at startup by {@link #initInstance()}:
 * {@link WinEventQueue} on Windows, {@link MacosEventQueue} on macOS, and
 * {@link LinuxEventQueue} on all other Unix platforms.
 *
 * @see WinEventQueue
 * @see LinuxEventQueue
 * @see MacosEventQueue
 * @see Screen
 * @see JtvEvent
 */
public abstract class EventQueue {

    private static EventQueue INSTANCE;

    protected EventQueue() {}

    /**
     * Initialises the singleton with the correct platform implementation.
     * Must be called before {@link #getInstance()}.
     */
    public static void initInstance() {
        EventQueue instance;
        if (SystemUtils.IS_OS_WINDOWS) {
            instance = new WinEventQueue();
        }
        else if (SystemUtils.IS_OS_MAC) {
            instance = new MacosEventQueue();
        }
        else {
            instance = new LinuxEventQueue();
        }
        INSTANCE = instance;
        INSTANCE.init();
    }

    /**
     * Initialises the singleton with a custom instance, allowing tests to
     * substitute alternative implementations.
     *
     * @param instance the instance to use; must not be {@code null}
     */
    static void initInstance(EventQueue instance) {
        INSTANCE = instance;
    }

    /** Returns the singleton {@code EventQueue} instance. */
    public static EventQueue getInstance() {
        return INSTANCE;
    }

    // ------------------------------------------------------------------
    // Shared state
    // ------------------------------------------------------------------

    private final LinkedBlockingQueue<JtvEvent> eventQueue = new LinkedBlockingQueue<>();

    /** Event-decoding thread. */
    protected Thread inputThread;

    /** Raw-byte reader thread (Unix) or combined reader+decoder (Windows). */
    protected Thread readerThread;

    /** {@code true} while the input threads are running. */
    protected volatile boolean running = false;

    /** {@code true} after raw terminal mode has been successfully enabled. */
    protected boolean rawTerminalEnabled = false;

    /** Mouse tracking state, shared by Unix SGR and Windows mouse handlers. */
    protected MouseEvent lastMouse = new MouseEvent();
    protected long lastClickTime = 0;
    protected int clickCount = 0;

    /**
     * Maximum time between two mouse-down events (milliseconds) to qualify
     * as a double-click. Defaults to 400 ms.
     */
    public int doubleDelay = 400;

    // ------------------------------------------------------------------
    // Platform hooks — implemented by subclasses
    // ------------------------------------------------------------------

    /** Initialises the event queue and starts the input thread(s). */
    protected abstract void init();

    /**
     * Enables raw terminal input mode so that keystrokes arrive immediately.
     * <p>
     * Unix: saves terminal settings and applies {@code stty raw -echo}.
     * Windows: saves and changes console mode flags via {@code SetConsoleMode}.
     */
    protected abstract void enableRawTerminalMode();

    /**
     * Restores the terminal to its pre-raw-mode state.
     */
    protected abstract void disableRawTerminalMode();

    /**
     * Called by {@link #stopInputThreads} before threads are interrupted.
     * Subclasses should close streams or signal conditions that unblock blocked reads.
     *
     * @param forceClose whether to force-close any open stream
     */
    protected void beforeStopThreads(boolean forceClose) {}

    /**
     * Called by {@link #stopInputThreads} after all threads have been joined.
     * Subclasses should clear any intermediate byte queues here.
     */
    protected void afterStopThreads() {}

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /**
     * Shuts down the event queue and restores the terminal to its original state.
     */
    public void shutdown() {
        stopInputThreads(true);
        disableRawTerminalMode();
    }

    /**
     * Waits for at least one event to become available, up to {@code timeoutMs}
     * milliseconds. Always flushes pending screen updates first.
     *
     * @param timeoutMs maximum wait time in milliseconds
     */
    public void waitForEvents(int timeoutMs) {
        Screen.flushScreen();
        if (!eventQueue.isEmpty()) {
            return;
        }
        try {
            Thread.sleep(Math.max(1, Math.min(timeoutMs, 50)));
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Dequeues the next mouse event into {@code event}, or sets
     * {@code event.what} to {@code evNothing} if no mouse event is queued.
     * Coalesces consecutive {@code evMouseMove} events.
     *
     * @param event the target event object to fill in
     */
    public void getMouseEvent(JtvEvent event) {
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
    public JtvEvent getKeyEvent(JtvEvent event) {
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
     * No-op wake-up hint. Input threads run continuously.
     */
    public void wakeUp() {}

    /**
     * Suspends event processing and restores the terminal to cooked mode.
     * Call {@link #resume()} to re-initialise afterwards.
     */
    public void suspend() {
        stopInputThreads(false);
        disableRawTerminalMode();
    }

    /**
     * Re-initialises the event queue after a previous {@link #suspend()}.
     */
    public void resume() {
        if (!running) {
            init();
        }
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    /**
     * Signals all input threads to stop, waits for them to finish, and
     * clears thread references.
     *
     * @param forceCloseInputStream passed to {@link #beforeStopThreads}
     */
    protected void stopInputThreads(boolean forceCloseInputStream) {
        running = false;
        beforeStopThreads(forceCloseInputStream);
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
        afterStopThreads();
    }

    private void joinThread(Thread thread) {
        if (thread == null || thread == Thread.currentThread()) {
        	return;
        }
        try {
            thread.join(250);
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private JtvEvent peekEvent(int mask) {
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
     * Constructs an {@code evKeyDown} event and places it in the queue.
     *
     * @param keyCode   AWT virtual-key code
     * @param modifiers AWT modifier mask
     * @param keyChar   Unicode character, or {@link java.awt.event.KeyEvent#CHAR_UNDEFINED}
     */
    protected void pushKeyEvent(int keyCode, int modifiers, char keyChar) {
        JtvEvent event = new JtvEvent();
        event.setKeyDownEvent(keyCode, modifiers, keyChar);
        eventQueue.offer(event);
    }

    /**
     * Constructs an {@code evPaste} event carrying the pasted text and
     * places it in the queue.  Used by the bracketed-paste handler.
     *
     * @param text the clipboard text delivered by the terminal
     */
    protected void pushPasteEvent(String text) {
        JtvEvent event = new JtvEvent();
        event.setPasteEvent(text);
        eventQueue.offer(event);
    }

    /**
     * Places a pre-constructed event in the event queue.
     *
     * @param event the event to enqueue
     */
    protected void offerEvent(JtvEvent event) {
        eventQueue.offer(event);
    }

    /**
     * Converts an SGR mouse button index to the JT Vision button-mask constant.
     *
     * @param sgrButton SGR button index (0=left, 1=middle, 2=right)
     * @return {@code mbLeftButton}, {@code mbMiddleButton}, {@code mbRightButton}, or {@code 0}
     */
    protected int toButtonMask(int sgrButton) {
        switch (sgrButton) {
            case 0:  return mbLeftButton;
            case 1:  return mbMiddleButton;
            case 2:  return mbRightButton;
            default: return 0;
        }
    }
}
