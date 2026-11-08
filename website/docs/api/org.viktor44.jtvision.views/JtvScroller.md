---
title: "JtvScroller"
sidebar_label: "JtvScroller"
---

Class JtvScroller
=================

```java
public class JtvScroller
extends JtvView
```


A scrollable view that provides a viewport onto a larger virtual surface.



JtvScroller extends [JtvView](JtvView.md) to link two optional [JtvScrollBar](JtvScrollBar.md)
controls to a scrolling offset ([delta](#orgviktor44jtvisioncorejtvpoint-delta)) that subclasses use in
their [draw()](JtvView.md#draw) method to determine which portion of a larger
content area to render.

**How scrolling works**

When either scroll bar's value changes, the scroll bar broadcasts
`cmScrollBarChanged`. JtvScroller receives this broadcast in
[handleEvent(JtvEvent)](#handleeventjtvevent-event) and calls [scrollDraw()](#scrolldraw), which
reads the new values from both scroll bars, updates [delta](#orgviktor44jtvisioncorejtvpoint-delta), and
redraws the view. Subclasses render the part of their content starting
at [delta](#orgviktor44jtvisioncorejtvpoint-delta).



Programmatic scrolling is done via [scrollTo(int, int)](#scrolltoint-x-int-y), which
sets both scroll bar values and triggers a redraw through the same
broadcast path. The virtual content size is reported via
[setLimit(int, int)](#setlimitint-x-int-y), which adjusts the scroll bar ranges to
`[0, limit - viewSize]`.

**Draw locking**

The [drawLock](#int-drawlock) counter defers redraws while multiple scroll bar
parameters are being updated. When the counter drops to zero,
[checkDraw()](#checkdraw) triggers a deferred redraw if [drawFlag](#boolean-drawflag)
was set.

**Scroll bar visibility**

Scroll bars are shown only when the scroller is both active
(`sfActive`) and selected (`sfSelected`). The
[setState(int, boolean)](#setstateint-astate-boolean-enable) override calls
[showSBar(JtvScrollBar)](#showsbarjtvscrollbar-sbar) whenever either flag changes.

**Default palette**

The two-entry palette `cpScroller` maps to:

1. `6` — normal text colour.
2. `7` — selected/highlighted colour.



org.viktor44.jtvision.views.JtvScroller

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                       |
| ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvScroller(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar)](#jtvscrollerjtvrect-bounds-jtvscrollbar-ahscrollbar-jtvscrollbar-avscrollbar) |

### Fields

| Modifiers                | Field name                                                     | Type                                     |
| ------------------------ | -------------------------------------------------------------- | ---------------------------------------- |
| **protected**            | [delta](#orgviktor44jtvisioncorejtvpoint-delta)                | org.viktor44.jtvision.core.JtvPoint      |
| **protected**            | [limit](#orgviktor44jtvisioncorejtvpoint-limit)                | org.viktor44.jtvision.core.JtvPoint      |
| **protected**            | [hScrollBar](#orgviktor44jtvisionviewsjtvscrollbar-hscrollbar) | org.viktor44.jtvision.views.JtvScrollBar |
| **protected**            | [vScrollBar](#orgviktor44jtvisionviewsjtvscrollbar-vscrollbar) | org.viktor44.jtvision.views.JtvScrollBar |
| **protected**            | [drawLock](#int-drawlock)                                      | int                                      |
| **protected**            | [drawFlag](#boolean-drawflag)                                  | boolean                                  |
| **private static final** | [cpScroller](#orgviktor44jtvisioncorejtvpalette-cpscroller)    | org.viktor44.jtvision.core.JtvPalette    |

### Methods

| Modifiers  | Method signature                                                           | Return type                           |
| ---------- | -------------------------------------------------------------------------- | ------------------------------------- |
| **public** | [changeBounds(JtvRect bounds)](#changeboundsjtvrect-bounds)                | void                                  |
| **public** | [checkDraw()](#checkdraw)                                                  | void                                  |
| **public** | [getPalette()](#getpalette)                                                | org.viktor44.jtvision.core.JtvPalette |
| **public** | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                  | void                                  |
| **public** | [scrollDraw()](#scrolldraw)                                                | void                                  |
| **public** | [scrollTo(int x, int y)](#scrolltoint-x-int-y)                             | void                                  |
| **public** | [setLimit(int x, int y)](#setlimitint-x-int-y)                             | void                                  |
| **public** | [showSBar(JtvScrollBar sBar)](#showsbarjtvscrollbar-sbar)                  | void                                  |
| **public** | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable) | void                                  |
| **public** | [getDelta()](#getdelta)                                                    | org.viktor44.jtvision.core.JtvPoint   |
| **public** | [setDelta(JtvPoint delta)](#setdeltajtvpoint-delta)                        | void                                  |
| **public** | [getLimit()](#getlimit)                                                    | org.viktor44.jtvision.core.JtvPoint   |
| **public** | [setLimit(JtvPoint limit)](#setlimitjtvpoint-limit)                        | void                                  |
| **public** | [getDrawLock()](#getdrawlock)                                              | int                                   |
| **public** | [setDrawLock(int drawLock)](#setdrawlockint-drawlock)                      | void                                  |
| **public** | [isDrawFlag()](#isdrawflag)                                                | boolean                               |
| **public** | [setDrawFlag(boolean drawFlag)](#setdrawflagboolean-drawflag)              | void                                  |



## Constructors

### `JtvScroller(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar)`

Constructs a scroller with the given bounds and optional scroll bars.



Sets `ofSelectable` on options so the scroller can receive focus,
and adds `evBroadcast` to the event mask so it receives
`cmScrollBarChanged` broadcasts.

**Parameters**

- `bounds` - the bounding rectangle
- `aHScrollBar` - the horizontal scroll bar, or `null`
- `aVScrollBar` - the vertical scroll bar, or `null`



## Fields

### `org.viktor44.jtvision.core.JtvPoint delta`

The current horizontal and vertical scroll offset.
Subclasses add this to row/column indices when drawing their content.


### `org.viktor44.jtvision.core.JtvPoint limit`

The total virtual content size. Scroll bar ranges are computed from
this field via [setLimit(int, int)](#setlimitint-x-int-y): the scroll bar maximum is `limit - viewSize`.


### `org.viktor44.jtvision.views.JtvScrollBar hScrollBar`

The optional horizontal scroll bar. `null` if this scroller
has no horizontal scrolling.


### `org.viktor44.jtvision.views.JtvScrollBar vScrollBar`

The optional vertical scroll bar. `null` if this scroller
has no vertical scrolling.


### `int drawLock`

Deferred-redraw lock counter. Incremented by callers that update
multiple scroll bar parameters in one logical operation; decremented
by [checkDraw()](#checkdraw) after the updates are complete.


### `boolean drawFlag`

Set to `true` when a redraw is pending but deferred because
[drawLock](#int-drawlock) is non-zero. Cleared and actioned by
[checkDraw()](#checkdraw) when the lock reaches zero.


### `org.viktor44.jtvision.core.JtvPalette cpScroller`

Two-entry colour palette:

1. `6` — normal text.
2. `7` — highlighted text.




## Methods

### `changeBounds(JtvRect bounds)`

**Overrides:**

`changeBounds(JtvRect bounds)` in `org.viktor44.jtvision.views.JtvView`

Updates the bounding rectangle and refreshes the scroll bar ranges
via [setLimit(int, int)](#setlimitint-x-int-y) without triggering an immediate redraw
(uses the draw lock). Clears [drawFlag](#boolean-drawflag) and redraws afterwards.

**Parameters**

- `bounds` - the new bounding rectangle



### `checkDraw()`

Triggers a deferred redraw if the draw lock has reached zero and a
redraw was requested ([drawFlag](#boolean-drawflag) is `true`).



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the default scroller colour palette `cpScroller`.

**Returns**

the scroller's colour palette



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles `cmScrollBarChanged` broadcasts from the associated
scroll bars by calling [scrollDraw()](#scrolldraw) to update [delta](#orgviktor44jtvisioncorejtvpoint-delta)
and redraw the view.

**Parameters**

- `event` - the incoming event



### `scrollDraw()`

Reads the current values from both scroll bars, updates [delta](#orgviktor44jtvisioncorejtvpoint-delta),
and redraws the view. If the draw lock is held, sets [drawFlag](#boolean-drawflag)
to request a deferred redraw. Also adjusts the cursor position to
track the scroll offset change.



### `scrollTo(int x, int y)`

Sets both scroll bars to the given `(x, y)` position.
Uses the draw lock to defer the resulting redraws until both values
are set, then calls [checkDraw()](#checkdraw).

**Parameters**

- `x` - the desired horizontal scroll position
- `y` - the desired vertical scroll position



### `setLimit(int x, int y)`

Sets the virtual content size and adjusts the scroll bar ranges
to `[0, limit - viewSize]`, using the current view size as
the page step and `1` is kept as the arrow step.
Uses the draw lock to defer redraws until both bars are updated.

**Parameters**

- `x` - the total virtual width in columns
- `y` - the total virtual height in rows



### `showSBar(JtvScrollBar sBar)`

Shows or hides `sBar` based on whether this scroller is both
active and selected. Called from [setState(int, boolean)](#setstateint-astate-boolean-enable) when
either flag changes.

**Parameters**

- `sBar` - the scroll bar to show or hide; `null` is ignored



### `setState(int aState, boolean enable)`

**Overrides:**

`setState(int aState, boolean enable)` in `org.viktor44.jtvision.views.JtvView`

Propagates state changes and updates scroll bar visibility when
`sfActive` or `sfSelected` change.

**Parameters**

- `aState` - the state bits to modify
- `enable` - `true` to set; `false` to clear



### `getDelta()`

The current horizontal and vertical scroll offset.
Subclasses add this to row/column indices when drawing their content.



### `setDelta(JtvPoint delta)`

The current horizontal and vertical scroll offset.
Subclasses add this to row/column indices when drawing their content.

**Parameters**

- `delta`


### `getLimit()`

The total virtual content size. Scroll bar ranges are computed from
this field via [setLimit(int, int)](#setlimitint-x-int-y): the scroll bar maximum is `limit - viewSize`.



### `setLimit(JtvPoint limit)`

The total virtual content size. Scroll bar ranges are computed from
this field via [setLimit(int, int)](#setlimitint-x-int-y): the scroll bar maximum is `limit - viewSize`.

**Parameters**

- `limit`


### `getDrawLock()`

Deferred-redraw lock counter. Incremented by callers that update
multiple scroll bar parameters in one logical operation; decremented
by [checkDraw()](#checkdraw) after the updates are complete.



### `setDrawLock(int drawLock)`

Deferred-redraw lock counter. Incremented by callers that update
multiple scroll bar parameters in one logical operation; decremented
by [checkDraw()](#checkdraw) after the updates are complete.

**Parameters**

- `drawLock`


### `isDrawFlag()`

Set to `true` when a redraw is pending but deferred because
[drawLock](#int-drawlock) is non-zero. Cleared and actioned by
[checkDraw()](#checkdraw) when the lock reaches zero.



### `setDrawFlag(boolean drawFlag)`

Set to `true` when a redraw is pending but deferred because
[drawLock](#int-drawlock) is non-zero. Cleared and actioned by
[checkDraw()](#checkdraw) when the lock reaches zero.

**Parameters**

- `drawFlag`


