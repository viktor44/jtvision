---
title: "JtvScrollBar"
sidebar_label: "JtvScrollBar"
---

Class JtvScrollBar
==================

```java
public class JtvScrollBar
extends JtvView
```





A horizontal or vertical scroll bar control.



JtvScrollBar provides a standard scrolling control that can be oriented
either horizontally (width &gt; 1) or vertically (width == 1). The
orientation is determined automatically from the bounding rectangle
supplied at construction time.

**Visual layout**

The scroll bar is rendered as:


```
  [◄][░░░░░█░░░░][►]   (horizontal)
  [▲]                   (vertical)
  [░]
  [█]  ← indicator
  [░]
  [▼]
```


When the range is zero (min == max) a neutral fill character is used
in place of the track and no indicator is shown.

**Interaction**

- Clicking an arrow button steps by `arStep`.
- Clicking in the track (page area) steps by `pgStep`.
- Dragging the indicator sets the value proportionally.
- Keyboard arrow keys step by `arStep`; PgUp/PgDn step by
      `pgStep`; Home/End jump to the minimum/maximum.
- Mouse wheel scrolls vertically (up/down) or horizontally
      (left/right) by 3 × `arStep`.

**Broadcasts**

Whenever the value changes, [scrollDraw()](#scrolldraw) broadcasts
`cmScrollBarChanged` to the owner. When any part of the scroll
bar is first clicked, `cmScrollBarClicked` is broadcast before
any value change.

**Grow mode**

Vertical bars grow to keep the left edge and right edge pinned and the
bottom edge pinned (`gfGrowLoX | gfGrowHiX | gfGrowHiY`).
Horizontal bars keep the top and bottom edges pinned and the right
edge pinned (`gfGrowLoY | gfGrowHiX | gfGrowHiY`).

**Colour palette**

The three-entry palette `cpScrollBar` maps to:

1. Track (page) area colour.
2. Arrow button colour.
3. Indicator colour.



org.viktor44.jtvision.views.JtvScrollBar

## Summary

### Constructors

| Visibility | Signature                                                   |
| ---------- | ----------------------------------------------------------- |
| public     | [JtvScrollBar(JtvRect bounds)](#jtvscrollbarjtvrect-bounds) |

### Fields

| Modifiers                | Field name                                                    | Type                                  |
| ------------------------ | ------------------------------------------------------------- | ------------------------------------- |
| **private**              | [value](#int-value)                                           | int                                   |
| **private**              | [minVal](#int-minval)                                         | int                                   |
| **private**              | [maxVal](#int-maxval)                                         | int                                   |
| **private**              | [pgStep](#int-pgstep)                                         | int                                   |
| **private**              | [arStep](#int-arstep)                                         | int                                   |
| **private**              | [chars](#char-chars)                                          | char[]                                |
| **private static final** | [hChars](#char-hchars)                                        | char[]                                |
| **private static final** | [vChars](#char-vchars)                                        | char[]                                |
| **private static final** | [cpScrollBar](#orgviktor44jtvisioncorejtvpalette-cpscrollbar) | org.viktor44.jtvision.core.JtvPalette |

### Methods

| Modifiers   | Method signature                                                                                                                      | Return type                           |
| ----------- | ------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------- |
| **public**  | [draw()](#draw)                                                                                                                       | void                                  |
| **public**  | [drawPos(int pos)](#drawposint-pos)                                                                                                   | void                                  |
| **public**  | [getPalette()](#getpalette)                                                                                                           | org.viktor44.jtvision.core.JtvPalette |
| **public**  | [getPos()](#getpos)                                                                                                                   | int                                   |
| **private** | [scrollSize()](#scrollsize)                                                                                                           | int                                   |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                                                             | void                                  |
| **private** | [getPartCode(JtvPoint mouse, JtvRect extent, int p, int s)](#getpartcodejtvpoint-mouse-jtvrect-extent-int-p-int-s)                    | int                                   |
| **public**  | [scrollDraw()](#scrolldraw)                                                                                                           | void                                  |
| **public**  | [scrollStep(int part)](#scrollstepint-part)                                                                                           | int                                   |
| **public**  | [setParams(int aValue, int aMin, int aMax, int aPgStep, int aArStep)](#setparamsint-avalue-int-amin-int-amax-int-apgstep-int-aarstep) | void                                  |
| **public**  | [setRange(int aMin, int aMax)](#setrangeint-amin-int-amax)                                                                            | void                                  |
| **public**  | [setStep(int aPgStep, int aArStep)](#setstepint-apgstep-int-aarstep)                                                                  | void                                  |
| **public**  | [setValue(int aValue)](#setvalueint-avalue)                                                                                           | void                                  |
| **public**  | [getValue()](#getvalue)                                                                                                               | int                                   |
| **public**  | [getPgStep()](#getpgstep)                                                                                                             | int                                   |
| **public**  | [getArStep()](#getarstep)                                                                                                             | int                                   |



## Constructors

### `JtvScrollBar(JtvRect bounds)`

Constructs a scroll bar from the given bounds.



A width of exactly 1 produces a *vertical* scroll bar;
any other width produces a *horizontal* scroll bar.
Grow mode and display characters are selected accordingly.
Subscribes to `evMouseWheel` events.

**Parameters**

- `bounds` - the bounding rectangle \u2014 1 column wide for vertical,
                1 row tall for horizontal



## Fields

### `int value`

The current scroll position, clamped to `[minVal, maxVal]`.
Modified by [setValue(int)](#setvalueint-avalue) and [setParams](#setparamsint-avalue-int-amin-int-amax-int-apgstep-int-aarstep).


### `int minVal`

The minimum allowed value. Set via [setParams](#setparamsint-avalue-int-amin-int-amax-int-apgstep-int-aarstep) or [setRange](#setrangeint-amin-int-amax).


### `int maxVal`

The maximum allowed value. Set via [setParams](#setparamsint-avalue-int-amin-int-amax-int-apgstep-int-aarstep) or [setRange](#setrangeint-amin-int-amax).


### `int pgStep`

The number of positions to scroll when the user clicks in the track (page area).
Set via [setStep](#setstepint-apgstep-int-aarstep) or [setParams](#setparamsint-avalue-int-amin-int-amax-int-apgstep-int-aarstep).


### `int arStep`

The number of positions to scroll when the user clicks an arrow button or presses an arrow key. 
Set via [setStep](#setstepint-apgstep-int-aarstep) or [setParams](#setparamsint-avalue-int-amin-int-amax-int-apgstep-int-aarstep).


### `char[] chars`

The five display characters for this scroll bar in order:
`[0]` decrease arrow, `[1]` increase arrow,
`[2]` track fill, `[3]` indicator, `[4]` neutral fill.
Copied from `hChars` or `vChars` at construction time.


### `char[] hChars`

Display characters for a horizontal scroll bar:
`\u25c4` (decrease), `\u25ba` (increase), `\u2591` (track),
`\u2588` (indicator), `\u2592` (neutral fill when range == 0).


### `char[] vChars`

Display characters for a vertical scroll bar:
`\u25b2` (decrease), `\u25bc` (increase), `\u2591` (track),
`\u2588` (indicator), `\u2592` (neutral fill when range == 0).


### `org.viktor44.jtvision.core.JtvPalette cpScrollBar`

Three-entry colour palette:

1. `4` — track area.
2. `5` — arrow buttons.
3. `5` — indicator.




## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Renders the scroll bar at the current position via [drawPos(int)](#drawposint-pos).



### `drawPos(int pos)`

Renders the scroll bar with the indicator at pixel position `pos`.



The bar is drawn as: decrease-arrow, track (with indicator at
`pos`), increase-arrow. When `minVal` equals
`maxVal`, the neutral fill character is used and no indicator
is drawn.

**Parameters**

- `pos` - the pixel position of the indicator (1-based within the track)



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the colour palette `cpScrollBar`.

**Returns**

the scroll bar's colour palette



### `getPos()`

Computes the pixel position of the indicator within the track,
proportional to the current value in the range `[minVal, maxVal]`.
Returns `1` when the range is zero.

**Returns**

the 1-based indicator position within the scroll track



### `scrollSize()`

Returns the effective scroll bar length in cells, which is the
width (horizontal) or height (vertical), clamped to a minimum of 3.



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles mouse, keyboard, and wheel events for the scroll bar.

- **Mouse wheel** — steps the value by 3 × `arStep`
      in the appropriate direction and broadcasts
      `cmScrollBarClicked` then `cmScrollBarChanged`.
- **Mouse down on arrow** — enters an auto-repeat loop stepping
      by the arrow step while the button is held.
- **Mouse down on track** — allows dragging the indicator to
      set the value proportionally.
- **Arrow key** — steps by `arStep`.
- **PgUp/PgDn** — steps by `pgStep`.
- **Home/End** — jumps to `minVal` or `maxVal`.



**Parameters**

- `event` - the incoming event



### `getPartCode(JtvPoint mouse, JtvRect extent, int p, int s)`

Determines which logical scroll-bar part was clicked based on the
mouse position relative to the indicator and arrow positions.

**Parameters**

- `mouse` - the mouse position in view-local coordinates
- `extent` - the scroll bar extent (grown by 1 for hit-testing tolerance)
- `p` - the current indicator pixel position
- `s` - the scroll track size minus 1

**Returns**

one of `sbUpArrow`, `sbDownArrow`,
        `sbLeftArrow`, `sbRightArrow`,
        `sbPageUp`, `sbPageDown`,
        `sbPageLeft`, `sbPageRight`,
        `sbIndicator`, or `-1` if outside the bar



### `scrollDraw()`

Broadcasts `cmScrollBarChanged` to the owner group, notifying
associated scrollers or list viewers that the value has changed.



### `scrollStep(int part)`

Returns the signed scroll step for the given scroll-bar part code.



Parts with bit 1 clear use `arStep`; parts with bit 1 set use
`pgStep`. Parts with bit 0 clear produce a negative (decrease)
step; parts with bit 0 set produce a positive (increase) step.

**Parameters**

- `part` - the scroll-bar part code (`sbXXXX` constant)

**Returns**

the step amount (positive = increase, negative = decrease)



### `setParams(int aValue, int aMin, int aMax, int aPgStep, int aArStep)`

Sets all scroll bar parameters atomically.



Clamps `aValue` to `[aMin, aMax]` and ensures
`aMax >= aMin`. Redraws the bar if the displayed position
changed. Broadcasts `cmScrollBarChanged` if the value changed.

**Parameters**

- `aValue` - the new current value
- `aMin` - the new minimum value
- `aMax` - the new maximum value (clamped to at least `aMin`)
- `aPgStep` - the new page step size
- `aArStep` - the new arrow step size



### `setRange(int aMin, int aMax)`

Changes the minimum and maximum values without changing the current
value, page step, or arrow step.

**Parameters**

- `aMin` - the new minimum value
- `aMax` - the new maximum value



### `setStep(int aPgStep, int aArStep)`

Changes the page step and arrow step sizes without affecting the
current value or range.

**Parameters**

- `aPgStep` - the new page step size
- `aArStep` - the new arrow step size



### `setValue(int aValue)`

Sets the current scroll bar value, clamped to the existing
`[minVal, maxVal]` range. Redraws and broadcasts if the
value changed.

**Parameters**

- `aValue` - the new value



### `getValue()`

The current scroll position, clamped to `[minVal, maxVal]`.
Modified by [setValue(int)](#setvalueint-avalue) and [setParams](#setparamsint-avalue-int-amin-int-amax-int-apgstep-int-aarstep).



### `getPgStep()`

The number of positions to scroll when the user clicks in the track (page area).
Set via [setStep](#setstepint-apgstep-int-aarstep) or [setParams](#setparamsint-avalue-int-amin-int-amax-int-apgstep-int-aarstep).



### `getArStep()`

The number of positions to scroll when the user clicks an arrow button or presses an arrow key. 
Set via [setStep](#setstepint-apgstep-int-aarstep) or [setParams](#setparamsint-avalue-int-amin-int-amax-int-apgstep-int-aarstep).



