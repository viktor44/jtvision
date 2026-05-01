package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmRecordHistory;
import static org.viktor44.jtvision.core.CommandCodes.cmReleasedFocus;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.ViewFlags.ofPostProcess;
import static org.viktor44.jtvision.core.ViewFlags.sfFocused;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvGroup;
import org.viktor44.jtvision.views.JtvView;

/**
 * A small drop-down history picker linked to an {@link JtvInputLine}.
 * <p>
 * THistory implements a generic pick-list mechanism for recalling previous entries
 * made in an associated {@link JtvInputLine}. It appears as a small button (a
 * down-arrow icon) placed immediately to the right of the input line.
 * <p>
 * When the user clicks the history view or presses Alt+Down, a
 * {@link JtvHistoryWindow} pops up displaying the stored entries for this history
 * ID. If the user selects an entry, it is copied back into the linked input line.
 * <p>
 * Entries are automatically recorded when the input line loses focus
 * ({@code cmReleasedFocus}) or when a {@code cmRecordHistory} broadcast is
 * received (typically sent by {@link JtvButton#press()}).
 * <p>
 * All history data is stored in a static per-ID map so it persists for the
 * lifetime of the application. Up to 20 entries are retained per ID;
 * the oldest entry is dropped when the list is full.
 * 
 * <h3>Colour palette</h3>
 * 
 * The two-entry palette {@link #cpHistory} maps to:
 * <ol>
 *   <li>Icon normal colour.</li>
 *   <li>Icon shortcut / highlight colour.</li>
 * </ol>
 */
public class JtvHistory extends JtvView {

    /** The input line whose text is recorded and restored by this history view. */
    private JtvInputLine link;

    /**
     * The identifier that groups this history view's entries together.
     * Multiple input lines may share the same ID to share a common history.
     */
    private int historyId;

    /** Application-wide storage: maps history ID to its ordered list of entries. */
    private static final Map<Integer, List<String>> histories = new HashMap<>();

    /**
     * Two-entry colour palette:
     * <ol>
     *   <li>{@code 0x16} — icon normal colour.</li>
     *   <li>{@code 0x17} — icon shortcut / highlight colour.</li>
     * </ol>
     */
    private static final JtvPalette cpHistory = new JtvPalette(new int[] {0x16, 0x17});

    /** Icon string rendered by this view (right-half block, down-arrow, left-half block). */
    private static final String historyIcon = "▐~↓~▌";

    /**
     * Creates a history view with the given bounds, linked input line, and history ID.
     *
     * @param bounds     the bounding rectangle (typically a 3×1 cell)
     * @param aLink      the input line whose entries this view manages
     * @param aHistoryId the ID used to look up and store history entries
     */
    public JtvHistory(JtvRect bounds, JtvInputLine aLink, int aHistoryId) {
        super(bounds);
        link = aLink;
        historyId = aHistoryId;
        options |= ofPostProcess;
        eventMask |= evBroadcast;
    }

    /**
     * Draws the history drop-down arrow icon.
     */
    @Override
    public void draw() {
        JtvDrawBuffer b = new JtvDrawBuffer();
        b.moveChar(0, ' ', getColor(1), size.getX());
        b.moveCStr(0, historyIcon, getColor(2), getColor(1));
        writeLine(0, 0, size.getX(), 1, b);
    }

    /**
     * Returns the default color palette {@code cpHistory}.
     *
     * @return the history view's color palette
     */
    @Override
    public JtvPalette getPalette() {
        return cpHistory;
    }

    /**
     * Handles mouse, keyboard, and broadcast events.
     * <ul>
     *   <li>Mouse click — opens the history picker window.</li>
     *   <li>Alt+Down — opens the history picker window.</li>
     *   <li>{@code cmRecordHistory} broadcast — records the linked input line's current text.</li>
     *   <li>{@code cmReleasedFocus} for the linked view — records the current text.</li>
     * </ul>
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evMouseDown) {
            showHistory();
            clearEvent(event);
        } else if (event.getWhat() == evKeyDown) {
            if (event.getKeyDown().getKeyCode() == JtvKey.kbDown &&
                (event.getKeyDown().getModifiers() & JtvKey.kbAltMask) != 0) {
                showHistory();
                clearEvent(event);
            }
        } else if (event.getWhat() == evBroadcast) {
            if (event.getMessage().getCommand() == cmRecordHistory) {
                recordHistory(historyId, link.getData());
            } else if (event.getMessage().getCommand() == cmReleasedFocus && event.getMessage().getInfoPtr() == link) {
                recordHistory(historyId, link.getData());
            }
        }
    }

    /**
     * Displays the {@link JtvHistoryWindow} for the current history ID.
     * If the user selects an entry, it is written back to the linked input line.
     */
    private void showHistory() {
        if (link == null || owner == null) {
            return;
        }
        List<String> hist = getHistory(historyId);
        if (hist == null || hist.isEmpty()) {
            return;
        }
        if (!link.focus()) {
            return;
        }

        recordHistory(historyId, link.getData());

        JtvRect r = link.getBounds();
        r.setA(new JtvPoint(r.getA().getX() - 1, r.getA().getY() - 1));
        r.setB(new JtvPoint(r.getB().getX() + 1, r.getB().getY() + 7));
        JtvRect ownerExtent = owner.getExtent();
        r.intersect(ownerExtent);
        r.setB(new JtvPoint(r.getB().getX(), r.getB().getY() - 1));

        JtvHistoryWindow historyWindow = initHistoryWindow(r);
        if (historyWindow == null) {
            return;
        }

        if (owner instanceof JtvGroup) {
            int c = ((JtvGroup) owner).execView(historyWindow);
            if (c == cmOK) {
                String item = historyWindow.getSelection();
                if (item != null) {
                    link.setData(item);
                    link.selectAll(true);
                    link.drawView();
                }
            }
            ((JtvGroup) owner).destroy(historyWindow);
        } else {
            String item = hist.get(hist.size() - 1);
            if (item != null) {
                link.setData(item);
                link.selectAll(true);
                link.drawView();
            }
        }
    }

    /**
     * Creates and returns a {@link JtvHistoryWindow} for the given bounds.
     * The window's help context is set from the linked input line.
     *
     * @param bounds the bounding rectangle for the popup window
     * @return a new {@link JtvHistoryWindow}, or {@code null} if construction fails
     */
    public JtvHistoryWindow initHistoryWindow(JtvRect bounds) {
        JtvHistoryWindow w = new JtvHistoryWindow(bounds, historyId);
        if (link != null) {
            w.setHelpCtx(link.getHelpCtx());
        }
        return w;
    }

    // --- Static history management ---

    /**
     * Records the string {@code str} under the given history ID.
     * Duplicate entries are removed before adding the new entry at the end.
     * The list is capped at 20 entries; the oldest entry is removed when full.
     *
     * @param id  the history list identifier
     * @param str the string to record; empty or {@code null} strings are ignored
     */
    public static void recordHistory(int id, String str) {
        if (str == null || str.isEmpty()) return;
        List<String> hist = histories.computeIfAbsent(id, k -> new ArrayList<>());
        hist.remove(str); // Remove duplicate if present
        hist.add(str);
        if (hist.size() > 20) // Limit history size
            hist.remove(0);
    }

    /**
     * Returns the list of history entries for the given ID, or {@code null} if none exist.
     *
     * @param id the history list identifier
     * @return the list of recorded strings, or {@code null}
     */
    public static List<String> getHistory(int id) {
        return histories.get(id);
    }

    /**
     * Removes all history entries for the given ID.
     *
     * @param id the history list identifier to clear
     */
    public static void clearHistory(int id) {
        histories.remove(id);
    }
}
