/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

/**
 * View state, option, grow mode, drag mode, and other flag constants.
 */
public final class ViewFlags {

    private ViewFlags() {}

    // -------------------------------------------------------------------------
    // JtvView State masks (sfXxx)
    // -------------------------------------------------------------------------

    /** The view is visible on its owner. Set by default; {@code show()} and {@code hide()} modify it. */
    public static final int sfVisible = 0x001;

    /** The view's cursor is visible. {@code showCursor()} and {@code hideCursor()} modify it. */
    public static final int sfCursorVis = 0x002;

    /** The cursor is a solid block insert cursor; when clear the cursor is the default underline. */
    public static final int sfCursorIns = 0x004;

    /** The view has a shadow displayed beneath it. */
    public static final int sfShadow = 0x008;

    /** The view is the active window, or a subview within that active window. */
    public static final int sfActive = 0x010;

    /** The view is the currently selected subview within its owner group. */
    public static final int sfSelected = 0x020;

    /** The view has the input focus — it is selected and on the focused chain from the Application object. */
    public static final int sfFocused = 0x040;

    /** The view is currently being dragged by the user. */
    public static final int sfDragging = 0x080;

    /** The view is disabled and ignores all events sent to it. */
    public static final int sfDisabled = 0x100;

    /** The view is modal — it is the root of the active event tree until {@code endModal()} is called. */
    public static final int sfModal = 0x200;

    /** The view is the default button in its group. */
    public static final int sfDefault = 0x400;

    /** The view is owned (directly or indirectly) by the Application object and therefore possibly visible on screen. */
    public static final int sfExposed = 0x800;

    // -------------------------------------------------------------------------
    // JtvView Option masks (ofXxx)
    // -------------------------------------------------------------------------

    /** The view can be selected, for example by a mouse click. */
    public static final int ofSelectable = 0x001;

    /** When selected, the view moves in front of all peer views (top-select behaviour). */
    public static final int ofTopSelect = 0x002;

    /** A mouse click that selects the view is also delivered to the view as a normal click event. */
    public static final int ofFirstClick = 0x004;

    /** A frame is drawn around the view. */
    public static final int ofFramed = 0x008;

    /** The view receives focused keyboard events before they are sent to the focused subview (pre-process phase). */
    public static final int ofPreProcess = 0x010;

    /** The view receives focused keyboard events after the focused subview if that subview did not consume them (post-process phase). */
    public static final int ofPostProcess = 0x020;

    /** For group objects: allocate a cache buffer so the group can be redrawn without re-drawing every child. */
    public static final int ofBuffered = 0x040;

    /** The desktop may tile or cascade this view; used only with window objects. */
    public static final int ofTileable = 0x080;

    /** The view is centered on the X-axis of its owner when drawn. */
    public static final int ofCenterX = 0x100;

    /** The view is centered on the Y-axis of its owner when drawn. */
    public static final int ofCenterY = 0x200;

    /** The view is centered both horizontally and vertically within its owner ({@code ofCenterX | ofCenterY}). */
    public static final int ofCentered = 0x300;

    /** {@code valid()} is called on the view before it loses focus, allowing it to reject the focus change. */
    public static final int ofValidate = 0x400;

    // -------------------------------------------------------------------------
    // JtvView GrowMode masks (gfXxx)
    // -------------------------------------------------------------------------

    /** The left edge of the view maintains a constant distance from the left edge of its owner as the owner resizes. */
    public static final int gfGrowLoX = 0x01;

    /** The top edge of the view maintains a constant distance from the top edge of its owner as the owner resizes. */
    public static final int gfGrowLoY = 0x02;

    /** The right edge of the view maintains a constant distance from the right edge of its owner as the owner resizes. */
    public static final int gfGrowHiX = 0x04;

    /** The bottom edge of the view maintains a constant distance from the bottom edge of its owner as the owner resizes. */
    public static final int gfGrowHiY = 0x08;

    /** All four edges track their respective owner edges — the view moves with the lower-right corner of its owner ({@code gfGrowLoX | gfGrowLoY | gfGrowHiX | gfGrowHiY}). */
    public static final int gfGrowAll = 0x0F;

    /** When used with window objects, the view changes size proportionally relative to the owner's size change. */
    public static final int gfGrowRel = 0x10;

    /** The view has a fixed size and does not resize when its owner resizes. */
    public static final int gfFixed = 0x20;

    // -------------------------------------------------------------------------
    // JtvView DragMode masks (dmXxx)
    // -------------------------------------------------------------------------

    /** Dragging moves the view to a new position. */
    public static final int dmDragMove = 0x01;

    /** Dragging resizes the view by moving its lower-right corner. */
    public static final int dmDragGrow = 0x02;

    /** Dragging resizes the view by moving its left edge. */
    public static final int dmDragGrowLeft = 0x04;

    /** The view's left edge cannot be dragged outside the limits rectangle. */
    public static final int dmLimitLoX = 0x10;

    /** The view's top edge cannot be dragged outside the limits rectangle. */
    public static final int dmLimitLoY = 0x20;

    /** The view's right edge cannot be dragged outside the limits rectangle. */
    public static final int dmLimitHiX = 0x40;

    /** The view's bottom edge cannot be dragged outside the limits rectangle. */
    public static final int dmLimitHiY = 0x80;

    /** No part of the view can be dragged outside the limits rectangle ({@code dmLimitLoX | dmLimitLoY | dmLimitHiX | dmLimitHiY}). */
    public static final int dmLimitAll = dmLimitLoX | dmLimitLoY | dmLimitHiX | dmLimitHiY;

    // -------------------------------------------------------------------------
    // Help context codes (hcXxx)
    // -------------------------------------------------------------------------

    /** No help context is associated with the view. */
    public static final int hcNoContext = 0;

    /** Help context returned by {@code getHelpCtx()} while the view is being dragged. */
    public static final int hcDragging = 1;

    // -------------------------------------------------------------------------
    // JtvScrollBar part codes (sbXxx)
    // -------------------------------------------------------------------------

    /** The left arrow of a horizontal scroll bar. */
    public static final int sbLeftArrow = 0;

    /** The right arrow of a horizontal scroll bar. */
    public static final int sbRightArrow = 1;

    /** The left paging area (between the left arrow and the indicator) of a horizontal scroll bar. */
    public static final int sbPageLeft = 2;

    /** The right paging area (between the indicator and the right arrow) of a horizontal scroll bar. */
    public static final int sbPageRight = 3;

    /** The up arrow of a vertical scroll bar. */
    public static final int sbUpArrow = 4;

    /** The down arrow of a vertical scroll bar. */
    public static final int sbDownArrow = 5;

    /** The upper paging area (between the up arrow and the indicator) of a vertical scroll bar. */
    public static final int sbPageUp = 6;

    /** The lower paging area (between the indicator and the down arrow) of a vertical scroll bar. */
    public static final int sbPageDown = 7;

    /** The position indicator (thumb) of a scroll bar. */
    public static final int sbIndicator = 8;

    // -------------------------------------------------------------------------
    // JtvScrollBar options for JtvWindow.standardScrollBar (sbXxx)
    // -------------------------------------------------------------------------

    /** Create a horizontal scroll bar. */
    public static final int sbHorizontal = 0x000;

    /** Create a vertical scroll bar. */
    public static final int sbVertical = 0x001;

    /** The scroll bar responds to keyboard arrow and page keys in addition to mouse input. */
    public static final int sbHandleKeyboard = 0x002;

    // -------------------------------------------------------------------------
    // JtvWindow flags masks (wfXxx)
    // -------------------------------------------------------------------------

    /** The user can move the window by dragging its title bar. */
    public static final int wfMove = 0x01;

    /** The user can resize the window by dragging its lower-right corner. */
    public static final int wfGrow = 0x02;

    /** The window frame contains a close icon that dismisses the window on click. */
    public static final int wfClose = 0x04;

    /** The window frame contains a zoom icon that toggles the window between normal and maximised size. */
    public static final int wfZoom = 0x08;

    // -------------------------------------------------------------------------
    // JtvView inhibit flags (noXxx) — suppress default Application subviews
    // -------------------------------------------------------------------------

    /** Suppress the creation of the default menu bar. */
    public static final int noMenuBar = 0x0001;

    /** Suppress the creation of the default desktop. */
    public static final int noDeskTop = 0x0002;

    /** Suppress the creation of the default status line. */
    public static final int noStatusLine = 0x0004;

    /** Suppress the creation of the default background view. */
    public static final int noBackground = 0x0008;

    /** Suppress the creation of the default window frame. */
    public static final int noFrame = 0x0010;

    /** Suppress the creation of the default viewer. */
    public static final int noViewer = 0x0020;

    /** Suppress the creation of the default history list. */
    public static final int noHistory = 0x0040;

    // -------------------------------------------------------------------------
    // JtvWindow number constants (wnXxx)
    // -------------------------------------------------------------------------

    /** Passed to the TWindow constructor to indicate that no window number should be shown in the title bar. */
    public static final int wnNoNumber = 0;

    // -------------------------------------------------------------------------
    // JtvWindow palette entries (wpXxx)
    // -------------------------------------------------------------------------

    /** Window drawn with white text on a blue background. */
    public static final int wpBlueWindow = 0;

    /** Window drawn with blue text on a cyan background. */
    public static final int wpCyanWindow = 1;

    /** Window drawn with black text on a gray background. */
    public static final int wpGrayWindow = 2;

    // -------------------------------------------------------------------------
    // Button flags (bfXxx)
    // -------------------------------------------------------------------------

    /** Normal non-default button; no special behaviour. */
    public static final int bfNormal = 0x00;

    /** The button is the default button in its group and is activated by the Enter key. */
    public static final int bfDefault = 0x01;

    /** The button label is left-aligned; when clear the label is centred. */
    public static final int bfLeftJust = 0x02;

    /** When pressed, the button sends a broadcast message to its owner rather than a command event. */
    public static final int bfBroadcast = 0x04;

    /** The button takes input focus when clicked with the mouse. */
    public static final int bfGrabFocus = 0x08;

    // -------------------------------------------------------------------------
    // Application palette entries (apXxx)
    // -------------------------------------------------------------------------

    /** Use the full-colour palette for colour screen display. */
    public static final int apColor = 0;

    /** Use the black-and-white palette for LCD or monochrome-capable colour screens. */
    public static final int apBlackWhite = 1;

    /** Use the monochrome palette for true monochrome screens. */
    public static final int apMonochrome = 2;

    // -------------------------------------------------------------------------
    // Dialog palette entries (dpXxx)
    // -------------------------------------------------------------------------

    /** Dialog box drawn with a blue background. */
    public static final int dpBlueDialog = 0;

    /** Dialog box drawn with a cyan background. */
    public static final int dpCyanDialog = 1;

    /** Dialog box drawn with a gray background. */
    public static final int dpGrayDialog = 2;

    // -------------------------------------------------------------------------
    // Message box type flags (mfXxx)
    // -------------------------------------------------------------------------

    /** Display a Warning message box (default type). */
    public static final int mfWarning = 0x0000;

    /** Display an Error message box. */
    public static final int mfError = 0x0001;

    /** Display an Information message box. */
    public static final int mfInformation = 0x0002;

    /** Display a Confirmation message box. */
    public static final int mfConfirmation = 0x0003;

    // -------------------------------------------------------------------------
    // Message box button flags (mfXxx)
    // -------------------------------------------------------------------------

    /** Include a Yes button in the message box. */
    public static final int mfYesButton = 0x0100;

    /** Include a No button in the message box. */
    public static final int mfNoButton = 0x0200;

    /** Include an OK button in the message box. */
    public static final int mfOKButton = 0x0400;

    /** Include a Cancel button in the message box. */
    public static final int mfCancelButton = 0x0800;

    /** Convenience combination: Yes, No, and Cancel buttons ({@code mfYesButton | mfNoButton | mfCancelButton}). */
    public static final int mfYesNoCancel = mfYesButton | mfNoButton | mfCancelButton;

    /** Convenience combination: OK and Cancel buttons ({@code mfOKButton | mfCancelButton}). */
    public static final int mfOKCancel = mfOKButton | mfCancelButton;

    // -------------------------------------------------------------------------
    // File dialog options (fdXxx)
    // -------------------------------------------------------------------------

    /** Include an OK button in the file dialog. */
    public static final int fdOKButton = 0x0001;

    /** Include an Open button in the file dialog. */
    public static final int fdOpenButton = 0x0002;

    /** Include a Replace button in the file dialog. */
    public static final int fdReplaceButton = 0x0004;

    /** Include a Clear button in the file dialog. */
    public static final int fdClearButton = 0x0008;

    /** Include a Help button in the file dialog. */
    public static final int fdHelpButton = 0x0010;

    /** Do not load the current directory listing when the dialog is initialised (e.g. when restoring from a stream). */
    public static final int fdNoLoadDir = 0x0100;

    // -------------------------------------------------------------------------
    // Change-directory dialog options (cdXxx)
    // -------------------------------------------------------------------------

    /** Create the change-directory dialog normally, including loading the current directory listing. */
    public static final int cdNormal = 0x0000;

    /** Initialise the dialog without loading the directory listing; used when restoring the dialog from a stream. */
    public static final int cdNoLoadDir = 0x0001;

    /** Include a Help button in the change-directory dialog. */
    public static final int cdHelpButton = 0x0002;
}
