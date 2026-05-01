/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmFileFocused;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvRect;

/**
 * An input line that automatically updates itself when the file list focus changes.
 * <p>
 * JtvFileInputLine extends {@link JtvInputLine} to handle {@code cmFileFocused}
 * broadcast events from a {@link JtvFileList}. When a file or directory is focused
 * in the list and this input line does not have the selection, the input line
 * text is updated to reflect the focused item.
 * <p>
 * For a focused directory, the text is set to
 * {@code "<dir_name>/<wildcard>"} using the wildcard from the owning
 * {@link JtvFileDialog}.
 * <p>
 * This class is used internally by {@link JtvFileDialog} and does not normally
 * need to be subclassed.
 */
public class JtvFileInputLine extends JtvInputLine {

    /**
     * Creates a file input line with the given bounds and maximum text length.
     * Adds {@code evBroadcast} to the event mask so it receives {@code cmFileFocused}
     * broadcasts.
     *
     * @param bounds   the bounding rectangle
     * @param aMaxLen  the maximum number of characters the field may hold
     */
    public JtvFileInputLine(JtvRect bounds, int aMaxLen) {
        super(bounds, aMaxLen);
        eventMask |= evBroadcast;
    }

    /**
     * Extends the inherited event handler to respond to {@code cmFileFocused}
     * broadcasts from {@link JtvFileList}.
     * <p>
     * When a {@link JtvFileRecord} is focused in the file list and this input line
     * is not currently selected, the field is updated to the focused item's name.
     * If the focused item is a directory, the wildcard from the owning
     * {@link JtvFileDialog} is appended.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evBroadcast && event.getMessage().getCommand() == cmFileFocused && (state & sfSelected) == 0) {
            if (event.getMessage().getInfoPtr() instanceof JtvFileRecord) {
                JtvFileRecord rec = (JtvFileRecord) event.getMessage().getInfoPtr();
                setData(rec.getName());
                if (rec.isDirectory()) {
                    setData(getData() + "/");
                    if (owner instanceof JtvFileDialog) {
                        setData(getData() + ((JtvFileDialog) owner).getWildCard());
                    }
                }
                selectAll(false);
                drawView();
            }
        }
    }
}
