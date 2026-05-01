/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.tvdemo;

import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.EventCodes.evKeyboard;
import static org.viktor44.jtvision.core.EventCodes.evMouse;
import static org.viktor44.jtvision.core.EventCodes.evNothing;
import static org.viktor44.jtvision.core.ViewFlags.gfFixed;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiX;
import static org.viktor44.jtvision.core.ViewFlags.gfGrowHiY;
import static org.viktor44.jtvision.core.ViewFlags.sbHandleKeyboard;
import static org.viktor44.jtvision.core.ViewFlags.sbVertical;
import static org.viktor44.jtvision.core.ViewFlags.wnNoNumber;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvScrollBar;
import org.viktor44.jtvision.views.JtvScroller;
import org.viktor44.jtvision.views.JtvWindow;

/**
 * Scrollable event log. The application's {@code getEvent()} calls
 * {@link #print(JtvEvent)} for every event it pulls; the viewer appends a
 * human-readable dump until the buffer fills, then rolls oldest lines off.
 * Port of evntview.cpp; TTerminal is replaced with a ring-buffer scroller.
 */
public class EventViewerWindow extends JtvWindow {

    public static final int CM_FND_EVENT_VIEW = 6200;

    private static final String[] TITLES = {"Event Viewer", "Event Viewer (Stopped)"};

    private boolean stopped;
    private long eventCount;
    private final EventLog log;

    public EventViewerWindow(JtvRect bounds, int bufSize) {
        super(bounds, TITLES[0], wnNoNumber);
        eventMask |= evBroadcast;
        JtvScrollBar vsb = standardScrollBar(sbVertical | sbHandleKeyboard);
        log = new EventLog(getExtent().grow(-1, -1), vsb, bufSize);
        log.setGrowMode(gfGrowHiX | gfGrowHiY | gfFixed);
        insert(log);
    }

    public void toggle() {
        stopped = !stopped;
        title = TITLES[stopped ? 1 : 0];
        if (frame != null) frame.drawView();
    }

    public boolean isStopped() {
        return stopped;
    }

    public void print(JtvEvent ev) {
        if (ev.getWhat() == evNothing || stopped) return;
        eventCount++;
        log.append("Received event #" + eventCount);
        formatEvent(ev, log);
        log.commit();
    }

    @Override
    public void handleEvent(JtvEvent ev) {
        super.handleEvent(ev);
        if (ev.getWhat() == evBroadcast && ev.getMessage().getCommand() == CM_FND_EVENT_VIEW) {
            clearEvent(ev);
        }
    }

    private static void formatEvent(JtvEvent ev, EventLog out) {
        out.append("TEvent {");
        out.append("  .what = " + whatName(ev.getWhat()));
        if ((ev.getWhat() & evMouse) != 0) {
            out.append("  .mouse = {");
            out.append("    .where = (" + ev.getMouse().getWhere().getX() + ", " + ev.getMouse().getWhere().getY() + ")");
            out.append("    .buttons = " + hex2(ev.getMouse().getButtons()));
            out.append("    .eventFlags = " + hex2(ev.getMouse().getEventFlags()));
            out.append("    .wheel = " + hex2(ev.getMouse().getWheel()));
            out.append("  }");
        }
        if ((ev.getWhat() & evKeyboard) != 0) {
            int code = ev.getKeyDown().getKeyCode();
            int mods = ev.getKeyDown().getModifiers();
            char ch = ev.getKeyDown().getKeyChar();
            String chStr = (ch >= 0x20 && ch < 0x7F)
                ? " ('" + ch + "')" : "";
            out.append("  .keyDown = {");
            out.append("    .keyCode = " + hex4(code));
            out.append("    .modifiers = " + hex4(mods));
            out.append("    .keyChar = " + (int) ch + chStr);
            out.append("  }");
        }
        if ((ev.getWhat() & (evCommand | evBroadcast)) != 0) {
            out.append("  .message = {");
            out.append("    .command = " + ev.getMessage().getCommand());
            out.append("    .infoPtr = " + ev.getMessage().getInfoPtr());
            out.append("  }");
        }
        out.append("}");
    }

    private static String whatName(int what) {
        switch (what) {
            case evMouse: return "evMouse";
            case 0x0001: return "evMouseDown";
            case 0x0002: return "evMouseUp";
            case 0x0004: return "evMouseMove";
            case 0x0008: return "evMouseAuto";
            case 0x0010: return "evKeyDown";
            case 0x0020: return "evMouseWheel";
            case 0x0100: return "evCommand";
            case 0x0200: return "evBroadcast";
            default: return hex4(what);
        }
    }

    private static String hex2(int v) { return String.format("0x%02X", v & 0xFF); }
    private static String hex4(int v) { return String.format("0x%04X", v & 0xFFFF); }

    /** Ring-buffer scroller that renders the event log as scrollable text. */
    static class EventLog extends JtvScroller {
        private final Deque<String> lines = new ArrayDeque<>();
        private final int bufSize;
        private final List<String> pending = new ArrayList<>();
        private int maxWidth;

        EventLog(JtvRect r, JtvScrollBar vsb, int bufSize) {
            super(r, null, vsb);
            this.bufSize = Math.max(bufSize, 16);
            setLimit(0, 0);
        }

        void append(String s) { pending.add(s); }

        void commit() {
            if (pending.isEmpty()) return;
            for (String s : pending) {
                lines.addLast(s);
                if (s.length() > maxWidth) maxWidth = s.length();
                while (lines.size() > bufSize) lines.removeFirst();
            }
            pending.clear();
            setLimit(maxWidth, lines.size());
            if (vScrollBar != null)
                vScrollBar.setValue(Math.max(0, lines.size() - size.getY()));
            drawView();
        }

        @Override
        public void draw() {
            JtvDrawBuffer buf = new JtvDrawBuffer();
            JtvColorAttr color = getColor(1);
            Object[] snapshot = lines.toArray();
            for (int i = 0; i < size.getY(); i++) {
                buf.moveChar(0, ' ', color, size.getX());
                int idx = i + delta.getY();
                if (idx >= 0 && idx < snapshot.length) {
                    String s = (String) snapshot[idx];
                    if (delta.getX() < s.length())
                        buf.moveStr(0, s.substring(delta.getX()), color);
                }
                writeLine(0, i, size.getX(), 1, buf);
            }
        }
    }
}
