/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.mmenu;

import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.CommandCodes.cmQuit;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;

import org.viktor44.jtvision.core.EventCodes;
import org.viktor44.jtvision.core.JtvApplication;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
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
            new JtvStatusDef(0, 0xFFFF, null)
                .addItem(new JtvStatusItem("~Ctrl-Q~ Exit", JtvKey.kbCtrlQ, cmQuit))
                .addItem(new JtvStatusItem(null, JtvKey.kbF10, cmMenu))
                .addItem(new JtvStatusItem(null, JtvKey.kbAltF3, cmClose)));
    }

    @Override
    protected JtvMenuBar initMenuBar(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getA().getY(), r.getB().getX(), r.getA().getY() + 1);

        // Menu Number One
        JtvMenu menu1 = new JtvMenu()
                .addItem(new JtvMenuItem("~N~ext menu", cmCycle, JtvKey.kbAltN))
                .addItem(new JtvSubMenu("~M~enu One", JtvKey.kbAltM)
                    .addItem(new JtvMenuItem("~O~ne", cmOne, JtvKey.kbAltO))
                    .addItem(new JtvMenuItem("~T~wo", cmTwo, JtvKey.kbAltT))
                    .addItem(new JtvMenuItem("T~h~ree", cmThree, JtvKey.kbAltH)))
                .addItem(new JtvSubMenu("~F~ile", JtvKey.kbAltF)
                    .addItem(new JtvMenuItem("~N~ew", cmNothing, JtvKey.kbAltN))
                    .addItem(new JtvMenuItem("~O~pen", cmNothing, JtvKey.kbAltO))
                    .addItem(new JtvMenuItem("~S~ave", cmNothing, JtvKey.kbAltS))
                    .addItem(new JtvMenuItem("S~a~ve all", cmNothing, JtvKey.kbAltA)));

        // Menu Number Two
        JtvMenu menu2 = new JtvMenu()
                .addItem(new JtvMenuItem("~N~ext menu", cmCycle, JtvKey.kbAltN))
                .addItem(new JtvSubMenu("~M~enu Two", JtvKey.kbAltM)
                    .addItem(new JtvMenuItem("~O~ne", cmOne, JtvKey.kbAltO))
                    .addItem(new JtvMenuItem("~T~wo", cmTwo, JtvKey.kbAltT))
                    .addItem(new JtvMenuItem("T~h~ree", cmThree, JtvKey.kbAltH)))
                .addItem(new JtvSubMenu("~E~dit", JtvKey.kbAltE)
                    .addItem(new JtvMenuItem("Cu~t~", cmNothing, JtvKey.kbAltT))
                    .addItem(new JtvMenuItem("~C~opy", cmNothing, JtvKey.kbAltC))
                    .addItem(new JtvMenuItem("~P~aste", cmNothing, JtvKey.kbAltP)));

        // Menu Number Three
        JtvMenu menu3 = new JtvMenu()
                .addItem(new JtvMenuItem("~N~ext menu", cmCycle, JtvKey.kbAltN))
                .addItem(new JtvSubMenu("~M~enu Three", JtvKey.kbAltM)
                    .addItem(new JtvMenuItem("~O~ne", cmOne, JtvKey.kbAltO))
                    .addItem(new JtvMenuItem("~T~wo", cmTwo, JtvKey.kbAltT))
                    .addItem(new JtvMenuItem("T~h~ree", cmThree, JtvKey.kbAltH)))
                .addItem(new JtvSubMenu("~C~ompile", JtvKey.kbAltC)
                    .addItem(new JtvMenuItem("~C~ompile", cmNothing, JtvKey.kbAltO))
                    .addItem(new JtvMenuItem("~M~ake", cmNothing, JtvKey.kbAltT))
                    .addItem(new JtvMenuItem("~L~ink", cmNothing, JtvKey.kbAltH))
                    .addItem(new JtvMenuItem("~B~uild All", cmNothing, JtvKey.kbAltH)));

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
