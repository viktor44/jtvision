---
title: "JtvStaticText"
sidebar_label: "JtvStaticText"
---

Class JtvStaticText
===================

```java
public class JtvStaticText
extends JtvView
```


A non-interactive view that displays a fixed text string.



JtvStaticText encapsulates a text string in a view, displaying it within its
bounding rectangle. It is not selectable, so the user never actively interacts
with it. Use this view for labels, instructions, or other fixed text elements
in dialog boxes.



The text may span multiple lines by embedding newline characters (`\n`).
A line that begins with ASCII character 3 (Ctrl+C, `\x03`) is centred
horizontally within the view.



The text is retrieved via the method [getText()](#gettext)

**Colour palette**

The single-entry palette `cpStaticText` maps to:

1. Static text colour.



org.viktor44.jtvision.dialogs.JtvStaticText

## Summary

### Constructors

| Visibility | Signature                                                                                |
| ---------- | ---------------------------------------------------------------------------------------- |
| public     | [JtvStaticText(JtvRect bounds, String aText)](#jtvstatictextjtvrect-bounds-string-atext) |

### Fields

| Modifiers                | Field name                                                      | Type                                  |
| ------------------------ | --------------------------------------------------------------- | ------------------------------------- |
| **private static final** | [cpStaticText](#orgviktor44jtvisioncorejtvpalette-cpstatictext) | org.viktor44.jtvision.core.JtvPalette |
| **private**              | [text](#string-text)                                            | String                                |

### Methods

| Modifiers  | Method signature                            | Return type                           |
| ---------- | ------------------------------------------- | ------------------------------------- |
| **public** | [draw()](#draw)                             | void                                  |
| **public** | [getPalette()](#getpalette)                 | org.viktor44.jtvision.core.JtvPalette |
| **public** | [getText()](#gettext)                       | String                                |
| **public** | [setText(String text)](#settextstring-text) | void                                  |



## Constructors

### `JtvStaticText(JtvRect bounds, String aText)`

Creates a static text view with the given bounds and text.



The view is fixed (does not grow) and non-selectable.

**Parameters**

- `bounds` - the bounding rectangle; must be large enough to display the entire text
- `aText` - the text to display; use `\n` for line breaks and
                ASCII 3 at the start of a line to centre it



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpStaticText`

Single-entry colour palette:

1. `6` — static text colour.




### `String text`

The text string to display. Lines ending with `\n` are wrapped.


## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the text content returned by [getText()](#gettext) line by line.
Lines are word-wrapped to the view width. Lines beginning with ASCII 3 are centred.



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the default color palette `cpStaticText`.

**Returns**

the static text's color palette



### `getText()`

Returns the text string to be displayed.
Subclasses can override this to supply dynamic or formatted text.

**Returns**

the display text; never `null`



### `setText(String text)`

*No method description provided*

**Parameters**

- `text`


