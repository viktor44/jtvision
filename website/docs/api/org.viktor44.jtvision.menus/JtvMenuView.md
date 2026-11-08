---
title: "JtvMenuView"
sidebar_label: "JtvMenuView"
---

Class JtvMenuView
=================

```java
public class JtvMenuView
extends JtvView
```


Abstract base class for all menu view objects.



JtvMenuView provides the core event loop, keyboard navigation, mouse
tracking, hot-key dispatch, and command-set update logic shared by
[JtvMenuBar](JtvMenuBar.md) (horizontal bar) and [JtvMenuBox](JtvMenuBox.md) (drop-down or
pop-up box). Direct instances of JtvMenuView are not useful; use one of
the concrete subclasses instead.

**Menu ownership chain**

Menu views are nested via [parentMenu](#orgviktor44jtvisionmenusjtvmenuview-parentmenu): a [JtvMenuBox](JtvMenuBox.md) opened
from a bar entry knows its parent is the [JtvMenuBar](JtvMenuBar.md). Mouse and
keyboard events propagate back up the chain so that moving the pointer
between siblings or pressing Escape correctly closes child menus and
returns focus to the parent.

**Event loop — [execute()](#execute)**

`execute()` runs a modal loop that processes mouse and keyboard
events until either a command item is selected or the menu is cancelled.
It is always called via [org.viktor44.jtvision.views.JtvGroup.execView](../org.viktor44.jtvision.views/JtvGroup.md#execviewjtvview-p) and must never be invoked
directly. The loop:

- Tracks the mouse to update [current](#orgviktor44jtvisionmenusjtvmenuitem-current).
- Handles arrow-key navigation via [trackKey(boolean)](#trackkeyboolean-findnext).
- Opens submenus into a child [JtvMenuBox](JtvMenuBox.md) via `execView`.
- Returns the selected command, or `0` if cancelled.

**Hot keys and shortcuts**

[handleEvent(JtvEvent)](#handleeventjtvevent-event) intercepts `evKeyDown` events before
the event loop and tries to match them against shortcut key codes stored
in the menu items (`JtvMenuItem.keyCode`). A match causes an
`evCommand` event to be posted without opening the menu visually.

**Colour palette**

The six-entry palette `cpMenuView` maps to:

1. `2` — normal text.
2. `3` — disabled text.
3. `4` — shortcut letter.
4. `5` — selected shortcut.
5. `6` — selected normal.
6. `7` — selected disabled.



org.viktor44.jtvision.menus.JtvMenuView

## Summary

### Constructors

| Visibility | Signature                                                                            |
| ---------- | ------------------------------------------------------------------------------------ |
| public     | [JtvMenuView(JtvRect bounds)](#jtvmenuviewjtvrect-bounds)                            |
| public     | [JtvMenuView(JtvRect bounds, JtvMenu menu)](#jtvmenuviewjtvrect-bounds-jtvmenu-menu) |

### Fields

| Modifiers                | Field name                                                    | Type                                    |
| ------------------------ | ------------------------------------------------------------- | --------------------------------------- |
| **protected**            | [menu](#orgviktor44jtvisionmenusjtvmenu-menu)                 | org.viktor44.jtvision.menus.JtvMenu     |
| **protected**            | [parentMenu](#orgviktor44jtvisionmenusjtvmenuview-parentmenu) | org.viktor44.jtvision.menus.JtvMenuView |
| **protected**            | [current](#orgviktor44jtvisionmenusjtvmenuitem-current)       | org.viktor44.jtvision.menus.JtvMenuItem |
| **private static final** | [cpMenuView](#orgviktor44jtvisioncorejtvpalette-cpmenuview)   | org.viktor44.jtvision.core.JtvPalette   |

### Methods

| Modifiers     | Method signature                                                                                                                                                    | Return type                             |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------- |
| **public**    | [getPalette()](#getpalette)                                                                                                                                         | org.viktor44.jtvision.core.JtvPalette   |
| **public**    | [getItemRect(JtvMenuItem item)](#getitemrectjtvmenuitem-item)                                                                                                       | org.viktor44.jtvision.core.JtvRect      |
| **protected** | [tryCommandByShortcut(JtvEvent e)](#trycommandbyshortcutjtvevent-e)                                                                                                 | boolean                                 |
| **protected** | [tryCommandByShortcutInMenu(JtvMenu searchMenu, int keyCode, JtvEvent sourceEvent)](#trycommandbyshortcutinmenujtvmenu-searchmenu-int-keycode-jtvevent-sourceevent) | boolean                                 |
| **public**    | [execute()](#execute)                                                                                                                                               | int                                     |
| **protected** | [trackMouse(JtvEvent e, boolean active)](#trackmousejtvevent-e-boolean-active)                                                                                      | void                                    |
| **protected** | [trackKey(boolean findNext)](#trackkeyboolean-findnext)                                                                                                             | void                                    |
| **protected** | [nextItem()](#nextitem)                                                                                                                                             | void                                    |
| **protected** | [prevItem()](#previtem)                                                                                                                                             | void                                    |
| **protected** | [mouseInOwner(JtvEvent e)](#mouseinownerjtvevent-e)                                                                                                                 | boolean                                 |
| **protected** | [mouseInMenus(JtvEvent e)](#mouseinmenusjtvevent-e)                                                                                                                 | boolean                                 |
| **public**    | [topMenu()](#topmenu)                                                                                                                                               | org.viktor44.jtvision.menus.JtvMenuView |
| **protected** | [doASelect(JtvEvent event)](#doaselectjtvevent-event)                                                                                                               | void                                    |
| **private**   | [updateMenu(JtvMenu aMenu)](#updatemenujtvmenu-amenu)                                                                                                               | boolean                                 |
| **protected** | [firstItem(JtvMenu aMenu)](#firstitemjtvmenu-amenu)                                                                                                                 | org.viktor44.jtvision.menus.JtvMenuItem |
| **public**    | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                                                                                           | void                                    |



## Constructors

### `JtvMenuView(JtvRect bounds)`

Constructs a menu view with the given bounds and no menu assigned.
Adds `evBroadcast` to the event mask so the view receives
`cmCommandSetChanged` broadcasts.

**Parameters**

- `bounds` - the bounding rectangle



### `JtvMenuView(JtvRect bounds, JtvMenu menu)`

Constructs a menu view with the given bounds and an initial menu.
Adds `evBroadcast` to the event mask.

**Parameters**

- `bounds` - the bounding rectangle
- `menu` - the `JtvMenu` whose items to display



## Fields

### `org.viktor44.jtvision.menus.JtvMenu menu`

The [JtvMenu](JtvMenu.md) record whose items this view renders and processes.
`null` until assigned by a subclass constructor.


### `org.viktor44.jtvision.menus.JtvMenuView parentMenu`

The parent menu view in the ownership chain, or `null` if this
is the top-level menu. Used to route mouse events and Escape-key
handling back to the owning bar or box.


### `org.viktor44.jtvision.menus.JtvMenuItem current`

The currently highlighted (focused) menu item, or `null` if
no item is highlighted. Updated by mouse tracking and keyboard
navigation; saved to `JtvMenu.defaultItem` when the menu closes.


### `org.viktor44.jtvision.core.JtvPalette cpMenuView`

Six-entry colour palette shared by all menu view descendants:

1. `2` — normal text.
2. `3` — disabled text.
3. `4` — shortcut letter.
4. `5` — selected shortcut.
5. `6` — selected normal.
6. `7` — selected disabled.




## Methods

### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the menu view's shared colour palette `cpMenuView`.

**Returns**

the six-entry palette



### `getItemRect(JtvMenuItem item)`

Returns the bounding rectangle of `item` within this view.
The base implementation returns an empty rectangle. Concrete
subclasses ([JtvMenuBar](JtvMenuBar.md), [JtvMenuBox](JtvMenuBox.md)) must override this
to support mouse hit-testing.

**Parameters**

- `item` - the menu item whose position to compute

**Returns**

the rectangle occupied by the item in view-local coordinates



### `tryCommandByShortcut(JtvEvent e)`

Searches [menu](#orgviktor44jtvisionmenusjtvmenu-menu) (and recursively its submenus) for an item
whose `JtvMenuItem.keyCode` matches the key code in `e`.
If found and the item is enabled, posts an `evCommand` event
with the item's command and clears the key event.

**Parameters**

- `e` - the key-down event to match

**Returns**

`true` if a shortcut was matched and consumed



### `tryCommandByShortcutInMenu(JtvMenu searchMenu, int keyCode, JtvEvent sourceEvent)`

Recursively searches `searchMenu` for an item with the given
`keyCode`. Posts an `evCommand` if found and enabled.

**Parameters**

- `searchMenu` - the menu to search
- `keyCode` - the scan code to match
- `sourceEvent` - the original key-down event (cleared on match)

**Returns**

`true` if a match was found and the event consumed



### `execute()`

**Overrides:**

`execute()` in `org.viktor44.jtvision.views.JtvView`

Runs the modal menu event loop.



Processes events until the user selects a command item (returns its
command code), presses Escape, or clicks outside all menus (returns
`0`). This method must only be called via
[org.viktor44.jtvision.views.JtvGroup.execView](../org.viktor44.jtvision.views/JtvGroup.md#execviewjtvview-p).



The loop handles:

- `evMouseDown/Up/Move` — track the pointer, auto-open
      submenus, and select the item on mouse-up.
- `evKeyDown` — Up/Down navigate within a box; Left/Right
      navigate across bar items; Enter selects; Escape exits; letter
      keys match hot-key characters.
- `evCommand cmMenu` — auto-opens the currently focused
      submenu.



**Returns**

the command code of the selected item, or `0` if
        the menu was cancelled



### `trackMouse(JtvEvent e, boolean active)`

Updates [current](#orgviktor44jtvisionmenusjtvmenuitem-current) to the item whose bounding rectangle
(from [getItemRect(JtvMenuItem)](#getitemrectjtvmenuitem-item)) contains the mouse pointer
position reported in `e`.

**Parameters**

- `e` - the mouse event providing the pointer position
- `active` - unused; reserved for subclass extensions



### `trackKey(boolean findNext)`

Advances or retreats [current](#orgviktor44jtvisionmenusjtvmenuitem-current) by one non-separator item,
wrapping around at the ends of the list.

**Parameters**

- `findNext` - `true` to move forward (Down/Right),
                  `false` to move backward (Up/Left)



### `nextItem()`

Advances [current](#orgviktor44jtvisionmenusjtvmenuitem-current) to the next item in the list, wrapping
back to `JtvMenu.items` after the last item.



### `prevItem()`

Retreats [current](#orgviktor44jtvisionmenusjtvmenuitem-current) by one position, wrapping to the last
item when currently at the first item.



### `mouseInOwner(JtvEvent e)`

Returns `true` if the mouse pointer reported in `e` is
within the bounding rectangle of the item currently highlighted in
[parentMenu](#orgviktor44jtvisionmenusjtvmenuview-parentmenu). Used to detect whether a mouse-up on the parent
bar should close the child box rather than selecting an item.

**Parameters**

- `e` - the mouse event

**Returns**

`true` if the pointer is over the parent's current item



### `mouseInMenus(JtvEvent e)`

Returns `true` if the mouse pointer is within any ancestor
menu view in the [parentMenu](#orgviktor44jtvisionmenusjtvmenuview-parentmenu) chain. Used to route a click
that lands on a sibling menu entry back to the top-level handler.

**Parameters**

- `e` - the mouse event

**Returns**

`true` if the pointer is inside an ancestor menu



### `topMenu()`

Walks the [parentMenu](#orgviktor44jtvisionmenusjtvmenuview-parentmenu) chain and returns the root (top-level)
menu view, which is the one with no parent.

**Returns**

the top-most menu view in the chain



### `doASelect(JtvEvent event)`

Executes this menu view as a modal sub-view via the owner group.
Posts `event` to signal activation, calls
[org.viktor44.jtvision.views.JtvGroup.execView](../org.viktor44.jtvision.views/JtvGroup.md#execviewjtvview-p), and if a
non-zero command is returned and is enabled, posts an `evCommand`
event with that command. Clears the original event on return.

**Parameters**

- `event` - the activating event (typically `evMouseDown` or
               `evCommand cmMenu`)



### `updateMenu(JtvMenu aMenu)`

Walks the entire menu tree rooted at `aMenu` and refreshes the
`JtvMenuItem.disabled` flag on each command item by calling
[JtvView.commandEnabled(int)](../org.viktor44.jtvision.views/JtvView.md#commandenabledint-command).

**Parameters**

- `aMenu` - the menu to update recursively

**Returns**

`true` if any item's enabled state changed



### `firstItem(JtvMenu aMenu)`

*No method description provided*

**Parameters**

- `aMenu`


### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles events for the menu view.



First tries shortcut keys via [tryCommandByShortcut(JtvEvent)](#trycommandbyshortcutjtvevent-e).
Then:

- `evMouseDown` — activates the menu via [doASelect](#doaselectjtvevent-event).
- `evCommand cmMenu` — activates the menu.
- `evBroadcast cmCommandSetChanged` — refreshes disabled
      flags via `updateMenu` and redraws if changed.



**Parameters**

- `event` - the incoming event



