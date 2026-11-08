---
title: "JtvHistoryWindow"
sidebar_label: "JtvHistoryWindow"
---

Class JtvHistoryWindow
======================

```java
public class JtvHistoryWindow
extends JtvWindow
```


The popup window that hosts a [JtvHistoryViewer](JtvHistoryViewer.md).



JtvHistoryWindow is a borderless, number-less window created automatically by
[JtvHistory.initHistoryWindow(JtvRect)](JtvHistory.md#inithistorywindowjtvrect-bounds) when the user requests the history
pick-list. It contains a [JtvHistoryViewer](JtvHistoryViewer.md) with horizontal and vertical
scroll bars.



Clicking outside the window's bounds dismisses it with `cmCancel`.
This class is managed automatically by [JtvHistory](JtvHistory.md) and does not normally
need to be used directly.

**Colour palette**

The seven-entry palette `cpHistoryWindow` maps to:

1. Passive frame colour.
2. Active frame / title colour.
3. Frame icon colour.
4. Scroll bar page area colour.
5. Scroll bar arrow colour.
6. History list normal item colour.
7. History list focused item colour.



org.viktor44.jtvision.dialogs.JtvHistoryWindow

## Summary

### Constructors

| Visibility | Signature                                                                                        |
| ---------- | ------------------------------------------------------------------------------------------------ |
| public     | [JtvHistoryWindow(JtvRect bounds, int historyId)](#jtvhistorywindowjtvrect-bounds-int-historyid) |

### Fields

| Modifiers                | Field name                                                            | Type                                           |
| ------------------------ | --------------------------------------------------------------------- | ---------------------------------------------- |
| **private static final** | [cpHistoryWindow](#orgviktor44jtvisioncorejtvpalette-cphistorywindow) | org.viktor44.jtvision.core.JtvPalette          |
| **private**              | [viewer](#orgviktor44jtvisiondialogsjtvhistoryviewer-viewer)          | org.viktor44.jtvision.dialogs.JtvHistoryViewer |

### Methods

| Modifiers     | Method signature                                                           | Return type                                    |
| ------------- | -------------------------------------------------------------------------- | ---------------------------------------------- |
| **protected** | [initViewer(JtvRect r, int historyId)](#initviewerjtvrect-r-int-historyid) | org.viktor44.jtvision.dialogs.JtvHistoryViewer |
| **public**    | [getPalette()](#getpalette)                                                | org.viktor44.jtvision.core.JtvPalette          |
| **public**    | [getSelection()](#getselection)                                            | String                                         |
| **public**    | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                  | void                                           |



## Constructors

### `JtvHistoryWindow(JtvRect bounds, int historyId)`

Creates a history window with the given bounds and history ID.
Constructs and inserts a [JtvHistoryViewer](JtvHistoryViewer.md) with keyboard-enabled scroll bars.

**Parameters**

- `bounds` - the bounding rectangle for the popup window
- `historyId` - the ID of the history list to display



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpHistoryWindow`

Seven-entry colour palette:

1. `0x13` — passive frame colour.
2. `0x13` — active frame / title colour.
3. `0x15` — frame icon colour.
4. `0x18` — scroll bar page area colour.
5. `0x17` — scroll bar arrow colour.
6. `0x13` — history list normal item colour.
7. `0x14` — history list focused item colour.




### `org.viktor44.jtvision.dialogs.JtvHistoryViewer viewer`

The list-viewer component that displays the history entries.


## Methods

### `initViewer(JtvRect r, int historyId)`

Creates the [JtvHistoryViewer](JtvHistoryViewer.md) inside this window, with standard
horizontal and vertical scroll bars. Override to customise the viewer.

**Parameters**

- `r` - the window's client rectangle (will be shrunk by 1 on each side)
- `historyId` - the history ID to pass to the viewer

**Returns**

a new `JtvHistoryViewer`



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvWindow`

Returns the default color palette `cpHistoryWindow`.

**Returns**

the history window's color palette



### `getSelection()`

Returns the text of the entry currently selected in the viewer.

**Returns**

the selected history entry text, or an empty string if no viewer exists



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvWindow`

Extends the inherited event handler to dismiss the window (`cmCancel`)
when the user clicks outside its bounds.

**Parameters**

- `event` - the incoming event



