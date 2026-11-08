---
title: "JtvLabel"
sidebar_label: "JtvLabel"
---

Class JtvLabel
==============

```java
public class JtvLabel
extends JtvView
```


A text label that acts as a proxy for another control.



JtvLabel serves two purposes: it displays an identifying text string next to
a linked control, and it allows the user to focus the linked control either
by clicking the label or by pressing its Alt+letter hot-key.



The label highlights (changes colour) when its linked control has focus,
by listening to `cmReceivedFocus` and `cmReleasedFocus` broadcasts.



Use [JtvStaticText](JtvStaticText.md) instead of TLabel when you only
need to display text without linking it to another control.

**Colour palette**

The four-entry palette `cpLabel` maps to:

1. Normal label text.
2. Highlighted label text (linked control has focus).
3. Shortcut key (normal state).
4. Shortcut key (highlighted state).



org.viktor44.jtvision.dialogs.JtvLabel

## Summary

### Constructors

| Visibility | Signature                                                                                                   |
| ---------- | ----------------------------------------------------------------------------------------------------------- |
| public     | [JtvLabel(JtvRect bounds, String aText, JtvView aLink)](#jtvlabeljtvrect-bounds-string-atext-jtvview-alink) |

### Fields

| Modifiers                | Field name                                            | Type                                  |
| ------------------------ | ----------------------------------------------------- | ------------------------------------- |
| **private**              | [text](#string-text)                                  | String                                |
| **private**              | [link](#orgviktor44jtvisionviewsjtvview-link)         | org.viktor44.jtvision.views.JtvView   |
| **private**              | [light](#boolean-light)                               | boolean                               |
| **private static final** | [cpLabel](#orgviktor44jtvisioncorejtvpalette-cplabel) | org.viktor44.jtvision.core.JtvPalette |

### Methods

| Modifiers  | Method signature                                          | Return type                           |
| ---------- | --------------------------------------------------------- | ------------------------------------- |
| **public** | [draw()](#draw)                                           | void                                  |
| **public** | [getPalette()](#getpalette)                               | org.viktor44.jtvision.core.JtvPalette |
| **public** | [handleEvent(JtvEvent event)](#handleeventjtvevent-event) | void                                  |



## Constructors

### `JtvLabel(JtvRect bounds, String aText, JtvView aLink)`

Creates a label with the given bounds, text, and linked control.

**Parameters**

- `bounds` - the bounding rectangle
- `aText` - the label text; enclose a character in tildes to define a hot-key
- `aLink` - the view to focus when the label is clicked or hot-key pressed



## Fields

### `String text`

The label text (may contain a tilde-delimited hot-key character).


### `org.viktor44.jtvision.views.JtvView link`

The view that receives focus when the label is clicked or its hot-key is pressed.


### `boolean light`

Whether the label is currently "lit" (linked control has focus).
`true` causes the label to render with the highlighted colour.


### `org.viktor44.jtvision.core.JtvPalette cpLabel`

Four-entry colour palette:

1. `7` — normal label text.
2. `8` — highlighted label text (linked control has focus).
3. `9` — shortcut key (normal state).
4. `9` — shortcut key (highlighted state).




## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the label text in either the normal or highlighted colour
depending on whether the linked control currently has focus.



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the default color palette `cpLabel`.

**Returns**

the label's color palette



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles events to proxy focus to the linked control.

- Mouse click — focuses the linked control.
- Alt+hot-key — focuses the linked control.
- `cmReceivedFocus` / `cmReleasedFocus` broadcast for the linked
      view — toggles the `light` flag and redraws.



**Parameters**

- `event` - the incoming event



