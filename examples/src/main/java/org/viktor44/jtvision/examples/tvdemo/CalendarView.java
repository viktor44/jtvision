/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.tvdemo;

import static org.viktor44.jtvision.core.EventCodes.evKeyboard;
import static org.viktor44.jtvision.core.EventCodes.evMouseAuto;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.sfSelected;

import java.time.LocalDate;
import java.time.YearMonth;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvView;

/**
 * Monthly calendar view. Today's date is highlighted; +/- or up/down arrows
 * (or the up/down arrow indicators on the title bar) change the month.
 */
public class CalendarView extends JtvView {

    private static final String[] MONTH_NAMES = {
        "", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    private static final char MONTH_NEXT_SYMBOL = '\u25B2';
    private static final char MONTH_PREV_SYMBOL = '\u25BC';

    private int month;
    private int year;
    private final int curDay;
    private final int curMonth;
    private final int curYear;

    public CalendarView(JtvRect r) {
        super(r);
        options |= ofSelectable;
        eventMask |= evMouseAuto;
        LocalDate today = LocalDate.now();
        curDay = today.getDayOfMonth();
        curMonth = today.getMonthValue();
        curYear = today.getYear();
        month = curMonth;
        year = curYear;
    }

    @Override
    public void draw() {
        JtvDrawBuffer buf = new JtvDrawBuffer();
        JtvColorAttr color = getColor(6);
        JtvColorAttr boldColor = getColor(7);

        buf.moveChar(0, ' ', color, 22);
        String title = String.format("%9s %4d %c  %c ",
            MONTH_NAMES[month], year, MONTH_NEXT_SYMBOL, MONTH_PREV_SYMBOL);
        buf.moveStr(0, title, color);
        writeLine(0, 0, 22, 1, buf);

        buf.moveChar(0, ' ', color, 22);
        buf.moveStr(0, "Su Mo Tu We Th Fr Sa", color);
        writeLine(0, 1, 22, 1, buf);

        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();
        // Sunday = 0; java DayOfWeek: Monday=1..Sunday=7
        int firstDow = ym.atDay(1).getDayOfWeek().getValue() % 7;
        int current = 1 - firstDow;

        for (int row = 1; row <= 6; row++) {
            buf.moveChar(0, ' ', color, 22);
            for (int col = 0; col < 7; col++) {
                if (current < 1 || current > daysInMonth) {
                    buf.moveStr(col * 3, "   ", color);
                } else {
                    String s = String.format("%2d", current);
                    JtvColorAttr c = (year == curYear && month == curMonth && current == curDay)
                        ? boldColor : color;
                    buf.moveStr(col * 3, s, c);
                }
                current++;
            }
            writeLine(0, row + 1, 22, 1, buf);
        }
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if ((state & sfSelected) == 0) return;

        if ((event.getWhat() & (evMouseDown | evMouseAuto)) != 0) {
            JtvPoint p = makeLocal(event.getMouse().getWhere());
            if (p.getX() == 15 && p.getY() == 0) { addMonth(1); drawView(); }
            else if (p.getX() == 18 && p.getY() == 0) { addMonth(-1); drawView(); }
        } else if ((event.getWhat() & evKeyboard) != 0) {
            char ch = event.getKeyDown().getKeyChar();
            int kc = event.getKeyDown().getKeyCode();
            if (ch == '+' || kc == org.viktor44.jtvision.core.JtvKey.kbDown) {
                addMonth(1); drawView();
            } else if (ch == '-' || kc == org.viktor44.jtvision.core.JtvKey.kbUp) {
                addMonth(-1); drawView();
            }
        }
    }

    private void addMonth(int delta) {
        month += delta;
        while (month > 12) { month -= 12; year++; }
        while (month < 1)  { month += 12; year--; }
    }
}
