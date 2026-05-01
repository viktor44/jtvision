/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.dir;

import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.gfFixed;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowLoY;
import static org.viktor44.jtvision.core.ViewFlags.ofFramed;
import static org.viktor44.jtvision.core.ViewFlags.wnNoNumber;

import java.io.File;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvNode;
import org.viktor44.jtvision.views.JtvScrollBar;
import org.viktor44.jtvision.views.JtvWindow;

/**
 * Window that hosts the directory outline and file pane side by side.
 */
public class DirWindow extends JtvWindow {

    private final DirOutline outline;
    private final FilePane filePane;

    public DirWindow(String drive) {
        super(new JtvRect(1, 1, 76, 21), drive, wnNoNumber);

        JtvScrollBar filesVsb = new JtvScrollBar(new JtvRect(74, 1, 75, 15));
        JtvScrollBar filesHsb = new JtvScrollBar(new JtvRect(22, 15, 73, 16));
        filePane = new FilePane(new JtvRect(21, 1, 74, 15), filesHsb, filesVsb);
        filePane.setOptions(filePane.getOptions() | ofFramed);
        filePane.setGrowMode(gfGrowHiY | gfGrowHiX | gfFixed);
        insert(filesHsb);
        insert(filesVsb);
        insert(filePane);

        JtvScrollBar treeVsb = new JtvScrollBar(new JtvRect(20, 1, 21, 19));
        JtvScrollBar treeHsb = new JtvScrollBar(new JtvRect(2, 19, 19, 20));
        JtvNode root = DirNode.buildTree(new File(drive).getAbsoluteFile());
        outline = new DirOutline(new JtvRect(1, 1, 20, 19), treeHsb, treeVsb, root);
        outline.setOptions(outline.getOptions() | ofFramed);
        outline.setGrowMode(gfGrowHiY | gfFixed);
        treeVsb.setGrowMode(gfGrowHiY);
        treeHsb.setGrowMode(gfGrowHiY | gfGrowLoY);
        insert(treeHsb);
        insert(treeVsb);
        insert(outline);

        filePane.newDir(outline.getCurrentPath());
    }

    @Override
    public void handleEvent(JtvEvent event) {
        if (event.getWhat() == evCommand && event.getMessage().getCommand() == DirOutline.cmNewDirFocused) {
            String path = outline.getCurrentPath();
            filePane.newDir(path);
            title = path;
            if (frame != null) frame.drawView();
            clearEvent(event);
        }
        super.handleEvent(event);
    }

    @Override
    public JtvPoint getMinimumSize() {
        return new JtvPoint(40, 10);
    }
}
