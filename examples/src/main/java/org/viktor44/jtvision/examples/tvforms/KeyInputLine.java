package org.viktor44.jtvision.examples.tvforms;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmValid;
import static org.viktor44.jtvision.core.ViewFlags.mfError;
import static org.viktor44.jtvision.core.ViewFlags.mfOKButton;

import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvInputLine;
import org.viktor44.jtvision.util.MessageBox;

/**
 * TInputLine variant that rejects an empty value. 
 */
public class KeyInputLine extends JtvInputLine {

    public KeyInputLine(JtvRect bounds, int aMaxLen) {
        super(bounds, aMaxLen);
    }

    @Override
    public boolean valid(int command) {
        if (command != cmCancel && command != cmValid && data.isEmpty()) {
            select();
            MessageBox.messageBox("This field cannot be empty.", mfError | mfOKButton);
            return false;
        }
        return super.valid(command);
    }
}
