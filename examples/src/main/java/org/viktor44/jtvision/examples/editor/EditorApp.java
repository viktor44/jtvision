/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.editor;

import static org.viktor44.jtvision.core.CommandCodes.cmCascade;
import static org.viktor44.jtvision.core.CommandCodes.cmChangeDir;
import static org.viktor44.jtvision.core.CommandCodes.cmClear;
import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmCopy;
import static org.viktor44.jtvision.core.CommandCodes.cmCut;
import static org.viktor44.jtvision.core.CommandCodes.cmDosShell;
import static org.viktor44.jtvision.core.CommandCodes.cmFind;
import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.CommandCodes.cmNew;
import static org.viktor44.jtvision.core.CommandCodes.cmNext;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmOpen;
import static org.viktor44.jtvision.core.CommandCodes.cmPaste;
import static org.viktor44.jtvision.core.CommandCodes.cmPrev;
import static org.viktor44.jtvision.core.CommandCodes.cmQuit;
import static org.viktor44.jtvision.core.CommandCodes.cmReplace;
import static org.viktor44.jtvision.core.CommandCodes.cmResize;
import static org.viktor44.jtvision.core.CommandCodes.cmSave;
import static org.viktor44.jtvision.core.CommandCodes.cmSaveAs;
import static org.viktor44.jtvision.core.CommandCodes.cmSearchAgain;
import static org.viktor44.jtvision.core.CommandCodes.cmTile;
import static org.viktor44.jtvision.core.CommandCodes.cmUndo;
import static org.viktor44.jtvision.core.CommandCodes.cmZoom;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.mfError;
import static org.viktor44.jtvision.core.ViewFlags.mfOKButton;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.viktor44.jtvision.core.CommandCodes;
import org.viktor44.jtvision.core.JtvApplication;
import org.viktor44.jtvision.core.JtvCommandSet;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvKeyStroke;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.core.ViewFlags;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvFileDialog;
import org.viktor44.jtvision.dialogs.JtvStaticText;
import org.viktor44.jtvision.editors.JtvEditWindow;
import org.viktor44.jtvision.menus.JtvMenu;
import org.viktor44.jtvision.menus.JtvMenuBar;
import org.viktor44.jtvision.menus.JtvMenuItem;
import org.viktor44.jtvision.menus.JtvStatusDef;
import org.viktor44.jtvision.menus.JtvStatusItem;
import org.viktor44.jtvision.menus.JtvStatusLine;
import org.viktor44.jtvision.menus.JtvSubMenu;
import org.viktor44.jtvision.util.MessageBox;
import org.viktor44.jtvision.views.JtvView;

/**
 * MDI editor application.
 */
public class EditorApp extends JtvApplication {

    private static final int cmAbout = 5000;

    public EditorApp(String[] files) {
        disableEditorCommandsUntilEditorActive();
        for (String f : files) {
            openEditor(f);
        }
        cascade();
    }

    private void disableEditorCommandsUntilEditorActive() {
        JtvCommandSet ts = new JtvCommandSet();
        ts.enableCmd(cmSave);
        ts.enableCmd(cmSaveAs);
        ts.enableCmd(cmCut);
        ts.enableCmd(cmCopy);
        ts.enableCmd(cmPaste);
        ts.enableCmd(cmClear);
        ts.enableCmd(cmUndo);
        ts.enableCmd(cmFind);
        ts.enableCmd(cmReplace);
        ts.enableCmd(cmSearchAgain);
        disableCommands(ts);
    }

    private void openEditor(String fileName) {
        JtvRect r = getDesktop().getExtent();
        try {
            insertWindow(new JtvEditWindow(r, fileName != null ? fileName : "", 0));
        }
        catch (RuntimeException e) {
            MessageBox.messageBox("Cannot open " + fileName + ": " + e.getMessage(),
                mfError | mfOKButton);
        }
    }

    private void fileOpen() {
        JtvFileDialog d = new JtvFileDialog(
            "*.*", "Open file", "~N~ame", ViewFlags.fdOpenButton, 100);
        if (validView(d) != null) {
            if (getDesktop().execView(d) != CommandCodes.cmCancel) {
                StringBuilder name = new StringBuilder();
                d.getFileName(name);
                openEditor(name.toString());
            }
            destroy(d);
        }
    }

    private void fileNew() {
        openEditor("");
    }

    private void changeDir() {
        JtvView d = validView(new org.viktor44.jtvision.dialogs.JtvChangeDirDialog(
            org.viktor44.jtvision.core.ViewFlags.cdNormal, 0));
        if (d != null) {
        	getDesktop().execView(d);
            destroy(d);
        }
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmOpen:
                    fileOpen();
                    break;
                case cmNew:
                    fileNew();
                    break;
                case org.viktor44.jtvision.core.CommandCodes.cmChangeDir:
                    changeDir();
                    break;
                case cmDosShell:
                    MessageBox.messageBox("DOS shell is not implemented in this port.",
                        org.viktor44.jtvision.core.ViewFlags.mfInformation | mfOKButton);
                    break;
                case cmAbout:
                    aboutBox();
                    break;
                default:
                    return;
            }
            clearEvent(event);
        }
    }

    private void aboutBox() {
        JtvDialog d = new JtvDialog(new JtvRect(0, 0, 40, 10), "About");
        d.insert(new JtvStaticText(new JtvRect(3, 2, 37, 6),
            "\003JT Vision Demo\n\n" +
            "\003Multi-window text editor"));
        d.insert(new JtvButton(new JtvRect(14, 7, 26, 9), " OK", cmOK, bfDefault));
        d.setOptions(d.getOptions() | ofCentered);
        executeDialog(d, null);
    }

    @Override
    protected JtvMenuBar initMenuBar(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getA().getY(), r.getB().getX(), r.getA().getY() + 1);
        JtvMenuItem systemMenu = new JtvSubMenu("~\u2261~")
        		.addItem(new JtvMenuItem("~A~bout...", cmAbout));

        JtvMenuItem fileMenu = new JtvSubMenu("~F~ile")
                .addItem(new JtvMenuItem("~O~pen...", cmOpen, JtvKeyStroke.of(KeyEvent.VK_F3), 0, "F3"))
                .addItem(new JtvMenuItem("~N~ew", cmNew, JtvKeyStroke.of(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), 0, "Ctrl+N"))
                .addItem(new JtvMenuItem("~S~ave", cmSave, JtvKeyStroke.of(KeyEvent.VK_F2), 0, "F2"))
                .addItem(new JtvMenuItem("S~a~ve as...", cmSaveAs))
                .addSeparator()
                .addItem(new JtvMenuItem("~C~hange dir...", cmChangeDir))
                .addItem(new JtvMenuItem("~D~OS shell", cmDosShell))
                .addSeparator()
                .addItem(new JtvMenuItem("E~x~it", cmQuit, JtvKeyStroke.of(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), 0, "Ctrl+Q"));

        JtvMenuItem editMenu = new JtvSubMenu("~E~dit")
                .addItem(new JtvMenuItem("~U~ndo", cmUndo, JtvKeyStroke.of(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK), 0, "Ctrl+U"))
                .addSeparator()
                .addItem(new JtvMenuItem("Cu~t~", cmCut, JtvKeyStroke.of(KeyEvent.VK_DELETE, InputEvent.SHIFT_DOWN_MASK), 0, "Shift+Del"))
                .addItem(new JtvMenuItem("~C~opy", cmCopy, JtvKeyStroke.of(KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK), 0, "Ctrl+Ins"))
                .addItem(new JtvMenuItem("~P~aste", cmPaste, JtvKeyStroke.of(KeyEvent.VK_INSERT, InputEvent.SHIFT_DOWN_MASK), 0, "Shift+Ins"))
                .addSeparator()
                .addItem(new JtvMenuItem("~C~lear", cmClear, JtvKeyStroke.of(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK), 0, "Ctrl+Del"));

        JtvMenuItem searchMenu = new JtvSubMenu("~S~earch")
                .addItem(new JtvMenuItem("~F~ind...", cmFind))
                .addItem(new JtvMenuItem("~R~eplace...", cmReplace))
                .addItem(new JtvMenuItem("~S~earch again", cmSearchAgain));

        JtvMenuItem windowMenu = new JtvSubMenu("~W~indow")
                .addItem(new JtvMenuItem("~S~ize/move", cmResize, JtvKeyStroke.of(KeyEvent.VK_F5, InputEvent.CTRL_DOWN_MASK), 0, "Ctrl+F5"))
                .addItem(new JtvMenuItem("~Z~oom", cmZoom, JtvKeyStroke.of(KeyEvent.VK_F5), 0, "F5"))
                .addItem(new JtvMenuItem("~T~ile", cmTile))
                .addItem(new JtvMenuItem("C~a~scade", cmCascade))
                .addItem(new JtvMenuItem("~N~ext", cmNext, JtvKeyStroke.of(KeyEvent.VK_F6), 0, "F6"))
                .addItem(new JtvMenuItem("~P~revious", cmPrev, JtvKeyStroke.of(KeyEvent.VK_F6, InputEvent.SHIFT_DOWN_MASK), 0, "Shift+F6"))
                .addItem(new JtvMenuItem("~C~lose", cmClose, JtvKeyStroke.of(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), 0, "Ctrl+W"));

        return new JtvMenuBar(r)
                .addItem(systemMenu)
                .addItem(fileMenu)
                .addItem(editMenu)
                .addItem(searchMenu)
                .addItem(windowMenu);
    }

    @Override
    protected JtvStatusLine initStatusLine(JtvRect r) {
        return new JtvStatusLine(
        		new JtvRect(r.getA().getX(), r.getB().getY() - 1, r.getB().getX(), r.getB().getY()), 
        		new JtvStatusDef()
			            .addItem(new JtvStatusItem("~F2~ Save", JtvKeyStroke.of(KeyEvent.VK_F2), cmSave))
			            .addItem(new JtvStatusItem("~F3~ Open", JtvKeyStroke.of(KeyEvent.VK_F3), cmOpen))
			            .addItem(new JtvStatusItem("~Ctrl+Q~ Exit", JtvKeyStroke.of(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), cmQuit))
			            .addItem(new JtvStatusItem("~Ctrl+W~ Close", JtvKeyStroke.of(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), cmClose))
			            .addItem(new JtvStatusItem("~F5~ Zoom", JtvKeyStroke.of(KeyEvent.VK_F5), cmZoom))
			            .addItem(new JtvStatusItem("~F6~ Next", JtvKeyStroke.of(KeyEvent.VK_F6), cmNext))
			            .addItem(new JtvStatusItem("~F10~ Menu", JtvKeyStroke.of(KeyEvent.VK_F10), cmMenu))
			            .addItem(new JtvStatusItem(null, JtvKeyStroke.of(KeyEvent.VK_DELETE, InputEvent.SHIFT_DOWN_MASK), cmCut))
			            .addItem(new JtvStatusItem(null, JtvKeyStroke.of(KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK), cmCopy))
			            .addItem(new JtvStatusItem(null, JtvKeyStroke.of(KeyEvent.VK_INSERT, InputEvent.SHIFT_DOWN_MASK), cmPaste))
			            .addItem(new JtvStatusItem(null, JtvKeyStroke.of(KeyEvent.VK_F5, InputEvent.CTRL_DOWN_MASK), cmResize))
        );
    }

    public static void main(String[] args) {
        new EditorApp(args).run();
    }
}
