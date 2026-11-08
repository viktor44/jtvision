---
title: "JtvBackground"
sidebar_label: "JtvBackground"
---

Class JtvBackground
===================

```java
public class JtvBackground
extends JtvView
```


The desktop background view, displayed behind all windows on the desktop.



`TBackground` fills its entire area with a single repeating `pattern`
character drawn in palette color 1.  By default the desktop uses
[JtvDesktop.defaultBkgrnd](JtvDesktop.md#char-defaultbkgrnd) (light-shade block `\u2591`, CP437 `0xB0`) as the
pattern, giving the classic JT Vision appearance.



To customise the desktop background, replace the factory passed to the
[JtvDesktop](JtvDesktop.md) constructor or override [JtvDesktop.initBackground](JtvDesktop.md#initbackgroundjtvrect-r).

**Colour palette**

The single-entry palette `cpBackground` maps to:

1. Background fill colour.



org.viktor44.jtvision.core.JtvBackground

## Summary

### Constructors

| Visibility | Signature                                                                                |
| ---------- | ---------------------------------------------------------------------------------------- |
| public     | [JtvBackground(JtvRect bounds, char pattern)](#jtvbackgroundjtvrect-bounds-char-pattern) |

### Fields

| Modifiers                | Field name                                                      | Type                                  |
| ------------------------ | --------------------------------------------------------------- | ------------------------------------- |
| **private static final** | [cpBackground](#orgviktor44jtvisioncorejtvpalette-cpbackground) | org.viktor44.jtvision.core.JtvPalette |
| **private**              | [pattern](#char-pattern)                                        | char                                  |

### Methods

| Modifiers  | Method signature                                    | Return type                           |
| ---------- | --------------------------------------------------- | ------------------------------------- |
| **public** | [draw()](#draw)                                     | void                                  |
| **public** | [getPalette()](#getpalette)                         | org.viktor44.jtvision.core.JtvPalette |
| **public** | [getPattern()](#getpattern)                         | char                                  |
| **public** | [setPattern(char pattern)](#setpatternchar-pattern) | void                                  |



## Constructors

### `JtvBackground(JtvRect bounds, char pattern)`

Constructs a background view covering `bounds` and filled with `aPattern`.

**Parameters**

- `bounds` - the area to cover (normally the desktop extent)
- `pattern` - the fill character



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpBackground`

Single-entry colour palette:

1. `1` — background fill colour.




### `char pattern`

The character tiled across the background area.


## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Redraws the entire background by tiling `pattern` across every row.



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the background's single-entry palette, mapping color index 1 upward through
the owner's palette chain.

**Returns**

the background palette



### `getPattern()`

The character tiled across the background area.



### `setPattern(char pattern)`

The character tiled across the background area.

**Parameters**

- `pattern`


