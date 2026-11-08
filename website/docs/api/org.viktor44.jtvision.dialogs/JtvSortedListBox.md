---
title: "JtvSortedListBox"
sidebar_label: "JtvSortedListBox"
---

Class JtvSortedListBox
======================

```java
public class JtvSortedListBox
extends JtvListBox
```


A list box that keeps its items in sorted order and supports incremental search.



JtvSortedListBox extends [JtvListBox](JtvListBox.md) to maintain its item list in
case-insensitive sorted order. It provides incremental (type-ahead) searching:
as the user types characters the list scrolls to the first matching item.



The sort order and key extraction can be customised by overriding
[compareItems(Object, Object)](#compareitemsobject-a-object-b) and [getKey(String)](#getkeystring-s).
The default implementation sorts objects by their `Object.toString()`
representation, case-insensitively.



Backspace removes the last typed search character. Typing a period (`.`)
finds the dot in the currently focused item and truncates or extends the search
prefix to that position. The search prefix is reset when the focused item changes.

org.viktor44.jtvision.dialogs.JtvSortedListBox

## Summary

### Constructors

| Visibility | Signature                                                                                                                                       |
| ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvSortedListBox(JtvRect bounds, int aNumCols, JtvScrollBar aScrollBar)](#jtvsortedlistboxjtvrect-bounds-int-anumcols-jtvscrollbar-ascrollbar) |

### Fields

| Modifiers           | Field name                           | Type         |
| ------------------- | ------------------------------------ | ------------ |
| **protected**       | [shiftState](#int-shiftstate)        | int          |
| **protected**       | [searchPos](#int-searchpos)          | int          |
| **protected final** | [items](#listobject-items)           | List&lt;Object> |
| **protected**       | [searchPrefix](#string-searchprefix) | String       |

### Methods

| Modifiers     | Method signature                                                   | Return type  |
| ------------- | ------------------------------------------------------------------ | ------------ |
| **protected** | [getKey(String s)](#getkeystring-s)                                | Object       |
| **protected** | [compareItems(Object a, Object b)](#compareitemsobject-a-object-b) | int          |
| **protected** | [itemText(Object item)](#itemtextobject-item)                      | String       |
| **public**    | [getText(int item, int maxLen)](#gettextint-item-int-maxlen)       | String       |
| **public**    | [list()](#list)                                                    | List&lt;Object> |
| **public**    | [newListObjects(List&lt;?> aList)](#newlistobjectslist-alist)         | void         |
| **public**    | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)          | void         |
| **protected** | [findByPrefix(String prefix)](#findbyprefixstring-prefix)          | int          |



## Constructors

### `JtvSortedListBox(JtvRect bounds, int aNumCols, JtvScrollBar aScrollBar)`

Creates a sorted list box with the given bounds, column count, and scroll bar.
A visible cursor is shown at column 1.

**Parameters**

- `bounds` - the bounding rectangle
- `aNumCols` - number of display columns
- `aScrollBar` - vertical scroll bar (may be `null`)



## Fields

### `int shiftState`

The keyboard shift state at the time the incremental search began.


### `int searchPos`

The character position of the search cursor within the search prefix (or -1 if no search is active).


### `List<Object> items`

The raw item objects in sorted order.


### `String searchPrefix`

The current incremental search prefix string.


## Methods

### `getKey(String s)`

Extracts the sort key from a search string.
Override to perform custom key extraction (e.g., strip a path prefix).

**Parameters**

- `s` - the raw search string typed by the user

**Returns**

the key object used to locate a matching item



### `compareItems(Object a, Object b)`

Compares two item objects for ordering.
The default implementation delegates to [itemText(Object)](#itemtextobject-item) and compares
the resulting strings case-insensitively.

**Parameters**

- `a` - the first item
- `b` - the second item

**Returns**

negative, zero, or positive as `a` is less than, equal to, or greater than `b`



### `itemText(Object item)`

Returns the display (and sort) text for an item object.
The default implementation calls `Object.toString()`.

**Parameters**

- `item` - the item object

**Returns**

the item's text representation



### `getText(int item, int maxLen)`

**Overrides:**

`getText(int item, int maxLen)` in `org.viktor44.jtvision.dialogs.JtvListBox`

Returns the display text for item `item`, truncated to `maxLen` characters.

**Parameters**

- `item` - the zero-based item index
- `maxLen` - the maximum number of characters to return

**Returns**

the item text, or an empty string if out of range



### `list()`

Returns the raw item list (in sorted order).

**Returns**

the list of item objects



### `newListObjects(List<?> aList)`

Replaces the current item list with `aList`, sorts it using
[compareItems(Object, Object)](#compareitemsobject-a-object-b), resets the search state, and focuses
the first item.

**Parameters**

- `aList` - the new list of items; `null` is treated as an empty list



### `handleEvent(JtvEvent event)`

Extends the inherited event handler to add incremental (type-ahead) search.



Printable characters are appended to the search prefix and the list scrolls
to the first matching item. Backspace removes the last prefix character.
A period (`.`) navigates relative to a dot in the focused item's text.
The search prefix is reset whenever the focused item changes.

**Parameters**

- `event` - the incoming event



### `findByPrefix(String prefix)`

Returns the index of the first item whose text starts with `prefix`
(case-insensitive), or `-1` if no match is found.

**Parameters**

- `prefix` - the prefix string to search for

**Returns**

the zero-based index of the matching item, or `-1`



