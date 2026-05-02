/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.views;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmNext;
import static org.viktor44.jtvision.core.CommandCodes.cmPrev;
import static org.viktor44.jtvision.core.CommandCodes.cmReceivedFocus;
import static org.viktor44.jtvision.core.CommandCodes.cmReleasedFocus;
import static org.viktor44.jtvision.core.CommandCodes.cmResize;
import static org.viktor44.jtvision.core.CommandCodes.cmZoom;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.EventCodes.evMouseUp;
import static org.viktor44.jtvision.core.EventCodes.evNothing;
import static org.viktor44.jtvision.core.ViewFlags.dmDragGrow;
import static org.viktor44.jtvision.core.ViewFlags.dmDragGrowLeft;
import static org.viktor44.jtvision.core.ViewFlags.dmDragMove;
import static org.viktor44.jtvision.core.ViewFlags.dmLimitHiX;
import static org.viktor44.jtvision.core.ViewFlags.dmLimitHiY;
import static org.viktor44.jtvision.core.ViewFlags.dmLimitLoX;
import static org.viktor44.jtvision.core.ViewFlags.dmLimitLoY;
import static org.viktor44.jtvision.core.ViewFlags.gfFixed;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowLoX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowLoY;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowRel;
import static org.viktor44.jtvision.core.ViewFlags.hcDragging;
import static org.viktor44.jtvision.core.ViewFlags.hcNoContext;
import static org.viktor44.jtvision.core.ViewFlags.ofFirstClick;
import static org.viktor44.jtvision.core.ViewFlags.ofFramed;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.ofTopSelect;
import static org.viktor44.jtvision.core.ViewFlags.ofValidate;
import static org.viktor44.jtvision.core.ViewFlags.sfCursorIns;
import static org.viktor44.jtvision.core.ViewFlags.sfCursorVis;
import static org.viktor44.jtvision.core.ViewFlags.sfDisabled;
import static org.viktor44.jtvision.core.ViewFlags.sfDragging;
import static org.viktor44.jtvision.core.ViewFlags.sfExposed;
import static org.viktor44.jtvision.core.ViewFlags.sfFocused;
import static org.viktor44.jtvision.core.ViewFlags.sfModal;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;
import static org.viktor44.jtvision.core.ViewFlags.sfShadow;
import static org.viktor44.jtvision.core.ViewFlags.sfVisible;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvCommandSet;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.core.JtvScreenCell;
import org.viktor44.jtvision.platform.Screen;

/**
 * Abstract base class for all JT Vision views.
 * <p>
 * JtvView represents an empty rectangular region of the screen. It is the
 * common ancestor of every visible object in the JT Vision hierarchy
 * and contains all the fundamental screen-management methods and fields
 * used by dialogs, windows, controls, and the application itself.
 * <p>
 * Although you can instantiate a plain JtvView for prototyping, it is
 * usually subclassed. Subclasses override {@link #draw()} to produce
 * their visual content, {@link #handleEvent(JtvEvent)} to respond to
 * events, and {@link #getPalette()} to supply a colour palette.
 * 
 * <h3>Coordinate system</h3>
 * 
 * Every view has an {@link #origin} (its top-left corner expressed in
 * the owner's coordinate system) and a {@link #size} (its width and
 * height). {@link #getBounds()} returns the bounding rectangle;
 * {@link #getExtent()} returns the same rectangle translated to
 * {@code (0,0)}.
 * 
 * <h3>State flags</h3>
 * 
 * The {@link #state} field is a bitmask of {@code sfXXXX} constants
 * ({@code sfVisible}, {@code sfFocused}, {@code sfSelected}, etc.).
 * Use {@link #setState(int, boolean)} to change individual bits;
 * the method automatically triggers redraws and broadcasts.
 * 
 * <h3>Event routing</h3>
 * 
 * Events flow through {@link #handleEvent(JtvEvent)}. The base
 * implementation handles {@code evMouseDown} focus transfers.
 * Subclasses call {@code super.handleEvent(event)} first, then inspect
 * and consume remaining events.
 * 
 * <h3>Commands</h3>
 * 
 * The global {@link #curCommandSet} tracks which commands are currently
 * enabled. Window-management commands ({@code cmZoom}, {@code cmClose},
 * {@code cmResize}, {@code cmNext}, {@code cmPrev}) are disabled by
 * default and enabled only by the active window.
 */
public class JtvView {


    // Selection mode constants

    /** Selection mode indicating a standard selection change. */
    public static final int normalSelect = 0;

    /** Selection mode used when a view gains focus by entering it. */
    public static final int enterSelect = 1;

    /** Selection mode used when a view loses focus by leaving it. */
    public static final int leaveSelect = 2;

    // Phase constants for event routing

    /** Event routing phase: deliver to the focused subview. */
    public static final int phFocused = 0;

    /**
     * Event routing phase: deliver to subviews with {@code ofPreProcess}
     * before routing to the focused view.
     */
    public static final int phPreProcess = 1;

    /**
     * Event routing phase: deliver to subviews with {@code ofPostProcess}
     * after routing to the focused view.
     */
    public static final int phPostProcess = 2;

    // Global view state

    /**
     * The width and height of the shadow cast by views that have the
     * {@code sfShadow} state bit set. Defaults to {@code (2, 1)}.
     */
    public static JtvPoint shadowSize = new JtvPoint(2, 1);

    /**
     * The colour attribute used to render shadows. Low nibble is the
     * shadow foreground colour; high nibble is the shadow background.
     * Defaults to {@code 0x08} (dark gray on black).
     */
    public static int shadowAttr = 0x08;

    /**
     * When {@code true}, the list viewer draws side markers ({@code »} / {@code «})
     * beside selected items. Defaults to {@code false}.
     */
    public static boolean showMarkers = false;

    /**
     * The colour attribute used to highlight palette mapping errors.
     * Defaults to {@code 0xCF} (white on red).
     */
    public static int errorAttr = 0xCF;

    /**
     * Set to {@code true} whenever {@link #curCommandSet} changes, allowing
     * the status line and menus to redraw only when necessary.
     */
    public static boolean commandSetChanged = false;

    /**
     * The application-wide set of currently enabled commands.
     * Initialised with all 256 commands enabled except the four
     * window-management commands ({@code cmZoom}, {@code cmClose},
     * {@code cmResize}, {@code cmNext}, {@code cmPrev}).
     */
    public static JtvCommandSet curCommandSet = initCommands();

    /**
     * The current top-level modal view, or {@code null} when no modal
     * view is active. Set and restored by {@link JtvGroup#execView}.
     */
    public static JtvView theTopView = null;
	
    // Linked list pointers

    /**
     * The next view in the owner's circular singly-linked list.
     * Maintained by {@link JtvGroup#insertView} and {@link JtvGroup#removeView}.
     */
    protected JtvView next;

    /**
     * The {@link JtvGroup} that owns this view, or {@code null} if the view
     * is not yet inserted into any group.
     */
    protected JtvGroup owner;

    // Geometry

    /**
     * The top-left corner of this view in the owner group's local
     * coordinate system. Set by {@link #setBounds(JtvRect)}.
     */
    protected JtvPoint origin = new JtvPoint();

    /**
     * The width and height of this view. Together with {@link #origin},
     * it defines the bounding rectangle returned by {@link #getBounds()}.
     */
    protected JtvPoint size = new JtvPoint();

	/**
     * The cursor position within the view's local coordinate system.
     * Updated by {@link #setCursor(int, int)} and {@link #resetCursor()}.
     */
    protected JtvPoint cursor = new JtvPoint();

	/**
     * Accumulated sub-pixel balance used by {@link #balancedRange} to
     * distribute fractional pixels when growing a view relative to its
     * owner. Prevents drift when the owner is resized many times.
     */
    protected JtvPoint resizeBalance = new JtvPoint();

    // State and mode flags

    /**
     * Bitmask of {@code sfXXXX} state constants describing the current
     * visibility, focus, selection, and dragging status of the view.
     * Modify only through {@link #setState(int, boolean)}.
     */
    protected int state;

	/**
     * Bitmask of {@code ofXXXX} option constants controlling selectable,
     * framed, centered, buffered, and other optional behaviour.
     */
    protected int options;

	/**
     * Event-type bitmask that determines which event categories this view
     * wants to receive (e.g. {@code evMouseDown}, {@code evKeyDown},
     * {@code evCommand}). Set to the union of event types handled by
     * {@link #handleEvent(JtvEvent)}.
     */
    protected int eventMask;

	/**
     * Grow-mode bitmask of {@code gfXXXX} constants controlling how this
     * view resizes relative to its owner (e.g. {@code gfGrowHiX} to keep
     * the right edge pinned to the owner's right side).
     */
    protected int growMode;

	/**
     * Drag-mode bitmask of {@code dmXXXX} constants constraining where
     * the view can be dragged or grown. Defaults to {@code dmLimitLoY}.
     */
    protected int dragMode;

    /**
     * The help-context identifier for this view. The default value is
     * {@code hcNoContext}. Accessed via {@link #getHelpCtx()} which returns
     * {@code hcDragging} when the view is being dragged.
     */
    protected int helpCtx;

    /** Builds the initial {@link #curCommandSet} with window commands disabled. */
    private static JtvCommandSet initCommands() {
        JtvCommandSet temp = new JtvCommandSet();
        for (int i = 0; i < 256; i++)
            temp.enableCmd(i);
        temp.disableCmd(cmZoom);
        temp.disableCmd(cmClose);
        temp.disableCmd(cmResize);
        temp.disableCmd(cmNext);
        temp.disableCmd(cmPrev);
        return temp;
    }

    /**
     * Constructs a view with the given bounding rectangle.
     * <p>
     * Initialises {@link #state} to {@code sfVisible}, {@link #eventMask}
     * to {@code evMouseDown | evKeyDown | evCommand}, {@link #dragMode}
     * to {@code dmLimitLoY}, and {@link #helpCtx} to {@code hcNoContext}.
     *
     * @param bounds the initial bounding rectangle in the owner's coordinate system
     */
    public JtvView(JtvRect bounds) {
        options = 0;
        eventMask = evMouseDown | evKeyDown | evCommand;
        state = sfVisible;
        growMode = 0;
        dragMode = dmLimitLoY;
        helpCtx = hcNoContext;
        owner = null;
        next = null;
        setBounds(bounds);
    }

    /**
     * Called after the view is inserted into its owner group and the group
     * is fully initialised. Subclasses can override to perform deferred
     * initialisation. The base implementation does nothing.
     */
    public void awaken() {
    }

    /**
     * Switches the cursor to insert (block) mode by setting
     * {@code sfCursorIns} in the state.
     */
    public void blockCursor() {
        setState(sfCursorIns, true);
    }

    /**
     * Computes the new bounding rectangle for this view when its owner is
     * resized by {@code delta} columns/rows.
     * <p>
     * Each edge is moved or held fixed according to the {@link #growMode}
     * flags. After applying the grow rules the result is clamped to the
     * view's size limits via {@link #getMinimumSize()} and {@link #getMaximumSize()}.
     *
     * @param bounds receives the newly computed bounding rectangle
     * @param delta  the change in the owner's size (positive = owner grew)
     */
    public void calcBounds(JtvRect bounds, JtvPoint delta) {
        bounds.assign(getBounds());

        int s = owner.size.getX();
        int d = delta.getX();

        if ((growMode & gfGrowLoX) != 0)
            bounds.setA(new JtvPoint(grow(s, d, bounds.getA().getX()), bounds.getA().getY()));
        if ((growMode & gfGrowHiX) != 0)
            bounds.setB(new JtvPoint(grow(s, d, bounds.getB().getX()), bounds.getB().getY()));

        s = owner.size.getY();
        d = delta.getY();

        if ((growMode & gfGrowLoY) != 0)
            bounds.setA(new JtvPoint(bounds.getA().getX(), grow(s, d, bounds.getA().getY())));
        if ((growMode & gfGrowHiY) != 0)
            bounds.setB(new JtvPoint(bounds.getB().getX(), grow(s, d, bounds.getB().getY())));

        JtvPoint min = getMinimumSize();
        JtvPoint max = getMaximumSize();
        bounds.setB(new JtvPoint(
            bounds.getA().getX() + balancedRange(bounds.getB().getX() - bounds.getA().getX(), min.getX(), max.getX(), true),
            bounds.getA().getY() + balancedRange(bounds.getB().getY() - bounds.getA().getY(), min.getY(), max.getY(), false)));
    }

    /** Applies grow-mode scaling or translation to a single coordinate. */
    private int grow(int s, int d, int i) {
        if ((growMode & gfGrowRel) != 0) {
            if (s != d)
                i = (i * s + ((s - d) >> 1)) / (s - d);
        } else {
            i += d;
        }
        return i;
    }

    /**
     * Clamps {@code val} to {@code [min, max]} while accumulating the
     * fractional remainder in {@link #resizeBalance} to avoid drift over
     * repeated owner resizes.
     */
    private int balancedRange(int val, int min, int max, boolean useX) {
        if (min > max)
            max = min;
        int balance = useX ? resizeBalance.getX() : resizeBalance.getY();
        int result;
        if (val < min) {
            balance += val - min;
            result = min;
        } else if (val > max) {
            balance += val - max;
            result = max;
        } else {
            int offset = range(val + balance, min, max) - val;
            balance -= offset;
            result = val + offset;
        }
        if (useX) resizeBalance = new JtvPoint(balance, resizeBalance.getY());
        else resizeBalance = new JtvPoint(resizeBalance.getX(), balance);
        return result;
    }

    /**
     * Sets the view's bounding rectangle and redraws the view.
     * Called by {@link JtvGroup} when the owner is resized.
     *
     * @param bounds the new bounding rectangle
     */
    public void changeBounds(JtvRect bounds) {
        setBounds(bounds);
        drawView();
    }

    /**
     * Marks {@code event} as consumed by setting its type to
     * {@code evNothing} and recording {@code this} as the last handler.
     *
     * @param event the event to consume
     */
    public void clearEvent(JtvEvent event) {
        event.setWhat(evNothing);
        event.getMessage().setInfoPtr(this);
    }

    /**
     * Returns {@code true} if {@code command} is currently enabled.
     * Commands above 255 are always considered enabled.
     *
     * @param command the command code to test
     * @return {@code true} if the command is enabled
     */
    public static boolean commandEnabled(int command) {
        return command > 255 || curCommandSet.has(command);
    }

    /**
     * Returns the number of bytes used by this view's data record
     * for {@link #getDataTo}/{@link #setDataFrom} transfers.
     * The base implementation returns {@code 0}.
     *
     * @return the data record size in bytes
     */
    public int dataSize() {
        return 0;
    }

    /**
     * Disables every command in {@code commands} from {@link #curCommandSet}
     * and sets {@link #commandSetChanged} if any were previously enabled.
     *
     * @param commands the set of commands to disable
     */
    public static void disableCommands(JtvCommandSet commands) {
        commandSetChanged = commandSetChanged || !(curCommandSet.and(commands)).isEmpty();
        curCommandSet.disableCmd(commands);
    }

    /**
     * Disables a single command in {@link #curCommandSet} and sets
     * {@link #commandSetChanged} if it was previously enabled.
     *
     * @param command the command code to disable
     */
    public static void disableCommand(int command) {
        commandSetChanged = commandSetChanged || curCommandSet.has(command);
        curCommandSet.disableCmd(command);
    }

    /**
     * Draws the view's contents on screen.
     * <p>
     * The base implementation fills the view's rectangle with a single
     * space character in the view's default colour (palette entry 1).
     * Subclasses override this method to provide their own visual output.
     */
    public void draw() {
        JtvDrawBuffer b = new JtvDrawBuffer();
        JtvColorAttr c1 = getColor(1);
        b.moveChar(0, ' ', c1, size.getX());
        writeLine(0, 0, size.getX(), size.getY(), b);
    }

    /**
     * Updates the hardware cursor position if the view is focused and the
     * cursor is visible. Called at the end of {@link #drawView()}.
     */
    public void drawCursor() {
        if ((state & sfFocused) != 0)
            resetCursor();
    }

    /**
     * Redraws the screen area that was covered by this view before it was
     * hidden. Also draws the cursor if visible.
     *
     * @param lastView the sibling view behind which to stop redrawing, or
     *                 {@code null} to redraw all siblings
     */
    public void drawHide(JtvView lastView) {
        drawCursor();
        drawUnderView((state & sfShadow) != 0, lastView);
    }

    /**
     * Draws this view and, if it has a shadow, the area covered by the
     * shadow.
     *
     * @param lastView the sibling view behind which to stop drawing shadows,
     *                 or {@code null}
     */
    public void drawShow(JtvView lastView) {
        drawView();
        if ((state & sfShadow) != 0)
            drawUnderView(true, lastView);
    }

    /**
     * Asks the owner to redraw all views in the sibling list that overlap
     * the rectangle {@code r}, starting after this view.
     *
     * @param r        the rectangle to redraw
     * @param lastView the sibling view at which to stop, or {@code null}
     */
    public void drawUnderRect(JtvRect r, JtvView lastView) {
        owner.clip.intersect(r);
        owner.drawSubViews(nextView(), lastView);
        owner.clip = owner.getExtent();
    }

    /**
     * Redraws the screen area beneath this view (including its shadow area
     * if {@code doShadow} is {@code true} or the view carries {@code sfShadow}).
     *
     * @param doShadow {@code true} to include the shadow region
     * @param lastView the sibling view at which to stop, or {@code null}
     */
    public void drawUnderView(boolean doShadow, JtvView lastView) {
        JtvRect r = getBounds();
        if (doShadow)
            r.setB(r.getB().add(shadowSize));
        if ((options & ofFramed) != 0)
            r.grow(1, 1);
        drawUnderRect(r, lastView);
    }

    /**
     * Redraws this view only if it is currently exposed (visible and not
     * completely hidden by other views). Also updates the cursor.
     */
    public void drawView() {
        if (exposed()) {
            draw();
            drawCursor();
        }
    }

    /**
     * Enables every command in {@code commands} in {@link #curCommandSet}
     * and sets {@link #commandSetChanged} if any were previously disabled.
     *
     * @param commands the set of commands to enable
     */
    public static void enableCommands(JtvCommandSet commands) {
        commandSetChanged = commandSetChanged || !curCommandSet.and(commands).equals(commands);
        curCommandSet.enableCmd(commands);
    }

    /**
     * Enables a single command in {@link #curCommandSet} and sets
     * {@link #commandSetChanged} if it was previously disabled.
     *
     * @param command the command code to enable
     */
    public static void enableCommand(int command) {
        commandSetChanged = commandSetChanged || !curCommandSet.has(command);
        curCommandSet.enableCmd(command);
    }

    /**
     * Ends the current modal state by delegating to the topmost modal view.
     * The top-level modal view records {@code command} as its return value.
     *
     * @param command the value to return from {@link JtvGroup#execute()}
     */
    public void endModal(int command) {
        JtvView top = topView();
        if (top != null)
            top.endModal(command);
    }

    /**
     * Returns {@code true} if the event queue is non-empty without
     * consuming the pending event (it is re-enqueued immediately).
     *
     * @return {@code true} if at least one event is waiting
     */
    public boolean eventAvail() {
        JtvEvent event = new JtvEvent();
        getEvent(event);
        if (event.getWhat() != evNothing)
            putEvent(event);
        return event.getWhat() != evNothing;
    }

    /**
     * Runs the view's modal event loop and returns the exit command.
     * The base implementation always returns {@code cmCancel}.
     * {@link JtvGroup} overrides this with a full event loop.
     *
     * @return the command that caused the modal loop to end
     */
    public int execute() {
        return cmCancel;
    }

    /**
     * Returns {@code true} if any row of this view is currently visible
     * on screen (not fully obscured by other views and exposed by its owner).
     *
     * @return {@code true} if the view contributes visible pixels to the screen
     */
    public boolean exposed() {
        if ((state & sfExposed) == 0)
            return false;
        if (size.getX() <= 0 || size.getY() <= 0)
            return false;
        if (owner == null)
            return false;
        // Simplified: check if any row has visible pixels
        for (int y = 0; y < size.getY(); y++) {
            if (exposedRow(y, 0, size.getX()))
                return true;
        }
        return false;
    }

    /** Checks whether a specific row segment of the view is visible. */
    private boolean exposedRow(int y, int x1, int x2) {
        // Walk up the owner chain checking if this row is visible
        JtvView target = this;
        int ay = y + origin.getY();
        int ax1 = x1 + origin.getX();
        int ax2 = x2 + origin.getX();
        JtvGroup grp = owner;

        while (grp != null) {
            // Clip to owner's clip rect
            if (ay < grp.clip.getA().getY() || ay >= grp.clip.getB().getY())
                return false;
            ax1 = Math.max(ax1, grp.clip.getA().getX());
            ax2 = Math.min(ax2, grp.clip.getB().getX());
            if (ax1 >= ax2)
                return false;

            // Check sibling views in front of target
            if (grp.last != null) {
                JtvView v = grp.last.next;
                while (v != target) {
                    if ((v.state & sfVisible) != 0) {
                        int vy1 = v.origin.getY();
                        int vy2 = vy1 + v.size.getY();
                        if (ay >= vy1 && ay < vy2) {
                            int vx1 = v.origin.getX();
                            int vx2 = vx1 + v.size.getX();
                            if (ax1 >= vx1 && ax2 <= vx2)
                                return false; // Completely occluded
                            if (ax1 < vx1 && ax2 > vx2) {
                                // Split: check left part, if visible return true
                                // (simplified: just mark partially visible)
                                return true;
                            }
                            if (ax1 < vx2 && ax1 >= vx1)
                                ax1 = vx2;
                            if (ax2 > vx1 && ax2 <= vx2)
                                ax2 = vx1;
                            if (ax1 >= ax2)
                                return false;
                        }
                    }
                    v = v.next;
                }
            }

            // Move up
            if (grp.buffer != null || grp.lockFlag != 0)
                return true; // Will be written to buffer
            target = grp;
            ay += grp.origin.getY();
            ax1 += grp.origin.getX();
            ax2 += grp.origin.getX();
            grp = grp.owner;
        }
        return true; // Reached top level
    }

    /**
     * Requests that this view become the focused (selected) view in its
     * owner, first making the owner group focused recursively up the chain.
     * <p>
     * If the currently focused view has {@code ofValidate} set and
     * {@code valid(cmReleasedFocus)} returns {@code false}, focus is
     * not transferred.
     *
     * @return {@code true} if focus was successfully acquired
     */
    public boolean focus() {
        boolean result = true;
        if ((state & (sfSelected | sfModal)) == 0) {
            if (owner != null) {
                result = owner.focus();
                if (result) {
                    if (owner.current == null ||
                        (owner.current.options & ofValidate) == 0 ||
                        owner.current.valid(cmReleasedFocus))
                        select();
                    else
                        return false;
                }
            }
        }
        return result;
    }

    /**
     * Returns the view's bounding rectangle in the owner's coordinate system.
     *
     * @return the bounding rectangle ({@code origin} to {@code origin + size})
     */
    public JtvRect getBounds() {
        return new JtvRect(origin, origin.add(size));
    }

    /**
     * Returns the portion of the view's extent that lies within the
     * owner's current clip rectangle, translated to the view's own
     * coordinate system.
     *
     * @return the effective clip rectangle for drawing
     */
    public JtvRect getClipRect() {
        JtvRect clip = getBounds();
        if (owner != null)
            clip.intersect(owner.clip);
        clip.move(-origin.getX(), -origin.getY());
        return clip;
    }

    /**
     * Resolves a single colour-palette index through the palette chain and
     * returns the final colour attribute.
     *
     * @param color the palette index (only the low 8 bits are used)
     * @return the resolved colour attribute
     */
    public JtvColorAttr getColor(int color) {
        return mapColor(color & 0xFF);
    }

    /**
     * Copies the current global command set into {@code commands}.
     *
     * @param commands receives a copy of {@link #curCommandSet}
     */
    public static void getCommands(JtvCommandSet commands) {
        commands.assign(curCommandSet);
    }

    /**
     * No-op data-export stub. Subclasses override to write their data
     * into {@code rec}.
     *
     * @param rec the destination data record
     */
    public void getDataTo(Object rec) {
    }

    /**
     * Retrieves the next pending event by delegating to the owner group.
     * Returns an unchanged event (with type {@code evNothing}) if there
     * is no owner.
     *
     * @param event the event object to populate
     * @return the same {@code event} reference, populated with the next event
     */
    public JtvEvent getEvent(JtvEvent event) {
        if (owner != null) {
            return owner.getEvent(event);
        }
        return event;
    }

    /**
     * Returns the view's extent rectangle — a rectangle from {@code (0,0)}
     * to {@link #size}, i.e. the bounds translated to the view's own origin.
     *
     * @return the extent rectangle
     */
    public JtvRect getExtent() {
        return new JtvRect(0, 0, size.getX(), size.getY());
    }

    /**
     * Returns the effective help-context ID for this view. Returns
     * {@code hcDragging} while the view is being dragged, otherwise
     * returns {@link #helpCtx}.
     *
     * @return the current help-context ID
     */
    public int getHelpCtx() {
        if ((state & sfDragging) != 0)
            return hcDragging;
        return helpCtx;
    }

    /**
     * Sets the help-context ID for this view.
     *
     * @param helpCtx the new help-context ID
     */
    public void setHelpCtx(int helpCtx) {
        this.helpCtx = helpCtx;
    }

    /**
     * Returns the colour palette for this view. The base implementation
     * returns an empty palette (direct colour mapping — no indirection).
     * Subclasses override this to provide their own palette entries.
     *
     * @return the view's colour palette
     */
    public JtvPalette getPalette() {
        return new JtvPalette(new int[0]);
    }

    /**
     * Tests whether all bits in {@code aState} are currently set in
     * {@link #state}.
     *
     * @param aState the state bitmask to test
     * @return {@code true} if all specified bits are set
     */
    public boolean hasState(int aState) {
        return (state & aState) == aState;
    }

    /**
     * Resizes the view to exactly {@code x} columns wide and {@code y}
     * rows tall, keeping the current top-left origin.
     *
     * @param x the new width in columns
     * @param y the new height in rows
     */
    public void growTo(int x, int y) {
        JtvRect r = new JtvRect(origin.getX(), origin.getY(), origin.getX() + x, origin.getY() + y);
        locate(r);
    }

    /**
     * Handles an event for this view.
     * <p>
     * The base implementation handles {@code evMouseDown}: if the view is
     * not selected or disabled and has {@code ofSelectable} set, it calls
     * {@link #focus()}. Unless {@code ofFirstClick} is set, the mouse-down
     * event is consumed so it does not also activate the view's normal
     * action.
     *
     * @param event the incoming event (modified in place to consume it)
     */
    public void handleEvent(JtvEvent event) {
        if (event.getWhat() == evMouseDown) {
            if ((state & (sfSelected | sfDisabled)) == 0 && (options & ofSelectable) != 0) {
                if (!focus() || (options & ofFirstClick) == 0)
                    clearEvent(event);
            }
        }
    }

    /**
     * Makes the view invisible by clearing {@code sfVisible}.
     * If the view is already invisible this is a no-op.
     */
    public void hide() {
        if ((state & sfVisible) != 0)
            setState(sfVisible, false);
    }

    /**
     * Hides the hardware cursor by clearing {@code sfCursorVis}.
     */
    public void hideCursor() {
        setState(sfCursorVis, false);
    }

    /**
     * Blocks until a keyboard event arrives, storing it in {@code event}.
     * Any non-keyboard events are discarded.
     *
     * @param event the event object that receives the keyboard event
     */
    public void keyEvent(JtvEvent event) {
        do {
            getEvent(event);
        } while (event.getWhat() != evKeyDown);
    }

    /**
     * Moves and/or resizes the view to the given bounds, clamped to the
     * size limits returned by {@link #getMinimumSize()} and {@link #getMaximumSize()}.
     * Triggers a redraw of the area previously occupied by the view if
     * the bounds actually changed.
     *
     * @param bounds the desired new bounding rectangle
     */
    public void locate(JtvRect bounds) {
        JtvPoint min = getMinimumSize();
        JtvPoint max = getMaximumSize();
        bounds.setB(new JtvPoint(
            bounds.getA().getX() + range(bounds.getB().getX() - bounds.getA().getX(), min.getX(), max.getX()),
            bounds.getA().getY() + range(bounds.getB().getY() - bounds.getA().getY(), min.getY(), max.getY())));
        JtvRect r = getBounds();
        if (!bounds.equals(r)) {
            changeBounds(bounds);
            if (owner != null && (state & sfVisible) != 0) {
                if ((state & sfShadow) != 0) {
                    r.union(bounds);
                    r.setB(r.getB().add(shadowSize));
                }
                drawUnderRect(r, null);
            }
        }
    }

    /**
     * Moves this view to the front of the owner group (z-order), making
     * it the first view in the circular linked list.
     */
    public void makeFirst() {
        putInFrontOf(owner.first());
    }

    /**
     * Converts {@code source} from local view coordinates to global screen
     * coordinates by walking up the owner chain adding each group's origin.
     *
     * @param source the point in this view's local coordinate system
     * @return the corresponding point in screen coordinates
     */
    public JtvPoint makeGlobal(JtvPoint source) {
        JtvPoint temp = new JtvPoint(source.getX() + origin.getX(), source.getY() + origin.getY());
        JtvView cur = this;
        while (cur.owner != null) {
            cur = cur.owner;
            temp = temp.add(cur.origin);
        }
        return temp;
    }

    /**
     * Converts {@code source} from global screen coordinates to local view
     * coordinates by walking up the owner chain subtracting each origin.
     *
     * @param source the point in screen coordinates
     * @return the corresponding point in this view's local coordinate system
     */
    public JtvPoint makeLocal(JtvPoint source) {
        JtvPoint temp = new JtvPoint(source.getX() - origin.getX(), source.getY() - origin.getY());
        JtvView cur = this;
        while (cur.owner != null) {
            cur = cur.owner;
            temp = temp.subtract(cur.origin);
        }
        return temp;
    }

    /**
     * Maps palette entry {@code index} through the view's own palette and,
     * if the result refers to another palette entry, continues up the
     * owner chain until a final colour attribute is resolved.
     * Returns {@link #errorAttr} if the index is out of range or maps
     * to zero (the "transparent" sentinel).
     *
     * @param index the one-based palette index to resolve
     * @return the final colour attribute
     */
    protected JtvColorAttr mapColor(int index) {
        JtvPalette p = getPalette();
        JtvColorAttr color;
        if (p.length() != 0) {
            if (index > 0 && index <= p.length()) {
                color = p.get(index);
            }
            else {
                return new JtvColorAttr(errorAttr);
            }
        }
        else {
            color = new JtvColorAttr(index);
        }
        if (color.getValue() == 0) {
            return new JtvColorAttr(errorAttr);
        }
        if (owner != null) {
            return owner.mapColor(color.getValue());
        }
        return color;
    }

    /**
     * Polls for mouse events, blocking until one of the events matching
     * {@code mask} (or a mouse-up) arrives.
     *
     * @param event the event object to populate
     * @param mask  the event type bitmask to wait for
     * @return {@code true} if the loop ended on a masked event;
     *         {@code false} if it ended on a mouse-up
     */
    public boolean mouseEvent(JtvEvent event, int mask) {
        do {
            getEvent(event);
        } while ((event.getWhat() & (mask | evMouseUp)) == 0);
        return event.getWhat() != evMouseUp;
    }

    /**
     * Returns {@code true} if {@code mouse} (in global screen coordinates)
     * lies within this view's visible bounds.
     *
     * @param mouse a point in global screen coordinates
     * @return {@code true} if the point is inside the view's extent
     */
    public boolean mouseInView(JtvPoint mouse) {
        mouse = makeLocal(mouse);
        JtvRect r = getExtent();
        return r.contains(mouse);
    }

    /**
     * Moves the view so its top-left corner is at {@code (x, y)} in the
     * owner's coordinate system. The view's size is unchanged.
     *
     * @param x the new left column
     * @param y the new top row
     */
    public void moveTo(int x, int y) {
        JtvRect r = new JtvRect(x, y, x + size.getX(), y + size.getY());
        locate(r);
    }

    /**
     * Returns the next visible sibling of this view in the owner's
     * z-order, or {@code null} if this view is the bottommost sibling.
     *
     * @return the next sibling, or {@code null}
     */
    public JtvView nextView() {
        if (owner == null || this == owner.last)
            return null;
        else
            return next;
    }

    /**
     * Switches the cursor to normal (underline) mode by clearing
     * {@code sfCursorIns}.
     */
    public void normalCursor() {
        setState(sfCursorIns, false);
    }

    /**
     * Returns the previous sibling in the owner's circular linked list
     * by walking the list until the view whose {@code next} is {@code this}
     * is found.
     *
     * @return the previous sibling (never {@code null} if owner is non-null)
     */
    public JtvView prev() {
        JtvView res = this;
        while (res.next != this)
            res = res.next;
        return res;
    }

    /**
     * Returns the previous visible sibling of this view in z-order, or
     * {@code null} if this view is the topmost sibling.
     *
     * @return the previous sibling, or {@code null}
     */
    public JtvView prevView() {
        if (owner == null || this == owner.first())
            return null;
        else
            return prev();
    }

    /**
     * Places {@code event} into the owner group's event queue.
     * Has no effect if this view has no owner.
     *
     * @param event the event to inject
     */
    public void putEvent(JtvEvent event) {
        if (owner != null)
            owner.putEvent(event);
    }

    /**
     * Moves this view immediately in front of {@code target} in the owner's
     * z-order, triggering appropriate show/hide redraws.
     * Has no effect if {@code target} is this view, the view directly behind
     * it, or belongs to a different owner.
     *
     * @param target the sibling that this view should be placed in front of,
     *               or {@code null} to move to the back
     */
    public void putInFrontOf(JtvView target) {
        if (owner != null && target != this && target != nextView() &&
            (target == null || target.owner == owner)) {
            if ((state & sfVisible) == 0) {
                owner.removeView(this);
                owner.insertView(this, target);
            } else {
                JtvView lastView = nextView();
                JtvView p = target;
                while (p != null && p != this)
                    p = p.nextView();
                if (p == null)
                    lastView = target;
                state &= ~sfVisible;
                if (lastView == target)
                    drawHide(lastView);
                owner.removeView(this);
                owner.insertView(this, target);
                state |= sfVisible;
                if (lastView != target)
                    drawShow(lastView);
                if ((options & ofSelectable) != 0)
                    owner.resetCurrent();
            }
        }
    }

    /**
     * Repositions the hardware cursor to this view's {@link #cursor} point
     * (in screen coordinates) when the view is focused and the cursor is
     * visible. Hides the hardware cursor otherwise.
     */
    public void resetCursor() {
        if (owner != null && (state & (sfCursorVis | sfFocused)) == (sfCursorVis | sfFocused)) {
            JtvPoint globalCursor = makeGlobal(cursor);
            Screen.setCursorPosition(globalCursor.getX(), globalCursor.getY());
            Screen.setCursorShape((state & sfCursorIns) != 0);
            Screen.setCursorVisible(true);
        } else if (owner != null) {
            Screen.setCursorVisible(false);
        }
    }

    /**
     * Makes this view the selected view in its owner group.
     * <p>
     * If {@code ofTopSelect} is set, the view is also brought to the front
     * of the z-order via {@link #makeFirst()}.
     */
    public void select() {
        if ((options & ofSelectable) != 0 && owner != null) {
            if ((options & ofTopSelect) != 0)
                makeFirst();
            else
                owner.setCurrent(this, normalSelect);
        }
    }

    /**
     * Updates {@link #origin} and {@link #size} from the given bounding
     * rectangle without triggering a redraw.
     *
     * @param bounds the new bounding rectangle
     */
    public void setBounds(JtvRect bounds) {
        origin = new JtvPoint(bounds.getA());
        size = new JtvPoint(bounds.getB().getX() - bounds.getA().getX(), bounds.getB().getY() - bounds.getA().getY());
    }

    /**
     * Enables or disables all commands in {@code commands} according to
     * {@code enable}, calling {@link #enableCommands} or
     * {@link #disableCommands} accordingly.
     *
     * @param commands the command set to modify
     * @param enable   {@code true} to enable, {@code false} to disable
     */
    public static void setCmdState(JtvCommandSet commands, boolean enable) {
        if (enable)
            enableCommands(commands);
        else
            disableCommands(commands);
    }

    /**
     * Replaces the entire global command set with {@code commands} and
     * sets {@link #commandSetChanged} if the set is different.
     *
     * @param commands the new command set
     */
    public static void setCommands(JtvCommandSet commands) {
        commandSetChanged = commandSetChanged || !curCommandSet.equals(commands);
        curCommandSet.assign(commands);
    }

    /**
     * Moves the cursor to position {@code (x, y)} within the view and
     * updates the screen cursor via {@link #drawCursor()}.
     *
     * @param x the cursor column in view-local coordinates
     * @param y the cursor row in view-local coordinates
     */
    public void setCursor(int x, int y) {
        cursor = new JtvPoint(x, y);
        drawCursor();
    }

    /**
     * No-op data-import stub. Subclasses override to read their data
     * from {@code rec}.
     *
     * @param rec the source data record
     */
    public void setDataFrom(Object rec) {
    }

    /**
     * Sets or clears one or more state bits and triggers the corresponding
     * side effects:
     * <ul>
     *   <li>{@code sfVisible} — shows or hides the view and resets focus.</li>
     *   <li>{@code sfCursorVis} / {@code sfCursorIns} — repositions the cursor.</li>
     *   <li>{@code sfShadow} — redraws the shadow area.</li>
     *   <li>{@code sfFocused} — moves the hardware cursor and broadcasts
     *       {@code cmReceivedFocus} / {@code cmReleasedFocus}.</li>
     * </ul>
     *
     * @param aState the state bits to modify
     * @param enable {@code true} to set the bits, {@code false} to clear them
     */
    public void setState(int aState, boolean enable) {
        if (enable)
            state |= aState;
        else
            state &= ~aState;

        if (owner == null)
            return;

        switch (aState) {
            case sfVisible:
                if ((owner.state & sfExposed) != 0)
                    setState(sfExposed, enable);
                if (enable)
                    drawShow(null);
                else
                    drawHide(null);
                if ((options & ofSelectable) != 0)
                    owner.resetCurrent();
                break;
            case sfCursorVis:
            case sfCursorIns:
                drawCursor();
                break;
            case sfShadow:
                drawUnderView(true, null);
                break;
            case sfFocused:
                resetCursor();
                message(owner, evBroadcast,
                    enable ? cmReceivedFocus : cmReleasedFocus, this);
                break;
        }
    }

    /**
     * Makes the view visible by setting {@code sfVisible}.
     * If the view is already visible this is a no-op.
     */
    public void show() {
        if ((state & sfVisible) == 0)
            setState(sfVisible, true);
    }

    /**
     * Makes the cursor visible by setting {@code sfCursorVis}.
     */
    public void showCursor() {
        setState(sfCursorVis, true);
    }

    /**
     * Returns the minimum allowable size for this view.
     * The base implementation returns {@code (0, 0)}.
     */
    public JtvPoint getMinimumSize() {
        return new JtvPoint(0, 0);
    }

    /**
     * Returns the maximum allowable size for this view.
     * The base implementation returns the owner's size, or
     * {@code (Integer.MAX_VALUE, Integer.MAX_VALUE)} when the view has
     * {@code gfFixed} set or has no owner.
     */
    public JtvPoint getMaximumSize() {
        return ((growMode & gfFixed) == 0 && owner != null)
                ? new JtvPoint(owner.size)
                : new JtvPoint(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the topmost modal view in the ownership chain, starting at
     * {@link #theTopView} if set. Returns {@code null} if no modal view
     * is found.
     *
     * @return the active modal view, or {@code null}
     */
    public JtvView topView() {
        if (theTopView != null) {
            return theTopView;
        }
        JtvView p = this;
        while (p != null && (p.state & sfModal) == 0) {
            p = p.owner;
        }
        return p;
    }

    /**
     * Validates the view before it is closed by the given command.
     * The base implementation always returns {@code true}. Subclasses
     * override to prevent closing when data is invalid.
     *
     * @param command the command attempting to close the view
     * @return {@code true} if the view may close
     */
    public boolean valid(int command) {
        return true;
    }

    /**
     * Returns {@code true} if this view is visible and the mouse pointer
     * in {@code event} lies within the view's bounds.
     *
     * @param event the mouse event carrying the cursor position
     * @return {@code true} if the mouse is inside the visible view
     */
    public boolean containsMouse(JtvEvent event) {
        return (state & sfVisible) != 0 && mouseInView(event.getMouse().getWhere());
    }

    /**
     * Hides the view and removes it from its owner group.
     * After this call the view is no longer part of the view hierarchy.
     */
    public void shutDown() {
        hide();
        if (owner != null)
            owner.remove(this);
    }

    // --- Write methods ---

    /**
     * Writes a rectangular block of screen cells from {@code b} at position
     * {@code (x, y)} in view-local coordinates.
     * The buffer is consumed row by row; each row is {@code w} cells wide.
     *
     * @param x      left column (view-local)
     * @param y      top row (view-local)
     * @param w      width in columns
     * @param h      height in rows
     * @param b      source draw buffer
     */
    public void writeBuf(int x, int y, int w, int h, JtvDrawBuffer b) {
        int offset = 0;
        while (h-- > 0) {
            writeView(x, y++, w, b.getData(), offset);
            offset += w;
        }
    }

    /**
     * Writes a rectangular block of screen cells from a {@link JtvScreenCell}
     * array at position {@code (x, y)} in view-local coordinates.
     *
     * @param x      left column (view-local)
     * @param y      top row (view-local)
     * @param w      width in columns
     * @param h      height in rows
     * @param b      source cell array
     */
    public void writeBuf(int x, int y, int w, int h, JtvScreenCell[] b) {
        int offset = 0;
        while (h-- > 0) {
            writeView(x, y++, w, b, offset);
            offset += w;
        }
    }

    /**
     * Writes the same single row from {@code b} repeatedly for {@code h}
     * consecutive rows, starting at {@code (x, y)}.
     * Row 0 of {@code b} is reused for every output row.
     *
     * @param x  left column (view-local)
     * @param y  top row (view-local)
     * @param w  width in columns
     * @param h  number of rows to write
     * @param b  source draw buffer (row 0 is used)
     */
    public void writeLine(int x, int y, int w, int h, JtvDrawBuffer b) {
        while (h-- > 0) {
            writeView(x, y++, w, b.getData(), 0);
        }
    }

    /**
     * Writes {@code count} copies of character {@code c} with colour
     * {@code color} starting at position {@code (x, y)}.
     *
     * @param x     left column (view-local)
     * @param y     row (view-local)
     * @param c     the character to write
     * @param color the one-based palette index
     * @param count the number of character cells to fill
     */
    public void writeChar(int x, int y, char c, int color, int count) {
        if (count > size.getX())
            count = size.getX();
        if (count > 0) {
            JtvScreenCell[] buf = new JtvScreenCell[count];
            JtvColorAttr attr = mapColor(color);
            for (int i = 0; i < count; i++) {
                buf[i] = new JtvScreenCell(c, attr);
            }
            writeView(x, y, count, buf, 0);
        }
    }

    /**
     * Writes string {@code str} with colour {@code color} at position
     * {@code (x, y)}, clipped to the view's width.
     *
     * @param x     left column (view-local)
     * @param y     row (view-local)
     * @param str   the string to write
     * @param color the one-based palette index
     */
    public void writeStr(int x, int y, String str, int color) {
        if (str == null) return;
        int count = Math.min(str.length(), size.getX());
        if (count > 0) {
            JtvScreenCell[] buf = new JtvScreenCell[count];
            JtvColorAttr attr = mapColor(color);
            for (int i = 0; i < count; i++) {
                buf[i] = new JtvScreenCell(str.charAt(i), attr);
            }
            writeView(x, y, count, buf, 0);
        }
    }

    /**
     * Core write method: clips the row to the view's bounds and delegates
     * to {@link #writeToOwner} for occlusion testing and final rendering.
     */
    protected void writeView(int x, int y, int count, JtvScreenCell[] buf, int bufOffset) {
        if (y < 0 || y >= size.getY()) return;
        int x1 = Math.max(x, 0);
        int x2 = Math.min(x + count, size.getX());
        if (x1 >= x2) return;

        writeToOwner(x1, x2, y, buf, bufOffset + (x1 - x), false);
    }

    /**
     * Computes the colour attribute to use for a shadow cell, given the
     * original cell attribute. Preserves the shadow attribute or its
     * inverse when the input already uses those colours.
     *
     * @param attr the original colour attribute
     * @return the shadow colour attribute
     */
    public static JtvColorAttr applyShadow(JtvColorAttr attr) {
        int v = attr.getValue();
        int shadowAttrInv = ((shadowAttr & 0x0F) << 4) | ((shadowAttr & 0xF0) >> 4);
        if (v == shadowAttr || v == shadowAttrInv) return attr;
        return new JtvColorAttr((v & 0xF0) != 0 ? shadowAttr : shadowAttrInv);
    }

    /**
     * Propagates a screen-cell write from this view up to its owner group,
     * accounting for the owner's clip rectangle and the shadow flag.
     */
    protected void writeToOwner(int x1, int x2, int y,
                                      JtvScreenCell[] buf, int bufOffset, boolean inShadow) {
        if ((state & sfVisible) == 0 || owner == null)
            return;

        JtvGroup grp = owner;
        int ay = y + origin.getY();
        int ax1 = x1 + origin.getX();
        int ax2 = x2 + origin.getX();

        if (ay < grp.clip.getA().getY() || ay >= grp.clip.getB().getY())
            return;
        int cx1 = Math.max(ax1, grp.clip.getA().getX());
        int cx2 = Math.min(ax2, grp.clip.getB().getX());
        if (cx1 >= cx2) return;

        int adjBufOffset = bufOffset + (cx1 - ax1);

        grp.writeClipped(this, cx1, cx2, ay, buf, adjBufOffset, inShadow);
    }

    // --- Drag support ---

    /**
     * Moves and/or resizes the view to the position {@code p} with size
     * {@code s}, clamping both to the supplied limits.
     *
     * @param p       the new top-left origin
     * @param s       the new size
     * @param limits  the bounding rectangle the view must stay inside
     * @param minSize the minimum allowed size
     * @param maxSize the maximum allowed size
     * @param mode    drag-mode flags controlling which constraints apply
     */
    public void moveGrow(JtvPoint p, JtvPoint s, JtvRect limits,
                         JtvPoint minSize, JtvPoint maxSize, int mode) {
        s = new JtvPoint(Math.min(Math.max(s.getX(), minSize.getX()), maxSize.getX()),
                         Math.min(Math.max(s.getY(), minSize.getY()), maxSize.getY()));
        p = new JtvPoint(Math.min(Math.max(p.getX(), limits.getA().getX() - s.getX() + 1), limits.getB().getX() - 1),
                         Math.min(Math.max(p.getY(), limits.getA().getY() - s.getY() + 1), limits.getB().getY() - 1));

        if ((mode & dmLimitLoX) != 0)
            p = new JtvPoint(Math.max(p.getX(), limits.getA().getX()), p.getY());
        if ((mode & dmLimitLoY) != 0)
            p = new JtvPoint(p.getX(), Math.max(p.getY(), limits.getA().getY()));
        if ((mode & dmLimitHiX) != 0)
            p = new JtvPoint(Math.min(p.getX(), limits.getB().getX() - s.getX()), p.getY());
        if ((mode & dmLimitHiY) != 0)
            p = new JtvPoint(p.getX(), Math.min(p.getY(), limits.getB().getY() - s.getY()));
        locate(new JtvRect(p.getX(), p.getY(), p.getX() + s.getX(), p.getY() + s.getY()));
    }

    /**
     * Runs the interactive drag/resize loop for this view.
     * <p>
     * If {@code event} is a mouse-down, the loop tracks mouse movement
     * calling {@link #moveGrow} for each position. If {@code event} is a
     * keyboard event the loop reads arrow keys, Home, End, PgUp, PgDn;
     * plain arrows move or grow the view (depending on mode), Shift+arrow
     * resizes/moves in the opposite dimension, Escape cancels (restoring
     * the original bounds), and Enter confirms.
     * <p>
     * The {@code sfDragging} state bit is set for the duration of the loop
     * so the frame can render in its dragging style.
     *
     * @param event   the initiating event (mouse-down or keyboard)
     * @param mode    drag-mode flags ({@code dmDragMove}, {@code dmDragGrow}, etc.)
     * @param limits  the bounding rectangle the view must stay within
     * @param minSize the minimum allowed size
     * @param maxSize the maximum allowed size
     */
    public void dragView(JtvEvent event, int mode, JtvRect limits,
                         JtvPoint minSize, JtvPoint maxSize) {
        setState(sfDragging, true);

        if (event.getWhat() == evMouseDown) {
            JtvPoint p;
            JtvPoint downWhere = new JtvPoint(event.getMouse().getWhere());
            if ((mode & dmDragMove) != 0) {
                p = origin.subtract(downWhere);
                do {
                    JtvPoint moveWhere = event.getMouse().getWhere().add(p);
                    moveGrow(moveWhere, new JtvPoint(size),
                             limits, minSize, maxSize, mode);
                } while (mouseEvent(event, evMouseMove));
            } else if ((mode & dmDragGrow) != 0) {
                p = size.subtract(downWhere);
                do {
                    JtvPoint growWhere = event.getMouse().getWhere().add(p);
                    moveGrow(new JtvPoint(origin), growWhere,
                             limits, minSize, maxSize, mode);
                } while (mouseEvent(event, evMouseMove));
            } else if ((mode & dmDragGrowLeft) != 0) {
                JtvRect start = getBounds();
                int fixedRight = start.getB().getX();
                int fixedTop = start.getA().getY();
                JtvPoint ownerGlobal = new JtvPoint();
                if (owner != null) {
                    ownerGlobal = owner.makeGlobal(new JtvPoint(0, 0));
                }
                JtvPoint cornerGlobal = makeGlobal(new JtvPoint(0, size.getY() - 1));
                p = cornerGlobal.subtract(downWhere);
                do {
                    JtvPoint adjusted = event.getMouse().getWhere().add(p);
                    int newLeft = adjusted.getX() - ownerGlobal.getX();
                    int newBottom = adjusted.getY() - ownerGlobal.getY() + 1;
                    JtvPoint newOrigin = new JtvPoint(newLeft, fixedTop);
                    JtvPoint newSize = new JtvPoint(fixedRight - newLeft, newBottom - fixedTop);
                    moveGrow(newOrigin, newSize, limits, minSize, maxSize, mode);
                } while (mouseEvent(event, evMouseMove));
            }
        } else {
            // Keyboard dragging
            JtvRect saveBounds = getBounds();
            do {
                JtvPoint p = new JtvPoint(origin);
                JtvPoint s = new JtvPoint(size);
                keyEvent(event);
                int kc = event.getKeyDown().getKeyCode();
                JtvPoint delta = new JtvPoint();
                if (kc == KeyEvent.VK_LEFT) delta = new JtvPoint(-1, 0);
                else if (kc == KeyEvent.VK_RIGHT) delta = new JtvPoint(1, 0);
                else if (kc == KeyEvent.VK_UP) delta = new JtvPoint(0, -1);
                else if (kc == KeyEvent.VK_DOWN) delta = new JtvPoint(0, 1);
                else if (kc == KeyEvent.VK_HOME) { p = new JtvPoint(limits.getA().getX(), p.getY()); }
                else if (kc == KeyEvent.VK_END) { p = new JtvPoint(limits.getB().getX() - s.getX(), p.getY()); }
                else if (kc == KeyEvent.VK_PAGE_UP) { p = new JtvPoint(p.getX(), limits.getA().getY()); }
                else if (kc == KeyEvent.VK_PAGE_DOWN) { p = new JtvPoint(p.getX(), limits.getB().getY() - s.getY()); }

                if ((mode & dmDragMove) != 0 && (event.getKeyDown().getModifiers() & InputEvent.SHIFT_DOWN_MASK) == 0)
                    p = p.add(delta);
                else if ((mode & dmDragGrow) != 0 && (event.getKeyDown().getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0)
                    s = s.add(delta);

                moveGrow(p, s, limits, minSize, maxSize, mode);
            } while (event.getKeyDown().getKeyCode() != KeyEvent.VK_ESCAPE &&
                     event.getKeyDown().getKeyCode() != KeyEvent.VK_ENTER);
            if (event.getKeyDown().getKeyCode() == KeyEvent.VK_ESCAPE)
                locate(saveBounds);
        }
        setState(sfDragging, false);
    }

    // --- Utility ---

    /**
     * Delivers a synthetic event directly to {@code receiver}'s
     * {@link #handleEvent} method and returns the event's {@code infoPtr}
     * if the event was consumed (cleared), or {@code null} otherwise.
     *
     * @param receiver the view to deliver the event to
     * @param what     the event type ({@code evBroadcast}, {@code evCommand}, etc.)
     * @param command  the command code in the message
     * @param infoPtr  the arbitrary data object in the message
     * @return the message's {@code infoPtr} if consumed, otherwise {@code null}
     */
    public static Object message(JtvView receiver, int what, int command, Object infoPtr) {
        if (receiver == null) {
            return null;
        }
        JtvEvent event = new JtvEvent();
        event.setWhat(what);
        event.getMessage().setCommand(command);;
        event.getMessage().setInfoPtr(infoPtr);
        receiver.handleEvent(event);
        if (event.getWhat() == evNothing) {
            return event.getMessage().getInfoPtr();
        }
        return null;
    }

    /**
     * Clamps {@code val} to the range {@code [min, max]}.
     * If {@code min > max}, {@code min} is used for both bounds.
     *
     * @param val the value to clamp
     * @param min the lower bound (inclusive)
     * @param max the upper bound (inclusive)
     * @return the clamped value
     */
    protected static int range(int val, int min, int max) {
        if (min > max) min = max;
        return Math.max(min, Math.min(max, val));
    }

    /**
     * The width and height of this view. Together with {@link #origin},
     * it defines the bounding rectangle returned by {@link #getBounds()}.
     */
    public JtvPoint getSize() {
		return size;
	}

	/**
     * The cursor position within the view's local coordinate system.
     * Updated by {@link #setCursor(int, int)} and {@link #resetCursor()}.
     */
    public JtvPoint getCursor() {
		return cursor;
	}

    /**
     * Bitmask of {@code sfXXXX} state constants describing the current
     * visibility, focus, selection, and dragging status of the view.
     * Modify only through {@link #setState(int, boolean)}.
     */
    public int getState() {
		return state;
	}

	/**
     * Bitmask of {@code ofXXXX} option constants controlling selectable,
     * framed, centered, buffered, and other optional behaviour.
     */
    public int getOptions() {
		return options;
	}

	/**
     * Bitmask of {@code ofXXXX} option constants controlling selectable,
     * framed, centered, buffered, and other optional behaviour.
     */
	public void setOptions(int options) {
		this.options = options;
	}

	/**
     * Event-type bitmask that determines which event categories this view
     * wants to receive (e.g. {@code evMouseDown}, {@code evKeyDown},
     * {@code evCommand}). Set to the union of event types handled by
     * {@link #handleEvent(JtvEvent)}.
     */
    public int getEventMask() {
		return eventMask;
	}

	/**
     * Event-type bitmask that determines which event categories this view
     * wants to receive (e.g. {@code evMouseDown}, {@code evKeyDown},
     * {@code evCommand}). Set to the union of event types handled by
     * {@link #handleEvent(JtvEvent)}.
     */
	public void setEventMask(int eventMask) {
		this.eventMask = eventMask;
	}

	/**
     * Grow-mode bitmask of {@code gfXXXX} constants controlling how this
     * view resizes relative to its owner (e.g. {@code gfGrowHiX} to keep
     * the right edge pinned to the owner's right side).
     */
    public int getGrowMode() {
		return growMode;
	}

	/**
     * Grow-mode bitmask of {@code gfXXXX} constants controlling how this
     * view resizes relative to its owner (e.g. {@code gfGrowHiX} to keep
     * the right edge pinned to the owner's right side).
     */
	public void setGrowMode(int growMode) {
		this.growMode = growMode;
	}
}
