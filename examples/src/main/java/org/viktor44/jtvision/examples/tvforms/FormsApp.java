package org.viktor44.jtvision.examples.tvforms;

import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmQuit;
import static org.viktor44.jtvision.core.CommandCodes.cmResize;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.fdOpenButton;
import static org.viktor44.jtvision.core.ViewFlags.mfError;
import static org.viktor44.jtvision.core.ViewFlags.mfOKButton;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.viktor44.jtvision.core.CommandCodes;
import org.viktor44.jtvision.core.JtvApplication;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.core.ViewFlags;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvChangeDirDialog;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvFileDialog;
import org.viktor44.jtvision.dialogs.JtvStaticText;
import org.viktor44.jtvision.menus.JtvMenu;
import org.viktor44.jtvision.menus.JtvMenuBar;
import org.viktor44.jtvision.menus.JtvMenuItem;
import org.viktor44.jtvision.menus.JtvStatusDef;
import org.viktor44.jtvision.menus.JtvStatusItem;
import org.viktor44.jtvision.menus.JtvStatusLine;
import org.viktor44.jtvision.menus.JtvSubMenu;
import org.viktor44.jtvision.util.MessageBox;
import org.viktor44.jtvision.views.JtvView;

/**
 * Turbo Vision forms demo: browse and edit a phone-directory record file.
 *
 * <p>The file path is read from argv[0] (defaults to {@code phonenum.txt}).
 * Each line is a tab-separated PhoneRecord; the list window offers New / Edit /
 * Delete, and each button opens the TPhoneForm dialog for data entry.
 */
public class FormsApp extends JtvApplication {

    private static final int cmAboutBox = 2000;
    private static final int cmListOpen = 3000;
    private static final int cmChgDir = 3001;
    private static final int cmListSave = 3002;

    private final Path defaultFile;

    public FormsApp(Path defaultFile) {
        this.defaultFile = defaultFile;
        openList(defaultFile);
    }

    private void openList(Path path) {
        try {
            insertWindow(new ListDialog(path));
        } catch (IOException e) {
            MessageBox.messageBox("Cannot open " + path + ": " + e.getMessage(),
                mfError | mfOKButton);
        }
    }

    private void openListDialog() {
        JtvFileDialog d = new JtvFileDialog("*.txt", "Open File", "~N~ame", fdOpenButton, 120);
        if (validView(d) != null) {
            if (getDesktop().execView(d) != CommandCodes.cmCancel) {
                StringBuilder fileName = new StringBuilder();
                d.getFileName(fileName);
                openList(Paths.get(fileName.toString()));
            }
            destroy(d);
        }
    }

    private void changeDir() {
        JtvView d = validView(new JtvChangeDirDialog(0, 0));
        if (d != null) {
        	getDesktop().execView(d);
            destroy(d);
        }
    }

    private void saveTopList() {
        JtvView top = getDesktop().firstThat(v -> v instanceof ListDialog);
        if (top instanceof ListDialog) {
            ((ListDialog) top).saveRecords();
        }
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmAboutBox:
                    aboutBox();
                    break;
                case cmListOpen:
                    openListDialog();
                    break;
                case cmListSave:
                    saveTopList();
                    break;
                case cmChgDir:
                    changeDir();
                    break;
                default:
                    return;
            }
            clearEvent(event);
        }
    }

    private void aboutBox() {
        JtvDialog d = new JtvDialog(new JtvRect(0, 0, 40, 10), "About");
        d.insert(new JtvStaticText(new JtvRect(3, 2, 37, 6),
            "\003JT Vision Forms Demo\n\n" +
            "\003Phone directory data entry"));
        d.insert(new JtvButton(new JtvRect(14, 7, 26, 9), " OK", cmOK, bfDefault));
        d.setOptions(d.getOptions() | ofCentered);
        executeDialog(d, null);
    }

    @Override
    protected JtvMenuBar initMenuBar(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getA().getY(), r.getB().getX(), r.getA().getY() + 1);
        return new JtvMenuBar(r, new JtvMenu()
                .addItem(new JtvMenuItem("~\u2261~", JtvKey.kbNoKey, new JtvMenu(
                new JtvMenuItem("~A~bout...", cmAboutBox, JtvKey.kbAltA))))
                .addItem(new JtvSubMenu("~F~ile", JtvKey.kbAltF)
                .addItem(new JtvMenuItem("~O~pen list", cmListOpen, JtvKey.kbF3, 0, "F3"))
                .addItem(new JtvMenuItem("~S~ave", cmListSave, JtvKey.kbF2, 0, "F2"))
                .addSeparator()
                .addItem(new JtvMenuItem("~C~hange directory...", cmChgDir, JtvKey.kbNoKey))
                .addSeparator()
                .addItem(new JtvMenuItem("E~x~it", cmQuit, JtvKey.kbCtrlQ, 0, "Ctrl-Q")))
                .addItem(new JtvSubMenu("~W~indow", JtvKey.kbAltW)
                .addItem(new JtvMenuItem("~M~ove", cmResize, JtvKey.kbCtrlF5, 0, "Ctrl-F5"))));
    }

    @Override
    protected JtvStatusLine initStatusLine(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getB().getY() - 1, r.getB().getX(), r.getB().getY());
        return new JtvStatusLine(r,
            new JtvStatusDef(0, 0xFFFF, null)
                .addItem(new JtvStatusItem("~F2~ Save", JtvKey.kbF2, cmListSave))
                .addItem(new JtvStatusItem("~F3~ Open", JtvKey.kbF3, cmListOpen))
                .addItem(new JtvStatusItem(null, JtvKey.kbCtrlF5, cmResize))
                .addItem(new JtvStatusItem(null, JtvKey.kbF10, cmMenu))
                .addItem(new JtvStatusItem("~Ctrl-Q~ Exit", JtvKey.kbCtrlQ, cmQuit))
                .addItem(new JtvStatusItem(null, JtvKey.kbAltF3, cmClose)));
    }

    public static void main(String[] args) {
        Path file = Paths.get(args.length >= 1 ? args[0] : "phonenum.txt");
        new FormsApp(file).run();
    }
}
