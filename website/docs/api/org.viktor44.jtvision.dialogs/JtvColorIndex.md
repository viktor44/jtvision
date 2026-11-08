---
title: "JtvColorIndex"
sidebar_label: "JtvColorIndex"
---

Class JtvColorIndex
===================

```java
public class JtvColorIndex
```


Stores the per-group selected-item indices for a [JtvColorDialog](JtvColorDialog.md).



TColorIndex is a data transfer object used by [JtvColorDialog](JtvColorDialog.md) to save and
restore the focused item within each colour group. This allows the dialog to
remember which palette entry was last selected in each group across successive
opens.



The arrays are pre-allocated to a generous upper bound (256 groups); only the
first [colorSize](#int-colorsize) entries are valid.

org.viktor44.jtvision.dialogs.JtvColorIndex

## Summary

### Constructors

| Visibility | Signature                         |
| ---------- | --------------------------------- |
| public     | [JtvColorIndex()](#jtvcolorindex) |

### Fields

| Modifiers     | Field name                    | Type  |
| ------------- | ----------------------------- | ----- |
| **protected** | [groupIndex](#int-groupindex) | int   |
| **protected** | [colorSize](#int-colorsize)   | int   |
| **protected** | [colorIndex](#int-colorindex) | int[] |



## Constructors

### `JtvColorIndex()`

*No method description provided*



## Fields

### `int groupIndex`

The index of the most recently active colour group.


### `int colorSize`

The number of valid entries in [colorIndex](#int-colorindex).


### `int[] colorIndex`

Per-group focused-item indices. Entry `i` holds the index of the
focused [JtvColorItem](JtvColorItem.md) within group `i`.


