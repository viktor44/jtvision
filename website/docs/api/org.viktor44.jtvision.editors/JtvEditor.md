---
title: "JtvEditor"
sidebar_label: "JtvEditor"
---

Class JtvEditor
===============

```java
public class JtvEditor
extends JtvView
```


A simple, fast text editor view for use in JT Vision applications.



`JtvEditor` provides mouse support, undo, clipboard cut/copy/paste,
automatic indentation, insert/overwrite toggle, key binding, and search and replace.
It can be used both for editing text files and for multi-line memo fields in dialog boxes.



`JtvEditor` is the base class for [JtvMemo](JtvMemo.md) (dialog memo fields) and
[JtvFileEditor](JtvFileEditor.md) (file-backed editors).  All editing functionality is implemented
here; subclasses add persistence and data-transfer support.



The editor stores all text in a `StringBuilder` buffer.  Positions within the
text are expressed as zero-based character offsets into that buffer.

**Colour palette**

The two-entry palette [cpEditor](#orgviktor44jtvisioncorejtvpalette-cpeditor) maps to:

1. Normal text colour.
2. Selected text colour.



org.viktor44.jtvision.editors.JtvEditor

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                              |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| public     | [JtvEditor(JtvRect bounds, JtvScrollBar hScrollBar, JtvScrollBar vScrollBar, JtvIndicator indicator)](#jtveditorjtvrect-bounds-jtvscrollbar-hscrollbar-jtvscrollbar-vscrollbar-jtvindicator-indicator) |

### Fields

| Modifiers                  | Field name                                                     | Type                                       |
| -------------------------- | -------------------------------------------------------------- | ------------------------------------------ |
| **protected static final** | [cpEditor](#orgviktor44jtvisioncorejtvpalette-cpeditor)        | org.viktor44.jtvision.core.JtvPalette      |
| **private**                | [hScrollBar](#orgviktor44jtvisionviewsjtvscrollbar-hscrollbar) | org.viktor44.jtvision.views.JtvScrollBar   |
| **private**                | [vScrollBar](#orgviktor44jtvisionviewsjtvscrollbar-vscrollbar) | org.viktor44.jtvision.views.JtvScrollBar   |
| **private**                | [indicator](#orgviktor44jtvisioneditorsjtvindicator-indicator) | org.viktor44.jtvision.editors.JtvIndicator |
| **protected**              | [buffer](#stringbuilder-buffer)                                | StringBuilder                              |
| **protected**              | [bufLen](#int-buflen)                                          | int                                        |
| **protected**              | [selStart](#int-selstart)                                      | int                                        |
| **protected**              | [selEnd](#int-selend)                                          | int                                        |
| **protected**              | [curPtr](#int-curptr)                                          | int                                        |
| **protected**              | [curPos](#orgviktor44jtvisioncorejtvpoint-curpos)              | org.viktor44.jtvision.core.JtvPoint        |
| **protected**              | [delta](#orgviktor44jtvisioncorejtvpoint-delta)                | org.viktor44.jtvision.core.JtvPoint        |
| **protected**              | [limit](#orgviktor44jtvisioncorejtvpoint-limit)                | org.viktor44.jtvision.core.JtvPoint        |
| **protected**              | [lineOffsets](#int-lineoffsets)                                | int[]                                      |
| **protected**              | [isValid](#boolean-isvalid)                                    | boolean                                    |
| **protected**              | [canUndo](#boolean-canundo)                                    | boolean                                    |
| **protected**              | [modified](#boolean-modified)                                  | boolean                                    |
| **protected**              | [selecting](#boolean-selecting)                                | boolean                                    |
| **protected**              | [overwrite](#boolean-overwrite)                                | boolean                                    |
| **protected**              | [autoIndent](#boolean-autoindent)                              | boolean                                    |
| **protected**              | [undoOffset](#int-undooffset)                                  | int                                        |
| **protected**              | [undoDeletedText](#string-undodeletedtext)                     | String                                     |
| **protected**              | [undoInsertedLength](#int-undoinsertedlength)                  | int                                        |
| **protected**              | [undoCurPtr](#int-undocurptr)                                  | int                                        |
| **protected**              | [undoSelStart](#int-undoselstart)                              | int                                        |
| **protected**              | [undoSelEnd](#int-undoselend)                                  | int                                        |
| **protected**              | [canUndoState](#boolean-canundostate)                          | boolean                                    |
| **protected**              | [readOnly](#boolean-readonly)                                  | boolean                                    |

### Methods

| Modifiers          | Method signature                                                                                                                                                | Return type                           |
| ------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------- |
| **public**         | [getPalette()](#getpalette)                                                                                                                                     | org.viktor44.jtvision.core.JtvPalette |
| **public**         | [changeBounds(JtvRect bounds)](#changeboundsjtvrect-bounds)                                                                                                     | void                                  |
| **public**         | [draw()](#draw)                                                                                                                                                 | void                                  |
| **private static** | [printableChar(char ch)](#printablecharchar-ch)                                                                                                                 | char                                  |
| **protected**      | [updateMetrics()](#updatemetrics)                                                                                                                               | void                                  |
| **protected**      | [updateMetricsIncremental(int editOffset, int deletedLength, int insertedLength)](#updatemetricsincrementalint-editoffset-int-deletedlength-int-insertedlength) | void                                  |
| **protected**      | [notifyTitleUpdate()](#notifytitleupdate)                                                                                                                       | void                                  |
| **protected**      | [snapshotUndo(int offset, String deletedText, int insertedLength)](#snapshotundoint-offset-string-deletedtext-int-insertedlength)                               | void                                  |
| **private**        | [recalcCurPos()](#recalccurpos)                                                                                                                                 | void                                  |
| **private**        | [upperBound(int value)](#upperboundint-value)                                                                                                                   | int                                   |
| **private**        | [getLineLength(int lineIndex)](#getlinelengthint-lineindex)                                                                                                     | int                                   |
| **private**        | [updateScrollBars()](#updatescrollbars)                                                                                                                         | void                                  |
| **private**        | [updateIndicator()](#updateindicator)                                                                                                                           | void                                  |
| **private**        | [inSelection(int i)](#inselectionint-i)                                                                                                                         | boolean                               |
| **protected**      | [getLine(int index)](#getlineint-index)                                                                                                                         | String                                |
| **protected**      | [lineToPtr(int lineIndex, int col)](#linetoptrint-lineindex-int-col)                                                                                            | int                                   |
| **protected**      | [lineStart(int ptr)](#linestartint-ptr)                                                                                                                         | int                                   |
| **protected**      | [lineEnd(int ptr)](#lineendint-ptr)                                                                                                                             | int                                   |
| **protected**      | [nextLine(int ptr)](#nextlineint-ptr)                                                                                                                           | int                                   |
| **protected**      | [prevLine(int ptr)](#prevlineint-ptr)                                                                                                                           | int                                   |
| **protected**      | [nextChar(int ptr)](#nextcharint-ptr)                                                                                                                           | int                                   |
| **protected**      | [prevChar(int ptr)](#prevcharint-ptr)                                                                                                                           | int                                   |
| **protected**      | [nextWord(int ptr)](#nextwordint-ptr)                                                                                                                           | int                                   |
| **protected**      | [prevWord(int ptr)](#prevwordint-ptr)                                                                                                                           | int                                   |
| **protected**      | [hasSelection()](#hasselection)                                                                                                                                 | boolean                               |
| **protected**      | [setSelect(int a, int b, boolean curAtStart)](#setselectint-a-int-b-boolean-curatstart)                                                                         | void                                  |
| **protected**      | [setCurPtr(int p, boolean extendSelection)](#setcurptrint-p-boolean-extendselection)                                                                            | void                                  |
| **protected**      | [deleteSelect()](#deleteselect)                                                                                                                                 | void                                  |
| **protected**      | [insertText(String s, boolean selectText)](#inserttextstring-s-boolean-selecttext)                                                                              | void                                  |
| **protected**      | [deleteRange(int a, int b)](#deleterangeint-a-int-b)                                                                                                            | void                                  |
| **protected**      | [newLine()](#newline)                                                                                                                                           | void                                  |
| **protected**      | [scrollTo(int x, int y)](#scrolltoint-x-int-y)                                                                                                                  | void                                  |
| **protected**      | [trackCursor(boolean center)](#trackcursorboolean-center)                                                                                                       | void                                  |
| **protected**      | [toggleInsMode()](#toggleinsmode)                                                                                                                               | void                                  |
| **protected**      | [startSelect()](#startselect)                                                                                                                                   | void                                  |
| **protected**      | [hideSelect()](#hideselect)                                                                                                                                     | void                                  |
| **protected**      | [clipCopy()](#clipcopy)                                                                                                                                         | boolean                               |
| **protected**      | [clipCut()](#clipcut)                                                                                                                                           | void                                  |
| **protected**      | [clipPaste()](#clippaste)                                                                                                                                       | void                                  |
| **protected**      | [undo()](#undo)                                                                                                                                                 | void                                  |
| **protected**      | [doFind()](#dofind)                                                                                                                                             | void                                  |
| **protected**      | [doReplace()](#doreplace)                                                                                                                                       | void                                  |
| **protected**      | [doSearchAgain()](#dosearchagain)                                                                                                                               | void                                  |
| **protected**      | [updateCommands()](#updatecommands)                                                                                                                             | void                                  |
| **protected**      | [setCmdState(int command, boolean enable)](#setcmdstateint-command-boolean-enable)                                                                              | void                                  |
| **public**         | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable)                                                                                      | void                                  |
| **protected**      | [keyToCommand(KeyDownEvent keyEvent)](#keytocommandkeydownevent-keyevent)                                                                                       | int                                   |
| **protected**      | [handleCommand(int command, boolean extendSelection)](#handlecommandint-command-boolean-extendselection)                                                        | boolean                               |
| **public**         | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                                                                                       | void                                  |
| **public**         | [valid(int command)](#validint-command)                                                                                                                         | boolean                               |
| **public**         | [setReadOnly(boolean readOnly)](#setreadonlyboolean-readonly)                                                                                                   | void                                  |



## Constructors

### `JtvEditor(JtvRect bounds, JtvScrollBar hScrollBar, JtvScrollBar vScrollBar, JtvIndicator indicator)`

Constructs a `TEditor` view.



Enables `ofSelectable` and configures the event mask to receive
mouse, key, command, and broadcast events.  Associates the supplied scroll bars and
indicator, then shows the cursor and initialises the view metrics.

**Parameters**

- `bounds` - the bounding rectangle for this editor view
- `hScrollBar` - optional horizontal scroll bar, or `null`
- `vScrollBar` - optional vertical scroll bar, or `null`
- `indicator` - optional position indicator, or `null`
- `bufSize` - initial buffer capacity (minimum `0x1000`)



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpEditor`

Two-entry colour palette:

1. `6` — normal text colour.
2. `7` — selected text colour.




### `org.viktor44.jtvision.views.JtvScrollBar hScrollBar`

Horizontal scroll bar, or `null` if none.


### `org.viktor44.jtvision.views.JtvScrollBar vScrollBar`

Vertical scroll bar, or `null` if none.


### `org.viktor44.jtvision.editors.JtvIndicator indicator`

Line/column indicator view, or `null` if none.
Updated after every cursor movement via [JtvIndicator.setValue](JtvIndicator.md#setvaluejtvpoint-p-boolean-amodified).


### `StringBuilder buffer`

The text content being edited.


### `int bufLen`

Current number of characters in the buffer (`buffer.length()`).
Kept in sync by [updateMetrics()](#updatemetrics).


### `int selStart`

Buffer offset of the start of the current selection.
When there is no selection, `selStart == selEnd`.


### `int selEnd`

Buffer offset of the end of the current selection.
When there is no selection, `selStart == selEnd`.


### `int curPtr`

Buffer offset of the insertion cursor.
Either equals [selStart](#int-selstart) or [selEnd](#int-selend) depending on the
direction in which the selection was extended.


### `org.viktor44.jtvision.core.JtvPoint curPos`

Column/line position of the cursor within the text.
`curPos.x` is the column (0-based); `curPos.y` is the line (0-based).


### `org.viktor44.jtvision.core.JtvPoint delta`

The top-left offset of the visible area.
`delta.x` is the leftmost visible column; `delta.y` is the topmost visible line.


### `org.viktor44.jtvision.core.JtvPoint limit`

Maximum width and line count of the current text.
`limit.x` is the length of the longest line + 1; `limit.y` is the line count.
Used to configure scroll bar ranges.


### `int[] lineOffsets`

Buffer offsets of the start of each line.
`lineOffsets[i]` is the index in [buffer](#stringbuilder-buffer) where line `i` begins.
Rebuilt by [updateMetrics()](#updatemetrics) after every buffer change.


### `boolean isValid`

`true` if this view is in a valid state.
Returned by [valid(int)](#validint-command) to indicate whether the editor can be used.


### `boolean canUndo`

`true` if the editor supports single-level undo.
Set to `true` by the constructor; may be set to `false` to disable undo.


### `boolean modified`

`true` if the buffer has been modified since the last load or save.
Displayed in the [JtvIndicator](JtvIndicator.md) as an asterisk prefix.


### `boolean selecting`

`true` while the user is in selection mode (e.g. after `cmStartSelect`
but before the selection is finalized).


### `boolean overwrite`

`true` if the editor is in overwrite mode; `false` for insert mode.
Toggled by the Insert key or `cmInsMode` command.


### `boolean autoIndent`

`true` if the editor automatically indents new lines to match the indentation
of the preceding line.  Toggled by the `cmIndentMode` command.


### `int undoOffset`

Buffer offset where the last recorded change started.


### `String undoDeletedText`

Text deleted at [undoOffset](#int-undooffset) by the last recorded change (empty if none).


### `int undoInsertedLength`

Number of characters inserted at [undoOffset](#int-undooffset) by the last recorded change.


### `int undoCurPtr`

Saved cursor position for undo.


### `int undoSelStart`

Saved selection start for undo.


### `int undoSelEnd`

Saved selection end for undo.


### `boolean canUndoState`

`true` when a saved undo snapshot is available to restore.


### `boolean readOnly`

`true` if the editor should reject all modifications.


## Methods

### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the editor palette (`cpEditor`), mapping color index 1 to normal text
and color index 2 to selected text.

**Returns**

the editor palette



### `changeBounds(JtvRect bounds)`

**Overrides:**

`changeBounds(JtvRect bounds)` in `org.viktor44.jtvision.views.JtvView`

Resizes the editor view to `bounds`, clamps the scroll offset so it remains
within the valid range, then redraws.

**Parameters**

- `bounds` - new bounding rectangle



### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Renders the visible portion of the editor content, applying the normal attribute to
unselected characters and the selected attribute to characters within the current
selection.  Positions the hardware cursor at the current cursor location.



### `printableChar(char ch)`

*No method description provided*

**Parameters**

- `ch`


### `updateMetrics()`

Recomputes [bufLen](#int-buflen), [limit](#orgviktor44jtvisioncorejtvpoint-limit), and [curPos](#orgviktor44jtvisioncorejtvpoint-curpos) from the current
buffer content, then updates the scroll bars, the indicator, and the command states.
Must be called after any modification to [buffer](#stringbuilder-buffer) or [curPtr](#int-curptr).



### `updateMetricsIncremental(int editOffset, int deletedLength, int insertedLength)`

Updates metrics incrementally after an edit at `editOffset` that replaced
`deletedLength` characters with `insertedLength` characters.
Runs in O(insertedLength + lines in affected region + log n) instead of O(bufLen).
The `lineOffsets` array must still reflect the state BEFORE the edit when this
is called — the buffer itself must already contain the new content.

**Parameters**

- `editOffset`
- `deletedLength`
- `insertedLength`


### `notifyTitleUpdate()`

Broadcasts `cmUpdateTitle` to the owner so the window frame refreshes its title.



### `snapshotUndo(int offset, String deletedText, int insertedLength)`

Records the delta of an edit operation so that [undo()](#undo) can reverse it.
Only the changed region is stored, not the entire buffer.

**Parameters**

- `offset` - buffer offset where the change starts
- `deletedText` - text that will be (or was) deleted at `offset`
- `insertedLength` - number of characters that will be (or were) inserted at `offset`



### `recalcCurPos()`

*No method description provided*



### `upperBound(int value)`

*No method description provided*

**Parameters**

- `value`


### `getLineLength(int lineIndex)`

*No method description provided*

**Parameters**

- `lineIndex`


### `updateScrollBars()`

*No method description provided*



### `updateIndicator()`

*No method description provided*



### `inSelection(int i)`

*No method description provided*

**Parameters**

- `i`


### `getLine(int index)`

Returns the text content of the line at the given 0-based line index.
The returned string does not include the terminating newline character.

**Parameters**

- `index` - 0-based line index

**Returns**

line content, or an empty string if the index is out of range



### `lineToPtr(int lineIndex, int col)`

Converts a (line, column) coordinate to a buffer offset.

**Parameters**

- `lineIndex` - 0-based line index
- `col` - 0-based column index within the line

**Returns**

buffer offset, clamped to `[0, buffer.length()]`



### `lineStart(int ptr)`

Returns the buffer offset of the first character of the line containing `ptr`.

**Parameters**

- `ptr` - a buffer offset

**Returns**

offset of the start of the line



### `lineEnd(int ptr)`

Returns the buffer offset of the newline character (or end of buffer) that terminates
the line containing `ptr`.

**Parameters**

- `ptr` - a buffer offset

**Returns**

offset of the end of the line (the `'\n'` or `buffer.length()`)



### `nextLine(int ptr)`

Returns the buffer offset of the first character of the line following the line
containing `ptr`.  If already on the last line, returns the end of the buffer.

**Parameters**

- `ptr` - a buffer offset

**Returns**

offset of the start of the next line



### `prevLine(int ptr)`

Returns the buffer offset of the first character of the line preceding the line
containing `ptr`.  If already on the first line, returns `0`.

**Parameters**

- `ptr` - a buffer offset

**Returns**

offset of the start of the previous line



### `nextChar(int ptr)`

Returns the buffer offset of the character immediately after `ptr`, clamped to
the end of the buffer.

**Parameters**

- `ptr` - a buffer offset

**Returns**

`ptr + 1`, clamped to `buffer.length()`



### `prevChar(int ptr)`

Returns the buffer offset of the character immediately before `ptr`, clamped to
the start of the buffer.

**Parameters**

- `ptr` - a buffer offset

**Returns**

`ptr - 1`, clamped to `0`



### `nextWord(int ptr)`

Returns the buffer offset of the start of the next word after `ptr`.
Skips over the current word (letter/digit run), then skips over any non-word characters.

**Parameters**

- `ptr` - a buffer offset

**Returns**

offset of the start of the next word, or end-of-buffer



### `prevWord(int ptr)`

Returns the buffer offset of the start of the word preceding `ptr`.
Skips backwards over non-word characters, then backwards over the word itself.

**Parameters**

- `ptr` - a buffer offset

**Returns**

offset of the start of the previous word, or `0`



### `hasSelection()`

Returns `true` if the editor currently has a non-empty text selection.

**Returns**

`true` if `selStart != selEnd`



### `setSelect(int a, int b, boolean curAtStart)`

Sets the selection range and cursor position without recording an undo snapshot.

**Parameters**

- `a` - selection start (smaller offset)
- `b` - selection end (larger offset)
- `curAtStart` - `true` to place the cursor at the start of the selection,
                    `false` to place it at the end



### `setCurPtr(int p, boolean extendSelection)`

Moves the cursor to buffer offset `p` and optionally extends the selection.

**Parameters**

- `p` - target buffer offset
- `extendSelection` - `true` to extend the selection; `false` to collapse it



### `deleteSelect()`

Deletes the currently selected text, records an undo snapshot, and updates metrics.
Does nothing if there is no selection.



### `insertText(String s, boolean selectText)`

Inserts `s` at the cursor position, replacing any current selection first.
Records a single combined undo delta covering both the selection deletion and the insertion.

**Parameters**

- `s` - text to insert
- `selectText` - `true` to select the newly inserted text after insertion



### `deleteRange(int a, int b)`

Deletes the characters in the range `[a, b)` from the buffer, records an
undo snapshot, and updates metrics.  The range is clamped to valid buffer boundaries.

**Parameters**

- `a` - start offset (inclusive)
- `b` - end offset (exclusive)



### `newLine()`

Inserts a newline at the cursor position.  If [autoIndent](#boolean-autoindent) is `true`,
the new line is automatically indented to match the leading whitespace of the
current line.



### `scrollTo(int x, int y)`

Scrolls the view so that column `x` and line `y` are at the top-left
of the visible area.  Values are clamped to the valid scroll range.  Redraws if
the scroll position actually changed.

**Parameters**

- `x` - target horizontal scroll offset (column)
- `y` - target vertical scroll offset (line)



### `trackCursor(boolean center)`

Scrolls the view to ensure the cursor is visible, then updates the indicator.

**Parameters**

- `center` - `true` to center the cursor vertically; `false` to do the
                minimum scroll necessary to keep the cursor in view



### `toggleInsMode()`

Toggles between insert and overwrite modes, updating the [overwrite](#boolean-overwrite) flag and
the `sfCursorIns` state flag that controls the cursor shape.



### `startSelect()`

Enters selection mode: sets [selecting](#boolean-selecting) to `true` and anchors the
selection start/end at the current cursor position.



### `hideSelect()`

Exits selection mode and collapses the selection to the cursor position.



### `clipCopy()`

Copies the selected text to the clipboard.

**Returns**

`true` if text was copied; `false` if there was no selection



### `clipCut()`

Copies the selected text to the clipboard then deletes it from the buffer.
Does nothing if there is no selection.



### `clipPaste()`

Inserts the clipboard text at the cursor position, replacing any current selection.
Does nothing if the clipboard holds no text.



### `undo()`

Reverses the last recorded edit using the stored delta, then swaps in the inverse
delta so the operation can be redone by calling `undo()` again.
Does nothing if no snapshot is available.



### `doFind()`

Opens the Find dialog.  The base implementation is a no-op; override in subclasses
(e.g. [JtvFileEditor](JtvFileEditor.md)) to present a real dialog.



### `doReplace()`

Opens the Replace dialog.  The base implementation is a no-op; override in subclasses
to present a real dialog.



### `doSearchAgain()`

Repeats the most recent search/replace operation.  The base implementation is a no-op;
override in subclasses to implement the actual repeat.



### `updateCommands()`

Enables or disables the standard edit commands (`cmUndo`, `cmCut`,
`cmCopy`, `cmPaste`, `cmClear`, `cmFind`, `cmReplace`,
`cmSearchAgain`) based on the current selection and clipboard state.
Called by [updateMetrics()](#updatemetrics) and [setState](#setstateint-astate-boolean-enable).



### `setCmdState(int command, boolean enable)`

Enables or disables a single command depending on `enable`.

**Parameters**

- `command` - the command code to update
- `enable` - `true` to enable, `false` to disable



### `setState(int aState, boolean enable)`

**Overrides:**

`setState(int aState, boolean enable)` in `org.viktor44.jtvision.views.JtvView`

Propagates the `sfActive` state change to the associated scroll bars and indicator,
showing or hiding them as the editor gains or loses focus, then refreshes the command set.

**Parameters**

- `aState` - state flag(s) being changed
- `enable` - `true` to set the flag, `false` to clear it



### `keyToCommand(KeyDownEvent keyEvent)`

Maps a key press to the equivalent editor command code.
Returns `0` when no command corresponds to the key.

**Parameters**

- `keyEvent`


### `handleCommand(int command, boolean extendSelection)`

Executes an editor command. Returns `true` if the command was recognised
and dispatched, `false` otherwise.

**Parameters**

- `command`
- `extendSelection`


### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles mouse, keyboard, command, and scroll-bar broadcast events.



Mouse clicks move the cursor; mouse drag extends the selection.
Printable key presses insert (or overwrite) characters.
Navigation keys (`kbLeft`, `kbRight`, …) move the cursor,
optionally extending the selection when [selecting](#boolean-selecting) is active.
Command events are dispatched to the appropriate editing operations.
Scroll-bar broadcasts update [delta](#orgviktor44jtvisioncorejtvpoint-delta) and redraw.

**Parameters**

- `event` - the event to handle



### `valid(int command)`

**Overrides:**

`valid(int command)` in `org.viktor44.jtvision.views.JtvView`

Returns `true` if this editor view is in a valid state.
The base implementation always returns [isValid](#boolean-isvalid).

**Parameters**

- `command` - the validation command (e.g. `cmValid`, `cmClose`)

**Returns**

`true` if the editor is valid and may be used/closed



### `setReadOnly(boolean readOnly)`

*No method description provided*

**Parameters**

- `readOnly`


