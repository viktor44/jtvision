/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmFileClear;
import static org.viktor44.jtvision.core.CommandCodes.cmFileDoubleClicked;
import static org.viktor44.jtvision.core.CommandCodes.cmFileInit;
import static org.viktor44.jtvision.core.CommandCodes.cmFileOpen;
import static org.viktor44.jtvision.core.CommandCodes.cmFileReplace;
import static org.viktor44.jtvision.core.CommandCodes.cmHelp;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;
import static org.viktor44.jtvision.core.ViewFlags.fdClearButton;
import static org.viktor44.jtvision.core.ViewFlags.fdHelpButton;
import static org.viktor44.jtvision.core.ViewFlags.fdNoLoadDir;
import static org.viktor44.jtvision.core.ViewFlags.fdOKButton;
import static org.viktor44.jtvision.core.ViewFlags.fdOpenButton;
import static org.viktor44.jtvision.core.ViewFlags.fdReplaceButton;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowAll;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowLoX;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;
import static org.viktor44.jtvision.core.ViewFlags.wfGrow;

import java.io.File;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvProgram;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.util.MessageBox;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * A standard file-open / file-save dialog box.
 * <p>
 * TFileDialog presents a resizable dialog that lets the user browse and select files.
 * It contains:
 * <ul>
 *   <li>A filename input line ({@link JtvFileInputLine}) with a history list.</li>
 *   <li>A file list box ({@link JtvFileList}) showing the current directory.</li>
 *   <li>A file information pane ({@link JtvFileInfoPane}) at the bottom.</li>
 *   <li>Configurable action buttons controlled by the {@code aOptions} flag.</li>
 * </ul>
 * <p>
 * The set of buttons is controlled by {@code aOptions} (see {@code fdXXXX} constants):
 * {@code fdOpenButton}, {@code fdOKButton}, {@code fdReplaceButton}, {@code fdClearButton},
 * {@code fdHelpButton}. A Cancel button is always present.
 * <p>
 * When an action button is pressed, the dialog ends its modal state with the
 * corresponding command ({@code cmFileOpen}, {@code cmFileReplace}, {@code cmFileClear}).
 * A double-click in the file list is treated as {@code cmOK}.
 * <p>
 * {@link #valid(int)} checks the filename input line before closing:
 * <ul>
 *   <li>A wildcard updates the directory/filter and reloads the list without closing.</li>
 *   <li>A directory path navigates into that directory without closing.</li>
 *   <li>A plain filename closes the dialog.</li>
 * </ul>
 * <p>
 * After the dialog closes, the fully-qualified selected file path can be retrieved
 * via {@link #getFileName(StringBuilder)}.
 */
public class JtvFileDialog extends JtvDialog {

	private static final String filesText = "~F~iles";
    private static final String openText = "~O~pen";
    private static final String okText = "~O~K";
    private static final String replaceText = "~R~eplace";
    private static final String clearText = "~C~lear";
    private static final String cancelText = "Cancel";
    private static final String helpText = "~H~elp";
    private static final String invalidDriveText = "Invalid directory";
    private static final String invalidFileText = "Invalid file name";

    /**
     * The filename input line.
     * Exposed so callers can read or set the filename programmatically.
     */
    private JtvFileInputLine fileName;

	/** The file list box that displays directory contents. */
    private JtvFileList fileList;

    /**
     * The current wildcard filter (e.g., {@code "*.java"}).
     * Updated when the user types a wildcard pattern in the input line.
     */
    private String wildCard;

	/**
     * The absolute path of the directory currently displayed, always ending with a file separator character.
     */
    private String directory;

    /**
     * Creates the file dialog.
     * <p>
     * The dialog is initially centred and sized for the current screen, expanding
     * further if the screen is large enough. Unless {@code fdNoLoadDir} is set in
     * {@code aOptions}, the file list is loaded immediately with the current directory.
     *
     * @param aWildCard  the initial wildcard filter (e.g., {@code "*.java"}); defaults
     *                   to {@code "*"} if {@code null}
     * @param aTitle     the dialog title bar text
     * @param inputName  the label string for the file-name input line
     * @param aOptions   combination of {@code fdXXXX} flag constants controlling
     *                   which buttons appear
     * @param histId     the history list ID for the filename input line
     */
    public JtvFileDialog(String aWildCard, String aTitle, String inputName, int aOptions, int histId) {
        super(new JtvRect(15, 1, 64, 20), aTitle);
        options |= ofCentered;
        flags |= wfGrow;
        wildCard = aWildCard != null ? aWildCard : "*";
        directory = System.getProperty("user.dir") + File.separator;

        fileName = new JtvFileInputLine(new JtvRect(3, 3, 31, 4), 255);
        fileName.setData(wildCard);
        insert(fileName);
        first().setGrowMode(gfGrowHiX);

        insert(new JtvLabel(new JtvRect(2, 2, 3 + inputName.length(), 3), inputName, fileName));
        first().setGrowMode(0);

        JtvHistory history = new JtvHistory(new JtvRect(31, 3, 34, 4), fileName, histId);
        history.setGrowMode(gfGrowLoX | gfGrowHiX);
        insert(history);

        JtvScrollBar sb = new JtvScrollBar(new JtvRect(3, 14, 34, 15));
        insert(sb);
        insert(fileList = new JtvFileList(new JtvRect(3, 6, 34, 14), sb));
        first().setGrowMode(gfGrowHiX | gfGrowHiY);
        insert(new JtvLabel(new JtvRect(2, 5, 8, 6), filesText, fileList));
        first().setGrowMode(0);

        int opt = bfDefault;
        JtvRect r = new JtvRect(35, 3, 46, 5);
        if ((aOptions & fdOpenButton) != 0) {
            addButton(r, openText, cmFileOpen, opt);
            opt = bfNormal;
            r.move(0, 3);
        }
        if ((aOptions & fdOKButton) != 0) {
            addButton(r, okText, cmFileOpen, opt);
            opt = bfNormal;
            r.move(0, 3);
        }
        if ((aOptions & fdReplaceButton) != 0) {
            addButton(r, replaceText, cmFileReplace, opt);
            opt = bfNormal;
            r.move(0, 3);
        }
        if ((aOptions & fdClearButton) != 0) {
            addButton(r, clearText, cmFileClear, opt);
            opt = bfNormal;
            r.move(0, 3);
        }
        addButton(r, cancelText, cmCancel, bfNormal);
        r.move(0, 3);
        if ((aOptions & fdHelpButton) != 0) {
            addButton(r, helpText, cmHelp, bfNormal);
        }

        JtvFileInfoPane infoPane = new JtvFileInfoPane(new JtvRect(1, 16, 48, 18));
        insert(infoPane);
        first().setGrowMode(gfGrowAll & ~gfGrowLoX);

        selectNext(false);

        JtvProgram app = JtvProgram.getApplication();
        if (app != null) {
            JtvRect bounds = getBounds();
            JtvRect screenBounds = app.getBounds();

            if (app.getSize().getX() > 90) {
                bounds.grow(15, 0);
            } else if (app.getSize().getX() > 63) {
                screenBounds.grow(-7, 0);
                bounds.setA(new JtvPoint(screenBounds.getA().getX(), bounds.getA().getY()));
                bounds.setB(new JtvPoint(screenBounds.getB().getX(), bounds.getB().getY()));
            }

            if (app.getSize().getY() > 34) {
                bounds.grow(0, 5);
            } else if (app.getSize().getY() > 25) {
                screenBounds.grow(0, -3);
                bounds.setA(new JtvPoint(bounds.getA().getX(), screenBounds.getA().getY()));
                bounds.setB(new JtvPoint(bounds.getB().getX(), screenBounds.getB().getY()));
            }

            locate(bounds);
        }

        if ((aOptions & fdNoLoadDir) == 0) {
            readDirectory();
        }
    }

    /** Creates and inserts a growing button with the given parameters. */
    private void addButton(JtvRect r, String title, int cmd, int flags) {
        JtvButton b = new JtvButton(new JtvRect(r), title, cmd, flags);
        b.setGrowMode(gfGrowLoX | gfGrowHiX);
        insert(b);
    }

    /**
     * Enforces a minimum dialog size of 49×19 characters.
     *
     * @param min receives the minimum size
     * @param max receives the maximum size
     */
    @Override
    public JtvPoint getMinimumSize() {
        return new JtvPoint(49, 19);
    }

    /**
     * Builds the fully-qualified path of the selected file and appends it to
     * {@code out}. If the filename in the input line is relative, it is resolved
     * against the current {@link #directory}.
     *
     * @param out the {@link StringBuilder} to receive the absolute file path
     */
    public void getFileName(StringBuilder out) {
        String text = fileName.getData() != null ? fileName.getData().trim() : "";
        File f = new File(text);
        if (!f.isAbsolute()) {
            f = new File(directory, text);
        }
        out.setLength(0);
        out.append(f.getAbsolutePath());
    }

    /**
     * Handles {@code cmFileOpen}, {@code cmFileReplace}, and {@code cmFileClear}
     * commands by ending the modal state. Handles {@code cmFileDoubleClicked}
     * broadcasts from the file list by converting them to a {@code cmOK} command.
     *
     * @param event the incoming event
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmFileOpen:
                case cmFileReplace:
                case cmFileClear:
                    endModal(event.getMessage().getCommand());
                    clearEvent(event);
                    break;
                default:
                    break;
            }
        } else if (event.getWhat() == evBroadcast && event.getMessage().getCommand() == cmFileDoubleClicked) {
            event.setWhat(evCommand);
            event.getMessage().setCommand(cmOK);
            putEvent(event);
            clearEvent(event);
        }
    }

    /**
     * Reloads the file list from the current JVM working directory using the
     * active {@link #wildCard} filter.
     */
    public void readDirectory() {
        File cur = new File(System.getProperty("user.dir"));
        directory = cur.getAbsolutePath() + File.separator;
        fileList.readDirectory(directory, wildCard);
    }

    /**
     * Handles data transfer from the dialog. If the record is a string that
     * contains a wildcard, re-initialises the file list filter.
     *
     * @param rec the source data record
     */
    @Override
    public void setDataFrom(Object rec) {
        super.setDataFrom(rec);
        if (rec instanceof String) {
            String s = (String) rec;
            if (s.contains("*") || s.contains("?")) {
                valid(cmFileInit);
                fileName.select();
            }
        }
    }

    /**
     * Writes the selected file path into {@code rec} if it is a {@link StringBuilder}.
     *
     * @param rec a {@link StringBuilder} to receive the selected file path
     */
    @Override
    public void getDataTo(Object rec) {
        if (rec instanceof StringBuilder) {
            getFileName((StringBuilder) rec);
        }
    }

    /**
     * Shows an "invalid directory" message box and refocuses the filename input line.
     *
     * @param str the directory path that failed validation
     * @return {@code true} if the directory exists and is valid
     */
    private boolean checkDirectory(String str) {
        File d = new File(str);
        if (d.exists() && d.isDirectory()) {
            return true;
        }
        MessageBox.messageBox(invalidDriveText + ": '" + str + "'", 0x0001 | 0x0400);
        fileName.select();
        return false;
    }

    /**
     * Validates the dialog before closing.
     * <ul>
     *   <li>Zero or {@code cmCancel} / {@code cmFileClear} — always valid.</li>
     *   <li>Wildcard in the input line — updates the filter and reloads the list,
     *       returns {@code false} to keep the dialog open.</li>
     *   <li>Directory path in the input line — navigates to that directory,
     *       returns {@code false} to keep the dialog open.</li>
     *   <li>Plain filename — returns {@code true} allowing the dialog to close.</li>
     * </ul>
     *
     * @param command the command that is attempting to close the dialog
     * @return {@code true} if the dialog may close; {@code false} otherwise
     */
    @Override
    public boolean valid(int command) {
        if (command == 0) {
            return true;
        }
        if (!super.valid(command)) {
            return false;
        }
        if (command == cmCancel || command == cmFileClear) {
            return true;
        }

        String text = fileName.getData() != null ? fileName.getData().trim() : "";
        if (text.isEmpty()) {
            return true;
        }

        File f = new File(text);
        if (!f.isAbsolute()) {
            f = new File(directory, text);
        }

        if (text.contains("*") || text.contains("?")) {
            File dir = f.getParentFile();
            if (dir == null) {
                dir = new File(directory);
            }
            if (checkDirectory(dir.getAbsolutePath())) {
                directory = dir.getAbsolutePath() + File.separator;
                wildCard = f.getName();
                if (command != cmFileInit) {
                    fileList.select();
                }
                fileList.readDirectory(directory, wildCard);
            }
            return false;
        }

        if (f.isDirectory()) {
            if (checkDirectory(f.getAbsolutePath())) {
                directory = f.getAbsolutePath() + File.separator;
                if (command != cmFileInit) {
                    fileList.select();
                }
                fileList.readDirectory(directory, wildCard);
            }
            return false;
        }

        String name = f.getName();
        if (name == null || name.trim().isEmpty()) {
            MessageBox.messageBox(invalidFileText + ": '" + text + "'", 0x0001 | 0x0400);
            return false;
        }
        return true;
    }

    /**
     * The filename input line.
     * Exposed so callers can read or set the filename programmatically.
     */
    public JtvFileInputLine getFileName() {
		return fileName;
	}

    /**
     * The current wildcard filter (e.g., {@code "*.java"}).
     * Updated when the user types a wildcard pattern in the input line.
     */
    public String getWildCard() {
		return wildCard;
	}

	/**
     * The absolute path of the directory currently displayed, always ending with a file separator character.
     */
    public String getDirectory() {
		return directory;
	}
}
