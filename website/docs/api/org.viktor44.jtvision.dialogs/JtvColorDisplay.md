---
title: "JtvColorDisplay"
sidebar_label: "JtvColorDisplay"
---

Class JtvColorDisplay
=====================

```java
public class JtvColorDisplay
extends JtvView
```


A preview view that renders sample text in the currently selected colours.



TColorDisplay is used inside [JtvColorDialog](JtvColorDialog.md) to give the user an immediate
preview of how the selected foreground and background colours look. It displays
its `text` string (repeated to fill the view) rendered with the colour
attribute stored in the active entry of `colorArray`.



The colour is updated by:

- Calling [setColor(JtvColorAttr[], int)](#setcolororgviktor44jtvisioncorejtvcolorattr-colors-int-index) — points to a palette entry and
      broadcasts `cmColorSet`.
- `cmColorForegroundChanged` broadcasts — update the foreground nibble.
- `cmColorBackgroundChanged` broadcasts — update the background nibble.



org.viktor44.jtvision.dialogs.JtvColorDisplay

## Summary

### Constructors

| Visibility | Signature                                                                                    |
| ---------- | -------------------------------------------------------------------------------------------- |
| public     | [JtvColorDisplay(JtvRect bounds, String aText)](#jtvcolordisplayjtvrect-bounds-string-atext) |

### Fields

| Modifiers   | Field name                                                    | Type                                      |
| ----------- | ------------------------------------------------------------- | ----------------------------------------- |
| **private** | [colorArray](#orgviktor44jtvisioncorejtvcolorattr-colorarray) | org.viktor44.jtvision.core.JtvColorAttr[] |
| **private** | [colorIndex](#int-colorindex)                                 | int                                       |
| **private** | [text](#string-text)                                          | String                                    |

### Methods

| Modifiers   | Method signature                                                                                                                       | Return type |
| ----------- | -------------------------------------------------------------------------------------------------------------------------------------- | ----------- |
| **public**  | [draw()](#draw)                                                                                                                        | void        |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                                                              | void        |
| **public**  | [setColor(org.viktor44.jtvision.core.JtvColorAttr[] colors, int index)](#setcolororgviktor44jtvisioncorejtvcolorattr-colors-int-index) | void        |
| **public**  | [setColor(org.viktor44.jtvision.core.JtvColorAttr[] aColor)](#setcolororgviktor44jtvisioncorejtvcolorattr-acolor)                      | void        |
| **private** | [hasColorRef()](#hascolorref)                                                                                                          | boolean     |



## Constructors

### `JtvColorDisplay(JtvRect bounds, String aText)`

Creates a colour display view with the given bounds and sample text.

**Parameters**

- `bounds` - the bounding rectangle
- `aText` - the sample text to render (e.g., `"Text"`)



## Fields

### `org.viktor44.jtvision.core.JtvColorAttr[] colorArray`

Palette array that contains the currently edited colour entry.


### `int colorIndex`

Index within `colorArray` of the currently edited colour entry.


### `String text`

The sample text repeated across the view to demonstrate the colour combination.


## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the sample text repeatedly across the view using the current colour attribute.
Falls back to `errorAttr` when no colour has been set or the colour is 0.



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles `cmColorForegroundChanged` and `cmColorBackgroundChanged`
broadcasts to update the respective nibble of the stored colour attribute.

**Parameters**

- `event` - the incoming event



### `setColor(org.viktor44.jtvision.core.JtvColorAttr[] colors, int index)`

Sets a new colour reference and broadcasts `cmColorSet` to update the
colour selectors. Redraws the view.

**Parameters**

- `colors` - palette data array that owns the colour entry
- `index` - index in `colors` of the active colour attribute byte



### `setColor(org.viktor44.jtvision.core.JtvColorAttr[] aColor)`

Overload for callers that pass a one-element mutable array.

**Parameters**

- `aColor` - one-element colour reference array



### `hasColorRef()`

*No method description provided*



