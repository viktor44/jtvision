/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.tvdir;

import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmQuit;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;

import org.viktor44.jtvision.core.JtvApplication;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvStaticText;
import org.viktor44.jtvision.menus.JtvMenu;
import org.viktor44.jtvision.menus.JtvMenuBar;
import org.viktor44.jtvision.menus.JtvMenuItem;
import org.viktor44.jtvision.menus.JtvStatusDef;
import org.viktor44.jtvision.menus.JtvStatusItem;
import org.viktor44.jtvision.menus.JtvStatusLine;
import org.viktor44.jtvision.menus.JtvSubMenu;

/**
 * Directory tree demo: browse a filesystem tree in a collapsible outline.
 */
public class DirApp extends JtvApplication {

    private static final int cmAbout = 10100;
    private static final int cmDirTree = 10101;

    private final String drive;

    public DirApp(String drive) {
        this.drive = drive;
        insertWindow(new DirWindow(drive));
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmAbout:
                    aboutBox();
                    break;
                case cmDirTree:
                    insertWindow(new DirWindow(drive));
                    break;
                default:
                    return;
            }
            clearEvent(event);
        }
    }

    private void aboutBox() {
        JtvDialog about = new JtvDialog(new JtvRect(0, 0, 39, 11), "About");
        about.insert(new JtvStaticText(new JtvRect(9, 2, 30, 7),
            "\003Outline Viewer Demo\n\n" +
            "\003Copyright (c) 1994\n\n" +
            "\003Borland International"));
        about.insert(new JtvButton(new JtvRect(14, 8, 25, 10), " OK", cmOK, bfDefault));
        about.setOptions(about.getOptions() | ofCentered);
        executeDialog(about, null);
    }

    @Override
    protected JtvMenuBar initMenuBar(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getA().getY(), r.getB().getX(), r.getA().getY() + 1);
        return new JtvMenuBar(r, new JtvMenu()
                .addItem(new JtvMenuItem("~\u2261~", JtvKey.kbNoKey, new JtvMenu(
                new JtvMenuItem("~A~bout...", cmAbout, JtvKey.kbAltA))))
                .addItem(new JtvSubMenu("~F~ile", JtvKey.kbAltF)
                .addItem(new JtvMenuItem("~N~ew Window...", cmDirTree, JtvKey.kbAltN))
                .addSeparator()
                .addItem(new JtvMenuItem("E~x~it", cmQuit, JtvKey.kbCtrlQ, 0, "Ctrl-Q"))));
    }

    @Override
    protected JtvStatusLine initStatusLine(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getB().getY() - 1, r.getB().getX(), r.getB().getY());
        return new JtvStatusLine(r,
            new JtvStatusDef(0, 0xFFFF, null)
                .addItem(new JtvStatusItem("~Ctrl-Q~ Exit", JtvKey.kbCtrlQ, cmQuit))
                .addItem(new JtvStatusItem(null, JtvKey.kbF10, cmMenu))
                .addItem(new JtvStatusItem(null, JtvKey.kbAltF3, cmClose)));
    }

    public static void main(String[] args) {
        String drive = (args.length >= 1) ? args[0] : ".";
        new DirApp(drive).run();
    }
}
