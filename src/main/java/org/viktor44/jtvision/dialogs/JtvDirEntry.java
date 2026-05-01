package org.viktor44.jtvision.dialogs;

/**
 * A data object representing a single entry in the directory tree list.
 * <p>
 * TDirEntry pairs a formatted display string (with indentation and prefix
 * characters that visualise the tree structure) with the absolute path of the
 * directory it represents. Instances are created internally by
 * {@link JtvDirListBox} and passed as event payloads in {@code cmChangeDir}
 * command events.
 */
public class JtvDirEntry {

    /** The formatted text shown in the directory list (includes tree-drawing prefix characters). */
    private final String displayText;

    /** The absolute path of the directory this entry represents. */
    private final String directory;

    /**
     * Creates a directory entry with the given display text and path.
     *
     * @param txt the formatted display string (tree prefix + directory name)
     * @param dir the absolute directory path
     */
    public JtvDirEntry(String txt, String dir) {
        displayText = txt;
        directory = dir;
    }

    /**
     * Returns the absolute path of the directory this entry represents.
     *
     * @return the absolute directory path
     */
    public String dir() {
        return directory;
    }

    /**
     * Returns the formatted display text for this entry as shown in the list.
     *
     * @return the display string including tree-drawing prefix characters
     */
    public String text() {
        return displayText;
    }
}
