/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmDefault;
import static org.viktor44.jtvision.core.CommandCodes.cmNo;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmYes;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.ViewFlags.dpBlueDialog;
import static org.viktor44.jtvision.core.ViewFlags.dpCyanDialog;
import static org.viktor44.jtvision.core.ViewFlags.dpGrayDialog;
import static org.viktor44.jtvision.core.ViewFlags.sfModal;
import static org.viktor44.jtvision.core.ViewFlags.wfClose;
import static org.viktor44.jtvision.core.ViewFlags.wfMove;
import static org.viktor44.jtvision.core.ViewFlags.wnNoNumber;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvWindow;

/**
 * A descendant of {@link JtvWindow} used to create modal and non-modal dialog boxes.
 * <p>
 * Dialog boxes are windows that display a temporary interface for gathering user
 * input. TDialog adds keyboard handling for Tab/Shift+Tab focus cycling, Escape
 * (generates {@code cmCancel}), and Enter (broadcasts {@code cmDefault}).
 * When modal, the dialog ends its modal state on {@code cmOK}, {@code cmCancel},
 * {@code cmYes}, or {@code cmNo} commands.
 * <p>
 * Three palette styles are available: gray (default), blue, and cyan, selected via
 * the {@code palette} field inherited from {@code TWindow}.
 * 
 * <h3>Colour palette</h3>
 * 
 * Each of the three 32-entry palettes ({@link #cpGrayDialog}, {@link #cpBlueDialog},
 * {@link #cpCyanDialog}) maps dialog positions 1–32 to the application palette:
 * <ol>
 *   <li>Passive frame.</li>
 *   <li>Active frame / title.</li>
 *   <li>Frame icons.</li>
 *   <li>Scroll bar page area.</li>
 *   <li>Scroll bar arrows.</li>
 *   <li>Static text.</li>
 *   <li>Label normal text.</li>
 *   <li>Label highlighted text.</li>
 *   <li>Label shortcut character.</li>
 *   <li>Button normal text.</li>
 *   <li>Button default text.</li>
 *   <li>Button selected text.</li>
 *   <li>Button disabled text.</li>
 *   <li>Button shortcut character.</li>
 *   <li>Button shadow.</li>
 *   <li>Cluster item normal text.</li>
 *   <li>Cluster item selected text.</li>
 *   <li>Cluster item shortcut character.</li>
 *   <li>Input line passive text.</li>
 *   <li>Input line selected text.</li>
 *   <li>Input line scroll arrow.</li>
 *   <li>History icon normal colour.</li>
 *   <li>History icon shortcut colour.</li>
 *   <li>Reserved.</li>
 *   <li>Reserved.</li>
 *   <li>Reserved.</li>
 *   <li>Reserved.</li>
 *   <li>Reserved.</li>
 *   <li>Reserved.</li>
 *   <li>File info pane text.</li>
 *   <li>Cluster disabled item text.</li>
 *   <li>Reserved.</li>
 * </ol>
 */
public class JtvDialog extends JtvWindow {

    /**
     * Gray dialog 32-entry colour palette, mapping positions 1–32 to application
     * palette entries {@code 0x20}–{@code 0x3F}:
     * <ol>
     *   <li>{@code 0x20} — passive frame.</li>
     *   <li>{@code 0x21} — active frame / title.</li>
     *   <li>{@code 0x22} — frame icons.</li>
     *   <li>{@code 0x23} — scroll bar page area.</li>
     *   <li>{@code 0x24} — scroll bar arrows.</li>
     *   <li>{@code 0x25} — static text.</li>
     *   <li>{@code 0x26} — label normal text.</li>
     *   <li>{@code 0x27} — label highlighted text.</li>
     *   <li>{@code 0x28} — label shortcut character.</li>
     *   <li>{@code 0x29} — button normal text.</li>
     *   <li>{@code 0x2A} — button default text.</li>
     *   <li>{@code 0x2B} — button selected text.</li>
     *   <li>{@code 0x2C} — button disabled text.</li>
     *   <li>{@code 0x2D} — button shortcut character.</li>
     *   <li>{@code 0x2E} — button shadow.</li>
     *   <li>{@code 0x2F} — cluster item normal text.</li>
     *   <li>{@code 0x30} — cluster item selected text.</li>
     *   <li>{@code 0x31} — cluster item shortcut character.</li>
     *   <li>{@code 0x32} — input line passive text.</li>
     *   <li>{@code 0x33} — input line selected text.</li>
     *   <li>{@code 0x34} — input line scroll arrow.</li>
     *   <li>{@code 0x35} — history icon normal colour.</li>
     *   <li>{@code 0x36} — history icon shortcut colour.</li>
     *   <li>{@code 0x37} — reserved.</li>
     *   <li>{@code 0x38} — reserved.</li>
     *   <li>{@code 0x39} — reserved.</li>
     *   <li>{@code 0x3A} — reserved.</li>
     *   <li>{@code 0x3B} — reserved.</li>
     *   <li>{@code 0x3C} — reserved.</li>
     *   <li>{@code 0x3D} — file info pane text.</li>
     *   <li>{@code 0x3E} — cluster disabled item text.</li>
     *   <li>{@code 0x3F} — reserved.</li>
     * </ol>
     */
    private static final JtvPalette cpGrayDialog = new JtvPalette(
    		new int[] {
			        0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27,
			        0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, 0x2E, 0x2F,
			        0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
			        0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D, 0x3E, 0x3F
		    }
    );
    
    /**
     * Blue dialog 32-entry colour palette, mapping positions 1–32 to application
     * palette entries {@code 0x40}–{@code 0x5F}:
     * <ol>
     *   <li>{@code 0x40} — passive frame.</li>
     *   <li>{@code 0x41} — active frame / title.</li>
     *   <li>{@code 0x42} — frame icons.</li>
     *   <li>{@code 0x43} — scroll bar page area.</li>
     *   <li>{@code 0x44} — scroll bar arrows.</li>
     *   <li>{@code 0x45} — static text.</li>
     *   <li>{@code 0x46} — label normal text.</li>
     *   <li>{@code 0x47} — label highlighted text.</li>
     *   <li>{@code 0x48} — label shortcut character.</li>
     *   <li>{@code 0x49} — button normal text.</li>
     *   <li>{@code 0x4A} — button default text.</li>
     *   <li>{@code 0x4B} — button selected text.</li>
     *   <li>{@code 0x4C} — button disabled text.</li>
     *   <li>{@code 0x4D} — button shortcut character.</li>
     *   <li>{@code 0x4E} — button shadow.</li>
     *   <li>{@code 0x4F} — cluster item normal text.</li>
     *   <li>{@code 0x50} — cluster item selected text.</li>
     *   <li>{@code 0x51} — cluster item shortcut character.</li>
     *   <li>{@code 0x52} — input line passive text.</li>
     *   <li>{@code 0x53} — input line selected text.</li>
     *   <li>{@code 0x54} — input line scroll arrow.</li>
     *   <li>{@code 0x55} — history icon normal colour.</li>
     *   <li>{@code 0x56} — history icon shortcut colour.</li>
     *   <li>{@code 0x57} — reserved.</li>
     *   <li>{@code 0x58} — reserved.</li>
     *   <li>{@code 0x59} — reserved.</li>
     *   <li>{@code 0x5A} — reserved.</li>
     *   <li>{@code 0x5B} — reserved.</li>
     *   <li>{@code 0x5C} — reserved.</li>
     *   <li>{@code 0x5D} — file info pane text.</li>
     *   <li>{@code 0x5E} — cluster disabled item text.</li>
     *   <li>{@code 0x5F} — reserved.</li>
     * </ol>
     */
    private static final JtvPalette cpBlueDialog = new JtvPalette(
    		new int[] {
			        0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,
			        0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F,
			        0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57,
			        0x58, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x5E, 0x5F
		    }
    );
    
    /**
     * Cyan dialog 32-entry colour palette, mapping positions 1–32 to application
     * palette entries {@code 0x60}–{@code 0x7F}:
     * <ol>
     *   <li>{@code 0x60} — passive frame.</li>
     *   <li>{@code 0x61} — active frame / title.</li>
     *   <li>{@code 0x62} — frame icons.</li>
     *   <li>{@code 0x63} — scroll bar page area.</li>
     *   <li>{@code 0x64} — scroll bar arrows.</li>
     *   <li>{@code 0x65} — static text.</li>
     *   <li>{@code 0x66} — label normal text.</li>
     *   <li>{@code 0x67} — label highlighted text.</li>
     *   <li>{@code 0x68} — label shortcut character.</li>
     *   <li>{@code 0x69} — button normal text.</li>
     *   <li>{@code 0x6A} — button default text.</li>
     *   <li>{@code 0x6B} — button selected text.</li>
     *   <li>{@code 0x6C} — button disabled text.</li>
     *   <li>{@code 0x6D} — button shortcut character.</li>
     *   <li>{@code 0x6E} — button shadow.</li>
     *   <li>{@code 0x6F} — cluster item normal text.</li>
     *   <li>{@code 0x70} — cluster item selected text.</li>
     *   <li>{@code 0x71} — cluster item shortcut character.</li>
     *   <li>{@code 0x72} — input line passive text.</li>
     *   <li>{@code 0x73} — input line selected text.</li>
     *   <li>{@code 0x74} — input line scroll arrow.</li>
     *   <li>{@code 0x75} — history icon normal colour.</li>
     *   <li>{@code 0x76} — history icon shortcut colour.</li>
     *   <li>{@code 0x77} — reserved.</li>
     *   <li>{@code 0x78} — reserved.</li>
     *   <li>{@code 0x79} — reserved.</li>
     *   <li>{@code 0x7A} — reserved.</li>
     *   <li>{@code 0x7B} — reserved.</li>
     *   <li>{@code 0x7C} — reserved.</li>
     *   <li>{@code 0x7D} — file info pane text.</li>
     *   <li>{@code 0x7E} — cluster disabled item text.</li>
     *   <li>{@code 0x7F} — reserved.</li>
     * </ol>
     */
    private static final JtvPalette cpCyanDialog = new JtvPalette(
    		new int[] {
			        0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67,
			        0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F,
			        0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77,
			        0x78, 0x79, 0x7A, 0x7B, 0x7C, 0x7D, 0x7E, 0x7F
		    }
    );

    /**
     * Creates a dialog box with the given bounds and title.
     * <p>
     * Initialises with no window number, grow mode 0, move and close flags,
     * and the default gray palette.
     *
     * @param bounds the bounding rectangle of the dialog
     * @param aTitle the title bar string
     */
    public JtvDialog(JtvRect bounds, String aTitle) {
        super(bounds, aTitle, wnNoNumber);
        growMode = 0;
        flags = wfMove | wfClose;
        paletteIndex = dpGrayDialog;
    }

    /**
     * Returns the color palette appropriate for the current palette style
     * ({@code dpGrayDialog}, {@code dpBlueDialog}, or {@code dpCyanDialog}).
     *
     * @return the dialog's color palette
     */
    @Override
    public JtvPalette getPalette() {
        switch (paletteIndex) {
            case dpBlueDialog: 
            	return cpBlueDialog;
            case dpCyanDialog: 
            	return cpCyanDialog;
            case dpGrayDialog:
            	return cpGrayDialog;
        }
    	return cpGrayDialog;
    }

    /**
     * Handles keyboard and command events for the dialog.
     * <p>
     * Tab and Shift+Tab cycle focus among the dialog's controls. Escape
     * generates a {@code cmCancel} command; Enter broadcasts {@code cmDefault}.
     * When the dialog is modal, {@code cmOK}, {@code cmCancel}, {@code cmYes},
     * and {@code cmNo} commands call {@code endModal}.
     *
     * @param event the event to handle
     */
    @Override
    public void handleEvent(JtvEvent event) {
        if (event.getWhat() == evKeyDown) {
            switch (event.getKeyDown().getKeyStroke()) {
                case JtvKey.kbTab:
                    focusNext(false);
                    clearEvent(event);
                    return;
                case JtvKey.kbShiftTab:
                    focusNext(true);
                    clearEvent(event);
                    return;
                default:
                    break;
            }
        }

        super.handleEvent(event);
        switch (event.getWhat()) {
            case evKeyDown:
                switch (event.getKeyDown().getKeyCode()) {
                    case JtvKey.kbEsc:
                        event.setWhat(evCommand);
                        event.getMessage().setCommand(cmCancel);
                        event.getMessage().setInfoPtr(null);
                        putEvent(event);
                        clearEvent(event);
                        break;
                    case JtvKey.kbEnter:
                        event.setWhat(evBroadcast);
                        event.getMessage().setCommand(cmDefault);
                        event.getMessage().setInfoPtr(null);
                        putEvent(event);
                        clearEvent(event);
                        break;
                }
                break;
            case evCommand:
                switch (event.getMessage().getCommand()) {
                    case cmOK:
                    case cmCancel:
                    case cmYes:
                    case cmNo:
                        if ((state & sfModal) != 0) {
                            endModal(event.getMessage().getCommand());
                            clearEvent(event);
                        }
                        break;
                }
                break;
        }
    }

    /**
     * Always returns {@code true} for {@code cmCancel}; delegates to the
     * parent for all other commands.
     *
     * @param command the command to validate
     * @return {@code true} if the dialog may close for the given command
     */
    @Override
    public boolean valid(int command) {
        if (command == cmCancel)
            return true;
        return super.valid(command);
    }
}
