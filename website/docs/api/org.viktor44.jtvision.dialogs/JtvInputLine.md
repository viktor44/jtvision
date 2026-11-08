---
title: "JtvInputLine"
sidebar_label: "JtvInputLine"
---

Class JtvInputLine
==================

```java
public class JtvInputLine
extends JtvView
```


A single-line text editor control.



JtvInputLine provides a basic editable string field. It handles cursor movement,
text insertion and deletion, text selection (including Shift+arrow), overwrite
mode (Ins), and scrolling when the text is longer than the visible area.
Left and right arrow indicators (`\u25c4` / `\u25ba`) appear at the edges
when the text can be scrolled.



The maximum number of characters the field may hold is set at construction time
(`aMaxLen`) and must not be changed afterwards. Double-clicking selects
all text. When the field receives or loses selection, all text is selected or deselected.



History lists can be attached via a [JtvHistory](JtvHistory.md) view placed beside the input line.

**Colour palette**

The four-entry palette `cpInputLine` maps to:

1. Unfocused field text.
2. Focused field text.
3. Selected text highlight.
4. Scroll arrow colour.



org.viktor44.jtvision.dialogs.JtvInputLine

## Summary

### Constructors

| Visibility | Signature                                                                          |
| ---------- | ---------------------------------------------------------------------------------- |
| public     | [JtvInputLine(JtvRect bounds, int maxLen)](#jtvinputlinejtvrect-bounds-int-maxlen) |

### Fields

| Modifiers                | Field name                                                    | Type                                  |
| ------------------------ | ------------------------------------------------------------- | ------------------------------------- |
| **protected**            | [data](#string-data)                                          | String                                |
| **protected**            | [maxLen](#int-maxlen)                                         | int                                   |
| **protected**            | [curPos](#int-curpos)                                         | int                                   |
| **protected**            | [firstPos](#int-firstpos)                                     | int                                   |
| **protected**            | [selStart](#int-selstart)                                     | int                                   |
| **protected**            | [selEnd](#int-selend)                                         | int                                   |
| **protected**            | [anchor](#int-anchor)                                         | int                                   |
| **private static final** | [leftArrow](#char-leftarrow)                                  | char                                  |
| **private static final** | [rightArrow](#char-rightarrow)                                | char                                  |
| **private static final** | [cpInputLine](#orgviktor44jtvisioncorejtvpalette-cpinputline) | org.viktor44.jtvision.core.JtvPalette |

### Methods

| Modifiers   | Method signature                                                           | Return type                           |
| ----------- | -------------------------------------------------------------------------- | ------------------------------------- |
| **public**  | [draw()](#draw)                                                            | void                                  |
| **public**  | [getPalette()](#getpalette)                                                | org.viktor44.jtvision.core.JtvPalette |
| **public**  | [getDataTo(Object rec)](#getdatatoobject-rec)                              | void                                  |
| **public**  | [setDataFrom(Object rec)](#setdatafromobject-rec)                          | void                                  |
| **public**  | [canScroll(int delta)](#canscrollint-delta)                                | boolean                               |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                  | void                                  |
| **private** | [clipCopy()](#clipcopy)                                                    | void                                  |
| **private** | [clipCut()](#clipcut)                                                      | void                                  |
| **private** | [clipPaste()](#clippaste)                                                  | void                                  |
| **private** | [insertPastedText(String text)](#insertpastedtextstring-text)              | void                                  |
| **private** | [deleteSelect()](#deleteselect)                                            | void                                  |
| **private** | [adjustSelectBlock()](#adjustselectblock)                                  | void                                  |
| **public**  | [selectAll(boolean enable)](#selectallboolean-enable)                      | void                                  |
| **private** | [mouseDelta(JtvEvent event)](#mousedeltajtvevent-event)                    | int                                   |
| **private** | [mousePos(JtvEvent event)](#mouseposjtvevent-event)                        | int                                   |
| **public**  | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable) | void                                  |
| **public**  | [valid(int cmd)](#validint-cmd)                                            | boolean                               |
| **public**  | [getData()](#getdata)                                                      | String                                |
| **public**  | [setData(String data)](#setdatastring-data)                                | void                                  |
| **public**  | [getMaxLen()](#getmaxlen)                                                  | int                                   |
| **public**  | [setMaxLen(int maxLen)](#setmaxlenint-maxlen)                              | void                                  |



## Constructors

### `JtvInputLine(JtvRect bounds, int maxLen)`

Creates an input line with the given bounds and maximum text length.

**Parameters**

- `bounds` - the bounding rectangle (should be at least 3 characters wide to
                  accommodate scroll arrows and at least one character of text)
- `maxLen` - the maximum number of characters the field may hold



## Fields

### `String data`

The current text content of the input field.


### `int maxLen`

Maximum number of characters the field may hold (set at construction; do not change).


### `int curPos`

Current cursor position (character index within [data](#string-data)).


### `int firstPos`

Index of the first visible character (non-zero when the text is scrolled).


### `int selStart`

Start of the current text selection (inclusive, character index).


### `int selEnd`

End of the current text selection (exclusive, character index).


### `int anchor`

The anchor point used when extending a selection with Shift+arrow keys.


### `char leftArrow`

Arrow character shown on the left when the text can be scrolled left.


### `char rightArrow`

Arrow character shown on the right when the text can be scrolled right.


### `org.viktor44.jtvision.core.JtvPalette cpInputLine`

Four-entry colour palette:

1. `0x13` — unfocused field text.
2. `0x13` — focused field text.
3. `0x14` — selected text highlight.
4. `0x15` — scroll arrow colour.




## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the input field, rendering the visible portion of [data](#string-data),
selection highlight, and scroll-arrow indicators.



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the default color palette `cpInputLine`.

**Returns**

the input line's color palette



### `getDataTo(Object rec)`

Reads the field's current text. Callers should read the [data](#string-data) field directly
in this implementation.

**Parameters**

- `rec` - the destination record (unused)



### `setDataFrom(Object rec)`

**Overrides:**

`setDataFrom(Object rec)` in `org.viktor44.jtvision.views.JtvView`

Sets the field's text from a `String` record, truncating to [maxLen](#int-maxlen)
if necessary, then selects all text.

**Parameters**

- `rec` - a `String` with the new text value



### `canScroll(int delta)`

Returns whether the text can be scrolled in the given direction.

**Parameters**

- `delta` - negative to check left-scroll, positive to check right-scroll

**Returns**

`true` if scrolling in the given direction is possible



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles mouse and keyboard events for the input field.



Supported operations:

- Mouse clicks — move cursor; double-click selects all text.
- Shift+arrow keys — extend text selection.
- Arrow keys, Home, End — move cursor.
- Backspace, Delete — delete character or selection.
- Ins — toggle insert/overwrite mode.
- Printable characters — insert or overwrite text.
- Ctrl+Y — clear the entire field.



**Parameters**

- `event` - the incoming event



### `clipCopy()`

Copies the selected text to the clipboard.



### `clipCut()`

Copies the selected text to the clipboard then deletes it.



### `clipPaste()`

Inserts the clipboard text at the cursor position, replacing any current selection.



### `insertPastedText(String text)`

Inserts pasted text into the input line, filtering out control
characters (below ASCII 32). Used by both `clipPaste()` and the
bracketed-paste (`evPaste`) handler.

**Parameters**

- `text` - the raw pasted text



### `deleteSelect()`

Deletes the currently selected text range and moves the cursor to [selStart](#int-selstart).



### `adjustSelectBlock()`

Updates [selStart](#int-selstart) and [selEnd](#int-selend) based on [curPos](#int-curpos) and [anchor](#int-anchor).



### `selectAll(boolean enable)`

Selects all text (positions cursor at end, sets selection to the whole string) or
clears the selection (positions cursor and selection start at 0).

**Parameters**

- `enable` - `true` to select all, `false` to deselect all



### `mouseDelta(JtvEvent event)`

Returns the scroll direction implied by the current mouse position:
`-1` if the mouse is at or left of the left edge, `1` if at or right
of the right edge, `0` otherwise.

**Parameters**

- `event`


### `mousePos(JtvEvent event)`

Converts a mouse event position into a character index within [data](#string-data).

**Parameters**

- `event`


### `setState(int aState, boolean enable)`

**Overrides:**

`setState(int aState, boolean enable)` in `org.viktor44.jtvision.views.JtvView`

Extends the inherited `setState` to select or deselect all text when
the field is selected or activated.

**Parameters**

- `aState` - the state flag(s) being changed
- `enable` - `true` to set, `false` to clear



### `valid(int cmd)`

Always returns `true` — input lines are always valid regardless of command.

**Parameters**

- `cmd` - the command to validate

**Returns**

`true`



### `getData()`

The current text content of the input field.



### `setData(String data)`

The current text content of the input field.

**Parameters**

- `data`


### `getMaxLen()`

Maximum number of characters the field may hold (set at construction; do not change).



### `setMaxLen(int maxLen)`

Maximum number of characters the field may hold (set at construction; do not change).

**Parameters**

- `maxLen`


