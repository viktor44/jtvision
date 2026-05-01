package org.viktor44.jtvision.examples.test;

import static org.viktor44.jtvision.core.EventCodes.evKeyDown;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.KeyDownEvent;
import org.viktor44.jtvision.platform.EventQueue;

/**
 * Keyboard demo using {@link EventQueue}.
 * <p>
 * Reads key events from the platform event queue and prints each key's code
 * and modifier state to {@code System.out}. The Java AWT runtime is forced
 * into headless mode; no display is required. Press <b>Ctrl+Q</b> or
 * <b>Ctrl+C</b> to exit.
 */
public final class TestKeysApp {

    private TestKeysApp() {}

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        System.out.println("TestKeysApp - press any key (Ctrl+Q or Ctrl+C to quit).");
        System.out.println();

        Runtime.getRuntime().addShutdownHook(new Thread(EventQueue::shutdown, "TestKeysApp-shutdown"));
        EventQueue.init();

        JtvEvent event = new JtvEvent();
        boolean running = true;
        while (running) {
            EventQueue.waitForEvents(100);
            EventQueue.getKeyEvent(event);
            if (event.getWhat() != evKeyDown) continue;

            KeyDownEvent kd = event.getKeyDown();
            int vk       = kd.getKeyCode();
            int mods     = kd.getModifiers();
            char keyChar = kd.getKeyChar();

            report(vk, mods, keyChar);

            if ((mods & InputEvent.CTRL_DOWN_MASK) != 0 && (vk == KeyEvent.VK_Q || vk == KeyEvent.VK_C)) {
                System.out.print("(exit)\r\n");
                running = false;
            }
        }

        EventQueue.shutdown();
    }

    private static void report(int keyCode, int modifiers, char keyChar) {
        String name    = KeyEvent.getKeyText(keyCode);
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
        System.out.print(sb.toString() + "\r\n");
        System.out.flush();
    }
}
