/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.demo;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;

import org.viktor44.jtvision.core.JtvBackground;
import org.viktor44.jtvision.core.JtvProgram;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvInputLine;
import org.viktor44.jtvision.dialogs.JtvStaticText;

/**
 * Demo dialog to apply desktop background pattern.
 */
public class ChangeBackgroundDialog extends JtvDialog {

    private final JtvBackground background;
    private final JtvInputLine input;

    public ChangeBackgroundDialog(JtvBackground background) {
        super(new JtvRect(0, 0, 29, 9), "");
        this.background = background;

        JtvRect r = getBounds();
        r.move((JtvProgram.getDesktop().getSize().getX() - r.getB().getX()) / 2,
            (JtvProgram.getDesktop().getSize().getY() - r.getB().getY()) / 2);
        changeBounds(r);

        input = new JtvInputLine(new JtvRect(4, 5, 7, 6), 2);
        insert(input);
        insert(new JtvStaticText(new JtvRect(2, 2, 27, 3), "Enter background pattern:"));
        insert(new JtvButton(new JtvRect(16, 4, 26, 6), "~A~pply", cmOK, bfDefault));
        insert(new JtvButton(new JtvRect(16, 6, 26, 8), "~C~lose", cmCancel, bfNormal));
        input.focus();
    }

    @Override
    public boolean valid(int command) {
        if (!super.valid(command)) {
            return false;
        }
        if (background != null && command == cmOK) {
            String s = input.getData();
            if (s != null && !s.isEmpty()) {
                char pattern = s.charAt(0);
                if (pattern != '\0') {
                    background.setPattern(pattern);
                    background.drawView();
                }
            }
            return false;
        }
        return true;
    }
}
