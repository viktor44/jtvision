---
title: "JtvMonoSelector"
sidebar_label: "JtvMonoSelector"
---

Class JtvMonoSelector
=====================

```java
public class JtvMonoSelector
extends JtvCluster
```


A radio-button cluster for selecting monochrome text attributes.



JtvMonoSelector is a specialised [JtvCluster](JtvCluster.md) used by [JtvColorDialog](JtvColorDialog.md) on
monochrome displays (when `showMarkers` is `true`). It offers five
predefined text attribute choices: Normal, Highlight, Underline, Inverse, and Bold.



Selecting an item sets [JtvCluster.value](JtvCluster.md#int-value) to the corresponding CGA attribute
byte and broadcasts `cmColorForegroundChanged` and `cmColorBackgroundChanged`
so other views in the dialog update accordingly.



Responds to `cmColorSet` broadcasts by updating its selection to match the
closest predefined attribute.

org.viktor44.jtvision.dialogs.JtvMonoSelector

## Summary

### Constructors

| Visibility | Signature                                                         |
| ---------- | ----------------------------------------------------------------- |
| public     | [JtvMonoSelector(JtvRect bounds)](#jtvmonoselectorjtvrect-bounds) |

### Fields

| Modifiers                | Field name                    | Type     |
| ------------------------ | ----------------------------- | -------- |
| **private static final** | [monoColors](#int-monocolors) | int[]    |
| **private static final** | [button](#string-button)      | String   |
| **private static final** | [items](#string-items)        | String[] |

### Methods

| Modifiers  | Method signature                                          | Return type |
| ---------- | --------------------------------------------------------- | ----------- |
| **public** | [draw()](#draw)                                           | void        |
| **public** | [handleEvent(JtvEvent event)](#handleeventjtvevent-event) | void        |
| **public** | [mark(int item)](#markint-item)                           | boolean     |
| **public** | [newColor()](#newcolor)                                   | void        |
| **public** | [press(int item)](#pressint-item)                         | void        |
| **public** | [movedTo(int item)](#movedtoint-item)                     | void        |



## Constructors

### `JtvMonoSelector(JtvRect bounds)`

Creates a monochrome attribute selector with the given bounds.
Uses the predefined item labels and listens for `cmColorSet` broadcasts.

**Parameters**

- `bounds` - the bounding rectangle



## Fields

### `int[] monoColors`

CGA attribute bytes for each selectable monochrome style:
Normal (0x07), Highlight (0x0F), Underline (0x01), Inverse (0x70), Bold (0x09).


### `String button`

Radio-button icon template.


### `String[] items`

Item labels for the five monochrome attribute choices.


## Methods

### `draw()`

Draws the monochrome attribute options as radio buttons.



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Handles `cmColorSet` broadcasts by updating the selected item to the
one whose CGA attribute matches the received value.

**Parameters**

- `event` - the incoming event



### `mark(int item)`

**Overrides:**

`mark(int item)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Returns `true` if the item at `item` corresponds to the current
[JtvCluster.value](JtvCluster.md#int-value) attribute.

**Parameters**

- `item` - the zero-based item index

**Returns**

`true` if this item's attribute matches the current value



### `newColor()`

Broadcasts the foreground and background components of the current CGA attribute.
Called whenever the selected colour changes.



### `press(int item)`

**Overrides:**

`press(int item)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Applies the CGA attribute for item `item` and broadcasts the colour change.

**Parameters**

- `item` - the zero-based item index that was pressed



### `movedTo(int item)`

**Overrides:**

`movedTo(int item)` in `org.viktor44.jtvision.dialogs.JtvCluster`

Applies the CGA attribute for item `item` when the cursor moves to it,
and broadcasts the colour change.

**Parameters**

- `item` - the zero-based item index now under the cursor



