---
title: "JtvMenuBox"
sidebar_label: "JtvMenuBox"
---

Class JtvMenuBox
================

```java
public class JtvMenuBox
extends JtvMenuView
```


A vertical drop-down or pop-up menu box.



JtvMenuBox renders a bordered box containing the items of a [JtvMenu](JtvMenu.md).
Each item is drawn on its own row between a top and bottom border drawn
with Unicode box-drawing characters. Separator items are drawn as
horizontal rules using the left-tee (`\u251c`), dash (`\u2500`), and
right-tee (`\u2524`) characters. An arrow (`\u25ba`) is drawn to the
right of any submenu item.

**Size adjustment**

[adjustBounds()](#adjustbounds) computes the required width (longest item label
plus optional `JtvMenuItem.param` text plus borders) and sets the
view size accordingly before the box is shown. This is called by
[JtvMenuView.execute()](JtvMenuView.md#execute) when a submenu is opened.

**Colour and state**

The box state includes `sfShadow` so a drop shadow is drawn beneath
it. `ofPreProcess` is added to options so hot keys operate before
the focused view sees the event. The box uses the inherited
`JtvMenuView.cpMenuView` palette.

org.viktor44.jtvision.menus.JtvMenuBox

## Summary

### Constructors

| Visibility | Signature                                                                                                                         |
| ---------- | --------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvMenuBox(JtvRect bounds, JtvMenu menu, JtvMenuView parentMenu)](#jtvmenuboxjtvrect-bounds-jtvmenu-menu-jtvmenuview-parentmenu) |

### Fields

| Modifiers                | Field name                  | Type  |
| ------------------------ | --------------------------- | ----- |
| **private static final** | [cpMenuBox](#int-cpmenubox) | int[] |

### Methods

| Modifiers  | Method signature                                              | Return type                        |
| ---------- | ------------------------------------------------------------- | ---------------------------------- |
| **public** | [adjustBounds()](#adjustbounds)                               | void                               |
| **public** | [draw()](#draw)                                               | void                               |
| **public** | [getItemRect(JtvMenuItem item)](#getitemrectjtvmenuitem-item) | org.viktor44.jtvision.core.JtvRect |



## Constructors

### `JtvMenuBox(JtvRect bounds, JtvMenu menu, JtvMenuView parentMenu)`

Constructs a menu box with the given bounds, menu, and parent.
Sets `sfShadow` in the state so a drop shadow is rendered, and
adds `ofPreProcess` to options for hot-key pre-processing.

**Parameters**

- `bounds` - the initial bounding rectangle (adjusted by
                      `#adjustBounds()` before display)
- `menu` - the `JtvMenu` whose items to display
- `parentMenu` - the owning `JtvMenuView` (e.g. the bar), or
                      `null` for a standalone pop-up



## Fields

### `int[] cpMenuBox`

Six-entry colour palette identical to `JtvMenuView.cpMenuView`,
stored here for completeness:

1. `2` — normal text.
2. `3` — disabled text.
3. `4` — shortcut letter.
4. `5` — selected shortcut.
5. `6` — selected normal.
6. `7` — selected disabled.




## Methods

### `adjustBounds()`

Computes and applies the minimum width and height needed to display
all items. The width is the maximum of `10` and the longest
item's display width, which is:
`cstrLen(name) + 6 + (param.length() + 2 if present)`. The
height is the item count plus 2 for the top and bottom borders.



Must be called before the box is inserted into an owner group.
[JtvMenuView.execute()](JtvMenuView.md#execute) calls this automatically.



### `draw()`

Draws the complete menu box: top border, item rows, bottom border.



**Top border:** `\u250c\u2500 \u2026 \u2500\u2510`.

**Item rows:**

- Separators are drawn as `\u251c\u2500 \u2026 \u2500\u2524`.
- Command and submenu items are framed by `\u2502` on each
      side. The label is rendered with hot-key highlighting via
      [JtvDrawBuffer.moveCStr](../org.viktor44.jtvision.core/JtvDrawBuffer.md#movecstrint-indent-string-str-jtvcolorattr-normalattr-jtvcolorattr-highlightattr). The `JtvMenuItem.param`
      string, if present, is right-aligned. Submenu items show a
      `\u25ba` glyph near the right edge.

**Bottom border:** `\u2514\u2500 \u2026 \u2500\u2518`.



Colours mirror [JtvMenuBar.draw()](JtvMenuBar.md#draw): normal, selected, disabled,
and selected-disabled variants.



### `getItemRect(JtvMenuItem item)`

**Overrides:**

`getItemRect(JtvMenuItem item)` in `org.viktor44.jtvision.menus.JtvMenuView`

Returns the bounding rectangle of `item` within the box.
Items are laid out one per row starting at row 1 (after the top
border), spanning columns 1 through `size.x - 1`.

**Parameters**

- `item` - the menu item to locate

**Returns**

the item's rectangle in view-local coordinates, or
        `(1,1)-(size.x-1,2)` as a fallback if not found



