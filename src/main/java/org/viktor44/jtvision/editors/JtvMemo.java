/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.editors;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * A multi-line text memo field for use inside JT Vision dialog boxes and forms.
 *
 * <p>{@code JtvMemo} extends {@link JtvEditor} with data-transfer support ({@link #getDataTo},
 * {@link #setDataFrom}, {@link #dataSize}) so that it can participate in the standard dialog
 * data-record mechanism.  It is typically embedded in a dialog box alongside other controls.
 *
 * <p>The memo field treats its data as a simple string.  {@link #setDataFrom} replaces the
 * entire editor buffer with the supplied text; {@link #getDataTo} copies the current text
 * back into the caller's {@link StringBuilder}.
 */
public class JtvMemo extends JtvEditor {

    /**
     * Constructs a memo field.
     *
     * @param bounds       the bounding rectangle for this view
     * @param aHScrollBar  optional horizontal scroll bar, or {@code null}
     * @param aVScrollBar  optional vertical scroll bar, or {@code null}
     * @param aIndicator   optional position indicator, or {@code null}
     * @param aBufSize     maximum buffer size in characters
     */
    public JtvMemo(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, JtvIndicator aIndicator, int aBufSize) {
        super(bounds, aHScrollBar, aVScrollBar, aIndicator, aBufSize);
    }

    /**
     * Copies the current editor content into {@code rec}.
     * If {@code rec} is a {@link StringBuilder}, it is cleared and filled with the buffer text.
     *
     * @param rec target data record; must be a {@code StringBuilder} to receive text
     */
    @Override
    public void getDataTo(Object rec) {
        if (rec instanceof StringBuilder) {
            StringBuilder sb = (StringBuilder) rec;
            sb.setLength(0);
            sb.append(buffer);
        }
    }

    /**
     * Loads the editor buffer from {@code rec}.
     * If {@code rec} is a {@link CharSequence}, its content replaces the current buffer,
     * the cursor is moved to the start, and the modification flag is cleared.
     *
     * @param rec source data record; must be a {@code CharSequence}
     */
    @Override
    public void setDataFrom(Object rec) {
        if (rec instanceof CharSequence) {
            buffer.setLength(0);
            buffer.append((CharSequence) rec);
            curPtr = 0;
            selStart = selEnd = 0;
            modified = false;
            updateMetrics();
            drawView();
        }
    }

    /**
     * Returns the number of bytes that {@link #getDataTo} / {@link #setDataFrom} will
     * transfer.  Equals the current buffer length plus one (for a null terminator).
     *
     * @return data transfer size in bytes
     */
    @Override
    public int dataSize() {
        return buffer.length() + 1;
    }

    /**
     * Handles events by delegating to the inherited {@link JtvEditor} handler.
     *
     * @param event the event to handle
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
    }
}
