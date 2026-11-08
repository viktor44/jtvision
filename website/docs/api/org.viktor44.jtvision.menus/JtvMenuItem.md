---
title: "JtvMenuItem"
sidebar_label: "JtvMenuItem"
---

Class JtvMenuItem
=================

```java
public class JtvMenuItem
```


A single menu item record.



JtvMenuItem represents one entry in a [JtvMenu](JtvMenu.md): a selectable action,
a submenu trigger, or a visual separator (divider line).

**Item kinds**

- **Command item** — `command` is non-zero, `subMenu`
      is `null`. Selecting the item posts a `cmXXXX` command
      event. An optional `keyCode` hotkey and `param` label
      (e.g. `"Ctrl+X"`) may be supplied.
- **Submenu item** — `command` is zero, `subMenu`
      points to a nested [JtvMenu](JtvMenu.md). Selecting opens a child
      [JtvMenuBox](JtvMenuBox.md).
- **Separator** — `name` is `null`. Rendered as a
      horizontal divider line inside a [JtvMenuBox](JtvMenuBox.md).

**Hot keys**

`keyCode` holds the scan code of a keyboard shortcut that triggers
the item directly (typically a function key or Alt+letter combination).
[JtvMenuView](JtvMenuView.md) scans the key code during `evKeyDown` events.

**Enabled state**

`disabled` is initialised from the current command set via
[JtvView.commandEnabled(int)](../org.viktor44.jtvision.views/JtvView.md#commandenabledint-command), and is updated whenever a
`cmCommandSetChanged` broadcast is received.

org.viktor44.jtvision.menus.JtvMenuItem

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                       |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvMenuItem(String name, int command)](#jtvmenuitemstring-name-int-command)                                                                                                    |
| public     | [JtvMenuItem(String name, int command, JtvKeyStroke keyStroke)](#jtvmenuitemstring-name-int-command-jtvkeystroke-keystroke)                                                     |
| public     | [JtvMenuItem(String name, JtvKeyStroke keyStroke, JtvMenu subMenu)](#jtvmenuitemstring-name-jtvkeystroke-keystroke-jtvmenu-submenu)                                             |
| public     | [JtvMenuItem(String name, int command, JtvKeyStroke keyStroke, int helpCtx, String param)](#jtvmenuitemstring-name-int-command-jtvkeystroke-keystroke-int-helpctx-string-param) |
| public     | [JtvMenuItem(String name, JtvKeyStroke keyStroke, JtvMenu subMenu, int helpCtx)](#jtvmenuitemstring-name-jtvkeystroke-keystroke-jtvmenu-submenu-int-helpctx)                    |

### Fields

| Modifiers   | Field name                                                  | Type                                    |
| ----------- | ----------------------------------------------------------- | --------------------------------------- |
| **private** | [name](#string-name)                                        | String                                  |
| **private** | [command](#int-command)                                     | int                                     |
| **private** | [disabled](#boolean-disabled)                               | boolean                                 |
| **private** | [keyStroke](#orgviktor44jtvisioncorejtvkeystroke-keystroke) | org.viktor44.jtvision.core.JtvKeyStroke |
| **private** | [helpCtx](#int-helpctx)                                     | int                                     |
| **private** | [param](#string-param)                                      | String                                  |
| **private** | [subMenu](#orgviktor44jtvisionmenusjtvmenu-submenu)         | org.viktor44.jtvision.menus.JtvMenu     |

### Methods

| Modifiers  | Method signature                                                            | Return type                             |
| ---------- | --------------------------------------------------------------------------- | --------------------------------------- |
| **public** | [getName()](#getname)                                                       | String                                  |
| **public** | [setName(String name)](#setnamestring-name)                                 | void                                    |
| **public** | [getCommand()](#getcommand)                                                 | int                                     |
| **public** | [setCommand(int command)](#setcommandint-command)                           | void                                    |
| **public** | [isDisabled()](#isdisabled)                                                 | boolean                                 |
| **public** | [setDisabled(boolean disabled)](#setdisabledboolean-disabled)               | void                                    |
| **public** | [getKeyStroke()](#getkeystroke)                                             | org.viktor44.jtvision.core.JtvKeyStroke |
| **public** | [setKeyStroke(JtvKeyStroke keyStroke)](#setkeystrokejtvkeystroke-keystroke) | void                                    |
| **public** | [getHelpCtx()](#gethelpctx)                                                 | int                                     |
| **public** | [setHelpCtx(int helpCtx)](#sethelpctxint-helpctx)                           | void                                    |
| **public** | [getParam()](#getparam)                                                     | String                                  |
| **public** | [setParam(String param)](#setparamstring-param)                             | void                                    |
| **public** | [getSubMenu()](#getsubmenu)                                                 | org.viktor44.jtvision.menus.JtvMenu     |
| **public** | [setSubMenu(JtvMenu subMenu)](#setsubmenujtvmenu-submenu)                   | void                                    |



## Constructors

### `JtvMenuItem(String name, int command)`

Creates a command menu item.

**Parameters**

- `name` - the display label (may contain `~` hot-key markers)
- `command` - the `cmXXXX` command to post on selection



### `JtvMenuItem(String name, int command, JtvKeyStroke keyStroke)`

Creates a command menu item with no help context and no param string.

**Parameters**

- `name` - the display label (may contain `~` hot-key markers)
- `command` - the `cmXXXX` command to post on selection
- `keyStroke` - the shortcut keystroke, or `null`



### `JtvMenuItem(String name, JtvKeyStroke keyStroke, JtvMenu subMenu)`

Creates a submenu item with no help context.

**Parameters**

- `name` - the display label
- `keyStroke` - the hot-key keystroke (Alt+letter), or `null`
- `subMenu` - the nested `JtvMenu` to open on selection



### `JtvMenuItem(String name, int command, JtvKeyStroke keyStroke, int helpCtx, String param)`

Creates a command menu item with all fields specified.
`disabled` is set from the current command set.

**Parameters**

- `name` - the display label
- `command` - the `cmXXXX` command to post on selection
- `keyStroke` - the shortcut keystroke, or `null`
- `helpCtx` - the help context number
- `param` - the shortcut display string, or `null`



### `JtvMenuItem(String name, JtvKeyStroke keyStroke, JtvMenu subMenu, int helpCtx)`

Creates a submenu item. `command` is set to `0` and
`disabled` is always `false` — submenu items are never
disabled through the command set.

**Parameters**

- `name` - the display label
- `keyStroke` - the hot-key keystroke (Alt+letter), or `null`
- `subMenu` - the nested `JtvMenu`
- `helpCtx` - the help context number



## Fields

### `String name`

The display label for this menu item, with an optional tilde
(`~`) surrounding the hot-key letter, e.g. `"~F~ile"`.
`null` for separator lines.


### `int command`

The `cmXXXX` command constant posted when the item is selected,
or `0` for submenu items.


### `boolean disabled`

`true` if this item is currently disabled and cannot be selected.
Reflects [JtvView.commandEnabled(int)](../org.viktor44.jtvision.views/JtvView.md#commandenabledint-command) at construction time and is
refreshed on `cmCommandSetChanged` broadcasts.


### `org.viktor44.jtvision.core.JtvKeyStroke keyStroke`

The keystroke shortcut that triggers this item directly, or `null`
if there is no shortcut.


### `int helpCtx`

The help context number for this item.
[org.viktor44.jtvision.core.ViewFlags.hcNoContext](../org.viktor44.jtvision.core/ViewFlags.md#int-hcnocontext) indicates no context is assigned.


### `String param`

The keyboard shortcut display string shown to the right of the item
inside a [JtvMenuBox](JtvMenuBox.md), e.g. `"Ctrl+X"`. `null` for
submenu items or items with no displayable shortcut.


### `org.viktor44.jtvision.menus.JtvMenu subMenu`

For submenu items (`command` == 0), points to the nested
[JtvMenu](JtvMenu.md) that is opened when the item is selected. `null`
for command items and separators.


## Methods

### `getName()`

The display label for this menu item, with an optional tilde
(`~`) surrounding the hot-key letter, e.g. `"~F~ile"`.
`null` for separator lines.



### `setName(String name)`

The display label for this menu item, with an optional tilde
(`~`) surrounding the hot-key letter, e.g. `"~F~ile"`.
`null` for separator lines.

**Parameters**

- `name`


### `getCommand()`

The `cmXXXX` command constant posted when the item is selected,
or `0` for submenu items.



### `setCommand(int command)`

The `cmXXXX` command constant posted when the item is selected,
or `0` for submenu items.

**Parameters**

- `command`


### `isDisabled()`

`true` if this item is currently disabled and cannot be selected.
Reflects [JtvView.commandEnabled(int)](../org.viktor44.jtvision.views/JtvView.md#commandenabledint-command) at construction time and is
refreshed on `cmCommandSetChanged` broadcasts.



### `setDisabled(boolean disabled)`

`true` if this item is currently disabled and cannot be selected.
Reflects [JtvView.commandEnabled(int)](../org.viktor44.jtvision.views/JtvView.md#commandenabledint-command) at construction time and is
refreshed on `cmCommandSetChanged` broadcasts.

**Parameters**

- `disabled`


### `getKeyStroke()`

The keystroke shortcut that triggers this item directly, or `null`
if there is no shortcut.



### `setKeyStroke(JtvKeyStroke keyStroke)`

The keystroke shortcut that triggers this item directly, or `null`
if there is no shortcut.

**Parameters**

- `keyStroke`


### `getHelpCtx()`

The help context number for this item.
[org.viktor44.jtvision.core.ViewFlags.hcNoContext](../org.viktor44.jtvision.core/ViewFlags.md#int-hcnocontext) indicates no context is assigned.



### `setHelpCtx(int helpCtx)`

The help context number for this item.
[org.viktor44.jtvision.core.ViewFlags.hcNoContext](../org.viktor44.jtvision.core/ViewFlags.md#int-hcnocontext) indicates no context is assigned.

**Parameters**

- `helpCtx`


### `getParam()`

The keyboard shortcut display string shown to the right of the item
inside a [JtvMenuBox](JtvMenuBox.md), e.g. `"Ctrl+X"`. `null` for
submenu items or items with no displayable shortcut.



### `setParam(String param)`

The keyboard shortcut display string shown to the right of the item
inside a [JtvMenuBox](JtvMenuBox.md), e.g. `"Ctrl+X"`. `null` for
submenu items or items with no displayable shortcut.

**Parameters**

- `param`


### `getSubMenu()`

For submenu items (`command` == 0), points to the nested
[JtvMenu](JtvMenu.md) that is opened when the item is selected. `null`
for command items and separators.



### `setSubMenu(JtvMenu subMenu)`

For submenu items (`command` == 0), points to the nested
[JtvMenu](JtvMenu.md) that is opened when the item is selected. `null`
for command items and separators.

**Parameters**

- `subMenu`


