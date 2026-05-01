package org.viktor44.jtvision.views;

import static org.viktor44.jtvision.core.CommandCodes.cmScrollBarChanged;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;

import lombok.Getter;
import lombok.Setter;

/**
 * A scrollable view that provides a viewport onto a larger virtual surface.
 * <p>
 * TScroller extends {@link JtvView} to link two optional {@link JtvScrollBar}
 * controls to a scrolling offset ({@link #delta}) that subclasses use in
 * their {@link #draw()} method to determine which portion of a larger
 * content area to render.
 * 
 * <h3>How scrolling works</h3>
 * 
 * When either scroll bar's value changes, the scroll bar broadcasts
 * {@code cmScrollBarChanged}. TScroller receives this broadcast in
 * {@link #handleEvent(JtvEvent)} and calls {@link #scrollDraw()}, which
 * reads the new values from both scroll bars, updates {@link #delta}, and
 * redraws the view. Subclasses render the part of their content starting
 * at {@link #delta}.
 * <p>
 * Programmatic scrolling is done via {@link #scrollTo(int, int)}, which
 * sets both scroll bar values and triggers a redraw through the same
 * broadcast path. The virtual content size is reported via
 * {@link #setLimit(int, int)}, which adjusts the scroll bar ranges to
 * {@code [0, limit - viewSize]}.
 * 
 * <h3>Draw locking</h3>
 * 
 * The {@link #drawLock} counter defers redraws while multiple scroll bar
 * parameters are being updated. When the counter drops to zero,
 * {@link #checkDraw()} triggers a deferred redraw if {@link #drawFlag}
 * was set.
 * 
 * <h3>Scroll bar visibility</h3>
 * 
 * Scroll bars are shown only when the scroller is both active
 * ({@code sfActive}) and selected ({@code sfSelected}). The
 * {@link #setState(int, boolean)} override calls
 * {@link #showSBar(JtvScrollBar)} whenever either flag changes.
 * 
 * <h3>Default palette</h3>
 * 
 * The two-entry palette {@link #cpScroller} maps to:
 * <ol>
 *   <li>{@code 6} — normal text colour.</li>
 *   <li>{@code 7} — selected/highlighted colour.</li>
 * </ol>
 *
 * @see JtvScrollBar
 * @see JtvTextDevice
 */
public class JtvScroller extends JtvView {

    /**
     * The current horizontal and vertical scroll offset.
     * Subclasses add this to row/column indices when drawing their content.
     */
	@Getter
	@Setter
    protected JtvPoint delta = new JtvPoint();

	/**
     * The total virtual content size. Scroll bar ranges are computed from
     * this field via {@link #setLimit(int, int)}: the scroll bar maximum is {@code limit - viewSize}.
     */
	@Getter
	@Setter
    protected JtvPoint limit = new JtvPoint();

	/**
     * The optional horizontal scroll bar. {@code null} if this scroller
     * has no horizontal scrolling.
     */
    protected JtvScrollBar hScrollBar;

    /**
     * The optional vertical scroll bar. {@code null} if this scroller
     * has no vertical scrolling.
     */
    protected JtvScrollBar vScrollBar;

    /**
     * Deferred-redraw lock counter. Incremented by callers that update
     * multiple scroll bar parameters in one logical operation; decremented
     * by {@link #checkDraw()} after the updates are complete.
     */
	@Getter
	@Setter
    protected int drawLock;

	/**
     * Set to {@code true} when a redraw is pending but deferred because
     * {@link #drawLock} is non-zero. Cleared and actioned by
     * {@link #checkDraw()} when the lock reaches zero.
     */
	@Getter
	@Setter
    protected boolean drawFlag;

	/**
     * Two-entry colour palette:
     * <ol>
     *   <li>{@code 6} — normal text.</li>
     *   <li>{@code 7} — highlighted text.</li>
     * </ol>
     */
    private static final JtvPalette cpScroller = new JtvPalette(new int[] {6, 7});

    /**
     * Constructs a scroller with the given bounds and optional scroll bars.
     * <p>
     * Sets {@code ofSelectable} on options so the scroller can receive focus,
     * and adds {@code evBroadcast} to the event mask so it receives
     * {@code cmScrollBarChanged} broadcasts.
     *
     * @param bounds       the bounding rectangle
     * @param aHScrollBar  the horizontal scroll bar, or {@code null}
     * @param aVScrollBar  the vertical scroll bar, or {@code null}
     */
    public JtvScroller(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar) {
        super(bounds);
        drawLock = 0;
        drawFlag = false;
        hScrollBar = aHScrollBar;
        vScrollBar = aVScrollBar;
        options |= ofSelectable;
        eventMask |= evBroadcast;
    }

    /**
     * Clears the scroll bar references and calls the inherited
     * {@link JtvView#shutDown()}.
     */
    @Override
    public void shutDown() {
        hScrollBar = null;
        vScrollBar = null;
        super.shutDown();
    }

    /**
     * Updates the bounding rectangle and refreshes the scroll bar ranges
     * via {@link #setLimit(int, int)} without triggering an immediate redraw
     * (uses the draw lock). Clears {@link #drawFlag} and redraws afterwards.
     *
     * @param bounds the new bounding rectangle
     */
    @Override
    public void changeBounds(JtvRect bounds) {
        setBounds(bounds);
        drawLock++;
        setLimit(limit.getX(), limit.getY());
        drawLock--;
        drawFlag = false;
        drawView();
    }

    /**
     * Triggers a deferred redraw if the draw lock has reached zero and a
     * redraw was requested ({@link #drawFlag} is {@code true}).
     */
    public void checkDraw() {
        if (drawLock == 0 && drawFlag) {
            drawFlag = false;
            drawView();
        }
    }

    /**
     * Returns the default scroller colour palette {@link #cpScroller}.
     *
     * @return the scroller's colour palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpScroller;
    }

    /**
     * Handles {@code cmScrollBarChanged} broadcasts from the associated
     * scroll bars by calling {@link #scrollDraw()} to update {@link #delta}
     * and redraw the view.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evBroadcast
        		&& event.getMessage().getCommand() == cmScrollBarChanged
        		&& (event.getMessage().getInfoPtr() == hScrollBar || event.getMessage().getInfoPtr() == vScrollBar)) {
            scrollDraw();
        }
    }

    /**
     * Reads the current values from both scroll bars, updates {@link #delta},
     * and redraws the view. If the draw lock is held, sets {@link #drawFlag}
     * to request a deferred redraw. Also adjusts the cursor position to
     * track the scroll offset change.
     */
    public void scrollDraw() {
        JtvPoint d = new JtvPoint(
            (hScrollBar != null) ? hScrollBar.getValue() : 0,
            (vScrollBar != null) ? vScrollBar.getValue() : 0
        );

        if (d.getX() != delta.getX() || d.getY() != delta.getY()) {
            setCursor(cursor.getX() + delta.getX() - d.getX(), cursor.getY() + delta.getY() - d.getY());
            delta = d;
            if (drawLock != 0) {
                drawFlag = true;
            }
            else {
                drawView();
            }
        }
    }

    /**
     * Sets both scroll bars to the given {@code (x, y)} position.
     * Uses the draw lock to defer the resulting redraws until both values
     * are set, then calls {@link #checkDraw()}.
     *
     * @param x the desired horizontal scroll position
     * @param y the desired vertical scroll position
     */
    public void scrollTo(int x, int y) {
        drawLock++;
        if (hScrollBar != null) {
        	hScrollBar.setValue(x);
        }
        if (vScrollBar != null) {
        	vScrollBar.setValue(y);
        }
        drawLock--;
        checkDraw();
    }

    /**
     * Sets the virtual content size and adjusts the scroll bar ranges
     * to {@code [0, limit - viewSize]}, using the current view size as
     * the page step and {@code 1} is kept as the arrow step.
     * Uses the draw lock to defer redraws until both bars are updated.
     *
     * @param x the total virtual width in columns
     * @param y the total virtual height in rows
     */
    public void setLimit(int x, int y) {
        limit = new JtvPoint(x, y);
        drawLock++;
        if (hScrollBar != null) {
            hScrollBar.setParams(hScrollBar.getValue(), 0, x - size.getX(), size.getX() - 1, hScrollBar.getArStep());
        }
        if (vScrollBar != null) {
            vScrollBar.setParams(vScrollBar.getValue(), 0, y - size.getY(), size.getY() - 1, vScrollBar.getArStep());
        }
        drawLock--;
        checkDraw();
    }

    /**
     * Shows or hides {@code sBar} based on whether this scroller is both
     * active and selected. Called from {@link #setState(int, boolean)} when
     * either flag changes.
     *
     * @param sBar the scroll bar to show or hide; {@code null} is ignored
     */
    public void showSBar(JtvScrollBar sBar) {
        if (sBar != null) {
            if (hasState(sfActive | sfSelected)) {
                sBar.show();
            }
            else {
                sBar.hide();
            }
        }
    }

    /**
     * Propagates state changes and updates scroll bar visibility when
     * {@code sfActive} or {@code sfSelected} change.
     *
     * @param aState the state bits to modify
     * @param enable {@code true} to set; {@code false} to clear
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);
        if ((aState & (sfActive | sfSelected)) != 0) {
            showSBar(hScrollBar);
            showSBar(vScrollBar);
        }
    }
}
