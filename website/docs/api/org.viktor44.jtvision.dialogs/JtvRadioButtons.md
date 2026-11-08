---
title: "JtvRadioButtons"
sidebar_label: "JtvRadioButtons"
---

Class JtvRadioButtons
=====================

```java
public class JtvRadioButtons
extends JtvCluster
```


A cluster of mutually exclusive radio-button controls.



JtvRadioButtons is a specialised [JtvCluster](JtvCluster.md) where only one item can be
selected at a time. The inherited [JtvCluster.value](JtvCluster.md#int-value) field stores the
zero-based index of the currently selected item. Pressing or moving to an item
sets `value` to that item's index.



A bullet character (`\u2022`) appears inside the radio button (`(\u2022)`) when
the item is selected. Radio-button clusters are often accompanied by a
[JtvLabel](JtvLabel.md) that describes the group.

org.viktor44.jtvision.dialogs.JtvRadioButtons

## Summary

### Constructors

| Visibility | Signature                                                                                      |
| ---------- | ---------------------------------------------------------------------------------------------- |
| public     | [JtvRadioButtons(JtvRect bounds, String[] items)](#jtvradiobuttonsjtvrect-bounds-string-items) |

### Fields

| Modifiers                | Field name                     | Type   |
| ------------------------ | ------------------------------ | ------ |
| **private static final** | [radioIcon](#string-radioicon) | String |

### Methods

| Modifiers  | Method signature                                  | Return type |
| ---------- | ------------------------------------------------- | ----------- |
| **public** | [draw()](#draw)                                   | void        |
| **public** | [mark(int item)](#markint-item)                   | boolean     |
| **public** | [movedTo(int item)](#movedtoint-item)             | void        |
| **public** | [press(int item)](#pressint-item)                 | void        |
| **public** | [setDataFrom(Object rec)](#setdatafromobject-rec) | void        |



## Constructors

### `JtvRadioButtons(JtvRect bounds, String[] items)`

Creates a radio-button cluster with the given bounds and item labels.

**Parameters**

- `bounds` - the bounding rectangle
- `items` - the label strings for each radio button



## Fields

### `String radioIcon`

Icon template drawn for each item: `" ( ) "` (space, open paren, space, close paren, space).


## Methods

### `draw()`

Draws all radio buttons using the `( )` / `(\u2022)` icon.



### `mark(int item)`

**Overrides:**

`mark(int item)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Returns `true` if the radio button at `item` is the selected one.

**Parameters**

- `item` - the zero-based item index

**Returns**

`true` if `item == value`



### `movedTo(int item)`

**Overrides:**

`movedTo(int item)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Updates [JtvCluster.value](JtvCluster.md#int-value) when the cursor moves to item `item`
without an explicit press, keeping the selection in sync with cursor movement.

**Parameters**

- `item` - the zero-based item index now under the cursor



### `press(int item)`

**Overrides:**

`press(int item)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Selects item `item` by setting [JtvCluster.value](JtvCluster.md#int-value) to its index.

**Parameters**

- `item` - the zero-based item index to select



### `setDataFrom(Object rec)`

**Overrides:**

`setDataFrom(Object rec)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Extends the inherited `setDataFrom` to synchronise [JtvCluster.sel](JtvCluster.md#int-sel)
with the newly loaded [JtvCluster.value](JtvCluster.md#int-value).

**Parameters**

- `rec` - an `Integer` containing the index of the item to select



