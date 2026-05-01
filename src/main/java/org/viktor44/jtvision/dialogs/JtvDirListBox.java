/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmChangeDir;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.sfFocused;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * A list box that displays a hierarchical directory tree.
 * <p>
 * TDirListBox shows the directory tree of the current disk, with the
 * path from the root to the current directory highlighted. Subdirectories
 * of the current directory's parent are also shown, allowing the user to
 * navigate by selecting items.
 * <p>
 * When the user selects an entry, a {@code cmChangeDir} command event is
 * posted with the selected {@link JtvDirEntry} as the info pointer, allowing
 * the owner dialog (typically {@link JtvChangeDirDialog}) to change to that directory.
 * <p>
 * Passing the special string {@code "Drives"} to {@link #newDirectory(String)}
 * switches the list to show the available disk drives instead of a directory tree.
 */
public class JtvDirListBox extends JtvListBox {

    /** The {@link JtvDirEntry} objects currently displayed (parallel to the string list). */
    private final List<JtvDirEntry> entries = new ArrayList<JtvDirEntry>();

    /** The absolute path of the directory currently reflected in the list. */
    private String dir = "";

    /** The index within {@link #entries} of the currently selected (active) directory. */
    private int cur;

    /** Prefix string for items on the path from root to the current directory. */
    private static final String pathDir = "|--";
    /** Prefix for the first drive entry when showing the drives list. */
    private static final String firstDir = "\\|-";
    /** Prefix for middle entries in the drives list. */
    private static final String middleDir = " |-";
    /** Prefix for the last entry in the drives list. */
    private static final String lastDir = " \\--";
    /** Special directory name that causes {@link #newDirectory(String)} to list drives. */
    private static final String drives = "Drives";

    /**
     * Creates an empty directory list box with the given bounds and scroll bar.
     *
     * @param bounds      the bounding rectangle
     * @param aScrollBar  the vertical scroll bar
     */
    public JtvDirListBox(JtvRect bounds, JtvScrollBar aScrollBar) {
        super(bounds, 1, null, aScrollBar);
    }

    /**
     * Returns the display text for the entry at position {@code item},
     * truncated to {@code maxLen} characters.
     *
     * @param item   the zero-based item index
     * @param maxLen the maximum number of characters to return
     * @return the entry's display text
     */
    @Override
    public String getText(int item, int maxLen) {
        if (item < 0 || item >= entries.size()) {
            return "";
        }
        String s = entries.get(item).text();
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }

    /**
     * Returns {@code true} if item {@code item} is the currently active directory
     * (i.e., {@code item == cur}).
     *
     * @param item the zero-based item index
     * @return {@code true} if this is the selected (current) directory
     */
    @Override
    public boolean isSelected(int item) {
        return item == cur;
    }

    /**
     * Posts a {@code cmChangeDir} command event with the {@link JtvDirEntry} at
     * {@code item} as the payload, requesting that the owner changes to that directory.
     *
     * @param item the zero-based item index to navigate to
     */
    @Override
    public void selectItem(int item) {
        if (item >= 0 && item < entries.size()) {
            message(owner, evCommand, cmChangeDir, entries.get(item));
        }
    }

    /**
     * Returns the list of {@link JtvDirEntry} objects currently displayed.
     *
     * @return the entry list
     */
    public List<JtvDirEntry> list() {
        return entries;
    }

    /**
     * Populates the list with the directory tree for the given path, or with
     * drive letters if {@code str} equals {@code "Drives"}.
     * <p>
     * After loading, the list scrolls to the entry for the current directory.
     *
     * @param str the absolute path of the directory to display, or {@code null}
     *            to use the JVM's current working directory
     */
    public void newDirectory(String str) {
        if (str == null) {
            str = System.getProperty("user.dir");
        }
        dir = str;
        entries.clear();
        cur = 0;

        if (drives.equals(str)) {
            showDrives(entries);
        } else {
            showDirs(entries);
        }

        List<String> listText = new ArrayList<String>();
        for (JtvDirEntry e : entries) {
            listText.add(e.text());
        }
        super.newList(listText);
        focusItemNum(Math.min(cur, Math.max(0, entries.size() - 1)));
    }

    /**
     * Populates {@code dirs} with the available disk drive roots.
     * The first entry is a "Drives" header; subsequent entries are the root paths.
     */
    private void showDrives(List<JtvDirEntry> dirs) {
        dirs.add(new JtvDirEntry(drives, drives));
        File[] roots = File.listRoots();
        if (roots == null || roots.length == 0) {
            return;
        }
        for (int i = 0; i < roots.length; i++) {
            String p = roots[i].getAbsolutePath();
            String letter = p.length() >= 1 ? p.substring(0, 1).toUpperCase() : p;
            String prefix = (i == roots.length - 1) ? lastDir : (i == 0 ? firstDir : middleDir);
            dirs.add(new JtvDirEntry(prefix + letter, p));
        }
    }

    /**
     * Populates {@code dirs} with the directory tree from the filesystem root
     * down to the current directory, followed by the siblings of the current directory.
     * Sets {@link #cur} to the index of the current directory's entry.
     */
    private void showDirs(List<JtvDirEntry> dirs) {
        File curDir = new File(dir);
        if (!curDir.isAbsolute()) {
            curDir = curDir.getAbsoluteFile();
        }

        File root = curDir;
        while (root.getParentFile() != null) {
            root = root.getParentFile();
        }

        dirs.add(new JtvDirEntry(pathDir + root.getAbsolutePath(), root.getAbsolutePath()));

        List<File> chain = new ArrayList<File>();
        File walk = curDir;
        while (walk != null && !walk.equals(root)) {
            chain.add(walk);
            walk = walk.getParentFile();
        }
        Collections.reverse(chain);

        int indent = 2;
        for (File f : chain) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indent; i++) {
                sb.append(' ');
            }
            sb.append(pathDir).append(f.getName());
            dirs.add(new JtvDirEntry(sb.toString(), f.getAbsolutePath()));
            cur = dirs.size() - 1;
            indent += 2;
        }

        File parent = curDir.getParentFile() != null ? curDir.getParentFile() : curDir;
        File[] children = parent.listFiles();
        if (children == null) {
            return;
        }
        List<File> onlyDirs = new ArrayList<File>();
        for (File c : children) {
            if (c.isDirectory()) {
                onlyDirs.add(c);
            }
        }
        Collections.sort(onlyDirs, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        for (File d : onlyDirs) {
            if (d.equals(curDir)) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indent; i++) {
                sb.append(' ');
            }
            sb.append(middleDir).append(d.getName());
            dirs.add(new JtvDirEntry(sb.toString(), d.getAbsolutePath()));
        }
    }

    /**
     * Extends the inherited {@code setState} to toggle the Chdir button's default
     * state in the owning {@link JtvChangeDirDialog} when this list receives or loses focus.
     *
     * @param nState the state flag(s) being changed
     * @param enable {@code true} to set, {@code false} to clear
     */
    @Override
    public void setState(int nState, boolean enable) {
        super.setState(nState, enable);
        if ((nState & sfFocused) != 0 && owner instanceof JtvChangeDirDialog) {
            ((JtvChangeDirDialog) owner).getChDirButton().makeDefault(enable);
        }
    }
}
