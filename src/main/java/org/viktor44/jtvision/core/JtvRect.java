package org.viktor44.jtvision.core;

/**
 * A rectangle defined by two {@link JtvPoint} corner coordinates.
 *
 * <p>Field {@link #a} holds the upper-left corner and {@link #b} holds the lower-right corner.
 * Every Turbo Vision view is rectangular: their constructors accept a {@code TRect} parameter
 * (conventionally named {@code Bounds}) that determines the region the view covers.
 * {@code TRect} objects are not views themselves and cannot draw.
 *
 * <p>The rectangle {@code [a=(2,2), b=(5,4)]} covers 3 columns × 2 rows and contains 6 cells.
 * A rectangle with {@code a == b} has no area ({@link #isEmpty()} returns {@code true}).
 */
public class JtvRect {

    /** Upper-left corner of the rectangle. */
    private JtvPoint a;

	/** Lower-right corner of the rectangle (exclusive boundary). */
    private JtvPoint b;

    /** Returns a deep copy of this rectangle. */
    public JtvRect getCopy() {
        return new JtvRect(this);
    }

    /** Constructs a zero-sized rectangle at the origin. */
    public JtvRect() {
        a = new JtvPoint();
        b = new JtvPoint();
    }

    /**
     * Constructs a rectangle from raw coordinates.
     *
     * @param ax upper-left X
     * @param ay upper-left Y
     * @param bx lower-right X
     * @param by lower-right Y
     */
    public JtvRect(int ax, int ay, int bx, int by) {
        a = new JtvPoint(ax, ay);
        b = new JtvPoint(bx, by);
    }

    /**
     * Constructs a rectangle from two points.
     *
     * @param p1 upper-left corner
     * @param p2 lower-right corner
     */
    public JtvRect(JtvPoint p1, JtvPoint p2) {
        a = new JtvPoint(p1);
        b = new JtvPoint(p2);
    }

    /**
     * Copy constructor.
     *
     * @param other rectangle to copy
     */
    public JtvRect(JtvRect other) {
        a = new JtvPoint(other.getA());
        b = new JtvPoint(other.getB());
    }

    /**
     * Translates this rectangle by the given delta, modifying it in place.
     *
     * @param dx horizontal offset
     * @param dy vertical offset
     * @return {@code this}, for chaining
     */
    public JtvRect move(int dx, int dy) {
        a = new JtvPoint(a.getX() + dx, a.getY() + dy);
        b = new JtvPoint(b.getX() + dx, b.getY() + dy);
        return this;
    }

    /**
     * Expands (or shrinks) this rectangle symmetrically by the given amounts.
     * Corner {@code a} is moved by {@code (-dx, -dy)} and {@code b} by {@code (+dx, +dy)}.
     *
     * @param dx horizontal expansion (negative to shrink)
     * @param dy vertical expansion (negative to shrink)
     * @return {@code this}, for chaining
     */
    public JtvRect grow(int dx, int dy) {
        a = new JtvPoint(a.getX() - dx, a.getY() - dy);
        b = new JtvPoint(b.getX() + dx, b.getY() + dy);
        return this;
    }

    /**
     * Clips this rectangle to the overlap with {@code r}, modifying it in place.
     * If the rectangles do not overlap the result will satisfy {@link #isEmpty()}.
     *
     * @param r the clipping rectangle
     * @return {@code this}, for chaining
     */
    public JtvRect intersect(JtvRect r) {
        a = new JtvPoint(Math.max(a.getX(), r.getA().getX()), Math.max(a.getY(), r.getA().getY()));
        b = new JtvPoint(Math.min(b.getX(), r.getB().getX()), Math.min(b.getY(), r.getB().getY()));
        return this;
    }

    /**
     * Expands this rectangle to the smallest bounding box that contains both this rectangle
     * and {@code r}, modifying it in place.
     *
     * @param r the rectangle to include
     * @return {@code this}, for chaining
     */
    public JtvRect union(JtvRect r) {
        a = new JtvPoint(Math.min(a.getX(), r.getA().getX()), Math.min(a.getY(), r.getA().getY()));
        b = new JtvPoint(Math.max(b.getX(), r.getB().getX()), Math.max(b.getY(), r.getB().getY()));
        return this;
    }

    /**
     * Returns {@code true} if point {@code p} lies strictly inside this rectangle
     * (i.e. {@code a.x <= p.x < b.x} and {@code a.y <= p.y < b.y}).
     *
     * @param p the point to test
     * @return {@code true} if the point is contained
     */
    public boolean contains(JtvPoint p) {
        return p.getX() >= a.getX() && p.getX() < b.getX() && p.getY() >= a.getY() && p.getY() < b.getY();
    }

    /**
     * Returns {@code true} if the rectangle has zero or negative area
     * ({@code a.x >= b.x} or {@code a.y >= b.y}).
     *
     * @return {@code true} if empty
     */
    public boolean isEmpty() {
        return a.getX() >= b.getX() || a.getY() >= b.getY();
    }

    /**
     * Copies the corners of {@code other} into this rectangle.
     *
     * @param other source rectangle
     */
    public void assign(JtvRect other) {
        a = new JtvPoint(other.getA());
        b = new JtvPoint(other.getB());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof JtvRect)) return false;
        JtvRect other = (JtvRect) obj;
        return a.equals(other.getA()) && b.equals(other.getB());
    }

    @Override
    public int hashCode() {
        return 31 * a.hashCode() + b.hashCode();
    }

    @Override
    public String toString() {
        return "[" + a + ", " + b + "]";
    }

    /** Upper-left corner of the rectangle. */
    public JtvPoint getA() {
		return a;
	}

    /** Upper-left corner of the rectangle. */
	public void setA(JtvPoint a) {
		this.a = a;
	}

	/** Lower-right corner of the rectangle (exclusive boundary). */
	public JtvPoint getB() {
		return b;
	}

	/** Lower-right corner of the rectangle (exclusive boundary). */
	public void setB(JtvPoint b) {
		this.b = b;
	}
}
