/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.help;

import static org.viktor44.jtvision.core.ViewFlags.ofCentered;
import static org.viktor44.jtvision.core.ViewFlags.sbHandleKeyboard;
import static org.viktor44.jtvision.core.ViewFlags.sbHorizontal;
import static org.viktor44.jtvision.core.ViewFlags.sbVertical;
import static org.viktor44.jtvision.core.ViewFlags.wnNoNumber;

import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvWindow;

/**
 * A centred, non-numbered help window that hosts a {@link JtvHelpViewer}.
 * <p>
 * JtvHelpWindow is a standard {@link JtvWindow} descendant that wraps a
 * {@link JtvHelpViewer} together with keyboard-driven horizontal and vertical
 * scroll bars. It is the top-level container shown when context-sensitive
 * help is requested by the application.
 * <p>
 * The window is:
 * <ul>
 *   <li>Titled <em>"Help"</em>.</li>
 *   <li>Initially sized 50×18 characters and centred on screen
 *       ({@code ofCentered}).</li>
 *   <li>Numbered with {@code wnNoNumber} so no window number appears in
 *       the title bar.</li>
 * </ul>
 * 
 * <h3>Colour palette</h3>
 * 
 * The seven-entry palette {@link #cpHelpWindow} overrides the default window
 * palette to use help-specific colours for the frame, title, drag bar,
 * close button, resize corner, scroll bars, and scroll arrows.
 *
 * @see JtvHelpViewer
 * @see JtvHelpFile
 */
public class JtvHelpWindow extends JtvWindow {

    /** The title bar text of the help window. */
    private static final String helpWinTitle = "Help";

    /**
     * Default colour palette for the help window frame and controls:
     * <ol>
     *   <li>{@code 0x13} — frame (passive).</li>
     *   <li>{@code 0x13} — title.</li>
     *   <li>{@code 0x15} — frame (active).</li>
     *   <li>{@code 0x18} — drag bar.</li>
     *   <li>{@code 0x17} — close button.</li>
     *   <li>{@code 0x13} — scroll bar page area.</li>
     *   <li>{@code 0x14} — scroll bar arrows.</li>
     * </ol>
     */
    private static final JtvPalette cpHelpWindow = new JtvPalette(
    		new int[] {0x13, 0x13, 0x15, 0x18, 0x17, 0x13, 0x14}
    );

    /**
     * Constructs the help window, creates keyboard-controlled scroll bars,
     * and inserts a {@link JtvHelpViewer} that immediately displays the topic
     * for {@code context}.
     * <p>
     * The viewer bounds are inset two columns and one row from the window
     * interior to leave room for the frame.
     *
     * @param hFile   the help file to load topics from; may be {@code null},
     *                in which case an empty topic is shown
     * @param context the initial help-context ID to display in the viewer
     */
    public JtvHelpWindow(JtvHelpFile hFile, int context) {
        super(new JtvRect(0, 0, 50, 18), helpWinTitle, wnNoNumber);
        options |= ofCentered;

        JtvRect r = new JtvRect(0, 0, 50, 18);
        r.grow(-2, -1);

        insert(new JtvHelpViewer(
            r,
            standardScrollBar(sbHorizontal | sbHandleKeyboard),
            standardScrollBar(sbVertical | sbHandleKeyboard),
            hFile,
            context
        ));
    }

    /**
     * Returns the help window's colour palette {@link #cpHelpWindow},
     * overriding the default window palette with help-specific colours.
     *
     * @return the help window's colour palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpHelpWindow;
    }
}
