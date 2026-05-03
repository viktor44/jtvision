/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.platform;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.internal.CLibrary;
import org.fusesource.jansi.internal.CLibrary.WinSize;
import org.viktor44.jtvision.core.JtvColorAttr;
import org.viktor44.jtvision.core.JtvScreenCell;
import org.viktor44.jtvision.util.SystemUtils;
import org.viktor44.jtvision.views.JtvView;
import org.fusesource.jansi.internal.Kernel32;

import java.io.PrintStream;

/**
 * Low-level terminal screen manager for the jtvision TUI framework.
 * <p>
 * Screen corresponds to the JT Vision {@code Screen} unit: it owns the
 * physical terminal surface, maintains the logical {@link #screenBuffer} of
 * {@link JtvScreenCell} objects, and provides a dirty-cell optimised flush that
 * converts the buffer to ANSI escape sequences.
 * 
 * <h3>Lifecycle</h3>
 * 
 * Call {@link #init()} once at startup and {@link #shutdown()} at exit.
 * {@link #init()} installs Jansi, detects the terminal size, allocates both
 * the front buffer ({@link #screenBuffer}) and the shadow buffer
 * ({@code lastBuffer}), then switches the terminal to the alternate screen
 * buffer and enables SGR mouse mode. {@link #shutdown()} reverses all of that.
 * 
 * <h3>Double-buffer flush</h3>
 * 
 * {@link #flushScreen()} compares every cell in {@link #screenBuffer} against
 * the corresponding entry in the private {@code lastBuffer}. Only cells whose
 * character or attribute differ are written to the terminal, using compact
 * {@code ESC[row;colH} cursor-position sequences and {@code ESC[fg;bgm} SGR
 * colour sequences. Cursor position and visibility are deferred and applied at
 * the end of each flush so they are sent exactly once per frame.
 * 
 * <h3>Screen mode</h3>
 * 
 * {@link #screenMode} holds a JT Vision screen-mode constant. The default
 * value {@code 0x0003} is {@code smCO80} — 80-column colour mode. Other TV
 * constants include {@code smMono} (0x0007), {@code smBW80} (0x0002), and
 * {@code smFont8x8} (0x0100).
 * 
 * <h3>Colour mapping</h3>
 * 
 * JT Vision uses the CGA/BIOS 4-bit attribute byte: bits 0–3 are the
 * foreground colour index (0–15) and bits 4–6 are the background colour index
 * (0–7). {@link #ANSI_FG} and {@link #ANSI_BG} translate these indices to the
 * corresponding ANSI SGR colour codes.
 * 
 * <h3>Cursor model</h3>
 * 
 * Cursor state is double-buffered. The {@code desired*} fields record what the
 * application has requested via {@link #setCursorPosition(int, int)},
 * {@link #setCursorVisible(boolean)}, and {@link #setCursorShape(boolean)}.
 * The {@code applied*} fields track what was last actually sent to the
 * terminal. {@link #flushScreen()} reconciles them at the end of each frame,
 * emitting only the escape sequences needed to reach the desired state.
 * 
 * <h3>Suspend / resume</h3>
 * 
 * {@link #suspend()} temporarily leaves the alternate screen (e.g. before
 * shelling out) and {@link #resume()} re-enters it. After resuming,
 * {@link #invalidate()} is called automatically to force a full redraw.
 *
 * @see EventQueue
 * @see JtvScreenCell
 */
public class Screen {

    /**
     * The current terminal width in columns.
     * <p>
     * Populated by {@link #detectSize()} and kept in sync with
     * {@link #screenBuffer}. The JT Vision default is 80.
     */
    public static int screenWidth = 80;

    /**
     * The current terminal height in rows.
     * <p>
     * Populated by {@link #detectSize()} and kept in sync with
     * {@link #screenBuffer}. The JT Vision default is 25.
     */
    public static int screenHeight = 25;

    /**
     * The active screen mode, encoded as a JT Vision screen-mode constant.
     * <p>
     * The default value {@code 0x0003} corresponds to {@code smCO80}
     * (80-column colour mode). Other JT Vision constants:
     * <ul>
     *   <li>{@code 0x0002} — {@code smBW80}: 80-column black-and-white.</li>
     *   <li>{@code 0x0007} — {@code smMono}: monochrome.</li>
     *   <li>{@code 0x0100} — {@code smFont8x8}: 8×8 pixel font (EGA/VGA 43/50-line mode).</li>
     * </ul>
     * Changed by {@link #setVideoMode(int)}.
     */
    public static int screenMode = 0x0003; // smCO80

    /**
     * The logical front-buffer: one {@link JtvScreenCell} per screen position,
     * stored in row-major order ({@code y * screenWidth + x}).
     * <p>
     * Views write into this array via {@link #writeToScreen(int, int, int, JtvScreenCell[], int, boolean)}.
     * {@link #flushScreen()} reads it and emits only the cells that differ
     * from the shadow buffer {@code lastBuffer}.
     */
    public static JtvScreenCell[] screenBuffer;

    /**
     * The cursor shape register, mirroring the JT Vision {@code CursorLines}
     * variable. A value of {@code 0} selects the default cursor shape; other
     * values are reserved for hardware-cursor line masks in the original PC
     * BIOS interface (unused in the ANSI terminal back-end).
     */
    public static int cursorLines = 0;

    /**
     * The column position that the cursor should be at after the next
     * {@link #flushScreen()} call (0-based). Set by
     * {@link #setCursorPosition(int, int)}.
     */
    private static int desiredCursorX = 0;

    /**
     * The row position that the cursor should be at after the next
     * {@link #flushScreen()} call (0-based). Set by
     * {@link #setCursorPosition(int, int)}.
     */
    private static int desiredCursorY = 0;

    /**
     * Whether the cursor should be visible after the next
     * {@link #flushScreen()} call. Set by {@link #setCursorVisible(boolean)}.
     */
    private static boolean desiredCursorVisible = false;

    /**
     * Whether the cursor should use the steady-block shape ({@code true}) or
     * the steady-underline shape ({@code false}) after the next
     * {@link #flushScreen()} call. Set by {@link #setCursorShape(boolean)}.
     */
    private static boolean desiredCursorBlock = false;

    /**
     * The column position that was last actually sent to the terminal.
     * Initialised to {@code -1} so the first flush always emits an explicit
     * cursor-position sequence.
     */
    private static int appliedCursorX = -1;

    /**
     * The row position that was last actually sent to the terminal.
     * Initialised to {@code -1} so the first flush always emits an explicit
     * cursor-position sequence.
     */
    private static int appliedCursorY = -1;

    /**
     * Whether the cursor was last reported as visible to the terminal.
     */
    private static boolean appliedCursorVisible = false;

    /**
     * Whether the cursor shape last sent to the terminal was the block shape.
     */
    private static boolean appliedCursorBlock = false;

    /**
     * The shadow (back) buffer used by {@link #flushScreen()} for dirty-cell
     * detection. Each entry mirrors the on-screen state of the corresponding
     * {@link #screenBuffer} cell. Cells are initialised with character
     * {@code '\0'} and attribute {@code -1} so that every cell is considered
     * dirty on the first flush. Reset to the same sentinel by
     * {@link #invalidate()}.
     */
    private static JtvScreenCell[] lastBuffer;

    /**
     * Whether {@link #init()} has been called without a subsequent
     * {@link #shutdown()}. Guards against double-initialisation and ensures
     * {@link #flushScreen()} is a no-op before the screen is set up.
     */
    private static boolean initialized = false;

    /**
     * The Jansi-installed output stream used for all terminal output.
     * Obtained from {@link AnsiConsole#out()} during {@link #init()} so that
     * Jansi's Windows-compatibility layer is active.
     */
    private static PrintStream out;

    /**
     * ANSI SGR foreground colour codes for the 16 CGA/BIOS colour indices.
     * <p>
     * The foreground colour index occupies bits 0–3 of a JT Vision
     * attribute byte. Indices 0–7 map to standard ANSI colours (SGR 30–37)
     * and indices 8–15 map to bright/high-intensity colours (SGR 90–97).
     * The ordering follows the CGA palette:
     * <ol start="0">
     *   <li>Black (30)</li>   <li>Blue (34)</li>
     *   <li>Green (32)</li>   <li>Cyan (36)</li>
     *   <li>Red (31)</li>     <li>Magenta (35)</li>
     *   <li>Brown/Yellow (33)</li> <li>Light Grey (37)</li>
     *   <li>Dark Grey (90)</li>    <li>Light Blue (94)</li>
     *   <li>Light Green (92)</li>  <li>Light Cyan (96)</li>
     *   <li>Light Red (91)</li>    <li>Light Magenta (95)</li>
     *   <li>Yellow (93)</li>       <li>White (97)</li>
     * </ol>
     */
    private static final int[] ANSI_FG = {
        30, 34, 32, 36, 31, 35, 33, 37,  // 0-7: normal colors
        90, 94, 92, 96, 91, 95, 93, 97   // 8-15: bright colors
    };

    /**
     * ANSI SGR background colour codes for the 16 CGA/BIOS colour indices.
     * <p>
     * The background colour index occupies bits 4–6 of a JT Vision
     * attribute byte (3 bits, so only indices 0–7 are valid for backgrounds).
     * Indices 0–7 map to standard ANSI background codes (SGR 40–47) and
     * indices 8–15 map to high-intensity background codes (SGR 100–107),
     * following the same CGA ordering as {@link #ANSI_FG}.
     */
    private static final int[] ANSI_BG = {
        40, 44, 42, 46, 41, 45, 43, 47,  // 0-7: normal colors
        100, 104, 102, 106, 101, 105, 103, 107 // 8-15: bright colors
    };

    /**
     * Initialises the screen subsystem.
     * <p>
     * If already initialised this method returns immediately. Otherwise it:
     * <ol>
     *   <li>Installs Jansi via {@link AnsiConsole#systemInstall()} and
     *       captures the Jansi-wrapped output stream.</li>
     *   <li>Calls {@link #detectSize()} to populate {@link #screenWidth} and
     *       {@link #screenHeight}.</li>
     *   <li>Allocates {@link #screenBuffer} and the private shadow buffer
     *       {@code lastBuffer}. Each shadow cell is seeded with {@code '\0'}
     *       and attribute {@code -1} so every position is dirty on the first
     *       flush.</li>
     *   <li>Sends the following terminal sequences:
     *       <ul>
     *         <li>{@code ESC[?1049h} — switch to the alternate screen buffer.</li>
     *         <li>{@code ESC[?25l} — hide the hardware cursor.</li>
     *         <li>{@code ESC[?1002h} — enable button-event mouse tracking.</li>
     *         <li>{@code ESC[?1006h} — enable SGR mouse-reporting mode.</li>
     *       </ul>
     *   </li>
     * </ol>
     */
    public static void init() {
        if (initialized) {
        	return;
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            try {
                Kernel32.SetConsoleOutputCP(65001);
            } catch (Throwable ignored) {
            }
        }
        AnsiConsole.systemInstall();
        out = AnsiConsole.out();

        detectSize();

        int size = screenWidth * screenHeight;
        screenBuffer = new JtvScreenCell[size];
        lastBuffer = new JtvScreenCell[size];
        for (int i = 0; i < size; i++) {
            screenBuffer[i] = new JtvScreenCell();
            lastBuffer[i] = new JtvScreenCell('\0', new JtvColorAttr(0xFF));
        }

        // Enter alternate screen buffer, hide cursor, enable mouse reporting
        out.print("\033[?1049h");  // alternate screen
        out.print("\033[?25l");    // hide cursor
        out.print("\033[?1002h");  // button-event mouse tracking
        out.print("\033[?1006h");  // SGR mouse mode
        out.flush();

        initialized = true;
    }

    /**
     * Tears down the screen subsystem and restores the terminal to its
     * original state.
     * <p>
     * If not currently initialised this method is a no-op. Otherwise it sends:
     * <ul>
     *   <li>{@code ESC[?1006l} — disable SGR mouse mode.</li>
     *   <li>{@code ESC[?1002l} — disable button-event mouse tracking.</li>
     *   <li>{@code ESC[?25h} — restore cursor visibility.</li>
     *   <li>{@code ESC[?1049l} — return to the normal screen buffer.</li>
     * </ul>
     * Then calls {@link AnsiConsole#systemUninstall()} to remove Jansi and
     * sets {@link #initialized} to {@code false}.
     */
    public static void shutdown() {
        if (!initialized) return;

        // Disable mouse reporting, show cursor, leave alternate screen
        out.print("\033[?1006l");
        out.print("\033[?1002l");
        out.print("\033[?25h");
        out.print("\033[?1049l");
        out.flush();

        AnsiConsole.systemUninstall();
        initialized = false;
    }

    /**
     * Queries the terminal for its current dimensions and updates
     * {@link #screenWidth} and {@link #screenHeight}.
     * <p>
     * Three strategies are tried in order:
     * <ol>
     *   <li><b>Windows Kernel32</b> — on Windows, calls
     *       {@link Kernel32#GetConsoleScreenBufferInfo} on the standard output
     *       handle to obtain the window width and height.</li>
     *   <li><b>POSIX ioctl TIOCGWINSZ</b> — on Unix, calls
     *       {@link CLibrary#ioctl} with {@code TIOCGWINSZ} on stdout, stderr,
     *       and stdin in turn until one succeeds.</li>
     *   <li><b>Environment variables</b> — reads {@code COLUMNS} and
     *       {@code LINES} as a last resort.</li>
     * </ol>
     * If all strategies fail, {@link #screenWidth} and {@link #screenHeight}
     * retain their current values (defaulting to 80×25).
     */
    public static void detectSize() {
        if (SystemUtils.IS_OS_WINDOWS) {
            try {
                long handle = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
                Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
                if (Kernel32.GetConsoleScreenBufferInfo(handle, info) != 0) {
                    int w = info.windowWidth();
                    int h = info.windowHeight();
                    if (w > 0 && h > 0) {
                        screenWidth = w;
                        screenHeight = h;
                        return;
                    }
                }
            } catch (Throwable ignored) {
            }
        } else {
            try {
                int[] fds = {
                    CLibrary.STDOUT_FILENO,
                    CLibrary.STDERR_FILENO,
                    0 // STDIN_FILENO
                };
                for (int fd : fds) {
                    WinSize ws = new WinSize();
                    if (CLibrary.ioctl(fd, CLibrary.TIOCGWINSZ, ws) != -1
                    		&& ws.ws_col > 0 && ws.ws_row > 0) {
                        screenWidth = ws.ws_col;
                        screenHeight = ws.ws_row;
                        return;
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        // Fallback: try environment variables
        String cols = System.getenv("COLUMNS");
        String rows = System.getenv("LINES");
        if (cols != null) {
            try { screenWidth = Integer.parseInt(cols); } catch (NumberFormatException ignored) {}
        }
        if (rows != null) {
            try { screenHeight = Integer.parseInt(rows); } catch (NumberFormatException ignored) {}
        }
    }

    /**
     * Changes the screen mode and resizes the screen buffers to match.
     * <p>
     * Calls {@link #detectSize()} to pick up any terminal resize, then
     * reallocates both {@link #screenBuffer} and the private shadow buffer to
     * the new {@code screenWidth * screenHeight} size. The shadow buffer is
     * seeded with dirty sentinels so the next {@link #flushScreen()} performs
     * a full repaint.
     * <p>
     * The {@code mode} parameter is stored in {@link #screenMode} by the
     * caller; this method does not record it itself.
     *
     * @param mode the JT Vision screen-mode constant ({@code smCO80},
     *             {@code smMono}, {@code smBW80}, or {@code smFont8x8})
     */
    public static void setVideoMode(int mode) {
        detectSize();
        int size = screenWidth * screenHeight;
        screenBuffer = new JtvScreenCell[size];
        lastBuffer = new JtvScreenCell[size];
        for (int i = 0; i < size; i++) {
            screenBuffer[i] = new JtvScreenCell();
            lastBuffer[i] = new JtvScreenCell('\0', new JtvColorAttr(0xFF));
        }
    }

    /**
     * Flushes the logical screen buffer to the terminal, emitting ANSI
     * escape sequences only for cells that have changed since the last flush.
     * <p>
     * The algorithm:
     * <ol>
     *   <li>Iterates over every cell in {@link #screenBuffer} row by row.</li>
     *   <li>Compares each cell against {@link #lastBuffer}. If the character
     *       or attribute differs, a cursor-position sequence
     *       ({@code ESC[row;colH}) is emitted before the first dirty cell in
     *       each run; subsequent adjacent dirty cells need no position
     *       sequence. An SGR colour sequence ({@code ESC[fg;bgm}) is emitted
     *       whenever the attribute changes.</li>
     *   <li>After writing a dirty cell the shadow buffer is updated so the
     *       same cell is not re-sent on the next flush.</li>
     *   <li>If any cell was written, the cursor position and visibility are
     *       considered undefined; the {@code applied*} tracking fields are
     *       reset.</li>
     *   <li>After the cell pass, cursor state is reconciled: position,
     *       shape ({@code ESC[1 q} block / {@code ESC[3 q} underline), and
     *       visibility ({@code ESC[?25h} / {@code ESC[?25l}) are emitted as
     *       needed to reach the desired state.</li>
     *   <li>The completed escape-sequence string is written to {@link #out}
     *       in a single {@link PrintStream#print(String)} call.</li>
     * </ol>
     * This method is a no-op if {@link #initialized} is {@code false}.
     */
    public static void flushScreen() {
        if (!initialized) {
        	return;
        }

        StringBuilder sb = new StringBuilder(screenWidth * screenHeight * 4);
        JtvColorAttr lastAttr = new JtvColorAttr(0xFF);
        boolean hadCellChanges = false;

        for (int y = 0; y < screenHeight; y++) {
            boolean needPos = true;
            for (int x = 0; x < screenWidth; x++) {
                int idx = y * screenWidth + x;
                if (idx >= screenBuffer.length) break;
                JtvScreenCell cell = screenBuffer[idx];
                JtvScreenCell last = lastBuffer[idx];

                if (cell.getCh() != last.getCh() || !cell.getAttr().equals(last.getAttr())) {
                    if (!hadCellChanges) {
                        if (appliedCursorVisible) sb.append("\033[?25l");
                        hadCellChanges = true;
                    }
                    if (needPos) {
                        sb.append("\033[").append(y + 1).append(';').append(x + 1).append('H');
                        needPos = false;
                        lastAttr = new JtvColorAttr(0xFF);
                    }

                    if (!cell.getAttr().equals(lastAttr)) {
                        int fg = cell.getAttr().getForeground();
                        int bg = cell.getAttr().getBackground();
                        sb.append("\033[").append(ANSI_FG[fg]).append(';').append(ANSI_BG[bg]).append('m');
                        lastAttr = cell.getAttr();
                    }

                    sb.append(cell.getCh());
                    lastBuffer[idx] = cell;
                } else {
                    needPos = true;
                }
            }
        }

        if (hadCellChanges) {
            appliedCursorX = -1;
            appliedCursorY = -1;
            appliedCursorVisible = false;
        }

        if (desiredCursorVisible) {
            if (desiredCursorX != appliedCursorX || desiredCursorY != appliedCursorY)
                sb.append("\033[").append(desiredCursorY + 1).append(';').append(desiredCursorX + 1).append('H');
            if (desiredCursorBlock != appliedCursorBlock || !appliedCursorVisible)
                sb.append(desiredCursorBlock ? "\033[1 q" : "\033[3 q");
            if (!appliedCursorVisible)
                sb.append("\033[?25h");
        } else if (appliedCursorVisible) {
            sb.append("\033[?25l");
        }

        appliedCursorX = desiredCursorX;
        appliedCursorY = desiredCursorY;
        appliedCursorVisible = desiredCursorVisible;
        appliedCursorBlock = desiredCursorBlock;

        if (sb.length() > 0) {
            out.print(sb);
            out.flush();
        }
    }

    /**
     * Fills every cell in {@link #screenBuffer} with a space character
     * ({@code ' '}) and the default attribute {@code 0x07} (light-grey
     * foreground on black background), matching the JT Vision
     * {@code ClearScreen} behaviour.
     * <p>
     * This does not immediately update the terminal; call
     * {@link #flushScreen()} afterwards.
     */
    public static void clearScreen() {
        for (int i = 0; i < screenBuffer.length; i++) {
            screenBuffer[i] = new JtvScreenCell();
        }
    }

    /**
     * Queues a cursor position to be applied on the next {@link #flushScreen()}
     * call.
     * <p>
     * Coordinates are 0-based. The cursor is only moved if visibility is also
     * enabled via {@link #setCursorVisible(boolean)}.
     *
     * @param x the column index (0-based, left edge = 0)
     * @param y the row index (0-based, top edge = 0)
     */
    public static void setCursorPosition(int x, int y) {
        desiredCursorX = x;
        desiredCursorY = y;
    }

    /**
     * Queues cursor visibility to be applied on the next
     * {@link #flushScreen()} call.
     * <p>
     * When {@code true}, the cursor is shown at the position set by
     * {@link #setCursorPosition(int, int)} using the shape set by
     * {@link #setCursorShape(boolean)}. When {@code false}, the cursor is
     * hidden ({@code ESC[?25l}).
     *
     * @param visible {@code true} to show the cursor, {@code false} to hide it
     */
    public static void setCursorVisible(boolean visible) {
        desiredCursorVisible = visible;
    }

    /**
     * Queues a cursor shape to be applied on the next {@link #flushScreen()}
     * call.
     * <p>
     * Two shapes are supported, both steady (non-blinking) to match the
     * JT Vision insert/overwrite convention:
     * <ul>
     *   <li>{@code true} — steady block cursor ({@code ESC[1 q}), used in
     *       overwrite mode.</li>
     *   <li>{@code false} — steady underline cursor ({@code ESC[3 q}), used
     *       in insert mode.</li>
     * </ul>
     *
     * @param block {@code true} for a block cursor, {@code false} for an
     *              underline cursor
     */
    public static void setCursorShape(boolean block) {
        desiredCursorBlock = block;
    }

    /**
     * Marks all cells in the shadow buffer as dirty, forcing a full repaint
     * on the next {@link #flushScreen()} call.
     * <p>
     * Every cell in the private {@code lastBuffer} is reset to character
     * {@code '\0'} and attribute {@code -1}. Because these values cannot
     * appear in normal screen content, every position will be considered
     * changed by the next flush.
     * <p>
     * Called automatically by {@link #resume()} after returning from a
     * {@link #suspend()} and should also be called after any external process
     * has written to the terminal.
     */
    public static void invalidate() {
        if (lastBuffer != null) {
            JtvScreenCell sentinel = new JtvScreenCell('\0', new JtvColorAttr(0xFF));
            for (int i = 0; i < lastBuffer.length; i++) {
                lastBuffer[i] = sentinel;
            }
        }
    }

    /**
     * Temporarily suspends screen output and restores the normal terminal
     * state, for example before spawning a child shell.
     * <p>
     * Sends the same teardown sequences as {@link #shutdown()} (disable mouse,
     * show cursor, leave alternate screen) but does <em>not</em> uninstall
     * Jansi or clear {@link #initialized}, so {@link #resume()} can restore
     * the TUI without a full reinitialisation.
     */
    public static void suspend() {
        if (initialized) {
            out.print("\033[?1006l");
            out.print("\033[?1002l");
            out.print("\033[?25h");
            out.print("\033[?1049l");
            out.flush();
        }
    }

    /**
     * Resumes screen output after a {@link #suspend()} call.
     * <p>
     * Re-enters the alternate screen buffer and re-enables mouse reporting,
     * then calls {@link #invalidate()} to force a full repaint so that the
     * TUI is completely redrawn after whatever the child process may have
     * written to the terminal.
     */
    public static void resume() {
        if (initialized) {
            out.print("\033[?1049h");
            out.print("\033[?25l");
            out.print("\033[?1002h");
            out.print("\033[?1006h");
            out.flush();
            invalidate();
        }
    }

    /**
     * Copies a horizontal run of {@link JtvScreenCell} objects from a source
     * buffer into {@link #screenBuffer}, optionally applying a shadow
     * attribute transform.
     * <p>
     * The destination range is the columns {@code [x1, x2)} on row {@code y}.
     * The source cells are read starting at {@code buf[bufOffset]}.
     * <p>
     * When {@code inShadow} is {@code true} each cell's attribute is passed
     * through {@link JtvView#applyShadow(int)} before being stored, darkening
     * the colour to produce the drop-shadow effect used by overlapping windows
     * and menus. When {@code false} the cell is copied verbatim via
     * {@link JtvScreenCell#assign(JtvScreenCell)}.
     * <p>
     * Out-of-range indices (negative {@code idx} or {@code bi}, or values
     * beyond the buffer lengths) are silently skipped.
     *
     * @param x1        the first destination column (inclusive, 0-based)
     * @param x2        the last destination column (exclusive)
     * @param y         the destination row (0-based)
     * @param buf       the source cell array
     * @param bufOffset the index in {@code buf} corresponding to column {@code x1}
     * @param inShadow  {@code true} to apply the shadow colour transform,
     *                  {@code false} to copy cells verbatim
     */
    public static void writeToScreen(int x1, int x2, int y, JtvScreenCell[] buf, int bufOffset, boolean inShadow) {
        if (screenBuffer == null) {
            return;
        }
        for (int x = x1; x < x2; x++) {
            int idx = y * Screen.screenWidth + x;
            int bi = bufOffset + x - x1;
            if (idx >= 0 && idx < screenBuffer.length && bi >= 0 && bi < buf.length) {
                JtvScreenCell src = buf[bi];
                if (inShadow) {
                    screenBuffer[idx] = new JtvScreenCell(src.getCh(), JtvView.applyShadow(src.getAttr()));
                }
                else {
                    screenBuffer[idx] = src;
                }
            }
        }
    }

}
