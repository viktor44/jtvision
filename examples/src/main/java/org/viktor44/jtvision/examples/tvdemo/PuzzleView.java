package org.viktor44.jtvision.examples.tvdemo;

import static org.viktor44.jtvision.core.EventCodes.evKeyDown;
import static org.viktor44.jtvision.core.EventCodes.evKeyboard;
import static org.viktor44.jtvision.core.EventCodes.evMouse;
import static org.viktor44.jtvision.core.EventCodes.evMouseDown;
import static org.viktor44.jtvision.core.ViewFlags.ofSelectable;

import java.util.Random;

import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvDrawBuffer;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvKey;
import org.viktor44.jtvision.core.JtvPalette;
import org.viktor44.jtvision.core.JtvPoint;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvView;

/**
 * 15-tile puzzle view. Tiles form A..O plus one blank; arrow keys or
 * mouse clicks on an adjacent tile slide it into the blank.
 */
public class PuzzleView extends JtvView {

    private static final JtvPalette cpPuzzle = new JtvPalette(
    		new int[] {0x06, 0x07}
    );

    private static final char[] BOARD_START = {
        'A', 'B', 'C', 'D',
        'E', 'F', 'G', 'H',
        'I', 'J', 'K', 'L',
        'M', 'N', 'O', ' '
    };
    // parity map on tile letters: used to color alternating squares
    private static final int[] MAP = {
        0, 1, 0, 1,
        1, 0, 1, 0,
        0, 1, 0, 1,
        1, 0, 1
    };
    private static final String SOLUTION = "ABCDEFGHIJKLMNO ";

    private final char[][] board = new char[4][4];
    private int moves;
    private boolean solved;
    private final Random rand = new Random();

    public PuzzleView(JtvRect r) {
        super(r);
        options |= ofSelectable;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = BOARD_START[i * 4 + j];
            }
        }
        scramble();
    }

    @Override
    public JtvPalette getPalette() { 
    	return cpPuzzle;
    }

    @Override
    public void draw() {
        JtvDrawBuffer buf = new JtvDrawBuffer();
        JtvColorAttr colorBack = getColor(1);
        JtvColorAttr colorA = colorBack;
        JtvColorAttr colorB = solved ? colorBack : getColor(2);

        for (int i = 0; i < 4; i++) {
            buf.moveChar(0, ' ', colorBack, 18);
            if (i == 1) {
            	buf.moveStr(13, "Move", colorBack);
            }
            if (i == 2) {
            	buf.moveStr(14, Integer.toString(moves), colorBack);
            }
            for (int j = 0; j < 4; j++) {
                char tile = board[i][j];
                String s = " " + tile + " ";
                JtvColorAttr c;
                if (tile == ' ') c = colorA;
                else c = (MAP[tile - 'A'] != 0) ? colorB : colorA;
                buf.moveStr(j * 3, s, c);
            }
            writeLine(0, i, 18, 1, buf);
        }
    }

    @Override
    public void handleEvent(JtvEvent event) {
        super.handleEvent(event);
        if (solved && (event.getWhat() & (evKeyboard | evMouse)) != 0) {
            scramble();
            clearEvent(event);
            return;
        }
        if (event.getWhat() == evMouseDown) {
            moveTile(event.getMouse().getWhere());
            clearEvent(event);
            winCheck();
        } 
        else if (event.getWhat() == evKeyDown) {
            moveKey(event.getKeyDown().getKeyCode());
            clearEvent(event);
            winCheck();
        }
    }

    private int blankIndex() {
        for (int i = 0; i < 16; i++) {
            if (board[i / 4][i % 4] == ' ') {
            	return i;
            }
        }
        return 15;
    }

    public void moveKey(int key) {
        int i = blankIndex();
        int x = i % 4, y = i / 4;
        switch (key) {
            case JtvKey.kbDown:
                if (y > 0) { board[y][x] = board[y - 1][x]; board[y - 1][x] = ' '; bumpMoves(); }
                break;
            case JtvKey.kbUp:
                if (y < 3) { board[y][x] = board[y + 1][x]; board[y + 1][x] = ' '; bumpMoves(); }
                break;
            case JtvKey.kbRight:
                if (x > 0) { board[y][x] = board[y][x - 1]; board[y][x - 1] = ' '; bumpMoves(); }
                break;
            case JtvKey.kbLeft:
                if (x < 3) { board[y][x] = board[y][x + 1]; board[y][x + 1] = ' '; bumpMoves(); }
                break;
        }
        drawView();
    }

    private void bumpMoves() { if (moves < 1000) moves++; }

    private void moveTile(JtvPoint p) {
        JtvPoint lp = makeLocal(p);
        int i = blankIndex();
        int x = lp.getX() / 3;
        int y = lp.getY();
        switch (y * 4 + x - i) {
            case -4: moveKey(JtvKey.kbDown); break;
            case -1: moveKey(JtvKey.kbRight); break;
            case 1:  moveKey(JtvKey.kbLeft); break;
            case 4:  moveKey(JtvKey.kbUp); break;
        }
        drawView();
    }

    public void scramble() {
        moves = 0;
        solved = false;
        do {
            switch (rand.nextInt(4)) {
                case 0: moveKey(JtvKey.kbUp); break;
                case 1: moveKey(JtvKey.kbDown); break;
                case 2: moveKey(JtvKey.kbRight); break;
                case 3: moveKey(JtvKey.kbLeft); break;
            }
        }
        while (moves++ <= 500);
        moves = 0;
        drawView();
    }

    private void winCheck() {
        boolean match = true;
        for (int i = 0; i < 16 && match; i++) {
            if (board[i / 4][i % 4] != SOLUTION.charAt(i)) {
            	match = false;
            }
        }
        if (match) {
        	solved = true;
        }
        drawView();
    }
}
