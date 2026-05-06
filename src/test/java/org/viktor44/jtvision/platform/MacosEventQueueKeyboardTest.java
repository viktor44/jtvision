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
        List<Arguments> args = new ArrayList<>();

        // Option+X on macOS US keyboard produces ≈ (U+2248), UTF-8: 0xE2 0x89 0x88
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK),
            0xE2, 0x89, 0x88));
        // Shift+Option+X on macOS US keyboard produces ˛ (U+02DB), UTF-8: 0xCB 0x9B
        args.add(of(JtvKeyStroke.of(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK),
            0xCB, 0x9B));

        return args.stream();
    }
}
