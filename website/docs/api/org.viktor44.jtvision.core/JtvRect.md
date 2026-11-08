---
title: "JtvRect"
sidebar_label: "JtvRect"
---

Class JtvRect
=============

```java
public class JtvRect
```


A rectangle defined by two [JtvPoint](JtvPoint.md) corner coordinates.



Field `a` holds the upper-left corner and `b` holds the lower-right corner.
Every JT Vision view is rectangular: their constructors accept a `JtvRect` parameter
(conventionally named `Bounds`) that determines the region the view covers.
`TRect` objects are not views themselves and cannot draw.



The rectangle `[a=(2,2), b=(5,4)]` covers 3 columns × 2 rows and contains 6 cells.
A rectangle with `a == b` has no area ([isEmpty()](#isempty) returns `true`).

org.viktor44.jtvision.core.JtvRect

## Summary

### Constructors

| Visibility | Signature                                                                      |
| ---------- | ------------------------------------------------------------------------------ |
| public     | [JtvRect()](#jtvrect)                                                          |
| public     | [JtvRect(int ax, int ay, int bx, int by)](#jtvrectint-ax-int-ay-int-bx-int-by) |
| public     | [JtvRect(JtvPoint p1, JtvPoint p2)](#jtvrectjtvpoint-p1-jtvpoint-p2)           |
| public     | [JtvRect(JtvRect other)](#jtvrectjtvrect-other)                                |

### Fields

| Modifiers   | Field name                              | Type                                |
| ----------- | --------------------------------------- | ----------------------------------- |
| **private** | [a](#orgviktor44jtvisioncorejtvpoint-a) | org.viktor44.jtvision.core.JtvPoint |
| **private** | [b](#orgviktor44jtvisioncorejtvpoint-b) | org.viktor44.jtvision.core.JtvPoint |

### Methods

| Modifiers     | Method signature                                | Return type                         |
| ------------- | ----------------------------------------------- | ----------------------------------- |
| **public**    | [getCopy()](#getcopy)                           | org.viktor44.jtvision.core.JtvRect  |
| **public**    | [move(int dx, int dy)](#moveint-dx-int-dy)      | org.viktor44.jtvision.core.JtvRect  |
| **public**    | [grow(int dx, int dy)](#growint-dx-int-dy)      | org.viktor44.jtvision.core.JtvRect  |
| **public**    | [intersect(JtvRect r)](#intersectjtvrect-r)     | org.viktor44.jtvision.core.JtvRect  |
| **public**    | [union(JtvRect r)](#unionjtvrect-r)             | org.viktor44.jtvision.core.JtvRect  |
| **public**    | [contains(JtvPoint p)](#containsjtvpoint-p)     | boolean                             |
| **public**    | [isEmpty()](#isempty)                           | boolean                             |
| **public**    | [assign(JtvRect other)](#assignjtvrect-other)   | void                                |
| **public**    | [toString()](#tostring)                         | String                              |
| **public**    | [getAx()](#getax)                               | int                                 |
| **public**    | [getAy()](#getay)                               | int                                 |
| **public**    | [getBx()](#getbx)                               | int                                 |
| **public**    | [getBy()](#getby)                               | int                                 |
| **public**    | [equals(Object o)](#equalsobject-o)             | boolean                             |
| **protected** | [canEqual(Object other)](#canequalobject-other) | boolean                             |
| **public**    | [hashCode()](#hashcode)                         | int                                 |
| **public**    | [getA()](#geta)                                 | org.viktor44.jtvision.core.JtvPoint |
| **public**    | [setA(JtvPoint a)](#setajtvpoint-a)             | void                                |
| **public**    | [getB()](#getb)                                 | org.viktor44.jtvision.core.JtvPoint |
| **public**    | [setB(JtvPoint b)](#setbjtvpoint-b)             | void                                |



## Constructors

### `JtvRect()`

Constructs a zero-sized rectangle at the origin.



### `JtvRect(int ax, int ay, int bx, int by)`

Constructs a rectangle from raw coordinates.

**Parameters**

- `ax` - upper-left X
- `ay` - upper-left Y
- `bx` - lower-right X
- `by` - lower-right Y



### `JtvRect(JtvPoint p1, JtvPoint p2)`

Constructs a rectangle from two points.

**Parameters**

- `p1` - upper-left corner
- `p2` - lower-right corner



### `JtvRect(JtvRect other)`

Copy constructor.

**Parameters**

- `other` - rectangle to copy



## Fields

### `org.viktor44.jtvision.core.JtvPoint a`

Upper-left corner of the rectangle.


### `org.viktor44.jtvision.core.JtvPoint b`

Lower-right corner of the rectangle (exclusive boundary).


## Methods

### `getCopy()`

Returns a deep copy of this rectangle.



### `move(int dx, int dy)`

Translates this rectangle by the given delta, modifying it in place.

**Parameters**

- `dx` - horizontal offset
- `dy` - vertical offset

**Returns**

`this`, for chaining



### `grow(int dx, int dy)`

Expands (or shrinks) this rectangle symmetrically by the given amounts.
Corner `a` is moved by `(-dx, -dy)` and `b` by `(+dx, +dy)`.

**Parameters**

- `dx` - horizontal expansion (negative to shrink)
- `dy` - vertical expansion (negative to shrink)

**Returns**

`this`, for chaining



### `intersect(JtvRect r)`

Clips this rectangle to the overlap with `r`, modifying it in place.
If the rectangles do not overlap the result will satisfy [isEmpty()](#isempty).

**Parameters**

- `r` - the clipping rectangle

**Returns**

`this`, for chaining



### `union(JtvRect r)`

Expands this rectangle to the smallest bounding box that contains both this rectangle
and `r`, modifying it in place.

**Parameters**

- `r` - the rectangle to include

**Returns**

`this`, for chaining



### `contains(JtvPoint p)`

Returns `true` if point `p` lies strictly inside this rectangle
(i.e. `a.x <= p.x < b.x` and `a.y <= p.y < b.y`).

**Parameters**

- `p` - the point to test

**Returns**

`true` if the point is contained



### `isEmpty()`

Returns `true` if the rectangle has zero or negative area
(`a.x >= b.x` or `a.y >= b.y`).

**Returns**

`true` if empty



### `assign(JtvRect other)`

Copies the corners of `other` into this rectangle.

**Parameters**

- `other` - source rectangle



### `toString()`

**Overrides:**

`toString()` in `java.lang.Object`

*No method description provided*



### `getAx()`

*No method description provided*



### `getAy()`

*No method description provided*



### `getBx()`

*No method description provided*



### `getBy()`

*No method description provided*



### `equals(Object o)`

*No method description provided*

**Parameters**

- `o`


### `canEqual(Object other)`

*No method description provided*

**Parameters**

- `other`


### `hashCode()`

**Overrides:**

`hashCode()` in `java.lang.Object`

*No method description provided*



### `getA()`

Upper-left corner of the rectangle.



### `setA(JtvPoint a)`

Upper-left corner of the rectangle.

**Parameters**

- `a`


### `getB()`

Lower-right corner of the rectangle (exclusive boundary).



### `setB(JtvPoint b)`

Lower-right corner of the rectangle (exclusive boundary).

**Parameters**

- `b`


