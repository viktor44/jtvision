---
title: "JtvCheckBoxes"
sidebar_label: "JtvCheckBoxes"
---

Class JtvCheckBoxes
===================

```java
public class JtvCheckBoxes
extends JtvCluster
```


A cluster of independent check-box controls.



TCheckBoxes is a specialised [JtvCluster](JtvCluster.md) that allows any combination of
items to be checked simultaneously. Each item corresponds to one bit in the
inherited [JtvCluster.value](JtvCluster.md#int-value) field; pressing an item toggles its bit.
An `X` is displayed inside the box (`[X]`) when the item is checked.



Up to 16 items (bits 0–15) are supported by the default data-size of 2 bytes.
Check-box clusters are often accompanied by a [JtvLabel](JtvLabel.md) that describes
the group.

org.viktor44.jtvision.dialogs.JtvCheckBoxes

## Summary

### Constructors

| Visibility | Signature                                                                                  |
| ---------- | ------------------------------------------------------------------------------------------ |
| public     | [JtvCheckBoxes(JtvRect bounds, String[] items)](#jtvcheckboxesjtvrect-bounds-string-items) |

### Fields

| Modifiers                | Field name                     | Type   |
| ------------------------ | ------------------------------ | ------ |
| **private static final** | [checkIcon](#string-checkicon) | String |

### Methods

| Modifiers  | Method signature                  | Return type |
| ---------- | --------------------------------- | ----------- |
| **public** | [draw()](#draw)                   | void        |
| **public** | [mark(int item)](#markint-item)   | boolean     |
| **public** | [press(int item)](#pressint-item) | void        |



## Constructors

### `JtvCheckBoxes(JtvRect bounds, String[] items)`

Creates a check-box cluster with the given bounds and item labels.

**Parameters**

- `bounds` - the bounding rectangle
- `items` - the label strings for each check box



## Fields

### `String checkIcon`

Icon template drawn for each item: `" [ ] "` (space, open bracket, space, close bracket, space).


## Methods

### `draw()`

Draws all check boxes using the `[ ]` / `[X]` icon.



### `mark(int item)`

**Overrides:**

`mark(int item)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Returns `true` if the check box at `item` is checked.
A check box is checked when bit `item` of [JtvCluster.value](JtvCluster.md#int-value) is set.

**Parameters**

- `item` - the zero-based item index (0\u201315)

**Returns**

`true` if the item is checked



### `press(int item)`

**Overrides:**

`press(int item)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Toggles the checked state of item `item` by flipping its bit in
[JtvCluster.value](JtvCluster.md#int-value).

**Parameters**

- `item` - the zero-based item index (0\u201315)



