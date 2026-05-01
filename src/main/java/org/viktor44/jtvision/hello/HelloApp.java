/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.hello;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.CommandCodes.cmQuit;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;
import static org.viktor44.jtvision.core.ViewFlags.hcNoContext;

import org.viktor44.jtvision.core.JtvApplication;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvStaticText;
import org.viktor44.jtvision.menus.JtvMenuBar;
import org.viktor44.jtvision.menus.JtvMenuItem;
import org.viktor44.jtvision.menus.JtvStatusDef;
import org.viktor44.jtvision.menus.JtvStatusItem;
import org.viktor44.jtvision.menus.JtvStatusLine;
import org.viktor44.jtvision.menus.JtvSubMenu;

/**
 * JT Vision Hello World Demo.
 */
public class HelloApp extends JtvApplication {

    private static final int GreetThemCmd = 100;

    public HelloApp() {
    }

    private void greetingBox() {
        JtvDialog d = new JtvDialog(new JtvRect(25, 5, 55, 16), "Hello, World!");

        d.insert(new JtvStaticText(new JtvRect(3, 5, 15, 6), "How are you?"));
        d.insert(new JtvButton(new JtvRect(16, 2, 28, 4), "Terrific", cmCancel, bfNormal));
        d.insert(new JtvButton(new JtvRect(16, 4, 28, 6), "Ok", cmCancel, bfNormal));
        d.insert(new JtvButton(new JtvRect(16, 6, 28, 8), "Lousy", cmCancel, bfNormal));
        d.insert(new JtvButton(new JtvRect(16, 8, 28, 10), "Cancel", cmCancel, bfNormal));

        getDesktop().execView(d);
        destroy(d);
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case GreetThemCmd:
                    greetingBox();
                    clearEvent(event);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected JtvMenuBar initMenuBar(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getA().getY(), r.getB().getX(), r.getA().getY() + 1);
        return new JtvMenuBar(r,
            new JtvSubMenu("~H~ello", JtvKey.kbAltH)
                .addItem(new JtvMenuItem("~G~reeting...", GreetThemCmd, JtvKey.kbAltG))
                .addSeparator()
                .addItem(new JtvMenuItem("E~x~it", cmQuit, JtvKey.kbCtrlQ, hcNoContext, "Ctrl+Q"))
        );
    }

    @Override
    protected JtvStatusLine initStatusLine(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getB().getY() - 1, r.getB().getX(), r.getB().getY());
        return new JtvStatusLine(r,
            new JtvStatusDef(0, 0xFFFF, null)
                .addItem(new JtvStatusItem("~Ctrl-Q~ Exit", JtvKey.kbCtrlQ, cmQuit))
                .addItem(new JtvStatusItem(null, JtvKey.kbF10, cmMenu)));
    }

    public static void main(String[] args) {
        HelloApp app = new HelloApp();
        app.run();
    }
}
