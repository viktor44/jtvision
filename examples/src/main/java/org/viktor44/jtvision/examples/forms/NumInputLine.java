/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.forms;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmValid;
import static org.viktor44.jtvision.core.ViewFlags.mfError;
import static org.viktor44.jtvision.core.ViewFlags.mfOKButton;

import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvInputLine;
import org.viktor44.jtvision.util.MessageBox;

/**
 * TInputLine variant that only accepts integer values within [min, max].
 */
public class NumInputLine extends JtvInputLine {

    public final long min;
    public final long max;

    public NumInputLine(JtvRect bounds, int aMaxLen, long aMin, long aMax) {
        super(bounds, aMaxLen);
        this.min = aMin;
        this.max = aMax;
    }

    @Override
    public boolean valid(int command) {
        if (command != cmCancel && command != cmValid) {
            String s = data.isEmpty() ? "0" : data;
            long value;
            try {
                value = Long.parseLong(s.trim());
            } catch (NumberFormatException e) {
                value = 0;
            }
            if (value == 0 || value < min || value > max) {
                select();
                MessageBox.messageBox("Number must be from " + min + " to " + max + ".",
                    mfError | mfOKButton);
                selectAll(true);
                return false;
            }
        }
        return super.valid(command);
    }
}
