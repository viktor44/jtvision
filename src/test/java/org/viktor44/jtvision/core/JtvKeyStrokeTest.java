/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class JtvKeyStrokeTest {

    // ---- no modifiers -------------------------------------------------------

    @Test
    void plainLetter() {
        assertEquals("A", JtvKeyStroke.of(KeyEvent.VK_A).toString());
    }

    @Test
    void plainEscape() {
        assertEquals("Escape", JtvKeyStroke.of(KeyEvent.VK_ESCAPE).toString());
    }

    @Test
    void plainEnter() {
        assertEquals("Enter", JtvKeyStroke.of(KeyEvent.VK_ENTER).toString());
    }

    @Test
    void plainTab() {
        assertEquals("Tab", JtvKeyStroke.of(KeyEvent.VK_TAB).toString());
    }

    @Test
    void plainF1() {
        assertEquals("F1", JtvKeyStroke.of(KeyEvent.VK_F1).toString());
    }

    @Test
    void plainF10() {
        assertEquals("F10", JtvKeyStroke.of(KeyEvent.VK_F10).toString());
    }

    @Test
    void plainArrowUp() {
        assertEquals("Up", JtvKeyStroke.of(KeyEvent.VK_UP).toString());
    }

    @Test
    void plainDelete() {
        assertEquals("Delete", JtvKeyStroke.of(KeyEvent.VK_DELETE).toString());
    }

    @Test
    void plainBackspace() {
        assertEquals("Backspace", JtvKeyStroke.of(KeyEvent.VK_BACK_SPACE).toString());
    }

    // ---- single modifier ----------------------------------------------------

    @Test
    void ctrlA() {
        assertEquals("Ctrl+A",
            JtvKeyStroke.of(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK).toString());
    }

    @Test
    void altA() {
        assertEquals("Alt+A",
            JtvKeyStroke.of(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK).toString());
    }

    @Test
    void shiftF5() {
        assertEquals("Shift+F5",
            JtvKeyStroke.of(KeyEvent.VK_F5, InputEvent.SHIFT_DOWN_MASK).toString());
    }

    // ---- two modifiers ------------------------------------------------------

    @Test
    void ctrlAlt() {
        assertEquals("Ctrl+Alt+A",
            JtvKeyStroke.of(KeyEvent.VK_A,
                InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK).toString());
    }

    @Test
    void ctrlShift() {
        assertEquals("Ctrl+Shift+Delete",
            JtvKeyStroke.of(KeyEvent.VK_DELETE,
                InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK).toString());
    }

    @Test
    void altShiftF5() {
        assertEquals("Alt+Shift+F5",
            JtvKeyStroke.of(KeyEvent.VK_F5,
                InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK).toString());
    }

    // ---- three modifiers ----------------------------------------------------

    @Test
    void ctrlAltShift() {
        assertEquals("Ctrl+Alt+Shift+F1",
            JtvKeyStroke.of(KeyEvent.VK_F1,
                InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK).toString());
    }

    // ---- modifier order is always Ctrl, Alt, Shift --------------------------

    @Test
    void modifierOrderIsCtrlAltShift() {
        int all = InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
        assertEquals("Ctrl+Alt+Shift+A", JtvKeyStroke.of(KeyEvent.VK_A, all).toString());
    }

    // ---- navigation keys with modifiers -------------------------------------

    @Test
    void ctrlHome() {
        assertEquals("Ctrl+Home",
            JtvKeyStroke.of(KeyEvent.VK_HOME, InputEvent.CTRL_DOWN_MASK).toString());
    }

    @Test
    void shiftTab() {
        assertEquals("Shift+Tab",
            JtvKeyStroke.of(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK).toString());
    }

    // ---- all Ctrl+letter combinations via parameterized test ----------------

    @ParameterizedTest
    @CsvSource({
        "65,  A",  "66,  B",  "67,  C",  "68,  D",  "69,  E",
        "70,  F",  "71,  G",  "72,  H",  "73,  I",  "74,  J",
        "75,  K",  "76,  L",  "77,  M",  "78,  N",  "79,  O",
        "80,  P",  "81,  Q",  "82,  R",  "83,  S",  "84,  T",
        "85,  U",  "86,  V",  "87,  W",  "88,  X",  "89,  Y",
        "90,  Z"
    })
    void ctrlAllLetters(int vk, String letter) {
        assertEquals("Ctrl+" + letter.trim(),
            JtvKeyStroke.of(vk, InputEvent.CTRL_DOWN_MASK).toString());
    }
}
