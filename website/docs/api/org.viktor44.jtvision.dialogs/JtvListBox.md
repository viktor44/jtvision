---
title: "JtvListBox"
sidebar_label: "JtvListBox"
---

Class JtvListBox
================

```java
public class JtvListBox
extends JtvListViewer
```


A scrollable list-box control backed by a `List` of strings.



JtvListBox is a concrete descendant of `TListViewer` that stores its
items in a `List`. For many use cases the default implementation
requires no subclassing. When items are not plain strings, override
[getText(int, int)](#gettextint-item-int-maxlen) to convert items to display text.



A new list is assigned at any time with [newList(List)](#newlistliststring-alist), which
replaces the current list, resets the scroll range, and focuses the first item.



The data record for setting/reading this control is a single `int`
representing the index of the focused item.

org.viktor44.jtvision.dialogs.JtvListBox

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                          |
| ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvListBox(JtvRect bounds, int numCols, JtvScrollBar hScrollBar, JtvScrollBar vScrollBar)](#jtvlistboxjtvrect-bounds-int-numcols-jtvscrollbar-hscrollbar-jtvscrollbar-vscrollbar) |

### Fields

| Modifiers     | Field name               | Type         |
| ------------- | ------------------------ | ------------ |
| **protected** | [list](#liststring-list) | List&lt;String> |

### Methods

| Modifiers  | Method signature                                             | Return type  |
| ---------- | ------------------------------------------------------------ | ------------ |
| **public** | [getText(int item, int maxLen)](#gettextint-item-int-maxlen) | String       |
| **public** | [newList(List&lt;String> aList)](#newlistliststring-alist)      | void         |
| **public** | [setDataFrom(Object rec)](#setdatafromobject-rec)            | void         |
| **public** | [getList()](#getlist)                                        | List&lt;String> |



## Constructors

### `JtvListBox(JtvRect bounds, int numCols, JtvScrollBar hScrollBar, JtvScrollBar vScrollBar)`

Creates a list box with the given bounds, column count, and optional scroll bars.

**Parameters**

- `bounds` - the bounding rectangle
- `numCols` - number of display columns (usually 1)
- `hScrollBar` - optional horizontal scroll bar (may be `null`)
- `vScrollBar` - optional vertical scroll bar (may be `null`)



## Fields

### `List<String> list`

The list of string items displayed in the list box.


## Methods

### `getText(int item, int maxLen)`

**Overrides:**

`getText(int item, int maxLen)` in `org.viktor44.jtvision.views.JtvListViewer`

Returns the display text for the item at position `item`,
truncated to `maxLen` characters.

**Parameters**

- `item` - the zero-based item index
- `maxLen` - the maximum number of characters to return

**Returns**

the item's display text, or an empty string if out of range



### `newList(List<String> aList)`

Replaces the current list with `aList`, updates the scroll range, and
focuses the first item.

**Parameters**

- `aList` - the new list of items; if `null` an empty list is used



### `setDataFrom(Object rec)`

Focuses the item at the given index and redraws.

**Parameters**

- `rec` - an `Integer` containing the item index to focus



### `getList()`

The list of string items displayed in the list box.



