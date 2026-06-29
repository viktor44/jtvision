/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.examples.demo;

import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evKeyboard;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.ViewFlags.ofFramed;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;

import java.awt.event.KeyEvent;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.dialogs.JtvDialog;
import org.viktor44.jtvision.dialogs.JtvLabel;
import org.viktor44.jtvision.views.JtvListViewer;
import org.viktor44.jtvision.views.JtvScrollBar;
import org.viktor44.jtvision.views.JtvView;

/**
 * UTF-8 character table demo. Shows a dropdown to select a Unicode range,
 * a 32-column character grid, and a report line at the bottom with details
 * about the focused character.
 */
public class Utf8ChartWindow extends JtvDialog {

    public static final int CM_CHAR_FOCUSED = 2001;
    public static final int CM_RANGE_CHANGED = 2002;

    /** 
     * A named Unicode range with start and end code points.<br>
     * https://www.w3schools.com/charsets/ref_utf_reference.asp 
     */
    enum Utf8Range {
        BASIC_LATIN                ("Basic Latin",               0x0000, 0x007F),
        LATIN_1_SUPPLEMENT         ("Latin-1 Supplement",         0x0080, 0x00FF),
        LATIN_EXTENDED_A           ("Latin Extended A",           0x0100, 0x017F),
        LATIN_EXTENDED_B           ("Latin Extended B",           0x0180, 0x024F),
        LATIN_IPA_EXTENSIONS       ("Latin IPA Extensions",       0x0250, 0x02AD),
        SPACING_MODIFIERS          ("Spacing Modifiers",          0x02B0, 0x02FF),
        // Diacritical Marks 0300-036F
        GREEK_AND_COPTIC           ("Greek and Coptic",           0x0370, 0x03FF),
        CYRILLIC_BASIC             ("Cyrillic Basic",             0x0400, 0x04FF),
        CYRILLIC_SUPPLEMENT        ("Cyrillic Supplement",        0x0500, 0x052F),
        ARMENIAN                   ("Armenian",                   0x0530, 0x058F),
        HEBREW                     ("Hebrew",                     0x0590, 0x05FF),
        ARABIC                     ("Arabic",                     0x0600, 0x06FF),
        SYRIAC                     ("Syriac",                     0x0700, 0x074F),
        ARABIC_SUPPLEMENT          ("Arabic Supplement",          0x0750, 0x077F),
        THAANA                     ("Thaana",                     0x0780, 0x07BF),
        NKO                        ("N'Ko",                       0x07C0, 0x07FF),
        SAMARITAN                  ("Samaritan",                  0x0800, 0x083F),
        MANDAIC                    ("Mandaic",                    0x0840, 0x085F),
        ARABIC_EXTENDED_B          ("Arabic Extended-B",          0x0870, 0x089F),
        ARABIC_EXTENDED_A          ("Arabic Extended-A",          0x08A0, 0x08FF),
        HINDI_DEVANAGARI           ("Hindi / Devanagari",         0x0900, 0x097F),
        BENGALI                    ("Bengali",                    0x0980, 0x09FF),
        GURMUKHI                   ("Gurmukhi",                   0x0A00, 0x0A7F),
        GUJARATI                   ("Gujarati",                   0x0A80, 0x0AFF),
        ORIYA                      ("Oriya",                      0x0B00, 0x0B7F),
        TAMIL                      ("Tamil",                      0x0B80, 0x0BFF),
        TELUGU                     ("Telugu",                     0x0C00, 0x0C7F),
        KANNADA                    ("Kannada",                    0x0C80, 0x0CFF),
        MALAYALAM                  ("Malayalam",                  0x0D00, 0x0D7F),
        SINHALA                    ("Sinhala",                    0x0D80, 0x0DFF),
        THAI                       ("Thai",                       0x0E00, 0x0E7F),
        LAO                        ("Lao",                        0x0E80, 0x0EFF),
        TIBETAN                    ("Tibetan",                    0x0F00, 0x0FFF),
        MYANMAR                    ("Myanmar",                    0x1000, 0x109F),
        GEORGIAN                   ("Georgian",                   0x10A0, 0x10FF),
        ETHIOPIC                   ("Ethiopic",                   0x1200, 0x137F),
        ETHIOPIC_SUPPLEMENT        ("Ethiopic Supplement",        0x1380, 0x139F),
        CHEROKEE                   ("Cherokee",                   0x13A0, 0x13FF),
        CANADIAN_ABORIGINAL        ("Canadian Aboriginal",        0x1400, 0x167F),
        OGHAM                      ("Ogham",                      0x1680, 0x169C),
        RUNIC                      ("Runic",                      0x16A0, 0x16FF),
        TAGALOG                    ("Tagalog",                    0x1700, 0x171F),
        HANUNOO                    ("Hanunoo",                    0x1720, 0x173F),
        BUHID                      ("Buhid",                      0x1740, 0x175F),
        TAGBANWA                   ("Tagbanwa",                   0x1760, 0x177F),
        MONGOLIAN                  ("Mongolian",                  0x1800, 0x18AF),
        KHMER                      ("Khmer",                      0x18B0, 0x18FF),
        
        CANADIAN_ABORIGINAL_EXT    ("Canadian Aboriginal Extended", 0x18B0, 0x18FF),
        KHMER_SYMBOLS              ("Khmer Symbols",              0x19E0, 0x19FF),
        
//+        Canadian Aboriginal Extended    18B0-18FF
//+        Khmer Symbols    19E0-19FF
//+        Diacritical Marks Extended    1AB0-1C89
//+        Cyrillic Extended C    1C80-1C89
//+        Georgian Extended    1C90-1CBF
//+        Phonetic Extensions    1D00-1D7F
//+        Phonetic Extensions Supplement    1D80-1DBF
//+        Diacritical Marks Supplement    1DC0-1DFF
//+        Latin Extended Additional    1E00-1EFF
//+        Greek Extended    1F00-1FFF
        GENERAL_PUNCTUATION        ("General Punctuation",        0x2000, 0x206F),
        SUPERSCRIPTS_SUBSCRIPTS    ("Superscripts/Subscripts",    0x2070, 0x209F),
        CURRENCY_SYMBOLS           ("Currency Symbols",           0x20A0, 0x20CF),
        LETTERLIKE_SYMBOLS         ("Letterlike Symbols",         0x2100, 0x214F),
        NUMBER_FORMS               ("Number Forms",               0x2150, 0x218F),
        ARROWS                     ("Arrows",                     0x2190, 0x21FF),
        MATHEMATICAL_OPERATORS     ("Mathematical Operators",     0x2200, 0x22FF),
        MISC_TECHNICAL             ("Misc Technical",             0x2300, 0x23FF),
        ENCLOSED_ALPHANUMERIC      ("Enclosed Alphanumeric",      0x2460, 0x24FF),
        BOX_DRAWINGS               ("Box Drawings",               0x2500, 0x257F),
        BLOCK_ELEMENTS             ("Block Elements",             0x2580, 0x259F),
        GEOMETRIC_SHAPES           ("Geometric Shapes",           0x25A0, 0x25FF),
        MISCELLANEOUS_SYMBOLS      ("Miscellaneous Symbols",      0x2600, 0x26FF),
        DINGBATS                   ("Dingbats",                   0x2700, 0x27BF),
        MISC_MATH_SYMBOLS_A        ("Misc Math Symbols A",        0x27C0, 0x27EF),
        SUPPLEMENTAL_ARROWS_A      ("Supplemental Arrows A",      0x27F0, 0x27FF),
        BRAILLE                    ("Braille",                    0x2800, 0x28FF),
        SUPPLEMENTAL_ARROWS_B      ("Supplemental Arrows B",      0x2900, 0x297F),
        MISC_MATH_SYMBOLS_B        ("Misc Math Symbols B",        0x2980, 0x29FF),
//+        Supplemental Math Operators    2A00-2AFF        
        MISC_SYMBOLS_AND_ARROWS    ("Misc Symbols and Arrows",    0x2B00, 0x2BFF),
        GLAGOLITIC                 ("Glagolitic",                 0x2C00, 0x2C5F),
        LATIN_EXTENDED_C           ("Latin Extended C",           0x2C60, 0x2C7F),
        COPTIC                     ("Coptic",                     0x2C80, 0x2CFF),
//+        Georgian Supplement    2D00-2D2F
//+        Ethiopic Extended    2D80-2DDF
//+        Cyrillic Extended A    2DE0-2DFF
//+        Supplemental Punctuation    2E00-2E7F        
//        CJK_RADICALS_SUPPLEMENT    ("CJK Radicals Supplement",    0x2E80, 0x2EFF), //wide symbols
//        KANGXI_RADICALS            ("KangXi Radicals",            0x2F00, 0x2FDF), //wide symbols
//        CJK_UNIFIED_IDEOGRAPHS     ("CJK Unified Ideographs",     0x4E00, 0x9FFF), //wide symbols
//+        Cyrillic Extended B			A640-A69F
//+        Latin Extended D				A720-A7FF
//+        Cham							AA00-AA5F
//+        Latin Extended E				AB30-AB6F
//+        Gothic						10330-1034F
//+        Aegean Numbers				10100-1013F
//+        Phoenican					10900-1091F
//+        Lydian						10920-1093F
//+        Meroitic Hieroglyphs			10980-1099F
//+        Old Turkic					10C00-10C4F
//+        Egyptian Hieroglyphs			13000–1342F
//+        Bamum Supplement				16800-16A3F        
//        MUSICAL_SYMBOLS            ("Musical Symbols",            0x1D100, 0x1D1FF), //wide symbols
//        MATH_ALPHANUMERIC          ("Math Alphanumeric",          0x1D400, 0x1D7FF), //wide symbols
        MAHJONG_TILES              ("Mahjong Tiles",              0x1F000, 0x1F02F),
        DOMINO_TILES               ("Domino Tiles",               0x1F030, 0x1F09F),
        PLAYING_CARDS              ("Playing Cards",              0x1F0A0, 0x1F0FF),
        ENCLOSED_ALPHA_SUPPLEMENT  ("Enclosed Alpha Supplement",  0x1F100, 0x1F1FF),
        ENCLOSED_IDEOGRAPHIC       ("Enclosed Ideographic",       0x1F200, 0x1F2FF),
        MISC_SYMBOLS_PICTOGRAPHS   ("Miscellaneous Symbols",      0x1F300, 0x1F5FF),
        EMOTICONS                  ("Emoticons",                  0x1F600, 0x1F64F),
        ORNAMENTAL_DINGBATS        ("Ornamental Dingbats",        0x1F650, 0x1F67F),
        TRANSPORT_AND_MAPS         ("Transport and Maps",         0x1F680, 0x1F6FF),
        ALCHEMICAL_SYMBOLS         ("Alchemical Symbols",         0x1F700, 0x1F77F),
        GEOMETRIC_SHAPES_EXTENDED  ("Geometric Shapes Extended",  0x1F780, 0x1F7FF),
        SUPPLEMENTAL_ARROWS_C      ("Supplemental Arrows C",      0x1F800, 0x1F8FF),
//        SUPPLEMENTAL_SYMBOLS       ("Supplemental Symbols",       0x1F900, 0x1F9FF),
        ;

    	private final String label;
        private final int start;
        private final int end;

        private Utf8Range(String label, int start, int end) {
            this.label = label;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return String.format("%s (U+%04X-U+%04X)", label, start, end);
        }
    }

    /** All UTF-8 ranges in declaration order. */
    private static final Utf8Range[] RANGES = Utf8Range.values();

    /** The number of columns in the character grid. */
    private static final int GRID_COLS = 32;

    private RangeListView rangeList;
    private CharTableView table;
    private CharReportView report;

    public Utf8ChartWindow() {
        super(new JtvRect(0, 0, 46, 19), "UTF-8 Chart");

        JtvRect r = getExtent().grow(-1, -1);

        // --- Report line at the bottom ---
        JtvRect reportR = new JtvRect(r.getAx(), r.getBy() - 1, r.getBx(), r.getBy());
        report = new CharReportView(reportR);
        report.enableOptions(ofFramed);
        report.setEventMask(report.getEventMask() | evBroadcast);
        insert(report);

        // --- Range selector (list-based dropdown) at the top ---
        JtvRect labelR = new JtvRect(r.getAx(), r.getAy(), r.getAx() + 8, r.getAy() + 1);
        JtvRect listR = new JtvRect(r.getAx() + 8, r.getAy(), r.getBx(), r.getAy() + 3);

        JtvScrollBar vScroll = new JtvScrollBar(new JtvRect(listR.getBx() - 1, listR.getAy(), listR.getBx(), listR.getBy()));
        vScroll.enableOptions(ofSelectable);
        insert(vScroll);

        rangeList = new RangeListView(
                new JtvRect(listR.getAx(), listR.getAy(), listR.getBx() - 1, listR.getBy()),
                1, null, vScroll
        );
        rangeList.enableOptions(ofSelectable);
        rangeList.setEventMask(rangeList.getEventMask() | evBroadcast);
        insert(rangeList);

        JtvLabel label = new JtvLabel(labelR, "~R~ange:", rangeList);
        insert(label);

        // --- Character table ---
        JtvRect tableR = new JtvRect(r.getAx(), r.getAy() + 4,
                                     r.getBx(), r.getBy() - 2);
        table = new CharTableView(tableR);
        table.enableOptions(ofFramed | ofSelectable);
        table.setEventMask(table.getEventMask() | evBroadcast);
        table.blockCursor();
        insert(table);
        table.select();
    }

    /**
     * Returns the currently selected UTF-8 range.
     */
    Utf8Range getSelectedRange() {
        int idx = rangeList.getFocusedIndex();
        if (idx >= 0 && idx < RANGES.length) {
            return RANGES[idx];
        }
        return RANGES[0];
    }

    // ---------------------------------------------------------------
    // TRangeList -- scrollable range selector using JtvListViewer
    // ---------------------------------------------------------------
    public class RangeListView extends JtvListViewer {

        public RangeListView(JtvRect bounds, int numCols,
                          JtvScrollBar hScrollBar, JtvScrollBar vScrollBar) {
            super(bounds, numCols, hScrollBar, vScrollBar);
            setRange(RANGES.length);
            eventMask |= evBroadcast;
        }

        @Override
        public String getText(int item, int maxLen) {
            if (item >= 0 && item < RANGES.length) {
                String s = RANGES[item].toString();
                return s.length() > maxLen ? s.substring(0, maxLen) : s;
            }
            return "";
        }

        public int getFocusedIndex() {
            return focused;
        }

        @Override
        public void focusItem(int item) {
            super.focusItem(item);
            // Broadcast range change to sibling views
            JtvEvent e = new JtvEvent();
            e.setWhat(evBroadcast);
            e.getMessage().setCommand(CM_RANGE_CHANGED);
            e.getMessage().setInfoPtr(item);
            if (owner != null) {
                owner.handleEvent(e);
            }
        }
    }

    // ---------------------------------------------------------------
    // TTable -- 32-column Unicode character grid
    // ---------------------------------------------------------------
    public class CharTableView extends JtvView {

        public CharTableView(JtvRect r) {
            super(r);
            eventMask |= evKeyboard;
        }

        /** Returns the code point at grid position (x, y) in the current range. */
        private int codePointAt(int x, int y) {
            Utf8Range range = getSelectedRange();
            return range.start + y * GRID_COLS + x;
        }

        /** Returns the total number of rows needed for the current range. */
        private int totalRows() {
            Utf8Range range = getSelectedRange();
            int count = range.end - range.start + 1;
            return (count + GRID_COLS - 1) / GRID_COLS;
        }

        @Override
        public void draw() {
            JtvDrawBuffer buf = new JtvDrawBuffer();
            JtvColorAttr color = getColor(6);
            Utf8Range range = getSelectedRange();
            int total = range.end - range.start + 1;
            for (int y = 0; y < getSize().getY(); y++) {
                buf.moveChar(0, ' ', color, getSize().getX());
                for (int x = 0; x < Math.min(GRID_COLS, getSize().getX()); x++) {
                    int offset = y * GRID_COLS + x;
                    if (offset < total) {
                        int cp = range.start + offset;
                        char ch = Character.isDefined(cp) && !Character.isISOControl(cp)
                                ? (char) cp : ' ';
                        buf.moveChar(x, ch, color, 1);
                    }
                }
                writeLine(0, y, getSize().getX(), 1, buf);
            }
            showCursor();
        }

        private void broadcast() {
            int cp = codePointAt(cursor.getX(), cursor.getY());
            JtvEvent e = new JtvEvent();
            e.setWhat(evBroadcast);
            e.getMessage().setCommand(CM_CHAR_FOCUSED);
            e.getMessage().setInfoPtr(cp);
            if (owner != null) {
                owner.handleEvent(e);
            }
        }

        @Override
        public void handleEvent(JtvEvent event) {
            super.handleEvent(event);
            if (event.getWhat() == evBroadcast
                    && event.getMessage().getCommand() == CM_RANGE_CHANGED) {
                // Range changed -- reset cursor and redraw
                setCursor(0, 0);
                drawView();
                broadcast();
                return;
            }
            if (event.getWhat() == evMouseDown) {
                do {
                    if (mouseInView(event.getMouse().getWhere())) {
                        JtvPoint spot = makeLocal(event.getMouse().getWhere());
                        int maxX = Math.min(GRID_COLS - 1, getSize().getX() - 1);
                        int maxY = totalRows() - 1;
                        setCursor(Math.min(spot.getX(), maxX),
                                  Math.min(spot.getY(), Math.min(maxY, getSize().getY() - 1)));
                        broadcast();
                    }
                }
                while (mouseEvent(event, evMouseMove));
                clearEvent(event);
            }
            else if ((event.getWhat() & evKeyboard) != 0) {
                Utf8Range range = getSelectedRange();
                int total = range.end - range.start + 1;
                int maxCol = Math.min(GRID_COLS - 1, getSize().getX() - 1);
                int maxRow = Math.min(totalRows() - 1, getSize().getY() - 1);
                switch (event.getKeyDown().getKeyCode()) {
                    case KeyEvent.VK_HOME:
                        setCursor(0, 0);
                        break;
                    case KeyEvent.VK_END:
                        setCursor(maxCol, maxRow);
                        break;
                    case KeyEvent.VK_UP:
                        if (getCursor().getY() > 0) {
                            setCursor(getCursor().getX(), getCursor().getY() - 1);
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (getCursor().getY() < maxRow) {
                            setCursor(getCursor().getX(), getCursor().getY() + 1);
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (getCursor().getX() > 0) {
                            setCursor(getCursor().getX() - 1, getCursor().getY());
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (getCursor().getX() < maxCol) {
                            setCursor(getCursor().getX() + 1, getCursor().getY());
                        }
                        break;
                    default:
                        return;
                }
                broadcast();
                clearEvent(event);
            }
        }
    }

    // ---------------------------------------------------------------
    // TReport -- shows details of the focused code point
    // ---------------------------------------------------------------
    public static class CharReportView extends JtvView {
        private int codePoint;

        public CharReportView(JtvRect r) {
            super(r);
        }

        @Override
        public void draw() {
            JtvDrawBuffer buf = new JtvDrawBuffer();
            JtvColorAttr color = getColor(6);
            char display = (codePoint > 0 && Character.isDefined(codePoint)
                    && !Character.isISOControl(codePoint)) ? (char) codePoint : ' ';
            String s = String.format("  Char: %c  Dec: %5d  Hex: U+%04X  ",
                    display, codePoint, codePoint);
            buf.moveChar(0, ' ', color, getSize().getX());
            buf.moveStr(0, s, color);
            writeLine(0, 0, getSize().getX(), 1, buf);
        }

        @Override
        public void handleEvent(JtvEvent event) {
            super.handleEvent(event);
            if (event.getWhat() == evBroadcast
                    && event.getMessage().getCommand() == CM_CHAR_FOCUSED) {
                Object info = event.getMessage().getInfoPtr();
                if (info instanceof Integer) {
                    codePoint = (Integer) info;
                    drawView();
                }
            }
        }
    }
}
