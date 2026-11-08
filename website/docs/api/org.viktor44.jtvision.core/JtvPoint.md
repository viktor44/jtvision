---
title: "JtvPoint"
sidebar_label: "JtvPoint"
---

Class JtvPoint
==============

```java
public class JtvPoint
```


An immutable screen-position object encapsulating integer (X, Y) coordinates.



The coordinate origin `(0, 0)` is the top-left corner of the screen.
`X` increases horizontally to the right; `Y` increases vertically downwards.
All JT Vision views are positioned and sized using `TPoint` and [JtvRect](JtvRect.md).

org.viktor44.jtvision.core.JtvPoint

## Summary

### Constructors

| Visibility | Signature                                      |
| ---------- | ---------------------------------------------- |
| public     | [JtvPoint()](#jtvpoint)                        |
| public     | [JtvPoint(int x, int y)](#jtvpointint-x-int-y) |

### Fields

| Modifiers         | Field name  | Type |
| ----------------- | ----------- | ---- |
| **private final** | [x](#int-x) | int  |
| **private final** | [y](#int-y) | int  |

### Methods

| Modifiers  | Method signature                                    | Return type                         |
| ---------- | --------------------------------------------------- | ----------------------------------- |
| **public** | [add(JtvPoint other)](#addjtvpoint-other)           | org.viktor44.jtvision.core.JtvPoint |
| **public** | [subtract(JtvPoint other)](#subtractjtvpoint-other) | org.viktor44.jtvision.core.JtvPoint |
| **public** | [equals(Object obj)](#equalsobject-obj)             | boolean                             |
| **public** | [hashCode()](#hashcode)                             | int                                 |
| **public** | [toString()](#tostring)                             | String                              |
| **public** | [getX()](#getx)                                     | int                                 |
| **public** | [getY()](#gety)                                     | int                                 |



## Constructors

### `JtvPoint()`

Constructs the origin point `(0, 0)`.



### `JtvPoint(int x, int y)`

Constructs a point at the given coordinates.

**Parameters**

- `x` - horizontal position
- `y` - vertical position



## Fields

### `int x`

Horizontal coordinate; 0 is the leftmost column.


### `int y`

Vertical coordinate; 0 is the topmost row.


## Methods

### `add(JtvPoint other)`

Returns a new point that is the component-wise sum of this point and `other`.

**Parameters**

- `other` - the point to add

**Returns**

new point `(x + other.x, y + other.y)`



### `subtract(JtvPoint other)`

Returns a new point that is the component-wise difference of this point and `other`.

**Parameters**

- `other` - the point to subtract

**Returns**

new point `(x - other.x, y - other.y)`



### `equals(Object obj)`

*No method description provided*

**Parameters**

- `obj`


### `hashCode()`

**Overrides:**

`hashCode()` in `java.lang.Object`

*No method description provided*



### `toString()`

**Overrides:**

`toString()` in `java.lang.Object`

*No method description provided*



### `getX()`

Horizontal coordinate; 0 is the leftmost column.



### `getY()`

Vertical coordinate; 0 is the topmost row.



