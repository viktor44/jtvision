package org.viktor44.jtvision.examples.tvdemo;

import static org.viktor44.jtvision.core.EventCodes.evBroadcast;
import static org.viktor44.jtvision.core.EventCodes.evKeyboard;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.EventCodes.evMouseMove;
import static org.viktor44.jtvision.core.ViewFlags.ofFramed;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;
import static org.viktor44.jtvision.core.ViewFlags.wfGrow;
import static org.viktor44.jtvision.core.ViewFlags.wfZoom;
import static org.viktor44.jtvision.core.ViewFlags.wnNoNumber;
import static org.viktor44.jtvision.core.ViewFlags.wpGrayWindow;

import java.nio.charset.Charset;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvView;
import org.viktor44.jtvision.views.JtvWindow;

/**
 * ASCII table demo. A {@link TTable} grid at the top shows all 256 code
 * points; arrow keys or the mouse move the cursor over a cell and broadcast
 * {@link #CM_CHAR_FOCUSED} to the {@link TReport} line below.
 */
public class AsciiChartWindow extends JtvWindow {

    public static final int CM_CHAR_FOCUSED = 2000;

    private static final char[] CP437 = buildCp437();

    private static char[] buildCp437() {
        char[] table = new char[256];
        byte[] bytes = new byte[256];
        for (int i = 0; i < 256; i++) bytes[i] = (byte) i;
        String decoded = new String(bytes, Charset.forName("IBM437"));
        for (int i = 0; i < 256; i++) table[i] = decoded.charAt(i);
        char[] lowGlyphs = {
            ' ',      '\u263A', '\u263B', '\u2665', '\u2666', '\u2663', '\u2660', '\u2022',
            '\u25D8', '\u25CB', '\u25D9', '\u2642', '\u2640', '\u266A', '\u266B', '\u263C',
            '\u25BA', '\u25C4', '\u2195', '\u203C', '\u00B6', '\u00A7', '\u25AC', '\u21A8',
            '\u2191', '\u2193', '\u2192', '\u2190', '\u221F', '\u2194', '\u25B2', '\u25BC'
        };
        System.arraycopy(lowGlyphs, 0, table, 0, 32);
        table[0x7F] = '\u2302';
        return table;
    }

    public AsciiChartWindow() {
        super(new JtvRect(0, 0, 34, 12), "ASCII Chart", wnNoNumber);
        flags &= ~(wfGrow | wfZoom);
        growMode = 0;
        paletteIndex = wpGrayWindow;

        JtvRect r = getExtent();
        r.grow(-1, -1);
        JtvRect reportR = new JtvRect(r.getA().getX(), r.getB().getY() - 1, r.getB().getX(), r.getB().getY());
        TReport report = new TReport(reportR);
        report.setOptions(report.getOptions() | ofFramed);
        report.setEventMask(report.getEventMask() | evBroadcast);
        insert(report);

        JtvRect tableR = new JtvRect(r.getA().getX(), r.getA().getY(), r.getB().getX(), r.getB().getY() - 2);
        TTable table = new TTable(tableR);
        table.setOptions(table.getOptions() | ofFramed | ofSelectable);
        table.blockCursor();
        insert(table);
        table.select();
    }

    /** 32-column x N-row character table. Tracks cursor and broadcasts focus. */
    public static class TTable extends JtvView {
        public TTable(JtvRect r) {
            super(r);
            eventMask |= evKeyboard;
        }

        @Override
        public void draw() {
            JtvDrawBuffer buf = new JtvDrawBuffer();
            JtvColorAttr color = getColor(6);
            for (int y = 0; y < getSize().getY(); y++) {
                buf.moveChar(0, ' ', color, getSize().getX());
                for (int x = 0; x < getSize().getX(); x++) {
                    int code = 32 * y + x;
                    buf.moveChar(x, CP437[code & 0xFF], color, 1);
                }
                writeLine(0, y, getSize().getX(), 1, buf);
            }
            showCursor();
        }

        private void broadcast() {
            int code = cursor.getX() + 32 * cursor.getY();
            JtvEvent e = new JtvEvent();
            e.setWhat(evBroadcast);
            e.getMessage().setCommand(CM_CHAR_FOCUSED);
            e.getMessage().setInfoPtr(code);
            if (owner != null) owner.handleEvent(e);
        }

        @Override
        public void handleEvent(JtvEvent event) {
            super.handleEvent(event);
            if (event.getWhat() == evMouseDown) {
                do {
                    if (mouseInView(event.getMouse().getWhere())) {
                        JtvPoint spot = makeLocal(event.getMouse().getWhere());
                        setCursor(spot.getX(), spot.getY());
                        broadcast();
                    }
                } while (mouseEvent(event, evMouseMove));
                clearEvent(event);
            } else if ((event.getWhat() & evKeyboard) != 0) {
                switch (event.getKeyDown().getKeyCode()) {
                    case JtvKey.kbHome: 
	                    	setCursor(0, 0);
	                    	break;
                    case JtvKey.kbEnd:
	                    	setCursor(getSize().getX() - 1, getSize().getY() - 1);
	                    	break;
                    case JtvKey.kbUp:
	                    	if (getCursor().getY() > 0) {
	                    		setCursor(getCursor().getX(), getCursor().getY() - 1);
	                    	}
	                    	break;
                    case JtvKey.kbDown:
	                    	if (getCursor().getY() < getSize().getY() - 1) {
	                    		setCursor(getCursor().getX(), getCursor().getY() + 1);
	                    	}
	                    	break;
                    case JtvKey.kbLeft:
	                    	if (getCursor().getX() > 0) {
	                    		setCursor(getCursor().getX() - 1, getCursor().getY());
	                    	}
	                    	break;
                    case JtvKey.kbRight:
	                    	if (getCursor().getX() < getSize().getX() - 1) {
	                    		setCursor(getCursor().getX() + 1, getCursor().getY());
	                    	}
	                    	break;
                    default: {
                        char ch = event.getKeyDown().getKeyChar();
                        if (ch != java.awt.event.KeyEvent.CHAR_UNDEFINED && ch > 0) setCursor(ch % 32, ch / 32);
                    }
                }
                broadcast();
                clearEvent(event);
            }
        }
    }

    /** One-line readout of the character currently under the table cursor. */
    public static class TReport extends JtvView {
        private int asciiChar;

        public TReport(JtvRect r) { super(r); }

        @Override
        public void draw() {
            JtvDrawBuffer buf = new JtvDrawBuffer();
            JtvColorAttr color = getColor(6);
            char display = asciiChar == 0 ? ' ' : CP437[asciiChar & 0xFF];
            String s = String.format("  Char: %c Decimal: %3d Hex %02X     ",
                display, asciiChar, asciiChar);
            buf.moveChar(0, ' ', color, getSize().getX());
            buf.moveStr(0, s, color);
            writeLine(0, 0, getSize().getX(), 1, buf);
        }

        @Override
        public void handleEvent(JtvEvent event) {
            super.handleEvent(event);
            if (event.getWhat() == evBroadcast && event.getMessage().getCommand() == CM_CHAR_FOCUSED) {
                Object info = event.getMessage().getInfoPtr();
                if (info instanceof Integer) {
                    asciiChar = (Integer) info;
                    drawView();
                }
            }
        }
    }
}
