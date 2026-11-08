---
title: "JtvSubMenu"
sidebar_label: "JtvSubMenu"
---

Class JtvSubMenu
================

```java
public class JtvSubMenu
extends JtvMenuItem
```


A [JtvMenuItem](JtvMenuItem.md) that represents a submenu entry in a menu bar or
another menu box.



JtvSubMenu is a convenience subclass that automatically creates an empty
[JtvMenu](JtvMenu.md) as its `JtvMenuItem.subMenu` and provides a builder API
for populating it. This makes it easy to construct nested menu structures
in a readable, chained style:


```
JtvSubMenu file = new JtvSubMenu("~F~ile", JtvKeyStroke.altKey(KeyEvent.VK_F))
    .addItem(new JtvMenuItem("~O~pen", cmFileOpen, JtvKeyStroke.of(KeyEvent.VK_F3)))
    .addItem("~S~ave", cmFileSave)
    .addSeparator()
    .addItem(new JtvMenuItem("E~x~it", cmQuit, JtvKeyStroke.altKey(KeyEvent.VK_X)));
```



org.viktor44.jtvision.menus.JtvSubMenu

## Summary

### Constructors

| Visibility | Signature                                                                                                                 |
| ---------- | ------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvSubMenu(String name)](#jtvsubmenustring-name)                                                                         |
| public     | [JtvSubMenu(String name, JtvKeyStroke keyStroke)](#jtvsubmenustring-name-jtvkeystroke-keystroke)                          |
| public     | [JtvSubMenu(String name, JtvKeyStroke keyStroke, int helpCtx)](#jtvsubmenustring-name-jtvkeystroke-keystroke-int-helpctx) |

### Methods

| Modifiers  | Method signature                                                     | Return type                            |
| ---------- | -------------------------------------------------------------------- | -------------------------------------- |
| **public** | [addItem(JtvMenuItem item)](#additemjtvmenuitem-item)                | org.viktor44.jtvision.menus.JtvSubMenu |
| **public** | [addItem(String name, int command)](#additemstring-name-int-command) | org.viktor44.jtvision.menus.JtvSubMenu |
| **public** | [addSeparator()](#addseparator)                                      | org.viktor44.jtvision.menus.JtvSubMenu |



## Constructors

### `JtvSubMenu(String name)`

Creates a submenu item with no help context.

**Parameters**

- `name` - the display label for this top-level menu entry
               (may contain `~` hot-key markers, e.g. `"~F~ile"`)



### `JtvSubMenu(String name, JtvKeyStroke keyStroke)`

Creates a submenu item with no help context.

**Parameters**

- `name` - the display label (may contain `~` hot-key markers)
- `keyStroke` - the Alt+letter keystroke that opens this submenu, or `null`



### `JtvSubMenu(String name, JtvKeyStroke keyStroke, int helpCtx)`

Creates a submenu item with a help context. An empty [JtvMenu](JtvMenu.md) is
created and assigned to `JtvMenuItem.subMenu`; items are added
via [addItem(JtvMenuItem)](#additemjtvmenuitem-item).

**Parameters**

- `name` - the display label
- `keyStroke` - the Alt+letter keystroke, or `null`
- `helpCtx` - the help context number



## Methods

### `addItem(JtvMenuItem item)`

Appends `item` to this submenu's item list and returns
`this` for builder-style chaining.



If the submenu is currently empty, `item` becomes the first
(and default) entry. Otherwise it is appended at the end of the
existing list.

**Parameters**

- `item` - the `JtvMenuItem` to add; must not already be in a list

**Returns**

`this`



### `addItem(String name, int command)`

Appends a new [JtvMenuItem](JtvMenuItem.md) with the given name and command
to this submenu's item list and returns `this` for chaining.



This is a convenience shorthand for
`addItem(new JtvMenuItem(name, command))` for menu items
that do not require a keystroke shortcut or help context.

**Parameters**

- `name` - the display label for the menu item
                 (may contain `~` hot-key markers, e.g. `"~O~pen"`)
- `command` - the command constant dispatched when the item is selected

**Returns**

`this`



### `addSeparator()`

*No method description provided*



