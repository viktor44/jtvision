---
title: "JtvView"
sidebar_label: "JtvView"
---

Class JtvView
=============

```java
public class JtvView
```


Abstract base class for all JT Vision views.



JtvView represents an empty rectangular region of the screen. It is the
common ancestor of every visible object in the JT Vision hierarchy
and contains all the fundamental screen-management methods and fields
used by dialogs, windows, controls, and the application itself.



Although you can instantiate a plain JtvView for prototyping, it is
usually subclassed. Subclasses override [draw()](#draw) to produce
their visual content, [handleEvent(JtvEvent)](#handleeventjtvevent-event) to respond to
events, and [getPalette()](#getpalette) to supply a colour palette.

**Coordinate system**

Every view has an [origin](#orgviktor44jtvisioncorejtvpoint-origin) (its top-left corner expressed in
the owner's coordinate system) and a [size](#orgviktor44jtvisioncorejtvpoint-size) (its width and
height). [getBounds()](#getbounds) returns the bounding rectangle;
[getExtent()](#getextent) returns the same rectangle translated to
`(0,0)`.

**State flags**

The [state](#int-state) field is a bitmask of `sfXXXX` constants
(`sfVisible`, `sfFocused`, `sfSelected`, etc.).
Use [setState(int, boolean)](#setstateint-astate-boolean-enable) to change individual bits;
the method automatically triggers redraws and broadcasts.

**Event routing**

Events flow through [handleEvent(JtvEvent)](#handleeventjtvevent-event). The base
implementation handles `evMouseDown` focus transfers.
Subclasses call `super.handleEvent(event)` first, then inspect
and consume remaining events.

**Commands**

The global [curCommandSet](#orgviktor44jtvisioncorejtvcommandset-curcommandset) tracks which commands are currently
enabled. Window-management commands (`cmZoom`, `cmClose`,
`cmResize`, `cmNext`, `cmPrev`) are disabled by
default and enabled only by the active window.

org.viktor44.jtvision.views.JtvView

## Summary

### Constructors

| Visibility | Signature                                         |
| ---------- | ------------------------------------------------- |
| public     | [JtvView(JtvRect bounds)](#jtvviewjtvrect-bounds) |

### Fields

| Modifiers               | Field name                                                           | Type                                     |
| ----------------------- | -------------------------------------------------------------------- | ---------------------------------------- |
| **public static final** | [normalSelect](#int-normalselect)                                    | int                                      |
| **public static final** | [enterSelect](#int-enterselect)                                      | int                                      |
| **public static final** | [leaveSelect](#int-leaveselect)                                      | int                                      |
| **public static final** | [phFocused](#int-phfocused)                                          | int                                      |
| **public static final** | [phPreProcess](#int-phpreprocess)                                    | int                                      |
| **public static final** | [phPostProcess](#int-phpostprocess)                                  | int                                      |
| **public static**       | [shadowSize](#orgviktor44jtvisioncorejtvpoint-shadowsize)            | org.viktor44.jtvision.core.JtvPoint      |
| **public static**       | [shadowAttr](#int-shadowattr)                                        | int                                      |
| **public static**       | [showMarkers](#boolean-showmarkers)                                  | boolean                                  |
| **public static**       | [errorAttr](#orgviktor44jtvisioncorejtvcolorattr-errorattr)          | org.viktor44.jtvision.core.JtvColorAttr  |
| **public static**       | [commandSetChanged](#boolean-commandsetchanged)                      | boolean                                  |
| **public static**       | [curCommandSet](#orgviktor44jtvisioncorejtvcommandset-curcommandset) | org.viktor44.jtvision.core.JtvCommandSet |
| **protected**           | [next](#orgviktor44jtvisionviewsjtvview-next)                        | org.viktor44.jtvision.views.JtvView      |
| **protected**           | [owner](#orgviktor44jtvisionviewsjtvgroup-owner)                     | org.viktor44.jtvision.views.JtvGroup     |
| **protected**           | [origin](#orgviktor44jtvisioncorejtvpoint-origin)                    | org.viktor44.jtvision.core.JtvPoint      |
| **protected**           | [size](#orgviktor44jtvisioncorejtvpoint-size)                        | org.viktor44.jtvision.core.JtvPoint      |
| **protected**           | [cursor](#orgviktor44jtvisioncorejtvpoint-cursor)                    | org.viktor44.jtvision.core.JtvPoint      |
| **protected**           | [resizeBalance](#orgviktor44jtvisioncorejtvpoint-resizebalance)      | org.viktor44.jtvision.core.JtvPoint      |
| **protected**           | [state](#int-state)                                                  | int                                      |
| **private**             | [options](#int-options)                                              | int                                      |
| **protected**           | [eventMask](#int-eventmask)                                          | int                                      |
| **protected**           | [growMode](#int-growmode)                                            | int                                      |
| **protected**           | [dragMode](#int-dragmode)                                            | int                                      |
| **protected**           | [helpCtx](#int-helpctx)                                              | int                                      |
| **private**             | [minimumSize](#orgviktor44jtvisioncorejtvpoint-minimumsize)          | org.viktor44.jtvision.core.JtvPoint      |

### Methods

| Modifiers            | Method signature                                                                                                                                                                                                                 | Return type                              |
| -------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------- |
| **private static**   | [initCommands()](#initcommands)                                                                                                                                                                                                  | org.viktor44.jtvision.core.JtvCommandSet |
| **public**           | [enableOptions(int[] option)](#enableoptionsint-option)                                                                                                                                                                          | void                                     |
| **public**           | [isOptionEnabled(int option)](#isoptionenabledint-option)                                                                                                                                                                        | boolean                                  |
| **public**           | [disableOptions(int[] option)](#disableoptionsint-option)                                                                                                                                                                        | void                                     |
| **public**           | [isOptionDisabled(int option)](#isoptiondisabledint-option)                                                                                                                                                                      | boolean                                  |
| **public**           | [awaken()](#awaken)                                                                                                                                                                                                              | void                                     |
| **public**           | [blockCursor()](#blockcursor)                                                                                                                                                                                                    | void                                     |
| **public**           | [calcBounds(JtvRect bounds, JtvPoint delta)](#calcboundsjtvrect-bounds-jtvpoint-delta)                                                                                                                                           | void                                     |
| **private**          | [grow(int s, int d, int i)](#growint-s-int-d-int-i)                                                                                                                                                                              | int                                      |
| **private**          | [balancedRange(int val, int min, int max, boolean useX)](#balancedrangeint-val-int-min-int-max-boolean-usex)                                                                                                                     | int                                      |
| **public**           | [changeBounds(JtvRect bounds)](#changeboundsjtvrect-bounds)                                                                                                                                                                      | void                                     |
| **public**           | [clearEvent(JtvEvent event)](#cleareventjtvevent-event)                                                                                                                                                                          | void                                     |
| **public static**    | [commandEnabled(int command)](#commandenabledint-command)                                                                                                                                                                        | boolean                                  |
| **public static**    | [disableCommands(JtvCommandSet commands)](#disablecommandsjtvcommandset-commands)                                                                                                                                                | void                                     |
| **public static**    | [disableCommand(int command)](#disablecommandint-command)                                                                                                                                                                        | void                                     |
| **public**           | [draw()](#draw)                                                                                                                                                                                                                  | void                                     |
| **public**           | [drawCursor()](#drawcursor)                                                                                                                                                                                                      | void                                     |
| **public**           | [drawHide(JtvView lastView)](#drawhidejtvview-lastview)                                                                                                                                                                          | void                                     |
| **public**           | [drawShow(JtvView lastView)](#drawshowjtvview-lastview)                                                                                                                                                                          | void                                     |
| **public**           | [drawUnderRect(JtvRect r, JtvView lastView)](#drawunderrectjtvrect-r-jtvview-lastview)                                                                                                                                           | void                                     |
| **public**           | [drawUnderView(boolean doShadow, JtvView lastView)](#drawunderviewboolean-doshadow-jtvview-lastview)                                                                                                                             | void                                     |
| **public**           | [drawView()](#drawview)                                                                                                                                                                                                          | void                                     |
| **public static**    | [enableCommands(JtvCommandSet commands)](#enablecommandsjtvcommandset-commands)                                                                                                                                                  | void                                     |
| **public static**    | [enableCommand(int command)](#enablecommandint-command)                                                                                                                                                                          | void                                     |
| **public**           | [endModal(int command)](#endmodalint-command)                                                                                                                                                                                    | void                                     |
| **public**           | [eventAvail()](#eventavail)                                                                                                                                                                                                      | boolean                                  |
| **public**           | [execute()](#execute)                                                                                                                                                                                                            | int                                      |
| **public**           | [exposed()](#exposed)                                                                                                                                                                                                            | boolean                                  |
| **private**          | [exposedRow(int y, int x1, int x2)](#exposedrowint-y-int-x1-int-x2)                                                                                                                                                              | boolean                                  |
| **public**           | [focus()](#focus)                                                                                                                                                                                                                | boolean                                  |
| **public**           | [getBounds()](#getbounds)                                                                                                                                                                                                        | org.viktor44.jtvision.core.JtvRect       |
| **public**           | [getClipRect()](#getcliprect)                                                                                                                                                                                                    | org.viktor44.jtvision.core.JtvRect       |
| **public**           | [getColor(int color)](#getcolorint-color)                                                                                                                                                                                        | org.viktor44.jtvision.core.JtvColorAttr  |
| **public static**    | [getCommands(JtvCommandSet commands)](#getcommandsjtvcommandset-commands)                                                                                                                                                        | void                                     |
| **public**           | [getDataTo(T rec)](#getdatatot-rec)                                                                                                                                                                                              | void                                     |
| **public**           | [setDataFrom(Object rec)](#setdatafromobject-rec)                                                                                                                                                                                | void                                     |
| **public**           | [getEvent(JtvEvent event)](#geteventjtvevent-event)                                                                                                                                                                              | org.viktor44.jtvision.core.JtvEvent      |
| **public**           | [getExtent()](#getextent)                                                                                                                                                                                                        | org.viktor44.jtvision.core.JtvRect       |
| **public**           | [getHelpCtx()](#gethelpctx)                                                                                                                                                                                                      | int                                      |
| **public**           | [setHelpCtx(int helpCtx)](#sethelpctxint-helpctx)                                                                                                                                                                                | void                                     |
| **public**           | [getPalette()](#getpalette)                                                                                                                                                                                                      | org.viktor44.jtvision.core.JtvPalette    |
| **public**           | [hasState(int aState)](#hasstateint-astate)                                                                                                                                                                                      | boolean                                  |
| **public**           | [isInState(int value)](#isinstateint-value)                                                                                                                                                                                      | boolean                                  |
| **public**           | [isNotInState(int value)](#isnotinstateint-value)                                                                                                                                                                                | boolean                                  |
| **public**           | [growTo(int x, int y)](#growtoint-x-int-y)                                                                                                                                                                                       | void                                     |
| **public**           | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                                                                                                                                                        | void                                     |
| **public**           | [hide()](#hide)                                                                                                                                                                                                                  | void                                     |
| **public**           | [hideCursor()](#hidecursor)                                                                                                                                                                                                      | void                                     |
| **public**           | [keyEvent(JtvEvent event)](#keyeventjtvevent-event)                                                                                                                                                                              | void                                     |
| **public**           | [locate(JtvRect bounds)](#locatejtvrect-bounds)                                                                                                                                                                                  | void                                     |
| **public**           | [makeFirst()](#makefirst)                                                                                                                                                                                                        | void                                     |
| **public**           | [makeGlobal(JtvPoint source)](#makeglobaljtvpoint-source)                                                                                                                                                                        | org.viktor44.jtvision.core.JtvPoint      |
| **public**           | [makeLocal(JtvPoint source)](#makelocaljtvpoint-source)                                                                                                                                                                          | org.viktor44.jtvision.core.JtvPoint      |
| **protected**        | [mapColor(int index)](#mapcolorint-index)                                                                                                                                                                                        | org.viktor44.jtvision.core.JtvColorAttr  |
| **public**           | [mouseEvent(JtvEvent event, int mask)](#mouseeventjtvevent-event-int-mask)                                                                                                                                                       | boolean                                  |
| **public**           | [mouseInView(JtvPoint mouse)](#mouseinviewjtvpoint-mouse)                                                                                                                                                                        | boolean                                  |
| **public**           | [moveTo(int x, int y)](#movetoint-x-int-y)                                                                                                                                                                                       | void                                     |
| **public**           | [nextView()](#nextview)                                                                                                                                                                                                          | org.viktor44.jtvision.views.JtvView      |
| **public**           | [normalCursor()](#normalcursor)                                                                                                                                                                                                  | void                                     |
| **public**           | [prev()](#prev)                                                                                                                                                                                                                  | org.viktor44.jtvision.views.JtvView      |
| **public**           | [prevView()](#prevview)                                                                                                                                                                                                          | org.viktor44.jtvision.views.JtvView      |
| **public**           | [putEvent(JtvEvent event)](#puteventjtvevent-event)                                                                                                                                                                              | void                                     |
| **public**           | [putInFrontOf(JtvView target)](#putinfrontofjtvview-target)                                                                                                                                                                      | void                                     |
| **public**           | [resetCursor()](#resetcursor)                                                                                                                                                                                                    | void                                     |
| **public**           | [select()](#select)                                                                                                                                                                                                              | void                                     |
| **public**           | [setBounds(JtvRect bounds)](#setboundsjtvrect-bounds)                                                                                                                                                                            | void                                     |
| **public static**    | [setCmdState(JtvCommandSet commands, boolean enable)](#setcmdstatejtvcommandset-commands-boolean-enable)                                                                                                                         | void                                     |
| **public static**    | [setCommands(JtvCommandSet commands)](#setcommandsjtvcommandset-commands)                                                                                                                                                        | void                                     |
| **public**           | [setCursor(int x, int y)](#setcursorint-x-int-y)                                                                                                                                                                                 | void                                     |
| **public**           | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable)                                                                                                                                                       | void                                     |
| **public**           | [show()](#show)                                                                                                                                                                                                                  | void                                     |
| **public**           | [showCursor()](#showcursor)                                                                                                                                                                                                      | void                                     |
| **public**           | [getMaximumSize()](#getmaximumsize)                                                                                                                                                                                              | org.viktor44.jtvision.core.JtvPoint      |
| **public**           | [topView()](#topview)                                                                                                                                                                                                            | org.viktor44.jtvision.views.JtvView      |
| **public**           | [valid(int command)](#validint-command)                                                                                                                                                                                          | boolean                                  |
| **public**           | [containsMouse(JtvEvent event)](#containsmousejtvevent-event)                                                                                                                                                                    | boolean                                  |
| **public**           | [writeBuf(int x, int y, int w, int h, JtvDrawBuffer b)](#writebufint-x-int-y-int-w-int-h-jtvdrawbuffer-b)                                                                                                                        | void                                     |
| **public**           | [writeBuf(int x, int y, int w, int h, org.viktor44.jtvision.core.JtvScreenCell[] b)](#writebufint-x-int-y-int-w-int-h-orgviktor44jtvisioncorejtvscreencell-b)                                                                    | void                                     |
| **public**           | [writeLine(int x, int y, int w, int h, JtvDrawBuffer b)](#writelineint-x-int-y-int-w-int-h-jtvdrawbuffer-b)                                                                                                                      | void                                     |
| **public**           | [writeChar(int x, int y, char c, int color, int count)](#writecharint-x-int-y-char-c-int-color-int-count)                                                                                                                        | void                                     |
| **public**           | [writeStr(int x, int y, String str, int color)](#writestrint-x-int-y-string-str-int-color)                                                                                                                                       | void                                     |
| **protected**        | [writeView(int x, int y, int count, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset)](#writeviewint-x-int-y-int-count-orgviktor44jtvisioncorejtvscreencell-buf-int-bufoffset)                                      | void                                     |
| **public static**    | [applyShadow(JtvColorAttr attr)](#applyshadowjtvcolorattr-attr)                                                                                                                                                                  | org.viktor44.jtvision.core.JtvColorAttr  |
| **protected**        | [writeToOwner(int x1, int x2, int y, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset, boolean inShadow)](#writetoownerint-x1-int-x2-int-y-orgviktor44jtvisioncorejtvscreencell-buf-int-bufoffset-boolean-inshadow) | void                                     |
| **public**           | [moveGrow(JtvPoint p, JtvPoint s, JtvRect limits, JtvPoint minSize, JtvPoint maxSize, int mode)](#movegrowjtvpoint-p-jtvpoint-s-jtvrect-limits-jtvpoint-minsize-jtvpoint-maxsize-int-mode)                                       | void                                     |
| **public**           | [dragView(JtvEvent event, int mode, JtvRect limits, JtvPoint minSize, JtvPoint maxSize)](#dragviewjtvevent-event-int-mode-jtvrect-limits-jtvpoint-minsize-jtvpoint-maxsize)                                                      | void                                     |
| **public static**    | [message(JtvView receiver, int what, int command, Object infoPtr)](#messagejtvview-receiver-int-what-int-command-object-infoptr)                                                                                                 | Object                                   |
| **protected static** | [range(int val, int min, int max)](#rangeint-val-int-min-int-max)                                                                                                                                                                | int                                      |
| **public**           | [getSize()](#getsize)                                                                                                                                                                                                            | org.viktor44.jtvision.core.JtvPoint      |
| **public**           | [getCursor()](#getcursor)                                                                                                                                                                                                        | org.viktor44.jtvision.core.JtvPoint      |
| **public**           | [getState()](#getstate)                                                                                                                                                                                                          | int                                      |
| **public**           | [getOptions()](#getoptions)                                                                                                                                                                                                      | int                                      |
| **protected**        | [setOptions(int options)](#setoptionsint-options)                                                                                                                                                                                | void                                     |
| **public**           | [getEventMask()](#geteventmask)                                                                                                                                                                                                  | int                                      |
| **public**           | [setEventMask(int eventMask)](#seteventmaskint-eventmask)                                                                                                                                                                        | void                                     |
| **public**           | [getGrowMode()](#getgrowmode)                                                                                                                                                                                                    | int                                      |
| **public**           | [setGrowMode(int growMode)](#setgrowmodeint-growmode)                                                                                                                                                                            | void                                     |
| **public**           | [getMinimumSize()](#getminimumsize)                                                                                                                                                                                              | org.viktor44.jtvision.core.JtvPoint      |
| **public**           | [setMinimumSize(JtvPoint minimumSize)](#setminimumsizejtvpoint-minimumsize)                                                                                                                                                      | void                                     |



## Constructors

### `JtvView(JtvRect bounds)`

Constructs a view with the given bounding rectangle.



Initialises [state](#int-state) to `sfVisible`, [eventMask](#int-eventmask)
to `evMouseDown | evKeyDown | evCommand`, [dragMode](#int-dragmode)
to `dmLimitLoY`, and [helpCtx](#int-helpctx) to `hcNoContext`.

**Parameters**

- `bounds` - the initial bounding rectangle in the owner's coordinate system



## Fields

### `int normalSelect`

Selection mode indicating a standard selection change.


### `int enterSelect`

Selection mode used when a view gains focus by entering it.


### `int leaveSelect`

Selection mode used when a view loses focus by leaving it.


### `int phFocused`

Event routing phase: deliver to the focused subview.


### `int phPreProcess`

Event routing phase: deliver to subviews with `ofPreProcess`
before routing to the focused view.


### `int phPostProcess`

Event routing phase: deliver to subviews with `ofPostProcess`
after routing to the focused view.


### `org.viktor44.jtvision.core.JtvPoint shadowSize`

The width and height of the shadow cast by views that have the
`sfShadow` state bit set. Defaults to `(2, 1)`.


### `int shadowAttr`

The colour attribute used to render shadows. Low nibble is the
shadow foreground colour; high nibble is the shadow background.
Defaults to `0x08` (dark gray on black).


### `boolean showMarkers`

When `true`, the list viewer draws side markers (`»` / `«`)
beside selected items. Defaults to `false`.


### `org.viktor44.jtvision.core.JtvColorAttr errorAttr`

The colour attribute used to highlight palette mapping errors.
Defaults to `0xCF` (white on red).


### `boolean commandSetChanged`

Set to `true` whenever [curCommandSet](#orgviktor44jtvisioncorejtvcommandset-curcommandset) changes, allowing
the status line and menus to redraw only when necessary.


### `org.viktor44.jtvision.core.JtvCommandSet curCommandSet`

The application-wide set of currently enabled commands.
Initialised with all 256 commands enabled except the four
window-management commands (`cmZoom`, `cmClose`,
`cmResize`, `cmNext`, `cmPrev`).


### `org.viktor44.jtvision.views.JtvView next`

The next view in the owner's circular singly-linked list.
Maintained by [JtvGroup.insertView](JtvGroup.md#insertviewjtvview-p-jtvview-target) and [JtvGroup.removeView](JtvGroup.md#removeviewjtvview-p).


### `org.viktor44.jtvision.views.JtvGroup owner`

The [JtvGroup](JtvGroup.md) that owns this view, or `null` if the view
is not yet inserted into any group.


### `org.viktor44.jtvision.core.JtvPoint origin`

The top-left corner of this view in the owner group's local
coordinate system. Set by [setBounds(JtvRect)](#setboundsjtvrect-bounds).


### `org.viktor44.jtvision.core.JtvPoint size`

The width and height of this view. Together with [origin](#orgviktor44jtvisioncorejtvpoint-origin),
it defines the bounding rectangle returned by [getBounds()](#getbounds).


### `org.viktor44.jtvision.core.JtvPoint cursor`

The cursor position within the view's local coordinate system.
Updated by [setCursor(int, int)](#setcursorint-x-int-y) and [resetCursor()](#resetcursor).


### `org.viktor44.jtvision.core.JtvPoint resizeBalance`

Accumulated sub-pixel balance used by `balancedRange` to
distribute fractional pixels when growing a view relative to its
owner. Prevents drift when the owner is resized many times.


### `int state`

Bitmask of `sfXXXX` state constants describing the current
visibility, focus, selection, and dragging status of the view.
Modify only through [setState(int, boolean)](#setstateint-astate-boolean-enable).


### `int options`

Bitmask of `ofXXXX` option constants controlling selectable,
framed, centered, buffered, and other optional behaviour.


### `int eventMask`

Event-type bitmask that determines which event categories this view
wants to receive (e.g. `evMouseDown`, `evKeyDown`,
`evCommand`). Set to the union of event types handled by
[handleEvent(JtvEvent)](#handleeventjtvevent-event).


### `int growMode`

Grow-mode bitmask of `gfXXXX` constants controlling how this
view resizes relative to its owner (e.g. `gfGrowHiX` to keep
the right edge pinned to the owner's right side).


### `int dragMode`

Drag-mode bitmask of `dmXXXX` constants constraining where
the view can be dragged or grown. Defaults to `dmLimitLoY`.


### `int helpCtx`

The help-context identifier for this view. The default value is
`hcNoContext`. Accessed via [getHelpCtx()](#gethelpctx) which returns
`hcDragging` when the view is being dragged.


### `org.viktor44.jtvision.core.JtvPoint minimumSize`

The minimum allowed window size


## Methods

### `initCommands()`

Builds the initial [curCommandSet](#orgviktor44jtvisioncorejtvcommandset-curcommandset) with window commands disabled.



### `enableOptions(int[] option)`

Enables the given option flags by OR-ing each `ofXXXX` bit
into `options`.

**Parameters**

- `option` - one or more `ofXXXX` option constants



### `isOptionEnabled(int option)`

Tests whether any bit of the given option flag is set in
`options`.

**Parameters**

- `option` - the `ofXXXX` option constant to test

**Returns**

`true` if the option is enabled



### `disableOptions(int[] option)`

Disables the given option flags by clearing each `ofXXXX` bit
from `options`.

**Parameters**

- `option` - one or more `ofXXXX` option constants



### `isOptionDisabled(int option)`

Tests whether no bit of the given option flag is set in
`options`.

**Parameters**

- `option` - the `ofXXXX` option constant to test

**Returns**

`true` if the option is disabled



### `awaken()`

Called after the view is inserted into its owner group and the group
is fully initialised. Subclasses can override to perform deferred
initialisation. The base implementation does nothing.



### `blockCursor()`

Switches the cursor to insert (block) mode by setting
`sfCursorIns` in the state.



### `calcBounds(JtvRect bounds, JtvPoint delta)`

Computes the new bounding rectangle for this view when its owner is
resized by `delta` columns/rows.



Each edge is moved or held fixed according to the [growMode](#int-growmode)
flags. After applying the grow rules the result is clamped to the
view's size limits via [getMinimumSize()](#getminimumsize) and [getMaximumSize()](#getmaximumsize).

**Parameters**

- `bounds` - receives the newly computed bounding rectangle
- `delta` - the change in the owner's size (positive = owner grew)



### `grow(int s, int d, int i)`

Applies grow-mode scaling or translation to a single coordinate.

**Parameters**

- `s`
- `d`
- `i`


### `balancedRange(int val, int min, int max, boolean useX)`

Clamps `val` to `[min, max]` while accumulating the
fractional remainder in [resizeBalance](#orgviktor44jtvisioncorejtvpoint-resizebalance) to avoid drift over
repeated owner resizes.

**Parameters**

- `val`
- `min`
- `max`
- `useX`


### `changeBounds(JtvRect bounds)`

Sets the view's bounding rectangle and redraws the view.
Called by [JtvGroup](JtvGroup.md) when the owner is resized.

**Parameters**

- `bounds` - the new bounding rectangle



### `clearEvent(JtvEvent event)`

Marks `event` as consumed by setting its type to
`evNothing` and recording `this` as the last handler.

**Parameters**

- `event` - the event to consume



### `commandEnabled(int command)`

Returns `true` if `command` is currently enabled.
Commands above 255 are always considered enabled.

**Parameters**

- `command` - the command code to test

**Returns**

`true` if the command is enabled



### `disableCommands(JtvCommandSet commands)`

Disables every command in `commands` from [curCommandSet](#orgviktor44jtvisioncorejtvcommandset-curcommandset)
and sets [commandSetChanged](#boolean-commandsetchanged) if any were previously enabled.

**Parameters**

- `commands` - the set of commands to disable



### `disableCommand(int command)`

Disables a single command in [curCommandSet](#orgviktor44jtvisioncorejtvcommandset-curcommandset) and sets
[commandSetChanged](#boolean-commandsetchanged) if it was previously enabled.

**Parameters**

- `command` - the command code to disable



### `draw()`

Draws the view's contents on screen.



The base implementation fills the view's rectangle with a single
space character in the view's default colour (palette entry 1).
Subclasses override this method to provide their own visual output.



### `drawCursor()`

Updates the hardware cursor position if the view is focused and the
cursor is visible. Called at the end of [drawView()](#drawview).



### `drawHide(JtvView lastView)`

Redraws the screen area that was covered by this view before it was
hidden. Also draws the cursor if visible.

**Parameters**

- `lastView` - the sibling view behind which to stop redrawing, or
                  `null` to redraw all siblings



### `drawShow(JtvView lastView)`

Draws this view and, if it has a shadow, the area covered by the
shadow.

**Parameters**

- `lastView` - the sibling view behind which to stop drawing shadows,
                  or `null`



### `drawUnderRect(JtvRect r, JtvView lastView)`

Asks the owner to redraw all views in the sibling list that overlap
the rectangle `r`, starting after this view.

**Parameters**

- `r` - the rectangle to redraw
- `lastView` - the sibling view at which to stop, or `null`



### `drawUnderView(boolean doShadow, JtvView lastView)`

Redraws the screen area beneath this view (including its shadow area
if `doShadow` is `true` or the view carries `sfShadow`).

**Parameters**

- `doShadow` - `true` to include the shadow region
- `lastView` - the sibling view at which to stop, or `null`



### `drawView()`

Redraws this view only if it is currently exposed (visible and not
completely hidden by other views). Also updates the cursor.



### `enableCommands(JtvCommandSet commands)`

Enables every command in `commands` in [curCommandSet](#orgviktor44jtvisioncorejtvcommandset-curcommandset)
and sets [commandSetChanged](#boolean-commandsetchanged) if any were previously disabled.

**Parameters**

- `commands` - the set of commands to enable



### `enableCommand(int command)`

Enables a single command in [curCommandSet](#orgviktor44jtvisioncorejtvcommandset-curcommandset) and sets
[commandSetChanged](#boolean-commandsetchanged) if it was previously disabled.

**Parameters**

- `command` - the command code to enable



### `endModal(int command)`

Ends the current modal state by delegating to the topmost modal view.
The top-level modal view records `command` as its return value.

**Parameters**

- `command` - the value to return from `JtvGroup#execute()`



### `eventAvail()`

Returns `true` if the event queue is non-empty without
consuming the pending event (it is re-enqueued immediately).

**Returns**

`true` if at least one event is waiting



### `execute()`

Runs the view's modal event loop and returns the exit command.
The base implementation always returns `cmCancel`.
[JtvGroup](JtvGroup.md) overrides this with a full event loop.

**Returns**

the command that caused the modal loop to end



### `exposed()`

Returns `true` if any row of this view is currently visible
on screen (not fully obscured by other views and exposed by its owner).

**Returns**

`true` if the view contributes visible pixels to the screen



### `exposedRow(int y, int x1, int x2)`

Checks whether a specific row segment of the view is visible.

**Parameters**

- `y`
- `x1`
- `x2`


### `focus()`

Requests that this view become the focused (selected) view in its
owner, first making the owner group focused recursively up the chain.



If the currently focused view has `ofValidate` set and
`valid(cmReleasedFocus)` returns `false`, focus is
not transferred.

**Returns**

`true` if focus was successfully acquired



### `getBounds()`

Returns the view's bounding rectangle in the owner's coordinate system.

**Returns**

the bounding rectangle (`origin` to `origin + size`)



### `getClipRect()`

Returns the portion of the view's extent that lies within the
owner's current clip rectangle, translated to the view's own
coordinate system.

**Returns**

the effective clip rectangle for drawing



### `getColor(int color)`

Resolves a single colour-palette index through the palette chain and
returns the final colour attribute.

**Parameters**

- `color` - the palette index (only the low 8 bits are used)

**Returns**

the resolved colour attribute



### `getCommands(JtvCommandSet commands)`

Copies the current global command set into `commands`.

**Parameters**

- `commands` - receives a copy of `#curCommandSet`



### `getDataTo(T rec)`

No-op data-export stub. Subclasses override to write their data
into `rec`.

**Parameters**

- `rec` - the destination data record



### `setDataFrom(Object rec)`

No-op data-import stub. Subclasses override to read their data
from `rec`.

**Parameters**

- `rec` - the source data record



### `getEvent(JtvEvent event)`

Retrieves the next pending event by delegating to the owner group.
Returns an unchanged event (with type `evNothing`) if there
is no owner.

**Parameters**

- `event` - the event object to populate

**Returns**

the same `event` reference, populated with the next event



### `getExtent()`

Returns the view's extent rectangle — a rectangle from `(0,0)`
to [size](#orgviktor44jtvisioncorejtvpoint-size), i.e. the bounds translated to the view's own origin.

**Returns**

the extent rectangle



### `getHelpCtx()`

Returns the effective help-context ID for this view. Returns
`hcDragging` while the view is being dragged, otherwise
returns [helpCtx](#int-helpctx).

**Returns**

the current help-context ID



### `setHelpCtx(int helpCtx)`

Sets the help-context ID for this view.

**Parameters**

- `helpCtx` - the new help-context ID



### `getPalette()`

Returns the colour palette for this view. The base implementation
returns an empty palette (direct colour mapping — no indirection).
Subclasses override this to provide their own palette entries.

**Returns**

the view's colour palette



### `hasState(int aState)`

Tests whether all bits in `aState` are currently set in
[state](#int-state).

**Parameters**

- `aState` - the state bitmask to test

**Returns**

`true` if all specified bits are set



### `isInState(int value)`

Tests whether any bit of the given state flag is set in
[state](#int-state).

**Parameters**

- `value` - the `sfXXXX` state constant to test

**Returns**

`true` if the state bit is set



### `isNotInState(int value)`

Tests whether no bit of the given state flag is set in
[state](#int-state).

**Parameters**

- `value` - the `sfXXXX` state constant to test

**Returns**

`true` if the state bit is clear



### `growTo(int x, int y)`

Resizes the view to exactly `x` columns wide and `y`
rows tall, keeping the current top-left origin.

**Parameters**

- `x` - the new width in columns
- `y` - the new height in rows



### `handleEvent(JtvEvent event)`

Handles an event for this view.



The base implementation handles `evMouseDown`: if the view is
not selected or disabled and has `ofSelectable` set, it calls
[focus()](#focus). Unless `ofFirstClick` is set, the mouse-down
event is consumed so it does not also activate the view's normal
action.

**Parameters**

- `event` - the incoming event (modified in place to consume it)



### `hide()`

Makes the view invisible by clearing `sfVisible`.
If the view is already invisible this is a no-op.



### `hideCursor()`

Hides the hardware cursor by clearing `sfCursorVis`.



### `keyEvent(JtvEvent event)`

Blocks until a keyboard event arrives, storing it in `event`.
Any non-keyboard events are discarded.

**Parameters**

- `event` - the event object that receives the keyboard event



### `locate(JtvRect bounds)`

Moves and/or resizes the view to the given bounds, clamped to the
size limits returned by [getMinimumSize()](#getminimumsize) and [getMaximumSize()](#getmaximumsize).
Triggers a redraw of the area previously occupied by the view if
the bounds actually changed.

**Parameters**

- `bounds` - the desired new bounding rectangle



### `makeFirst()`

Moves this view to the front of the owner group (z-order), making
it the first view in the circular linked list.



### `makeGlobal(JtvPoint source)`

Converts `source` from local view coordinates to global screen
coordinates by walking up the owner chain adding each group's origin.

**Parameters**

- `source` - the point in this view's local coordinate system

**Returns**

the corresponding point in screen coordinates



### `makeLocal(JtvPoint source)`

Converts `source` from global screen coordinates to local view
coordinates by walking up the owner chain subtracting each origin.

**Parameters**

- `source` - the point in screen coordinates

**Returns**

the corresponding point in this view's local coordinate system



### `mapColor(int index)`

Maps palette entry `index` through the view's own palette and,
if the result refers to another palette entry, continues up the
owner chain until a final colour attribute is resolved.
Returns [errorAttr](#orgviktor44jtvisioncorejtvcolorattr-errorattr) if the index is out of range or maps
to zero (the "transparent" sentinel).

**Parameters**

- `index` - the one-based palette index to resolve

**Returns**

the final colour attribute



### `mouseEvent(JtvEvent event, int mask)`

Polls for mouse events, blocking until one of the events matching
`mask` (or a mouse-up) arrives.

**Parameters**

- `event` - the event object to populate
- `mask` - the event type bitmask to wait for

**Returns**

`true` if the loop ended on a masked event;
        `false` if it ended on a mouse-up



### `mouseInView(JtvPoint mouse)`

Returns `true` if `mouse` (in global screen coordinates)
lies within this view's visible bounds.

**Parameters**

- `mouse` - a point in global screen coordinates

**Returns**

`true` if the point is inside the view's extent



### `moveTo(int x, int y)`

Moves the view so its top-left corner is at `(x, y)` in the
owner's coordinate system. The view's size is unchanged.

**Parameters**

- `x` - the new left column
- `y` - the new top row



### `nextView()`

Returns the next visible sibling of this view in the owner's
z-order, or `null` if this view is the bottommost sibling.

**Returns**

the next sibling, or `null`



### `normalCursor()`

Switches the cursor to normal (underline) mode by clearing
`sfCursorIns`.



### `prev()`

Returns the previous sibling in the owner's circular linked list
by walking the list until the view whose `next` is `this`
is found.

**Returns**

the previous sibling (never `null` if owner is non-null)



### `prevView()`

Returns the previous visible sibling of this view in z-order, or
`null` if this view is the topmost sibling.

**Returns**

the previous sibling, or `null`



### `putEvent(JtvEvent event)`

Places `event` into the owner group's event queue.
Has no effect if this view has no owner.

**Parameters**

- `event` - the event to inject



### `putInFrontOf(JtvView target)`

Moves this view immediately in front of `target` in the owner's
z-order, triggering appropriate show/hide redraws.
Has no effect if `target` is this view, the view directly behind
it, or belongs to a different owner.

**Parameters**

- `target` - the sibling that this view should be placed in front of,
                or `null` to move to the back



### `resetCursor()`

Repositions the hardware cursor to this view's [cursor](#orgviktor44jtvisioncorejtvpoint-cursor) point
(in screen coordinates) when the view is focused and the cursor is
visible. Hides the hardware cursor otherwise.



### `select()`

Makes this view the selected view in its owner group.



If `ofTopSelect` is set, the view is also brought to the front
of the z-order via [makeFirst()](#makefirst).



### `setBounds(JtvRect bounds)`

Updates [origin](#orgviktor44jtvisioncorejtvpoint-origin) and [size](#orgviktor44jtvisioncorejtvpoint-size) from the given bounding
rectangle without triggering a redraw.

**Parameters**

- `bounds` - the new bounding rectangle



### `setCmdState(JtvCommandSet commands, boolean enable)`

Enables or disables all commands in `commands` according to
`enable`, calling [enableCommands](#enablecommandsjtvcommandset-commands) or
[disableCommands](#disablecommandsjtvcommandset-commands) accordingly.

**Parameters**

- `commands` - the command set to modify
- `enable` - `true` to enable, `false` to disable



### `setCommands(JtvCommandSet commands)`

Replaces the entire global command set with `commands` and
sets [commandSetChanged](#boolean-commandsetchanged) if the set is different.

**Parameters**

- `commands` - the new command set



### `setCursor(int x, int y)`

Moves the cursor to position `(x, y)` within the view and
updates the screen cursor via [drawCursor()](#drawcursor).

**Parameters**

- `x` - the cursor column in view-local coordinates
- `y` - the cursor row in view-local coordinates



### `setState(int aState, boolean enable)`

Sets or clears one or more state bits and triggers the corresponding
side effects:

- `sfVisible` — shows or hides the view and resets focus.
- `sfCursorVis` / `sfCursorIns` — repositions the cursor.
- `sfShadow` — redraws the shadow area.
- `sfFocused` — moves the hardware cursor and broadcasts
      `cmReceivedFocus` / `cmReleasedFocus`.



**Parameters**

- `state` - the state bits to modify
- `enable` - `true` to set the bits, `false` to clear them



### `show()`

Makes the view visible by setting `sfVisible`.
If the view is already visible this is a no-op.



### `showCursor()`

Makes the cursor visible by setting `sfCursorVis`.



### `getMaximumSize()`

Returns the maximum allowable size for this view.
The base implementation returns the owner's size, or
`(Integer.MAX_VALUE, Integer.MAX_VALUE)` when the view has
`gfFixed` set or has no owner.



### `topView()`

Returns the topmost modal view in the ownership chain, starting at `theTopView` if set. 
Returns `null` if no modal view is found.

**Returns**

the active modal view, or `null`



### `valid(int command)`

Validates the view before it is closed by the given command.
The base implementation always returns `true`. Subclasses
override to prevent closing when data is invalid.

**Parameters**

- `command` - the command attempting to close the view

**Returns**

`true` if the view may close



### `containsMouse(JtvEvent event)`

Returns `true` if this view is visible and the mouse pointer
in `event` lies within the view's bounds.

**Parameters**

- `event` - the mouse event carrying the cursor position

**Returns**

`true` if the mouse is inside the visible view



### `writeBuf(int x, int y, int w, int h, JtvDrawBuffer b)`

Writes a rectangular block of screen cells from `b` at position
`(x, y)` in view-local coordinates.
The buffer is consumed row by row; each row is `w` cells wide.

**Parameters**

- `x` - left column (view-local)
- `y` - top row (view-local)
- `w` - width in columns
- `h` - height in rows
- `b` - source draw buffer



### `writeBuf(int x, int y, int w, int h, org.viktor44.jtvision.core.JtvScreenCell[] b)`

Writes a rectangular block of screen cells from a [JtvScreenCell](../org.viktor44.jtvision.core/JtvScreenCell.md)
array at position `(x, y)` in view-local coordinates.

**Parameters**

- `x` - left column (view-local)
- `y` - top row (view-local)
- `w` - width in columns
- `h` - height in rows
- `b` - source cell array



### `writeLine(int x, int y, int w, int h, JtvDrawBuffer b)`

Writes the same single row from `b` repeatedly for `h`
consecutive rows, starting at `(x, y)`.
Row 0 of `b` is reused for every output row.

**Parameters**

- `x` - left column (view-local)
- `y` - top row (view-local)
- `w` - width in columns
- `h` - number of rows to write
- `b` - source draw buffer (row 0 is used)



### `writeChar(int x, int y, char c, int color, int count)`

Writes `count` copies of character `c` with colour
`color` starting at position `(x, y)`.

**Parameters**

- `x` - left column (view-local)
- `y` - row (view-local)
- `c` - the character to write
- `color` - the one-based palette index
- `count` - the number of character cells to fill



### `writeStr(int x, int y, String str, int color)`

Writes string `str` with colour `color` at position
`(x, y)`, clipped to the view's width.

**Parameters**

- `x` - left column (view-local)
- `y` - row (view-local)
- `str` - the string to write
- `color` - the one-based palette index



### `writeView(int x, int y, int count, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset)`

Core write method: clips the row to the view's bounds and delegates
to [writeToOwner](#writetoownerint-x1-int-x2-int-y-orgviktor44jtvisioncorejtvscreencell-buf-int-bufoffset-boolean-inshadow) for occlusion testing and final rendering.

**Parameters**

- `x`
- `y`
- `count`
- `buf`
- `bufOffset`


### `applyShadow(JtvColorAttr attr)`

Computes the colour attribute to use for a shadow cell, given the
original cell attribute. Preserves the shadow attribute or its
inverse when the input already uses those colours.

**Parameters**

- `attr` - the original colour attribute

**Returns**

the shadow colour attribute



### `writeToOwner(int x1, int x2, int y, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset, boolean inShadow)`

Propagates a screen-cell write from this view up to its owner group,
accounting for the owner's clip rectangle and the shadow flag.

**Parameters**

- `x1`
- `x2`
- `y`
- `buf`
- `bufOffset`
- `inShadow`


### `moveGrow(JtvPoint p, JtvPoint s, JtvRect limits, JtvPoint minSize, JtvPoint maxSize, int mode)`

Moves and/or resizes the view to the position `p` with size
`s`, clamping both to the supplied limits.

**Parameters**

- `p` - the new top-left origin
- `s` - the new size
- `limits` - the bounding rectangle the view must stay inside
- `minSize` - the minimum allowed size
- `maxSize` - the maximum allowed size
- `mode` - drag-mode flags controlling which constraints apply



### `dragView(JtvEvent event, int mode, JtvRect limits, JtvPoint minSize, JtvPoint maxSize)`

Runs the interactive drag/resize loop for this view.



If `event` is a mouse-down, the loop tracks mouse movement
calling [moveGrow](#movegrowjtvpoint-p-jtvpoint-s-jtvrect-limits-jtvpoint-minsize-jtvpoint-maxsize-int-mode) for each position. If `event` is a
keyboard event the loop reads arrow keys, Home, End, PgUp, PgDn;
plain arrows move or grow the view (depending on mode), Shift+arrow
resizes/moves in the opposite dimension, Escape cancels (restoring
the original bounds), and Enter confirms.



The `sfDragging` state bit is set for the duration of the loop
so the frame can render in its dragging style.

**Parameters**

- `event` - the initiating event (mouse-down or keyboard)
- `mode` - drag-mode flags (`dmDragMove`, `dmDragGrow`, etc.)
- `limits` - the bounding rectangle the view must stay within
- `minSize` - the minimum allowed size
- `maxSize` - the maximum allowed size



### `message(JtvView receiver, int what, int command, Object infoPtr)`

Delivers a synthetic event directly to `receiver`'s
[handleEvent](#handleeventjtvevent-event) method and returns the event's `infoPtr`
if the event was consumed (cleared), or `null` otherwise.

**Parameters**

- `receiver` - the view to deliver the event to
- `what` - the event type (`evBroadcast`, `evCommand`, etc.)
- `command` - the command code in the message
- `infoPtr` - the arbitrary data object in the message

**Returns**

the message's `infoPtr` if consumed, otherwise `null`



### `range(int val, int min, int max)`

Clamps `val` to the range `[min, max]`.
If `min > max`, `min` is used for both bounds.

**Parameters**

- `val` - the value to clamp
- `min` - the lower bound (inclusive)
- `max` - the upper bound (inclusive)

**Returns**

the clamped value



### `getSize()`

The width and height of this view. Together with [origin](#orgviktor44jtvisioncorejtvpoint-origin),
it defines the bounding rectangle returned by [getBounds()](#getbounds).



### `getCursor()`

The cursor position within the view's local coordinate system.
Updated by [setCursor(int, int)](#setcursorint-x-int-y) and [resetCursor()](#resetcursor).



### `getState()`

Bitmask of `sfXXXX` state constants describing the current
visibility, focus, selection, and dragging status of the view.
Modify only through [setState(int, boolean)](#setstateint-astate-boolean-enable).



### `getOptions()`

Bitmask of `ofXXXX` option constants controlling selectable,
framed, centered, buffered, and other optional behaviour.



### `setOptions(int options)`

Bitmask of `ofXXXX` option constants controlling selectable,
framed, centered, buffered, and other optional behaviour.

**Parameters**

- `options`


### `getEventMask()`

Event-type bitmask that determines which event categories this view
wants to receive (e.g. `evMouseDown`, `evKeyDown`,
`evCommand`). Set to the union of event types handled by
[handleEvent(JtvEvent)](#handleeventjtvevent-event).



### `setEventMask(int eventMask)`

Event-type bitmask that determines which event categories this view
wants to receive (e.g. `evMouseDown`, `evKeyDown`,
`evCommand`). Set to the union of event types handled by
[handleEvent(JtvEvent)](#handleeventjtvevent-event).

**Parameters**

- `eventMask`


### `getGrowMode()`

Grow-mode bitmask of `gfXXXX` constants controlling how this
view resizes relative to its owner (e.g. `gfGrowHiX` to keep
the right edge pinned to the owner's right side).



### `setGrowMode(int growMode)`

Grow-mode bitmask of `gfXXXX` constants controlling how this
view resizes relative to its owner (e.g. `gfGrowHiX` to keep
the right edge pinned to the owner's right side).

**Parameters**

- `growMode`


### `getMinimumSize()`

The minimum allowed window size



### `setMinimumSize(JtvPoint minimumSize)`

The minimum allowed window size

**Parameters**

- `minimumSize`


