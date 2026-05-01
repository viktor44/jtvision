/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.forms;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;

import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvCheckBoxes;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvInputLine;
import org.viktor44.jtvision.dialogs.JtvLabel;
import org.viktor44.jtvision.dialogs.JtvRadioButtons;

/**
 * Phone directory data-entry dialog. 
 */
public class PhoneForm extends JtvDialog {

    private static final int NAME_WIDTH = 25;
    private static final int COMPANY_WIDTH = 23;
    private static final int REMARKS_WIDTH = 22;
    private static final int PHONE_WIDTH = 20;

    private final KeyInputLine nameField;
    private final JtvInputLine companyField;
    private final JtvInputLine remarksField;
    private final JtvInputLine phoneField;
    private final JtvCheckBoxes typeBoxes;
    private final JtvRadioButtons genderButtons;

    public PhoneForm() {
        super(new JtvRect(5, 3, 46, 20), "Phone Numbers");

        int labelCol = 1, labelWid = 8, inputCol = 11;
        int y = 2;

        nameField = new KeyInputLine(new JtvRect(inputCol, y, inputCol + NAME_WIDTH + 2, y + 1), NAME_WIDTH);
        insert(nameField);
        insert(new JtvLabel(new JtvRect(labelCol, y, labelCol + labelWid, y + 1), "~N~ame", nameField));

        y += 2;
        companyField = new JtvInputLine(new JtvRect(inputCol, y, inputCol + COMPANY_WIDTH + 2, y + 1), COMPANY_WIDTH);
        insert(companyField);
        insert(new JtvLabel(new JtvRect(labelCol, y, labelCol + labelWid, y + 1), "~C~ompany", companyField));

        y += 2;
        remarksField = new JtvInputLine(new JtvRect(inputCol, y, inputCol + REMARKS_WIDTH + 2, y + 1), REMARKS_WIDTH);
        insert(remarksField);
        insert(new JtvLabel(new JtvRect(labelCol, y, labelCol + labelWid, y + 1), "~R~emarks", remarksField));

        y += 2;
        phoneField = new JtvInputLine(new JtvRect(inputCol, y, inputCol + PHONE_WIDTH + 2, y + 1), PHONE_WIDTH);
        insert(phoneField);
        insert(new JtvLabel(new JtvRect(labelCol, y, labelCol + labelWid, y + 1), "~P~hone", phoneField));

        int x = inputCol;
        y += 3;
        typeBoxes = new JtvCheckBoxes(new JtvRect(x, y, x + 14, y + 2),
            new String[] { "Business", "Personal" });
        insert(typeBoxes);
        insert(new JtvLabel(new JtvRect(x, y - 1, x + labelWid, y), "~T~ype", typeBoxes));

        x += 15;
        genderButtons = new JtvRadioButtons(new JtvRect(x, y, x + 12, y + 2),
            new String[] { "Male", "Female" });
        insert(genderButtons);
        insert(new JtvLabel(new JtvRect(x, y - 1, x + labelWid, y), "~G~ender", genderButtons));

        y += 3;
        int formWd = 41;
        int buttonWd = 12;
        int bx = formWd - 2 * (buttonWd + 2);
        insert(new JtvButton(new JtvRect(bx, y, bx + buttonWd, y + 2), "~S~ave", cmOK, bfDefault));
        bx = formWd - (buttonWd + 2);
        insert(new JtvButton(new JtvRect(bx, y, bx + buttonWd, y + 2), "Cancel", cmCancel, bfNormal));

        selectNext(false);
    }

    public void loadFrom(PhoneRecord r) {
        nameField.setDataFrom(r.name);
        companyField.setDataFrom(r.company);
        remarksField.setDataFrom(r.remarks);
        phoneField.setDataFrom(r.phone);
        typeBoxes.setValue(r.acquaintType);
        genderButtons.setValue(r.gender);
    }

    public void saveTo(PhoneRecord r) {
        r.name = nameField.getData();
        r.company = companyField.getData();
        r.remarks = remarksField.getData();
        r.phone = phoneField.getData();
        r.acquaintType = typeBoxes.getValue();
        r.gender = genderButtons.getValue();
    }
}
