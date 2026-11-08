---
title: "JtvColorGroup"
sidebar_label: "JtvColorGroup"
---

Class JtvColorGroup
===================

```java
public class JtvColorGroup
```


A named colour group used by [JtvColorDialog](JtvColorDialog.md).



TColorGroup represents a named category of palette entries (e.g., "Desktop",
"Menus"). Each group owns a list of [JtvColorItem](JtvColorItem.md) objects that
identify the individual palette slots within that category.



The [index](#int-index) field tracks which item in this group was most recently
focused in the colour item list, so the dialog can restore that position.

org.viktor44.jtvision.dialogs.JtvColorGroup

## Summary

### Constructors

| Visibility | Signature                                                                              |
| ---------- | -------------------------------------------------------------------------------------- |
| public     | [JtvColorGroup(String nm)](#jtvcolorgroupstring-nm)                                    |
| public     | [JtvColorGroup(String nm, JtvColorItem itm)](#jtvcolorgroupstring-nm-jtvcoloritem-itm) |

### Fields

| Modifiers     | Field name                                                 | Type                                             |
| ------------- | ---------------------------------------------------------- | ------------------------------------------------ |
| **protected** | [name](#string-name)                                       | String                                           |
| **protected** | [index](#int-index)                                        | int                                              |
| **protected** | [items](#listorgviktor44jtvisiondialogsjtvcoloritem-items) | List&lt;org.viktor44.jtvision.dialogs.JtvColorItem> |

### Methods

| Modifiers         | Method signature                                                                         | Return type                                      |
| ----------------- | ---------------------------------------------------------------------------------------- | ------------------------------------------------ |
| **public**        | [addItem(JtvColorItem item)](#additemjtvcoloritem-item)                                  | org.viktor44.jtvision.dialogs.JtvColorGroup      |
| **public static** | [appendItem(JtvColorGroup g, JtvColorItem i)](#appenditemjtvcolorgroup-g-jtvcoloritem-i) | org.viktor44.jtvision.dialogs.JtvColorGroup      |
| **public**        | [getName()](#getname)                                                                    | String                                           |
| **public**        | [getIndex()](#getindex)                                                                  | int                                              |
| **public**        | [setIndex(int index)](#setindexint-index)                                                | void                                             |
| **public**        | [getItems()](#getitems)                                                                  | List&lt;org.viktor44.jtvision.dialogs.JtvColorItem> |



## Constructors

### `JtvColorGroup(String nm)`

Creates a colour group with the given name and no items.

**Parameters**

- `nm` - the display name



### `JtvColorGroup(String nm, JtvColorItem itm)`

Creates a colour group with a specified initial item.

**Parameters**

- `nm` - the display name
- `itm` - an initial item to add to the group, or `null`



## Fields

### `String name`

The display name shown in the colour group list.


### `int index`

The index of the most recently focused item within this group's item list.
Used to restore the selection when switching between groups.


### `List<org.viktor44.jtvision.dialogs.JtvColorItem> items`

The list of [JtvColorItem](JtvColorItem.md) objects belonging to this group.


## Methods

### `addItem(JtvColorItem item)`

Adds `item` to this group's item list and returns this group for chaining.

**Parameters**

- `item` - the item to add

**Returns**

this group



### `appendItem(JtvColorGroup g, JtvColorItem i)`

Adds item `i` to group `g`'s item list.

**Parameters**

- `g` - the group to add the item to; if `null` this is a no-op
- `i` - the item to add

**Returns**

`g` (unchanged reference)



### `getName()`

The display name shown in the colour group list.



### `getIndex()`

The index of the most recently focused item within this group's item list.
Used to restore the selection when switching between groups.



### `setIndex(int index)`

The index of the most recently focused item within this group's item list.
Used to restore the selection when switching between groups.

**Parameters**

- `index`


### `getItems()`

The list of [JtvColorItem](JtvColorItem.md) objects belonging to this group.



