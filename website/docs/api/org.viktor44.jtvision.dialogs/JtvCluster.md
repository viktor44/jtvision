---
title: "JtvCluster"
sidebar_label: "JtvCluster"
---

Class JtvCluster
================

```java
public class JtvCluster
extends JtvView
```


Abstract base class for clustered controls such as check boxes and radio buttons.



A cluster is a group of labelled controls that all respond in a similar way.
The user can select items with mouse clicks, cursor movement (Up/Down),
and Alt+letter hot-keys. Each item can be enabled or disabled independently
via the [enableMask](#long-enablemask) field.



The current state of the cluster is held in the [value](#int-value) field; the
exact semantics of `value` are defined by concrete subclasses
([JtvCheckBoxes](JtvCheckBoxes.md) toggles bits; [JtvRadioButtons](JtvRadioButtons.md) stores the index of
the selected item).



When the bounding rectangle is wide enough, items wrap into multiple columns.

**Colour palette**

The five-entry palette `cpCluster` maps to:

1. Normal item text.
2. Selected item text.
3. Shortcut key (normal state).
4. Shortcut key (selected state).
5. Disabled item text.



org.viktor44.jtvision.dialogs.JtvCluster

## Summary

### Constructors

| Visibility | Signature                                                                            |
| ---------- | ------------------------------------------------------------------------------------ |
| public     | [JtvCluster(JtvRect bounds, String[] items)](#jtvclusterjtvrect-bounds-string-items) |

### Fields

| Modifiers                | Field name                                                | Type                                  |
| ------------------------ | --------------------------------------------------------- | ------------------------------------- |
| **private static final** | [cpCluster](#orgviktor44jtvisioncorejtvpalette-cpcluster) | org.viktor44.jtvision.core.JtvPalette |
| **protected**            | [value](#int-value)                                       | int                                   |
| **protected**            | [sel](#int-sel)                                           | int                                   |
| **protected**            | [strings](#liststring-strings)                            | List&lt;String>                          |
| **protected**            | [enableMask](#long-enablemask)                            | long                                  |

### Methods

| Modifiers  | Method signature                                                                     | Return type                           |
| ---------- | ------------------------------------------------------------------------------------ | ------------------------------------- |
| **public** | [getDataTo(Object rec)](#getdatatoobject-rec)                                        | void                                  |
| **public** | [setDataFrom(Object rec)](#setdatafromobject-rec)                                    | void                                  |
| **public** | [drawBox(String icon, char marker)](#drawboxstring-icon-char-marker)                 | void                                  |
| **public** | [drawMultiBox(String icon, String markers)](#drawmultiboxstring-icon-string-markers) | void                                  |
| **public** | [getPalette()](#getpalette)                                                          | org.viktor44.jtvision.core.JtvPalette |
| **public** | [getHelpCtx()](#gethelpctx)                                                          | int                                   |
| **public** | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                            | void                                  |
| **public** | [moveSel(int count, int s)](#moveselint-count-int-s)                                 | void                                  |
| **public** | [mark(int item)](#markint-item)                                                      | boolean                               |
| **public** | [multiMark(int item)](#multimarkint-item)                                            | int                                   |
| **public** | [movedTo(int item)](#movedtoint-item)                                                | void                                  |
| **public** | [press(int item)](#pressint-item)                                                    | void                                  |
| **public** | [column(int item)](#columnint-item)                                                  | int                                   |
| **public** | [findSel(JtvPoint p)](#findseljtvpoint-p)                                            | int                                   |
| **public** | [row(int item)](#rowint-item)                                                        | int                                   |
| **public** | [buttonState(int item)](#buttonstateint-item)                                        | boolean                               |
| **public** | [setButtonState(long mask, boolean enable)](#setbuttonstatelong-mask-boolean-enable) | void                                  |
| **public** | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable)           | void                                  |
| **public** | [getValue()](#getvalue)                                                              | int                                   |
| **public** | [setValue(int value)](#setvalueint-value)                                            | void                                  |



## Constructors

### `JtvCluster(JtvRect bounds, String[] items)`

Creates a cluster with the given bounds and item labels.

**Parameters**

- `bounds` - the bounding rectangle
- `items` - the item label strings; `null` entries are silently skipped



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpCluster`

Five-entry colour palette:

1. `0x10` — normal item text.
2. `0x11` — selected item text.
3. `0x12` — shortcut key (normal state).
4. `0x12` — shortcut key (selected state).
5. `0x1F` — disabled item text.




### `int value`

The current value of the cluster.
For [JtvCheckBoxes](JtvCheckBoxes.md) each bit corresponds to a checked item.
For [JtvRadioButtons](JtvRadioButtons.md) this is the zero-based index of the selected item.


### `int sel`

The index of the currently highlighted (cursor-over) item within the cluster.


### `List<String> strings`

Ordered list of item label strings (may contain tilde-delimited hot-key characters).


### `long enableMask`

Bitmask controlling which items are enabled.
Bit `i` (0-based) enables item `i`; a cleared bit disables the item.
Initialised to all-ones so all items start enabled.


## Methods

### `getDataTo(Object rec)`

Reads the cluster's current value into a record object.
Callers should read the [value](#int-value) field directly in this implementation.

**Parameters**

- `rec` - the destination record (unused in base implementation)



### `setDataFrom(Object rec)`

**Overrides:**

`setDataFrom(Object rec)` in `org.viktor44.jtvision.views.JtvView`

Sets the cluster value from a record object and redraws the view.

**Parameters**

- `rec` - an `Integer` containing the new `#value`



### `drawBox(String icon, char marker)`

Draws all items using a simple two-state (unmarked/marked) icon.
Delegates to [drawMultiBox(String, String)](#drawmultiboxstring-icon-string-markers) with a two-character marker string.

**Parameters**

- `icon` - the icon template (e.g., `" [ ] "` or `" ( ) "`)
- `marker` - the character placed inside the icon when an item is marked



### `drawMultiBox(String icon, String markers)`

Draws all cluster items in columns across the view.
Each item is rendered with the supplied icon; the character from `markers`
at the index returned by [multiMark(int)](#multimarkint-item) is placed in the icon's slot.

**Parameters**

- `icon` - the icon template
- `markers` - string whose characters correspond to possible mark states



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the default color palette `cpCluster`.

**Returns**

the cluster's color palette



### `getHelpCtx()`

**Overrides:**

`getHelpCtx()` in `org.viktor44.jtvision.views.JtvView`

Returns the help context for the currently highlighted item.
If the base help context is `hcNoContext` it is returned unchanged;
otherwise the item index is added to give per-item help.

**Returns**

the help context identifier



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles mouse and keyboard events for the cluster.



Mouse clicks highlight and optionally press the item under the cursor.
Up/Down arrow keys move the selection. Alt+letter hot-keys and Space
(when focused) activate the corresponding item.

**Parameters**

- `event` - the incoming event



### `moveSel(int count, int s)`

Moves the selection cursor to item `s` (if within range) and redraws.

**Parameters**

- `count` - the number of steps taken; must be `<= strings.size()`
- `s` - the target item index



### `mark(int item)`

Returns whether the given item is currently marked (checked/selected).
The base implementation always returns `false`; subclasses override this.

**Parameters**

- `item` - the zero-based item index

**Returns**

`true` if the item is marked



### `multiMark(int item)`

Returns the mark-state index used by [drawMultiBox](#drawmultiboxstring-icon-string-markers) to select the
display character from the markers string.
Returns `1` if [mark(int)](#markint-item) is true, `0` otherwise.

**Parameters**

- `item` - the zero-based item index

**Returns**

the index into the markers string



### `movedTo(int item)`

Called when the cursor moves to a new item without pressing it.
The base implementation does nothing; subclasses (e.g., [JtvRadioButtons](JtvRadioButtons.md))
override this to update the value.

**Parameters**

- `item` - the zero-based index of the item now under the cursor



### `press(int item)`

Called when the user presses the item at `item`.
The base implementation does nothing; subclasses override this to change
[value](#int-value).

**Parameters**

- `item` - the zero-based index of the pressed item



### `column(int item)`

Returns the X-coordinate of the left edge of the column containing `item`.
Items are laid out in rows of height `size.y`; this calculates the pixel
column offset for the given item index.

**Parameters**

- `item` - the zero-based item index

**Returns**

the column offset in character cells



### `findSel(JtvPoint p)`

Returns the item index at mouse position `p`, or `-1` if the
position is outside the cluster or does not correspond to a valid item.

**Parameters**

- `p` - the mouse position in local coordinates

**Returns**

zero-based item index, or `-1`



### `row(int item)`

Returns the row (Y offset) of item `item` within the cluster view.

**Parameters**

- `item` - the zero-based item index

**Returns**

the row index (0-based)



### `buttonState(int item)`

Returns whether item `item` is currently enabled.
An item is enabled when its corresponding bit in [enableMask](#long-enablemask) is set.
Items beyond index 31 are always considered disabled.

**Parameters**

- `item` - the zero-based item index

**Returns**

`true` if the item is enabled



### `setButtonState(long mask, boolean enable)`

Enables or disables the items selected by `aMask`.
Also updates [org.viktor44.jtvision.core.ViewFlags.ofSelectable](../org.viktor44.jtvision.core/ViewFlags.md#int-ofselectable) based on whether any items remain enabled.

**Parameters**

- `mask` - bitmask of items to enable/disable (bit `i` = item `i`)
- `enable` - `true` to enable the masked items, `false` to disable them



### `setState(int aState, boolean enable)`

**Overrides:**

`setState(int aState, boolean enable)` in `org.viktor44.jtvision.views.JtvView`

Extends the inherited `setState` to redraw when the selected state changes.

**Parameters**

- `aState` - the state flag(s) being changed
- `enable` - `true` to set, `false` to clear



### `getValue()`

The current value of the cluster.
For [JtvCheckBoxes](JtvCheckBoxes.md) each bit corresponds to a checked item.
For [JtvRadioButtons](JtvRadioButtons.md) this is the zero-based index of the selected item.



### `setValue(int value)`

The current value of the cluster.
For [JtvCheckBoxes](JtvCheckBoxes.md) each bit corresponds to a checked item.
For [JtvRadioButtons](JtvRadioButtons.md) this is the zero-based index of the selected item.

**Parameters**

- `value`


