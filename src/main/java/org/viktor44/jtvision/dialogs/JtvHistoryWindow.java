package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.ViewFlags.sbHandleKeyboard;
import static org.viktor44.jtvision.core.ViewFlags.sbHorizontal;
import static org.viktor44.jtvision.core.ViewFlags.sbVertical;
import static org.viktor44.jtvision.core.ViewFlags.wfClose;
import static org.viktor44.jtvision.core.ViewFlags.wnNoNumber;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvWindow;

/**
 * The popup window that hosts a {@link JtvHistoryViewer}.
 * <p>
 * THistoryWindow is a borderless, number-less window created automatically by
 * {@link JtvHistory#initHistoryWindow(JtvRect)} when the user requests the history
 * pick-list. It contains a {@link JtvHistoryViewer} with horizontal and vertical
 * scroll bars.
 * <p>
 * Clicking outside the window's bounds dismisses it with {@code cmCancel}.
 * This class is managed automatically by {@link JtvHistory} and does not normally
 * need to be used directly.
 * 
 * <h3>Colour palette</h3>
 * 
 * The seven-entry palette {@link #cpHistoryWindow} maps to:
 * <ol>
 *   <li>Passive frame colour.</li>
 *   <li>Active frame / title colour.</li>
 *   <li>Frame icon colour.</li>
 *   <li>Scroll bar page area colour.</li>
 *   <li>Scroll bar arrow colour.</li>
 *   <li>History list normal item colour.</li>
 *   <li>History list focused item colour.</li>
 * </ol>
 */
public class JtvHistoryWindow extends JtvWindow {

    /** The list-viewer component that displays the history entries. */
    private JtvHistoryViewer viewer;

    /**
     * Seven-entry colour palette:
     * <ol>
     *   <li>{@code 0x13} — passive frame colour.</li>
     *   <li>{@code 0x13} — active frame / title colour.</li>
     *   <li>{@code 0x15} — frame icon colour.</li>
     *   <li>{@code 0x18} — scroll bar page area colour.</li>
     *   <li>{@code 0x17} — scroll bar arrow colour.</li>
     *   <li>{@code 0x13} — history list normal item colour.</li>
     *   <li>{@code 0x14} — history list focused item colour.</li>
     * </ol>
     */
    private static final JtvPalette cpHistoryWindow = new JtvPalette(
    		new int[] {0x13, 0x13, 0x15, 0x18, 0x17, 0x13, 0x14}
    );

    /**
     * Creates a history window with the given bounds and history ID.
     * Constructs and inserts a {@link JtvHistoryViewer} with keyboard-enabled scroll bars.
     *
     * @param bounds    the bounding rectangle for the popup window
     * @param historyId the ID of the history list to display
     */
    public JtvHistoryWindow(JtvRect bounds, int historyId) {
        super(bounds, null, wnNoNumber);
        flags = wfClose;
        viewer = initViewer(getExtent(), historyId);
        if (viewer != null) {
            insert(viewer);
        }
    }

    /**
     * Creates the {@link JtvHistoryViewer} inside this window, with standard
     * horizontal and vertical scroll bars. Override to customise the viewer.
     *
     * @param r         the window's client rectangle (will be shrunk by 1 on each side)
     * @param historyId the history ID to pass to the viewer
     * @return a new {@link JtvHistoryViewer}
     */
    protected JtvHistoryViewer initViewer(JtvRect r, int historyId) {
        r.grow(-1, -1);
        return new JtvHistoryViewer(
            r,
            standardScrollBar(sbHorizontal | sbHandleKeyboard),
            standardScrollBar(sbVertical | sbHandleKeyboard),
            historyId
        );
    }

    /**
     * Returns the default color palette {@code cpHistoryWindow}.
     *
     * @return the history window's color palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpHistoryWindow;
    }

    /**
     * Returns the text of the entry currently selected in the viewer.
     *
     * @return the selected history entry text, or an empty string if no viewer exists
     */
    public String getSelection() {
        if (viewer == null) {
            return "";
        }
        return viewer.selectedText();
    }

    /**
     * Extends the inherited event handler to dismiss the window ({@code cmCancel})
     * when the user clicks outside its bounds.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evMouseDown && !mouseInView(event.getMouse().getWhere())) {
            endModal(cmCancel);
            clearEvent(event);
        }
    }
}
