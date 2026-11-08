---
title: "JtvMultiCheckBoxes"
sidebar_label: "JtvMultiCheckBoxes"
---

Class JtvMultiCheckBoxes
========================

```java
public class JtvMultiCheckBoxes
extends JtvCluster
```


A cluster of multi-state check-box controls.



JtvMultiCheckBoxes extends [JtvCluster](JtvCluster.md) to support items with more than two
states (checked/unchecked). Each item occupies a fixed number of bits in the
inherited 32-bit [JtvCluster.value](JtvCluster.md#int-value) field. Pressing an item cycles it
through `selRange` states.



The display character for each state is taken from the `states` string
at the corresponding index (state 0 = `states.charAt(0)`, etc.).



The `flags` parameter encodes the bit layout:

- Low byte — a bitmask covering the bits used per item (`mask`).
- High byte — the number of bits allocated per item (`bits`).



org.viktor44.jtvision.dialogs.JtvMultiCheckBoxes

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                               |
| ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvMultiCheckBoxes(JtvRect bounds, String[] items, int aSelRange, int aFlags, String aStates)](#jtvmulticheckboxesjtvrect-bounds-string-items-int-aselrange-int-aflags-string-astates) |

### Fields

| Modifiers                | Field name                               | Type   |
| ------------------------ | ---------------------------------------- | ------ |
| **private static final** | [multiCheckIcon](#string-multicheckicon) | String |
| **private**              | [selRange](#int-selrange)                | int    |
| **private**              | [flags](#int-flags)                      | int    |
| **private**              | [states](#string-states)                 | String |

### Methods

| Modifiers  | Method signature                          | Return type |
| ---------- | ----------------------------------------- | ----------- |
| **public** | [draw()](#draw)                           | void        |
| **public** | [multiMark(int item)](#multimarkint-item) | int         |
| **public** | [press(int item)](#pressint-item)         | void        |



## Constructors

### `JtvMultiCheckBoxes(JtvRect bounds, String[] items, int aSelRange, int aFlags, String aStates)`

Creates a multi-state check-box cluster.

**Parameters**

- `bounds` - the bounding rectangle
- `items` - the label strings for each item
- `aSelRange` - the number of states each item cycles through
- `aFlags` - bit layout: low byte = mask, high byte = bits per item
- `aStates` - characters for each state (index 0 = first state, etc.);
                    defaults to `" X"` if `null`



## Fields

### `String multiCheckIcon`

Icon template drawn for each item: `" [ ] "`.


### `int selRange`

Number of distinct states each item can cycle through.


### `int flags`

Bit layout descriptor: low byte is the per-item bit mask, high byte is
the number of bits per item.


### `String states`

Characters used to render each state index inside the check-box icon.


## Methods

### `draw()`

Draws all multi-state check boxes using the `[ ]` icon with per-state characters.



### `multiMark(int item)`

**Overrides:**

`multiMark(int item)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Returns the current state index for item `item` by extracting the relevant
bits from [JtvCluster.value](JtvCluster.md#int-value).

**Parameters**

- `item` - the zero-based item index

**Returns**

the state index (0 to `selRange - 1`)



### `press(int item)`

**Overrides:**

`press(int item)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Advances item `item` to its next state by incrementing its field in
[JtvCluster.value](JtvCluster.md#int-value) modulo `selRange`.

**Parameters**

- `item` - the zero-based item index



