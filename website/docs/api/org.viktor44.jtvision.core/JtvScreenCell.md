---
title: "JtvScreenCell"
sidebar_label: "JtvScreenCell"
---

Class JtvScreenCell
===================

```java
public class JtvScreenCell
```


A single terminal screen cell holding a display character and a color attribute.



The color attribute follows the classic CGA/BIOS format:
the high nibble encodes the background color and the low nibble the foreground color.
Color constants are defined in [JtvColorAttr](JtvColorAttr.md).
The default attribute `0x07` is light-gray text on a black background.



`TScreenCell` arrays form the backing store used by [JtvDrawBuffer](JtvDrawBuffer.md).

org.viktor44.jtvision.core.JtvScreenCell

## Summary

### Constructors

| Visibility | Signature                                                                            |
| ---------- | ------------------------------------------------------------------------------------ |
| public     | [JtvScreenCell(char ch, JtvColorAttr attr)](#jtvscreencellchar-ch-jtvcolorattr-attr) |

### Fields

| Modifiers               | Field name                                                     | Type                                     |
| ----------------------- | -------------------------------------------------------------- | ---------------------------------------- |
| **public static final** | [EMPTY_CELL](#orgviktor44jtvisioncorejtvscreencell-empty_cell) | org.viktor44.jtvision.core.JtvScreenCell |
| **private final**       | [ch](#char-ch)                                                 | char                                     |
| **private final**       | [attr](#orgviktor44jtvisioncorejtvcolorattr-attr)              | org.viktor44.jtvision.core.JtvColorAttr  |

### Methods

| Modifiers  | Method signature      | Return type                             |
| ---------- | --------------------- | --------------------------------------- |
| **public** | [getCh()](#getch)     | char                                    |
| **public** | [getAttr()](#getattr) | org.viktor44.jtvision.core.JtvColorAttr |



## Constructors

### `JtvScreenCell(char ch, JtvColorAttr attr)`

Constructs a cell with the given character and color attribute.

**Parameters**

- `ch` - display character
- `attr` - color attribute



## Fields

### `org.viktor44.jtvision.core.JtvScreenCell EMPTY_CELL`

Default empty cell with a space character and the default attribute (light-gray on black).


### `char ch`

The character to display in this cell.


### `org.viktor44.jtvision.core.JtvColorAttr attr`

The color attribute: high nibble = background, low nibble = foreground.


## Methods

### `getCh()`

The character to display in this cell.



### `getAttr()`

The color attribute: high nibble = background, low nibble = foreground.



