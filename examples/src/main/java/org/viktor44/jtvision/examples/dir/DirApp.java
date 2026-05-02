/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.dir;

import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmQuit;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.viktor44.jtvision.core.JtvApplication;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvKeyStroke;
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

        return new JtvMenuBar(r)
				.addItem(
						new JtvSubMenu("~\u2261~")
							.addItem(new JtvMenuItem("~A~bout...", cmAbout))
				)
				.addItem(
						new JtvSubMenu("~F~ile", JtvKeyStroke.of(KeyEvent.VK_F, InputEvent.ALT_DOWN_MASK))
							.addItem(new JtvMenuItem("~N~ew Window...", cmDirTree))
							.addSeparator()
							.addItem(new JtvMenuItem("E~x~it", cmQuit, JtvKeyStroke.of(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), 0, "Ctrl+Q"))
				);
    }

    @Override
    protected JtvStatusLine initStatusLine(JtvRect r) {
        return new JtvStatusLine(
        		new JtvRect(r.getA().getX(), r.getB().getY() - 1, r.getB().getX(), r.getB().getY()),
	            new JtvStatusDef()
		                .addItem(new JtvStatusItem("~Ctrl+Q~ Exit", JtvKeyStroke.of(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), cmQuit))
		                .addItem(new JtvStatusItem(null, JtvKeyStroke.of(KeyEvent.VK_F10), cmMenu))
		                .addItem(new JtvStatusItem(null, JtvKeyStroke.of(KeyEvent.VK_F3, InputEvent.ALT_DOWN_MASK), cmClose))
        );
    }

    public static void main(String[] args) {
        String drive = (args.length >= 1) ? args[0] : ".";
        new DirApp(drive).run();
    }
}
