/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.tvforms;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;
import static org.viktor44.jtvision.core.ViewFlags.mfError;
import static org.viktor44.jtvision.core.ViewFlags.mfOKButton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvProgram;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvSortedListBox;
import org.viktor44.jtvision.util.MessageBox;
import org.viktor44.jtvision.views.JtvScrollBar;

/**
 * List window showing all records in the current file. New/Edit/Delete buttons
 * invoke the phone form; the list is persisted to the backing text file.
 */
public class ListDialog extends JtvDialog {

    public static final int cmAdd = 3100;
    public static final int cmEdit = 3101;
    public static final int cmDelete = 3102;

    private final Path filePath;
    private final RecordListBox listBox;

    private static final class RecordListBox extends JtvSortedListBox {
        RecordListBox(JtvRect bounds, JtvScrollBar vsb) {
            super(bounds, 1, vsb);
        }

        @Override
        protected int compareItems(Object a, Object b) {
            String ka = key(a);
            String kb = key(b);
            return ka.compareToIgnoreCase(kb);
        }

        private String key(Object item) {
            if (item instanceof PhoneRecord) {
                PhoneRecord r = (PhoneRecord) item;
                return r.name != null ? r.name : "";
            }
            return item != null ? item.toString() : "";
        }

        @Override
        protected String itemText(Object item) {
            if (!(item instanceof PhoneRecord)) {
                return super.itemText(item);
            }
            PhoneRecord r = (PhoneRecord) item;
            String name = r.name != null && !r.name.isEmpty() ? r.name : "<unnamed>";
            String phone = r.phone != null ? r.phone : "";
            if (phone.isEmpty()) {
                return name;
            }
            return name + "  " + phone;
        }
    }

    public ListDialog(Path filePath) throws IOException {
        super(new JtvRect(10, 2, 70, 20), filePath.getFileName().toString());
        this.filePath = filePath;

        JtvScrollBar vsb = new JtvScrollBar(new JtvRect(45, 2, 46, 14));
        insert(vsb);
        listBox = new RecordListBox(new JtvRect(3, 2, 45, 14), vsb);
        insert(listBox);
        loadRecords();

        insert(new JtvButton(new JtvRect(47, 2, 58, 4), "~N~ew", cmAdd, bfNormal));
        insert(new JtvButton(new JtvRect(47, 5, 58, 7), "~E~dit", cmEdit, bfNormal));
        insert(new JtvButton(new JtvRect(47, 8, 58, 10), "~D~elete", cmDelete, bfNormal));
        insert(new JtvButton(new JtvRect(47, 14, 58, 16), "Close", cmCancel, bfNormal));
    }

    private void loadRecords() throws IOException {
        List<PhoneRecord> records = new ArrayList<>();
        if (Files.exists(filePath)) {
            for (String line : Files.readAllLines(filePath)) {
                if (!line.isEmpty()) records.add(PhoneRecord.parse(line));
            }
        }
        listBox.newListObjects(records);
    }

    private List<PhoneRecord> snapshotRecords() {
        List<PhoneRecord> records = new ArrayList<>();
        for (Object o : listBox.list()) {
            if (o instanceof PhoneRecord) {
                records.add((PhoneRecord) o);
            }
        }
        return records;
    }

    void saveRecords() {
        try {
            List<String> lines = new ArrayList<>();
            for (PhoneRecord r : snapshotRecords()) {
                lines.add(r.toLine());
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            MessageBox.messageBox("Cannot save: " + e.getMessage(), mfError | mfOKButton);
        }
    }

    private PhoneRecord focusedRecord() {
        int idx = listBox.getFocused();
        List<PhoneRecord> records = snapshotRecords();
        if (idx < 0 || idx >= records.size()) return null;
        return records.get(idx);
    }

    private void editForm(PhoneRecord r, boolean isNew) {
        PhoneForm form = new PhoneForm();
        form.loadFrom(r);
        int result = JtvProgram.getApplication().executeDialog(form, null);
        if (result != cmCancel) {
            form.saveTo(r);
            List<PhoneRecord> records = snapshotRecords();
            if (isNew) {
                records.add(r);
            }
            listBox.newListObjects(records);
            saveRecords();
        }
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmAdd:
                    editForm(new PhoneRecord(), true);
                    clearEvent(event);
                    break;
                case cmEdit: {
                    PhoneRecord r = focusedRecord();
                    if (r != null) editForm(r, false);
                    clearEvent(event);
                    break;
                }
                case cmDelete: {
                    int idx = listBox.getFocused();
                    List<PhoneRecord> records = snapshotRecords();
                    if (idx >= 0 && idx < records.size()) {
                        records.remove(idx);
                        listBox.newListObjects(records);
                        saveRecords();
                    }
                    clearEvent(event);
                    break;
                }
            }
        }
    }
}
