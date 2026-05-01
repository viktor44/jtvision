/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

/**
 * A compact set of up to 256 command codes, stored as a 256-bit array (32 bytes).
 *
 * <p>Every JT Vision view owns a reference to the application-wide current command set
 * ({@code JtvView.curCommandSet}).  Commands whose bit is <em>clear</em> are disabled, so their
 * associated menu items and buttons appear greyed out.  Only command codes in the range
 * {@code 0..255} can be tracked; codes outside that range are silently ignored.
 *
 * <p>Example — disable a group of window commands:
 * <pre>{@code
 * TCommandSet windowCmds = new TCommandSet();
 * windowCmds.enableCmd(cmNext);
 * windowCmds.enableCmd(cmPrev);
 * windowCmds.enableCmd(cmZoom);
 * windowCmds.enableCmd(cmResize);
 * windowCmds.enableCmd(cmClose);
 * disableCommands(windowCmds);
 * }</pre>
 */
public class JtvCommandSet {
    private final byte[] cmds = new byte[32];

    /** Constructs an empty command set (all commands disabled). */
    public JtvCommandSet() {
    }

    /**
     * Copy constructor.
     *
     * @param other the command set to copy
     */
    public JtvCommandSet(JtvCommandSet other) {
        System.arraycopy(other.cmds, 0, cmds, 0, 32);
    }

    /**
     * Returns {@code true} if the given command is present (enabled) in this set.
     *
     * @param cmd command code in the range {@code 0..255}
     * @return {@code true} if the bit for {@code cmd} is set
     */
    public boolean has(int cmd) {
        if (cmd < 0 || cmd > 255) return false;
        return (cmds[cmd / 8] & (1 << (cmd % 8))) != 0;
    }

    /**
     * Adds a single command to this set.
     *
     * @param cmd command code in the range {@code 0..255}
     */
    public void enableCmd(int cmd) {
        if (cmd >= 0 && cmd <= 255) {
            cmds[cmd / 8] |= (byte) (1 << (cmd % 8));
        }
    }

    /**
     * Removes a single command from this set.
     *
     * @param cmd command code in the range {@code 0..255}
     */
    public void disableCmd(int cmd) {
        if (cmd >= 0 && cmd <= 255) {
            cmds[cmd / 8] &= (byte) ~(1 << (cmd % 8));
        }
    }

    /**
     * Adds all commands from {@code other} to this set (bitwise OR in place).
     *
     * @param other the commands to enable
     */
    public void enableCmd(JtvCommandSet other) {
        for (int i = 0; i < 32; i++) {
            cmds[i] |= other.cmds[i];
        }
    }

    /**
     * Removes all commands that are present in {@code other} from this set (bitwise AND-NOT in place).
     *
     * @param other the commands to disable
     */
    public void disableCmd(JtvCommandSet other) {
        for (int i = 0; i < 32; i++) {
            cmds[i] &= ~other.cmds[i];
        }
    }

    /**
     * Returns {@code true} if no command bits are set.
     *
     * @return {@code true} if the set is empty
     */
    public boolean isEmpty() {
        for (int i = 0; i < 32; i++) {
            if (cmds[i] != 0) return false;
        }
        return true;
    }

    /**
     * Returns a new command set containing only the commands present in both this set and {@code other}.
     *
     * @param other the set to intersect with
     * @return bitwise AND of the two sets
     */
    public JtvCommandSet and(JtvCommandSet other) {
        JtvCommandSet result = new JtvCommandSet();
        for (int i = 0; i < 32; i++) {
            result.cmds[i] = (byte) (cmds[i] & other.cmds[i]);
        }
        return result;
    }

    /**
     * Returns a new command set containing all commands present in either this set or {@code other}.
     *
     * @param other the set to union with
     * @return bitwise OR of the two sets
     */
    public JtvCommandSet or(JtvCommandSet other) {
        JtvCommandSet result = new JtvCommandSet();
        for (int i = 0; i < 32; i++) {
            result.cmds[i] = (byte) (cmds[i] | other.cmds[i]);
        }
        return result;
    }

    /**
     * Copies the contents of {@code other} into this set, replacing all current bits.
     *
     * @param other source command set
     */
    public void assign(JtvCommandSet other) {
        System.arraycopy(other.cmds, 0, cmds, 0, 32);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof JtvCommandSet)) return false;
        JtvCommandSet other = (JtvCommandSet) obj;
        for (int i = 0; i < 32; i++) {
            if (cmds[i] != other.cmds[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < 32; i++) {
            hash = 31 * hash + cmds[i];
        }
        return hash;
    }
}
