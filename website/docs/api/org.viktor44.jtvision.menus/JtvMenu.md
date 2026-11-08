---
title: "JtvMenu"
sidebar_label: "JtvMenu"
---

Class JtvMenu
=============

```java
public class JtvMenu
```


Represents one level of a menu tree.



JtvMenu holds the item list of [JtvMenuItem](JtvMenuItem.md) records that form a single
menu level — either the top-level menu bar or one drop-down submenu.
A [JtvMenuView](JtvMenuView.md) (and its concrete descendants [JtvMenuBar](JtvMenuBar.md) and
[JtvMenuBox](JtvMenuBox.md)) owns a TMenu and iterates over `items` to render
and process the menu.



`defaultItem` remembers the last item that was focused so that reopening
the menu restores the previous position. It is updated by
[JtvMenuView.execute()](JtvMenuView.md#execute) after the user dismisses the menu.



TMenu objects are typically constructed by helper code (e.g. application
`initMenuBar`) and passed to a [JtvMenuBar](JtvMenuBar.md) or
[JtvMenuBox](JtvMenuBox.md) constructor.

org.viktor44.jtvision.menus.JtvMenu

## Summary

### Constructors

| Visibility | Signature                                               |
| ---------- | ------------------------------------------------------- |
| public     | [JtvMenu()](#jtvmenu)                                   |
| public     | [JtvMenu(JtvMenuItem items)](#jtvmenujtvmenuitem-items) |

### Fields

| Modifiers   | Field name                                                      | Type                                          |
| ----------- | --------------------------------------------------------------- | --------------------------------------------- |
| **private** | [items](#listorgviktor44jtvisionmenusjtvmenuitem-items)         | List&lt;org.viktor44.jtvision.menus.JtvMenuItem> |
| **private** | [defaultItem](#orgviktor44jtvisionmenusjtvmenuitem-defaultitem) | org.viktor44.jtvision.menus.JtvMenuItem       |

### Methods

| Modifiers  | Method signature                                                                  | Return type                                   |
| ---------- | --------------------------------------------------------------------------------- | --------------------------------------------- |
| **public** | [addItem(JtvMenuItem item)](#additemjtvmenuitem-item)                             | org.viktor44.jtvision.menus.JtvMenu           |
| **public** | [addSeparator()](#addseparator)                                                   | org.viktor44.jtvision.menus.JtvMenu           |
| **public** | [getItems()](#getitems)                                                           | List&lt;org.viktor44.jtvision.menus.JtvMenuItem> |
| **public** | [getDefaultItem()](#getdefaultitem)                                               | org.viktor44.jtvision.menus.JtvMenuItem       |
| **public** | [setDefaultItem(JtvMenuItem defaultItem)](#setdefaultitemjtvmenuitem-defaultitem) | void                                          |



## Constructors

### `JtvMenu()`

Constructs an empty menu with no items and no default.



### `JtvMenu(JtvMenuItem items)`

Constructs a menu and adds `aItems` (and any chained items).
The default item is set to the first inserted item.

**Parameters**

- `items` - the first `JtvMenuItem` to add



## Fields

### `List<org.viktor44.jtvision.menus.JtvMenuItem> items`

The ordered list of items in this menu.


### `org.viktor44.jtvision.menus.JtvMenuItem defaultItem`

Pointer to the default (last focused) item in the list. Used to
restore the highlighted position when the menu is re-opened.
Initially set to `items` and updated by [JtvMenuView.execute()](JtvMenuView.md#execute) after each interaction.


## Methods

### `addItem(JtvMenuItem item)`

*No method description provided*

**Parameters**

- `item`


### `addSeparator()`

*No method description provided*



### `getItems()`

The ordered list of items in this menu.



### `getDefaultItem()`

Pointer to the default (last focused) item in the list. Used to
restore the highlighted position when the menu is re-opened.
Initially set to `items` and updated by [JtvMenuView.execute()](JtvMenuView.md#execute) after each interaction.



### `setDefaultItem(JtvMenuItem defaultItem)`

Pointer to the default (last focused) item in the list. Used to
restore the highlighted position when the menu is re-opened.
Initially set to `items` and updated by [JtvMenuView.execute()](JtvMenuView.md#execute) after each interaction.

**Parameters**

- `defaultItem`


