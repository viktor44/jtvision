---
title: "JtvColorAttr"
sidebar_label: "JtvColorAttr"
---

Class JtvColorAttr
==================

```java
public class JtvColorAttr
```


Encodes a classic CGA/BIOS 8-bit terminal color attribute.



The byte is split into two nibbles:

- Bits 3–0 (low nibble): foreground color index (0–15)
- Bits 7–4 (high nibble): background color index (0–7; bit 7 may enable blinking)

The 16 standard color constants defined here correspond to the IBM PC BIOS color palette.
Use [JtvColorAttr(int, int)](#jtvcolorattrint-fg-int-bg) to build an attribute from separate foreground and
background values, or pass the raw packed byte directly to [JtvColorAttr(int)](#jtvcolorattrint-value).

org.viktor44.jtvision.core.JtvColorAttr

## Summary

### Constructors

| Visibility | Signature                                                  |
| ---------- | ---------------------------------------------------------- |
| public     | [JtvColorAttr()](#jtvcolorattr)                            |
| public     | [JtvColorAttr(int value)](#jtvcolorattrint-value)          |
| public     | [JtvColorAttr(int fg, int bg)](#jtvcolorattrint-fg-int-bg) |

### Fields

| Modifiers               | Field name                                                                      | Type                                    |
| ----------------------- | ------------------------------------------------------------------------------- | --------------------------------------- |
| **public static final** | [BLACK](#int-black)                                                             | int                                     |
| **public static final** | [BLUE](#int-blue)                                                               | int                                     |
| **public static final** | [GREEN](#int-green)                                                             | int                                     |
| **public static final** | [CYAN](#int-cyan)                                                               | int                                     |
| **public static final** | [RED](#int-red)                                                                 | int                                     |
| **public static final** | [MAGENTA](#int-magenta)                                                         | int                                     |
| **public static final** | [BROWN](#int-brown)                                                             | int                                     |
| **public static final** | [LIGHT_GRAY](#int-light_gray)                                                   | int                                     |
| **public static final** | [DARK_GRAY](#int-dark_gray)                                                     | int                                     |
| **public static final** | [LIGHT_BLUE](#int-light_blue)                                                   | int                                     |
| **public static final** | [LIGHT_GREEN](#int-light_green)                                                 | int                                     |
| **public static final** | [LIGHT_CYAN](#int-light_cyan)                                                   | int                                     |
| **public static final** | [LIGHT_RED](#int-light_red)                                                     | int                                     |
| **public static final** | [LIGHT_MAGENTA](#int-light_magenta)                                             | int                                     |
| **public static final** | [YELLOW](#int-yellow)                                                           | int                                     |
| **public static final** | [WHITE](#int-white)                                                             | int                                     |
| **public static final** | [LIGHT_GRAY_ON_BLACK](#orgviktor44jtvisioncorejtvcolorattr-light_gray_on_black) | org.viktor44.jtvision.core.JtvColorAttr |
| **public static final** | [WHITE_ON_WHITE](#orgviktor44jtvisioncorejtvcolorattr-white_on_white)           | org.viktor44.jtvision.core.JtvColorAttr |
| **public static final** | [WHITE_ON_LIGHT_RED](#orgviktor44jtvisioncorejtvcolorattr-white_on_light_red)   | org.viktor44.jtvision.core.JtvColorAttr |
| **private final**       | [value](#int-value)                                                             | int                                     |

### Methods

| Modifiers     | Method signature                                | Return type |
| ------------- | ----------------------------------------------- | ----------- |
| **public**    | [getForeground()](#getforeground)               | int         |
| **public**    | [getBackground()](#getbackground)               | int         |
| **public**    | [getValue()](#getvalue)                         | int         |
| **public**    | [equals(Object o)](#equalsobject-o)             | boolean     |
| **protected** | [canEqual(Object other)](#canequalobject-other) | boolean     |
| **public**    | [hashCode()](#hashcode)                         | int         |



## Constructors

### `JtvColorAttr()`

Constructs the default attribute: light-gray foreground on black background (`0x07`).



### `JtvColorAttr(int value)`

Constructs a color attribute from a packed 8-bit value.

**Parameters**

- `value` - raw attribute byte (high nibble = background, low nibble = foreground)



### `JtvColorAttr(int fg, int bg)`

Constructs a color attribute from separate foreground and background color indices.

**Parameters**

- `fg` - foreground color index (0\u201315, see color constants)
- `bg` - background color index (0\u20137)



## Fields

### `int BLACK`

Black (0x00).


### `int BLUE`

Blue (0x01).


### `int GREEN`

Green (0x02).


### `int CYAN`

Cyan (0x03).


### `int RED`

Red (0x04).


### `int MAGENTA`

Magenta (0x05).


### `int BROWN`

Brown / dark yellow (0x06).


### `int LIGHT_GRAY`

Light gray (0x07).


### `int DARK_GRAY`

Dark gray / bright black (0x08).


### `int LIGHT_BLUE`

Light blue (0x09).


### `int LIGHT_GREEN`

Light green (0x0A).


### `int LIGHT_CYAN`

Light cyan (0x0B).


### `int LIGHT_RED`

Light red / bright red (0x0C).


### `int LIGHT_MAGENTA`

Light magenta / bright magenta (0x0D).


### `int YELLOW`

Yellow / bright brown (0x0E).


### `int WHITE`

White / bright gray (0x0F).


### `org.viktor44.jtvision.core.JtvColorAttr LIGHT_GRAY_ON_BLACK`

*No description provided*


### `org.viktor44.jtvision.core.JtvColorAttr WHITE_ON_WHITE`

*No description provided*


### `org.viktor44.jtvision.core.JtvColorAttr WHITE_ON_LIGHT_RED`

*No description provided*


### `int value`

*No description provided*


## Methods

### `getForeground()`

Returns the foreground color index (bits 3–0 of the attribute byte).

**Returns**

foreground color, 0\u201315



### `getBackground()`

Returns the background color index (bits 7–4 of the attribute byte).

**Returns**

background color, 0\u20137 (bit 3 may be the blink flag)



### `getValue()`

Returns the raw 8-bit attribute value.

**Returns**

packed color byte



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



