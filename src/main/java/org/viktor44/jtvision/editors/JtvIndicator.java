package org.viktor44.jtvision.editors;

import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowLoY;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;
import static org.viktor44.jtvision.core.ViewFlags.sfDragging;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvView;

/**
 * A line/column indicator displayed in the lower-left corner of an editor window.
 * <p>
 * The indicator shows the current cursor position in {@code line:column} format.
 * When the associated editor's text has been modified, an asterisk ({@code *}) prefix
 * is prepended to signal unsaved changes.
 * <p>
 * Editor window objects create and associate an indicator automatically.
 * The editor calls {@link #setValue} to keep the display current whenever the
 * cursor moves or the modification flag changes.
 * 
 * <h3>Colour palette</h3>
 * 
 * The two-entry palette {@link #cpIndicator} maps to:
 * <ol>
 *   <li>Normal (non-dragging) indicator colour.</li>
 *   <li>Indicator colour while dragging.</li>
 * </ol>
 */
public class JtvIndicator extends JtvView {

    /**
     * The current (column, line) position to display.
     * Updated by the associated {@link JtvEditor} via {@link #setValue}.
     */
    private JtvPoint location = new JtvPoint();

    /**
     * {@code true} if the associated editor's buffer has been modified.
     * When {@code true}, {@link #draw()} prepends an asterisk to the indicator.
     */
    private boolean modified;

    /**
     * Two-entry colour palette:
     * <ol>
     *   <li>{@code 2} — normal (non-dragging) indicator colour.</li>
     *   <li>{@code 3} — indicator colour while dragging.</li>
     * </ol>
     */
    private static final JtvPalette cpIndicator = new JtvPalette(new int[]{2, 3});

    private static final char dragFrame = '\u2550';
    private static final char normalFrame = '\u2500';

    /**
     * Constructs an indicator anchored to the bottom of its owner.
     * Sets {@code growMode} to {@code gfGrowLoY | gfGrowHiY} so the indicator
     * stays in the status row as the window is resized.
     *
     * @param bounds the bounding rectangle for this indicator
     */
    public JtvIndicator(JtvRect bounds) {
        super(bounds);
        growMode = gfGrowLoY | gfGrowHiY;
    }

    /**
     * Draws the indicator as a horizontal line filled with the frame character, with the
     * cursor position overlaid in {@code line:column} format.
     * An asterisk is shown at the leftmost position when {@link #modified} is {@code true}.
     * The color and frame character differ when the window is being dragged.
     */
    @Override
    public void draw() {
        boolean dragging = (state & sfDragging) != 0;
        JtvColorAttr color = dragging ? getColor(2) : getColor(1);
        char frame = dragging ? normalFrame : dragFrame;
        JtvDrawBuffer b = new JtvDrawBuffer();
        b.moveChar(0, frame, color, size.getX());
        if (modified) {
            b.putChar(0, '*');
        }
        String s = " " + (location.getY() + 1) + ":" + (location.getX() + 1) + " ";
        int colon = s.indexOf(':');
        int start = Math.max(0, 8 - Math.max(0, colon));
        b.moveStr(start, s, color, Math.max(0, size.getX() - start), 0);
        writeLine(0, 0, size.getX(), size.getY(), b);
    }

    /**
     * Returns the indicator palette ({@code CIndicator}), mapping color indices 1 and 2
     * onto the second and third entries in the standard application palette
     * (the same colors used by window frames).
     *
     * @return the indicator palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpIndicator;
    }

    /**
     * Updates the displayed cursor position and modification flag, then redraws the view
     * if either value changed.  Called by the associated editor after each cursor movement
     * or text modification.
     *
     * @param p         new (column, line) position, or {@code null} to leave unchanged
     * @param aModified {@code true} if the editor buffer has been modified
     */
    public void setValue(JtvPoint p, boolean aModified) {
        boolean changed = false;
        if (p != null && !location.equals(p)) {
            location = new JtvPoint(p);
            changed = true;
        }
        if (modified != aModified) {
            modified = aModified;
            changed = true;
        }
        if (changed) {
            drawView();
        }
    }

    /**
     * Calls the inherited {@code setState}, then redraws the indicator when the
     * {@code sfDragging} or {@code sfActive} flags change so the frame character and
     * color are updated immediately.
     *
     * @param aState state flag(s) being changed
     * @param enable {@code true} to set the flag, {@code false} to clear it
     */
    @Override
    public void setState(int aState, boolean enable) {
        super.setState(aState, enable);
        if ((aState & sfDragging) != 0 || (aState & sfActive) != 0) {
            drawView();
        }
    }
}
