/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.dialogs;

import java.io.File;

import lombok.Getter;

/**
 * An immutable snapshot of a single file-system entry used by {@link JtvFileList}.
 * <p>
 * JtvFileRecord holds the metadata for one file or directory: its display name,
 * absolute path, whether it is a directory, its size in bytes, and its last-modified
 * timestamp. The special name {@code ".."} is used for the parent directory entry.
 * <p>
 * Instances are created via the static factory methods {@link #fromFile(File)} and
 * {@link #parent(String)}; the default no-arg constructor produces an empty record
 * suitable as a placeholder.
 */
public class JtvFileRecord {

    /** The file or directory name (not the full path). {@code ".."} denotes the parent directory. */
	@Getter
    private String name;

	/** The absolute path of this entry. */
	@Getter
    private String absolutePath;

	/** {@code true} if this entry is a directory. */
	@Getter
    private boolean directory;

	/** File size in bytes; 0 for directories. */
	@Getter
    private long size;

	/** Last-modified timestamp (milliseconds since epoch); 0 for the parent entry. */
	@Getter
    private long lastModified;

	/**
     * Creates an empty file record. All fields are initialised to {@code null} / 0.
     * Useful as a placeholder before real data is assigned.
     */
    public JtvFileRecord() {
    }

    /**
     * Creates a file record from an existing {@link File} object.
     *
     * @param file the file or directory to snapshot
     * @return a populated {@link JtvFileRecord}
     */
    public static JtvFileRecord fromFile(File file) {
        JtvFileRecord r = new JtvFileRecord();
        r.name = file.getName();
        if (r.name == null || r.name.isEmpty()) {
            r.name = file.getPath();
        }
        r.absolutePath = file.getAbsolutePath();
        r.directory = file.isDirectory();
        r.size = file.isFile() ? file.length() : 0L;
        r.lastModified = file.lastModified();
        return r;
    }

    /**
     * Creates a synthetic parent-directory record with the name {@code ".."}.
     *
     * @param parentPath the absolute path of the parent directory
     * @return a {@link JtvFileRecord} representing the parent directory entry
     */
    public static JtvFileRecord parent(String parentPath) {
        JtvFileRecord r = new JtvFileRecord();
        r.name = "..";
        r.absolutePath = parentPath;
        r.directory = true;
        r.size = 0L;
        r.lastModified = 0L;
        return r;
    }
}
