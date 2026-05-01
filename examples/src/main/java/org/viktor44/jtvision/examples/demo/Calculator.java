/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.demo;

import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evKeyboard;
import static org.viktor44.jtvision.core.ViewFlags.bfBroadcast;
import static org.viktor44.jtvision.core.ViewFlags.bfNormal;
import static org.viktor44.jtvision.core.ViewFlags.ofFirstClick;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvButton;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.views.JtvView;

/**
 * A 4x5 grid of broadcast buttons drives a
 * {@link CalcDisplay}; the display tracks operand/operator state and
 * updates on each button press or typed key.
 */
public class Calculator extends JtvDialog {

    public static final int cmCalcButton = 6100;

    private static final int DISPLAYLEN = 25;

    private static final String[] KEY_CHAR = {
        "C", "\u2190", "%", "\u00B1",
        "7", "8", "9", "/",
        "4", "5", "6", "*",
        "1", "2", "3", "-",
        "0", ".", "=", "+"
    };

    public Calculator() {
        super(new JtvRect(5, 3, 29, 18), "Calculator");
        options |= ofFirstClick;

        for (int i = 0; i < 20; i++) {
            int x = (i % 4) * 5 + 2;
            int y = (i / 4) * 2 + 4;
            JtvRect r = new JtvRect(x, y, x + 5, y + 2);
            JtvButton b = new JtvButton(r, KEY_CHAR[i], cmCalcButton, bfNormal | bfBroadcast);
            b.setOptions(b.getOptions() & ~ofSelectable);
            insert(b);
        }
        insert(new CalcDisplay(new JtvRect(3, 2, 21, 3)));
    }

    private enum Status { FIRST, VALID, ERROR }

    /** Numeric readout; consumes keyboard input and {@code cmCalcButton} broadcasts. */
    public static class CalcDisplay extends JtvView {

        private static final JtvPalette cpCalc = new JtvPalette(new int[] {0x13});

        private Status status = Status.FIRST;
        private StringBuilder number = new StringBuilder("0");
        private char sign = ' ';
        private char operate = '=';
        private double operand = 0;

        public CalcDisplay(JtvRect r) {
            super(r);
            options |= ofSelectable;
            eventMask = evKeyboard | evBroadcast;
        }

        @Override
        public JtvPalette getPalette() {
        	return cpCalc;
        }

        @Override
        public void handleEvent(JtvEvent event) {
            super.handleEvent(event);
            if ((event.getWhat() & evKeyboard) != 0) {
                calcKey(event.getKeyDown().getKeyChar());
                clearEvent(event);
            } else if (event.getWhat() == evBroadcast) {
                if (event.getMessage().getCommand() == cmCalcButton &&
                    event.getMessage().getInfoPtr() instanceof JtvButton) {
                    calcKey(((JtvButton) event.getMessage().getInfoPtr()).getTitle().charAt(0));
                    clearEvent(event);
                }
            }
        }

        @Override
        public void draw() {
            JtvColorAttr color = getColor(1);
            JtvDrawBuffer buf = new JtvDrawBuffer();
            int i = getSize().getX() - number.length() - 2;
            buf.moveChar(0, ' ', color, getSize().getX());
            buf.moveChar(i, sign, color, 1);
            buf.moveStr(i + 1, number.toString(), color);
            writeLine(0, 0, getSize().getX(), 1, buf);
        }

        private double getDisplay() {
            try { return Double.parseDouble(number.toString()); }
            catch (NumberFormatException e) { return 0; }
        }

        private void error() {
            status = Status.ERROR;
            number.setLength(0);
            number.append("Error");
            sign = ' ';
        }

        private void clear() {
            status = Status.FIRST;
            number.setLength(0);
            number.append("0");
            sign = ' ';
            operate = '=';
            operand = 0;
        }

        private void setDisplay(double r) {
            if (r < 0) { sign = '-'; r = -r; }
            else sign = ' ';
            String s = format(r);
            if (s.length() > DISPLAYLEN)
                error();
            else {
                number.setLength(0);
                number.append(s);
            }
        }

        private static String format(double r) {
            if (r == Math.floor(r) && !Double.isInfinite(r) && Math.abs(r) < 1e15)
                return Long.toString((long) r);
            String s = Double.toString(r);
            return s;
        }

        private void checkFirst() {
            if (status == Status.FIRST) {
                status = Status.VALID;
                number.setLength(0);
                number.append("0");
                sign = ' ';
            }
        }

        private void calcKey(char key) {
            key = Character.toUpperCase(key);
            if (status == Status.ERROR && key != 'C')
                key = ' ';

            switch (key) {
                case '0': 
                case '1': 
                case '2': 
                case '3': 
                case '4':
                case '5': 
                case '6':
                case '7':
                case '8':
                case '9':
                    checkFirst();
                    if (number.length() < 15) {
                        if (number.toString().equals("0"))
                            number.setLength(0);
                        number.append(key);
                    }
                    break;
                case '.':
                    checkFirst();
                    if (number.indexOf(".") < 0)
                        number.append('.');
                    break;
                case 8:
                case 27:
                case '\u2190':
                    checkFirst();
                    if (number.length() == 1) {
                        number.setLength(0);
                        number.append('0');
                    } else {
                        number.setLength(number.length() - 1);
                    }
                    break;
                case '_':
                case '\u00B1':
                    sign = (sign == ' ') ? '-' : ' ';
                    break;
                case '+': 
                case '-': 
                case '*': 
                case '/':
                case '=': 
                case '%': 
                case 13:
                    if (status == Status.VALID) {
                        status = Status.FIRST;
                        double r = getDisplay() * (sign == '-' ? -1.0 : 1.0);
                        if (key == '%') {
                            if (operate == '+' || operate == '-')
                                r = (operand * r) / 100;
                            else
                                r /= 100;
                        }
                        switch (operate) {
                            case '+': setDisplay(operand + r); break;
                            case '-': setDisplay(operand - r); break;
                            case '*': setDisplay(operand * r); break;
                            case '/':
                                if (r == 0) error();
                                else setDisplay(operand / r);
                                break;
                        }
                    }
                    operate = key;
                    operand = getDisplay() * (sign == '-' ? -1.0 : 1.0);
                    break;
                case 'C':
                    clear();
                    break;
            }
            drawView();
        }
    }
}
