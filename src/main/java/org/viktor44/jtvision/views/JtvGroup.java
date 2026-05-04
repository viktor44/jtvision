/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.views;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmError;
import static org.viktor44.jtvision.core.CommandCodes.cmReleasedFocus;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evNothing;
import static org.viktor44.jtvision.core.EventCodes.focusedEvents;
import static org.viktor44.jtvision.core.EventCodes.positionalEvents;
import static org.viktor44.jtvision.core.ViewFlags.hcNoContext;
import static org.viktor44.jtvision.core.ViewFlags.ofBuffered;
import static org.viktor44.jtvision.core.ViewFlags.ofCenterX;
import static org.viktor44.jtvision.core.ViewFlags.ofCenterY;
import static org.viktor44.jtvision.core.ViewFlags.ofPostProcess;
import static org.viktor44.jtvision.core.ViewFlags.ofPreProcess;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.ofValidate;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;
import static org.viktor44.jtvision.core.ViewFlags.sfDisabled;
import static org.viktor44.jtvision.core.ViewFlags.sfDragging;
import static org.viktor44.jtvision.core.ViewFlags.sfExposed;
import static org.viktor44.jtvision.core.ViewFlags.sfFocused;
import static org.viktor44.jtvision.core.ViewFlags.sfModal;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;
import static org.viktor44.jtvision.core.ViewFlags.sfShadow;
import static org.viktor44.jtvision.core.ViewFlags.sfVisible;

import org.viktor44.jtvision.core.JtvCommandSet;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.core.JtvScreenCell;
import org.viktor44.jtvision.platform.Screen;

import lombok.extern.slf4j.Slf4j;

/**
 * A container view that owns and manages an ordered list of child views.
 * <p>
 * JtvGroup extends {@link JtvView} to hold a dynamically chained list of
 * related views. It handles:
 * <ul>
 *   <li><b>Insertion and removal</b> — {@link #insert(JtvView)},
 *       {@link #remove(JtvView)}, {@link #destroy(JtvView)}.</li>
 *   <li><b>Event routing</b> — distributes events to children in three
 *       phases: {@code phPreProcess}, {@code phFocused}, {@code phPostProcess}.</li>
 *   <li><b>Focus management</b> — {@link #setCurrent(JtvView, int)},
 *       {@link #resetCurrent()}, {@link #selectNext(boolean)}.</li>
 *   <li><b>Modal execution</b> — {@link #execute()} runs an event loop;
 *       {@link #execView(JtvView)} runs a child view modally.</li>
 *   <li><b>Double-buffered drawing</b> — when the {@code ofBuffered} option
 *       is set (the default), the group draws all children into an off-screen
 *       {@link JtvDrawBuffer} and blits it to the screen in one pass.</li>
 *   <li><b>Resize propagation</b> — {@link #changeBounds(JtvRect)} calls
 *       {@link JtvView#calcBounds} and {@link JtvView#changeBounds} on every
 *       child according to its grow-mode flags.</li>
 * </ul>
 * <p>
 * The child list is a circular singly-linked ring maintained through
 * {@link JtvView#next}. {@link #last} points to the back of the list
 * (the view with the lowest z-order); {@link #first()} returns the
 * front (highest z-order). Views are inserted at the front by default.
 * <p>
 * Groups are created with {@code ofSelectable | ofBuffered} and an
 * {@link #eventMask} of {@code 0xFFFF} so they receive all event types.
 *
 * @see JtvView
 * @see JtvWindow
 */
@Slf4j
public class JtvGroup extends JtvView {

    /**
     * The currently focused (selected) child view, or {@code null} if no
     * child is selected. Set by {@link #setCurrent(JtvView, int)}.
     */
    protected JtvView current;

    /**
     * The last (back-most) view in the circular linked-list of children,
     * or {@code null} when the group is empty. The front-most child is
     * {@code last.next}.
     */
    protected JtvView last;

    /**
     * The current event-routing phase: {@link JtvView#phFocused},
     * {@link JtvView#phPreProcess}, or {@link JtvView#phPostProcess}.
     */
    protected int phase;

    /**
     * The off-screen draw buffer used when {@code ofBuffered} is set.
     * Allocated lazily when the group is first drawn; freed when the
     * group is hidden ({@code sfExposed} is cleared).
     */
    protected JtvDrawBuffer buffer;

    /**
     * Lock counter used to defer {@link #drawView()} calls while subviews
     * are being moved or resized. The group is drawn once when this counter
     * drops back to zero.
     */
    protected int lockFlag;

    /**
     * The command code that caused the current modal {@link #execute()} loop
     * to exit. Set by {@link #endModal(int)}.
     */
    protected int endState;

    /**
     * The clipping rectangle used during drawing. Normally equals the group's
     * extent, but is temporarily narrowed during occlusion-aware redraws.
     */
    protected JtvRect clip;

    /**
     * Constructs a group with the given bounds.
     * <p>
     * Initialises options to {@code ofSelectable | ofBuffered},
     * event mask to {@code 0xFFFF} (all events), phase to
     * {@link JtvView#phFocused}, and clip to the full extent.
     *
     * @param bounds the initial bounding rectangle
     */
    public JtvGroup(JtvRect bounds) {
        super(bounds);
        current = null;
        last = null;
        phase = phFocused;
        buffer = null;
        lockFlag = 0;
        endState = 0;
        options = ofSelectable | ofBuffered;
        clip = getExtent();
        eventMask = 0xFFFF;
    }

    /**
     * Hides and destroys all child views, releases the draw buffer,
     * and then calls the inherited {@link JtvView#shutDown()}.
     */
    @Override
    public void shutDown() {
        if (last != null) {
            JtvView p = last;
            do {
                p.hide();
                p = p.prev();
            }
            while (p != last);

            do {
                JtvView t = p.prev();
                destroy(p);
                p = t;
            }
            while (last != null);
        }
        freeBuffer();
        current = null;
        super.shutDown();
    }

    /**
     * Removes and shuts down a child view, freeing its resources.
     * If {@code p} is {@code null} the call is ignored.
     *
     * @param p the child view to destroy
     */
    public void destroy(JtvView p) {
        if (p != null) {
            remove(p);
            p.shutDown();
        }
    }

    /**
     * Calls {@link JtvView#awaken()} on every child view. Overrides the
     * no-op in {@link JtvView} to propagate the awaken notification
     * through the whole group hierarchy.
     */
    @Override
    public void awaken() {
        forEach(JtvView::awaken);
    }

    /**
     * Resizes the group to the new bounds and proportionally adjusts every
     * child view according to its {@link JtvView#growMode} flags.
     * <p>
     * If the size does not change the group is simply redrawn. If the size
     * changes, the group locks drawing, recalculates every child's bounds,
     * then unlocks (triggering a single redraw).
     *
     * @param bounds the new bounding rectangle
     */
    @Override
    public void changeBounds(JtvRect bounds) {
        JtvPoint d = new JtvPoint(
            (bounds.getB().getX() - bounds.getA().getX()) - size.getX(),
            (bounds.getB().getY() - bounds.getA().getY()) - size.getY()
        );
        if (d.getX() == 0 && d.getY() == 0) {
            setBounds(bounds);
            drawView();
        }
        else {
            setBounds(bounds);
            clip = getExtent();
            initBuffer();
            lock();
            forEach((v) -> {
                JtvRect r = new JtvRect();
                v.calcBounds(r, d);
                v.changeBounds(r);
            });
            unlock();
        }
    }

    /**
     * Returns the total data record size of all child views by summing
     * each child's {@link JtvView#dataSize()}.
     *
     * @return the combined data size in bytes
     */
    @Override
    public int dataSize() {
        int total = 0;
        if (last != null) {
            JtvView v = last;
            do {
                total += v.dataSize();
                v = v.prev();
            }
            while (v != last);
        }
        return total;
    }

    /**
     * Removes a child view from this group without destroying it.
     * <p>
     * The view is temporarily hidden, unlinked, its {@link JtvView#owner}
     * and {@link JtvView#next} are cleared, and then it is made visible
     * again if it was visible before removal.
     *
     * @param p the child view to remove, or {@code null} (ignored)
     */
    public void remove(JtvView p) {
        if (p == null) {
        	return;
        }
        int saveState = p.state;
        p.hide();
        removeView(p);
        p.owner = null;
        p.next = null;
        if ((saveState & sfVisible) != 0) {
            p.show();
        }
    }

    /**
     * Draws the group by blitting its off-screen buffer (if available),
     * or by re-drawing all child views within the current clip rectangle.
     * If the buffer does not exist it is lazily allocated here.
     */
    @Override
    public void draw() {
        if (buffer == null) {
            initBuffer();
            if (buffer != null) {
                lockFlag++;
                redraw();
                lockFlag--;
            }
        }
        if (buffer != null) {
            writeBuf(0, 0, size.getX(), size.getY(), buffer);
        }
        else {
            clip = getClipRect();
            redraw();
            clip = getExtent();
        }
    }

    /**
     * Calls {@link JtvView#drawView()} on every child view from {@code p}
     * up to (but not including) {@code bottom}.
     *
     * @param p      the first child to draw (in list order)
     * @param bottom the child at which to stop, or {@code null} to draw all
     */
    protected void drawSubViews(JtvView p, JtvView bottom) {
        while (p != bottom) {
            if (p == null) break;
            p.drawView();
            p = p.nextView();
        }
    }

    /**
     * Ends the modal state for this group if it is currently modal,
     * or delegates to the parent via {@link JtvView#endModal(int)}.
     *
     * @param command the command code to return from {@link #execute()}
     */
    @Override
    public void endModal(int command) {
        if ((state & sfModal) != 0) {
            endState = command;
        }
        else {
            super.endModal(command);
        }
    }

    /**
     * Called when an event is not consumed by any child view. Delegates
     * to the owner group, propagating unhandled events up the hierarchy.
     *
     * @param event the unhandled event
     */
    public void eventError(JtvEvent event) {
        if (owner != null) {
            owner.eventError(event);
        }
    }

    /**
     * Runs the group's event loop until {@link #valid(int)} returns
     * {@code true} for the exit command.
     * <p>
     * Each iteration fetches one event, routes it through
     * {@link #handleEvent(JtvEvent)}, and passes unhandled events to
     * {@link #eventError(JtvEvent)}. Runtime exceptions are wrapped into
     * {@code cmError} command events via {@link #handleRuntimeException}.
     *
     * @return the command that caused the loop to exit
     */
    @Override
    public int execute() {
        do {
            endState = 0;
            do {
                JtvEvent e = new JtvEvent();
                getEvent(e);
                try {
                    handleEvent(e);
                    if (e.getWhat() != evNothing) {
                        eventError(e);
                    }
                }
                catch (Throwable ex) {
                    handleException(ex);
                }
            }
            while (endState == 0);
        }
        while (!valid(endState));

        return endState;
    }

    /**
     * Wraps an uncaught {@link RuntimeException} in a {@code cmError}
     * command event and delivers it to {@link #eventError(JtvEvent)}.
     * A second exception thrown from the error handler is silently swallowed.
     *
     * @param ex the runtime exception to report
     */
    protected void handleException(Throwable ex) {
    	log.error("", ex);
        JtvEvent errorEvent = new JtvEvent();
        errorEvent.setWhat(evCommand);
        errorEvent.getMessage().setCommand(cmError);
        errorEvent.getMessage().setInfoPtr(ex);
        try {
            eventError(errorEvent);
        }
        catch (RuntimeException ignored) {
        }
    }

    /**
     * Runs a child view {@code p} as a modal subview of this group.
     * <p>
     * The method saves the current modal context (top-view, focused child,
     * command set), promotes {@code p} to modal, calls its
     * {@link JtvView#execute()} method, and then restores the saved context.
     * If {@code p} has no owner it is temporarily inserted into this group
     * and removed afterwards.
     *
     * @param p the view to execute modally, or {@code null} (returns
     *          {@code cmCancel} immediately)
     * @return the command that ended the modal session
     */
    public int execView(JtvView p) {
        if (p == null) {
            return cmCancel;
        }

        int saveOptions = p.options;
        JtvGroup saveOwner = p.owner;
        JtvView saveTopView = theTopView;
        JtvView saveCurrent = current;
        JtvCommandSet saveCommands = new JtvCommandSet();
        getCommands(saveCommands);
        theTopView = p;
        p.options = p.options & ~ofSelectable;
        p.setState(sfModal, true);
        setCurrent(p, enterSelect);
        if (saveOwner == null) {
            insert(p);
        }
        int retval = p.execute();
        if (saveOwner == null) {
            remove(p);
        }
        setCurrent(saveCurrent, leaveSelect);
        p.setState(sfModal, false);
        p.options = saveOptions;
        theTopView = saveTopView;
        setCommands(saveCommands);
        return retval;
    }

    /**
     * Returns the first (front-most) child view in the group, or
     * {@code null} if the group is empty.
     *
     * @return the first child view, or {@code null}
     */
    public JtvView first() {
        if (last == null) {
            return null;
        }
        return last.next;
    }

    /**
     * Finds the next focusable sibling after (or before) {@link #current}
     * in the direction indicated by {@code forwards}.
     * <p>
     * A view is focusable if it is visible, not disabled, and has
     * {@code ofSelectable} set. Returns {@code null} if no other focusable
     * sibling exists.
     *
     * @param forwards {@code true} to search forward; {@code false} backward
     * @return the next focusable sibling, or {@code null}
     */
    public JtvView findNext(boolean forwards) {
        if (current == null) {
        	return null;
        }
        JtvView p = current;
        do {
            if (forwards) {
                p = p.next;
            }
            else {
                p = p.prev();
            }
        }
        while (!(((p.state & (sfVisible | sfDisabled)) == sfVisible)
        		&& (p.options & ofSelectable) != 0) && p != current);
        if (p != current) {
            return p;
        }
        return null;
    }

    /**
     * Focuses the next selectable sibling in the given direction by calling
     * {@link JtvView#focus()} on the result of {@link #findNext(boolean)}.
     *
     * @param forwards {@code true} to move focus forward; {@code false} backward
     * @return {@code true} if focus was successfully transferred or no change
     *         was needed
     */
    public boolean focusNext(boolean forwards) {
        JtvView p = findNext(forwards);
        if (p != null) {
            return p.focus();
        }
        return true;
    }

    /**
     * Returns the first child view that has all bits in {@code aState} set
     * and all bits in {@code aOptions} set. Iterates the list from
     * {@link #last} backwards. Returns {@code null} if none matches.
     *
     * @param aState   the state bits to require
     * @param aOptions the option bits to require
     * @return the first matching view, or {@code null}
     */
    public JtvView firstMatch(int aState, int aOptions) {
        if (last == null) {
            return null;
        }
        JtvView temp = last;
        do {
            if ((temp.state & aState) == aState && (temp.options & aOptions) == aOptions) {
                return temp;
            }
            temp = temp.next;
        }
        while (temp != last);
        return null;
    }

    /**
     * Releases the off-screen draw buffer if {@code ofBuffered} is set.
     * Called when the group is hidden so that memory is returned while the
     * group is not visible.
     */
    public void freeBuffer() {
        if ((options & ofBuffered) != 0) {
            buffer = null;
        }
    }

    /**
     * Allocates the off-screen draw buffer if the group is exposed and
     * {@code ofBuffered} is set. The buffer is sized to hold the entire
     * group ({@code width × height} cells).
     */
    private void initBuffer() {
        if ((state & sfExposed) != 0 && (options & ofBuffered) != 0) {
            int sz = size.getX() * size.getY();
            if (sz > 0) {
                buffer = new JtvDrawBuffer(sz);
            }
        }
    }

    /**
     * Routes {@code event} through the three-phase delivery mechanism:
     * <ol>
     *   <li>{@link JtvView#phPreProcess} — views with {@code ofPreProcess}.</li>
     *   <li>{@link JtvView#phFocused} — the current focused view for keyboard/
     *       command events, or the topmost view under the mouse for
     *       positional events.</li>
     *   <li>{@link JtvView#phPostProcess} — views with {@code ofPostProcess}.</li>
     * </ol>
     * The base {@link JtvView#handleEvent} is called first to handle
     * mouse-down focus transfers.
     *
     * @param event the event to route
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);

        if ((event.getWhat() & focusedEvents) != 0) {
            phase = phPreProcess;
            forEachHandleEvent(event);

            phase = phFocused;
            doHandleEvent(current, event);

            phase = phPostProcess;
            forEachHandleEvent(event);
        }
        else if (event.getWhat() != evNothing) {
            phase = phFocused;
            if ((event.getWhat() & positionalEvents) != 0) {
                JtvView target = firstThat(v -> v.containsMouse(event));
                doHandleEvent(target, event);
            }
            else {
                forEachHandleEvent(event);
            }
        }
    }

    /** Delivers {@code event} to a single child view, respecting phase and disable state. */
    private void doHandleEvent(JtvView p, JtvEvent event) {
        if (p == null) {
        	return;
        }
        if ((p.state & sfDisabled) != 0 && (event.getWhat() & (positionalEvents | focusedEvents)) != 0) {
            return;
        }

        switch (phase) {
            case phPreProcess:
                if ((p.options & ofPreProcess) == 0) {
                	return;
                }
                break;
            case phPostProcess:
                if ((p.options & ofPostProcess) == 0) {
                	return;
                }
                break;
        }
        if ((event.getWhat() & p.eventMask) != 0) {
            p.handleEvent(event);
        }
    }

    /** Iterates all children from back to front, calling {@link #doHandleEvent} on each. */
    private void forEachHandleEvent(JtvEvent event) {
        if (last == null) {
        	return;
        }
        JtvView p = last;
        do {
            JtvView next = p.prev(); // iterate in reverse (last to first)
            doHandleEvent(p, event);
            p = next;
        }
        while (p != last);
    }

    /**
     * Inserts {@code p} into this group as the front-most child view.
     * If {@code p} has the {@code ofCenterX} or {@code ofCenterY} option
     * set, its origin is adjusted to centre it within the group.
     *
     * @param p the view to insert; must not already have an owner
     */
    public void insert(JtvView p) {
        insertBefore(p, first());
    }

    /** Inserts {@code p} immediately before {@code target} in the z-order. */
    private void insertBefore(JtvView p, JtvView target) {
        if (p != null && p.owner == null && (target == null || target.owner == this)) {
            if ((p.options & ofCenterX) != 0) {
                p.origin = new JtvPoint((size.getX() - p.size.getX()) / 2, p.origin.getY());
            }
            if ((p.options & ofCenterY) != 0) {
                p.origin = new JtvPoint(p.origin.getX(), (size.getY() - p.size.getY()) / 2);
            }
            int saveState = p.state;
            p.hide();
            insertView(p, target);
            if ((saveState & sfVisible) != 0) {
                p.show();
            }
            if ((saveState & sfActive) != 0) {
                p.setState(sfActive, true);
            }
        }
    }

    /**
     * Low-level insertion: links {@code p} into the circular list
     * immediately before {@code target} (or at the end if {@code target}
     * is {@code null}), and sets {@link JtvView#owner} to this group.
     *
     * @param p      the view to link in
     * @param target the view to insert before, or {@code null} to append
     */
    protected void insertView(JtvView p, JtvView target) {
        p.owner = this;
        if (target != null) {
            JtvView prev = target.prev();
            p.next = prev.next;
            prev.next = p;
        }
        else {
            if (last == null) {
                p.next = p;
            }
            else {
                p.next = last.next;
                last.next = p;
            }
            last = p;
        }
    }

    /**
     * Low-level removal: unlinks {@code p} from the circular list without
     * touching its owner or next pointers (those are cleared by
     * {@link #remove(JtvView)}).
     *
     * @param p the view to unlink
     */
    protected void removeView(JtvView p) {
        if (last == null) {
        	return;
        }
        JtvView cur = last;
        while (cur.next != p) {
            if (cur.next == last) {
            	return; // not found
            }
            cur = cur.next;
        }
        cur.next = p.next;
        if (last == p) {
            if (cur == p) {
                last = null;
            }
            else {
                last = cur;
            }
        }
    }

    /**
     * Increments the draw lock counter, deferring any {@link #drawView()}
     * calls until {@link #unlock()} brings the counter back to zero.
     * Has no effect if neither a buffer nor an existing lock is present.
     */
    protected void lock() {
        if (buffer != null || lockFlag != 0) {
            lockFlag++;
        }
    }

    /**
     * Decrements the draw lock counter. When it reaches zero, triggers a
     * deferred {@link #drawView()} to flush all buffered changes.
     */
    protected void unlock() {
        if (lockFlag != 0 && --lockFlag == 0) {
            drawView();
        }
    }

    /**
     * Redraws all child views by calling {@link #drawSubViews} from
     * {@link #first()} to the end of the list.
     */
    public void redraw() {
        drawSubViews(first(), null);
    }

    /**
     * Selects the first visible, selectable child as the current focused
     * view by calling {@link #setCurrent} with {@link JtvView#normalSelect}.
     */
    public void resetCurrent() {
        setCurrent(firstMatch(sfVisible, ofSelectable), normalSelect);
    }

    /**
     * Delegates the cursor-reset to the currently focused child.
     * If no child is focused the hardware cursor is not updated.
     */
    @Override
    public void resetCursor() {
        if (current != null) {
            current.resetCursor();
        }
    }

    /**
     * Moves focus to the next selectable sibling in the given direction.
     * Has no effect if no current view is focused.
     *
     * @param forwards {@code true} to move to the next sibling;
     *                 {@code false} to move to the previous sibling
     */
    public void selectNext(boolean forwards) {
        if (current != null) {
            JtvView p = findNext(forwards);
            if (p != null) {
            	p.select();
            }
        }
    }

    /**
     * Sets or clears the {@code sfSelected} state on view {@code p}.
     *
     * @param p      the view to change, or {@code null} (ignored)
     * @param enable {@code true} to select; {@code false} to deselect
     */
    public void selectView(JtvView p, boolean enable) {
        if (p != null) {
            p.setState(sfSelected, enable);
        }
    }

    /**
     * Sets or clears the {@code sfFocused} state on view {@code p},
     * but only if this group itself is focused.
     *
     * @param p      the view to change, or {@code null} (ignored)
     * @param enable {@code true} to focus; {@code false} to unfocus
     */
    public void focusView(JtvView p, boolean enable) {
        if ((state & sfFocused) != 0 && p != null) {
            p.setState(sfFocused, enable);
        }
    }

    /**
     * Changes the currently focused child to {@code p} using the selection
     * mode {@code mode}. Handles transferring {@code sfSelected} and
     * {@code sfFocused} state bits between the old and new current views.
     *
     * @param p    the new current view, or {@code null} to clear selection
     * @param mode {@link JtvView#normalSelect}, {@link JtvView#enterSelect},
     *             or {@link JtvView#leaveSelect}
     */
    public void setCurrent(JtvView p, int mode) {
        if (current != p) {
            lock();
            focusView(current, false);
            if (mode != enterSelect && current != null) {
                current.setState(sfSelected, false);
            }
            if (mode != leaveSelect && p != null) {
                p.setState(sfSelected, true);
            }
            if ((state & sfFocused) != 0 && p != null) {
                p.setState(sfFocused, true);
            }
            current = p;
            unlock();
        }
    }

    /**
     * No-op data-import override. Subclasses may override to distribute
     * data records to child views.
     *
     * @param rec the source data record
     */
    @Override
    public void setDataFrom(Object rec) {
        // Subclasses may implement
    }

    /**
     * Propagates state changes to all children and handles special cases:
     * <ul>
     *   <li>{@code sfActive} / {@code sfDragging} — forwarded to all children.</li>
     *   <li>{@code sfFocused} — forwarded to the current child.</li>
     *   <li>{@code sfExposed} — forwarded to all visible children; clearing
     *       it also frees the draw buffer.</li>
     * </ul>
     *
     * @param aState the state bits to modify
     * @param enable {@code true} to set; {@code false} to clear
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);

        if ((aState & (sfActive | sfDragging)) != 0) {
            lock();
            forEach(v -> v.setState(aState, enable));
            unlock();
        }

        if ((aState & sfFocused) != 0) {
            if (current != null)
                current.setState(sfFocused, enable);
        }

        if ((aState & sfExposed) != 0) {
            forEach(v -> {
                if ((v.state & sfVisible) != 0) {
                    v.setState(sfExposed, enable);
                }
            });
            if (!enable) {
                freeBuffer();
            }
        }
    }

    /**
     * Validates all child views for the given command. For
     * {@code cmReleasedFocus} only the current child (if it has
     * {@code ofValidate}) is checked; for all other commands every child
     * is validated.
     *
     * @param command the command requesting validation
     * @return {@code true} if all applicable children return {@code true}
     */
    @Override
    public boolean valid(int command) {
        if (command == cmReleasedFocus) {
            if (current != null && (current.options & ofValidate) != 0) {
                return current.valid(command);
            }
            return true;
        }

        if (last == null) {
        	return true;
        }
        JtvView p = last;
        do {
            if (!p.valid(command)) {
                return false;
            }
            p = p.prev();
        }
        while (p != last);
        return true;
    }

    /**
     * Returns the effective help-context ID. Asks the current child first;
     * if it returns {@code hcNoContext}, falls back to the group's own
     * help context via the parent {@link JtvView#getHelpCtx()}.
     *
     * @return the current help-context ID
     */
    @Override
    public int getHelpCtx() {
        int h = hcNoContext;
        if (current != null) {
            h = current.getHelpCtx();
        }
        if (h == hcNoContext) {
            h = super.getHelpCtx();
        }
        return h;
    }

    // --- Iteration helpers ---

    /**
     * Applies {@code action} to every child view exactly once,
     * iterating the circular list safely even if {@code action} modifies
     * the list.
     *
     * @param action the consumer to invoke for each child view
     */
    public void forEach(java.util.function.Consumer<JtvView> action) {
        if (last == null) {
            return;
        }
        JtvView term = last;
        JtvView temp = last;
        JtvView next = temp.next;
        do {
            temp = next;
            next = temp.next;
            action.accept(temp);
        }
        while (temp != term);
    }

    /**
     * Returns the first child view for which {@code test} returns
     * {@code true}, or {@code null} if none matches.
     *
     * @param test the predicate to apply
     * @return the first matching child, or {@code null}
     */
    public JtvView firstThat(java.util.function.Predicate<JtvView> test) {
        if (last == null) {
        	return null;
        }
        JtvView p = last;
        do {
            p = p.next;
            if (test.test(p)) {
                return p;
            }
        }
        while (p != last);

        return null;
    }

    /**
     * Returns the 1-based index of view {@code p} within the circular
     * list (starting at 1 for {@link #first()}), or {@code 0} if not found.
     *
     * @param p the view whose index to find
     * @return the 1-based position, or {@code 0} if not a child
     */
    public int indexOf(JtvView p) {
        if (last == null) {
        	return 0;
        }
        int index = 0;
        JtvView v = last;
        do {
            index++;
            v = v.next;
        }
        while (v != p && v != last);

        return (v == p) ? index : 0;
    }

    /**
     * Returns the child view at 1-based {@code index} in the circular list,
     * or {@code null} if the group is empty or the index is invalid.
     *
     * @param index 1-based position (1 = first/front-most)
     * @return the view at that position, or {@code null}
     */
    public JtvView at(int index) {
        if (last == null || index <= 0) {
        	return null;
        }
        JtvView v = last;
        for (int i = 0; i < index; i++) {
            v = v.next;
        }
        return v;
    }

    // --- Screen write helpers ---

    /**
     * Writes cells into the group's buffer or screen, skipping over any
     * sibling views that occlude the target area and applying shadow colour
     * transformations where needed.
     *
     * @param target    the child view that is writing (occlusion is checked
     *                  only for children in front of it)
     * @param x1        start column (group-local, inclusive)
     * @param x2        end column (group-local, exclusive)
     * @param y         row (group-local)
     * @param buf       source cell array
     * @param bufOffset starting index into {@code buf}
     * @param inShadow  {@code true} if this segment should be rendered as shadow
     */
    protected void writeClipped(JtvView target,
                                int x1, int x2, int y,
                                JtvScreenCell[] buf, int bufOffset,
                                boolean inShadow) {
        if (x1 >= x2) return;

        if (last != null) {
            JtvView v = last.next;
            while (v != target) {
                if ((v.state & sfVisible) != 0) {
                    int vx1 = v.origin.getX();
                    int vx2 = vx1 + v.size.getX();
                    int vy1 = v.origin.getY();
                    int vy2 = vy1 + v.size.getY();
                    boolean hasShadow = (v.state & sfShadow) != 0;

                    int shadowX1 = -1, shadowX2 = -1;

                    if (y >= vy1 && y < vy2) {
                        if (x1 < vx1 && x2 > vx1) {
                            int preEnd = Math.min(x2, vx1);
                            writeClippedAfter(v, target, x1, preEnd, y, buf, bufOffset, inShadow);
                            if (x2 <= vx1) return;
                            bufOffset += (vx1 - x1);
                            x1 = vx1;
                        }
                        if (x1 < vx2 && x1 >= vx1) {
                            if (x2 <= vx2) return;
                            bufOffset += (vx2 - x1);
                            x1 = vx2;
                        }
                        if (hasShadow && y >= vy1 + shadowSize.getY()) {
                            shadowX1 = vx2;
                            shadowX2 = vx2 + shadowSize.getX();
                        }
                    } else if (hasShadow && y >= vy2 && y < vy2 + shadowSize.getY()) {
                        shadowX1 = vx1 + shadowSize.getX();
                        shadowX2 = vx2 + shadowSize.getX();
                    }

                    if (shadowX1 >= 0 && x1 < shadowX2 && x2 > shadowX1) {
                        if (x1 < shadowX1) {
                            int preEnd = Math.min(x2, shadowX1);
                            writeClippedAfter(v, target, x1, preEnd, y, buf, bufOffset, inShadow);
                            if (x2 <= shadowX1) return;
                            bufOffset += (shadowX1 - x1);
                            x1 = shadowX1;
                        }
                        int shEnd = Math.min(x2, shadowX2);
                        writeClippedAfter(v, target, x1, shEnd, y, buf, bufOffset, true);
                        if (x2 <= shadowX2) return;
                        bufOffset += (shadowX2 - x1);
                        x1 = shadowX2;
                    }
                }
                v = v.next;
            }
        }

        writeFinal(x1, x2, y, buf, bufOffset, inShadow);
    }

    /** Continuation of {@link #writeClipped} starting after view {@code after}. */
    private void writeClippedAfter(JtvView after, JtvView target,
                                   int x1, int x2, int y,
                                   JtvScreenCell[] buf, int bufOffset,
                                   boolean inShadow) {
        if (x1 >= x2) return;
        JtvView v = after.next;
        while (v != target) {
            if ((v.state & sfVisible) != 0) {
                int vx1 = v.origin.getX();
                int vx2 = vx1 + v.size.getX();
                int vy1 = v.origin.getY();
                int vy2 = vy1 + v.size.getY();
                boolean hasShadow = (v.state & sfShadow) != 0;

                int shadowX1 = -1, shadowX2 = -1;

                if (y >= vy1 && y < vy2) {
                    if (x1 < vx1 && x2 > vx1) {
                        int preEnd = Math.min(x2, vx1);
                        writeClippedAfter(v, target, x1, preEnd, y, buf, bufOffset, inShadow);
                        if (x2 <= vx1) return;
                        bufOffset += (vx1 - x1);
                        x1 = vx1;
                    }
                    if (x1 < vx2 && x1 >= vx1) {
                        if (x2 <= vx2) return;
                        bufOffset += (vx2 - x1);
                        x1 = vx2;
                    }
                    if (hasShadow && y >= vy1 + shadowSize.getY()) {
                        shadowX1 = vx2;
                        shadowX2 = vx2 + shadowSize.getX();
                    }
                } else if (hasShadow && y >= vy2 && y < vy2 + shadowSize.getY()) {
                    shadowX1 = vx1 + shadowSize.getX();
                    shadowX2 = vx2 + shadowSize.getX();
                }

                if (shadowX1 >= 0 && x1 < shadowX2 && x2 > shadowX1) {
                    if (x1 < shadowX1) {
                        int preEnd = Math.min(x2, shadowX1);
                        writeClippedAfter(v, target, x1, preEnd, y, buf, bufOffset, inShadow);
                        if (x2 <= shadowX1) return;
                        bufOffset += (shadowX1 - x1);
                        x1 = shadowX1;
                    }
                    int shEnd = Math.min(x2, shadowX2);
                    writeClippedAfter(v, target, x1, shEnd, y, buf, bufOffset, true);
                    if (x2 <= shadowX2) return;
                    bufOffset += (shadowX2 - x1);
                    x1 = shadowX2;
                }
            }
            v = v.next;
        }
        writeFinal(x1, x2, y, buf, bufOffset, inShadow);
    }

    /**
     * Writes cells to the off-screen buffer (if any) and/or the screen,
     * applying shadow colour transformation when {@code inShadow} is true.
     * When the draw lock is held, only the buffer is written (no screen
     * output). When the group has no owner, writes directly to the screen.
     *
     * @param x1       start column (inclusive)
     * @param x2       end column (exclusive)
     * @param y        row
     * @param buf      source cell array
     * @param bufOffset starting index into {@code buf}
     * @param inShadow {@code true} to apply shadow colour transformation
     */
    protected void writeFinal(int x1, int x2, int y,
                              JtvScreenCell[] buf, int bufOffset, boolean inShadow) {
        if (buffer != null) {
            JtvScreenCell[] dst = buffer.getData();
            for (int x = x1; x < x2; x++) {
                int idx = y * size.getX() + x;
                int bi = bufOffset + x - x1;
                if (idx >= 0 && idx < dst.length && bi >= 0 && bi < buf.length) {
                    JtvScreenCell src = buf[bi];
                    if (inShadow) {
                        dst[idx] = new JtvScreenCell(src.getCh(), applyShadow(src.getAttr()));
                    } else {
                        dst[idx] = src;
                    }
                }
            }
        }

        if (lockFlag == 0) {
            if (owner != null) {
                writeToOwner(x1, x2, y, buf, bufOffset, inShadow);
            } else {
                Screen.writeToScreen(x1, x2, y, buf, bufOffset, inShadow);
            }
        }
    }


}
