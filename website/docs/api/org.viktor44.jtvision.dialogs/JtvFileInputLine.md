---
title: "JtvFileInputLine"
sidebar_label: "JtvFileInputLine"
---

Class JtvFileInputLine
======================

```java
public class JtvFileInputLine
extends JtvInputLine
```


An input line that automatically updates itself when the file list focus changes.



JtvFileInputLine extends [JtvInputLine](JtvInputLine.md) to handle `cmFileFocused`
broadcast events from a [JtvFileList](JtvFileList.md). When a file or directory is focused
in the list and this input line does not have the selection, the input line
text is updated to reflect the focused item.



For a focused directory, the text is set to
`"<dir_name>/<wildcard>"` using the wildcard from the owning
[JtvFileDialog](JtvFileDialog.md).



This class is used internally by [JtvFileDialog](JtvFileDialog.md) and does not normally
need to be subclassed.

org.viktor44.jtvision.dialogs.JtvFileInputLine

## Summary

### Constructors

| Visibility | Signature                                                                                    |
| ---------- | -------------------------------------------------------------------------------------------- |
| public     | [JtvFileInputLine(JtvRect bounds, int aMaxLen)](#jtvfileinputlinejtvrect-bounds-int-amaxlen) |

### Methods

| Modifiers  | Method signature                                          | Return type |
| ---------- | --------------------------------------------------------- | ----------- |
| **public** | [handleEvent(JtvEvent event)](#handleeventjtvevent-event) | void        |



## Constructors

### `JtvFileInputLine(JtvRect bounds, int aMaxLen)`

Creates a file input line with the given bounds and maximum text length.
Adds `evBroadcast` to the event mask so it receives `cmFileFocused`
broadcasts.

**Parameters**

- `bounds` - the bounding rectangle
- `aMaxLen` - the maximum number of characters the field may hold



## Methods

### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.dialogs.JtvInputLine`

Extends the inherited event handler to respond to `cmFileFocused`
broadcasts from [JtvFileList](JtvFileList.md).



When a [JtvFileRecord](JtvFileRecord.md) is focused in the file list and this input line
is not currently selected, the field is updated to the focused item's name.
If the focused item is a directory, the wildcard from the owning
[JtvFileDialog](JtvFileDialog.md) is appended.

**Parameters**

- `event` - the incoming event



