---
title: "JtvStatusLine"
sidebar_label: "JtvStatusLine"
---

Class JtvStatusLine
===================

```java
public class JtvStatusLine
extends JtvView
```


The application status line, typically displayed at the bottom of the screen.



JtvStatusLine renders a single row of [JtvStatusItem](JtvStatusItem.md) labels and an
optional context-sensitive hint string. It is normally owned by the
application group and is one of the three standard application sub-views
(together with the menu bar and desktop).

**Context-sensitive display**

The status line holds an ordered list of [JtvStatusDef](JtvStatusDef.md) records, each
covering a range of help context values. [update()](#update) is called from
the application idle loop; it queries the help context of the currently
focused view and selects the first [JtvStatusDef](JtvStatusDef.md) whose range contains
that context. The matching definition's [JtvStatusItem](JtvStatusItem.md) list becomes
the active `items` list and is rendered by [draw()](#draw).

**Hints**

After drawing the status items, [draw()](#draw) calls [hint(int)](#hintint-ahelpctx)
with the current help context. If the returned string is non-empty it is
drawn to the right of a vertical separator (`\u2502`). Subclasses can
override [hint(int)](#hintint-ahelpctx) to supply dynamic hint text.

**Hot keys**

`JtvStatusItem.keyCode` values are scanned during `evKeyDown`
events: a match converts the key event to an `evCommand` event with
the item's command, providing application-wide shortcut key bindings.
Clicking on a label area triggers the same command via `evMouseDown`.

**Grow mode**

The status line's `growMode` is set to
`gfGrowLoY | gfGrowHiX | gfGrowHiY` so it stays pinned to the
bottom edge and stretches horizontally when the application is resized.

**Colour palette**

The six-entry palette `cpStatusLine` maps to:

1. `2` — normal text.
2. `3` — disabled text.
3. `4` — shortcut letter.
4. `5` — selected shortcut.
5. `6` — selected normal.
6. `7` — selected disabled.



org.viktor44.jtvision.views.JtvStatusLine

## Summary

### Constructors

| Visibility | Signature                                                                                            |
| ---------- | ---------------------------------------------------------------------------------------------------- |
| public     | [JtvStatusLine(JtvRect bounds, JtvStatusDef aDefs)](#jtvstatuslinejtvrect-bounds-jtvstatusdef-adefs) |

### Fields

| Modifiers                | Field name                                                      | Type                                            |
| ------------------------ | --------------------------------------------------------------- | ----------------------------------------------- |
| **private static final** | [cpStatusLine](#orgviktor44jtvisioncorejtvpalette-cpstatusline) | org.viktor44.jtvision.core.JtvPalette           |
| **private static final** | [hintSeparator](#string-hintseparator)                          | String                                          |
| **private**              | [defs](#listorgviktor44jtvisionviewsjtvstatusdef-defs)          | List&lt;org.viktor44.jtvision.views.JtvStatusDef>  |
| **private**              | [items](#listorgviktor44jtvisionviewsjtvstatusitem-items)       | List&lt;org.viktor44.jtvision.views.JtvStatusItem> |

### Methods

| Modifiers  | Method signature                                                        | Return type                               |
| ---------- | ----------------------------------------------------------------------- | ----------------------------------------- |
| **public** | [addDefinition(JtvStatusDef item)](#adddefinitionjtvstatusdef-item)     | org.viktor44.jtvision.views.JtvStatusLine |
| **public** | [draw()](#draw)                                                         | void                                      |
| **public** | [drawSelect(JtvStatusItem selected)](#drawselectjtvstatusitem-selected) | void                                      |
| **public** | [findItems()](#finditems)                                               | void                                      |
| **public** | [getPalette()](#getpalette)                                             | org.viktor44.jtvision.core.JtvPalette     |
| **public** | [itemMouseIsIn(JtvPoint mouse)](#itemmouseisinjtvpoint-mouse)           | org.viktor44.jtvision.views.JtvStatusItem |
| **public** | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)               | void                                      |
| **public** | [hint(int aHelpCtx)](#hintint-ahelpctx)                                 | String                                    |
| **public** | [update()](#update)                                                     | void                                      |



## Constructors

### `JtvStatusLine(JtvRect bounds, JtvStatusDef aDefs)`

Constructs a status line with the given bounds and definition list.
Sets `ofPreProcess` in options (for hot-key pre-processing),
adds `evBroadcast` to the event mask, sets grow mode to
`gfGrowLoY | gfGrowHiX | gfGrowHiY`, and calls
[findItems()](#finditems) to select the initial item list.

**Parameters**

- `bounds` - the bounding rectangle (typically the bottom row)
- `aDefs` - the initial status definition, or `null`



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpStatusLine`

Six-entry colour palette:

1. `2` — normal text.
2. `3` — disabled text.
3. `4` — shortcut letter.
4. `5` — selected shortcut.
5. `6` — selected normal.
6. `7` — selected disabled.




### `String hintSeparator`

The separator drawn between the status items and the hint text:
`"\u2502 "` (vertical bar followed by a space).


### `List<org.viktor44.jtvision.views.JtvStatusDef> defs`

The ordered list of [JtvStatusDef](JtvStatusDef.md) records. [update()](#update)
walks this list to find the definition appropriate for the current
help context.


### `List<org.viktor44.jtvision.views.JtvStatusItem> items`

The currently active [JtvStatusItem](JtvStatusItem.md) list, selected from
`defs` by [findItems()](#finditems). Rendered by [draw()](#draw).


## Methods

### `addDefinition(JtvStatusDef item)`

*No method description provided*

**Parameters**

- `item`


### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the status line by delegating to [drawSelect(JtvStatusItem)](#drawselectjtvstatusitem-selected)
with no item highlighted.



### `drawSelect(JtvStatusItem selected)`

Renders the full status line row, optionally highlighting one item.



Each item in `items` whose `JtvStatusItem.text` is
non-`null` is drawn with surrounding spaces. The colour is:

- Selected and enabled — `cSelect`.
- Selected and disabled — `cSelDisabled`.
- Enabled — `cNormal`.
- Disabled — `cNormDisabled`.

After all items, if space remains and [hint(int)](#hintint-ahelpctx) returns a
non-empty string, the hint is drawn following `hintSeparator`.

**Parameters**

- `selected` - the item to highlight as pressed, or `null`
                  for normal (non-pressed) drawing



### `findItems()`

Selects the active item list by finding the first [JtvStatusDef](JtvStatusDef.md)
in `defs` whose range contains [JtvView.helpCtx](JtvView.md#int-helpctx). Sets
`items` to that definition's item list, or `null` if no
definition matches.



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the status line's colour palette `cpStatusLine`.

**Returns**

the six-entry palette



### `itemMouseIsIn(JtvPoint mouse)`

Returns the [JtvStatusItem](JtvStatusItem.md) whose label area contains the given
view-local mouse position, or `null` if the pointer is not over
any item.



Only items in row 0 (`mouse.y == 0`) are considered. Items are
laid out sequentially from column 0; each item occupies
`cstrLen(text) + 2` columns.

**Parameters**

- `mouse` - the pointer position in view-local coordinates

**Returns**

the item under the pointer, or `null`



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles events for the status line.



**`evMouseDown`:** tracks the pointer while the button is
held, calling [drawSelect(JtvStatusItem)](#drawselectjtvstatusitem-selected) to highlight the item
under the pointer. On button release, if the pointer is still over
an enabled item, posts an `evCommand` event with that item's
command and clears the mouse event.



**`evKeyDown`:** scans `items` for a matching
`JtvStatusItem.keyCode`. On match, converts the event to an
`evCommand` with that item's command (leaving it for the
application to process).



**`evBroadcast cmCommandSetChanged`:** redraws the status
line to reflect any hot key enable/disable changes.

**Parameters**

- `event` - the incoming event



### `hint(int aHelpCtx)`

Returns a context-sensitive hint string for display after the status
items. The base implementation always returns an empty string.
Subclasses should override this to provide meaningful hints based on
`aHelpCtx`.

**Parameters**

- `aHelpCtx` - the current help context of the focused view

**Returns**

a hint string, or an empty string for no hint



### `update()`

Checks whether the focused view's help context has changed since the
last call. If so, calls [findItems()](#finditems) to re-select the active
item list and redraws the status line.



This method is called from the application's idle loop
(`TProgram.idle()`) after each event.



