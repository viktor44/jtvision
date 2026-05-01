/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmChangeDir;
import static org.viktor44.jtvision.core.CommandCodes.cmHelp;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmRevert;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;
import static org.viktor44.jtvision.core.ViewFlags.cdHelpButton;
import static org.viktor44.jtvision.core.ViewFlags.cdNoLoadDir;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowLoX;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;
import static org.viktor44.jtvision.core.ViewFlags.wfGrow;

import java.io.File;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.util.MessageBox;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * A dialog box for changing the current working directory.
 * <p>
 * TChDirDialog presents a resizable "Change directory" dialog that contains:
 * <ul>
 *   <li>A directory name input line with a history list.</li>
 *   <li>A directory tree list box showing the current disk hierarchy.</li>
 *   <li>OK, Chdir, and Revert buttons (and optionally a Help button).</li>
 * </ul>
 * <p>
 * The dialog is controlled by option flags passed to the constructor:
 * <ul>
 *   <li>{@code cdHelpButton} — adds a Help button.</li>
 *   <li>{@code cdNoLoadDir} — skips loading the initial directory on construction
 *       (useful when the dialog is restored from a stream).</li>
 * </ul>
 * <p>
 * On OK, {@link #valid(int)} verifies that the text in the input line names a valid
 * existing directory and, if so, updates {@code System.getProperty("user.dir")}.
 */
public class JtvChangeDirDialog extends JtvDialog {

	private static final String changeDirTitle = "Change directory";
    private static final String dirNameText = "~D~irectory name";
    private static final String dirTreeText = "Directory ~t~ree";
    private static final String okText = "~O~K";
    private static final String chdirText = "~C~hdir";
    private static final String revertText = "~R~evert";
    private static final String helpText = "~H~elp";
    private static final String invalidText = "Invalid directory";

    /** The input line where the user types or views the current directory path. */
    private JtvInputLine dirInput;

    /** The directory tree list box. */
    private JtvDirListBox dirList;

    /** The OK button that confirms the directory change. */
    private JtvButton okButton;

    /**
     * The Chdir button that navigates to the directory currently selected in
     * the tree list box. Exposed so that {@link JtvDirListBox} can toggle its
     * default state when the list box receives or loses focus.
     */
    private JtvButton chDirButton;

    /**
     * Creates the change-directory dialog.
     *
     * @param opts    option flags: {@code cdHelpButton} to include a Help button,
     *                {@code cdNoLoadDir} to skip initial directory loading
     * @param histId  the history list ID for the directory name input line
     */
    public JtvChangeDirDialog(int opts, int histId) {
        super(new JtvRect(16, 2, 64, 20), changeDirTitle);
        options |= ofCentered;
        flags |= wfGrow;

        dirInput = new JtvInputLine(new JtvRect(3, 3, 42, 4), 254);
        dirInput.setGrowMode(gfGrowHiX);
        insert(dirInput);
        insert(new JtvLabel(new JtvRect(2, 2, 17, 3), dirNameText, dirInput));

        JtvHistory history = new JtvHistory(new JtvRect(42, 3, 45, 4), dirInput, histId);
        history.setGrowMode(gfGrowLoX | gfGrowHiX);
        insert(history);

        JtvScrollBar sb = new JtvScrollBar(new JtvRect(32, 6, 33, 16));
        insert(sb);
        dirList = new JtvDirListBox(new JtvRect(3, 6, 32, 16), sb);
        dirList.setGrowMode(gfGrowHiX | gfGrowHiY);
        insert(dirList);
        insert(new JtvLabel(new JtvRect(2, 5, 17, 6), dirTreeText, dirList));

        okButton = new JtvButton(new JtvRect(35, 6, 45, 8), okText, cmOK, bfDefault);
        okButton.setGrowMode(gfGrowLoX | gfGrowHiX);
        insert(okButton);

        chDirButton = new JtvButton(new JtvRect(35, 9, 45, 11), chdirText, cmChangeDir, bfNormal);
        chDirButton.setGrowMode(gfGrowLoX | gfGrowHiX);
        insert(chDirButton);

        JtvButton revertButton = new JtvButton(new JtvRect(35, 12, 45, 14), revertText, cmRevert, bfNormal);
        revertButton.setGrowMode(gfGrowLoX | gfGrowHiX);
        insert(revertButton);

        if ((opts & cdHelpButton) != 0) {
            JtvButton helpButton = new JtvButton(new JtvRect(35, 15, 45, 17), helpText, cmHelp, bfNormal);
            helpButton.setGrowMode(gfGrowLoX | gfGrowHiX);
            insert(helpButton);
        }

        if ((opts & cdNoLoadDir) == 0) {
            setUpDialog();
        }
        selectNext(false);
    }

    /**
     * Returns 0 — this dialog does not participate in standard data transfer.
     *
     * @return 0
     */
    @Override
    public int dataSize() {
        return 0;
    }

    /**
     * Does nothing — data transfer is not implemented for this dialog.
     *
     * @param rec unused
     */
    @Override
    public void getDataTo(Object rec) {
    }

    /**
     * Does nothing — data transfer is not implemented for this dialog.
     *
     * @param rec unused
     */
    @Override
    public void setDataFrom(Object rec) {
    }

    /**
     * Enforces a minimum dialog size of 48×18 characters.
     *
     * @param min receives the minimum size
     * @param max receives the maximum size
     */
    @Override
    public JtvPoint getMinimumSize() {
        return new JtvPoint(48, 18);
    }

    /**
     * Handles {@code cmRevert} and {@code cmChangeDir} commands.
     * <p>
     * {@code cmRevert} restores the current working directory.
     * {@code cmChangeDir} navigates to the directory indicated by the
     * {@link JtvDirEntry} carried in the event.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() != evCommand) {
            return;
        }
        String curDir;
        switch (event.getMessage().getCommand()) {
            case cmRevert:
                curDir = System.getProperty("user.dir");
                break;
            case cmChangeDir:
                if (event.getMessage().getInfoPtr() instanceof JtvDirEntry) {
                    JtvDirEntry p = (JtvDirEntry) event.getMessage().getInfoPtr();
                    curDir = p.dir();
                } else {
                    return;
                }
                break;
            default:
                return;
        }
        dirList.newDirectory(curDir);
        dirInput.setData(trimEndSeparator(curDir));
        dirInput.drawView();
        dirList.select();
        clearEvent(event);
    }

    /** Loads the initial directory tree and sets the input line to the current working directory. */
    private void setUpDialog() {
        String curDir = System.getProperty("user.dir");
        dirList.newDirectory(curDir);
        dirInput.setData(trimEndSeparator(curDir));
        dirInput.drawView();
    }

    /** Removes trailing path separators from {@code path}, leaving at least one character. */
    private String trimEndSeparator(String path) {
        if (path == null) {
            return "";
        }
        while (path.length() > 1 && (path.endsWith("/") || path.endsWith("\\"))) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * Validates the dialog before it is closed with {@code cmOK}.
     * <p>
     * Checks that the directory named in the input line exists and is a directory.
     * If it is valid, sets {@code System.setProperty("user.dir")} to the absolute path.
     * On failure, displays an error message box and returns {@code false}.
     * All other commands always return {@code true}.
     *
     * @param command the command that is attempting to close the dialog
     * @return {@code true} if the dialog may close; {@code false} if validation failed
     */
    @Override
    public boolean valid(int command) {
        if (command != cmOK) {
            return true;
        }
        String path = dirInput.getData() != null ? dirInput.getData().trim() : "";
        if (path.isEmpty()) {
            return false;
        }
        File f = new File(path);
        if (!f.isAbsolute()) {
            f = new File(System.getProperty("user.dir"), path);
        }
        if (!f.exists() || !f.isDirectory()) {
            MessageBox.messageBox(invalidText + ": '" + f.getAbsolutePath() + "'.", 0x0001 | 0x0400);
            return false;
        }
        System.setProperty("user.dir", f.getAbsolutePath());
        return true;
    }

    /**
     * The Chdir button that navigates to the directory currently selected in
     * the tree list box. Exposed so that {@link JtvDirListBox} can toggle its
     * default state when the list box receives or loses focus.
     */
    public JtvButton getChDirButton() {
		return chDirButton;
	}
}
