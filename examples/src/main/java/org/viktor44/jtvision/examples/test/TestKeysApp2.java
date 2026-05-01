package org.viktor44.jtvision.examples.test;

import java.awt.Frame;
import java.awt.Label;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Simple AWT keyboard demo.
 * <p>
 * Opens an AWT window and prints the key code and modifiers to {@code System.out}
 * whenever a key is pressed. Close the window or press <b>Escape</b> to exit.
 * <p>
 * Output format matches {@code TestKeysApp}. Character is read from
 * {@code keyTyped} because AWT's {@code keyPressed} may return
 * {@code CHAR_UNDEFINED} for printable keys on some platforms.
 */
public final class TestKeysApp2 {

    private TestKeysApp2() {}

    public static void main(String[] args) {
        Frame frame = new Frame("TestKeysApp2 - press any key (Esc to quit)");
        frame.add(new Label("Focus this window and press any key"));
        frame.setSize(400, 120);

        frame.addKeyListener(new KeyAdapter() {
            // keyCode/modifiers saved in keyPressed, consumed in keyTyped or keyReleased
            private int pendingCode;
            private int pendingMods;

            @Override
            public void keyPressed(KeyEvent e) {
                // Flush any pending non-printable key that never got a keyTyped
                flushPending();

                int keyCode = e.getKeyCode();
                int modifiers = e.getModifiersEx();
                // AWT omits the just-pressed modifier key from modifiersEx
                if      (keyCode == KeyEvent.VK_SHIFT)     modifiers |= InputEvent.SHIFT_DOWN_MASK;
                else if (keyCode == KeyEvent.VK_CONTROL)   modifiers |= InputEvent.CTRL_DOWN_MASK;
                else if (keyCode == KeyEvent.VK_ALT)       modifiers |= InputEvent.ALT_DOWN_MASK;
                else if (keyCode == KeyEvent.VK_META)      modifiers |= InputEvent.META_DOWN_MASK;
                else if (keyCode == KeyEvent.VK_ALT_GRAPH) modifiers |= InputEvent.ALT_GRAPH_DOWN_MASK;

                // Modifier/lock keys never produce keyTyped — output immediately
                if (isModifierKey(keyCode)) {
                    report(keyCode, modifiers, KeyEvent.CHAR_UNDEFINED);
                } else {
                    pendingCode = keyCode;
                    pendingMods = modifiers;
                }

                if (keyCode == KeyEvent.VK_ESCAPE) frame.dispose();
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // keyTyped always carries the correct Unicode character
                if (pendingCode != 0) {
                    report(pendingCode, pendingMods, e.getKeyChar());
                    pendingCode = 0;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Non-printable key (F1, arrow, etc.) — no keyTyped ever fired
                if (pendingCode == e.getKeyCode()) {
                    flushPending();
                }
            }

            private void flushPending() {
                if (pendingCode != 0) {
                    report(pendingCode, pendingMods, KeyEvent.CHAR_UNDEFINED);
                    pendingCode = 0;
                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.println("Exiting.");
                System.exit(0);
            }
        });

        frame.setVisible(true);
    }

    private static boolean isModifierKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_ALT:
            case KeyEvent.VK_META:
            case KeyEvent.VK_ALT_GRAPH:
            case KeyEvent.VK_CAPS_LOCK:
            case KeyEvent.VK_NUM_LOCK:
            case KeyEvent.VK_SCROLL_LOCK:
                return true;
            default:
                return false;
        }
    }

    private static void report(int keyCode, int modifiers, char keyChar) {
        String name = KeyEvent.getKeyText(keyCode);
        String modText = modifiers == 0 ? "none" : InputEvent.getModifiersExText(modifiers);
        String charText;
        if (keyChar == KeyEvent.CHAR_UNDEFINED || keyChar == 0) {
            charText = "";
        } else if (keyChar >= 32 && keyChar != 127) {
            charText = "  char='" + keyChar + "'";
        } else {
            charText = String.format("  char=0x%02X", (int) keyChar);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Key: ").append(name)
          .append("  code=").append(keyCode)
          .append(" (0x").append(String.format("%04X", keyCode)).append(")")
          .append("  modifiers=0x").append(String.format("%X", modifiers))
          .append(" [").append(modText).append("]")
          .append(charText);
        System.out.println(sb);
    }
}
