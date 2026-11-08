---
title: "JtvHistoryViewer"
sidebar_label: "JtvHistoryViewer"
---

Class JtvHistoryViewer
======================

```java
public class JtvHistoryViewer
extends JtvListViewer
```


The list-viewer component inside a [JtvHistoryWindow](JtvHistoryWindow.md).



JtvHistoryViewer displays the history entries for a given history ID in
reverse-chronological order (most recent first). Selecting an entry — either
by pressing Enter, double-clicking, or calling [selectItem(int)](#selectitemint-item) —
ends the modal state with `cmOK`. Pressing Escape or receiving a
`cmCancel` command ends it with `cmCancel`.



This class is managed automatically by [JtvHistory](JtvHistory.md) and does not normally
need to be used directly.

**Colour palette**

The five-entry palette `cpHistoryViewer` maps to:

1. Active (focused list) item text.
2. Inactive (unfocused list) item text.
3. Focused item highlight.
4. Selected item text.
5. Divider row text.



org.viktor44.jtvision.dialogs.JtvHistoryViewer

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                                |
| ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvHistoryViewer(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, int aHistoryId)](#jtvhistoryviewerjtvrect-bounds-jtvscrollbar-ahscrollbar-jtvscrollbar-avscrollbar-int-ahistoryid) |

### Fields

| Modifiers                | Field name                                                            | Type                                  |
| ------------------------ | --------------------------------------------------------------------- | ------------------------------------- |
| **private static final** | [cpHistoryViewer](#orgviktor44jtvisioncorejtvpalette-cphistoryviewer) | org.viktor44.jtvision.core.JtvPalette |
| **private**              | [historyId](#int-historyid)                                           | int                                   |
| **private**              | [entries](#liststring-entries)                                        | List&lt;String>                          |

### Methods

| Modifiers     | Method signature                                             | Return type                           |
| ------------- | ------------------------------------------------------------ | ------------------------------------- |
| **protected** | [refreshEntries()](#refreshentries)                          | void                                  |
| **public**    | [getPalette()](#getpalette)                                  | org.viktor44.jtvision.core.JtvPalette |
| **public**    | [getText(int item, int maxLen)](#gettextint-item-int-maxlen) | String                                |
| **public**    | [historyWidth()](#historywidth)                              | int                                   |
| **public**    | [selectedText()](#selectedtext)                              | String                                |
| **public**    | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)    | void                                  |
| **public**    | [selectItem(int item)](#selectitemint-item)                  | void                                  |



## Constructors

### `JtvHistoryViewer(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, int aHistoryId)`

Creates a history viewer for the given bounds, scroll bars, and history ID.
Loads entries from the global history and focuses the most recent one.

**Parameters**

- `bounds` - the bounding rectangle
- `aHScrollBar` - horizontal scroll bar (may be `null`)
- `aVScrollBar` - vertical scroll bar (may be `null`)
- `aHistoryId` - the ID of the history list to display



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpHistoryViewer`

Five-entry colour palette:

1. `0x06` — active (focused list) item text.
2. `0x06` — inactive (unfocused list) item text.
3. `0x07` — focused item highlight.
4. `0x06` — selected item text.
5. `0x06` — divider row text.




### `int historyId`

The history ID whose entries this viewer displays.


### `List<String> entries`

The entries displayed, in reverse-chronological order (most recent first).


## Methods

### `refreshEntries()`

Reloads entries from the global history store in reverse order
and resets the list range and horizontal scroll bar.



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvListViewer`

Returns the default color palette `cpHistoryViewer`.

**Returns**

the history viewer's color palette



### `getText(int item, int maxLen)`

**Overrides:**

`getText(int item, int maxLen)` in `org.viktor44.jtvision.views.JtvListViewer`

Returns the display text for the entry at position `item`,
truncated to `maxLen` characters.

**Parameters**

- `item` - the zero-based item index
- `maxLen` - the maximum number of characters to return

**Returns**

the entry text, or an empty string if out of range



### `historyWidth()`

Returns the length of the longest entry, used to set the horizontal scroll range.

**Returns**

the maximum entry width in characters



### `selectedText()`

Returns the text of the currently focused entry.

**Returns**

the focused entry's text, or an empty string if nothing is focused



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvListViewer`

Handles Enter (or double-click) to confirm a selection, and Escape (or
`cmCancel`) to dismiss the picker without making a selection.

**Parameters**

- `event` - the incoming event



### `selectItem(int item)`

**Overrides:**

`selectItem(int item)` in `org.viktor44.jtvision.views.JtvListViewer`

Selects the item at `item` and ends the modal state with `cmOK`.

**Parameters**

- `item` - the zero-based index of the selected entry



