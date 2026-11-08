---
title: "JtvWindow"
sidebar_label: "JtvWindow"
---

Class JtvWindow
===============

```java
public class JtvWindow
extends JtvGroup
```


A bordered, titled window with optional close, zoom, and resize controls.



JtvWindow extends [JtvGroup](JtvGroup.md) to provide a standard JT Vision window.
It automatically creates and inserts a [JtvFrame](JtvFrame.md) that draws the
border, title bar, close button, zoom button, and resize corner icons.

**Window flags**

The [flags](#int-flags) field is a bitmask of `wfXXXX` constants that
control which interactive features are enabled:

- `wfMove` — the window can be dragged by its title bar.
- `wfGrow` — the window can be resized from its bottom corners.
- `wfClose` — a close button is shown; clicking it or pressing
      Ctrl+F4 sends `cmClose`.
- `wfZoom` — a zoom button is shown; clicking it or pressing F5
      toggles the window between its normal and maximum size.

All four flags are set by default. When selected, the corresponding commands
(`cmResize`, `cmClose`, `cmZoom`, `cmNext`,
`cmPrev`) are enabled in the global command set and disabled again
when the window loses focus.

**Colour palettes**

Three built-in palettes are available, selected by the [paletteIndex](#int-paletteindex)
field: `wpBlueWindow` (default), `wpCyanWindow`, and
`wpGrayWindow`.

**Colour palette**

Each window palette has 8 entries:

1. Passive (inactive) frame colour.
2. Active frame colour.
3. Frame icons colour (close, zoom, resize).
4. Scroll bar page area colour.
5. Scroll bar arrow controls colour.
6. Scroller normal text colour.
7. Scroller selected text colour.
8. Reserved.

**Scroll bars**

Use [standardScrollBar(int)](#standardscrollbarint-aoptions) to create a keyboard-driven scroll bar
attached to the window frame. Pass `sbVertical` or `sbHorizontal`
(optionally OR-ed with `sbHandleKeyboard`) to control orientation.

**Zoom**

Calling [zoom()](#zoom) toggles between the maximum size and the size
stored in [zoomRect](#orgviktor44jtvisioncorejtvrect-zoomrect). [zoomRect](#orgviktor44jtvisioncorejtvrect-zoomrect) is updated whenever the
window is at a non-maximum size when zoom is activated.

org.viktor44.jtvision.views.JtvWindow

## Summary

### Constructors

| Visibility | Signature                                                                                                   |
| ---------- | ----------------------------------------------------------------------------------------------------------- |
| public     | [JtvWindow(JtvRect bounds, String aTitle, int aNumber)](#jtvwindowjtvrect-bounds-string-atitle-int-anumber) |

### Fields

| Modifiers               | Field name                                                      | Type                                  |
| ----------------------- | --------------------------------------------------------------- | ------------------------------------- |
| **protected**           | [flags](#int-flags)                                             | int                                   |
| **protected**           | [zoomRect](#orgviktor44jtvisioncorejtvrect-zoomrect)            | org.viktor44.jtvision.core.JtvRect    |
| **protected**           | [number](#int-number)                                           | int                                   |
| **protected**           | [paletteIndex](#int-paletteindex)                               | int                                   |
| **protected**           | [title](#string-title)                                          | String                                |
| **protected**           | [frame](#orgviktor44jtvisionviewsjtvframe-frame)                | org.viktor44.jtvision.views.JtvFrame  |
| **public static final** | [cpBlueWindow](#orgviktor44jtvisioncorejtvpalette-cpbluewindow) | org.viktor44.jtvision.core.JtvPalette |
| **public static final** | [cpCyanWindow](#orgviktor44jtvisioncorejtvpalette-cpcyanwindow) | org.viktor44.jtvision.core.JtvPalette |
| **public static final** | [cpGrayWindow](#orgviktor44jtvisioncorejtvpalette-cpgraywindow) | org.viktor44.jtvision.core.JtvPalette |

### Methods

| Modifiers     | Method signature                                                           | Return type                              |
| ------------- | -------------------------------------------------------------------------- | ---------------------------------------- |
| **public**    | [close()](#close)                                                          | void                                     |
| **public**    | [getPalette()](#getpalette)                                                | org.viktor44.jtvision.core.JtvPalette    |
| **public**    | [getTitle(int maxSize)](#gettitleint-maxsize)                              | String                                   |
| **public**    | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                  | void                                     |
| **protected** | [initFrame(JtvRect r)](#initframejtvrect-r)                                | org.viktor44.jtvision.views.JtvFrame     |
| **public**    | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable) | void                                     |
| **public**    | [standardScrollBar(int aOptions)](#standardscrollbarint-aoptions)          | org.viktor44.jtvision.views.JtvScrollBar |
| **public**    | [zoom()](#zoom)                                                            | void                                     |
| **public**    | [getFlags()](#getflags)                                                    | int                                      |
| **public**    | [setFlags(int flags)](#setflagsint-flags)                                  | void                                     |
| **public**    | [getZoomRect()](#getzoomrect)                                              | org.viktor44.jtvision.core.JtvRect       |
| **public**    | [setZoomRect(JtvRect zoomRect)](#setzoomrectjtvrect-zoomrect)              | void                                     |
| **public**    | [getNumber()](#getnumber)                                                  | int                                      |
| **public**    | [setNumber(int number)](#setnumberint-number)                              | void                                     |
| **public**    | [getPaletteIndex()](#getpaletteindex)                                      | int                                      |
| **public**    | [setPaletteIndex(int paletteIndex)](#setpaletteindexint-paletteindex)      | void                                     |
| **public**    | [getTitle()](#gettitle)                                                    | String                                   |
| **public**    | [setTitle(String title)](#settitlestring-title)                            | void                                     |



## Constructors

### `JtvWindow(JtvRect bounds, String aTitle, int aNumber)`

Creates a window.



Initialises flags to `wfMove | wfGrow | wfClose | wfZoom`,
palette to `wpBlueWindow`, and state includes `sfShadow`.
Options include `ofSelectable | ofTopSelect` (the window floats
to the front when selected) and grow-mode is `gfGrowAll | gfGrowRel`.

**Parameters**

- `bounds` - the initial bounding rectangle
- `aTitle` - the title bar text
- `aNumber` - the window number (use `wnNoNumber` to omit)



## Fields

### `int flags`

Bitmask of `wfXXXX` window-feature flags (`wfMove`, `wfGrow`, `wfClose`, `wfZoom`).


### `org.viktor44.jtvision.core.JtvRect zoomRect`

The bounding rectangle to restore when the window is un-zoomed.
Updated by [zoom()](#zoom) before maximising.


### `int number`

The window's sequence number displayed in the title bar (e.g. `:1`).
Use `wnNoNumber` to suppress the number display.


### `int paletteIndex`

Selects the window colour palette: `wpBlueWindow`, `wpCyanWindow`, or `wpGrayWindow`.


### `String title`

The text displayed in the window's title bar.
Accessed by the frame via [getTitle(int)](#gettitleint-maxsize).


### `org.viktor44.jtvision.views.JtvFrame frame`

The [JtvFrame](JtvFrame.md) child view that draws this window's border, title, and control icons. 
Set to `null` during `shutDown()`.


### `org.viktor44.jtvision.core.JtvPalette cpBlueWindow`

Blue-window palette — 8 entries, used when [paletteIndex](#int-paletteindex) is
`wpBlueWindow`:

- `8` — passive frame.
- `9` — active frame.
- `10` — frame icons (close, zoom, resize).
- `11` — scroll bar page area.
- `12` — scroll bar arrow controls.
- `13` — scroller normal text.
- `14` — scroller selected text.
- `15` — reserved.




### `org.viktor44.jtvision.core.JtvPalette cpCyanWindow`

Cyan-window palette — 8 entries, used when [paletteIndex](#int-paletteindex) is
`wpCyanWindow`:

- `16` — passive frame.
- `17` — active frame.
- `18` — frame icons (close, zoom, resize).
- `19` — scroll bar page area.
- `20` — scroll bar arrow controls.
- `21` — scroller normal text.
- `22` — scroller selected text.
- `23` — reserved.




### `org.viktor44.jtvision.core.JtvPalette cpGrayWindow`

Gray-window palette — 8 entries, used when [paletteIndex](#int-paletteindex) is
`wpGrayWindow`:

- `24` — passive frame.
- `25` — active frame.
- `26` — frame icons (close, zoom, resize).
- `27` — scroll bar page area.
- `28` — scroll bar arrow controls.
- `29` — scroller normal text.
- `30` — scroller selected text.
- `31` — reserved.




## Methods

### `close()`

Validates and removes this window from its owner if
[valid(int)](JtvGroup.md#validint-command) approves `cmClose`.



### `getPalette()`

Returns the colour palette for the current [paletteIndex](#int-paletteindex) choice.
Defaults to [cpBlueWindow](#orgviktor44jtvisioncorejtvpalette-cpbluewindow) for any unrecognised palette value.

**Returns**

the active window colour palette



### `getTitle(int maxSize)`

Returns the title bar text, truncated to `maxSize` characters.
The frame uses this method when rendering the title.

**Parameters**

- `maxSize` - the maximum number of characters to return

**Returns**

the window title (currently ignores the maxSize limit)



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvGroup`

Handles window-specific commands and keyboard shortcuts:

- `cmResize` — starts an interactive drag/resize session
      when `wfMove` or `wfGrow` is set.
- `cmClose` — closes the window (non-modal) or sends
      `cmCancel` (modal).
- `cmZoom` — toggles the zoom state when `wfZoom` is set.
- Tab / Shift+Tab — moves focus forward or backward among children.
- `cmSelectWindowNum` broadcast — selects this window if its
      [number](#int-number) matches.



**Parameters**

- `event` - the incoming event



### `initFrame(JtvRect r)`

Constructs [JtvFrame](JtvFrame.md) for the given bounds.

**Parameters**

- `r` - the frame's bounding rectangle (the window's extent)

**Returns**

a new `JtvFrame`



### `setState(int aState, boolean enable)`

**Overrides:**

`setState(int aState, boolean enable)` in `org.viktor44.jtvision.views.JtvGroup`

Propagates the `sfSelected` state change by toggling
`sfActive` on the window and its frame, and enabling or
disabling the window-management command set (`cmNext`,
`cmPrev`, `cmResize`, `cmClose`, `cmZoom`).

**Parameters**

- `aState` - the state bits to modify
- `enable` - `true` to set; `false` to clear



### `standardScrollBar(int aOptions)`

Creates and inserts a scroll bar aligned to one of the window's
interior edges.



For a vertical scroll bar the bar occupies the rightmost column;
for a horizontal scroll bar it occupies the bottom row. If
`sbHandleKeyboard` is included in `aOptions`, the
scroll bar receives keyboard events via `ofPostProcess`.

**Parameters**

- `aOptions` - `sbVertical` or `sbHorizontal`, optionally
                  OR-ed with `sbHandleKeyboard`

**Returns**

the newly created and inserted `JtvScrollBar`



### `zoom()`

Toggles between the maximum size and the pre-zoom size stored in
[zoomRect](#orgviktor44jtvisioncorejtvrect-zoomrect).



If the window is not already at maximum size, [zoomRect](#orgviktor44jtvisioncorejtvrect-zoomrect) is
updated with the current bounds before the window is expanded to
the maximum. If the window is already at maximum size,
[zoomRect](#orgviktor44jtvisioncorejtvrect-zoomrect) is restored.



### `getFlags()`

Bitmask of `wfXXXX` window-feature flags (`wfMove`, `wfGrow`, `wfClose`, `wfZoom`).



### `setFlags(int flags)`

Bitmask of `wfXXXX` window-feature flags (`wfMove`, `wfGrow`, `wfClose`, `wfZoom`).

**Parameters**

- `flags`


### `getZoomRect()`

The bounding rectangle to restore when the window is un-zoomed.
Updated by [zoom()](#zoom) before maximising.



### `setZoomRect(JtvRect zoomRect)`

The bounding rectangle to restore when the window is un-zoomed.
Updated by [zoom()](#zoom) before maximising.

**Parameters**

- `zoomRect`


### `getNumber()`

The window's sequence number displayed in the title bar (e.g. `:1`).
Use `wnNoNumber` to suppress the number display.



### `setNumber(int number)`

The window's sequence number displayed in the title bar (e.g. `:1`).
Use `wnNoNumber` to suppress the number display.

**Parameters**

- `number`


### `getPaletteIndex()`

Selects the window colour palette: `wpBlueWindow`, `wpCyanWindow`, or `wpGrayWindow`.



### `setPaletteIndex(int paletteIndex)`

Selects the window colour palette: `wpBlueWindow`, `wpCyanWindow`, or `wpGrayWindow`.

**Parameters**

- `paletteIndex`


### `getTitle()`

The text displayed in the window's title bar.
Accessed by the frame via [getTitle(int)](#gettitleint-maxsize).



### `setTitle(String title)`

The text displayed in the window's title bar.
Accessed by the frame via [getTitle(int)](#gettitleint-maxsize).

**Parameters**

- `title`


