/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmCommandSetChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.CommandCodes.cmQuit;
import static org.viktor44.jtvision.core.CommandCodes.cmReleasedFocus;
import static org.viktor44.jtvision.core.CommandCodes.cmSelectWindowNum;
import static org.viktor44.jtvision.core.CommandCodes.cmValid;
import static org.viktor44.jtvision.core.CommandCodes.cmZoom;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evNothing;
import static org.viktor44.jtvision.core.ViewFlags.apBlackWhite;
import static org.viktor44.jtvision.core.ViewFlags.apColor;
import static org.viktor44.jtvision.core.ViewFlags.apMonochrome;
import static org.viktor44.jtvision.core.ViewFlags.sfExposed;
import static org.viktor44.jtvision.core.ViewFlags.sfFocused;
import static org.viktor44.jtvision.core.ViewFlags.sfModal;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;
import static org.viktor44.jtvision.core.ViewFlags.sfVisible;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.menus.JtvMenu;
import org.viktor44.jtvision.menus.JtvMenuBar;
import org.viktor44.jtvision.menus.JtvStatusDef;
import org.viktor44.jtvision.menus.JtvStatusItem;
import org.viktor44.jtvision.menus.JtvStatusLine;
import org.viktor44.jtvision.platform.EventQueue;
import org.viktor44.jtvision.platform.Screen;
import org.viktor44.jtvision.util.SystemUtils;
import org.viktor44.jtvision.views.JtvGroup;
import org.viktor44.jtvision.views.JtvView;
import org.viktor44.jtvision.views.JtvWindow;

/**
 * Abstract base class for Turbo Vision applications.
 *
 * <p>{@code TProgram} is the top-level group that fills the entire screen and owns three
 * standard subviews: a {@link JtvMenuBar} at the top, a {@link JtvDesktop} in the middle, and a
 * {@link JtvStatusLine} at the bottom.  These are created via injectable factory functions so
 * that applications can supply custom implementations without subclassing the factories.
 *
 * <p>The event loop (inherited from {@code TGroup.execute()}) runs until {@code endModal} is
 * called.  {@code TProgram.getEvent()} polls the platform event queue, dispatches key events
 * to the status line, and calls {@link #idle()} when no events are waiting.
 *
 * <p>Applications should normally subclass {@link JtvApplication} rather than {@code TProgram}
 * directly, since {@code TApplication} also initialises the platform Screen and EventQueue
 * subsystems.
 */
public abstract class JtvProgram extends JtvGroup {

    /** The current application's status line, or {@code null} if none. */
    private static JtvStatusLine statusLine;

	/** The current application's menu bar, or {@code null} if none. */
    private static JtvMenuBar menuBar;

    /** The current application's desktop, or {@code null} if none. */
    private static JtvDesktop desktop;

    /** Reference to the running {@code TProgram} instance (set in the constructor). */
    private static JtvProgram application;

    /** Shared clipboard: system clipboard when available, local fallback otherwise. */
    private static final Clipboard clipboard = initClipboard();

	/**
     * The active color palette mode.  One of the {@code ap*} constants from
     * {@link ViewFlags} (e.g. {@code apColor}, {@code apBlackWhite}, {@code apMonochrome}).
     */
    public static int appPalette = apColor;

    /**
     * Maximum milliseconds to wait for a platform event before calling {@link #idle()}.
     * Default is {@code 20} ms.
     */
    public static int eventTimeoutMs = 20;

    /** Buffered event injected by {@link #putEvent} and consumed on the next {@link #getEvent} call. */
    protected JtvEvent pending = new JtvEvent();

    /** Master application palette — 135 entries for color mode. */
    private static final JtvPalette cpAppColor = new JtvPalette(
    		new int[] {
			        0x71, 0x70, 0x78, 0x74, 0x20, 0x28, 0x24, 0x17, 0x1F, 0x1A, 0x31, 0x31, 0x1E, 0x71, 0x1F,       // 1-15
			        0x37, 0x3F, 0x3A, 0x13, 0x13, 0x3E, 0x21, 0x3F, 0x70, 0x7F, 0x7A, 0x13, 0x13, 0x70, 0x7F, 0x7E, // 16-31
			        0x70, 0x7F, 0x7A, 0x13, 0x13, 0x70, 0x70, 0x7F, 0x7E, 0x20, 0x2B, 0x2F, 0x78, 0x2E, 0x70, 0x30, // 32-47
			        0x3F, 0x3E, 0x1F, 0x2F, 0x1A, 0x20, 0x72, 0x31, 0x31, 0x30, 0x2F, 0x3E, 0x31, 0x13, 0x38, 0x00, // 48-63
			        0x17, 0x1F, 0x1A, 0x71, 0x71, 0x1E, 0x17, 0x1F, 0x1E, 0x20, 0x2B, 0x2F, 0x78, 0x2E, 0x10, 0x30, // 64-79
			        0x3F, 0x3E, 0x70, 0x2F, 0x7A, 0x20, 0x12, 0x31, 0x31, 0x30, 0x2F, 0x3E, 0x31, 0x13, 0x38, 0x00, // 80-95
			        0x37, 0x3F, 0x3A, 0x13, 0x13, 0x3E, 0x30, 0x3F, 0x3E, 0x20, 0x2B, 0x2F, 0x78, 0x2E, 0x30, 0x70, // 96-111
			        0x7F, 0x7E, 0x1F, 0x2F, 0x1A, 0x20, 0x32, 0x31, 0x71, 0x70, 0x2F, 0x7E, 0x71, 0x13, 0x78, 0x00, // 112-127
			        0x37, 0x3F, 0x3A, 0x13, 0x13, 0x30, 0x3E, 0x1E                                                  // 128-135
		    }
    );

    /** Master application palette — 135 entries for black-and-white mode. */
    private static final JtvPalette cpAppBlackWhite = new JtvPalette(
            new int[] {
                    0x70, 0x70, 0x78, 0x7F, 0x07, 0x07, 0x0F, 0x07, 0x0F, 0x07, 0x70, 0x70, 0x07, 0x70, 0x0F, 		// 1-15
                    0x07, 0x0F, 0x07, 0x70, 0x70, 0x07, 0x70, 0x0F, 0x70, 0x7F, 0x7F, 0x70, 0x07, 0x70, 0x07, 0x0F, // 16-31
                    0x70, 0x7F, 0x7F, 0x70, 0x07, 0x70, 0x70, 0x7F, 0x7F, 0x07, 0x0F, 0x0F, 0x78, 0x0F, 0x78, 0x07, // 32-47
                    0x0F, 0x0F, 0x0F, 0x70, 0x0F, 0x07, 0x70, 0x70, 0x70, 0x07, 0x70, 0x0F, 0x07, 0x07, 0x08, 0x00, // 48-63
                    0x07, 0x0F, 0x0F, 0x07, 0x70, 0x07, 0x07, 0x0F, 0x0F, 0x70, 0x78, 0x7F, 0x08, 0x7F, 0x08, 0x70, // 64-79
                    0x7F, 0x7F, 0x7F, 0x0F, 0x70, 0x70, 0x07, 0x70, 0x70, 0x70, 0x07, 0x7F, 0x70, 0x07, 0x78, 0x00, // 80-95
                    0x70, 0x7F, 0x7F, 0x70, 0x07, 0x70, 0x70, 0x7F, 0x7F, 0x07, 0x0F, 0x0F, 0x78, 0x0F, 0x78, 0x07, // 96-111
                    0x0F, 0x0F, 0x0F, 0x70, 0x0F, 0x07, 0x70, 0x70, 0x70, 0x07, 0x70, 0x0F, 0x07, 0x07, 0x08, 0x00, // 112-127
                    0x07, 0x0F, 0x07, 0x70, 0x70, 0x07, 0x0F, 0x70                                                  // 128-135
            }
    );

    /** Master application palette — 135 entries for monochrome mode. */
    private static final JtvPalette cpAppMonochrome = new JtvPalette(
            new int[] {
                    0x70, 0x07, 0x07, 0x0F, 0x70, 0x70, 0x70, 0x07, 0x0F, 0x07, 0x70, 0x70, 0x07, 0x70, 0x00, 		// 1-15
                    0x07, 0x0F, 0x07, 0x70, 0x70, 0x07, 0x70, 0x00, 0x70, 0x70, 0x70, 0x07, 0x07, 0x70, 0x07, 0x00, // 16-31
                    0x70, 0x70, 0x70, 0x07, 0x07, 0x70, 0x70, 0x70, 0x0F, 0x07, 0x07, 0x0F, 0x70, 0x0F, 0x70, 0x07, // 32-47
                    0x0F, 0x0F, 0x07, 0x70, 0x07, 0x07, 0x70, 0x07, 0x07, 0x07, 0x70, 0x0F, 0x07, 0x07, 0x70, 0x00, // 48-63
                    0x70, 0x70, 0x70, 0x07, 0x07, 0x70, 0x70, 0x70, 0x0F, 0x07, 0x07, 0x0F, 0x70, 0x0F, 0x70, 0x07, // 64-79
                    0x0F, 0x0F, 0x07, 0x70, 0x07, 0x07, 0x70, 0x07, 0x07, 0x07, 0x70, 0x0F, 0x07, 0x07, 0x01, 0x00, // 80-95
                    0x70, 0x70, 0x70, 0x07, 0x07, 0x70, 0x70, 0x70, 0x0F, 0x07, 0x07, 0x0F, 0x70, 0x0F, 0x70, 0x07, // 96-111
                    0x0F, 0x0F, 0x07, 0x70, 0x07, 0x07, 0x70, 0x07, 0x07, 0x07, 0x70, 0x0F, 0x07, 0x07, 0x01, 0x00, // 112-127
                    0x07, 0x0F, 0x07, 0x70, 0x70, 0x07, 0x0F, 0x70                                                  // 128-135
            }
    );

    /** User-supplied palette that overrides {@link #cpAppColor} when non-{@code null}. */
    private static JtvPalette customAppPalette;

    /**
     * Constructs a {@code TProgram}.  Creates the standard subviews by calling
     * {@link #initDesktop(JtvRect)}, {@link #initStatusLine(JtvRect)}, and
     * {@link #initMenuBar(JtvRect)}, which subclasses may override to supply custom
     * implementations.  A method may return {@code null} to omit that subview.
     */
    public JtvProgram() {
        super(new JtvRect(0, 0, Screen.screenWidth, Screen.screenHeight));
        application = this;
        initScreen();
        state = sfVisible | sfSelected | sfFocused | sfModal | sfExposed;
        options = 0;
        buffer = new JtvDrawBuffer(Screen.screenBuffer);

        JtvDesktop dt = initDesktop(getExtent());
        if (dt != null) {
            desktop = dt;
            insert(desktop);
        }

        JtvStatusLine sl = initStatusLine(getExtent());
        if (sl != null) {
            statusLine = sl;
            insert(statusLine);
        }

        JtvMenuBar mb = initMenuBar(getExtent());
        if (mb != null) {
            menuBar = mb;
            insert(menuBar);
        }
    }

    /** Nulls the three subview references then delegates to {@code TGroup.shutDown()}. */
    @Override
    public void shutDown() {
        statusLine = null;
        menuBar = null;
        desktop = null;
        super.shutDown();
    }

    /**
     * Returns {@code true} if keyboard focus can be moved away from the currently focused window
     * (i.e. the desktop accepts a {@code cmReleasedFocus} validation).
     *
     * @return {@code true} if focus can be transferred
     */
    public boolean canMoveFocus() {
        return desktop != null && desktop.valid(cmReleasedFocus);
    }

    /**
     * Executes a dialog modally on the desktop.  Optionally transfers data to the dialog before
     * execution and retrieves the result afterward.
     *
     * @param pD   the dialog to execute; destroyed after execution
     * @param data optional data object passed to {@code setDataFrom} / {@code getDataTo}, may be {@code null}
     * @return the command code that dismissed the dialog (e.g. {@code cmOK}, {@code cmCancel})
     */
    public int executeDialog(JtvDialog pD, Object data) {
        int c = cmCancel;
        if (validView(pD) != null) {
            pD.setDataFrom(data);
            c = desktop.execView(pD);
            if (c != cmCancel && data != null) {
                pD.getDataTo(data);
            }
            destroy(pD);
        }
        return c;
    }

    /**
     * Retrieves the next event from the platform queue (or from the {@link #pending} buffer).
     *
     * <p>If no event is available, {@link #idle()} is called.  Key events and status-line
     * mouse clicks are pre-processed by the status line before being returned.
     *
     * @param event the event object to fill
     * @return the filled {@code event}
     */
    @Override
    public JtvEvent getEvent(JtvEvent event) {
        if (pending.getWhat() != evNothing) {
            event.copyFrom(pending);
            pending.setWhat(evNothing);
        } 
        else {
            EventQueue.waitForEvents(eventTimeoutMs);
            EventQueue.getMouseEvent(event);
            if (event.getWhat() == evNothing) {
                EventQueue.getKeyEvent(event);
                if (event.getWhat() == evNothing) {
                    idle();
                }
            }
        }

        if (statusLine != null) {
            if ((event.getWhat() & evKeyDown) != 0 ||
                ((event.getWhat() & evMouseDown) != 0 &&
                 firstThat(v -> (v.getState() & sfVisible) != 0 && v.mouseInView(event.getMouse().getWhere())) == statusLine)) {
                statusLine.handleEvent(event);
            }
        }
        return event;
    }

    /**
     * Returns the application-wide color palette.  Returns the custom palette if one was set
     * via {@link #setAppPalette}, otherwise selects among the three built-in 135-entry palettes
     * based on {@link #appPalette} ({@code apColor}, {@code apBlackWhite}, or {@code apMonochrome}).
     *
     * @return the application palette
     */
    @Override
    public JtvPalette getPalette() {
        if (customAppPalette != null) {
            return customAppPalette;
        }
        switch (appPalette) {
            case apBlackWhite: return cpAppBlackWhite;
            case apMonochrome: return cpAppMonochrome;
            default:           return cpAppColor;
        }
    }

    /**
     * Replaces the application-wide color palette.  Pass {@code null} to restore the built-in
     * default palette.
     *
     * @param palette new palette, or {@code null} to use the default
     */
    public static void setAppPalette(JtvPalette palette) {
        customAppPalette = palette;
    }

    /** Shared clipboard: system clipboard when available, local fallback otherwise. */
    public static Clipboard getClipboard() {
		return clipboard;
	}

    private static Clipboard initClipboard() {
        try {
        	if (SystemUtils.IS_OS_MAC) {
        		System.setProperty("apple.awt.UIElement", "true"); // to hide Java app icon in MacOS
        	}
            return Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        catch (UnsupportedOperationException e) {
            return new Clipboard("jtvision");
        }
    }

    /**
     * Handles application-level events.
     *
     * <p>Intercepts Alt+{@code 1}–{@code 9} to broadcast {@code cmSelectWindowNum}, and
     * {@code cmQuit} to end the modal event loop.  All other events are passed to the
     * inherited group handler.
     *
     * @param event the event to handle
     */
    @Override
    public void handleEvent(JtvEvent event) {
        if (event.getWhat() == evKeyDown) {
            char c = JtvKey.getAltChar(event.getKeyDown().getKeyCode());
            if (c >= '1' && c <= '9') {
                if (canMoveFocus()) {
                    JtvEvent numEvent = new JtvEvent();
                    numEvent.setWhat(evBroadcast);
                    numEvent.getMessage().setCommand(cmSelectWindowNum);
                    numEvent.getMessage().setInfoInt(c - '0');
                    if (message(desktop, evBroadcast, cmSelectWindowNum, null) != null)
                        clearEvent(event);
                } else {
                    clearEvent(event);
                }
            }
        }

        super.handleEvent(event);

        if (event.getWhat() == evCommand && event.getMessage().getCommand() == cmQuit) {
            endModal(cmQuit);
            clearEvent(event);
        }
    }

    /**
     * Called by the event loop when no event is pending.
     *
     * <p>Updates the status line, broadcasts {@code cmCommandSetChanged} if the command set
     * has changed since the last idle, and checks for terminal resize events.
     * Override (calling {@code super.idle()}) to perform background work such as enabling or
     * disabling commands based on application state.
     */
    public void idle() {
        if (statusLine != null)
            statusLine.update();

        if (commandSetChanged) {
            message(this, evBroadcast, cmCommandSetChanged, null);
            commandSetChanged = false;
        }

        int oldW = Screen.screenWidth;
        int oldH = Screen.screenHeight;
        Screen.detectSize();
        if (Screen.screenWidth != oldW || Screen.screenHeight != oldH) {
            setScreenMode(Screen.screenMode);
        }
    }

    /**
     * Initialises screen-mode variables: shadow size (2 columns × 1 row), markers, and
     * application palette mode.  Called from the constructor and from {@link #setScreenMode}.
     */
    public void initScreen() {
        shadowSize = new JtvPoint(2, 1);
        showMarkers = false;
        appPalette = apColor;
    }

    /**
     * Queues {@code event} so it will be returned by the next {@link #getEvent} call.
     * Only one event can be buffered at a time; a second call overwrites the first.
     *
     * @param event the event to buffer
     */
    @Override
    public void putEvent(JtvEvent event) {
        pending.copyFrom(event);
    }

    /** Starts the application's modal event loop by calling {@link #execute()}. */
    public void run() {
        execute();
    }

    /**
     * Changes the video mode, reinitialises the screen variables, updates the screen buffer
     * reference, resizes the program group to match the new dimensions, and redraws everything.
     *
     * @param mode the new screen mode (passed to the platform {@code Screen.setVideoMode})
     */
    public void setScreenMode(int mode) {
        Screen.setVideoMode(mode);
        initScreen();
        buffer = new JtvDrawBuffer(Screen.screenBuffer);
        JtvRect r = new JtvRect(0, 0, Screen.screenWidth, Screen.screenHeight);
        changeBounds(r);
        setState(sfExposed, false);
        setState(sfExposed, true);
        redraw();
    }

    /**
     * Validates a newly constructed view.  If {@code p} is {@code null} or fails
     * {@code cmValid} validation it is destroyed and {@code null} is returned; otherwise
     * {@code p} is returned unchanged.
     *
     * @param p the view to validate
     * @return {@code p} if valid, {@code null} otherwise
     */
    public JtvView validView(JtvView p) {
        if (p == null) {
        	return null;
        }
        if (!p.valid(cmValid)) {
            destroy(p);
            return null;
        }
        return p;
    }

    /**
     * Validates and inserts {@code pWin} into the desktop.
     * The window is destroyed and {@code null} returned if it fails validation or if focus
     * cannot be moved ({@link #canMoveFocus()} returns {@code false}).
     *
     * @param pWin the window to insert
     * @return {@code pWin} on success, {@code null} on failure
     */
    public JtvWindow insertWindow(JtvWindow pWin) {
        if (validView(pWin) != null) {
            if (canMoveFocus()) {
                desktop.insert(pWin);
                return pWin;
            }
            else {
                destroy(pWin);
            }
        }
        return null;
    }

    // --- Default factory methods (override in subclasses to customise) ---

    /**
     * Creates the desktop subview.  Override to supply a custom desktop.
     * Return {@code null} to omit the desktop entirely.
     *
     * @param r the full screen rectangle
     * @return a new desktop view, or {@code null}
     */
    protected JtvDesktop initDesktop(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getA().getY() + 1, r.getB().getX(), r.getB().getY() - 1);
        return new JtvDesktop(r);
    }

    /**
     * Creates the menu bar subview.  Override to supply a custom menu bar.
     * Return {@code null} to omit the menu bar entirely.
     *
     * @param r the full screen rectangle
     * @return a new menu bar view, or {@code null}
     */
    protected JtvMenuBar initMenuBar(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getA().getY(), r.getB().getX(), r.getA().getY() + 1);
        return new JtvMenuBar(r, new JtvMenu());
    }

    /**
     * Creates the status line subview.  Override to supply a custom status line.
     * Return {@code null} to omit the status line entirely.
     *
     * @param r the full screen rectangle
     * @return a new status line view, or {@code null}
     */
    protected JtvStatusLine initStatusLine(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getB().getY() - 1, r.getB().getX(), r.getB().getY());
        String meta = JtvKey.getMetaKeyLabel();
        return new JtvStatusLine(r,
            new JtvStatusDef(0, 0xFFFF, null)
                .addItem(new JtvStatusItem("~" + meta + "-X~ Exit", JtvKey.kbAltX, cmQuit))
                .addItem(new JtvStatusItem(null, JtvKey.kbF10, cmMenu))
                .addItem(new JtvStatusItem(null, JtvKey.kbAltF3, cmClose))
                .addItem(new JtvStatusItem(null, JtvKey.kbF5, cmZoom)));
    }

    /** The current application's status line, or {@code null} if none. */
    public static JtvStatusLine getStatusLine() {
		return statusLine;
	}

	/** The current application's menu bar, or {@code null} if none. */
	public static JtvMenuBar getMenuBar() {
		return menuBar;
	}

    /** The current application's desktop, or {@code null} if none. */
	public static JtvDesktop getDesktop() {
		return desktop;
	}

    /** Reference to the running {@code TProgram} instance (set in the constructor). */
	public static JtvProgram getApplication() {
		return application;
	}
}
