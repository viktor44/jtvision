/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import java.awt.event.KeyEvent;

/**
 * macOS implementation of {@link EventQueue}.
 * <p>
 * On macOS the Option key acts as a full substitute for Alt: pressing
 * Option+letter or Option+digit (with or without Shift) is reported as the
 * corresponding {@code VK_*} with {@link java.awt.event.InputEvent#ALT_DOWN_MASK}
 * only, so {@code KeyDownEvent.isAltDown()} returns {@code true} and the
 * shortcut renders as {@code "Alt+..."}.
 *
 * <p>The OS delivers each Option-modified key as a UTF-8 sequence of the
 * Unicode glyph it would produce on the US layout (e.g. Option+X → ≈, U+2248).
 * {@link #macOptionCodeToAlt(int)} reverses that mapping back to the original
 * AWT virtual-key code.
 */
class MacosEventQueue extends UnixEventQueue {

    /**
     * Maps a Unicode code point produced by macOS Option+key (US keyboard) to
     * the corresponding AWT virtual-key code. Both Option+key and Shift+Option+key
     * code points map to the same plain {@code VK_*} so that the resulting
     * event always reads as {@code Alt+key} regardless of Shift state.
     *
     * @param codePoint the Unicode code point to test
     * @return the AWT {@code VK_*} code, or {@code 0} if not an Option+key glyph
     */
    @Override
    protected int macOptionCodeToAlt(int codePoint) {
        switch (codePoint) {
            // letters: Option+letter and Shift+Option+letter
            case 0x00E5: case 0x00C5: return KeyEvent.VK_A; // å, Å
            case 0x222B: case 0x0131: return KeyEvent.VK_B; // ∫, ı
            case 0x00E7: case 0x00C7: return KeyEvent.VK_C; // ç, Ç
            case 0x2202: case 0x00CE: return KeyEvent.VK_D; // ∂, Î
            case 0x00B4:              return KeyEvent.VK_E; // ´ (dead key, both Option+E and Shift+Option+E)
            case 0x0192: case 0x00CF: return KeyEvent.VK_F; // ƒ, Ï
            case 0x00A9: case 0x02DD: return KeyEvent.VK_G; // ©, ˝
            case 0x02D9: case 0x00D3: return KeyEvent.VK_H; // ˙, Ó
            case 0x02C6:              return KeyEvent.VK_I; // ˆ (dead key)
            case 0x2206: case 0x00D4: return KeyEvent.VK_J; // ∆, Ô
            case 0x02DA: case 0xF8FF: return KeyEvent.VK_K; // ˚,  (Apple logo)
            case 0x00AC: case 0x00D2: return KeyEvent.VK_L; // ¬, Ò
            case 0x00B5: case 0x00C2: return KeyEvent.VK_M; // µ, Â
            case 0x02DC:              return KeyEvent.VK_N; // ˜ (dead key)
            case 0x00F8: case 0x00D8: return KeyEvent.VK_O; // ø, Ø
            case 0x03C0: case 0x220F: return KeyEvent.VK_P; // π, ∏
            case 0x0153: case 0x0152: return KeyEvent.VK_Q; // œ, Œ
            case 0x00AE: case 0x2030: return KeyEvent.VK_R; // ®, ‰
            case 0x00DF: case 0x00CD: return KeyEvent.VK_S; // ß, Í
            case 0x2020: case 0x02C7: return KeyEvent.VK_T; // †, ˇ
            case 0x00A8:              return KeyEvent.VK_U; // ¨ (dead key)
            case 0x221A: case 0x25CA: return KeyEvent.VK_V; // √, ◊
            case 0x2211: case 0x201E: return KeyEvent.VK_W; // ∑, „
            case 0x2248: case 0x02DB: return KeyEvent.VK_X; // ≈, ˛
            case 0x00A5: case 0x00C1: return KeyEvent.VK_Y; // ¥, Á
            case 0x03A9: case 0x00B8: return KeyEvent.VK_Z; // Ω, ¸

            // digits: Option+digit and Shift+Option+digit
            case 0x00A1: case 0x2044: return KeyEvent.VK_1; // ¡, ⁄
            case 0x2122: case 0x20AC: return KeyEvent.VK_2; // ™, €
            case 0x00A3: case 0x2039: return KeyEvent.VK_3; // £, ‹
            case 0x00A2: case 0x203A: return KeyEvent.VK_4; // ¢, ›
            case 0x221E: case 0xFB01: return KeyEvent.VK_5; // ∞, ﬁ
            case 0x00A7: case 0xFB02: return KeyEvent.VK_6; // §, ﬂ
            case 0x00B6: case 0x2021: return KeyEvent.VK_7; // ¶, ‡
            case 0x2022: case 0x00B0: return KeyEvent.VK_8; // •, °
            case 0x00AA: case 0x00B7: return KeyEvent.VK_9; // ª, ·
            case 0x00BA: case 0x201A: return KeyEvent.VK_0; // º, ‚

            default: return 0;
        }
    }
}
