---
title: "JtvFrame"
sidebar_label: "JtvFrame"
---

Class JtvFrame
==============

```java
public class JtvFrame
extends JtvView
```


The border and title-bar view drawn around a [JtvWindow](JtvWindow.md).



JtvFrame renders a full-perimeter border using Unicode box-drawing characters.
The border uses single-line characters when the window is inactive or
being dragged, and double-line characters when active. Mixed-weight junction
glyphs (e.g. `\u255f` / `\u2564`) are used where `ofFramed`
subviews intersect the frame.

**Title bar row (row 0)**

The top row contains:

- The close button (`[\u25a0]`) at columns 2–4 when `wfClose`
      is set and the window is active.
- The window title centred in the available space.
- The window number (e.g. `1`) near the right edge when the
      window is numbered.
- The zoom button (`[\u2191]` or `[\u2193]`) at the right edge
      when `wfZoom` is set and the window is active.

**Bottom row**

When the window is active and `wfGrow` is set, drag-corner icons
(`\u2514\u2500` / `\u2500\u2518`) are placed at the bottom-left and bottom-right
corners to indicate the resize handles.

**Mouse handling**

- Clicking the close button (or double-clicking the title bar) sends
      `cmClose`.
- Clicking the zoom button or double-clicking the top row sends
      `cmZoom`.
- Dragging the title bar moves the window (`dmDragMove`).
- Dragging the right resize corner grows from the right
      (`dmDragGrow`); dragging the left corner grows from the left
      (`dmDragGrowLeft`).

**Colour palette**

The five-entry palette `cpFrame` maps to:

1. Inactive frame colour.
2. Inactive title colour.
3. Active frame colour.
4. Active title colour.
5. Drag/resize icon colour.



org.viktor44.jtvision.views.JtvFrame

## Summary

### Constructors

| Visibility | Signature                                           |
| ---------- | --------------------------------------------------- |
| public     | [JtvFrame(JtvRect bounds)](#jtvframejtvrect-bounds) |

### Fields

| Modifiers                | Field name                                            | Type                                  |
| ------------------------ | ----------------------------------------------------- | ------------------------------------- |
| **private static final** | [initFrame](#int-initframe)                           | int[]                                 |
| **private static final** | [frameChars](#char-framechars)                        | char[]                                |
| **private static final** | [closeIcon](#string-closeicon)                        | String                                |
| **private static final** | [zoomIcon](#string-zoomicon)                          | String                                |
| **private static final** | [unZoomIcon](#string-unzoomicon)                      | String                                |
| **private static final** | [dragIcon](#string-dragicon)                          | String                                |
| **private static final** | [dragLeftIcon](#string-draglefticon)                  | String                                |
| **private static final** | [cpFrame](#orgviktor44jtvisioncorejtvpalette-cpframe) | org.viktor44.jtvision.core.JtvPalette |

### Methods

| Modifiers   | Method signature                                                                                                                       | Return type                           |
| ----------- | -------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------- |
| **public**  | [draw()](#draw)                                                                                                                        | void                                  |
| **private** | [frameLine(JtvDrawBuffer frameBuf, int y, int n, JtvColorAttr color)](#framelinejtvdrawbuffer-framebuf-int-y-int-n-jtvcolorattr-color) | void                                  |
| **public**  | [getPalette()](#getpalette)                                                                                                            | org.viktor44.jtvision.core.JtvPalette |
| **public**  | [dragWindow(JtvEvent event, int mode)](#dragwindowjtvevent-event-int-mode)                                                             | void                                  |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                                                              | void                                  |
| **public**  | [setState(int state, boolean enable)](#setstateint-state-boolean-enable)                                                               | void                                  |



## Constructors

### `JtvFrame(JtvRect bounds)`

Constructs a frame that tracks the full width and height of its owner.
Grows with both dimensions (`gfGrowHiX | gfGrowHiY`) and
subscribes to broadcast and mouse-up events.

**Parameters**

- `bounds` - the initial bounding rectangle (the owner window's extent)



## Fields

### `int[] initFrame`

Per-cell direction bitmask table indexed by row/position (0–17).



Entries 0–8 are used for inactive and dragging windows (single-line);
entries 9–17 for active windows (double-line, bit `0x10` set).
The low nibble encodes connection directions:
`0x01`=up, `0x02`=right, `0x04`=down, `0x08`=left.
The `0x10` bit selects the double-line (active) style for the main
frame lines, while subview connector bits use only the low nibble.


### `char[] frameChars`

32-entry glyph lookup table indexed by a 5-bit direction code
(`main-double | left | down | right | up`).



Entries 0–15 produce single-line box-drawing characters; entries 16–31
produce double-line main-frame characters with optional single-line
branches for subview junctions (e.g. `\u255f`, `\u2564`, `\u2562`,
`\u2567`).


### `String closeIcon`

Title bar close button icon (`[\u25a0]`), with tilde hot-key markers.


### `String zoomIcon`

Zoom button icon when the window is not at maximum size (`[\u2191]`).


### `String unZoomIcon`

Zoom button icon when the window is at maximum size (`[\u2193]`).


### `String dragIcon`

Right drag-corner icon (`\u2500\u2518`) placed at the bottom-right.


### `String dragLeftIcon`

Left drag-corner icon (`\u2514\u2500`) placed at the bottom-left.


### `org.viktor44.jtvision.core.JtvPalette cpFrame`

Five-entry colour palette:

1. `1` — inactive frame.
2. `1` — inactive title.
3. `2` — active frame.
4. `2` — active title.
5. `3` — drag/zoom icons (active only).




## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the complete window frame for all rows.



Selects the colour scheme and frame-character set based on whether
the window is active, inactive, or being dragged. Draws the top row
(title bar with optional close/zoom buttons and window number), all
middle side rows, and the bottom row (with optional drag-corner icons).
Subviews with `ofFramed` cause mixed-weight junction glyphs to
appear where their borders meet the frame.



### `frameLine(JtvDrawBuffer frameBuf, int y, int n, JtvColorAttr color)`

Fills one row of the frame draw buffer with the appropriate border
characters and colour attribute.



The base characters come from the `initFrame` direction table
at offsets `n`, `n+1`, and `n+2` (left edge, fill,
right edge). Any sibling subviews with `ofFramed` that intersect
row `y` contribute additional direction bits that cause
mixed-weight junction glyphs to be selected from `frameChars`.

**Parameters**

- `frameBuf` - the draw buffer to write into
- `y` - the frame-local row index
- `n` - the starting offset into `#initFrame`
- `color` - the resolved colour attribute to apply



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the frame colour palette `cpFrame`.

**Returns**

the frame's colour palette



### `dragWindow(JtvEvent event, int mode)`

Initiates an interactive drag/resize of the owning window.
Retrieves the drag limits from the owner's owner, and the min/max
size from the owner's [JtvView.getMinimumSize()](JtvView.md#getminimumsize) and [JtvView.getMaximumSize()](JtvView.md#getmaximumsize),
then calls [JtvView.dragView(JtvEvent, int, JtvRect, JtvPoint, JtvPoint)](JtvView.md#dragviewjtvevent-event-int-mode-jtvrect-limits-jtvpoint-minsize-jtvpoint-maxsize).

**Parameters**

- `event` - the initiating mouse-down event
- `mode` - drag mode flags (`dmDragMove`, `dmDragGrow`,
               or `dmDragGrowLeft`)



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles mouse interactions with the frame:

- Click on close button → sends `cmClose`.
- Click on zoom button or double-click on title row → sends
      `cmZoom`.
- Drag on title row → calls [dragWindow](#dragwindowjtvevent-event-int-mode) with
      `dmDragMove`.
- Drag on right resize corner → calls [dragWindow](#dragwindowjtvevent-event-int-mode) with
      `dmDragGrow`.
- Drag on left resize corner → calls [dragWindow](#dragwindowjtvevent-event-int-mode) with
      `dmDragGrowLeft`.

Also redisplays the frame when the active or dragging state changes.

**Parameters**

- `event` - the incoming event



### `setState(int state, boolean enable)`

Redraws the frame whenever the `sfActive` or `sfDragging`
state changes to switch between single-line and double-line border styles.

**Parameters**

- `state` - the state bits being changed
- `enable` - `true` if the bits are being set



