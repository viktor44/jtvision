---
title: "JtvFileList"
sidebar_label: "JtvFileList"
---

Class JtvFileList
=================

```java
public class JtvFileList
extends JtvSortedListBox
```


A sorted list box that displays files and directories for a file dialog.



JtvFileList extends [JtvSortedListBox](JtvSortedListBox.md) to show the contents of a directory
filtered by a wildcard pattern. It displays:

- A `".."` parent-directory entry (always first).
- Subdirectories (sorted alphabetically, shown with a trailing `/`).
- Files matching the wildcard (sorted alphabetically).

When an item receives focus, `cmFileFocused` is broadcast with the
corresponding [JtvFileRecord](JtvFileRecord.md) so other views (e.g., [JtvFileInputLine](JtvFileInputLine.md)
and [JtvFileInfoPane](JtvFileInfoPane.md)) can update. When an item is selected (double-click or
Enter), `cmFileDoubleClicked` is broadcast.



The wildcard pattern follows standard glob syntax (`*` and `?`).

org.viktor44.jtvision.dialogs.JtvFileList

## Summary

### Constructors

| Visibility | Signature                                                                                                  |
| ---------- | ---------------------------------------------------------------------------------------------------------- |
| public     | [JtvFileList(JtvRect bounds, JtvScrollBar aScrollBar)](#jtvfilelistjtvrect-bounds-jtvscrollbar-ascrollbar) |

### Methods

| Modifiers          | Method signature                                                                       | Return type                                       |
| ------------------ | -------------------------------------------------------------------------------------- | ------------------------------------------------- |
| **protected**      | [itemText(Object item)](#itemtextobject-item)                                          | String                                            |
| **protected**      | [compareItems(Object a, Object b)](#compareitemsobject-a-object-b)                     | int                                               |
| **public**         | [getText(int item, int maxLen)](#gettextint-item-int-maxlen)                           | String                                            |
| **public**         | [focusItem(int item)](#focusitemint-item)                                              | void                                              |
| **public**         | [selectItem(int item)](#selectitemint-item)                                            | void                                              |
| **public**         | [getDataTo(Object rec)](#getdatatoobject-rec)                                          | void                                              |
| **public**         | [setDataFrom(Object rec)](#setdatafromobject-rec)                                      | void                                              |
| **public**         | [readDirectory(String dir, String wildCard)](#readdirectorystring-dir-string-wildcard) | void                                              |
| **public**         | [readDirectory(String wildcardPath)](#readdirectorystring-wildcardpath)                | void                                              |
| **public**         | [fileList()](#filelist)                                                                | List&lt;org.viktor44.jtvision.dialogs.JtvFileRecord> |
| **private static** | [wildcardPattern(String wildcard)](#wildcardpatternstring-wildcard)                    | regex.Pattern                                     |



## Constructors

### `JtvFileList(JtvRect bounds, JtvScrollBar aScrollBar)`

Creates a two-column file list box with the given bounds and scroll bar.

**Parameters**

- `bounds` - the bounding rectangle
- `aScrollBar` - the vertical scroll bar



## Methods

### `itemText(Object item)`

**Overrides:**

`itemText(Object item)` in `org.viktor44.jtvision.dialogs.JtvSortedListBox`

Returns the display text for a list item. Converts a [JtvFileRecord](JtvFileRecord.md)
to its file name, falling back to the superclass for other object types.

**Parameters**

- `item` - the item object

**Returns**

the item's file name



### `compareItems(Object a, Object b)`

**Overrides:**

`compareItems(Object a, Object b)` in `org.viktor44.jtvision.dialogs.JtvSortedListBox`

Compares two [JtvFileRecord](JtvFileRecord.md) objects for sorted order:

1. `".."` always sorts first.
2. Directories sort before files.
3. Within each group, items are sorted case-insensitively by name.



**Parameters**

- `a` - the first item
- `b` - the second item

**Returns**

negative, zero, or positive according to sort order



### `getText(int item, int maxLen)`

**Overrides:**

`getText(int item, int maxLen)` in `org.viktor44.jtvision.dialogs.JtvSortedListBox`

Returns the display text for the item at position `item`.
Directories (except `".."`) are shown with a trailing `/`.

**Parameters**

- `item` - the zero-based item index
- `maxLen` - the maximum number of characters to return

**Returns**

the display text, or an empty string if out of range



### `focusItem(int item)`

Focuses the item at `item` and broadcasts `cmFileFocused` with
the corresponding [JtvFileRecord](JtvFileRecord.md).

**Parameters**

- `item` - the zero-based item index to focus



### `selectItem(int item)`

Broadcasts `cmFileDoubleClicked` with the selected [JtvFileRecord](JtvFileRecord.md)
when the user double-clicks or presses Enter on an item.

**Parameters**

- `item` - the zero-based item index that was selected



### `getDataTo(Object rec)`

Does nothing — data transfer is not implemented for this control.

**Parameters**

- `rec` - unused



### `setDataFrom(Object rec)`

Does nothing — data transfer is not implemented for this control.

**Parameters**

- `rec` - unused



### `readDirectory(String dir, String wildCard)`

Reloads the list for the given directory filtered by the wildcard mask.
Adds a parent (`".."`) entry if a parent directory exists, then
adds all subdirectories and matching files.

**Parameters**

- `dir` - the absolute directory path to list
- `wildCard` - the wildcard filter (e.g., `"*.java"`); `null`
                  or empty defaults to `"*"`



### `readDirectory(String wildcardPath)`

Reloads the list from a path that may contain a wildcard.
Splits the path into a directory and wildcard, then delegates to
[readDirectory(String, String)](#readdirectorystring-dir-string-wildcard).

**Parameters**

- `wildcardPath` - the path, optionally containing `*` or `?`



### `fileList()`

Returns the underlying list cast to `List<TFileRecord>`.

**Returns**

the list of `JtvFileRecord` objects



### `wildcardPattern(String wildcard)`

Compiles a glob wildcard pattern into a case-insensitive `Pattern`.
`*` matches any sequence of characters; `?` matches exactly one.

**Parameters**

- `wildcard` - the glob pattern

**Returns**

the compiled regex pattern



