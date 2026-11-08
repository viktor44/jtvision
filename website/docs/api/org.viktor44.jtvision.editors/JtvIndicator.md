---
title: "JtvIndicator"
sidebar_label: "JtvIndicator"
---

Class JtvIndicator
==================

```java
public class JtvIndicator
extends JtvView
```


A line/column indicator displayed in the lower-left corner of an editor window.



The indicator shows the current cursor position in `line:column` format.
When the associated editor's text has been modified, an asterisk (`*`) prefix
is prepended to signal unsaved changes.



Editor window objects create and associate an indicator automatically.
The editor calls [setValue](#setvaluejtvpoint-p-boolean-amodified) to keep the display current whenever the
cursor moves or the modification flag changes.

**Colour palette**

The two-entry palette `cpIndicator` maps to:

1. Normal (non-dragging) indicator colour.
2. Indicator colour while dragging.



org.viktor44.jtvision.editors.JtvIndicator

## Summary

### Constructors

| Visibility | Signature                                                   |
| ---------- | ----------------------------------------------------------- |
| public     | [JtvIndicator(JtvRect bounds)](#jtvindicatorjtvrect-bounds) |

### Fields

| Modifiers                | Field name                                                    | Type                                  |
| ------------------------ | ------------------------------------------------------------- | ------------------------------------- |
| **private static final** | [cpIndicator](#orgviktor44jtvisioncorejtvpalette-cpindicator) | org.viktor44.jtvision.core.JtvPalette |
| **private static final** | [dragFrame](#char-dragframe)                                  | char                                  |
| **private static final** | [normalFrame](#char-normalframe)                              | char                                  |
| **private**              | [location](#orgviktor44jtvisioncorejtvpoint-location)         | org.viktor44.jtvision.core.JtvPoint   |
| **private**              | [modified](#boolean-modified)                                 | boolean                               |

### Methods

| Modifiers  | Method signature                                                                 | Return type                           |
| ---------- | -------------------------------------------------------------------------------- | ------------------------------------- |
| **public** | [draw()](#draw)                                                                  | void                                  |
| **public** | [getPalette()](#getpalette)                                                      | org.viktor44.jtvision.core.JtvPalette |
| **public** | [setValue(JtvPoint p, boolean aModified)](#setvaluejtvpoint-p-boolean-amodified) | void                                  |
| **public** | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable)       | void                                  |



## Constructors

### `JtvIndicator(JtvRect bounds)`

Constructs an indicator anchored to the bottom of its owner.
Sets `growMode` to `gfGrowLoY | gfGrowHiY` so the indicator
stays in the status row as the window is resized.

**Parameters**

- `bounds` - the bounding rectangle for this indicator



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpIndicator`

Two-entry colour palette:

1. `2` — normal (non-dragging) indicator colour.
2. `3` — indicator colour while dragging.




### `char dragFrame`

*No description provided*


### `char normalFrame`

*No description provided*


### `org.viktor44.jtvision.core.JtvPoint location`

The current (column, line) position to display.
Updated by the associated [JtvEditor](JtvEditor.md) via [setValue](#setvaluejtvpoint-p-boolean-amodified).


### `boolean modified`

`true` if the associated editor's buffer has been modified.
When `true`, [draw()](#draw) prepends an asterisk to the indicator.


## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the indicator as a horizontal line filled with the frame character, with the
cursor position overlaid in `line:column` format.
An asterisk is shown at the leftmost position when `modified` is `true`.
The color and frame character differ when the window is being dragged.



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the indicator palette (`CIndicator`), mapping color indices 1 and 2
onto the second and third entries in the standard application palette
(the same colors used by window frames).

**Returns**

the indicator palette



### `setValue(JtvPoint p, boolean aModified)`

Updates the displayed cursor position and modification flag, then redraws the view
if either value changed.  Called by the associated editor after each cursor movement
or text modification.

**Parameters**

- `p` - new (column, line) position, or `null` to leave unchanged
- `aModified` - `true` if the editor buffer has been modified



### `setState(int aState, boolean enable)`

**Overrides:**

`setState(int aState, boolean enable)` in `org.viktor44.jtvision.views.JtvView`

Calls the inherited `setState`, then redraws the indicator when the
`sfDragging` or `sfActive` flags change so the frame character and
color are updated immediately.

**Parameters**

- `aState` - state flag(s) being changed
- `enable` - `true` to set the flag, `false` to clear it



