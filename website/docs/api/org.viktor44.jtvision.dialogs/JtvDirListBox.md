---
title: "JtvDirListBox"
sidebar_label: "JtvDirListBox"
---

Class JtvDirListBox
===================

```java
public class JtvDirListBox
extends JtvListBox
```


A list box that displays a hierarchical directory tree.



TDirListBox shows the directory tree of the current disk, with the
path from the root to the current directory highlighted. Subdirectories
of the current directory's parent are also shown, allowing the user to
navigate by selecting items.



When the user selects an entry, a `cmChangeDir` command event is
posted with the selected [JtvDirEntry](JtvDirEntry.md) as the info pointer, allowing
the owner dialog (typically [JtvChangeDirDialog](JtvChangeDirDialog.md)) to change to that directory.



Passing the special string `"Drives"` to [newDirectory(String)](#newdirectorystring-str)
switches the list to show the available disk drives instead of a directory tree.

org.viktor44.jtvision.dialogs.JtvDirListBox

## Summary

### Constructors

| Visibility | Signature                                                                                                      |
| ---------- | -------------------------------------------------------------------------------------------------------------- |
| public     | [JtvDirListBox(JtvRect bounds, JtvScrollBar aScrollBar)](#jtvdirlistboxjtvrect-bounds-jtvscrollbar-ascrollbar) |

### Fields

| Modifiers                | Field name                                                    | Type                                            |
| ------------------------ | ------------------------------------------------------------- | ----------------------------------------------- |
| **private static final** | [pathDir](#string-pathdir)                                    | String                                          |
| **private static final** | [firstDir](#string-firstdir)                                  | String                                          |
| **private static final** | [middleDir](#string-middledir)                                | String                                          |
| **private static final** | [lastDir](#string-lastdir)                                    | String                                          |
| **private static final** | [drives](#string-drives)                                      | String                                          |
| **private final**        | [entries](#listorgviktor44jtvisiondialogsjtvdirentry-entries) | List&lt;org.viktor44.jtvision.dialogs.JtvDirEntry> |
| **private**              | [dir](#string-dir)                                            | String                                          |
| **private**              | [cur](#int-cur)                                               | int                                             |

### Methods

| Modifiers   | Method signature                                                                                                              | Return type                                     |
| ----------- | ----------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------- |
| **public**  | [getText(int item, int maxLen)](#gettextint-item-int-maxlen)                                                                  | String                                          |
| **public**  | [isSelected(int item)](#isselectedint-item)                                                                                   | boolean                                         |
| **public**  | [selectItem(int item)](#selectitemint-item)                                                                                   | void                                            |
| **public**  | [list()](#list)                                                                                                               | List&lt;org.viktor44.jtvision.dialogs.JtvDirEntry> |
| **public**  | [newDirectory(String str)](#newdirectorystring-str)                                                                           | void                                            |
| **private** | [showDrives(List&lt;org.viktor44.jtvision.dialogs.JtvDirEntry> dirs)](#showdriveslistorgviktor44jtvisiondialogsjtvdirentry-dirs) | void                                            |
| **private** | [showDirs(List&lt;org.viktor44.jtvision.dialogs.JtvDirEntry> dirs)](#showdirslistorgviktor44jtvisiondialogsjtvdirentry-dirs)     | void                                            |
| **public**  | [setState(int nState, boolean enable)](#setstateint-nstate-boolean-enable)                                                    | void                                            |



## Constructors

### `JtvDirListBox(JtvRect bounds, JtvScrollBar aScrollBar)`

Creates an empty directory list box with the given bounds and scroll bar.

**Parameters**

- `bounds` - the bounding rectangle
- `aScrollBar` - the vertical scroll bar



## Fields

### `String pathDir`

Prefix string for items on the path from root to the current directory.


### `String firstDir`

Prefix for the first drive entry when showing the drives list.


### `String middleDir`

Prefix for middle entries in the drives list.


### `String lastDir`

Prefix for the last entry in the drives list.


### `String drives`

Special directory name that causes [newDirectory(String)](#newdirectorystring-str) to list drives.


### `List<org.viktor44.jtvision.dialogs.JtvDirEntry> entries`

The [JtvDirEntry](JtvDirEntry.md) objects currently displayed (parallel to the string list).


### `String dir`

The absolute path of the directory currently reflected in the list.


### `int cur`

The index within `entries` of the currently selected (active) directory.


## Methods

### `getText(int item, int maxLen)`

**Overrides:**

`getText(int item, int maxLen)` in `org.viktor44.jtvision.dialogs.JtvListBox`

Returns the display text for the entry at position `item`,
truncated to `maxLen` characters.

**Parameters**

- `item` - the zero-based item index
- `maxLen` - the maximum number of characters to return

**Returns**

the entry's display text



### `isSelected(int item)`

Returns `true` if item `item` is the currently active directory
(i.e., `item == cur`).

**Parameters**

- `item` - the zero-based item index

**Returns**

`true` if this is the selected (current) directory



### `selectItem(int item)`

Posts a `cmChangeDir` command event with the [JtvDirEntry](JtvDirEntry.md) at
`item` as the payload, requesting that the owner changes to that directory.

**Parameters**

- `item` - the zero-based item index to navigate to



### `list()`

Returns the list of [JtvDirEntry](JtvDirEntry.md) objects currently displayed.

**Returns**

the entry list



### `newDirectory(String str)`

Populates the list with the directory tree for the given path, or with
drive letters if `str` equals `"Drives"`.



After loading, the list scrolls to the entry for the current directory.

**Parameters**

- `str` - the absolute path of the directory to display, or `null`
             to use the JVM's current working directory



### `showDrives(List<org.viktor44.jtvision.dialogs.JtvDirEntry> dirs)`

Populates `dirs` with the available disk drive roots.
The first entry is a "Drives" header; subsequent entries are the root paths.

**Parameters**

- `dirs`


### `showDirs(List<org.viktor44.jtvision.dialogs.JtvDirEntry> dirs)`

Populates `dirs` with the directory tree from the filesystem root
down to the current directory, followed by the siblings of the current directory.
Sets `cur` to the index of the current directory's entry.

**Parameters**

- `dirs`


### `setState(int nState, boolean enable)`

Extends the inherited `setState` to toggle the Chdir button's default
state in the owning [JtvChangeDirDialog](JtvChangeDirDialog.md) when this list receives or loses focus.

**Parameters**

- `nState` - the state flag(s) being changed
- `enable` - `true` to set, `false` to clear



