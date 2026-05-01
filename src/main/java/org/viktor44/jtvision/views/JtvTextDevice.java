/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.views;

import org.viktor44.jtvision.core.JtvRect;

/**
 * Abstract base class for text-output views that extend {@link JtvScroller}.
 * <p>
 * JtvTextDevice provides a minimal streaming-text interface on top of a
 * scrollable viewport. Concrete subclasses implement {@link #doSputn(String)}
 * to store and display the incoming text; JtvTextDevice supplies the higher-level
 * {@link #write(String)} entry point and the {@link #overflow(int)} overflow
 * callback.
 * <p>
 * The primary concrete implementation is {@link JtvTerminal}, which maintains
 * a line-oriented ring buffer and redraws itself as text arrives.
 */
public abstract class JtvTextDevice extends JtvScroller {

    /**
     * Constructs a text device with the given bounds and optional scroll bars.
     * Delegates directly to {@link JtvScroller#TScroller(JtvRect, JtvScrollBar, JtvScrollBar)}.
     *
     * @param bounds        the bounding rectangle
     * @param aHScrollBar   the horizontal scroll bar, or {@code null}
     * @param aVScrollBar   the vertical scroll bar, or {@code null}
     */
    public JtvTextDevice(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar) {
        super(bounds, aHScrollBar, aVScrollBar);
    }

    /**
     * Writes the string {@code s} to the underlying text store and updates
     * the display. This is the core output primitive that subclasses must
     * implement.
     *
     * @param s the string to output
     * @return the number of characters actually written
     */
    public abstract int doSputn(String s);

    /**
     * Overflow callback invoked when the output buffer is full. If {@code c}
     * is non-negative it is converted to a character and written via
     * {@link #doSputn(String)}.
     *
     * @param c the character code to emit, or a negative value to skip output
     * @return {@code 1} always
     */
    public int overflow(int c) {
        if (c >= 0) {
            doSputn(String.valueOf((char) c));
        }
        return 1;
    }

    /**
     * Writes the string {@code s} via {@link #doSputn(String)}.
     * Returns {@code 0} immediately for {@code null} or empty input.
     *
     * @param s the string to write
     * @return the number of characters written, or {@code 0} if {@code s}
     *         is {@code null} or empty
     */
    public int write(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        return doSputn(s);
    }
}
