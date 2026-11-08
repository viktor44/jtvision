---
title: "JtvColorItemList"
sidebar_label: "JtvColorItemList"
---

Class JtvColorItemList
======================

```java
public class JtvColorItemList
extends JtvListViewer
```


A list viewer that displays the colour items within the active [JtvColorGroup](JtvColorGroup.md).



TColorItemList shows the individual palette entries (e.g., "Normal text",
"Highlighted text") for the currently selected group in a [JtvColorDialog](JtvColorDialog.md).



When the user focuses an item, this viewer broadcasts:

- `cmSaveColorIndex` — saves the focused item index for the current group.
- `cmNewColorIndex` — sends the palette index of the focused item so the
      colour selectors and display can update.

When a `cmNewColorItem` broadcast arrives (from [JtvColorGroupList](JtvColorGroupList.md)
when the active group changes), this viewer replaces its item list with the new
group's items and restores the previously saved focus position.

org.viktor44.jtvision.dialogs.JtvColorItemList

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                                                       |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvColorItemList(JtvRect bounds, JtvScrollBar aScrollBar, List&lt;org.viktor44.jtvision.dialogs.JtvColorItem> aItems)](#jtvcoloritemlistjtvrect-bounds-jtvscrollbar-ascrollbar-listorgviktor44jtvisiondialogsjtvcoloritem-aitems) |

### Fields

| Modifiers   | Field name                                                 | Type                                             |
| ----------- | ---------------------------------------------------------- | ------------------------------------------------ |
| **private** | [items](#listorgviktor44jtvisiondialogsjtvcoloritem-items) | List&lt;org.viktor44.jtvision.dialogs.JtvColorItem> |

### Methods

| Modifiers   | Method signature                                             | Return type                                |
| ----------- | ------------------------------------------------------------ | ------------------------------------------ |
| **public**  | [focusItem(int item)](#focusitemint-item)                    | void                                       |
| **public**  | [getText(int item, int maxLen)](#gettextint-item-int-maxlen) | String                                     |
| **private** | [itemAt(int idx)](#itematint-idx)                            | org.viktor44.jtvision.dialogs.JtvColorItem |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)    | void                                       |



## Constructors

### `JtvColorItemList(JtvRect bounds, JtvScrollBar aScrollBar, List<org.viktor44.jtvision.dialogs.JtvColorItem> aItems)`

Creates a colour item list with the given bounds, scroll bar, and initial item list.

**Parameters**

- `bounds` - the bounding rectangle
- `aScrollBar` - the vertical scroll bar
- `aItems` - the initial item list (may be `null`)



## Fields

### `List<org.viktor44.jtvision.dialogs.JtvColorItem> items`

The list of [JtvColorItem](JtvColorItem.md) objects currently displayed.


## Methods

### `focusItem(int item)`

**Overrides:**

`focusItem(int item)` in `org.viktor44.jtvision.views.JtvListViewer`

Focuses item `item` and broadcasts the palette index of that item
so colour selectors can reflect the current colour.

**Parameters**

- `item` - the zero-based item index to focus



### `getText(int item, int maxLen)`

**Overrides:**

`getText(int item, int maxLen)` in `org.viktor44.jtvision.views.JtvListViewer`

Returns the display name of the item at position `item`,
truncated to `maxLen` characters.

**Parameters**

- `item` - the zero-based item index
- `maxLen` - the maximum number of characters to return

**Returns**

the item name, or an empty string if out of range



### `itemAt(int idx)`

Returns the item at position `idx`, or `null` if out of range.

**Parameters**

- `idx`


### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvListViewer`

Handles `cmNewColorItem` broadcasts from [JtvColorGroupList](JtvColorGroupList.md).
Replaces the item list with the new group's items and restores the saved focus index.

**Parameters**

- `event` - the incoming event



