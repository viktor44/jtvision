---
title: "JtvMenuBar"
sidebar_label: "JtvMenuBar"
---

Class JtvMenuBar
================

```java
public class JtvMenuBar
extends JtvMenuView
```


The horizontal menu bar displayed at the top of the application screen.



JtvMenuBar renders its top-level [JtvMenuItem](JtvMenuItem.md) list in a single
horizontal row, with each item preceded and followed by a padding space.
The currently highlighted item ([JtvMenuView.current](JtvMenuView.md#orgviktor44jtvisionmenusjtvmenuitem-current)) is drawn in
the selected colour; disabled items use their own colour pair.

**Activation**

The menu bar can be activated in three ways:

- **Mouse click** on any bar item triggers [doASelect](JtvMenuView.md#doaselectjtvevent-event).
- **F10** sets [JtvMenuView.current](JtvMenuView.md#orgviktor44jtvisionmenusjtvmenuitem-current) to the first item and
      posts a `cmMenu` command to start the modal loop.
- **Alt+letter** matching a top-level item's hot key activates
      that item directly.

When `cmMenu` is received as a broadcast or command, the bar
activates and executes its modal loop, opening a child [JtvMenuBox](JtvMenuBox.md)
for submenus.

**Grow mode**

`growMode` is set to `gfGrowHiX` so the bar stretches
horizontally when the application window is resized.



`ofPreProcess` is added to `options` so hot keys (Alt+letter,
F10) are processed before focus views see the event.



The bar uses the inherited `JtvMenuView.cpMenuView` palette.

org.viktor44.jtvision.menus.JtvMenuBar

## Summary

### Constructors

| Visibility | Signature                                                                          |
| ---------- | ---------------------------------------------------------------------------------- |
| public     | [JtvMenuBar(JtvRect bounds)](#jtvmenubarjtvrect-bounds)                            |
| public     | [JtvMenuBar(JtvRect bounds, JtvMenu menu)](#jtvmenubarjtvrect-bounds-jtvmenu-menu) |

### Methods

| Modifiers   | Method signature                                              | Return type                            |
| ----------- | ------------------------------------------------------------- | -------------------------------------- |
| **public**  | [draw()](#draw)                                               | void                                   |
| **public**  | [getItemRect(JtvMenuItem item)](#getitemrectjtvmenuitem-item) | org.viktor44.jtvision.core.JtvRect     |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)     | void                                   |
| **private** | [activateMenu(JtvEvent event)](#activatemenujtvevent-event)   | void                                   |
| **public**  | [addItem(JtvMenuItem item)](#additemjtvmenuitem-item)         | org.viktor44.jtvision.menus.JtvMenuBar |
| **public**  | [addSeparator()](#addseparator)                               | org.viktor44.jtvision.menus.JtvMenuBar |



## Constructors

### `JtvMenuBar(JtvRect bounds)`

Constructs a menu bar with the given bounds and menu.

**Parameters**

- `bounds` - the bounding rectangle (typically the full top row)



### `JtvMenuBar(JtvRect bounds, JtvMenu menu)`

Constructs a menu bar with the given bounds and menu.
Sets `growMode` to `gfGrowHiX` and adds
`ofPreProcess` to options.

**Parameters**

- `bounds` - the bounding rectangle (typically the full top row)
- `menu` - the `JtvMenu` whose items to display



## Methods

### `draw()`

Draws the menu bar in a single row.



Iterates the item list, writing each item's label with surrounding
spaces and the appropriate colour:

- Active and current — selected normal (`cSelect`).
- Disabled and current — selected disabled (`cSelDisabled`).
- Disabled, not current — normal disabled (`cNormDisabled`).
- Normal — `cNormal`.

Separator items (null name) are skipped.



### `getItemRect(JtvMenuItem item)`

**Overrides:**

`getItemRect(JtvMenuItem item)` in `org.viktor44.jtvision.menus.JtvMenuView`

Returns the bounding rectangle of `item` within the bar row.
Items are laid out sequentially from column 1; each item occupies
`name_length + 2` columns (one padding space on each side).

**Parameters**

- `item` - the menu item to locate

**Returns**

the item's rectangle in view-local coordinates, or a
        one-column rect at the current end position if not found



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.menus.JtvMenuView`

Handles menu bar activation events.



In addition to the inherited [JtvMenuView.handleEvent](JtvMenuView.md#handleeventjtvevent-event) processing:

- **Alt+letter** — if the letter matches a top-level item's
      hot key, sets that item as [JtvMenuView.current](JtvMenuView.md#orgviktor44jtvisionmenusjtvmenuitem-current), converts
      the event to `evCommand cmMenu`, and activates the bar.
- **F10** — focuses the first item and activates the bar.
- **evCommand cmMenu** — activates the bar if items are
      available, using the previously remembered current item.



**Parameters**

- `event` - the incoming event



### `activateMenu(JtvEvent event)`

Posts the activating event and executes the bar as a modal view via
the owner group. If a non-zero command is returned and is enabled,
posts an `evCommand` event. Clears the activating event.

**Parameters**

- `event` - the event that triggered activation



### `addItem(JtvMenuItem item)`

*No method description provided*

**Parameters**

- `item`


### `addSeparator()`

*No method description provided*



