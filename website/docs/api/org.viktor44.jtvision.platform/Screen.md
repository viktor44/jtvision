---
title: "Screen"
sidebar_label: "Screen"
---

Class Screen
============

```java
public class Screen
```


Low-level terminal screen manager for the jtvision TUI framework.



Screen corresponds to the JT Vision `Screen` unit: it owns the
physical terminal surface, maintains the logical [screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer) of
[JtvScreenCell](../org.viktor44.jtvision.core/JtvScreenCell.md) objects, and provides a dirty-cell optimised flush that
converts the buffer to ANSI escape sequences.

**Lifecycle**

Call [init()](#init) once at startup and [shutdown()](#shutdown) at exit.
[init()](#init) installs Jansi, detects the terminal size, allocates both
the front buffer ([screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer)) and the shadow buffer
(`lastBuffer`), then switches the terminal to the alternate screen
buffer and enables SGR mouse mode. [shutdown()](#shutdown) reverses all of that.

**Double-buffer flush**

[flushScreen()](#flushscreen) compares every cell in [screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer) against
the corresponding entry in the private `lastBuffer`. Only cells whose
character or attribute differ are written to the terminal, using compact
`ESC[row;colH` cursor-position sequences and `ESC[fg;bgm` SGR
colour sequences. Cursor position and visibility are deferred and applied at
the end of each flush so they are sent exactly once per frame.

**Screen mode**

[screenMode](#int-screenmode) holds a JT Vision screen-mode constant. The default
value `0x0003` is `smCO80` — 80-column colour mode. Other TV
constants include `smMono` (0x0007), `smBW80` (0x0002), and
`smFont8x8` (0x0100).

**Colour mapping**

JT Vision uses the CGA/BIOS 4-bit attribute byte: bits 0–3 are the
foreground colour index (0–15) and bits 4–6 are the background colour index
(0–7). `ANSI_FG` and `ANSI_BG` translate these indices to the
corresponding ANSI SGR colour codes.

**Cursor model**

Cursor state is double-buffered. The `desired*` fields record what the
application has requested via [setCursorPosition(int, int)](#setcursorpositionint-x-int-y),
[setCursorVisible(boolean)](#setcursorvisibleboolean-visible), and [setCursorShape(boolean)](#setcursorshapeboolean-block).
The `applied*` fields track what was last actually sent to the
terminal. [flushScreen()](#flushscreen) reconciles them at the end of each frame,
emitting only the escape sequences needed to reach the desired state.

**Suspend / resume**

[suspend()](#suspend) temporarily leaves the alternate screen (e.g. before
shelling out) and [resume()](#resume) re-enters it. After resuming,
[invalidate()](#invalidate) is called automatically to force a full redraw.

org.viktor44.jtvision.platform.Screen

## Summary

### Constructors

| Visibility | Signature           |
| ---------- | ------------------- |
| public     | [Screen()](#screen) |

### Fields

| Modifiers                | Field name                                                                           | Type                                       |
| ------------------------ | ------------------------------------------------------------------------------------ | ------------------------------------------ |
| **public static**        | [screenWidth](#int-screenwidth)                                                      | int                                        |
| **public static**        | [screenHeight](#int-screenheight)                                                    | int                                        |
| **public static**        | [screenMode](#int-screenmode)                                                        | int                                        |
| **public static**        | [screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer)                   | org.viktor44.jtvision.core.JtvScreenCell[] |
| **public static**        | [cursorLines](#int-cursorlines)                                                      | int                                        |
| **private static**       | [desiredCursorX](#int-desiredcursorx)                                                | int                                        |
| **private static**       | [desiredCursorY](#int-desiredcursory)                                                | int                                        |
| **private static**       | [desiredCursorVisible](#boolean-desiredcursorvisible)                                | boolean                                    |
| **private static**       | [desiredCursorBlock](#boolean-desiredcursorblock)                                    | boolean                                    |
| **private static**       | [appliedCursorX](#int-appliedcursorx)                                                | int                                        |
| **private static**       | [appliedCursorY](#int-appliedcursory)                                                | int                                        |
| **private static**       | [appliedCursorVisible](#boolean-appliedcursorvisible)                                | boolean                                    |
| **private static**       | [appliedCursorBlock](#boolean-appliedcursorblock)                                    | boolean                                    |
| **private static**       | [lastBuffer](#orgviktor44jtvisioncorejtvscreencell-lastbuffer)                       | org.viktor44.jtvision.core.JtvScreenCell[] |
| **private static**       | [initialized](#boolean-initialized)                                                  | boolean                                    |
| **private static**       | [out](#javaioprintstream-out)                                                        | java.io.PrintStream                        |
| **private static final** | [LAST_BUFFER_INIT_CELL](#orgviktor44jtvisioncorejtvscreencell-last_buffer_init_cell) | org.viktor44.jtvision.core.JtvScreenCell   |
| **private static final** | [ANSI_FG](#int-ansi_fg)                                                              | int[]                                      |
| **private static final** | [ANSI_BG](#int-ansi_bg)                                                              | int[]                                      |

### Methods

| Modifiers         | Method signature                                                                                                                                                                                                                   | Return type |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------- |
| **public static** | [init()](#init)                                                                                                                                                                                                                    | void        |
| **public static** | [shutdown()](#shutdown)                                                                                                                                                                                                            | void        |
| **public static** | [detectSize()](#detectsize)                                                                                                                                                                                                        | void        |
| **public static** | [setVideoMode(int mode)](#setvideomodeint-mode)                                                                                                                                                                                    | void        |
| **public static** | [flushScreen()](#flushscreen)                                                                                                                                                                                                      | void        |
| **public static** | [clearScreen()](#clearscreen)                                                                                                                                                                                                      | void        |
| **public static** | [setCursorPosition(int x, int y)](#setcursorpositionint-x-int-y)                                                                                                                                                                   | void        |
| **public static** | [setCursorVisible(boolean visible)](#setcursorvisibleboolean-visible)                                                                                                                                                              | void        |
| **public static** | [setCursorShape(boolean block)](#setcursorshapeboolean-block)                                                                                                                                                                      | void        |
| **public static** | [invalidate()](#invalidate)                                                                                                                                                                                                        | void        |
| **public static** | [suspend()](#suspend)                                                                                                                                                                                                              | void        |
| **public static** | [resume()](#resume)                                                                                                                                                                                                                | void        |
| **public static** | [writeToScreen(int x1, int x2, int y, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset, boolean inShadow)](#writetoscreenint-x1-int-x2-int-y-orgviktor44jtvisioncorejtvscreencell-buf-int-bufoffset-boolean-inshadow) | void        |



## Constructors

### `Screen()`

*No method description provided*



## Fields

### `int screenWidth`

The current terminal width in columns.



Populated by [detectSize()](#detectsize) and kept in sync with
[screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer). The JT Vision default is 80.


### `int screenHeight`

The current terminal height in rows.



Populated by [detectSize()](#detectsize) and kept in sync with
[screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer). The JT Vision default is 25.


### `int screenMode`

The active screen mode, encoded as a JT Vision screen-mode constant.



The default value `0x0003` corresponds to `smCO80`
(80-column colour mode). Other JT Vision constants:

- `0x0002` — `smBW80`: 80-column black-and-white.
- `0x0007` — `smMono`: monochrome.
- `0x0100` — `smFont8x8`: 8×8 pixel font (EGA/VGA 43/50-line mode).

Changed by [setVideoMode(int)](#setvideomodeint-mode).


### `org.viktor44.jtvision.core.JtvScreenCell[] screenBuffer`

The logical front-buffer: one [JtvScreenCell](../org.viktor44.jtvision.core/JtvScreenCell.md) per screen position,
stored in row-major order (`y * screenWidth + x`).



Views write into this array via [writeToScreen(int, int, int, JtvScreenCell[], int, boolean)](#writetoscreenint-x1-int-x2-int-y-orgviktor44jtvisioncorejtvscreencell-buf-int-bufoffset-boolean-inshadow).
[flushScreen()](#flushscreen) reads it and emits only the cells that differ
from the shadow buffer `lastBuffer`.


### `int cursorLines`

The cursor shape register, mirroring the JT Vision `CursorLines`
variable. A value of `0` selects the default cursor shape; other
values are reserved for hardware-cursor line masks in the original PC
BIOS interface (unused in the ANSI terminal back-end).


### `int desiredCursorX`

The column position that the cursor should be at after the next
[flushScreen()](#flushscreen) call (0-based). Set by
[setCursorPosition(int, int)](#setcursorpositionint-x-int-y).


### `int desiredCursorY`

The row position that the cursor should be at after the next
[flushScreen()](#flushscreen) call (0-based). Set by
[setCursorPosition(int, int)](#setcursorpositionint-x-int-y).


### `boolean desiredCursorVisible`

Whether the cursor should be visible after the next
[flushScreen()](#flushscreen) call. Set by [setCursorVisible(boolean)](#setcursorvisibleboolean-visible).


### `boolean desiredCursorBlock`

Whether the cursor should use the steady-block shape (`true`) or
the steady-underline shape (`false`) after the next
[flushScreen()](#flushscreen) call. Set by [setCursorShape(boolean)](#setcursorshapeboolean-block).


### `int appliedCursorX`

The column position that was last actually sent to the terminal.
Initialised to `-1` so the first flush always emits an explicit
cursor-position sequence.


### `int appliedCursorY`

The row position that was last actually sent to the terminal.
Initialised to `-1` so the first flush always emits an explicit
cursor-position sequence.


### `boolean appliedCursorVisible`

Whether the cursor was last reported as visible to the terminal.


### `boolean appliedCursorBlock`

Whether the cursor shape last sent to the terminal was the block shape.


### `org.viktor44.jtvision.core.JtvScreenCell[] lastBuffer`

The shadow (back) buffer used by [flushScreen()](#flushscreen) for dirty-cell
detection. Each entry mirrors the on-screen state of the corresponding
[screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer) cell. Cells are initialised with character
`'\0'` and attribute `-1` so that every cell is considered
dirty on the first flush. Reset to the same sentinel by
[invalidate()](#invalidate).


### `boolean initialized`

Whether [init()](#init) has been called without a subsequent
[shutdown()](#shutdown). Guards against double-initialisation and ensures
[flushScreen()](#flushscreen) is a no-op before the screen is set up.


### `java.io.PrintStream out`

The Jansi-installed output stream used for all terminal output.
Obtained from `AnsiConsole.out()` during [init()](#init) so that
Jansi's Windows-compatibility layer is active.


### `org.viktor44.jtvision.core.JtvScreenCell LAST_BUFFER_INIT_CELL`

*No description provided*


### `int[] ANSI_FG`

ANSI SGR foreground colour codes for the 16 CGA/BIOS colour indices.



The foreground colour index occupies bits 0–3 of a JT Vision
attribute byte. Indices 0–7 map to standard ANSI colours (SGR 30–37)
and indices 8–15 map to bright/high-intensity colours (SGR 90–97).
The ordering follows the CGA palette:
&lt;ol start="0">
- Black (30)
- Blue (34)
- Green (32)
- Cyan (36)
- Red (31)
- Magenta (35)
- Brown/Yellow (33)
- Light Grey (37)
- Dark Grey (90)
- Light Blue (94)
- Light Green (92)
- Light Cyan (96)
- Light Red (91)
- Light Magenta (95)
- Yellow (93)
- White (97)




### `int[] ANSI_BG`

ANSI SGR background colour codes for the 16 CGA/BIOS colour indices.



The background colour index occupies bits 4–6 of a JT Vision
attribute byte (3 bits, so only indices 0–7 are valid for backgrounds).
Indices 0–7 map to standard ANSI background codes (SGR 40–47) and
indices 8–15 map to high-intensity background codes (SGR 100–107),
following the same CGA ordering as `ANSI_FG`.


## Methods

### `init()`

Initialises the screen subsystem.



If already initialised this method returns immediately. Otherwise it:

1. Installs Jansi via `AnsiConsole.systemInstall()` and
      captures the Jansi-wrapped output stream.
2. Calls [detectSize()](#detectsize) to populate [screenWidth](#int-screenwidth) and
      [screenHeight](#int-screenheight).
3. Allocates [screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer) and the private shadow buffer
      `lastBuffer`. Each shadow cell is seeded with `'\0'`
      and attribute `-1` so every position is dirty on the first
      flush.
4. Sends the following terminal sequences:
  - `ESC[?1049h` — switch to the alternate screen buffer.
  - `ESC[?25l` — hide the hardware cursor.
  - `ESC[?1002h` — enable button-event mouse tracking.
  - `ESC[?1006h` — enable SGR mouse-reporting mode.





### `shutdown()`

Tears down the screen subsystem and restores the terminal to its
original state.



If not currently initialised this method is a no-op. Otherwise it sends:

- `ESC[?1006l` — disable SGR mouse mode.
- `ESC[?1002l` — disable button-event mouse tracking.
- `ESC[?25h` — restore cursor visibility.
- `ESC[?1049l` — return to the normal screen buffer.

Then calls `AnsiConsole.systemUninstall()` to remove Jansi and
sets `initialized` to `false`.



### `detectSize()`

Queries the terminal for its current dimensions and updates
[screenWidth](#int-screenwidth) and [screenHeight](#int-screenheight).



Three strategies are tried in order:

1. **Windows Kernel32** — on Windows, calls
      `Kernel32.GetConsoleScreenBufferInfo` on the standard output
      handle to obtain the window width and height.
2. **POSIX ioctl TIOCGWINSZ** — on Unix, calls
      `CLibrary.ioctl` with `TIOCGWINSZ` on stdout, stderr,
      and stdin in turn until one succeeds.
3. **Environment variables** — reads `COLUMNS` and
      `LINES` as a last resort.

If all strategies fail, [screenWidth](#int-screenwidth) and [screenHeight](#int-screenheight)
retain their current values (defaulting to 80×25).



### `setVideoMode(int mode)`

Changes the screen mode and resizes the screen buffers to match.



Calls [detectSize()](#detectsize) to pick up any terminal resize, then
reallocates both [screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer) and the private shadow buffer to
the new `screenWidth * screenHeight` size. The shadow buffer is
seeded with dirty sentinels so the next [flushScreen()](#flushscreen) performs
a full repaint.



The `mode` parameter is stored in [screenMode](#int-screenmode) by the
caller; this method does not record it itself.

**Parameters**

- `mode` - the JT Vision screen-mode constant (`smCO80`,
              `smMono`, `smBW80`, or `smFont8x8`)



### `flushScreen()`

Flushes the logical screen buffer to the terminal, emitting ANSI
escape sequences only for cells that have changed since the last flush.



The algorithm:

1. Iterates over every cell in [screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer) row by row.
2. Compares each cell against `lastBuffer`. If the character
      or attribute differs, a cursor-position sequence
      (`ESC[row;colH`) is emitted before the first dirty cell in
      each run; subsequent adjacent dirty cells need no position
      sequence. An SGR colour sequence (`ESC[fg;bgm`) is emitted
      whenever the attribute changes.
3. After writing a dirty cell the shadow buffer is updated so the
      same cell is not re-sent on the next flush.
4. If any cell was written, the cursor position and visibility are
      considered undefined; the `applied*` tracking fields are
      reset.
5. After the cell pass, cursor state is reconciled: position,
      shape (`ESC[1 q` block / `ESC[3 q` underline), and
      visibility (`ESC[?25h` / `ESC[?25l`) are emitted as
      needed to reach the desired state.
6. The completed escape-sequence string is written to `out`
      in a single `PrintStream.print(String)` call.

This method is a no-op if `initialized` is `false`.



### `clearScreen()`

Fills every cell in [screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer) with a space character
(`' '`) and the default attribute `0x07` (light-grey
foreground on black background), matching the JT Vision
`ClearScreen` behaviour.



This does not immediately update the terminal; call
[flushScreen()](#flushscreen) afterwards.



### `setCursorPosition(int x, int y)`

Queues a cursor position to be applied on the next [flushScreen()](#flushscreen)
call.



Coordinates are 0-based. The cursor is only moved if visibility is also
enabled via [setCursorVisible(boolean)](#setcursorvisibleboolean-visible).

**Parameters**

- `x` - the column index (0-based, left edge = 0)
- `y` - the row index (0-based, top edge = 0)



### `setCursorVisible(boolean visible)`

Queues cursor visibility to be applied on the next
[flushScreen()](#flushscreen) call.



When `true`, the cursor is shown at the position set by
[setCursorPosition(int, int)](#setcursorpositionint-x-int-y) using the shape set by
[setCursorShape(boolean)](#setcursorshapeboolean-block). When `false`, the cursor is
hidden (`ESC[?25l`).

**Parameters**

- `visible` - `true` to show the cursor, `false` to hide it



### `setCursorShape(boolean block)`

Queues a cursor shape to be applied on the next [flushScreen()](#flushscreen)
call.



Two shapes are supported, both steady (non-blinking) to match the
JT Vision insert/overwrite convention:

- `true` — steady block cursor (`ESC[1 q`), used in
      overwrite mode.
- `false` — steady underline cursor (`ESC[3 q`), used
      in insert mode.



**Parameters**

- `block` - `true` for a block cursor, `false` for an
               underline cursor



### `invalidate()`

Marks all cells in the shadow buffer as dirty, forcing a full repaint
on the next [flushScreen()](#flushscreen) call.



Every cell in the private `lastBuffer` is reset to character
`'\0'` and attribute `-1`. Because these values cannot
appear in normal screen content, every position will be considered
changed by the next flush.



Called automatically by [resume()](#resume) after returning from a
[suspend()](#suspend) and should also be called after any external process
has written to the terminal.



### `suspend()`

Temporarily suspends screen output and restores the normal terminal
state, for example before spawning a child shell.



Sends the same teardown sequences as [shutdown()](#shutdown) (disable mouse,
show cursor, leave alternate screen) but does *not* uninstall
Jansi or clear `initialized`, so [resume()](#resume) can restore
the TUI without a full reinitialisation.



### `resume()`

Resumes screen output after a [suspend()](#suspend) call.



Re-enters the alternate screen buffer and re-enables mouse reporting,
then calls [invalidate()](#invalidate) to force a full repaint so that the
TUI is completely redrawn after whatever the child process may have
written to the terminal.



### `writeToScreen(int x1, int x2, int y, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset, boolean inShadow)`

Copies a horizontal run of [JtvScreenCell](../org.viktor44.jtvision.core/JtvScreenCell.md) objects from a source
buffer into [screenBuffer](#orgviktor44jtvisioncorejtvscreencell-screenbuffer), optionally applying a shadow
attribute transform.



The destination range is the columns `[x1, x2)` on row `y`.
The source cells are read starting at `buf[bufOffset]`.



When `inShadow` is `true` each cell's attribute is passed
through `JtvView.applyShadow(int)` before being stored, darkening
the colour to produce the drop-shadow effect used by overlapping windows
and menus. When `false` the cell is copied verbatim via
`JtvScreenCell.assign(JtvScreenCell)`.



Out-of-range indices (negative `idx` or `bi`, or values
beyond the buffer lengths) are silently skipped.

**Parameters**

- `x1` - the first destination column (inclusive, 0-based)
- `x2` - the last destination column (exclusive)
- `y` - the destination row (0-based)
- `buf` - the source cell array
- `bufOffset` - the index in `buf` corresponding to column `x1`
- `inShadow` - `true` to apply the shadow colour transform,
                   `false` to copy cells verbatim



