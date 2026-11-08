---
title: "JtvFileInfoPane"
sidebar_label: "JtvFileInfoPane"
---

Class JtvFileInfoPane
=====================

```java
public class JtvFileInfoPane
extends JtvView
```


An information pane that shows details about the focused file.



TFileInfoPane is displayed at the bottom of a [JtvFileDialog](JtvFileDialog.md). It renders
two rows of text:

1. The current directory path combined with the active wildcard filter.
2. The focused file's name, size (or `<DIR>` for directories),
      and last-modified date/time.

The pane updates automatically when a `cmFileFocused` broadcast arrives
from the [JtvFileList](JtvFileList.md).

**Colour palette**

The single-entry palette `cpInfoPane` maps to:

1. Info pane text colour.



org.viktor44.jtvision.dialogs.JtvFileInfoPane

## Summary

### Constructors

| Visibility | Signature                                                         |
| ---------- | ----------------------------------------------------------------- |
| public     | [JtvFileInfoPane(JtvRect bounds)](#jtvfileinfopanejtvrect-bounds) |

### Fields

| Modifiers                | Field name                                                        | Type                                        |
| ------------------------ | ----------------------------------------------------------------- | ------------------------------------------- |
| **private static final** | [cpInfoPane](#orgviktor44jtvisioncorejtvpalette-cpinfopane)       | org.viktor44.jtvision.core.JtvPalette       |
| **private static final** | [DATE_FMT](#javatextsimpledateformat-date_fmt)                    | java.text.SimpleDateFormat                  |
| **private**              | [fileRecord](#orgviktor44jtvisiondialogsjtvfilerecord-filerecord) | org.viktor44.jtvision.dialogs.JtvFileRecord |

### Methods

| Modifiers  | Method signature                                          | Return type                           |
| ---------- | --------------------------------------------------------- | ------------------------------------- |
| **public** | [draw()](#draw)                                           | void                                  |
| **public** | [getPalette()](#getpalette)                               | org.viktor44.jtvision.core.JtvPalette |
| **public** | [handleEvent(JtvEvent event)](#handleeventjtvevent-event) | void                                  |



## Constructors

### `JtvFileInfoPane(JtvRect bounds)`

Creates a file-info pane with the given bounds.
Adds `evBroadcast` to the event mask so it receives `cmFileFocused`
broadcasts.

**Parameters**

- `bounds` - the bounding rectangle



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpInfoPane`

Single-entry colour palette:

1. `0x1E` — info pane text colour.




### `java.text.SimpleDateFormat DATE_FMT`

Date formatter used in row 2 for the last-modified timestamp.


### `org.viktor44.jtvision.dialogs.JtvFileRecord fileRecord`

The most recently focused file record, used to populate row 2 of the pane.


## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the two-row information display:

1. Row 0 — the directory + wildcard path from the owning [JtvFileDialog](JtvFileDialog.md).
2. Row 1 — the focused file's name, size, and date.





### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the default color palette `cpInfoPane`.

**Returns**

the info pane's color palette



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Updates the displayed file information when a `cmFileFocused` broadcast
carries a [JtvFileRecord](JtvFileRecord.md).

**Parameters**

- `event` - the incoming event



