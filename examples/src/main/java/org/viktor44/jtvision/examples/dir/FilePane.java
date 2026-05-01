/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.dir;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvScrollBar;
import org.viktor44.jtvision.views.JtvScroller;

/**
 * Scrollable file listing for the current directory.
 */
public class FilePane extends JtvScroller {

    private String[] rows = new String[0];

    public FilePane(JtvRect bounds, JtvScrollBar hsb, JtvScrollBar vsb) {
        super(bounds, hsb, vsb);
    }

    public void newDir(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles(File::isFile);
        if (files == null) files = new File[0];
        Arrays.sort(files, Comparator.comparing(f -> f.getName().toLowerCase()));

        SimpleDateFormat fmt = new SimpleDateFormat("yy-MM-dd  HH:mm");
        rows = new String[files.length];
        int maxWidth = 0;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            String name = f.getName();
            if (name.length() > 18) name = name.substring(0, 18);
            else name = padRight(name, 18);
            String row = String.format("%s  %8d  %s  %c%c",
                name, f.length(), fmt.format(new Date(f.lastModified())),
                f.canWrite() ? 'w' : '.', f.isHidden() ? 'h' : '.');
            rows[i] = row;
            if (row.length() > maxWidth) maxWidth = row.length();
        }

        if (rows.length == 0)
            setLimit(1, 1);
        else
            setLimit(maxWidth + 2, rows.length);
        drawView();
    }

    private static String padRight(String s, int width) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }

    @Override
    public void draw() {
        JtvDrawBuffer buf = new JtvDrawBuffer();
        JtvColorAttr color = getColor(1);
        for (int i = 0; i < getSize().getY(); i++) {
            buf.moveChar(0, ' ', color, getSize().getX());
            if (rows.length == 0 && i == 0) {
                buf.moveStr(2, "<no files>", color);
            } else if (i + getDelta().getY() < rows.length) {
                buf.moveStr(2, rows[i + getDelta().getY()], color, getSize().getX(), getDelta().getX());
            }
            writeLine(0, i, getSize().getX(), 1, buf);
        }
    }
}
