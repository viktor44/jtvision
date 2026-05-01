package org.viktor44.jtvision.core;

import static org.viktor44.jtvision.core.CommandCodes.cmCascade;
import static org.viktor44.jtvision.core.CommandCodes.cmTile;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.ofTileable;
import static org.viktor44.jtvision.core.ViewFlags.sfVisible;

import org.viktor44.jtvision.menus.JtvMenuBar;
import org.viktor44.jtvision.menus.JtvStatusLine;
import org.viktor44.jtvision.platform.EventQueue;
import org.viktor44.jtvision.platform.Screen;

/**
 * The concrete top-level application class that initialises platform subsystems and runs
 * the event loop.
 *
 * <p>{@code TApplication} extends {@link JtvProgram} by initialising the {@link Screen} and
 * {@link EventQueue} platform subsystems before constructing subviews, and shutting them down
 * after the event loop exits.  Most applications should subclass {@code TApplication} and
 * override {@link JtvProgram#initMenuBar}, {@link JtvProgram#initStatusLine}, and/or
 * {@link JtvProgram#initDesktop} to supply custom subviews.
 *
 * <p>{@link #idle()} is overridden to automatically enable or disable the {@code cmTile} and
 * {@code cmCascade} commands based on whether any {@link ViewFlags#ofTileable} windows are
 * currently visible on the desktop.
 */
public class JtvApplication extends JtvProgram {

    /**
     * Constructs a {@code TApplication}, initialising the platform Screen and EventQueue
     * before the parent constructor builds the subviews.
     */
    public JtvApplication() {
        this(initPlatform());
    }

    /** Delegates to {@link JtvProgram#JtvProgram()} after the platform has been initialised. */
    private JtvApplication(boolean platformReady) {
        super();
    }

    /** Initialises the Screen and EventQueue platform subsystems. */
    private static boolean initPlatform() {
        Screen.init();
        EventQueue.init();
        return true;
    }

    /**
     * Shuts down the application: calls {@link JtvProgram#shutDown()} then shuts down the
     * Screen and EventQueue platform subsystems.
     */
    public void shutdown() {
        shutDown();
        Screen.shutdown();
        EventQueue.shutdown();
    }

    /**
     * Suspends the application for OS shell-out: pauses the EventQueue then the Screen.
     * Call {@link #resume()} to restore normal operation.
     */
    public void suspend() {
        EventQueue.suspend();
        Screen.suspend();
    }

    /**
     * Resumes the application after a shell-out: restores the Screen then restarts the EventQueue.
     */
    public void resume() {
        Screen.resume();
        EventQueue.resume();
    }

    /**
     * Cascades all tileable visible windows within the tile rectangle returned by
     * {@link #getTileRect()}.
     */
    public void cascade() {
        getDesktop().cascade(getTileRect());
    }

    /**
     * Tiles all tileable visible windows within the tile rectangle returned by
     * {@link #getTileRect()}.
     */
    public void tile() {
        getDesktop().tile(getTileRect());
    }

    /**
     * Returns the rectangle used for tile and cascade operations.
     * Default is the full desktop extent; override to restrict the area.
     *
     * @return the tile/cascade boundary rectangle
     */
    public JtvRect getTileRect() {
        return getDesktop().getExtent();
    }

    /**
     * Extends the base idle handler to enable {@code cmTile} and {@code cmCascade} when at
     * least one {@link ViewFlags#ofTileable} window is visible on the desktop, and to disable
     * them when none are.
     */
    @Override
    public void idle() {
        super.idle();
        if (getDesktop().firstThat(
                v -> (v.getOptions() & ofTileable) != 0 && (v.getState() & sfVisible) != 0) != null) {
            enableCommand(cmTile);
            enableCommand(cmCascade);
        }
        else {
            disableCommand(cmTile);
            disableCommand(cmCascade);
        }
    }

    /**
     * Handles {@code cmCascade} and {@code cmTile} commands, delegating to {@link #cascade()}
     * and {@link #tile()} respectively.  All other events are passed to the inherited handler.
     *
     * @param event the event to handle
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmCascade:
                    cascade();
                    break;
                case cmTile:
                    tile();
                    break;
                default:
                    return;
            }
            clearEvent(event);
        }
    }

    /**
     * Runs the application event loop and ensures {@link #shutdown()} is called on exit,
     * even if an exception is thrown.
     */
    @Override
    public void run() {
        try {
            super.run();
        }
        finally {
            shutdown();
        }
    }
}
