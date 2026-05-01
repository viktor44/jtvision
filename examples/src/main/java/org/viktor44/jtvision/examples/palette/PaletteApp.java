package org.viktor44.jtvision.examples.palette;

import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmQuit;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;

import java.util.ArrayList;

import org.viktor44.jtvision.core.JtvApplication;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvStaticText;
import org.viktor44.jtvision.menus.JtvMenu;
import org.viktor44.jtvision.menus.JtvMenuBar;
import org.viktor44.jtvision.menus.JtvMenuItem;
import org.viktor44.jtvision.menus.JtvStatusDef;
import org.viktor44.jtvision.menus.JtvStatusItem;
import org.viktor44.jtvision.menus.JtvStatusLine;
import org.viktor44.jtvision.views.JtvView;

/**
 * Palette demo. Demonstrates per-view and per-window palette customization.
 */
public class PaletteApp extends JtvApplication {

    private static final int cmAbout = 100;
    private static final int cmPaletteView = 101;

    // Matches cpTestAppC from the original C++ palette example.
    private static final int[] cpTestAppExt = {0x3E, 0x2D, 0x72, 0x5F, 0x68, 0x4E};

    public PaletteApp() {
    }

    @Override
    public JtvPalette getPalette() {
        JtvPalette base = super.getPalette();
        int[] extended = new int[base.length() + cpTestAppExt.length];
        for (int i = 0; i < base.length(); i++) {
            extended[i] = base.get(i + 1).getValue();
        }
        System.arraycopy(cpTestAppExt, 0, extended, base.length(), cpTestAppExt.length);
        return new JtvPalette(extended);
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmAbout:
                    aboutDlg();
                    break;
                case cmPaletteView:
                    paletteView();
                    break;
                default:
                    return;
            }
            clearEvent(event);
        }
    }

    private void aboutDlg() {
        JtvDialog about = new JtvDialog(new JtvRect(0, 0, 47, 13), "About");
        about.insert(
        		new JtvStaticText(
        				new JtvRect(2, 1, 45, 9),
			            "\n\003PALETTE EXAMPLE\n \n" +
			            "\003 JT Vision Demo\n \n"
           		)
        );
        about.insert(new JtvButton(new JtvRect(18, 10, 29, 12), "OK", cmOK, bfDefault));
        about.setOptions(about.getOptions() | ofCentered);
        getDesktop().execView(about);
        destroy(about);
    }

    private void paletteView() {
        JtvView view = new PaletteWindow();
        if (validView(view) != null) {
        	getDesktop().insert(view);
        }
    }

    @Override
    protected JtvMenuBar initMenuBar(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getA().getY(), r.getB().getX(), r.getA().getY() + 1);
        return new JtvMenuBar(r, new JtvMenu()
                .addItem(new JtvMenuItem("~A~bout...", cmAbout, JtvKey.kbAltA))
                .addItem(new JtvMenuItem("~P~alette", cmPaletteView, JtvKey.kbAltP))
                .addItem(new JtvMenuItem("E~x~it", cmQuit, JtvKey.kbCtrlQ, 0, "Ctrl-Q")));
    }

    @Override
    protected JtvStatusLine initStatusLine(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getB().getY() - 1, r.getB().getX(), r.getB().getY());
        return new JtvStatusLine(r,
            new JtvStatusDef(0, 0xFFFF, new ArrayList<JtvStatusItem>())
                .addItem(new JtvStatusItem("~Ctrl-Q~ Exit", JtvKey.kbCtrlQ, cmQuit))
                .addItem(new JtvStatusItem(null, JtvKey.kbF10, cmMenu)));
    }

    public static void main(String[] args) {
        new PaletteApp().run();
    }
}
