---
title: "JtvColorGroupList"
sidebar_label: "JtvColorGroupList"
---

Class JtvColorGroupList
=======================

```java
public class JtvColorGroupList
extends JtvListViewer
```


A list viewer that displays the colour groups in a [JtvColorDialog](JtvColorDialog.md).



TColorGroupList shows the top-level colour categories (e.g., "Desktop",
"Menus") from a list of [JtvColorGroup](JtvColorGroup.md) objects. When the user
focuses a group, this viewer broadcasts `cmNewColorItem` so the companion
[JtvColorItemList](JtvColorItemList.md) can update itself to show that group's items.



When a `cmSaveColorIndex` broadcast is received, the viewer stores the
currently focused item index back into the active group's [JtvColorGroup.index](JtvColorGroup.md#int-index)
so the selection can be restored later.

org.viktor44.jtvision.dialogs.JtvColorGroupList

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                                                             |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvColorGroupList(JtvRect bounds, JtvScrollBar aScrollBar, List&lt;org.viktor44.jtvision.dialogs.JtvColorGroup> aGroups)](#jtvcolorgrouplistjtvrect-bounds-jtvscrollbar-ascrollbar-listorgviktor44jtvisiondialogsjtvcolorgroup-agroups) |

### Fields

| Modifiers   | Field name                                                    | Type                                              |
| ----------- | ------------------------------------------------------------- | ------------------------------------------------- |
| **private** | [groups](#listorgviktor44jtvisiondialogsjtvcolorgroup-groups) | List&lt;org.viktor44.jtvision.dialogs.JtvColorGroup> |

### Methods

| Modifiers  | Method signature                                                                   | Return type                                     |
| ---------- | ---------------------------------------------------------------------------------- | ----------------------------------------------- |
| **public** | [addGroup(JtvColorGroup item)](#addgroupjtvcolorgroup-item)                        | org.viktor44.jtvision.dialogs.JtvColorGroupList |
| **public** | [focusItem(int item)](#focusitemint-item)                                          | void                                            |
| **public** | [getText(int item, int maxLen)](#gettextint-item-int-maxlen)                       | String                                          |
| **public** | [handleEvent(JtvEvent ev)](#handleeventjtvevent-ev)                                | void                                            |
| **public** | [setGroupIndex(int groupNum, int itemNum)](#setgroupindexint-groupnum-int-itemnum) | void                                            |
| **public** | [getGroup(int groupNum)](#getgroupint-groupnum)                                    | org.viktor44.jtvision.dialogs.JtvColorGroup     |
| **public** | [getGroupIndex(int groupNum)](#getgroupindexint-groupnum)                          | int                                             |
| **public** | [getNumGroups()](#getnumgroups)                                                    | int                                             |



## Constructors

### `JtvColorGroupList(JtvRect bounds, JtvScrollBar aScrollBar, List<org.viktor44.jtvision.dialogs.JtvColorGroup> aGroups)`

Creates a colour group list with the given bounds, scroll bar, and group list.

**Parameters**

- `bounds` - the bounding rectangle
- `aScrollBar` - the vertical scroll bar
- `aGroups` - the list of colour groups



## Fields

### `List<org.viktor44.jtvision.dialogs.JtvColorGroup> groups`

The list of [JtvColorGroup](JtvColorGroup.md) objects to display.


## Methods

### `addGroup(JtvColorGroup item)`

Adds `item` to the group list, updates the range, and returns this list for chaining.

**Parameters**

- `item` - the group to add

**Returns**

this list



### `focusItem(int item)`

**Overrides:**

`focusItem(int item)` in `org.viktor44.jtvision.views.JtvListViewer`

Focuses item `item` and broadcasts `cmNewColorItem` with the
corresponding [JtvColorGroup](JtvColorGroup.md) so the item list updates.

**Parameters**

- `item` - the zero-based group index to focus



### `getText(int item, int maxLen)`

**Overrides:**

`getText(int item, int maxLen)` in `org.viktor44.jtvision.views.JtvListViewer`

Returns the display name of the group at position `item`,
truncated to `maxLen` characters.

**Parameters**

- `item` - the zero-based group index
- `maxLen` - the maximum number of characters to return

**Returns**

the group name, or an empty string if out of range



### `handleEvent(JtvEvent ev)`

Handles `cmSaveColorIndex` broadcasts by persisting the item index
from the event into the currently focused group.

**Parameters**

- `ev` - the incoming event



### `setGroupIndex(int groupNum, int itemNum)`

Stores item index `itemNum` into the [JtvColorGroup.index](JtvColorGroup.md#int-index) of group
`groupNum`.

**Parameters**

- `groupNum` - the zero-based group index
- `itemNum` - the item index to store



### `getGroup(int groupNum)`

Returns the [JtvColorGroup](JtvColorGroup.md) at position `groupNum` in the list.

**Parameters**

- `groupNum` - the zero-based group index

**Returns**

the group, or `null` if out of range



### `getGroupIndex(int groupNum)`

Returns the stored focused-item index for the group at `groupNum`.

**Parameters**

- `groupNum` - the zero-based group index

**Returns**

the saved item index, or 0 if the group does not exist



### `getNumGroups()`

Returns the total number of groups in the list.

**Returns**

the group count



