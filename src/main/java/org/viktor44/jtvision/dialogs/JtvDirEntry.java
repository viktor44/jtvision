/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import lombok.Getter;

/**
 * A data object representing a single entry in the directory tree list.
 * <p>
 * JtvDirEntry pairs a formatted display string (with indentation and prefix
 * characters that visualise the tree structure) with the absolute path of the
 * directory it represents. Instances are created internally by
 * {@link JtvDirListBox} and passed as event payloads in {@code cmChangeDir}
 * command events.
 */
public class JtvDirEntry {

    /** The formatted text shown in the directory list (includes tree-drawing prefix characters). */
	@Getter
    private final String displayText;

    /** The absolute path of the directory this entry represents. */
    @Getter
    private final String directory;

    /**
     * Creates a directory entry with the given display text and path.
     *
     * @param txt the formatted display string (tree prefix + directory name)
     * @param dir the absolute directory path
     */
    public JtvDirEntry(String txt, String dir) {
        displayText = txt;
        directory = dir;
    }
}
