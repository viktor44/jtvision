/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import java.io.InputStream;

import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * Verifies ANSI/VT escape-sequence parsing on Linux by injecting bytes
 * directly into the EventQueue input stream and asserting the resulting
 * KeyDownEvent fields. All cases come from
 * {@link AbstractUnixEventQueueKeyboardTest#commonKeySequences()}.
 */
@EnabledOnOs(OS.LINUX)
class LinuxEventQueueKeyboardTest extends AbstractUnixEventQueueKeyboardTest {

    @Override
    protected UnixEventQueue newQueue(InputStream pipeIn) {
        return new LinuxEventQueue() {
            @Override
            protected InputStream openInputStream() { return pipeIn; }
            @Override
            protected void enableRawTerminalMode() {}
            @Override
            protected void disableRawTerminalMode() {}
        };
    }
}
