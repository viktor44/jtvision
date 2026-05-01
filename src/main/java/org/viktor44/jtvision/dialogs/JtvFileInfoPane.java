/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmFileFocused;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvView;

/**
 * An information pane that shows details about the focused file.
 * <p>
 * TFileInfoPane is displayed at the bottom of a {@link JtvFileDialog}. It renders
 * two rows of text:
 * <ol>
 *   <li>The current directory path combined with the active wildcard filter.</li>
 *   <li>The focused file's name, size (or {@code <DIR>} for directories),
 *       and last-modified date/time.</li>
 * </ol>
 * <p>
 * The pane updates automatically when a {@code cmFileFocused} broadcast arrives
 * from the {@link JtvFileList}.
 * 
 * <h3>Colour palette</h3>
 * 
 * The single-entry palette {@link #cpInfoPane} maps to:
 * <ol>
 *   <li>Info pane text colour.</li>
 * </ol>
 */
public class JtvFileInfoPane extends JtvView {

    /**
     * Single-entry colour palette:
     * <ol>
     *   <li>{@code 0x1E} — info pane text colour.</li>
     * </ol>
     */
    private static final JtvPalette cpInfoPane = new JtvPalette(new int[] {0x1E});

    /** Date formatter used in row 2 for the last-modified timestamp. */
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("MMM dd, yyyy hh:mma", Locale.US);

    /** The most recently focused file record, used to populate row 2 of the pane. */
    private JtvFileRecord fileRecord = new JtvFileRecord();

    /**
     * Creates a file-info pane with the given bounds.
     * Adds {@code evBroadcast} to the event mask so it receives {@code cmFileFocused}
     * broadcasts.
     *
     * @param bounds the bounding rectangle
     */
    public JtvFileInfoPane(JtvRect bounds) {
        super(bounds);
        eventMask |= evBroadcast;
    }

    /**
     * Draws the two-row information display:
     * <ol>
     *   <li>Row 0 — the directory + wildcard path from the owning {@link JtvFileDialog}.</li>
     *   <li>Row 1 — the focused file's name, size, and date.</li>
     * </ol>
     */
    @Override
    public void draw() {
        JtvColorAttr color = getColor(1);
        JtvDrawBuffer b = new JtvDrawBuffer();

        String path = "";
        if (owner instanceof JtvFileDialog) {
            JtvFileDialog dlg = (JtvFileDialog) owner;
            path = dlg.getDirectory() + dlg.getWildCard();
        }
        b.moveChar(0, ' ', color, size.getX());
        b.moveStr(1, path, color, Math.max(0, size.getX() - 1), 0);
        writeLine(0, 0, size.getX(), 1, b);

        b.moveChar(0, ' ', color, size.getX());
        if (fileRecord != null && fileRecord.getName() != null) {
            b.moveStr(1, fileRecord.getName(), color, Math.max(0, size.getX() - 2), 0);
            String sz = fileRecord.isDirectory() ? "<DIR>" : Long.toString(fileRecord.getSize());
            String dt = fileRecord.getLastModified() > 0 ? DATE_FMT.format(new Date(fileRecord.getLastModified())).toLowerCase(Locale.US) : "";

            int szPos = Math.max(1, size.getX() - 38);
            int dtPos = Math.max(1, size.getX() - 22);
            b.moveStr(szPos, sz, color, Math.max(0, size.getX() - szPos), 0);
            b.moveStr(dtPos, dt, color, Math.max(0, size.getX() - dtPos), 0);
        }
        writeLine(0, 1, size.getX(), 1, b);

        b.moveChar(0, ' ', color, size.getX());
        if (size.getY() > 2) {
            writeLine(0, 2, size.getX(), size.getY() - 2, b);
        }
    }

    /**
     * Returns the default color palette {@code cpInfoPane}.
     *
     * @return the info pane's color palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpInfoPane;
    }

    /**
     * Updates the displayed file information when a {@code cmFileFocused} broadcast
     * carries a {@link JtvFileRecord}.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evBroadcast && event.getMessage().getCommand() == cmFileFocused) {
            if (event.getMessage().getInfoPtr() instanceof JtvFileRecord) {
                fileRecord = (JtvFileRecord) event.getMessage().getInfoPtr();
                drawView();
            }
        }
    }
}
