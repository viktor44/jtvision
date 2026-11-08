---
title: "JtvSurfaceView"
sidebar_label: "JtvSurfaceView"
---

Class JtvSurfaceView
====================

```java
public class JtvSurfaceView
extends JtvView
```


A view that renders a [JtvDrawSurface](JtvDrawSurface.md) with a scroll offset.



JtvSurfaceView maps a rectangular window onto a [JtvDrawSurface](JtvDrawSurface.md)
cell buffer. The `delta` field defines the top-left corner of
the visible region within the surface. Cells that lie outside the
surface bounds are filled with the background colour from palette
entry 1.

**Typical use**

Create a [JtvDrawSurface](JtvDrawSurface.md) of the desired virtual size, draw content
into it directly, then attach it to a TSurfaceView. Adjust
`delta` to pan the viewport over the surface.

**Colour palette**

The single-entry palette `cpSurfaceView` maps to:

1. `1` — background/empty cell colour.



org.viktor44.jtvision.views.JtvSurfaceView

## Summary

### Constructors

| Visibility | Signature                                                                                                      |
| ---------- | -------------------------------------------------------------------------------------------------------------- |
| public     | [JtvSurfaceView(JtvRect bounds)](#jtvsurfaceviewjtvrect-bounds)                                                |
| public     | [JtvSurfaceView(JtvRect bounds, JtvDrawSurface surface)](#jtvsurfaceviewjtvrect-bounds-jtvdrawsurface-surface) |

### Fields

| Modifiers                | Field name                                                        | Type                                       |
| ------------------------ | ----------------------------------------------------------------- | ------------------------------------------ |
| **private static final** | [cpSurfaceView](#orgviktor44jtvisioncorejtvpalette-cpsurfaceview) | org.viktor44.jtvision.core.JtvPalette      |
| **private**              | [surface](#orgviktor44jtvisionviewsjtvdrawsurface-surface)        | org.viktor44.jtvision.views.JtvDrawSurface |
| **private**              | [delta](#orgviktor44jtvisioncorejtvpoint-delta)                   | org.viktor44.jtvision.core.JtvPoint        |

### Methods

| Modifiers  | Method signature            | Return type                           |
| ---------- | --------------------------- | ------------------------------------- |
| **public** | [draw()](#draw)             | void                                  |
| **public** | [getPalette()](#getpalette) | org.viktor44.jtvision.core.JtvPalette |



## Constructors

### `JtvSurfaceView(JtvRect bounds)`

Constructs a surface view with the given bounds and no attached surface.
The view renders as a blank background until a surface is assigned.

**Parameters**

- `bounds` - the bounding rectangle



### `JtvSurfaceView(JtvRect bounds, JtvDrawSurface surface)`

Constructs a surface view with the given bounds and an attached surface.

**Parameters**

- `bounds` - the bounding rectangle
- `surface` - the draw surface to render, or `null` for blank



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpSurfaceView`

Single-entry colour palette:

1. `1` — background/empty cell colour.




### `org.viktor44.jtvision.views.JtvDrawSurface surface`

The [JtvDrawSurface](JtvDrawSurface.md) whose cells this view renders. May be
`null`, in which case the entire view is filled with the
background colour.


### `org.viktor44.jtvision.core.JtvPoint delta`

The scroll offset: the surface coordinates of the top-left cell
that maps to position `(0, 0)` in this view.


## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Renders the visible portion of the attached [JtvDrawSurface](JtvDrawSurface.md).



For each screen row `y`, cells are copied from surface coordinates
`(y + delta.y, x + delta.x)` to the draw buffer. Cells outside
the surface bounds are filled with a space character using the
background colour from palette entry 1. Each row is written to the
screen via [JtvView.writeBuf](JtvView.md#writebufint-x-int-y-int-w-int-h-jtvdrawbuffer-b).



If the view has zero width or height, returns immediately without
drawing.



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the surface view's colour palette `cpSurfaceView`.

**Returns**

the single-entry palette



