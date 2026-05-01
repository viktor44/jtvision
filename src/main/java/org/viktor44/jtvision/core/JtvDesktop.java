package org.viktor44.jtvision.core;

import static org.viktor44.jtvision.core.CommandCodes.cmNext;
import static org.viktor44.jtvision.core.CommandCodes.cmPrev;
import static org.viktor44.jtvision.core.CommandCodes.cmReleasedFocus;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.ofTileable;
import static org.viktor44.jtvision.core.ViewFlags.sfVisible;

import org.viktor44.jtvision.views.JtvGroup;
import org.viktor44.jtvision.views.JtvView;

import lombok.Getter;

/**
 * The desktop group that acts as the container for all application windows.
 *
 * <p>{@code TDesktop} sits between the menu bar and the status line, occupying the
 * middle portion of the {@link JtvProgram} screen.  Its back-most child is a
 * {@link JtvBackground} that provides the characteristic textured fill.  All other
 * children are user windows.
 *
 * <p>The desktop handles the {@code cmNext} and {@code cmPrev} commands to cycle
 * window focus, and provides {@link #tile} and {@link #cascade} operations that
 * arrange all {@link ViewFlags#ofTileable} windows within a given rectangle.
 */
public class JtvDesktop extends JtvGroup {

	public static final char defaultBkgrnd = '\u2591'; // LIGHT SHADE (CP437 0xB0)

    /** The background tile view inserted as the bottom-most child. */
	@Getter
    private JtvBackground background;

	/** When {@code true}, tile columns are arranged before rows; default is {@code false}. */
    private boolean tileColumnsFirst;

    /**
     * Constructs a desktop covering {@code bounds}
     *
     * @param bounds the desktop area (normally the screen minus menu and status line rows)
     */
    public JtvDesktop(JtvRect bounds) {
        super(bounds);
        growMode = gfGrowHiX | gfGrowHiY;
        tileColumnsFirst = false;
        background = initBackground(getExtent());
        insert(background);
    }

    /**
     * Handles {@code cmNext} (cycle to next window) and {@code cmPrev} (cycle to previous
     * window) commands, then delegates all other events to the inherited handler.
     *
     * @param event the event to process
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmNext:
                    if (valid(cmReleasedFocus)) {
                        selectNext(false);
                    }
                    break;
                case cmPrev:
                    if (valid(cmReleasedFocus) && current != null) {
                        current.putInFrontOf(background);
                    }
                    break;
                default:
                    return;
            }
            clearEvent(event);
        }
    }

    /**
     * Default background factory: creates a {@link JtvBackground} filled with
     * {@link #defaultBkgrnd} covering the full desktop rectangle.
     *
     * @param r the desktop extent
     * @return a new background view
     */
    protected JtvBackground initBackground(JtvRect r) {
        return new JtvBackground(r, defaultBkgrnd);
    }

    /**
     * Arranges all tileable visible windows in a cascaded (staircase) pattern within {@code r}.
     * Each window is offset one row and one column from the previous.
     * Calls {@link #tileError()} if the windows are too large to fit.
     *
     * @param r the rectangle within which to cascade windows
     */
    public void cascade(JtvRect r) {
        int count = 0;
        JtvView lastV = null;
        if (last != null) {
            JtvView p = last;
            do {
                if (tileable(p)) {
                    count++;
                    lastV = p;
                }
                p = p.prev();
            }
            while (p != last);
        }

        if (count > 0 && lastV != null) {
            JtvPoint min = lastV.getMinimumSize();
            if (min.getX() > r.getB().getX() - r.getA().getX() - count || min.getY() > r.getB().getY() - r.getA().getY() - count) {
                tileError();
            }
            else {
                final int[] idx = {count - 1};
                final JtvRect cr = r;
                lock();
                forEach(v -> {
                    if (tileable(v) && idx[0] >= 0) {
                        JtvRect nr = new JtvRect(cr);
                        nr.setA(new JtvPoint(nr.getA().getX() + idx[0], nr.getA().getY() + idx[0]));
                        v.locate(nr);
                        idx[0]--;
                    }
                });
                unlock();
            }
        }
    }

    /**
     * Arranges all tileable visible windows in a grid within {@code r}, dividing the area
     * as evenly as possible.  The number of columns and rows is derived from the window count.
     * Calls {@link #tileError()} if the area is too small.
     *
     * @param r the rectangle within which to tile windows
     */
    public void tile(JtvRect r) {
        int numTileable = 0;
        if (last != null) {
            JtvView p = last;
            do {
                if (tileable(p)) numTileable++;
                p = p.prev();
            }
            while (p != last);
        }

        if (numTileable > 0) {
            int numCols, numRows;
            int sq = (int) Math.sqrt(numTileable);
            if (sq * sq == numTileable) {
                numCols = sq;
                numRows = sq;
            }
            else {
                numCols = sq + 1;
                numRows = numTileable / numCols;
                if (numCols * numRows < numTileable) {
                    numRows++;
                }
            }
            if (!tileColumnsFirst) {
                int tmp = numCols;
                numCols = numRows;
                numRows = tmp;
            }

            int totalWidth = r.getB().getX() - r.getA().getX();
            int totalHeight = r.getB().getY() - r.getA().getY();
            if (totalWidth / numCols == 0 || totalHeight / numRows == 0) {
                tileError();
                return;
            }

            final int fc = numCols;
            final int fr = numRows;
            final int[] tileNum = {numTileable - 1};
            lock();
            forEach(v -> {
                if (tileable(v) && tileNum[0] >= 0) {
                    int col = tileNum[0] % fc;
                    int row = tileNum[0] / fc;
                    JtvRect nr = new JtvRect(
                        r.getA().getX() + totalWidth * col / fc,
                        r.getA().getY() + totalHeight * row / fr,
                        r.getA().getX() + totalWidth * (col + 1) / fc,
                        r.getA().getY() + totalHeight * (row + 1) / fr
                    );
                    v.locate(nr);
                    tileNum[0]--;
                }
            });
            unlock();
        }
    }

    /**
     * Called when {@link #tile} or {@link #cascade} cannot fit the windows in the available
     * space.  The default implementation is a no-op; override to show an error message.
     */
    public void tileError() {
    }

    /** Returns {@code true} if {@code v} is tileable and currently visible. */
    private static boolean tileable(JtvView v) {
        return (v.getOptions() & ofTileable) != 0 && (v.getState() & sfVisible) != 0;
    }
}
