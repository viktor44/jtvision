---
title: "JtvColorSelector"
sidebar_label: "JtvColorSelector"
---

Class JtvColorSelector
======================

```java
public class JtvColorSelector
extends JtvView
```


An interactive colour-swatch grid used within [JtvColorDialog](JtvColorDialog.md).



TColorSelector displays a grid of coloured blocks from which the user selects
either a foreground or background colour. The type is determined by the
[ColorSel](JtvColorSelector.md) enum passed at construction:

- [ColorSel.csForeground](JtvColorSelector.md) — shows all 16 foreground colours.
- [ColorSel.csBackground](JtvColorSelector.md) — shows the first 8 background colours.

The currently selected colour is marked with an `x` inside its block.
Mouse clicks and arrow key navigation change the selection. When the colour
changes, this view broadcasts `cmColorForegroundChanged` or
`cmColorBackgroundChanged` with the new colour value.



Responds to `cmColorSet` broadcasts to update its selection from the
current palette entry.

org.viktor44.jtvision.dialogs.JtvColorSelector

## Summary

### Constructors

| Visibility | Signature                                                                                                |
| ---------- | -------------------------------------------------------------------------------------------------------- |
| public     | [JtvColorSelector(JtvRect bounds, ColorSel aSelType)](#jtvcolorselectorjtvrect-bounds-colorsel-aseltype) |

### Fields

| Modifiers                | Field name                                                             | Type                                                    |
| ------------------------ | ---------------------------------------------------------------------- | ------------------------------------------------------- |
| **private**              | [color](#int-color)                                                    | int                                                     |
| **private**              | [selType](#orgviktor44jtvisiondialogsjtvcolorselectorcolorsel-seltype) | org.viktor44.jtvision.dialogs.JtvColorSelector.ColorSel |
| **private static final** | [icon](#char-icon)                                                     | char                                                    |

### Methods

| Modifiers   | Method signature                                          | Return type |
| ----------- | --------------------------------------------------------- | ----------- |
| **public**  | [draw()](#draw)                                           | void        |
| **private** | [colorChanged()](#colorchanged)                           | void        |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event) | void        |



## Constructors

### `JtvColorSelector(JtvRect bounds, ColorSel aSelType)`

Creates a colour selector of the given type.

**Parameters**

- `bounds` - the bounding rectangle (should accommodate a 4-column × 2- or 4-row grid)
- `aSelType` - `ColorSel#csForeground` or `ColorSel#csBackground`



## Fields

### `int color`

The currently selected colour index.


### `org.viktor44.jtvision.dialogs.JtvColorSelector.ColorSel selType`

Whether this selector controls the foreground or background colour.


### `char icon`

Unicode full-block character used to render each colour swatch.


## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the colour swatch grid. Each colour swatch is rendered as three
full-block characters. The currently selected swatch is marked with `x`.



### `colorChanged()`

Broadcasts the colour change command (`cmColorForegroundChanged` or
`cmColorBackgroundChanged`) with the current colour value.



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles mouse, keyboard, and broadcast events.

- Mouse click/drag — selects the colour under the pointer.
- Arrow keys — move the colour selection in the grid.
- `cmColorSet` broadcast — updates the selection from the packed colour byte.



**Parameters**

- `event` - the incoming event



