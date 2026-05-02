/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.mmenu;

import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.CommandCodes.cmQuit;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.viktor44.jtvision.core.EventCodes;
import org.viktor44.jtvision.core.JtvApplication;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvKeyStroke;
import org.viktor44.jtvision.core.JtvProgram;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.menus.JtvMenu;
import org.viktor44.jtvision.menus.JtvMenuBar;
import org.viktor44.jtvision.menus.JtvMenuItem;
import org.viktor44.jtvision.menus.JtvStatusDef;
import org.viktor44.jtvision.menus.JtvStatusItem;
import org.viktor44.jtvision.menus.JtvStatusLine;
import org.viktor44.jtvision.menus.JtvSubMenu;

/**
 * Multiple Menu Bar Demo.
 *
 * Demonstrates the TMultiMenu class which allows switching between
 * different menu bar configurations at runtime. Three menus are defined,
 * each with a "Next menu" item to cycle through them, a numbered submenu,
 * and a unique third submenu (File, Edit, Compile).
 */
public class MultiMenuApp extends JtvApplication {

    private static final int cmOne     = 100;
    private static final int cmTwo     = 101;
    private static final int cmThree   = 102;
    private static final int cmCycle   = 110;
    private static final int cmNothing = 111;

    private int curMenu = 0;

    public MultiMenuApp() {
    }

    @Override
    protected JtvStatusLine initStatusLine(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getB().getY() - 1, r.getB().getX(), r.getB().getY());
        return new JtvStatusLine(r,
            new JtvStatusDef()
                .addItem(new JtvStatusItem("~Ctrl+Q~ Exit", JtvKeyStroke.of(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), cmQuit))
                .addItem(new JtvStatusItem(null, JtvKeyStroke.of(KeyEvent.VK_F10), cmMenu))
                .addItem(new JtvStatusItem(null, JtvKeyStroke.of(KeyEvent.VK_F3, InputEvent.ALT_DOWN_MASK), cmClose))
        );
    }

    @Override
    protected JtvMenuBar initMenuBar(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getA().getY(), r.getB().getX(), r.getA().getY() + 1);

        // Menu Number One
        JtvMenu menu1 = new JtvMenu()
                .addItem(new JtvMenuItem("~N~ext menu", cmCycle))
                .addItem(
                		new JtvSubMenu("~M~enu One")
			                    .addItem(new JtvMenuItem("~O~ne", cmOne))
			                    .addItem(new JtvMenuItem("~T~wo", cmTwo))
			                    .addItem(new JtvMenuItem("T~h~ree", cmThree))
                )
                .addItem(
                		new JtvSubMenu("~F~ile")
			                    .addItem(new JtvMenuItem("~N~ew", cmNothing))
			                    .addItem(new JtvMenuItem("~O~pen", cmNothing))
			                    .addItem(new JtvMenuItem("~S~ave", cmNothing))
			                    .addItem(new JtvMenuItem("S~a~ve all", cmNothing))
                );

        // Menu Number Two
        JtvMenu menu2 = new JtvMenu()
                .addItem(new JtvMenuItem("~N~ext menu", cmCycle))
                .addItem(
                		new JtvSubMenu("~M~enu Two")
			                    .addItem(new JtvMenuItem("~O~ne", cmOne))
			                    .addItem(new JtvMenuItem("~T~wo", cmTwo))
			                    .addItem(new JtvMenuItem("T~h~ree", cmThree))
                )
                .addItem(
                		new JtvSubMenu("~E~dit")
			                    .addItem(new JtvMenuItem("Cu~t~", cmNothing))
			                    .addItem(new JtvMenuItem("~C~opy", cmNothing))
			                    .addItem(new JtvMenuItem("~P~aste", cmNothing))
                );

        // Menu Number Three
        JtvMenu menu3 = new JtvMenu()
                .addItem(new JtvMenuItem("~N~ext menu", cmCycle))
                .addItem(
                		new JtvSubMenu("~M~enu Three")
			                    .addItem(new JtvMenuItem("~O~ne", cmOne))
			                    .addItem(new JtvMenuItem("~T~wo", cmTwo))
			                    .addItem(new JtvMenuItem("T~h~ree", cmThree))
                )
                .addItem(
                		new JtvSubMenu("~C~ompile")
			                    .addItem(new JtvMenuItem("~C~ompile", cmNothing))
			                    .addItem(new JtvMenuItem("~M~ake", cmNothing))
			                    .addItem(new JtvMenuItem("~L~ink", cmNothing))
			                    .addItem(new JtvMenuItem("~B~uild All", cmNothing))
                );

        return new MultiMenu(r, menu1, menu2, menu3);
    }

    @Override
    public void handleEvent(JtvEvent event) {
        if (event.getWhat() == EventCodes.evCommand) {
            if (event.getMessage().getCommand() >= cmOne && event.getMessage().getCommand() <= cmThree) {
                curMenu = (event.getMessage().getCommand() - cmOne) % 3;
                message(JtvProgram.getMenuBar(), EventCodes.evBroadcast, MultiMenu.cmMMChangeMenu, null);
                // Set infoInt manually since message() passes infoPtr
                clearEvent(event);
                changeMenu(curMenu);
                return;
            }
            else if (event.getMessage().getCommand() == cmCycle) {
                curMenu = (curMenu + 1) % 3;
                clearEvent(event);
                changeMenu(curMenu);
                return;
            }
        }
        super.handleEvent(event);
    }

    private void changeMenu(int menuIndex) {
        JtvEvent e = new JtvEvent();
        e.setWhat(EventCodes.evBroadcast);
        e.getMessage().setCommand(MultiMenu.cmMMChangeMenu);
        e.getMessage().setInfoInt(menuIndex);
        putEvent(e);
    }

    public static void main(String[] args) {
        MultiMenuApp app = new MultiMenuApp();
        app.run();
    }
}
