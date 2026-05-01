package org.viktor44.jtvision.editors;

import static org.viktor44.jtvision.core.CommandCodes.cmUpdateTitle;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.ofTileable;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvScrollBar;
import org.viktor44.jtvision.views.JtvWindow;

import lombok.Getter;

/**
 * A window containing a {@link JtvFileEditor} together with horizontal and vertical scroll bars
 * and a {@link JtvIndicator}.
 *
 * <p>{@code TEditWindow} constructs all required subviews in its constructor and associates
 * them with the editor.  The window title is derived from the editor's file name, or
 * {@code "Untitled"} when no file name has been set.
 *
 * <p>When the file name changes (because of a {@code cmSaveAs} operation) the editor
 * broadcasts {@code cmUpdateTitle}; this window's {@link #handleEvent} refreshes the
 * window frame title in response.
 */
public class JtvEditWindow extends JtvWindow {

    /**
     * The embedded file editor view.  All text editing is performed by this object.
     */
	@Getter
    private JtvFileEditor editor;

	/** Window title used when the editor is acting as the clipboard (not used by default). */
    private static final String clipboardTitle = "Clipboard";
    /** Window title shown when no file name has been assigned. */
    private static final String untitled = "Untitled";

    /**
     * Constructs an editor window, creating and inserting horizontal and vertical scroll bars,
     * a position indicator, and a {@link JtvFileEditor} that covers the interior of the window
     * frame.  If {@code fileName} is non-empty the editor immediately loads that file.
     *
     * @param bounds   the bounding rectangle for the window
     * @param fileName path of the file to edit, or empty/null for a new untitled file
     * @param number   window number (displayed in the title bar and used by Alt+{@code n} switching)
     */
    public JtvEditWindow(JtvRect bounds, String fileName, int number) {
        super(bounds, null, number);
        options |= ofTileable;

        JtvRect r = getExtent();
        r.grow(-1, -1);

        JtvScrollBar hScrollBar = new JtvScrollBar(new JtvRect(18, size.getY() - 1, size.getX() - 2, size.getY()));
        hScrollBar.hide();
        insert(hScrollBar);

        JtvScrollBar vScrollBar = new JtvScrollBar(new JtvRect(size.getX() - 1, 1, size.getX(), size.getY() - 1));
        vScrollBar.hide();
        insert(vScrollBar);

        JtvIndicator indicator = new JtvIndicator(new JtvRect(2, size.getY() - 1, 16, size.getY()));
        indicator.hide();
        insert(indicator);

        editor = new JtvFileEditor(
            new JtvRect(r),
            hScrollBar,
            vScrollBar,
            indicator,
            fileName
        );
        editor.setGrowMode(gfGrowHiX | gfGrowHiY);
        insert(editor);

        title = getTitle(80);
    }

    /**
     * Closes the window only if the editor allows it (i.e. {@link JtvFileEditor#valid(int)}
     * returns {@code true}, which may prompt the user to save unsaved changes).
     */
    @Override
    public void close() {
        if (editor == null || editor.valid(0)) {
            super.close();
        }
    }

    /**
     * Returns the window title derived from the editor's file name, or {@code "Untitled"}
     * if no file name has been set.  The returned string is truncated to {@code maxSize}
     * characters if necessary.
     *
     * @param maxSize maximum length of the returned title string
     * @return the window title
     */
    @Override
    public String getTitle(int maxSize) {
        if (editor == null) {
            return untitled;
        }
        if (editor.getFileName() == null || editor.getFileName().isEmpty()) {
            return untitled;
        }
        String s = editor.getFileName();
        if (s.length() > maxSize) {
            return s.substring(0, maxSize);
        }
        return s;
    }

    /**
     * Handles events for the editor window.  Delegates to the inherited window handler,
     * then responds to the {@code cmUpdateTitle} broadcast by refreshing the window title
     * (emitted by the editor when the file name changes after a Save As operation).
     *
     * @param event the event to handle
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evBroadcast && event.getMessage().getCommand() == cmUpdateTitle) {
            title = getTitle(80);
            if (frame != null) {
                frame.drawView();
            }
            clearEvent(event);
        }
    }

    /**
     * Returns the minimum and maximum size constraints for this editor window.
     * The minimum is 30 columns × 10 rows to ensure the editor and scroll bars remain usable.
     *
     * @param min receives the minimum allowable size
     * @param max receives the maximum allowable size
     */
    @Override
    public JtvPoint getMinimumSize() {
        return new JtvPoint(30, 10);
    }
}
