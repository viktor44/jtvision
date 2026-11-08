---
title: "JtvPalette"
sidebar_label: "JtvPalette"
---

Class JtvPalette
================

```java
public class JtvPalette
```


An array of color-attribute indices used by the JT Vision palette system.



Each view returns a palette from its `getPalette()` method.  The palette maps
logical color indices (1-based) to physical color attributes.  These attributes are
looked up through the palette chain from the view up to the application, ultimately
producing the 8-bit CGA color byte that is stored in each [JtvScreenCell](JtvScreenCell.md).



Storage format: `data` holds the colour entries directly, with no length
prefix.  Consumers use [get(int)](#getint-index) with a 1-based index to retrieve entries.

org.viktor44.jtvision.core.JtvPalette

## Summary

### Constructors

| Visibility | Signature                                                   |
| ---------- | ----------------------------------------------------------- |
| public     | [JtvPalette(int[] source)](#jtvpaletteint-source)           |
| public     | [JtvPalette(JtvPalette other)](#jtvpalettejtvpalette-other) |

### Fields

| Modifiers   | Field name                                        | Type                                      |
| ----------- | ------------------------------------------------- | ----------------------------------------- |
| **private** | [data](#orgviktor44jtvisioncorejtvcolorattr-data) | org.viktor44.jtvision.core.JtvColorAttr[] |

### Methods

| Modifiers  | Method signature                                                       | Return type                               |
| ---------- | ---------------------------------------------------------------------- | ----------------------------------------- |
| **public** | [get(int index)](#getint-index)                                        | org.viktor44.jtvision.core.JtvColorAttr   |
| **public** | [set(int index, JtvColorAttr value)](#setint-index-jtvcolorattr-value) | void                                      |
| **public** | [length()](#length)                                                    | int                                       |
| **public** | [getData()](#getdata)                                                  | org.viktor44.jtvision.core.JtvColorAttr[] |



## Constructors

### `JtvPalette(int[] source)`

Constructs a palette from a raw array of color attribute values.
The resulting palette length is `source.length`.

**Parameters**

- `source` - array of color attribute bytes (1-based palette entries)



### `JtvPalette(JtvPalette other)`

Copy constructor.

**Parameters**

- `other` - the palette to copy



## Fields

### `org.viktor44.jtvision.core.JtvColorAttr[] data`

Internal storage array holding the colour entries.


## Methods

### `get(int index)`

Returns the palette entry at the given 1-based index.

**Parameters**

- `index` - 1-based palette entry index (`1..length()`)

**Returns**

the value at `index`, or a zero-value entry if out of bounds



### `set(int index, JtvColorAttr value)`

Sets the palette entry at the given 1-based index.

**Parameters**

- `index` - 1-based palette entry index
- `value` - new value



### `length()`

Returns the number of color entries in this palette.

**Returns**

palette entry count



### `getData()`

Internal storage array holding the colour entries.



