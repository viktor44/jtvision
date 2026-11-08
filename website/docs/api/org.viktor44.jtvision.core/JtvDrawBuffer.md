---
title: "JtvDrawBuffer"
sidebar_label: "JtvDrawBuffer"
---

Class JtvDrawBuffer
===================

```java
public class JtvDrawBuffer
```


A line-buffer of [JtvScreenCell](JtvScreenCell.md) objects used by view `draw()` methods.



Data and color attributes are assembled line-by-line in a `TDrawBuffer`,
then written to the screen via `TView.writeLine()` or `TView.writeBuf()`.
Typical draw pattern:


```
b = new TDrawBuffer();
b.moveChar(0, ' ', getColor(1), size.getX()); // fill with spaces
b.moveStr(0, label, getColor(2));              // overlay text
writeLine(0, row, size.getX(), 1, b);
```





The Pascal equivalent was a fixed `array[0..MaxViewWidth-1] of Word` where
each word packed a character (low byte) and attribute (high byte).  This Java version
uses an array of [JtvScreenCell](JtvScreenCell.md) objects instead.

org.viktor44.jtvision.core.JtvDrawBuffer

## Summary

### Constructors

| Visibility | Signature                                                                                                                 |
| ---------- | ------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvDrawBuffer()](#jtvdrawbuffer)                                                                                         |
| public     | [JtvDrawBuffer(int size)](#jtvdrawbufferint-size)                                                                         |
| public     | [JtvDrawBuffer(org.viktor44.jtvision.core.JtvScreenCell[] data)](#jtvdrawbufferorgviktor44jtvisioncorejtvscreencell-data) |

### Fields

| Modifiers   | Field name                                         | Type                                       |
| ----------- | -------------------------------------------------- | ------------------------------------------ |
| **private** | [data](#orgviktor44jtvisioncorejtvscreencell-data) | org.viktor44.jtvision.core.JtvScreenCell[] |

### Methods

| Modifiers  | Method signature                                                                                                                                                                                                                   | Return type                                |
| ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------ |
| **public** | [moveChar(int indent, char c, JtvColorAttr attr, int count)](#movecharint-indent-char-c-jtvcolorattr-attr-int-count)                                                                                                               | void                                       |
| **public** | [moveStr(int indent, String str, JtvColorAttr attr)](#movestrint-indent-string-str-jtvcolorattr-attr)                                                                                                                              | int                                        |
| **public** | [moveStr(int indent, String str, JtvColorAttr attr, int maxWidth, int strIndent)](#movestrint-indent-string-str-jtvcolorattr-attr-int-maxwidth-int-strindent)                                                                      | int                                        |
| **public** | [moveCStr(int indent, String str, JtvColorAttr normalAttr, JtvColorAttr highlightAttr)](#movecstrint-indent-string-str-jtvcolorattr-normalattr-jtvcolorattr-highlightattr)                                                         | int                                        |
| **public** | [moveCStr(int indent, String str, JtvColorAttr normalAttr, JtvColorAttr highlightAttr, int maxWidth, int strIndent)](#movecstrint-indent-string-str-jtvcolorattr-normalattr-jtvcolorattr-highlightattr-int-maxwidth-int-strindent) | int                                        |
| **public** | [moveBuf(int indent, org.viktor44.jtvision.core.JtvScreenCell[] source, JtvColorAttr attr, int count)](#movebufint-indent-orgviktor44jtvisioncorejtvscreencell-source-jtvcolorattr-attr-int-count)                                 | void                                       |
| **public** | [putAttribute(int indent, JtvColorAttr attr)](#putattributeint-indent-jtvcolorattr-attr)                                                                                                                                           | void                                       |
| **public** | [putChar(int indent, char c)](#putcharint-indent-char-c)                                                                                                                                                                           | void                                       |
| **public** | [getData()](#getdata)                                                                                                                                                                                                              | org.viktor44.jtvision.core.JtvScreenCell[] |



## Constructors

### `JtvDrawBuffer()`

Constructs a buffer with the default capacity of 256 cells.



### `JtvDrawBuffer(int size)`

Constructs a buffer with the given capacity.

**Parameters**

- `size` - number of cells to allocate



### `JtvDrawBuffer(org.viktor44.jtvision.core.JtvScreenCell[] data)`

Wraps an existing [JtvScreenCell](JtvScreenCell.md) array.  The array is adopted by reference — no
copy is made — so mutations to the buffer are visible through the original array and
vice versa.  Used by the root group to share its buffer with the platform screen buffer.

**Parameters**

- `data` - backing cell array



## Fields

### `org.viktor44.jtvision.core.JtvScreenCell[] data`

The underlying array of screen cells.


## Methods

### `moveChar(int indent, char c, JtvColorAttr attr, int count)`

Fills `count` cells starting at `indent` with copies of character `c`
using the given color attribute.



If `c` is `0` the character of each cell is left unchanged.
If `attr` is `null` the attribute of each cell is left unchanged.

**Parameters**

- `indent` - starting cell index
- `c` - character to write, or `0` to leave characters unchanged
- `attr` - color attribute to write, or `null` to leave attributes unchanged
- `count` - number of cells to fill



### `moveStr(int indent, String str, JtvColorAttr attr)`

Copies the characters of `str` into the buffer at `indent` using
the given color attribute.  If `attr` is `null` the existing cell
attributes are left unchanged.

**Parameters**

- `indent` - starting cell index
- `str` - text to copy
- `attr` - color attribute, or `null` to leave attributes unchanged

**Returns**

number of display columns written



### `moveStr(int indent, String str, JtvColorAttr attr, int maxWidth, int strIndent)`

Copies at most `maxWidth` columns of `str` into the buffer at `indent`,
optionally skipping the first `strIndent` display columns of the string.

**Parameters**

- `indent` - starting cell index in the buffer
- `str` - text to copy
- `attr` - color attribute, or `null` to leave attributes unchanged
- `maxWidth` - maximum number of columns to write
- `strIndent` - number of leading display columns in `str` to skip

**Returns**

number of display columns written



### `moveCStr(int indent, String str, JtvColorAttr normalAttr, JtvColorAttr highlightAttr)`

Copies a hotkey-marked string (a *CStr*) into the buffer at `indent`.



Tilde characters (`~`) in the string toggle between `normalAttr` and
`highlightAttr`.  The tilde characters themselves are not written.

**Parameters**

- `indent` - starting cell index
- `str` - CStr text with `~` delimiters around hotkey characters
- `normalAttr` - color attribute for normal characters
- `highlightAttr` - color attribute for hotkey characters

**Returns**

number of display columns written



### `moveCStr(int indent, String str, JtvColorAttr normalAttr, JtvColorAttr highlightAttr, int maxWidth, int strIndent)`

Copies at most `maxWidth` columns of a hotkey-marked string into the buffer,
optionally skipping the first `strIndent` display columns.

**Parameters**

- `indent` - starting cell index in the buffer
- `str` - CStr text with `~` delimiters
- `normalAttr` - color attribute for normal characters
- `highlightAttr` - color attribute for hotkey characters
- `maxWidth` - maximum number of columns to write
- `strIndent` - number of leading display columns in `str` to skip

**Returns**

number of display columns written



### `moveBuf(int indent, org.viktor44.jtvision.core.JtvScreenCell[] source, JtvColorAttr attr, int count)`

Copies `count` cells from the `source` array into this buffer at `indent`.



If `attr` is non-null it overrides the source attribute for every copied cell;
otherwise each source cell's original attribute is preserved.

**Parameters**

- `indent` - starting cell index in this buffer
- `source` - array of source cells
- `attr` - attribute override, or `null` to use source attributes
- `count` - number of cells to copy



### `putAttribute(int indent, JtvColorAttr attr)`

Sets the color attribute of the single cell at `indent`, leaving its character unchanged.

**Parameters**

- `indent` - cell index
- `attr` - new color attribute



### `putChar(int indent, char c)`

Sets the character of the single cell at `indent`, leaving its attribute unchanged.

**Parameters**

- `indent` - cell index
- `c` - new display character



### `getData()`

The underlying array of screen cells.



