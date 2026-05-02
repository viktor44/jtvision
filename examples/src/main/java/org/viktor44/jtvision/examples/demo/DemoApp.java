/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.demo;

import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmCascade;
import static org.viktor44.jtvision.core.CommandCodes.cmClose;
import static org.viktor44.jtvision.core.CommandCodes.cmHelp;
import static org.viktor44.jtvision.core.CommandCodes.cmMenu;
import static org.viktor44.jtvision.core.CommandCodes.cmOK;
import static org.viktor44.jtvision.core.CommandCodes.cmQuit;
import static org.viktor44.jtvision.core.CommandCodes.cmResize;
import static org.viktor44.jtvision.core.CommandCodes.cmScrollBarChanged;
import static org.viktor44.jtvision.core.CommandCodes.cmTile;
import static org.viktor44.jtvision.core.CommandCodes.cmZoom;
import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evMouse;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evNothing;
import static org.viktor44.jtvision.core.EventCodes.mbLeftButton;
import static org.viktor44.jtvision.core.EventCodes.mbRightButton;
import static org.viktor44.jtvision.core.EventCodes.meDoubleClick;
import static org.viktor44.jtvision.core.ViewFlags.bfDefault;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;
import static org.viktor44.jtvision.core.ViewFlags.cdHelpButton;
import static org.viktor44.jtvision.core.ViewFlags.fdOpenButton;
import static org.viktor44.jtvision.core.ViewFlags.mfError;
import static org.viktor44.jtvision.core.ViewFlags.mfOKButton;
import static org.viktor44.jtvision.core.ViewFlags.ofCentered;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.viktor44.jtvision.core.CommandCodes;
import org.viktor44.jtvision.core.EventCodes;
import org.viktor44.jtvision.core.JtvApplication;
import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDesktop;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvProgram;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.core.MouseEvent;
import org.viktor44.jtvision.core.ViewFlags;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvChangeDirDialog;
import org.viktor44.jtvision.dialogs.JtvCheckBoxes;
import org.viktor44.jtvision.dialogs.JtvColorDialog;
import org.viktor44.jtvision.dialogs.JtvColorGroup;
import org.viktor44.jtvision.dialogs.JtvColorItem;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvFileDialog;
import org.viktor44.jtvision.dialogs.JtvLabel;
import org.viktor44.jtvision.dialogs.JtvStaticText;
import org.viktor44.jtvision.editors.JtvEditWindow;
import org.viktor44.jtvision.help.JtvHelpFile;
import org.viktor44.jtvision.help.JtvHelpWindow;
import org.viktor44.jtvision.menus.JtvMenu;
import org.viktor44.jtvision.menus.JtvMenuBar;
import org.viktor44.jtvision.menus.JtvMenuItem;
import org.viktor44.jtvision.menus.JtvStatusDef;
import org.viktor44.jtvision.menus.JtvStatusItem;
import org.viktor44.jtvision.menus.JtvStatusLine;
import org.viktor44.jtvision.menus.JtvSubMenu;
import org.viktor44.jtvision.platform.EventQueue;
import org.viktor44.jtvision.util.MessageBox;
import org.viktor44.jtvision.views.JtvScrollBar;
import org.viktor44.jtvision.views.JtvView;
import org.viktor44.jtvision.views.JtvWindow;

/**
 * JT Vision demo application port.
 */
public class DemoApp extends JtvApplication {

    private static final int cmAboutCmd     = 6000;
    private static final int cmPuzzleCmd    = 6001;
    private static final int cmCalendarCmd  = 6002;
    private static final int cmAsciiCmd     = 6003;
    private static final int cmCalcCmd      = 6004;
    private static final int cmOpenCmd      = 6005;
    private static final int cmChDirCmd     = 6006;
    private static final int cmMouseCmd     = 6007;
    private static final int cmColorCmd     = 6008;
    private static final int cmSaveCmd      = 6009;
    private static final int cmRestoreCmd   = 6010;
    private static final int cmEventViewCmd = 6011;
    private static final int cmChBackground = 6012;

    private static final int hcViewer          = 2;
    private static final int hcPuzzle          = 3;
    private static final int hcCalculator      = 4;
    private static final int hcCalendar        = 5;
    private static final int hcAsciiTable      = 6;
    private static final int hcSAbout          = 8;
    private static final int hcFOFileOpenDBox  = 31;
    private static final int hcFCChDirDBox     = 37;
    private static final int hcOMMouseDBox     = 38;
    private static final int hcOCColorsDBox    = 39;

    private static final String desktopFileName = "TVDEMO.DST";

    private final Path desktopStateFile = Paths.get(desktopFileName);
    private boolean reverseMouseButtons;
    private boolean helpInUse;
    private JtvHelpFile helpFile;

    public DemoApp(String[] args) {
        openCommandLineFiles(args);
    }

    @Override
    public JtvEvent getEvent(JtvEvent event) {
    	event = super.getEvent(event);
        applyMouseOptions(event);
        printEvent(event);
        return event;
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (event.getWhat() == evCommand) {
            switch (event.getMessage().getCommand()) {
                case cmHelp:
                    showHelp();
                    break;
                case cmAboutCmd:
                    aboutBox();
                    break;
                case cmPuzzleCmd:
                    insertWindow(newPuzzleWindow());
                    break;
                case cmCalendarCmd:
                    insertWindow(newCalendarWindow());
                    break;
                case cmAsciiCmd:
                    insertWindow(newAsciiWindow());
                    break;
                case cmCalcCmd:
                    calculator();
                    break;
                case cmOpenCmd:
                    openFile("*.*");
                    break;
                case cmChDirCmd:
                    changeDir();
                    break;
                case cmMouseCmd:
                    mouseOptions();
                    break;
                case cmColorCmd:
                    colors();
                    break;
                case cmChBackground:
                    changeBackground();
                    break;
                case cmSaveCmd:
                    saveDesktop();
                    break;
                case cmRestoreCmd:
                    retrieveDesktop();
                    break;
                case cmEventViewCmd:
                    toggleEventViewer();
                    break;
                default: return;
            }
            clearEvent(event);
        }
    }

    private void openCommandLineFiles(String[] args) {
        if (args == null) {
            return;
        }
        for (String arg : args) {
            if (arg == null || arg.isEmpty()) {
                continue;
            }
            String spec = arg;
            if (spec.endsWith("/") || spec.endsWith("\\")) {
                spec = spec + "*";
            }
            if (spec.indexOf('*') >= 0 || spec.indexOf('?') >= 0) {
                openFile(spec);
            } else {
                openEditor(spec);
            }
        }
    }

    private void applyMouseOptions(JtvEvent event) {
        if ((event.getWhat() & evMouse) == 0) {
            return;
        }
        if (reverseMouseButtons) {
            MouseEvent m = event.getMouse();
            int buttons = m.getButtons();
            int swapped = buttons & ~(mbLeftButton | mbRightButton);
            if ((buttons & mbLeftButton) != 0) {
                swapped |= mbRightButton;
            }
            if ((buttons & mbRightButton) != 0) {
                swapped |= mbLeftButton;
            }
            event.setMouse(new MouseEvent(m.getWhere(), m.getEventFlags(), m.getModifiers(), swapped, m.getWheel()));
        }
        if (event.getWhat() == evMouseDown && event.getMouse().getButtons() == mbRightButton) {
            event.setWhat(evNothing);
        }
    }

    private JtvView findEventViewer() {
        Object r = JtvView.message(
        		getDesktop(), 
        		EventCodes.evBroadcast,
        		EventViewerWindow.CM_FND_EVENT_VIEW,
        		null
        );
        return (r instanceof JtvView) ? (JtvView) r : null;
    }

    private void printEvent(JtvEvent event) {
        JtvView viewer = findEventViewer();
        if (viewer instanceof EventViewerWindow) {
            ((EventViewerWindow) viewer).print(event);
        }
    }

    private void toggleEventViewer() {
        JtvView v = findEventViewer();
        if (v instanceof EventViewerWindow) {
            ((EventViewerWindow) v).toggle();
        }
        else {
            insertWindow(new EventViewerWindow(getDesktop().getExtent(), 0x0F00));
        }
    }

    private JtvWindow newPuzzleWindow() {
        PuzzleWindow w = new PuzzleWindow();
        w.setHelpCtx(hcPuzzle);
        return w;
    }

    private JtvWindow newCalendarWindow() {
        CalendarWindow w = new CalendarWindow();
        w.setHelpCtx(hcCalendar);
        return w;
    }

    private JtvWindow newAsciiWindow() {
        AsciiChartWindow w = new AsciiChartWindow();
        w.setHelpCtx(hcAsciiTable);
        return w;
    }

    private void calculator() {
        Calculator calc = new Calculator();
        calc.setHelpCtx(hcCalculator);
        insertWindow(calc);
//        executeDialog(calc, null);
    }

    private void openEditor(String fileName) {
        String name = fileName != null ? fileName : "";
        JtvRect r = getDesktop().getExtent();
        JtvEditWindow window = new JtvEditWindow(r, name, 0);
        window.setHelpCtx(hcViewer);
        window.getEditor().setReadOnly(true);
        insertWindow(window);
    }

    private void openFile(String fileSpec) {
        JtvFileDialog d = new JtvFileDialog(fileSpec, "Open a File", "~N~ame", fdOpenButton, 100);
        d.setHelpCtx(hcFOFileOpenDBox);
        if (validView(d) != null) {
            if (getDesktop().execView(d) != cmCancel) {
                StringBuilder fileName = new StringBuilder();
                d.getFileName(fileName);
                openEditor(fileName.toString());
            }
            destroy(d);
        }
    }

    private void changeDir() {
        JtvView d = validView(new JtvChangeDirDialog(0, 101));
        if (d != null) {
            d.setHelpCtx(hcFCChDirDBox);
            getDesktop().execView(d);
            destroy(d);
        }
    }

    private void mouseOptions() {
        int oldDelay = EventQueue.doubleDelay;

        JtvDialog d = new JtvDialog(new JtvRect(0, 0, 34, 12), "Mouse Options") {
            JtvScrollBar scrollBar;

            {
                setOptions(getOptions() | ofCentered);

                scrollBar = new JtvScrollBar(new JtvRect(3, 4, 30, 5));
                scrollBar.setParams(
                    Math.max(1, Math.min(20, EventQueue.doubleDelay / 55)),
                    1, 20, 20, 1);
                scrollBar.setOptions(scrollBar.getOptions() | ofSelectable);
                insert(scrollBar);

                insert(new JtvLabel(new JtvRect(2, 2, 22, 3), "~M~ouse double click", scrollBar));
                insert(new TClickTester(new JtvRect(3, 3, 30, 4), "Fast       Medium      Slow"));
            }

            @Override
                public void handleEvent(JtvEvent event) {
                    super.handleEvent(event);
                    if (event.getWhat() == evBroadcast && event.getMessage().getCommand() == cmScrollBarChanged) {
                    EventQueue.doubleDelay = scrollBar.getValue() * 55;
                    clearEvent(event);
                } else if (event.getWhat() == evCommand &&
                           event.getMessage().getCommand() == org.viktor44.jtvision.core.CommandCodes.cmCancel) {
                    EventQueue.doubleDelay = oldDelay;
                }
            }
        };

        JtvCheckBoxes boxes = new JtvCheckBoxes(new JtvRect(3, 6, 30, 7),
            new String[] {"~R~everse mouse buttons"});
        boxes.setValue(reverseMouseButtons ? 1 : 0);
        d.insert(boxes);

        d.insert(new JtvButton(new JtvRect(9, 9, 19, 11), "O~K~", cmOK, bfDefault));
        d.insert(new JtvButton(new JtvRect(21, 9, 31, 11), "Cancel", cmCancel, bfNormal));

        d.setHelpCtx(hcOMMouseDBox);

        if (getDesktop().execView(d) != cmCancel) {
            reverseMouseButtons = (boxes.getValue() & 1) != 0;
        } else {
            EventQueue.doubleDelay = oldDelay;
        }
        destroy(d);
    }

    private static List<JtvColorGroup> appendGroup(List<JtvColorGroup> list, String name, String[] itemNames, int[] indexes) {
        JtvColorGroup group = new JtvColorGroup(name);
        for (int i = 0; i < itemNames.length && i < indexes.length; i++) {
            JtvColorGroup.appendItem(group, new JtvColorItem(itemNames[i], indexes[i]));
        }
        list.add(group);
        return list;
    }

    private static List<JtvColorGroup> buildColorGroups() {
        List<JtvColorGroup> groups = new ArrayList<>();
        groups = appendGroup(groups, "Desktop",
            new String[] {"Color"},
            new int[] {1});

        groups = appendGroup(groups, "Menus",
            new String[] {"Normal", "Disabled", "Shortcut", "Selected", "Selected disabled", "Shortcut selected"},
            new int[] {2, 3, 4, 5, 6, 7});

        groups = appendGroup(groups, "Dialogs/Calc",
            new String[] {
                "Frame/background", "Frame icons", "Scroll bar page", "Scroll bar icons", "Static text",
                "Label normal", "Label selected", "Label shortcut",
                "Button normal", "Button default", "Button selected", "Button disabled", "Button shortcut", "Button shadow",
                "Cluster normal", "Cluster selected", "Cluster shortcut",
                "Input normal", "Input selected", "Input arrow",
                "History button", "History sides", "History bar page", "History bar icons",
                "List normal", "List focused", "List selected", "List divider",
                "Information pane"
            },
            new int[] {
                33, 34, 35, 36, 37,
                38, 39, 40,
                41, 42, 43, 44, 45, 46,
                47, 48, 49,
                50, 51, 52,
                53, 54, 55, 56,
                57, 58, 59, 60,
                61
            });

        groups = appendGroup(groups, "Viewer",
            new String[] {"Frame passive", "Frame active", "Frame icons", "Scroll bar page", "Scroll bar icons", "Text"},
            new int[] {8, 9, 10, 11, 12, 13});

        groups = appendGroup(groups, "Puzzle",
            new String[] {"Frame passive", "Frame active", "Frame icons", "Scroll bar page", "Scroll bar icons", "Normal text", "Highlighted text"},
            new int[] {8, 9, 10, 11, 12, 13, 14});

        groups = appendGroup(groups, "Calendar",
            new String[] {"Frame passive", "Frame active", "Frame icons", "Scroll bar page", "Scroll bar icons", "Normal text", "Current day"},
            new int[] {16, 17, 18, 19, 20, 21, 22});

        groups = appendGroup(groups, "Ascii table",
            new String[] {"Frame passive", "Frame active", "Frame icons", "Scroll bar page", "Scroll bar icons", "Text"},
            new int[] {24, 25, 26, 27, 28, 29});

        return groups;
    }

    private void colors() {
        JtvColorDialog d = new JtvColorDialog(null, buildColorGroups());
        d.setHelpCtx(hcOCColorsDBox);
        if (validView(d) != null) {
            JtvPalette palette = getPalette();
            d.setDataFrom(palette);
            if (getDesktop().execView(d) != org.viktor44.jtvision.core.CommandCodes.cmCancel) {
                d.getDataTo(palette);
                JtvProgram.setAppPalette(palette);
                setScreenMode(org.viktor44.jtvision.platform.Screen.screenMode);
            }
            destroy(d);
        }
    }

    private void changeBackground() {
        ChangeBackgroundDialog d = (ChangeBackgroundDialog) validView(new ChangeBackgroundDialog(getDesktop().getBackground()));
        if (d != null) {
        	getDesktop().execView(d);
            destroy(d);
        }
    }

    private String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\t", "\\t");
    }

    private String unesc(String s) {
        StringBuilder out = new StringBuilder();
        boolean slash = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (slash) {
                if (c == 't') {
                    out.append('\t');
                } else {
                    out.append(c);
                }
                slash = false;
            } else if (c == '\\') {
                slash = true;
            } else {
                out.append(c);
            }
        }
        if (slash) {
            out.append('\\');
        }
        return out.toString();
    }

    private String snapshotWindow(JtvWindow w) {
        String type;
        String arg = "";
        int flag = 0;

        if (w instanceof PuzzleWindow) {
            type = "puzzle";
        } else if (w instanceof CalendarWindow) {
            type = "calendar";
        } else if (w instanceof AsciiChartWindow) {
            type = "ascii";
        } else if (w instanceof EventViewerWindow) {
            type = "event";
            flag = ((EventViewerWindow) w).isStopped() ? 1 : 0;
        } else if (w instanceof JtvEditWindow) {
            type = "editor";
            JtvEditWindow editWindow = (JtvEditWindow) w;
            String fileName = editWindow.getEditor() != null ? editWindow.getEditor().getFileName() : "";
            arg = fileName != null ? fileName : "";
        } else {
            return null;
        }

        JtvRect b = w.getBounds();
        int width = b.getB().getX() - b.getA().getX();
        int height = b.getB().getY() - b.getA().getY();
        return "window\t" + type + "\t" + esc(arg) + "\t"
            + b.getA().getX() + "\t" + b.getA().getY() + "\t" + width + "\t" + height + "\t" + flag;
    }

    private void saveDesktop() {
        List<String> lines = new ArrayList<>();
        lines.add("jtvision-tvdemo-1");
        char pattern = JtvDesktop.defaultBkgrnd;
        pattern = getDesktop().getBackground().getPattern();
        lines.add("pattern=" + (int) pattern);
        lines.add("reverseMouse=" + (reverseMouseButtons ? 1 : 0));

    	getDesktop().forEach(v -> {
            if (v instanceof JtvWindow) {
                String line = snapshotWindow((JtvWindow) v);
                if (line != null) {
                    lines.add(line);
                }
            }
        });

        try {
            Files.write(desktopStateFile, lines, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            MessageBox.messageBox(
            		"Could not create " + desktopFileName + ": " + e.getMessage(),
            		mfError | mfOKButton
            );
        }
    }

    private int parseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private JtvWindow createSnapshotWindow(String type, String arg, int flag) {
        if ("puzzle".equals(type)) {
            return newPuzzleWindow();
        }
        if ("calendar".equals(type)) {
            return newCalendarWindow();
        }
        if ("ascii".equals(type)) {
            return newAsciiWindow();
        }
        if ("event".equals(type)) {
            EventViewerWindow w = new EventViewerWindow(getDesktop().getExtent(), 0x0F00);
            if (flag != 0) {
                w.toggle();
            }
            return w;
        }
        if ("editor".equals(type)) {
            JtvEditWindow w = new JtvEditWindow(getDesktop().getExtent(), arg, 0);
            w.setHelpCtx(hcViewer);
            w.getEditor().setReadOnly(true);
            return w;
        }
        return null;
    }

    private void closeDesktopWindows() {
        List<JtvWindow> windows = new ArrayList<>();
        getDesktop().forEach(v -> {
            if (v instanceof JtvWindow) {
                windows.add((JtvWindow) v);
            }
        });
        for (JtvWindow w : windows) {
        	getDesktop().destroy(w);
        }
    }

    private void retrieveDesktop() {
        if (!Files.exists(desktopStateFile)) {
            MessageBox.messageBox("Could not find desktop file", mfError | mfOKButton);
            return;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(desktopStateFile, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            MessageBox.messageBox("Error reading desktop file", mfError | mfOKButton);
            return;
        }
        if (lines.isEmpty() || !"jtvision-tvdemo-1".equals(lines.get(0))) {
            MessageBox.messageBox("Desktop file format is not supported", mfError | mfOKButton);
            return;
        }

        closeDesktopWindows();

        for (String line : lines) {
            if (line.startsWith("pattern=")) {
                int code = parseInt(line.substring("pattern=".length()), (int) JtvDesktop.defaultBkgrnd);
            	getDesktop().getBackground().setPattern((char) code);
            	getDesktop().getBackground().drawView();
            }
            else if (line.startsWith("reverseMouse=")) {
                reverseMouseButtons = parseInt(line.substring("reverseMouse=".length()), 0) != 0;
            }
            else if (line.startsWith("window\t")) {
                String[] parts = line.split("\t", -1);
                if (parts.length < 8) {
                    continue;
                }
                String type = parts[1];
                String arg = unesc(parts[2]);
                int x = parseInt(parts[3], 1);
                int y = parseInt(parts[4], 1);
                int w = Math.max(10, parseInt(parts[5], 30));
                int h = Math.max(5, parseInt(parts[6], 10));
                int flag = parseInt(parts[7], 0);

                JtvWindow window = createSnapshotWindow(type, arg, flag);
                if (window != null) {
                    window.locate(new JtvRect(x, y, x + w, y + h));
                    insertWindow(window);
                }
            }
        }
    }

    private Path helpPath() {
        Path[] candidates = new Path[] {
            Paths.get("demohelp.hlp"),
            Paths.get("examples", "demohelp.hlp")
        };
        for (Path p : candidates) {
            if (Files.exists(p)) {
                return p;
            }
        }
        return candidates[0];
    }

    private void showHelp() {
        if (helpInUse) {
            return;
        }
        helpInUse = true;
        try {
            if (helpFile == null) {
                helpFile = JtvHelpFile.load(helpPath());
            }
            JtvWindow w = new JtvHelpWindow(helpFile, getHelpCtx());
            if (validView(w) != null) {
                execView(w);
                destroy(w);
            }
        } catch (IOException e) {
            MessageBox.messageBox("Could not open help file", mfError | mfOKButton);
        } finally {
            helpInUse = false;
        }
    }

    private void aboutBox() {
        JtvDialog d = new JtvDialog(new JtvRect(0, 0, 40, 11), "About");
        d.insert(new JtvStaticText(new JtvRect(3, 2, 37, 8),
            "\n\n\003JT Vision Demo"));
        d.insert(new JtvButton(new JtvRect(14, 8, 26, 10), " OK", cmOK, bfDefault));
        d.setOptions(d.getOptions() | ofCentered);
        d.setHelpCtx(hcSAbout);
        executeDialog(d, null);
    }

    @Override
    protected JtvMenuBar initMenuBar(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getA().getY(), r.getB().getX(), r.getA().getY() + 1);
        return new JtvMenuBar(r)
		        .addItem(
		        		new JtvSubMenu("~\u2261~")
		                        .addItem(new JtvMenuItem("~A~bout...", cmAboutCmd, JtvKey.kbAltA))
		                        .addSeparator()
		                        .addItem(new JtvMenuItem("~P~uzzle", cmPuzzleCmd))
		                        .addItem(new JtvMenuItem("Ca~l~endar", cmCalendarCmd))
		                        .addItem(new JtvMenuItem("Ascii ~T~able", cmAsciiCmd))
		                        .addItem(new JtvMenuItem("~C~alculator", cmCalcCmd))
		                        .addItem(new JtvMenuItem("~E~vent Viewer", cmEventViewCmd, JtvKey.kbAlt0, 0, "Alt+0"))
		        )
		        .addItem(
		        		new JtvSubMenu("~F~ile", JtvKey.kbAltF)
		                        .addItem(new JtvMenuItem("~O~pen...", cmOpenCmd, JtvKey.kbF3, 0, "F3"))
		                        .addItem(new JtvMenuItem("~C~hange Dir...", cmChDirCmd))
		                        .addSeparator()
		                        .addItem(new JtvMenuItem("E~x~it", cmQuit, JtvKey.kbCtrlQ, 0, "Ctrl+Q"))
		        )
		        .addItem(
		        		new JtvSubMenu("~W~indow", JtvKey.kbAltW)
		                        .addItem(new JtvMenuItem("~R~esize/move", cmResize, JtvKey.kbCtrlF5, 0, "Ctrl+F5"))
		                        .addItem(new JtvMenuItem("~Z~oom", cmZoom, JtvKey.kbF5, 0, "F5"))
		                        .addItem(new JtvMenuItem("~N~ext", org.viktor44.jtvision.core.CommandCodes.cmNext, JtvKey.kbF6, 0, "F6"))
		                        .addItem(new JtvMenuItem("~C~lose", cmClose, JtvKey.kbAltF3, 0, "Alt+F3"))
		                        .addItem(new JtvMenuItem("~T~ile", cmTile))
		                        .addItem(new JtvMenuItem("C~a~scade", cmCascade))
		        )
		        .addItem(
		        		new JtvSubMenu("~O~ptions", JtvKey.kbAltO)
		                        .addItem(new JtvMenuItem("~M~ouse...", cmMouseCmd))
		                        .addItem(new JtvMenuItem("~C~olors...", cmColorCmd))
		                        .addItem(new JtvMenuItem("~B~ackground...", cmChBackground))
		                        .addItem(
		                        		new JtvSubMenu("~D~esktop")
		        		                        .addItem(new JtvMenuItem("~S~ave desktop", cmSaveCmd))
		        		                        .addItem(new JtvMenuItem("~R~etrieve desktop", cmRestoreCmd))
		                        )                
		        );
    }

    @Override
    protected JtvStatusLine initStatusLine(JtvRect r) {
        r = new JtvRect(r.getA().getX(), r.getB().getY() - 1, r.getB().getX(), r.getB().getY());
        return new JtvStatusLine(r,
            new JtvStatusDef(0, 0xFFFF, null)
                .addItem(new JtvStatusItem("~F1~ Help", JtvKey.kbF1, cmHelp))
                .addItem(new JtvStatusItem("~Ctrl+Q~ Exit", JtvKey.kbCtrlQ, cmQuit))
                .addItem(new JtvStatusItem(null, JtvKey.kbAltF3, cmClose))
                .addItem(new JtvStatusItem(null, JtvKey.kbF10, cmMenu))
                .addItem(new JtvStatusItem(null, JtvKey.kbF5, cmZoom))
                .addItem(new JtvStatusItem(null, JtvKey.kbCtrlF5, cmResize)));
    }

    public static void main(String[] args) {
        new DemoApp(args).run();
    }

    private static class TClickTester extends JtvStaticText {

        private static final JtvPalette cpMousePalette = new JtvPalette(new int[] {7, 8});

        private boolean clicked;

        TClickTester(JtvRect r, String aText) {
            super(r, aText);
        }

        @Override
        public JtvPalette getPalette() {
            return cpMousePalette;
        }

        @Override
        public void handleEvent(JtvEvent event) {
            super.handleEvent(event);
            if (event.getWhat() == evMouseDown) {
                if ((event.getMouse().getEventFlags() & meDoubleClick) != 0) {
                    clicked = !clicked;
                    drawView();
                }
                clearEvent(event);
            }
        }

        @Override
        public void draw() {
            JtvColorAttr color = getColor(clicked ? 2 : 1);
            String s = getText();
            JtvDrawBuffer buf = new JtvDrawBuffer();
            buf.moveChar(0, ' ', color, getSize().getX());
            buf.moveStr(0, s, color);
            writeLine(0, 0, getSize().getX(), 1, buf);
        }
    }
}
