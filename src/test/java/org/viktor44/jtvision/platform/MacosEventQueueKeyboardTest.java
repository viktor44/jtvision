/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.provider.Arguments;
import org.viktor44.jtvision.core.JtvKeyStroke;

/**
 * Verifies ANSI/VT escape-sequence parsing on macOS. Reuses the common cases
 * from {@link AbstractUnixEventQueueKeyboardTest} and adds the macOS-specific
 * Option-key UTF-8 mappings handled by
 * {@link MacosEventQueue#macOptionCodeToAlt(int)}.
 *
 * <p>Every Option+letter / Option+digit (and the Shift+Option variants) must
 * resolve to the corresponding {@code VK_*} with
 * {@link InputEvent#ALT_DOWN_MASK} only, so that {@code isAltDown()} returns
 * {@code true} and the shortcut renders as {@code "Alt+..."}.
 */
@EnabledOnOs(OS.MAC)
class MacosEventQueueKeyboardTest extends AbstractUnixEventQueueKeyboardTest {

    @Override
    protected UnixEventQueue newQueue(InputStream pipeIn) {
        return new MacosEventQueue() {
            @Override
            protected InputStream openInputStream() { return pipeIn; }
            @Override
            protected void enableRawTerminalMode() {}
            @Override
            protected void disableRawTerminalMode() {}
        };
    }

    @Override
    protected Stream<Arguments> additionalKeySequences() {
        // Each row: { VK, Option+key code point, Shift+Option+key code point }
        // For dead-key rows the two code points are equal (only one case is added).
        int[][] rows = {
            { KeyEvent.VK_A, 0x00E5, 0x00C5 },
            { KeyEvent.VK_B, 0x222B, 0x0131 },
            { KeyEvent.VK_C, 0x00E7, 0x00C7 },
            { KeyEvent.VK_D, 0x2202, 0x00CE },
            { KeyEvent.VK_E, 0x00B4, 0x00B4 },
            { KeyEvent.VK_F, 0x0192, 0x00CF },
            { KeyEvent.VK_G, 0x00A9, 0x02DD },
            { KeyEvent.VK_H, 0x02D9, 0x00D3 },
            { KeyEvent.VK_I, 0x02C6, 0x02C6 },
            { KeyEvent.VK_J, 0x2206, 0x00D4 },
            { KeyEvent.VK_K, 0x02DA, 0xF8FF },
            { KeyEvent.VK_L, 0x00AC, 0x00D2 },
            { KeyEvent.VK_M, 0x00B5, 0x00C2 },
            { KeyEvent.VK_N, 0x02DC, 0x02DC },
            { KeyEvent.VK_O, 0x00F8, 0x00D8 },
            { KeyEvent.VK_P, 0x03C0, 0x220F },
            { KeyEvent.VK_Q, 0x0153, 0x0152 },
            { KeyEvent.VK_R, 0x00AE, 0x2030 },
            { KeyEvent.VK_S, 0x00DF, 0x00CD },
            { KeyEvent.VK_T, 0x2020, 0x02C7 },
            { KeyEvent.VK_U, 0x00A8, 0x00A8 },
            { KeyEvent.VK_V, 0x221A, 0x25CA },
            { KeyEvent.VK_W, 0x2211, 0x201E },
            { KeyEvent.VK_X, 0x2248, 0x02DB },
            { KeyEvent.VK_Y, 0x00A5, 0x00C1 },
            { KeyEvent.VK_Z, 0x03A9, 0x00B8 },

            { KeyEvent.VK_1, 0x00A1, 0x2044 },
            { KeyEvent.VK_2, 0x2122, 0x20AC },
            { KeyEvent.VK_3, 0x00A3, 0x2039 },
            { KeyEvent.VK_4, 0x00A2, 0x203A },
            { KeyEvent.VK_5, 0x221E, 0xFB01 },
            { KeyEvent.VK_6, 0x00A7, 0xFB02 },
            { KeyEvent.VK_7, 0x00B6, 0x2021 },
            { KeyEvent.VK_8, 0x2022, 0x00B0 },
            { KeyEvent.VK_9, 0x00AA, 0x00B7 },
            { KeyEvent.VK_0, 0x00BA, 0x201A },
        };

        List<Arguments> args = new ArrayList<>();
        for (int[] row : rows) {
            args.add(of(JtvKeyStroke.of(row[0], InputEvent.ALT_DOWN_MASK), utf8(row[1])));
            if (row[2] != row[1]) {
                args.add(of(JtvKeyStroke.of(row[0], InputEvent.ALT_DOWN_MASK), utf8(row[2])));
            }
        }
        return args.stream();
    }

    /** Encodes a Unicode code point (BMP) into its UTF-8 byte sequence. */
    private static int[] utf8(int cp) {
        if (cp < 0x80) {
            return new int[] { cp };
        }
        if (cp < 0x800) {
            return new int[] {
                0xC0 | (cp >> 6),
                0x80 | (cp & 0x3F)
            };
        }
        return new int[] {
            0xE0 | (cp >> 12),
            0x80 | ((cp >> 6) & 0x3F),
            0x80 | (cp & 0x3F)
        };
    }
}
