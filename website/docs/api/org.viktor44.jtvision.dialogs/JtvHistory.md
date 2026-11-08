---
title: "JtvHistory"
sidebar_label: "JtvHistory"
---

Class JtvHistory
================

```java
public class JtvHistory
extends JtvView
```


A small drop-down history picker linked to an [JtvInputLine](JtvInputLine.md).



JtvHistory implements a generic pick-list mechanism for recalling previous entries
made in an associated [JtvInputLine](JtvInputLine.md). It appears as a small button (a
down-arrow icon) placed immediately to the right of the input line.



When the user clicks the history view or presses Alt+Down, a
[JtvHistoryWindow](JtvHistoryWindow.md) pops up displaying the stored entries for this history
ID. If the user selects an entry, it is copied back into the linked input line.



Entries are automatically recorded when the input line loses focus
(`cmReleasedFocus`) or when a `cmRecordHistory` broadcast is
received (typically sent by [JtvButton.press()](JtvButton.md#press)).



All history data is stored in a static per-ID map so it persists for the
lifetime of the application. Up to 20 entries are retained per ID;
the oldest entry is dropped when the list is full.

**Colour palette**

The two-entry palette `cpHistory` maps to:

1. Icon normal colour.
2. Icon shortcut / highlight colour.



org.viktor44.jtvision.dialogs.JtvHistory

## Summary

### Constructors

| Visibility | Signature                                                                                                                     |
| ---------- | ----------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvHistory(JtvRect bounds, JtvInputLine aLink, int aHistoryId)](#jtvhistoryjtvrect-bounds-jtvinputline-alink-int-ahistoryid) |

### Fields

| Modifiers                | Field name                                                | Type                                       |
| ------------------------ | --------------------------------------------------------- | ------------------------------------------ |
| **private**              | [link](#orgviktor44jtvisiondialogsjtvinputline-link)      | org.viktor44.jtvision.dialogs.JtvInputLine |
| **private**              | [historyId](#int-historyid)                               | int                                        |
| **private static final** | [histories](#mapintegerliststring-histories)              | Map&lt;Integer,List&lt;String>>                  |
| **private static final** | [cpHistory](#orgviktor44jtvisioncorejtvpalette-cphistory) | org.viktor44.jtvision.core.JtvPalette      |
| **private static final** | [historyIcon](#string-historyicon)                        | String                                     |

### Methods

| Modifiers         | Method signature                                                      | Return type                                    |
| ----------------- | --------------------------------------------------------------------- | ---------------------------------------------- |
| **public**        | [draw()](#draw)                                                       | void                                           |
| **public**        | [getPalette()](#getpalette)                                           | org.viktor44.jtvision.core.JtvPalette          |
| **public**        | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)             | void                                           |
| **private**       | [showHistory()](#showhistory)                                         | void                                           |
| **public**        | [initHistoryWindow(JtvRect bounds)](#inithistorywindowjtvrect-bounds) | org.viktor44.jtvision.dialogs.JtvHistoryWindow |
| **public static** | [recordHistory(int id, String str)](#recordhistoryint-id-string-str)  | void                                           |
| **public static** | [getHistory(int id)](#gethistoryint-id)                               | List&lt;String>                                   |
| **public static** | [clearHistory(int id)](#clearhistoryint-id)                           | void                                           |



## Constructors

### `JtvHistory(JtvRect bounds, JtvInputLine aLink, int aHistoryId)`

Creates a history view with the given bounds, linked input line, and history ID.

**Parameters**

- `bounds` - the bounding rectangle (typically a 3×1 cell)
- `aLink` - the input line whose entries this view manages
- `aHistoryId` - the ID used to look up and store history entries



## Fields

### `org.viktor44.jtvision.dialogs.JtvInputLine link`

The input line whose text is recorded and restored by this history view.


### `int historyId`

The identifier that groups this history view's entries together.
Multiple input lines may share the same ID to share a common history.


### `Map<Integer,List<String>> histories`

Application-wide storage: maps history ID to its ordered list of entries.


### `org.viktor44.jtvision.core.JtvPalette cpHistory`

Two-entry colour palette:

1. `0x16` — icon normal colour.
2. `0x17` — icon shortcut / highlight colour.




### `String historyIcon`

Icon string rendered by this view (right-half block, down-arrow, left-half block).


## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the history drop-down arrow icon.



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the default color palette `cpHistory`.

**Returns**

the history view's color palette



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles mouse, keyboard, and broadcast events.

- Mouse click — opens the history picker window.
- Alt+Down — opens the history picker window.
- `cmRecordHistory` broadcast — records the linked input line's current text.
- `cmReleasedFocus` for the linked view — records the current text.



**Parameters**

- `event` - the incoming event



### `showHistory()`

Displays the [JtvHistoryWindow](JtvHistoryWindow.md) for the current history ID.
If the user selects an entry, it is written back to the linked input line.



### `initHistoryWindow(JtvRect bounds)`

Creates and returns a [JtvHistoryWindow](JtvHistoryWindow.md) for the given bounds.
The window's help context is set from the linked input line.

**Parameters**

- `bounds` - the bounding rectangle for the popup window

**Returns**

a new `JtvHistoryWindow`, or `null` if construction fails



### `recordHistory(int id, String str)`

Records the string `str` under the given history ID.
Duplicate entries are removed before adding the new entry at the end.
The list is capped at 20 entries; the oldest entry is removed when full.

**Parameters**

- `id` - the history list identifier
- `str` - the string to record; empty or `null` strings are ignored



### `getHistory(int id)`

Returns the list of history entries for the given ID, or `null` if none exist.

**Parameters**

- `id` - the history list identifier

**Returns**

the list of recorded strings, or `null`



### `clearHistory(int id)`

Removes all history entries for the given ID.

**Parameters**

- `id` - the history list identifier to clear



