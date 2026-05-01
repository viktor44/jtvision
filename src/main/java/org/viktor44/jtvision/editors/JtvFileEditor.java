/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.editors;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmNo;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmSave;
import static org.viktor44.jtvision.core.CommandCodes.cmSaveAs;
import static org.viktor44.jtvision.core.CommandCodes.cmUpdateTitle;
import static org.viktor44.jtvision.core.CommandCodes.cmValid;
import static org.viktor44.jtvision.core.CommandCodes.cmYes;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;
import static org.viktor44.jtvision.core.ViewFlags.mfError;
import static org.viktor44.jtvision.core.ViewFlags.mfInformation;
import static org.viktor44.jtvision.core.ViewFlags.mfOKButton;
import static org.viktor44.jtvision.core.ViewFlags.mfYesNoCancel;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;
import static org.viktor44.jtvision.core.ViewFlags.sfActive;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvProgram;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvCheckBoxes;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvHistory;
import org.viktor44.jtvision.dialogs.JtvInputLine;
import org.viktor44.jtvision.dialogs.JtvLabel;
import org.viktor44.jtvision.util.MessageBox;
import org.viktor44.jtvision.views.JtvScrollBar;

import lombok.Getter;

/**
 * A file-backed editor that loads and saves text files.
 *
 * <p>{@code JtvFileEditor} extends {@link JtvEditor} with file I/O, search-and-replace dialogs,
 * and {@code cmSave}/{@code cmSaveAs} command handling.  It is normally hosted inside a
 * {@link JtvEditWindow}, which adds a window frame, scroll bars, and an indicator.
 *
 * <p>On construction the file named by {@code aFileName} is read into the edit buffer.
 * If the file does not exist the buffer starts empty (ready for a new file).
 * Before the editor closes, {@link #valid(int)} prompts the user to save if the buffer
 * has been modified.
 */
public class JtvFileEditor extends JtvEditor {

	/** Search flag: match is case-sensitive. */
    private static final int efCaseSensitive = 0x0001;
    /** Search flag: match only whole words. */
    private static final int efWholeWordsOnly = 0x0002;
    /** Search flag: prompt before replacing each occurrence. */
    private static final int efPromptOnReplace = 0x0004;
    /** Search flag: replace all occurrences without stopping. */
    private static final int efReplaceAll = 0x0008;
    /** Internal flag: a replace (not just find) operation is in progress. */
    private static final int efDoReplace = 0x0010;

    /** Most recently used search string; shared across all {@code TFileEditor} instances. */
    private static String findText = "";
    /** Most recently used replacement string; shared across all instances. */
    private static String replaceText = "";
    /** Current combination of {@code ef*} flags; shared across all instances. */
    private static int editorFlags = 0;

    /**
     * The path of the file currently being edited.
     * Empty if editing an untitled (unsaved) file.
     */
    @Getter
    private String fileName;

    /**
     * Constructs a file editor.  If {@code aFileName} is non-empty, the named file is
     * loaded into the buffer immediately via {@link #loadFile()}.
     *
     * @param bounds       the bounding rectangle for this editor view
     * @param hScrollBar  optional horizontal scroll bar, or {@code null}
     * @param vScrollBar  optional vertical scroll bar, or {@code null}
     * @param indicator   optional position indicator, or {@code null}
     * @param fileName    path of the file to edit, or empty/null for a new untitled file
     */
    public JtvFileEditor(JtvRect bounds, JtvScrollBar hScrollBar, JtvScrollBar vScrollBar, JtvIndicator indicator, String fileName) {
        super(bounds, hScrollBar, vScrollBar, indicator, 0x1000);
        this.fileName = normalizeFileName(fileName);
        if (!fileName.isEmpty()) {
            loadFile();
        }
    }

    /**
     * Reads the file named by {@link #fileName} into the edit buffer.
     * If the file does not exist, the buffer is cleared (ready to create a new file)
     * and {@code true} is returned.  Displays an error message and returns {@code false}
     * on read failure.
     *
     * @return {@code true} if the file was loaded (or does not yet exist); {@code false} on error
     */
    public boolean loadFile() {
        Path p = Paths.get(fileName);
        if (!Files.exists(p)) {
            buffer.setLength(0);
            curPtr = 0;
            selStart = selEnd = 0;
            modified = false;
            notifyTitleUpdate();
            updateMetrics();
            return true;
        }
        try {
            byte[] bytes = Files.readAllBytes(p);
            String text = new String(bytes, StandardCharsets.UTF_8);
            buffer.setLength(0);
            buffer.append(text);
            curPtr = 0;
            selStart = selEnd = 0;
            modified = false;
            notifyTitleUpdate();
            updateMetrics();
            drawView();
            return true;
        } catch (IOException e) {
            MessageBox.messageBox("Read error: " + fileName, 0x0001 | 0x0400);
            return false;
        }
    }

    /**
     * Saves the buffer.  If the file has no name yet, delegates to {@link #saveAs()};
     * otherwise delegates to {@link #saveFile()}.
     *
     * @return {@code true} if the save succeeded; {@code false} if cancelled or on error
     */
    public boolean save() {
        if (fileName == null || fileName.isEmpty()) {
            return saveAs();
        }
        return saveFile();
    }

    /**
     * Presents a file-save dialog to let the user choose a file name, then saves the buffer.
     * Updates {@link #fileName} and broadcasts {@code cmUpdateTitle} on success.
     *
     * @return {@code true} if the file was saved; {@code false} if the dialog was cancelled or on error
     */
    public boolean saveAs() {
        String initial = (fileName == null || fileName.isEmpty()) ? "*.*" : fileName;
        org.viktor44.jtvision.dialogs.JtvFileDialog d =
            new org.viktor44.jtvision.dialogs.JtvFileDialog(
                "*.*", "Save file as", "~N~ame", org.viktor44.jtvision.core.ViewFlags.fdOKButton, 101);
        d.getFileName().setData(initial);
        int result = JtvProgram.getDesktop().execView(d);
        if (result != org.viktor44.jtvision.core.CommandCodes.cmCancel) {
            StringBuilder sb = new StringBuilder();
            d.getFileName(sb);
            String newName = sb.toString();
            if (newName == null || newName.trim().isEmpty()) {
                JtvProgram.getDesktop().destroy(d);
                return false;
            }
            fileName = normalizeFileName(newName);
            message(owner, evBroadcast, cmUpdateTitle, this);
            boolean saved = saveFile();
            JtvProgram.getDesktop().destroy(d);
            return saved;
        }
        JtvProgram.getDesktop().destroy(d);
        return false;
    }

    /**
     * Writes the buffer to the file named by {@link #fileName} using UTF-8 encoding.
     * Clears the {@link JtvEditor#modified} flag and redraws on success.
     * Displays an error message and returns {@code false} on write failure.
     *
     * @return {@code true} if the file was written successfully; {@code false} on error
     */
    public boolean saveFile() {
        try {
            Files.write(Paths.get(fileName), buffer.toString().getBytes(StandardCharsets.UTF_8));
            modified = false;
            notifyTitleUpdate();
            updateMetrics();
            drawView();
            return true;
        } catch (IOException e) {
            MessageBox.messageBox("Write error: " + fileName, 0x0001 | 0x0400);
            return false;
        }
    }

    private static String normalizeFileName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        try {
            Path path = Paths.get(name);
            if (Files.exists(path)) {
                try {
                    return path.toRealPath().toString();
                } catch (IOException ignored) {
                    // Fall back to normalized absolute path below.
                }
            }
            return path.toAbsolutePath().normalize().toString();
        } catch (InvalidPathException | SecurityException e) {
            return name;
        }
    }

    /**
     * Opens the Find dialog to let the user enter a search string and options,
     * then performs the search.
     */
    @Override
    protected void doFind() {
        FindReplaceState state = new FindReplaceState();
        state.find = findText;
        state.flags = editorFlags;

        JtvDialog d = createFindDialog();
        if (JtvProgram.getApplication() != null && JtvProgram.getApplication().executeDialog(d, state) != cmCancel) {
            findText = state.find;
            editorFlags = state.flags & ~efDoReplace;
            doSearchReplace();
        }
    }

    /**
     * Opens the Replace dialog to let the user enter search and replacement strings and
     * options, then performs the search-and-replace.
     */
    @Override
    protected void doReplace() {
        FindReplaceState state = new FindReplaceState();
        state.find = findText;
        state.replace = replaceText;
        state.flags = editorFlags;

        JtvDialog d = createReplaceDialog();
        if (JtvProgram.getApplication() != null && JtvProgram.getApplication().executeDialog(d, state) != cmCancel) {
            findText = state.find;
            replaceText = state.replace;
            editorFlags = state.flags | efDoReplace;
            doSearchReplace();
        }
    }

    /**
     * Repeats the most recent search/replace operation using the saved {@code findText},
     * {@code replaceText}, and {@code editorFlags}.
     */
    @Override
    protected void doSearchAgain() {
        doSearchReplace();
    }

    private void doSearchReplace() {
        int answer;
        do {
            answer = cmCancel;
            if (!search(findText, editorFlags)) {
                if ((editorFlags & (efReplaceAll | efDoReplace)) != (efReplaceAll | efDoReplace)) {
                    MessageBox.messageBox("Search string not found.", mfError | mfOKButton);
                }
            } else if ((editorFlags & efDoReplace) != 0) {
                answer = cmYes;
                if ((editorFlags & efPromptOnReplace) != 0) {
                    answer = MessageBox.messageBox("Replace this occurrence?", mfInformation | mfYesNoCancel);
                }
                if (answer == cmYes) {
                    int a = Math.min(selStart, selEnd);
                    int b = Math.max(selStart, selEnd);
                    int replLen = replaceText != null ? replaceText.length() : 0;
                    if ((editorFlags & efReplaceAll) == 0) {
                        snapshotUndo(a, buffer.substring(a, b), replLen);
                    }
                    buffer.replace(a, b, replaceText != null ? replaceText : "");
                    curPtr = a + replLen;
                    selStart = selEnd = curPtr;
                    modified = true;
                    canUndo = true;
                    updateMetricsIncremental(a, b - a, replLen);
                    trackCursor(false);
                }
            }
        } while (answer != cmCancel && (editorFlags & efReplaceAll) != 0);
    }

    private boolean search(String findStr, int opts) {
        if (findText == null || findText.isEmpty()) {
            return false;
        }

        int start = curPtr;

        String hay = buffer.toString();
        String needle = findStr;
        if ((opts & efCaseSensitive) == 0) {
            hay = hay.toLowerCase(Locale.ROOT);
            needle = needle.toLowerCase(Locale.ROOT);
        }

        int idx = hay.indexOf(needle, Math.max(0, start));
        while (idx >= 0) {
            int end = idx + needle.length();
            if ((opts & efWholeWordsOnly) == 0 || isWholeWord(hay, idx, end)) {
                setSelect(idx, end, false);
                trackCursor(false);
                drawView();
                return true;
            }
            idx = hay.indexOf(needle, end);
        }
        return false;
    }

    private boolean isWholeWord(String text, int start, int end) {
        boolean leftOk = start <= 0 || !Character.isLetterOrDigit(text.charAt(start - 1));
        boolean rightOk = end >= text.length() || !Character.isLetterOrDigit(text.charAt(end));
        return leftOk && rightOk;
    }

    private JtvDialog createFindDialog() {
        return new FindReplaceDialog(false);
    }

    private JtvDialog createReplaceDialog() {
        return new FindReplaceDialog(true);
    }

    private static class FindReplaceState {
        private String find;
        private String replace;
        private int flags;
    }

    private static class FindReplaceDialog extends JtvDialog {
        private final boolean replaceMode;
        private final JtvInputLine findInput;
        private final JtvInputLine replaceInput;
        private final JtvCheckBoxes checks;

        FindReplaceDialog(boolean replaceMode) {
            super(replaceMode ? new JtvRect(0, 0, 40, 16) : new JtvRect(0, 0, 38, 12),
                replaceMode ? "Replace" : "Find");
            this.replaceMode = replaceMode;
            options |= ofCentered;

            if (replaceMode) {
                findInput = new JtvInputLine(new JtvRect(3, 3, 34, 4), 80);
                insert(findInput);
                insert(new JtvLabel(new JtvRect(2, 2, 15, 3), "~T~ext to find", findInput));
                insert(new JtvHistory(new JtvRect(34, 3, 37, 4), findInput, 10));

                replaceInput = new JtvInputLine(new JtvRect(3, 6, 34, 7), 80);
                insert(replaceInput);
                insert(new JtvLabel(new JtvRect(2, 5, 12, 6), "~N~ew text", replaceInput));
                insert(new JtvHistory(new JtvRect(34, 6, 37, 7), replaceInput, 11));

                checks = new JtvCheckBoxes(new JtvRect(3, 8, 37, 12),
                    new String[] {"~C~ase sensitive", "~W~hole words only", "~P~rompt on replace", "~R~eplace all"});
                insert(checks);

                insert(new JtvButton(new JtvRect(17, 13, 27, 15), "O~K~", cmOK, bfDefault));
                insert(new JtvButton(new JtvRect(28, 13, 38, 15), "Cancel", cmCancel, bfNormal));
            } else {
                findInput = new JtvInputLine(new JtvRect(3, 3, 32, 4), 80);
                insert(findInput);
                insert(new JtvLabel(new JtvRect(2, 2, 15, 3), "~T~ext to find", findInput));
                insert(new JtvHistory(new JtvRect(32, 3, 35, 4), findInput, 10));

                replaceInput = null;
                checks = new JtvCheckBoxes(new JtvRect(3, 5, 35, 7),
                    new String[] {"~C~ase sensitive", "~W~hole words only"});
                insert(checks);

                insert(new JtvButton(new JtvRect(14, 9, 24, 11), "O~K~", cmOK, bfDefault));
                insert(new JtvButton(new JtvRect(26, 9, 36, 11), "Cancel", cmCancel, bfNormal));
            }

            selectNext(false);
        }

        @Override
        public void setDataFrom(Object rec) {
            if (rec instanceof FindReplaceState) {
                FindReplaceState s = (FindReplaceState) rec;
                findInput.setData(s.find != null ? s.find : "");
                if (replaceInput != null) {
                    replaceInput.setData(s.replace != null ? s.replace : "");
                }
                checks.setValue(replaceMode
                    ? (s.flags & (efCaseSensitive | efWholeWordsOnly | efPromptOnReplace | efReplaceAll))
                    : (s.flags & (efCaseSensitive | efWholeWordsOnly)));
                drawView();
            }
        }

        @Override
        public void getDataTo(Object rec) {
            if (rec instanceof FindReplaceState) {
                FindReplaceState s = (FindReplaceState) rec;
                s.find = findInput.getData() != null ? findInput.getData() : "";
                s.replace = replaceInput != null && replaceInput.getData() != null ? replaceInput.getData() : "";
                s.flags = checks.getValue();
            }
        }
    }

    /**
     * Handles events by first delegating to the inherited {@link JtvEditor} handler, then
     * intercepts {@code cmSave} and {@code cmSaveAs} commands to save the file.
     *
     * @param event the event to handle
     */
    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmSave:
                    save();
                    clearEvent(event);
                    break;
                case cmSaveAs:
                    saveAs();
                    clearEvent(event);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Extends the base command update to enable {@code cmSave} and {@code cmSaveAs} when
     * the editor is active, and disable them when it is not focused.
     */
    @Override
    protected void updateCommands() {
        super.updateCommands();
        if (!readOnly && (state & sfActive) != 0) {
            enableCommand(cmSave);
            enableCommand(cmSaveAs);
        } else {
            disableCommand(cmSave);
            disableCommand(cmSaveAs);
        }
    }

    /**
     * Validates the editor for the given command.  For {@code cmValid}, returns
     * {@link JtvEditor#isValid}.  For any other command (typically {@code cmClose}), if the
     * buffer has been modified, prompts the user to save, discard, or cancel.
     *
     * @param command the validation command
     * @return {@code true} if it is safe to proceed; {@code false} if the user cancelled
     */
    @Override
    public boolean valid(int command) {
        if (command == cmValid) {
            return isValid;
        }

        if (!modified) {
            return true;
        }

        int answer;
        if (fileName == null || fileName.isEmpty()) {
            answer = MessageBox.messageBox("Save untitled file?", mfInformation | mfYesNoCancel);
        }
        else {
            answer = MessageBox.messageBox(fileName + " has been modified. Save?", mfInformation | mfYesNoCancel);
        }

        switch (answer) {
            case cmYes:
                return save();
            case cmNo:
                modified = false;
                notifyTitleUpdate();
                updateMetrics();
                drawView();
                return true;
            default:
                return false;
        }
    }
}
