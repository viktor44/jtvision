/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.util;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmNo;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmYes;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;
import static org.viktor44.jtvision.core.ViewFlags.mfCancelButton;
import static org.viktor44.jtvision.core.ViewFlags.mfNoButton;
import static org.viktor44.jtvision.core.ViewFlags.mfOKButton;
import static org.viktor44.jtvision.core.ViewFlags.mfYesButton;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;

import org.viktor44.jtvision.core.JtvProgram;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvInputLine;
import org.viktor44.jtvision.dialogs.JtvStaticText;

/**
 * Static factory for JT Vision–style message boxes and input boxes.
 * <p>
 * This class corresponds to the JT Vision {@code MsgBox} unit and provides
 * four public factory methods:
 * <ul>
 *   <li>{@link #messageBox(String, int)} — a centred 40×9 notification dialog.</li>
 *   <li>{@link #messageBoxRect(JtvRect, String, int)} — the same dialog at an
 *       explicit bounding rectangle.</li>
 *   <li>{@link #inputBox(String, String, int)} — a centred 40×8 single-line
 *       input dialog.</li>
 *   <li>{@link #inputBoxRect(JtvRect, String, String, int)} — the same input
 *       dialog at an explicit bounding rectangle.</li>
 * </ul>
 *
 * <h3>Options parameter encoding</h3>
 * The {@code aOptions} parameter of {@link #messageBox} and
 * {@link #messageBoxRect} is a bitmapped word whose low byte selects the
 * dialog icon/title and whose high byte selects the buttons:
 * <pre>
 *   msb ─────────────────────────── lsb
 *   [ mfCancelButton | mfOKButton | mfNoButton | mfYesButton ][ mfConfirmation / mfInformation / mfError / mfWarning ]
 * </pre>
 * <b>Icon/title constants (bits 0–1):</b>
 * <ul>
 *   <li>{@code mfWarning      = 0x0000} — "Warning" title.</li>
 *   <li>{@code mfError        = 0x0001} — "Error" title.</li>
 *   <li>{@code mfInformation  = 0x0002} — "Information" title.</li>
 *   <li>{@code mfConfirmation = 0x0003} — "Confirm" title.</li>
 * </ul>
 * <b>Button constants (bits 8–11):</b>
 * <ul>
 *   <li>{@code mfYesButton    = 0x0100} — add a Yes button.</li>
 *   <li>{@code mfNoButton     = 0x0200} — add a No button.</li>
 *   <li>{@code mfOKButton     = 0x0400} — add an OK button.</li>
 *   <li>{@code mfCancelButton = 0x0800} — add a Cancel button.</li>
 *   <li>{@code mfYesNoCancel  = 0x0B00} — Yes + No + Cancel.</li>
 *   <li>{@code mfOKCancel     = 0x0C00} — OK + Cancel.</li>
 * </ul>
 *
 * <h3>Return values</h3>
 * {@link #messageBox} and {@link #messageBoxRect} return the command code of
 * the button the user pressed: {@code cmYes}, {@code cmNo}, {@code cmOK}, or
 * {@code cmCancel}. If no application is running, {@code cmCancel} is returned.
 * <p>
 * {@link #inputBox} and {@link #inputBoxRect} return the string entered by the
 * user, or {@code null} if the dialog was cancelled.
 *
 * @see org.viktor44.jtvision.core.ViewFlags
 * @see org.viktor44.jtvision.core.CommandCodes
 */
public final class MessageBox {

    /**
     * Prevents instantiation of this static utility class.
     */
    private MessageBox() {}

    /**
     * Window titles indexed by the icon-type nibble ({@code aOptions & 0x03}).
     * <p>
     * The four entries correspond to:
     * <ol start="0">
     *   <li>{@code mfWarning = 0x0000} — {@code "Warning"}</li>
     *   <li>{@code mfError = 0x0001} — {@code "Error"}</li>
     *   <li>{@code mfInformation = 0x0002} — {@code "Information"}</li>
     *   <li>{@code mfConfirmation = 0x0003} — {@code "Confirm"}</li>
     * </ol>
     */
    private static final String[] titles = {
        "Warning", "Error", "Information", "Confirm"
    };

    /**
     * Tilde-encoded button label strings, in button-index order.
     * <p>
     * Indices are fixed constants used by the button-building loop:
     * <ul>
     *   <li>0 — {@code "~Y~es"}   (Yes button, hot key {@code Y})</li>
     *   <li>1 — {@code "~N~o"}    (No button, hot key {@code N})</li>
     *   <li>2 — {@code "~O~K"}    (OK button, hot key {@code O})</li>
     *   <li>3 — {@code "~C~ancel"}(Cancel button, hot key {@code C})</li>
     * </ul>
     */
    private static final String[] buttonText = {
        "~Y~es", "~N~o", "~O~K", "~C~ancel"
    };

    /**
     * Command codes emitted when each button is activated, parallel to
     * {@link #buttonText}.
     * <ul>
     *   <li>0 — {@code cmYes}</li>
     *   <li>1 — {@code cmNo}</li>
     *   <li>2 — {@code cmOK}</li>
     *   <li>3 — {@code cmCancel}</li>
     * </ul>
     */
    private static final int[] buttonCommands = {
        cmYes, cmNo, cmOK, cmCancel
    };

    /**
     * Button-flag defaults parallel to {@link #buttonText}.
     * <p>
     * The first button inserted into the dialog is always overridden to
     * {@code bfDefault} by the loop in {@link #messageBoxRect}, regardless of
     * these values. The remaining entries use {@code bfNormal}.
     * <ul>
     *   <li>0 (Yes)    — {@code bfNormal}</li>
     *   <li>1 (No)     — {@code bfNormal}</li>
     *   <li>2 (OK)     — {@code bfDefault} (pre-selected default)</li>
     *   <li>3 (Cancel) — {@code bfNormal}</li>
     * </ul>
     */
    private static final int[] buttonFlags = {
        bfNormal, bfNormal, bfDefault, bfNormal
    };

    /**
     * Displays a 40-column, 9-line message dialog centred on the desktop.
     * <p>
     * Computes a centred {@link JtvRect} of size 40×9 relative to
     * {@link JtvProgram#desktop} and delegates to
     * {@link #messageBoxRect(JtvRect, String, int)}.
     * <p>
     * Example usage:
     * <pre>
     * int result = MessageBox.messageBox(
     *     "Shall I reformat your hard drive now?",
     *     mfConfirmation | mfYesButton | mfNoButton);
     * if (result == cmYes) { ... }
     * </pre>
     *
     * @param msg      the message string to display inside the dialog
     * @param options a bitmapped combination of one icon-type constant
     *                 ({@code mfWarning}, {@code mfError}, {@code mfInformation},
     *                 or {@code mfConfirmation}) OR-ed with one or more button
     *                 constants ({@code mfYesButton}, {@code mfNoButton},
     *                 {@code mfOKButton}, {@code mfCancelButton})
     * @return the command code of the button that closed the dialog
     *         ({@code cmYes}, {@code cmNo}, {@code cmOK}, or {@code cmCancel}),
     *         or {@code cmCancel} if no application is available
     */
    public static int messageBox(String msg, int options) {
        JtvRect r = new JtvRect(0, 0, 40, 9);
        JtvRect dr = JtvProgram.getDesktop().getExtent();
        r.move((dr.getB().getX() - dr.getA().getX() - r.getB().getX()) / 2, (dr.getB().getY() - dr.getA().getY() - r.getB().getY()) / 2);
        return messageBoxRect(r, msg, options);
    }

    /**
     * Displays a message dialog at an explicit bounding rectangle.
     * <p>
     * Works exactly like {@link #messageBox(String, int)} but uses the caller-
     * supplied {@code r} instead of a fixed 40×9 centred box. Use this variant
     * when the message is too long for the default size.
     * <p>
     * The dialog title is selected from {@link #titles} using bits 0–1 of
     * {@code aOptions}. Buttons are added in the order Yes → No → OK → Cancel
     * according to the button-mask in bits 8–11 of {@code aOptions}; if no
     * button flag is set, an OK button is added by default. The first button
     * inserted is always marked {@code bfDefault} (activated by Enter).
     *
     * @param r        the bounding rectangle for the dialog (in desktop
     *                 coordinates)
     * @param msg      the message string to display
     * @param aOptions bitmapped options — see {@link #messageBox(String, int)}
     *                 for the encoding
     * @return the command code of the activating button, or {@code cmCancel}
     *         if no application is available
     */
    public static int messageBoxRect(JtvRect r, String msg, int aOptions) {
        int titleIdx = aOptions & 0x03;
        String title = titles[titleIdx];

        JtvDialog dialog = new JtvDialog(r, title);
        dialog.setOptions(dialog.getOptions() | ofCentered);

        // Add text
        JtvRect textRect = new JtvRect(3, 2, r.getB().getX() - r.getA().getX() - 2, r.getB().getY() - r.getA().getY() - 3);
        JtvStaticText text = new JtvStaticText(textRect, msg);
        dialog.insert(text);

        // Add buttons
        int buttonMask = aOptions & 0xFF00;
        int buttonCount = 0;
        int[] buttons = new int[4];
        if ((buttonMask & mfYesButton) != 0) buttons[buttonCount++] = 0;
        if ((buttonMask & mfNoButton) != 0) buttons[buttonCount++] = 1;
        if ((buttonMask & mfOKButton) != 0) buttons[buttonCount++] = 2;
        if ((buttonMask & mfCancelButton) != 0) buttons[buttonCount++] = 3;

        if (buttonCount == 0) {
            // Default to OK button
            buttons[buttonCount++] = 2;
        }

        int dialogWidth = r.getB().getX() - r.getA().getX();
        int totalButtonWidth = buttonCount * 12;
        int buttonX = (dialogWidth - totalButtonWidth) / 2;
        int buttonY = r.getB().getY() - r.getA().getY() - 3;

        for (int i = 0; i < buttonCount; i++) {
            int idx = buttons[i];
            int flags = (i == 0) ? bfDefault : bfNormal;
            JtvButton btn = new JtvButton(
                new JtvRect(buttonX, buttonY, buttonX + 10, buttonY + 2),
                buttonText[idx], buttonCommands[idx], flags);
            dialog.insert(btn);
            buttonX += 12;
        }

        if (JtvProgram.getApplication() != null)
            return JtvProgram.getApplication().executeDialog(dialog, null);
        return cmCancel;
    }

    /**
     * Displays a 40-column, 8-line single-line input dialog centred on the
     * desktop.
     * <p>
     * The dialog contains a static text label ({@code label}), a
     * {@link JtvInputLine} limited to {@code maxLen} characters, an OK button,
     * and a Cancel button.
     * <p>
     * Computes a centred 40×8 bounding rectangle relative to
     * {@link JtvProgram#desktop} and delegates to
     * {@link #inputBoxRect(JtvRect, String, String, int)}.
     * <p>
     * Example usage:
     * <pre>
     * String name = MessageBox.inputBox("Rename", "New file name:", 64);
     * if (name != null) { ... }
     * </pre>
     *
     * @param title  the dialog window title
     * @param label  the descriptive text shown above the input field
     * @param maxLen the maximum number of characters the user may type
     * @return the string entered by the user, or {@code null} if the user
     *         pressed Cancel or no application is available
     */
    public static String inputBox(String title, String label, int maxLen) {
        JtvRect r = new JtvRect(0, 0, 40, 8);
        JtvRect dr = JtvProgram.getDesktop().getExtent();
        r.move((dr.getB().getX() - dr.getA().getX() - r.getB().getX()) / 2, (dr.getB().getY() - dr.getA().getY() - r.getB().getY()) / 2);
        return inputBoxRect(r, title, label, maxLen);
    }

    /**
     * Displays a single-line input dialog at an explicit bounding rectangle.
     * <p>
     * Works exactly like {@link #inputBox(String, String, int)} but uses the
     * caller-supplied {@code r} instead of a fixed 40×8 centred box. Use this
     * variant when the label or expected input is too wide for the default size.
     * <p>
     * The layout within {@code r} is:
     * <ul>
     *   <li>Row 2 — static label text.</li>
     *   <li>Row 3 — the {@link JtvInputLine} (width = dialog width − 4 columns).</li>
     *   <li>Row 5–6 — centred OK ({@code bfDefault}) and Cancel buttons.</li>
     * </ul>
     * If the user closes the dialog with OK (or any non-Cancel command), the
     * text currently in the input line is returned. Otherwise {@code null} is
     * returned.
     *
     * @param r      the bounding rectangle for the dialog (in desktop
     *               coordinates)
     * @param title  the dialog window title
     * @param label  the descriptive text shown above the input field
     * @param maxLen the maximum number of characters the user may type
     * @return the string entered by the user, or {@code null} if the dialog was
     *         cancelled or no application is available
     */
    public static String inputBoxRect(JtvRect r, String title, String label, int maxLen) {
        JtvDialog dialog = new JtvDialog(r, title);
        dialog.setOptions(dialog.getOptions() | ofCentered);

        int dialogWidth = r.getB().getX() - r.getA().getX();

        // Add label
        JtvRect labelRect = new JtvRect(2, 2, dialogWidth - 2, 3);
        dialog.insert(new JtvStaticText(labelRect, label));

        // Add input line
        JtvRect inputRect = new JtvRect(2, 3, dialogWidth - 2, 4);
        JtvInputLine inputLine = new JtvInputLine(inputRect, maxLen);
        dialog.insert(inputLine);

        // Add OK and Cancel buttons
        int btnX = (dialogWidth - 24) / 2;
        dialog.insert(new JtvButton(
            new JtvRect(btnX, 5, btnX + 10, 7), "~O~K", cmOK, bfDefault));
        dialog.insert(new JtvButton(
            new JtvRect(btnX + 12, 5, btnX + 22, 7), "Cancel", cmCancel, bfNormal));

        if (JtvProgram.getApplication() != null) {
            int result = JtvProgram.getApplication().executeDialog(dialog, null);
            if (result != cmCancel)
                return inputLine.getData();
        }
        return null;
    }
}
