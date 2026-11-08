---
title: "JtvDrawSurface"
sidebar_label: "JtvDrawSurface"
---

Class JtvDrawSurface
====================

```java
public class JtvDrawSurface
```


A two-dimensional off-screen cell buffer used as a drawing surface.



JtvDrawSurface stores a rectangular grid of [JtvScreenCell](../org.viktor44.jtvision.core/JtvScreenCell.md) objects
laid out in row-major order. It is not a view itself — it has no parent,
no event handling, and no palette. Its primary consumer is
[JtvSurfaceView](JtvSurfaceView.md), which maps a scrollable viewport onto a surface
and copies cells to the screen during `draw()`.

**Layout**

Cells are stored as a flat array of size `width × height`. The
index of cell `(row, col)` is `row * width + col`. All
coordinates use the convention `(y, x)` to match the natural
row-then-column traversal order.

**Lifecycle**

After construction the surface may be resized with [resize(JtvPoint)](#resizejtvpoint-asize)
or grown incrementally with [grow(JtvPoint)](#growjtvpoint-delta). Call
[clear()](#clear) to fill the buffer with space characters and the default
grey-on-black attribute (`0x07`) before drawing.

org.viktor44.jtvision.views.JtvDrawSurface

## Summary

### Constructors

| Visibility | Signature                                                     |
| ---------- | ------------------------------------------------------------- |
| public     | [JtvDrawSurface()](#jtvdrawsurface)                           |
| public     | [JtvDrawSurface(JtvPoint size)](#jtvdrawsurfacejtvpoint-size) |

### Fields

| Modifiers   | Field name                                         | Type                                       |
| ----------- | -------------------------------------------------- | ------------------------------------------ |
| **private** | [size](#orgviktor44jtvisioncorejtvpoint-size)      | org.viktor44.jtvision.core.JtvPoint        |
| **private** | [data](#orgviktor44jtvisioncorejtvscreencell-data) | org.viktor44.jtvision.core.JtvScreenCell[] |

### Methods

| Modifiers  | Method signature                                | Return type                              |
| ---------- | ----------------------------------------------- | ---------------------------------------- |
| **public** | [resize(JtvPoint aSize)](#resizejtvpoint-asize) | void                                     |
| **public** | [grow(JtvPoint delta)](#growjtvpoint-delta)     | void                                     |
| **public** | [clear()](#clear)                               | void                                     |
| **public** | [at(int y, int x)](#atint-y-int-x)              | org.viktor44.jtvision.core.JtvScreenCell |
| **public** | [inBounds(int y, int x)](#inboundsint-y-int-x)  | boolean                                  |



## Constructors

### `JtvDrawSurface()`

Constructs an empty surface with zero dimensions.
Call [resize(JtvPoint)](#resizejtvpoint-asize) before accessing cells.



### `JtvDrawSurface(JtvPoint size)`

Constructs a surface with the given dimensions and initialises all
cells to default values.

**Parameters**

- `size` - the desired width (`x`) and height (`y`),
               or `null` for an empty surface



## Fields

### `org.viktor44.jtvision.core.JtvPoint size`

The current width and height of the surface in cells.
Updated by [resize(JtvPoint)](#resizejtvpoint-asize) and [grow(JtvPoint)](#growjtvpoint-delta).


### `org.viktor44.jtvision.core.JtvScreenCell[] data`

The flat cell array, stored in row-major order. Length is always
`size.x × size.y`. An empty array when the surface has zero
dimensions.


## Methods

### `resize(JtvPoint aSize)`

Resizes the surface to `aSize`, replacing the cell array with a
freshly-allocated one. If `aSize` is `null` or has a
non-positive dimension the surface is reset to zero size with an empty
array.

**Parameters**

- `size` - the new dimensions; must have positive `x` and
               `y` for a non-empty surface



### `grow(JtvPoint delta)`

Grows (or shrinks) the surface by adding `aDelta` to the current
dimensions and resizing. Positive delta values expand; negative values
contract. If `aDelta` is `null`, does nothing.

**Parameters**

- `delta` - the amount to add to each dimension



### `clear()`

Fills every cell in the buffer with a space character (`' '`)
and the default attribute `0x07` (light grey on black).



### `at(int y, int x)`

Returns the cell at position `(y, x)` without bounds checking.
The caller is responsible for verifying the coordinates with
[inBounds(int, int)](#inboundsint-y-int-x) before calling this method.

**Parameters**

- `y` - the row index (0-based)
- `x` - the column index (0-based)

**Returns**

the cell at the given position



### `inBounds(int y, int x)`

Returns `true` if `(y, x)` is a valid cell address within
the current surface dimensions.

**Parameters**

- `y` - the row index to test
- `x` - the column index to test

**Returns**

`true` if the coordinates are within bounds



