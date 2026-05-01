/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.help;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A binary help-file loader and topic registry for the JTVision help system.
 * <p>
 * JtvHelpFile reads a file in the custom JTVHELP binary format, parses each
 * help topic together with its paragraph text and cross-references, and
 * stores them in an internal map keyed by integer help-context IDs.
 * <p>
 * The binary format is:
 * <ol>
 *   <li>8-byte magic marker {@code "JTVHELP\0"}.</li>
 *   <li>4-byte format version (must equal {@code 1}).</li>
 *   <li>4-byte topic count.</li>
 *   <li>For each topic:
 *     <ul>
 *       <li>4-byte ID count followed by that many 4-byte context IDs
 *           (a single topic may respond to multiple context values).</li>
 *       <li>4-byte paragraph count; for each paragraph: a boolean flag,
 *           a 4-byte text length, ISO-8859-1-encoded paragraph bytes,
 *           a 4-byte reference count, and for each reference four 4-byte
 *           integers followed by a length-prefixed reference name.</li>
 *     </ul>
 *   </li>
 * </ol>
 * <p>
 * Once loaded, individual topics are retrieved via {@link #getTopic(int)}.
 * A synthetic "No help available" topic is returned for unknown context IDs.
 * Topics are always returned as defensive copies so callers cannot mutate
 * the internal state.
 * <p>
 * Topics can also be inserted programmatically via {@link #putTopic(int, JtvHelpTopic)}.
 *
 * @see JtvHelpTopic
 * @see JtvHelpViewer
 */
public class JtvHelpFile {

    /** Internal map from help-context ID to the corresponding topic. */
    private final Map<Integer, JtvHelpTopic> topics = new HashMap<Integer, JtvHelpTopic>();

    /**
     * File signature written at the start of every JTVHELP file.
     * The eight bytes spell {@code "JTVHELP"} followed by a NUL terminator.
     */
    private static final byte[] magic = {'J', 'T', 'V', 'H', 'E', 'L', 'P', 0};

    /** The only accepted help-file format version number. */
    private static final int formatVersion = 1;

    /**
     * Loads a help file from the given path and returns the populated
     * {@code THelpFile} instance.
     * <p>
     * The file must start with the JTVHELP magic signature and declare
     * version {@code 1}. Each topic's paragraphs are concatenated with
     * newline separators and the assembled text is passed to
     * {@link JtvHelpTopic#fromText(String)}. The resulting topic is registered
     * under every context ID listed for that topic entry.
     *
     * @param file path to the JTVHELP binary file
     * @return a fully loaded {@code THelpFile}
     * @throws IOException if the file cannot be read, the magic signature does
     *                     not match, the version is unsupported, or the file
     *                     structure is otherwise corrupt
     */
    public static JtvHelpFile load(Path file) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            byte[] actualMagic = new byte[magic.length];
            in.readFully(actualMagic);
            if (!Arrays.equals(actualMagic, magic)) {
                throw new IOException("Unsupported help file format: " + file);
            }

            int version = in.readInt();
            if (version != formatVersion) {
                throw new IOException("Unsupported help file version " + version + " in " + file);
            }

            int topicCount = in.readInt();
            if (topicCount < 0) {
                throw new IOException("Corrupt help file: negative topic count");
            }

            JtvHelpFile helpFile = new JtvHelpFile();
            for (int t = 0; t < topicCount; t++) {
                int idCount = in.readInt();
                if (idCount <= 0) {
                    throw new IOException("Corrupt help file: topic has no ids");
                }
                int[] ids = new int[idCount];
                for (int i = 0; i < idCount; i++) {
                    ids[i] = in.readInt();
                }

                int paragraphCount = in.readInt();
                if (paragraphCount < 0) {
                    throw new IOException("Corrupt help file: negative paragraph count");
                }

                StringBuilder text = new StringBuilder();
                for (int p = 0; p < paragraphCount; p++) {
                    in.readBoolean();
                    int textLen = in.readInt();
                    if (textLen < 0) {
                        throw new IOException("Corrupt help file: negative paragraph length");
                    }

                    byte[] bytes = new byte[textLen];
                    in.readFully(bytes);
                    String paragraph = new String(bytes, StandardCharsets.ISO_8859_1).replace('ÿ', ' ');
                    if (text.length() > 0 && text.charAt(text.length() - 1) != '\n') {
                        text.append('\n');
                    }
                    text.append(paragraph);
                    if (text.length() > 0 && text.charAt(text.length() - 1) != '\n') {
                        text.append('\n');
                    }

                    int refCount = in.readInt();
                    if (refCount < 0) {
                        throw new IOException("Corrupt help file: negative reference count");
                    }
                    for (int r = 0; r < refCount; r++) {
                        in.readInt();
                        in.readInt();
                        in.readInt();
                        int nameLen = in.readInt();
                        if (nameLen < 0) {
                            throw new IOException("Corrupt help file: negative reference name length");
                        }
                        byte[] refNameBytes = new byte[nameLen];
                        in.readFully(refNameBytes);
                    }
                }

                JtvHelpTopic topic = JtvHelpTopic.fromText(text.toString());
                for (int id : ids) {
                    helpFile.putTopic(id, topic);
                }
            }

            return helpFile;
        }
    }

    /**
     * Registers a topic under the given help-context ID.
     * Any previously registered topic for {@code id} is replaced.
     *
     * @param id    the integer help-context ID
     * @param topic the topic to associate with that ID
     */
    public void putTopic(int id, JtvHelpTopic topic) {
        topics.put(Integer.valueOf(id), topic);
    }

    /**
     * Returns a defensive copy of the topic registered for {@code id}.
     * <p>
     * If no topic has been registered for the requested ID, a synthetic
     * topic containing the text {@code "No help available for context <id>."}
     * is returned instead.
     *
     * @param id the help-context ID to look up
     * @return a copy of the matching topic, never {@code null}
     */
    public JtvHelpTopic getTopic(int id) {
        JtvHelpTopic topic = topics.get(Integer.valueOf(id));
        if (topic == null) {
            topic = JtvHelpTopic.fromText("No help available for context " + id + ".");
        }
        return new JtvHelpTopic(topic);
    }
}
