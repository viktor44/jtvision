package org.viktor44.jtvision.views;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvScreenCell;

/**
 * A two-dimensional off-screen cell buffer used as a drawing surface.
 * <p>
 * TDrawSurface stores a rectangular grid of {@link JtvScreenCell} objects
 * laid out in row-major order. It is not a view itself — it has no parent,
 * no event handling, and no palette. Its primary consumer is
 * {@link JtvSurfaceView}, which maps a scrollable viewport onto a surface
 * and copies cells to the screen during {@code draw()}.
 * 
 * <h3>Layout</h3>
 * 
 * Cells are stored as a flat array of size {@code width × height}. The
 * index of cell {@code (row, col)} is {@code row * width + col}. All
 * coordinates use the convention {@code (y, x)} to match the natural
 * row-then-column traversal order.
 * 
 * <h3>Lifecycle</h3>
 * 
 * After construction the surface may be resized with {@link #resize(JtvPoint)}
 * or grown incrementally with {@link #grow(JtvPoint)}. Call
 * {@link #clear()} to fill the buffer with space characters and the default
 * grey-on-black attribute ({@code 0x07}) before drawing.
 */
public class JtvDrawSurface {

    /**
     * The current width and height of the surface in cells.
     * Updated by {@link #resize(JtvPoint)} and {@link #grow(JtvPoint)}.
     */
    private JtvPoint size = new JtvPoint();

    /**
     * The flat cell array, stored in row-major order. Length is always
     * {@code size.x × size.y}. An empty array when the surface has zero
     * dimensions.
     */
    private JtvScreenCell[] data = new JtvScreenCell[0];

    /**
     * Constructs an empty surface with zero dimensions.
     * Call {@link #resize(JtvPoint)} before accessing cells.
     */
    public JtvDrawSurface() {
    }

    /**
     * Constructs a surface with the given dimensions and initialises all
     * cells to default values.
     *
     * @param aSize the desired width ({@code x}) and height ({@code y}),
     *              or {@code null} for an empty surface
     */
    public JtvDrawSurface(JtvPoint aSize) {
        resize(aSize);
    }

    /**
     * Resizes the surface to {@code aSize}, replacing the cell array with a
     * freshly-allocated one. If {@code aSize} is {@code null} or has a
     * non-positive dimension the surface is reset to zero size with an empty
     * array.
     *
     * @param aSize the new dimensions; must have positive {@code x} and
     *              {@code y} for a non-empty surface
     */
    public void resize(JtvPoint aSize) {
        if (aSize == null || aSize.getX() <= 0 || aSize.getY() <= 0) {
            size = new JtvPoint();
            data = new JtvScreenCell[0];
            return;
        }
        size = new JtvPoint(aSize);
        data = new JtvScreenCell[size.getX() * size.getY()];
        for (int i = 0; i < data.length; i++) {
            data[i] = new JtvScreenCell();
        }
    }

    /**
     * Grows (or shrinks) the surface by adding {@code aDelta} to the current
     * dimensions and resizing. Positive delta values expand; negative values
     * contract. If {@code aDelta} is {@code null}, does nothing.
     *
     * @param aDelta the amount to add to each dimension
     */
    public void grow(JtvPoint aDelta) {
        if (aDelta == null) {
            return;
        }
        resize(new JtvPoint(size.getX() + aDelta.getX(), size.getY() + aDelta.getY()));
    }

    /**
     * Fills every cell in the buffer with a space character ({@code ' '})
     * and the default attribute {@code 0x07} (light grey on black).
     */
    public void clear() {
        for (int i = 0; i < data.length; i++) {
            data[i] = new JtvScreenCell();
        }
    }

    /**
     * Returns the cell at position {@code (y, x)} without bounds checking.
     * The caller is responsible for verifying the coordinates with
     * {@link #inBounds(int, int)} before calling this method.
     *
     * @param y the row index (0-based)
     * @param x the column index (0-based)
     * @return the cell at the given position
     */
    public JtvScreenCell at(int y, int x) {
        return data[y * size.getX() + x];
    }

    /**
     * Returns {@code true} if {@code (y, x)} is a valid cell address within
     * the current surface dimensions.
     *
     * @param y the row index to test
     * @param x the column index to test
     * @return {@code true} if the coordinates are within bounds
     */
    public boolean inBounds(int y, int x) {
        return y >= 0 && x >= 0 && y < size.getY() && x < size.getX();
    }
}
