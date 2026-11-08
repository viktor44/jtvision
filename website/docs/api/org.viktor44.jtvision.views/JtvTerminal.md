---
title: "JtvTerminal"
sidebar_label: "JtvTerminal"
---

Class JtvTerminal
=================

```java
public class JtvTerminal
extends JtvTextDevice
```


A scrollable terminal view that accumulates text output in a line buffer.



JtvTerminal extends [JtvTextDevice](JtvTextDevice.md) with a concrete ring-buffer
implementation. Incoming text is split on newline characters into a list
of completed lines (`lines`) plus a `currentLine` that is
still being assembled. When the total character count exceeds
`bufSize` the oldest lines are discarded.

**Rendering**

[draw()](#draw) renders the visible window of lines starting from
`delta.y`. A blinking text cursor is displayed at the end of
`currentLine`; if the cursor position falls outside the visible
area it is placed off-screen at `(-1, -1)`.

**Grow mode**

The grow mode is set to `gfGrowHiX | gfGrowHiY` so the terminal
expands to fill its owner when the owner is resized.

**Colour palette**

The two-entry palette `cpTerminal` maps to:

1. `6` — normal text colour.
2. `7` — highlighted text colour.



org.viktor44.jtvision.views.JtvTerminal

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                  |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| public     | [JtvTerminal(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, int aBufSize)](#jtvterminaljtvrect-bounds-jtvscrollbar-ahscrollbar-jtvscrollbar-avscrollbar-int-abufsize) |

### Fields

| Modifiers                | Field name                                                  | Type                                  |
| ------------------------ | ----------------------------------------------------------- | ------------------------------------- |
| **private**              | [bufSize](#int-bufsize)                                     | int                                   |
| **private final**        | [lines](#liststring-lines)                                  | List&lt;String>                          |
| **private**              | [currentLine](#string-currentline)                          | String                                |
| **private static final** | [cpTerminal](#orgviktor44jtvisioncorejtvpalette-cpterminal) | org.viktor44.jtvision.core.JtvPalette |

### Methods

| Modifiers   | Method signature                        | Return type                           |
| ----------- | --------------------------------------- | ------------------------------------- |
| **public**  | [doSputn(String s)](#dosputnstring-s)   | int                                   |
| **private** | [trimToBufferSize()](#trimtobuffersize) | void                                  |
| **private** | [longestLineWidth()](#longestlinewidth) | int                                   |
| **public**  | [draw()](#draw)                         | void                                  |
| **public**  | [queEmpty()](#queempty)                 | boolean                               |
| **public**  | [getPalette()](#getpalette)             | org.viktor44.jtvision.core.JtvPalette |



## Constructors

### `JtvTerminal(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, int aBufSize)`

Constructs a terminal with the given bounds, scroll bars, and buffer
size. Sets grow mode to `gfGrowHiX | gfGrowHiY`, initialises the
scroll limit to `(0, 1)`, positions the cursor at the origin,
and makes the cursor visible.

**Parameters**

- `bounds` - the bounding rectangle
- `aHScrollBar` - the horizontal scroll bar, or `null`
- `aVScrollBar` - the vertical scroll bar, or `null`
- `aBufSize` - maximum total character count; clamped to
                       `[1, 32000]`



## Fields

### `int bufSize`

Maximum total character count across all stored lines. When exceeded,
the oldest lines are removed by `trimToBufferSize()`.


### `List<String> lines`

Completed lines in the terminal's line buffer, in chronological order.
The oldest entry is index 0; it is the first to be discarded when the
buffer overflows.


### `String currentLine`

The line currently being assembled. It becomes a completed entry in
`lines` when a newline character is received.


### `org.viktor44.jtvision.core.JtvPalette cpTerminal`

Two-entry colour palette:

1. `6` — normal text.
2. `7` — highlighted text.




## Methods

### `doSputn(String s)`

**Overrides:**

`doSputn(String s)` in `org.viktor44.jtvision.views.JtvTextDevice`

Appends the string `s` to the line buffer, splitting on
`'\n'`. Carriage-return characters (`'\r'`) are silently
dropped. After appending, trims the buffer to `bufSize`,
recalculates the scroll limit, and scrolls to the bottom before
redrawing.

**Parameters**

- `s` - the string to append; `null` is a no-op

**Returns**

the number of characters in `s`, or `0` for
        `null`



### `trimToBufferSize()`

Removes the oldest completed lines until the total character count
(including newline separators and the current line) is within
`bufSize`. If even after removing all completed lines the current
line is still too long, its leading characters are truncated.



### `longestLineWidth()`

Returns the length of the longest line across all completed lines and
the current line. Used to set the horizontal scroll limit.

**Returns**

the maximum line length in characters



### `draw()`

Renders the visible portion of the line buffer. Lines are drawn
starting from `delta.y`; each row is padded to the full view
width with spaces. The horizontal scroll offset (`delta.x`)
is applied via [JtvDrawBuffer.moveStr](../org.viktor44.jtvision.core/JtvDrawBuffer.md#movestrint-indent-string-str-jtvcolorattr-attr). The text cursor is
positioned at the end of the current line; if that position is outside
the viewport it is placed off-screen.



### `queEmpty()`

Returns `true` if both the completed line list and the current
line are empty, indicating the terminal has no pending output.

**Returns**

`true` if the terminal buffer is empty



### `getPalette()`

Returns the terminal's colour palette `cpTerminal`.

**Returns**

the two-entry palette



