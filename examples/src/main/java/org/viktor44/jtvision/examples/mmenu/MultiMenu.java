/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.mmenu;

import org.viktor44.jtvision.core.EventCodes;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.menus.JtvMenu;
import org.viktor44.jtvision.menus.JtvMenuBar;

/**
 * A menu bar that supports dynamic changing of menus via the messaging system.
 *
 * When created, an array of TMenu objects is passed to the constructor.
 * At runtime, any of these menus can be selected by sending a broadcast message:
 *
 *   message(menuBar, evBroadcast, cmMMChangeMenu, menuIndex)
 *
 * where menuIndex is the zero-based index of the desired menu.
 */
public class MultiMenu extends JtvMenuBar {

    public static final int cmMMChangeMenu = 0x1600;

    private final JtvMenu[] menuList;

    public MultiMenu(JtvRect bounds, JtvMenu... menus) {
        super(bounds, menus[0]);
        menuList = menus;
    }

    @Override
    public void handleEvent(JtvEvent event) {
        if (event.getWhat() == EventCodes.evBroadcast &&
            event.getMessage().getCommand() == cmMMChangeMenu) {
            int idx = event.getMessage().getInfoInt();
            if (idx >= 0 && idx < menuList.length) {
                if (menu != menuList[idx]) {
                    menu = menuList[idx];
                    drawView();
                }
            }
            clearEvent(event);
        } else {
            super.handleEvent(event);
        }
    }
}
