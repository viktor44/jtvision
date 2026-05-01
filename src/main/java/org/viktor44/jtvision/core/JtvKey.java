package org.viktor44.jtvision.core;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.viktor44.jtvision.util.SystemUtils;

/**
 * Keyboard shortcut constants aligned with {@link java.awt.event.KeyEvent}.
 *
 * <p>Plain key codes ({@code kbF1}, {@code kbEsc}, …) are equal to the
 * corresponding {@code KeyEvent.VK_*} virtual-key codes.  Modifier masks
 * ({@code kbShift}, {@code kbCtrlShift}, {@code kbAltShift}) are equal to the
 * corresponding {@link InputEvent} {@code _DOWN_MASK} flags.
 *
 * <p>Compound shortcuts that combine a modifier with a key (e.g.
 * {@code kbCtrlF1}, {@code kbAltA}, {@code kbShiftIns}) are encoded as
 * {@code (modifiers << 16) | keyCode}.  This is the same packing returned by
 * {@link KeyDownEvent#getKeyStroke()}, so a compound constant can be matched
 * directly against an event's keystroke value.
 */
public final class JtvKey {

    private JtvKey() {}

    /** No key pressed. */
    public static final int kbNoKey = 0;

    // ---- Modifier state masks (mirror InputEvent.*_DOWN_MASK) ----

    /** Shift key modifier mask. */
    public static final int kbShiftMask = InputEvent.SHIFT_DOWN_MASK;
    /** Ctrl key modifier mask. */
    public static final int kbCtrlMask = InputEvent.CTRL_DOWN_MASK;
    /** Alt key modifier mask. */
    public static final int kbAltMask = InputEvent.ALT_DOWN_MASK;

    // ---- Standard keys ----

    public static final int kbEsc = KeyEvent.VK_ESCAPE;
    public static final int kbBack = KeyEvent.VK_BACK_SPACE;
    public static final int kbTab = KeyEvent.VK_TAB;
    public static final int kbShiftTab = (kbShiftMask << 16) | KeyEvent.VK_TAB;
    public static final int kbEnter = KeyEvent.VK_ENTER;
    public static final int kbSpace = KeyEvent.VK_SPACE;

    // ---- Cursor / navigation keys ----

    public static final int kbUp = KeyEvent.VK_UP;
    public static final int kbDown = KeyEvent.VK_DOWN;
    public static final int kbLeft = KeyEvent.VK_LEFT;
    public static final int kbRight = KeyEvent.VK_RIGHT;
    public static final int kbHome = KeyEvent.VK_HOME;
    public static final int kbEnd = KeyEvent.VK_END;
    public static final int kbPgUp = KeyEvent.VK_PAGE_UP;
    public static final int kbPgDn = KeyEvent.VK_PAGE_DOWN;
    public static final int kbIns = KeyEvent.VK_INSERT;
    public static final int kbDel = KeyEvent.VK_DELETE;

    // ---- Ctrl + cursor keys ----

    public static final int kbCtrlLeft = (kbCtrlMask << 16) | KeyEvent.VK_LEFT;
    public static final int kbCtrlRight = (kbCtrlMask << 16) | KeyEvent.VK_RIGHT;
    public static final int kbCtrlUp = (kbCtrlMask << 16) | KeyEvent.VK_UP;
    public static final int kbCtrlDown = (kbCtrlMask << 16) | KeyEvent.VK_DOWN;
    public static final int kbCtrlPgUp = (kbCtrlMask << 16) | KeyEvent.VK_PAGE_UP;
    public static final int kbCtrlPgDn = (kbCtrlMask << 16) | KeyEvent.VK_PAGE_DOWN;
    public static final int kbCtrlHome = (kbCtrlMask << 16) | KeyEvent.VK_HOME;
    public static final int kbCtrlEnd = (kbCtrlMask << 16) | KeyEvent.VK_END;
    public static final int kbCtrlIns = (kbCtrlMask << 16) | KeyEvent.VK_INSERT;
    public static final int kbCtrlDel = (kbCtrlMask << 16) | KeyEvent.VK_DELETE;
    public static final int kbCtrlBack = (kbCtrlMask << 16) | KeyEvent.VK_BACK_SPACE;

    // ---- Ctrl + letter keys ----

    public static final int kbCtrlN = (kbCtrlMask << 16) | KeyEvent.VK_N;
    public static final int kbCtrlQ = (kbCtrlMask << 16) | KeyEvent.VK_Q;
    public static final int kbCtrlU = (kbCtrlMask << 16) | KeyEvent.VK_U;
    public static final int kbCtrlW = (kbCtrlMask << 16) | KeyEvent.VK_W;

    // ---- Shift + Insert / Delete ----

    public static final int kbShiftIns = (kbShiftMask << 16) | KeyEvent.VK_INSERT;
    public static final int kbShiftDel = (kbShiftMask << 16) | KeyEvent.VK_DELETE;

    // ---- Function keys ----

    public static final int kbF1 = KeyEvent.VK_F1;
    public static final int kbF2 = KeyEvent.VK_F2;
    public static final int kbF3 = KeyEvent.VK_F3;
    public static final int kbF4 = KeyEvent.VK_F4;
    public static final int kbF5 = KeyEvent.VK_F5;
    public static final int kbF6 = KeyEvent.VK_F6;
    public static final int kbF7 = KeyEvent.VK_F7;
    public static final int kbF8 = KeyEvent.VK_F8;
    public static final int kbF9 = KeyEvent.VK_F9;
    public static final int kbF10 = KeyEvent.VK_F10;

    // ---- Shift + Function keys ----

    public static final int kbShiftF1 = (kbShiftMask << 16) | KeyEvent.VK_F1;
    public static final int kbShiftF2 = (kbShiftMask << 16) | KeyEvent.VK_F2;
    public static final int kbShiftF3 = (kbShiftMask << 16) | KeyEvent.VK_F3;
    public static final int kbShiftF4 = (kbShiftMask << 16) | KeyEvent.VK_F4;
    public static final int kbShiftF5 = (kbShiftMask << 16) | KeyEvent.VK_F5;
    public static final int kbShiftF6 = (kbShiftMask << 16) | KeyEvent.VK_F6;
    public static final int kbShiftF7 = (kbShiftMask << 16) | KeyEvent.VK_F7;
    public static final int kbShiftF8 = (kbShiftMask << 16) | KeyEvent.VK_F8;
    public static final int kbShiftF9 = (kbShiftMask << 16) | KeyEvent.VK_F9;
    public static final int kbShiftF10 = (kbShiftMask << 16) | KeyEvent.VK_F10;

    // ---- Ctrl + Function keys ----

    public static final int kbCtrlF1 = (kbCtrlMask << 16) | KeyEvent.VK_F1;
    public static final int kbCtrlF2 = (kbCtrlMask << 16) | KeyEvent.VK_F2;
    public static final int kbCtrlF3 = (kbCtrlMask << 16) | KeyEvent.VK_F3;
    public static final int kbCtrlF4 = (kbCtrlMask << 16) | KeyEvent.VK_F4;
    public static final int kbCtrlF5 = (kbCtrlMask << 16) | KeyEvent.VK_F5;
    public static final int kbCtrlF6 = (kbCtrlMask << 16) | KeyEvent.VK_F6;
    public static final int kbCtrlF7 = (kbCtrlMask << 16) | KeyEvent.VK_F7;
    public static final int kbCtrlF8 = (kbCtrlMask << 16) | KeyEvent.VK_F8;
    public static final int kbCtrlF9 = (kbCtrlMask << 16) | KeyEvent.VK_F9;
    public static final int kbCtrlF10 = (kbCtrlMask << 16) | KeyEvent.VK_F10;

    // ---- Alt + Function keys ----

    public static final int kbAltF1 = (kbAltMask << 16) | KeyEvent.VK_F1;
    public static final int kbAltF2 = (kbAltMask << 16) | KeyEvent.VK_F2;
    public static final int kbAltF3 = (kbAltMask << 16) | KeyEvent.VK_F3;
    public static final int kbAltF4 = (kbAltMask << 16) | KeyEvent.VK_F4;
    public static final int kbAltF5 = (kbAltMask << 16) | KeyEvent.VK_F5;
    public static final int kbAltF6 = (kbAltMask << 16) | KeyEvent.VK_F6;
    public static final int kbAltF7 = (kbAltMask << 16) | KeyEvent.VK_F7;
    public static final int kbAltF8 = (kbAltMask << 16) | KeyEvent.VK_F8;
    public static final int kbAltF9 = (kbAltMask << 16) | KeyEvent.VK_F9;
    public static final int kbAltF10 = (kbAltMask << 16) | KeyEvent.VK_F10;

    // ---- Alt + letter keys (A–Z) ----

    public static final int kbAltA = (kbAltMask << 16) | KeyEvent.VK_A;
    public static final int kbAltB = (kbAltMask << 16) | KeyEvent.VK_B;
    public static final int kbAltC = (kbAltMask << 16) | KeyEvent.VK_C;
    public static final int kbAltD = (kbAltMask << 16) | KeyEvent.VK_D;
    public static final int kbAltE = (kbAltMask << 16) | KeyEvent.VK_E;
    public static final int kbAltF = (kbAltMask << 16) | KeyEvent.VK_F;
    public static final int kbAltG = (kbAltMask << 16) | KeyEvent.VK_G;
    public static final int kbAltH = (kbAltMask << 16) | KeyEvent.VK_H;
    public static final int kbAltI = (kbAltMask << 16) | KeyEvent.VK_I;
    public static final int kbAltJ = (kbAltMask << 16) | KeyEvent.VK_J;
    public static final int kbAltK = (kbAltMask << 16) | KeyEvent.VK_K;
    public static final int kbAltL = (kbAltMask << 16) | KeyEvent.VK_L;
    public static final int kbAltM = (kbAltMask << 16) | KeyEvent.VK_M;
    public static final int kbAltN = (kbAltMask << 16) | KeyEvent.VK_N;
    public static final int kbAltO = (kbAltMask << 16) | KeyEvent.VK_O;
    public static final int kbAltP = (kbAltMask << 16) | KeyEvent.VK_P;
    public static final int kbAltQ = (kbAltMask << 16) | KeyEvent.VK_Q;
    public static final int kbAltR = (kbAltMask << 16) | KeyEvent.VK_R;
    public static final int kbAltS = (kbAltMask << 16) | KeyEvent.VK_S;
    public static final int kbAltT = (kbAltMask << 16) | KeyEvent.VK_T;
    public static final int kbAltU = (kbAltMask << 16) | KeyEvent.VK_U;
    public static final int kbAltV = (kbAltMask << 16) | KeyEvent.VK_V;
    public static final int kbAltW = (kbAltMask << 16) | KeyEvent.VK_W;
    public static final int kbAltX = (kbAltMask << 16) | KeyEvent.VK_X;
    public static final int kbAltY = (kbAltMask << 16) | KeyEvent.VK_Y;
    public static final int kbAltZ = (kbAltMask << 16) | KeyEvent.VK_Z;

    // ---- Alt + digit keys (0–9) ----

    public static final int kbAlt0 = (kbAltMask << 16) | KeyEvent.VK_0;
    public static final int kbAlt1 = (kbAltMask << 16) | KeyEvent.VK_1;
    public static final int kbAlt2 = (kbAltMask << 16) | KeyEvent.VK_2;
    public static final int kbAlt3 = (kbAltMask << 16) | KeyEvent.VK_3;
    public static final int kbAlt4 = (kbAltMask << 16) | KeyEvent.VK_4;
    public static final int kbAlt5 = (kbAltMask << 16) | KeyEvent.VK_5;
    public static final int kbAlt6 = (kbAltMask << 16) | KeyEvent.VK_6;
    public static final int kbAlt7 = (kbAltMask << 16) | KeyEvent.VK_7;
    public static final int kbAlt8 = (kbAltMask << 16) | KeyEvent.VK_8;
    public static final int kbAlt9 = (kbAltMask << 16) | KeyEvent.VK_9;

    public static final int kbAltBack = (kbAltMask << 16) | KeyEvent.VK_BACK_SPACE;

    /**
     * Returns the {@code kbAlt*} stroke code for Alt+{@code c}.
     *
     * @param c letter character; case-insensitive, must be {@code A}–{@code Z}
     * @return the corresponding {@code kbAlt*} constant, or {@code 0} if not a letter
     */
    public static int getAltCode(char c) {
        c = Character.toUpperCase(c);
        if (c >= 'A' && c <= 'Z') {
            return (kbAltMask << 16) | c;
        }
        return 0;
    }

    /**
     * Returns the letter or digit character corresponding to an Alt+key stroke.
     *
     * @param keyStroke a {@code kbAlt*} keystroke value
     * @return the uppercase letter ({@code A}–{@code Z}) or digit ({@code 0}–{@code 9}),
     *         or {@code '\0'} if the value is not a recognised Alt+letter/digit combo
     */
    public static char getAltChar(int keyStroke) {
        if ((keyStroke >>> 16) != kbAltMask) {
            return '\0';
        }
        int vk = keyStroke & 0xFFFF;
        if (vk >= KeyEvent.VK_A && vk <= KeyEvent.VK_Z) {
            return (char) vk;
        }
        if (vk >= KeyEvent.VK_0 && vk <= KeyEvent.VK_9) {
            return (char) vk;
        }
        return '\0';
    }

    /**
     * Translates WordStar-style Ctrl key sequences to their cursor-movement equivalents.
     *
     * <p>Supports the classic WordStar diamond: Ctrl+E (up), Ctrl+X (down), Ctrl+S (left),
     * Ctrl+D (right) and several related commands.  Returns the original code unchanged if
     * there is no translation.  Inputs are AWT-style key strokes (from
     * {@link KeyDownEvent#getKeyStroke()}); outputs are plain VK codes.
     *
     * @param keyStroke the raw keystroke to translate
     * @return the equivalent cursor-key code, or {@code keyStroke} if no mapping exists
     */
    public static int ctrlToArrow(int keyStroke) {
        if ((keyStroke >>> 16) != kbCtrlMask) {
            return keyStroke;
        }
        int vk = keyStroke & 0xFFFF;
        switch (vk) {
            case KeyEvent.VK_S: return kbLeft;    // Ctrl+S
            case KeyEvent.VK_D: return kbRight;   // Ctrl+D
            case KeyEvent.VK_E: return kbUp;      // Ctrl+E
            case KeyEvent.VK_X: return kbDown;    // Ctrl+X
            case KeyEvent.VK_A: return kbHome;    // Ctrl+A (word left)
            case KeyEvent.VK_F: return kbEnd;     // Ctrl+F (word right)
            case KeyEvent.VK_G: return kbDel;     // Ctrl+G
            case KeyEvent.VK_V: return kbIns;     // Ctrl+V
            default: return keyStroke;
        }
    }

    /**
     * Returns the platform-appropriate label for the Alt/Option modifier key.
     * Returns {@code "Option"} on macOS and {@code "Alt"} on all other platforms.
     */
    public static String getMetaKeyLabel() {
        return SystemUtils.IS_OS_MAC ? "Option" : "Alt";
    }
}
