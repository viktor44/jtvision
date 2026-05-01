/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.views;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.core.JtvScreenCell;

/**
 * A view that renders a {@link JtvDrawSurface} with a scroll offset.
 * <p>
 * JtvSurfaceView maps a rectangular window onto a {@link JtvDrawSurface}
 * cell buffer. The {@link #delta} field defines the top-left corner of
 * the visible region within the surface. Cells that lie outside the
 * surface bounds are filled with the background colour from palette
 * entry 1.
 * 
 * <h3>Typical use</h3>
 * 
 * Create a {@link JtvDrawSurface} of the desired virtual size, draw content
 * into it directly, then attach it to a TSurfaceView. Adjust
 * {@link #delta} to pan the viewport over the surface.
 * 
 * <h3>Colour palette</h3>
 * 
 * The single-entry palette {@link #cpSurfaceView} maps to:
 * <ol>
 *   <li>{@code 1} — background/empty cell colour.</li>
 * </ol>
 */
public class JtvSurfaceView extends JtvView {

    /**
     * The {@link JtvDrawSurface} whose cells this view renders. May be
     * {@code null}, in which case the entire view is filled with the
     * background colour.
     */
    private JtvDrawSurface surface;

    /**
     * The scroll offset: the surface coordinates of the top-left cell
     * that maps to position {@code (0, 0)} in this view.
     */
    private JtvPoint delta = new JtvPoint();

    /**
     * Single-entry colour palette:
     * <ol>
     *   <li>{@code 1} — background/empty cell colour.</li>
     * </ol>
     */
    private static final JtvPalette cpSurfaceView = new JtvPalette(new int[] {1});

    /**
     * Constructs a surface view with the given bounds and no attached surface.
     * The view renders as a blank background until a surface is assigned.
     *
     * @param bounds the bounding rectangle
     */
    public JtvSurfaceView(JtvRect bounds) {
        this(bounds, null);
    }

    /**
     * Constructs a surface view with the given bounds and an attached surface.
     *
     * @param bounds   the bounding rectangle
     * @param aSurface the draw surface to render, or {@code null} for blank
     */
    public JtvSurfaceView(JtvRect bounds, JtvDrawSurface aSurface) {
        super(bounds);
        surface = aSurface;
    }

    /**
     * Renders the visible portion of the attached {@link JtvDrawSurface}.
     * <p>
     * For each screen row {@code y}, cells are copied from surface coordinates
     * {@code (y + delta.y, x + delta.x)} to the draw buffer. Cells outside
     * the surface bounds are filled with a space character using the
     * background colour from palette entry 1. Each row is written to the
     * screen via {@link JtvView#writeBuf}.
     * <p>
     * If the view has zero width or height, returns immediately without
     * drawing.
     */
    @Override
    public void draw() {
        if (size.getX() <= 0 || size.getY() <= 0) {
            return;
        }
        JtvColorAttr empty = getColor(1);
        JtvScreenCell[] line = new JtvScreenCell[size.getX()];
        for (int i = 0; i < size.getX(); i++) {
            line[i] = new JtvScreenCell(' ', empty);
        }

        for (int y = 0; y < size.getY(); y++) {
            for (int x = 0; x < size.getX(); x++) {
                line[x] = new JtvScreenCell(' ', empty);
            }
            if (surface != null) {
                int sy = y + delta.getY();
                for (int x = 0; x < size.getX(); x++) {
                    int sx = x + delta.getX();
                    if (surface.inBounds(sy, sx)) {
                        line[x] = surface.at(sy, sx);
                    }
                }
            }
            writeBuf(0, y, size.getX(), 1, line);
        }
    }

    /**
     * Returns the surface view's colour palette {@link #cpSurfaceView}.
     *
     * @return the single-entry palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpSurfaceView;
    }
}
