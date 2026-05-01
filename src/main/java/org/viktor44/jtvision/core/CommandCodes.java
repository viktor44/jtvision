/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

/**
 * Standard command codes, matching JT Vision conventions.
 */
public final class CommandCodes {

    private CommandCodes() {}

    // -------------------------------------------------------------------------
    // Standard command codes
    // -------------------------------------------------------------------------

    /** Passed to {@code valid()} to check whether a newly instantiated view is valid. */
    public static final int cmValid = 0;

    /** Terminates the application by calling {@code endModal(cmQuit)} on the application object. */
    public static final int cmQuit = 1;

    /** Never handled by any object; can represent an unimplemented or unsupported command. */
    public static final int cmError = 2;

    /** Causes a menu view to call {@code execView()} on itself to begin a menu-selection process. */
    public static final int cmMenu = 3;

    /**
     * Closes a window. If the window is modal a {@code cmCancel} command is generated via
     * {@code putEvent()}; otherwise the window's {@code close()} method is called directly.
     */
    public static final int cmClose = 4;

    /** Causes a zoomable window to call its {@code zoom()} method. */
    public static final int cmZoom = 5;

    /** Causes a resizable window to call {@code dragView()} on itself so the user can resize it. */
    public static final int cmResize = 6;

    /** Selects the next window on the desktop. */
    public static final int cmNext = 7;

    /** Selects the previous window on the desktop. */
    public static final int cmPrev = 8;

    /** Opens the context-sensitive help system for the currently focused view. */
    public static final int cmHelp = 9;

    // -------------------------------------------------------------------------
    // TDialog standard commands
    // -------------------------------------------------------------------------

    /** The OK button was pressed; the dialog should accept its data and close. */
    public static final int cmOK = 10;

    /** The Cancel button, close icon, or Escape key was used; the dialog should discard its data and close. */
    public static final int cmCancel = 11;

    /** The Yes button was pressed in a message box. */
    public static final int cmYes = 12;

    /** The No button was pressed in a message box. */
    public static final int cmNo = 13;

    /** The default button in a dialog was activated. */
    public static final int cmDefault = 14;

    // -------------------------------------------------------------------------
    // Application edit commands
    // -------------------------------------------------------------------------

    /** Cut the selected text and place it on the clipboard. */
    public static final int cmCut = 20;

    /** Copy the selected text to the clipboard without removing it. */
    public static final int cmCopy = 21;

    /** Paste the clipboard contents at the current insertion point. */
    public static final int cmPaste = 22;

    /** Undo the last editing operation. */
    public static final int cmUndo = 23;

    /** Delete the selected text without copying it to the clipboard. */
    public static final int cmClear = 24;

    /** Tile all tileable windows on the desktop. */
    public static final int cmTile = 25;

    /** Cascade all tileable windows on the desktop. */
    public static final int cmCascade = 26;

    /** Redo the last undone editing operation. */
    public static final int cmRedo = 27;

    // -------------------------------------------------------------------------
    // Standard application file commands
    // -------------------------------------------------------------------------

    /** Open a new, empty document (File | New). */
    public static final int cmNew = 30;

    /** Open an existing file from disk (File | Open). */
    public static final int cmOpen = 31;

    /** Save the current document to its existing file (File | Save). */
    public static final int cmSave = 32;

    /** Save the current document under a new name (File | Save As). */
    public static final int cmSaveAs = 33;

    /** Save all open documents (File | Save All). */
    public static final int cmSaveAll = 34;

    /** Change the current working directory (File | Change Dir). */
    public static final int cmChDir = 35;

    /** Suspend the application and shell to the operating system (File | DOS Shell). */
    public static final int cmDosShell = 36;

    /** Close all open documents (File | Close All). */
    public static final int cmCloseAll = 37;

    // -------------------------------------------------------------------------
    // Standard broadcast messages
    // -------------------------------------------------------------------------

    /**
     * Broadcast sent by {@code setState()} to the view's owner whenever {@code sfFocused} is set,
     * so that peer views (e.g. labels) can update their highlighted state.
     */
    public static final int cmReceivedFocus = 50;

    /**
     * Broadcast sent by {@code setState()} to the view's owner whenever {@code sfFocused} is cleared,
     * so that peer views can remove their highlighted state.
     */
    public static final int cmReleasedFocus = 51;

    /**
     * Broadcast sent by the application's {@code idle()} method whenever it detects a change in
     * the set of currently enabled commands, allowing views to enable or disable themselves.
     */
    public static final int cmCommandSetChanged = 52;

    /**
     * Broadcast sent by a scroll bar to its owner whenever the scroll bar's value changes,
     * so the linked view can scroll its content accordingly.
     */
    public static final int cmScrollBarChanged = 53;

    /**
     * Broadcast sent by a scroll bar to its owner when the user clicks any part of the scroll bar,
     * allowing the owner to update its display interactively.
     */
    public static final int cmScrollBarClicked = 54;

    /**
     * Causes a window to select (bring to front) itself if the event's {@code infoInt} field
     * matches the window's {@code number} field; used to implement numbered window selection.
     */
    public static final int cmSelectWindowNum = 55;

    /**
     * Broadcast sent by a list viewer to its owner whenever the user selects an item in the list,
     * allowing the owner to react to the new selection.
     */
    public static final int cmListItemSelected = 56;

    /** Broadcast sent when the screen dimensions or mode have changed and views should redraw themselves. */
    public static final int cmScreenChanged = 57;

    /** Broadcast sent when a timer expires, allowing views to perform periodic updates. */
    public static final int cmTimerExpired = 58;

    // -------------------------------------------------------------------------
    // Button internal commands
    // -------------------------------------------------------------------------

    /**
     * Broadcast sent by a button to its owner when the button is pressed, causing any linked
     * history object to record the current contents of the associated input line.
     */
    public static final int cmRecordHistory = 60;

    /**
     * Broadcast sent by a default-capable button when it gains focus, asking its owner to
     * designate it as the current default button.
     */
    public static final int cmGrabDefault = 61;

    /**
     * Broadcast sent by a default-capable button when it loses focus, asking its owner to
     * remove its default-button designation.
     */
    public static final int cmReleaseDefault = 62;

    // -------------------------------------------------------------------------
    // Color dialog internal commands
    // -------------------------------------------------------------------------

    /** Broadcast sent when the user selects a new foreground color in the color editor. */
    public static final int cmColorForegroundChanged = 71;

    /** Broadcast sent when the user selects a new background color in the color editor. */
    public static final int cmColorBackgroundChanged = 72;

    /** Broadcast sent to apply the current color selection to the target palette entry. */
    public static final int cmColorSet = 73;

    /** Broadcast sent when the user navigates to a new color item in the color group list. */
    public static final int cmNewColorItem = 74;

    /** Broadcast sent when the selected palette index changes in the color item list. */
    public static final int cmNewColorIndex = 75;

    /** Broadcast sent to persist the current palette index back to the color item list. */
    public static final int cmSaveColorIndex = 76;

    // -------------------------------------------------------------------------
    // Editor commands
    // -------------------------------------------------------------------------

    /** Open the Find (text search) dialog. */
    public static final int cmFind = 82;

    /** Open the Replace (search and replace) dialog. */
    public static final int cmReplace = 83;

    /** Repeat the most recent search operation. */
    public static final int cmSearchAgain = 84;

    // -------------------------------------------------------------------------
    // Outline viewer messages
    // -------------------------------------------------------------------------

    /** Broadcast sent by an outline viewer when the user selects an item in the outline tree. */
    public static final int cmOutlineItemSelected = 301;

    // -------------------------------------------------------------------------
    // Editor navigation / edit commands
    // -------------------------------------------------------------------------

    /** Move the cursor one character to the left. */
    public static final int cmCharLeft = 500;

    /** Move the cursor one character to the right. */
    public static final int cmCharRight = 501;

    /** Move the cursor one word to the left. */
    public static final int cmWordLeft = 502;

    /** Move the cursor one word to the right. */
    public static final int cmWordRight = 503;

    /** Move the cursor to the start of the current line. */
    public static final int cmLineStart = 504;

    /** Move the cursor to the end of the current line. */
    public static final int cmLineEnd = 505;

    /** Move the cursor up one line. */
    public static final int cmLineUp = 506;

    /** Move the cursor down one line. */
    public static final int cmLineDown = 507;

    /** Scroll the view up one page and move the cursor accordingly. */
    public static final int cmPageUp = 508;

    /** Scroll the view down one page and move the cursor accordingly. */
    public static final int cmPageDown = 509;

    /** Move the cursor to the very beginning of the text. */
    public static final int cmTextStart = 510;

    /** Move the cursor to the very end of the text. */
    public static final int cmTextEnd = 511;

    /** Insert a new line at the current cursor position. */
    public static final int cmNewLine = 512;

    /** Delete the character immediately before the cursor (Backspace). */
    public static final int cmBackSpace = 513;

    /** Delete the character at the cursor position (Delete). */
    public static final int cmDelChar = 514;

    /** Delete from the cursor to the end of the current word. */
    public static final int cmDelWord = 515;

    /** Delete from the cursor back to the start of the current line. */
    public static final int cmDelStart = 516;

    /** Delete from the cursor to the end of the current line. */
    public static final int cmDelEnd = 517;

    /** Delete the entire current line. */
    public static final int cmDelLine = 518;

    /** Toggle between insert and overwrite mode. */
    public static final int cmInsMode = 519;

    /** Begin (or anchor) a keyboard selection at the current cursor position. */
    public static final int cmStartSelect = 520;

    /** Collapse/hide the current selection without deleting it. */
    public static final int cmHideSelect = 521;

    /** Toggle automatic indentation mode. */
    public static final int cmIndentMode = 522;

    /** Request that the editor window update its title bar (e.g. to reflect modified state). */
    public static final int cmUpdateTitle = 523;

    /** Select all text in the editor. */
    public static final int cmSelectAll = 524;

    /** Delete from the cursor back to the start of the previous word. */
    public static final int cmDelWordLeft = 525;

    /** Change the character encoding of the document. */
    public static final int cmEncoding = 526;

    // -------------------------------------------------------------------------
    // Standard file dialog / file list internal commands
    // -------------------------------------------------------------------------

    /**
     * Broadcast sent by a file list box to its owner ({@code TFileDialog}) when the focused
     * file entry changes, so that {@code TFileInputLine} and {@code TFileInfoPane} can refresh.
     */
    public static final int cmFileFocused = 102;

    /** Broadcast sent by a file list box when the user double-clicks a file entry. */
    public static final int cmFileDoubleClicked = 103;

    /** Returned by {@code TFileDialog} when the user presses the Open button. */
    public static final int cmFileOpen = 1001;

    /** Returned by {@code TFileDialog} when the user presses the Replace button. */
    public static final int cmFileReplace = 1002;

    /** Returned by {@code TFileDialog} when the user presses the Clear button. */
    public static final int cmFileClear = 1003;

    /** Sent internally to initialise a file dialog after it has been constructed or restored from a stream. */
    public static final int cmFileInit = 1004;

    /** Sent internally when the user confirms a directory change in the change-directory dialog. */
    public static final int cmChangeDir = 1005;

    /** Revert the current document to the last saved version on disk. */
    public static final int cmRevert = 1006;
}
