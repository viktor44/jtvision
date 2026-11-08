---
title: "JtvListViewer"
sidebar_label: "JtvListViewer"
---

Class JtvListViewer
===================

```java
public class JtvListViewer
extends JtvView
```


A scrollable list view that displays a range of selectable items in one or
more columns.



JtvListViewer provides a general-purpose list control backed by an abstract
[getText(int, int)](#gettextint-item-int-maxlen) method that subclasses override to supply each
item's display string. The viewer handles keyboard navigation (arrow keys,
Page Up/Down, Home, End), mouse clicks and drag-selection, and optional
horizontal and vertical scroll bars.

**Multi-column layout**

When `aNumCols > 1`, items are distributed across columns top-to-bottom
within each column: item `j * viewHeight + i` appears at column
`j`, row `i`. A vertical separator character (`\u2502`)
is drawn between columns.

**Selection markers**

When [JtvView.showMarkers](JtvView.md#boolean-showmarkers) is `true`, the characters from
[specialChars](#char-specialchars) are placed at the left and right edges of each item:
`»` and `«` for selected items, `\u2022` for focused items,
and spaces otherwise.

**Colour palette**

The five-entry palette `cpListViewer` maps to:

1. `0x1A` — normal colour (active).
2. `0x1A` — normal colour (inactive).
3. `0x1B` — focused item colour.
4. `0x1C` — selected item colour.
5. `0x1D` — column-separator colour.



org.viktor44.jtvision.views.JtvListViewer

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                      |
| ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvListViewer(JtvRect bounds, int aNumCols, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar)](#jtvlistviewerjtvrect-bounds-int-anumcols-jtvscrollbar-ahscrollbar-jtvscrollbar-avscrollbar) |

### Fields

| Modifiers                | Field name                                                      | Type                                     |
| ------------------------ | --------------------------------------------------------------- | ---------------------------------------- |
| **public static final**  | [specialChars](#char-specialchars)                              | char[]                                   |
| **private static final** | [cpListViewer](#orgviktor44jtvisioncorejtvpalette-cplistviewer) | org.viktor44.jtvision.core.JtvPalette    |
| **protected**            | [numCols](#int-numcols)                                         | int                                      |
| **protected**            | [topItem](#int-topitem)                                         | int                                      |
| **protected**            | [focused](#int-focused)                                         | int                                      |
| **protected**            | [range](#int-range)                                             | int                                      |
| **protected**            | [hScrollBar](#orgviktor44jtvisionviewsjtvscrollbar-hscrollbar)  | org.viktor44.jtvision.views.JtvScrollBar |
| **protected**            | [vScrollBar](#orgviktor44jtvisionviewsjtvscrollbar-vscrollbar)  | org.viktor44.jtvision.views.JtvScrollBar |
| **protected**            | [emptyText](#string-emptytext)                                  | String                                   |

### Methods

| Modifiers  | Method signature                                                           | Return type                           |
| ---------- | -------------------------------------------------------------------------- | ------------------------------------- |
| **public** | [changeBounds(JtvRect bounds)](#changeboundsjtvrect-bounds)                | void                                  |
| **public** | [draw()](#draw)                                                            | void                                  |
| **public** | [focusItem(int item)](#focusitemint-item)                                  | void                                  |
| **public** | [focusItemNum(int item)](#focusitemnumint-item)                            | void                                  |
| **public** | [getPalette()](#getpalette)                                                | org.viktor44.jtvision.core.JtvPalette |
| **public** | [getText(int item, int maxLen)](#gettextint-item-int-maxlen)               | String                                |
| **public** | [isSelected(int item)](#isselectedint-item)                                | boolean                               |
| **public** | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                  | void                                  |
| **public** | [selectItem(int item)](#selectitemint-item)                                | void                                  |
| **public** | [setRange(int aRange)](#setrangeint-arange)                                | void                                  |
| **public** | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable) | void                                  |
| **public** | [getFocused()](#getfocused)                                                | int                                   |



## Constructors

### `JtvListViewer(JtvRect bounds, int aNumCols, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar)`

Constructs a list viewer with the given bounds, column count, and
optional scroll bars.



Sets `ofFirstClick` and `ofSelectable` on options so the
viewer takes focus on the first mouse click and accepts keyboard input.
Adds `evBroadcast` to the event mask to receive scroll-bar
notifications. Configures scroll bar page and arrow step sizes based on
the view height and column count.

**Parameters**

- `bounds` - the bounding rectangle
- `aNumCols` - number of display columns (&ge; 1)
- `aHScrollBar` - horizontal scroll bar, or `null`
- `aVScrollBar` - vertical scroll bar, or `null`



## Fields

### `char[] specialChars`

Characters used to mark list items when [JtvView.showMarkers](JtvView.md#boolean-showmarkers) is
`true`. Layout: `[focLeft, focRight, selLeft, selRight,
normLeft, normRight]`, where the pairs are the left/right edge markers
for focused, selected, and normal items respectively.

- `»` (») — left marker for selected items.
- `«` («) — right marker for selected items.
- `\u2022` (•) — left/right marker for focused items.




### `org.viktor44.jtvision.core.JtvPalette cpListViewer`

Five-entry colour palette:

1. `0x1A` — normal (active).
2. `0x1A` — normal (inactive).
3. `0x1B` — focused item.
4. `0x1C` — selected item.
5. `0x1D` — column separator.




### `int numCols`

The number of display columns the list is divided into.
Items are distributed top-to-bottom within each column.


### `int topItem`

The index of the first item visible in the topmost row of the view.


### `int focused`

The index of the currently focused (keyboard-cursor) item.


### `int range`

The total number of items in the list. Items at indices
`[0, range)` are valid; indices &ge; `range` are blank.


### `org.viktor44.jtvision.views.JtvScrollBar hScrollBar`

The optional horizontal scroll bar. `null` if not used.


### `org.viktor44.jtvision.views.JtvScrollBar vScrollBar`

The optional vertical scroll bar. `null` if not used.


### `String emptyText`

Text displayed in the first cell when the list is empty (`range == 0`).


## Methods

### `changeBounds(JtvRect bounds)`

**Overrides:**

`changeBounds(JtvRect bounds)` in `org.viktor44.jtvision.views.JtvView`

Adjusts scroll bar step sizes after a bounds change to match the new
view dimensions.

**Parameters**

- `bounds` - the new bounding rectangle



### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Renders all visible items across all columns.



Each row-column cell computes the item index as
`col * viewHeight + row + topItem`. Items at or beyond
[range](#int-range) are drawn blank. When the list is empty the first cell
shows [emptyText](#string-emptytext). A vertical separator is drawn at the right
edge of each column.



Colour selection:

- Focused and active — palette entry 3 (focused colour).
- Selected — palette entry 4.
- Normal — palette entry 1 (active) or 2 (inactive).

The text cursor is moved to the focused item's cell; if the focused item
is not visible it is placed off-screen at `(-1, -1)`.



### `focusItem(int item)`

Moves the focus to `item` and scrolls the vertical scroll bar (or
redraws directly) so the item is visible. Updates [topItem](#int-topitem) if
the item has scrolled out of the visible window.

**Parameters**

- `item` - the item index to focus; must be in `[0, range)`



### `focusItemNum(int item)`

Clamps `item` to the valid range `[0, range - 1]` and
delegates to [focusItem(int)](#focusitemint-item). Does nothing if [range](#int-range)
is zero.

**Parameters**

- `item` - the desired focus index (clamped automatically)



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the list viewer's colour palette `cpListViewer`.

**Returns**

the five-entry palette



### `getText(int item, int maxLen)`

Returns the display text for the given item index, truncated to
`maxLen` characters.



The base implementation returns an empty string. Subclasses must
override this method to provide meaningful content.

**Parameters**

- `item` - the zero-based item index
- `maxLen` - the maximum number of characters to return

**Returns**

the item's display text, or an empty string



### `isSelected(int item)`

Returns `true` if `item` is the currently focused item.
Subclasses with multi-selection can override this to reflect a richer
selection model.

**Parameters**

- `item` - the zero-based item index to test

**Returns**

`true` if the item is selected/focused



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles mouse clicks, drag-selection, and keyboard navigation.



**Mouse (`evMouseDown`):** computes the item under the pointer,
calls [focusItemNum(int)](#focusitemnumint-item), and loops while the button is held
(`evMouseMove | evMouseAuto`). Auto-scroll moves the focus when
the pointer is dragged above or below the view. A double-click triggers
[selectItem(int)](#selectitemint-item).



**Keyboard (`evKeyDown`):** arrow keys, Page Up/Down, Home,
and End adjust [focused](#int-focused) via [focusItemNum(int)](#focusitemnumint-item).
Right/Left arrows move by a full column (`size.getY()`) when
`numCols > 1`.



**Broadcast (`evBroadcast`):** `cmScrollBarClicked`
selects this view; `cmScrollBarChanged` from the vertical bar
updates the focused item, from the horizontal bar redraws.

**Parameters**

- `event` - the incoming event



### `selectItem(int item)`

Notifies the owner that the user has selected `item` by
broadcasting `cmListItemSelected` with this viewer as the info
pointer.

**Parameters**

- `item` - the zero-based index of the selected item



### `setRange(int aRange)`

Sets the total number of items and updates the vertical scroll bar
range. If the currently focused item falls outside the new range,
resets the focus to zero.

**Parameters**

- `aRange` - the new item count; pass `0` for an empty list



### `setState(int aState, boolean enable)`

**Overrides:**

`setState(int aState, boolean enable)` in `org.viktor44.jtvision.views.JtvView`

Propagates the state change to the parent, then shows or hides the
scroll bars depending on whether the viewer is both active and visible.
Redraws the view when `sfSelected`, `sfActive`, or
`sfVisible` change.

**Parameters**

- `aState` - the state bits to modify
- `enable` - `true` to set; `false` to clear



### `getFocused()`

The index of the currently focused (keyboard-cursor) item.



