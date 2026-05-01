package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmFileDoubleClicked;
import static org.viktor44.jtvision.core.CommandCodes.cmFileFocused;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * A sorted list box that displays files and directories for a file dialog.
 * <p>
 * TFileList extends {@link JtvSortedListBox} to show the contents of a directory
 * filtered by a wildcard pattern. It displays:
 * <ul>
 *   <li>A {@code ".."} parent-directory entry (always first).</li>
 *   <li>Subdirectories (sorted alphabetically, shown with a trailing {@code /}).</li>
 *   <li>Files matching the wildcard (sorted alphabetically).</li>
 * </ul>
 * <p>
 * When an item receives focus, {@code cmFileFocused} is broadcast with the
 * corresponding {@link JtvFileRecord} so other views (e.g., {@link JtvFileInputLine}
 * and {@link JtvFileInfoPane}) can update. When an item is selected (double-click or
 * Enter), {@code cmFileDoubleClicked} is broadcast.
 * <p>
 * The wildcard pattern follows standard glob syntax ({@code *} and {@code ?}).
 */
public class JtvFileList extends JtvSortedListBox {

    /**
     * Creates a two-column file list box with the given bounds and scroll bar.
     *
     * @param bounds     the bounding rectangle
     * @param aScrollBar the vertical scroll bar
     */
    public JtvFileList(JtvRect bounds, JtvScrollBar aScrollBar) {
        super(bounds, 2, aScrollBar);
    }

    /**
     * Returns the display text for a list item. Converts a {@link JtvFileRecord}
     * to its file name, falling back to the superclass for other object types.
     *
     * @param item the item object
     * @return the item's file name
     */
    @Override
    protected String itemText(Object item) {
        if (item instanceof JtvFileRecord) {
            JtvFileRecord r = (JtvFileRecord) item;
            return r.getName();
        }
        return super.itemText(item);
    }

    /**
     * Compares two {@link JtvFileRecord} objects for sorted order:
     * <ol>
     *   <li>{@code ".."} always sorts first.</li>
     *   <li>Directories sort before files.</li>
     *   <li>Within each group, items are sorted case-insensitively by name.</li>
     * </ol>
     *
     * @param a the first item
     * @param b the second item
     * @return negative, zero, or positive according to sort order
     */
    @Override
    protected int compareItems(Object a, Object b) {
        if (!(a instanceof JtvFileRecord) || !(b instanceof JtvFileRecord)) {
            return super.compareItems(a, b);
        }
        JtvFileRecord r1 = (JtvFileRecord) a;
        JtvFileRecord r2 = (JtvFileRecord) b;

        if ("..".equals(r1.getName())) {
            return -1;
        }
        if ("..".equals(r2.getName())) {
            return 1;
        }

        if (r1.isDirectory() != r2.isDirectory()) {
            return r1.isDirectory() ? -1 : 1;
        }
        return r1.getName().compareToIgnoreCase(r2.getName());
    }

    /**
     * Returns the display text for the item at position {@code item}.
     * Directories (except {@code ".."}) are shown with a trailing {@code /}.
     *
     * @param item   the zero-based item index
     * @param maxLen the maximum number of characters to return
     * @return the display text, or an empty string if out of range
     */
    @Override
    public String getText(int item, int maxLen) {
        if (item < 0 || item >= items.size()) {
            return "";
        }
        Object obj = items.get(item);
        if (obj instanceof JtvFileRecord) {
            JtvFileRecord r = (JtvFileRecord) obj;
            String s = r.isDirectory() && !"..".equals(r.getName()) ? r.getName() + "/" : r.getName();
            if (s.length() > maxLen) {
                return s.substring(0, maxLen);
            }
            return s;
        }
        return super.getText(item, maxLen);
    }

    /**
     * Focuses the item at {@code item} and broadcasts {@code cmFileFocused} with
     * the corresponding {@link JtvFileRecord}.
     *
     * @param item the zero-based item index to focus
     */
    @Override
    public void focusItem(int item) {
        super.focusItem(item);
        if (owner != null && item >= 0 && item < items.size()) {
            message(owner, evBroadcast, cmFileFocused, items.get(item));
        }
    }

    /**
     * Broadcasts {@code cmFileDoubleClicked} with the selected {@link JtvFileRecord}
     * when the user double-clicks or presses Enter on an item.
     *
     * @param item the zero-based item index that was selected
     */
    @Override
    public void selectItem(int item) {
        if (owner != null && item >= 0 && item < items.size()) {
            message(owner, evBroadcast, cmFileDoubleClicked, items.get(item));
        }
    }

    /**
     * Returns 0 — the file list does not participate in data transfer.
     *
     * @return 0
     */
    @Override
    public int dataSize() {
        return 0;
    }

    /**
     * Does nothing — data transfer is not implemented for this control.
     *
     * @param rec unused
     */
    @Override
    public void getDataTo(Object rec) {
    }

    /**
     * Does nothing — data transfer is not implemented for this control.
     *
     * @param rec unused
     */
    @Override
    public void setDataFrom(Object rec) {
    }

    /**
     * Reloads the list for the given directory filtered by the wildcard mask.
     * Adds a parent ({@code ".."}) entry if a parent directory exists, then
     * adds all subdirectories and matching files.
     *
     * @param dir      the absolute directory path to list
     * @param wildCard the wildcard filter (e.g., {@code "*.java"}); {@code null}
     *                 or empty defaults to {@code "*"}
     */
    public void readDirectory(String dir, String wildCard) {
        File base = dir == null || dir.isEmpty()
            ? new File(System.getProperty("user.dir"))
            : new File(dir);
        if (!base.isAbsolute()) {
            base = base.getAbsoluteFile();
        }
        if (!base.exists() || !base.isDirectory()) {
            newList(Collections.emptyList());
            return;
        }

        Pattern mask = wildcardPattern(wildCard == null || wildCard.isEmpty() ? "*" : wildCard);
        List<JtvFileRecord> records = new ArrayList<JtvFileRecord>();

        File parent = base.getParentFile();
        if (parent != null) {
            records.add(JtvFileRecord.parent(parent.getAbsolutePath()));
        }

        File[] files = base.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    records.add(JtvFileRecord.fromFile(f));
                } else if (mask.matcher(f.getName()).matches()) {
                    records.add(JtvFileRecord.fromFile(f));
                }
            }
        }

        Collections.sort(records, new Comparator<JtvFileRecord>() {
            @Override
            public int compare(JtvFileRecord o1, JtvFileRecord o2) {
                return compareItems(o1, o2);
            }
        });

        newListObjects(records);
        if (fileList().size() > 0 && owner != null) {
            message(owner, evBroadcast, cmFileFocused, fileList().get(0));
        }
    }

    /**
     * Reloads the list from a path that may contain a wildcard.
     * Splits the path into a directory and wildcard, then delegates to
     * {@link #readDirectory(String, String)}.
     *
     * @param wildcardPath the path, optionally containing {@code *} or {@code ?}
     */
    public void readDirectory(String wildcardPath) {
        String path = wildcardPath;
        if (path == null || path.isEmpty()) {
            readDirectory(System.getProperty("user.dir"), "*");
            return;
        }
        File f = new File(path);
        File dir;
        String mask;
        if (path.contains("*") || path.contains("?")) {
            dir = f.getParentFile();
            if (dir == null) {
                dir = new File(System.getProperty("user.dir"));
            }
            mask = f.getName();
        } else if (f.isDirectory()) {
            dir = f;
            mask = "*";
        } else {
            dir = f.getParentFile();
            if (dir == null) {
                dir = new File(System.getProperty("user.dir"));
            }
            mask = f.getName().isEmpty() ? "*" : f.getName();
        }
        readDirectory(dir.getAbsolutePath(), mask);
    }

    /**
     * Returns the underlying list cast to {@code List<TFileRecord>}.
     *
     * @return the list of {@link JtvFileRecord} objects
     */
    @SuppressWarnings("unchecked")
    public List<JtvFileRecord> fileList() {
        return (List<JtvFileRecord>) (List<?>) items;
    }

    /**
     * Compiles a glob wildcard pattern into a case-insensitive {@link Pattern}.
     * {@code *} matches any sequence of characters; {@code ?} matches exactly one.
     *
     * @param wildcard the glob pattern
     * @return the compiled regex pattern
     */
    private static Pattern wildcardPattern(String wildcard) {
        StringBuilder sb = new StringBuilder();
        sb.append('^');
        for (int i = 0; i < wildcard.length(); i++) {
            char c = wildcard.charAt(i);
            switch (c) {
                case '*':
                    sb.append(".*");
                    break;
                case '?':
                    sb.append('.');
                    break;
                case '.':
                    sb.append("\\.");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if ("+()^$|{}[]".indexOf(c) >= 0) {
                        sb.append('\\');
                    }
                    sb.append(c);
            }
        }
        sb.append('$');
        return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
    }
}
