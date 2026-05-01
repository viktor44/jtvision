/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

import lombok.Getter;

/**
 * An immutable screen-position object encapsulating integer (X, Y) coordinates.
 *
 * <p>The coordinate origin {@code (0, 0)} is the top-left corner of the screen.
 * {@code X} increases horizontally to the right; {@code Y} increases vertically downwards.
 * All JT Vision views are positioned and sized using {@code TPoint} and {@link JtvRect}.
 */
public class JtvPoint {

    /** Horizontal coordinate; 0 is the leftmost column. */
	@Getter
    private final int x;

	/** Vertical coordinate; 0 is the topmost row. */
	@Getter
    private final int y;

    /** Constructs the origin point {@code (0, 0)}. */
    public JtvPoint() {
        this(0, 0);
    }

    /**
     * Constructs a point at the given coordinates.
     *
     * @param x horizontal position
     * @param y vertical position
     */
    public JtvPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor.
     *
     * @param other point to copy
     */
    public JtvPoint(JtvPoint other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Returns a new point that is the component-wise sum of this point and {@code other}.
     *
     * @param other the point to add
     * @return new point {@code (x + other.x, y + other.y)}
     */
    public JtvPoint add(JtvPoint other) {
        return new JtvPoint(x + other.x, y + other.y);
    }

    /**
     * Returns a new point that is the component-wise difference of this point and {@code other}.
     *
     * @param other the point to subtract
     * @return new point {@code (x - other.x, y - other.y)}
     */
    public JtvPoint subtract(JtvPoint other) {
        return new JtvPoint(x - other.x, y - other.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof JtvPoint)) return false;
        JtvPoint other = (JtvPoint) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
