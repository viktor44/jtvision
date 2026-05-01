/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.util;

/**
 * String utility functions ported from JT Vision.
 */
public final class StringUtils {

    private StringUtils() {}

    /**
     * Extract the hotkey character from a string with ~ delimiters. e.g., "~F~ile" returns 'F'.
     */
    public static char hotKey(String s) {
        if (s == null) return '\0';
        int idx = s.indexOf('~');
        if (idx >= 0 && idx + 1 < s.length()) {
            return Character.toUpperCase(s.charAt(idx + 1));
        }
        return '\0';
    }

    /**
     * Return the hotkey substring from a ~-delimited string
     * 
     * @param s - string
     * @return the hotkey substring from a ~-delimited string
     */
    public static String hotKeyStr(String s) {
        if (s == null) return "";
        int start = s.indexOf('~');
        if (start < 0) return "";
        start++; // skip the ~
        int end = s.indexOf('~', start);
        if (end < 0) end = s.length();
        return s.substring(start, end);
    }

    /**
     * Calculate the display width of a CStr (string with ~ hotkey markers removed).
     */
    public static int cstrLen(String s) {
        if (s == null) return 0;
        int len = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '~') {
                len++;
            }
        }
        return len;
    }

    /**
     * Calculate the display width of a plain string.
     */
    public static int strWidth(String s) {
        return (s != null) ? s.length() : 0;
    }
}
