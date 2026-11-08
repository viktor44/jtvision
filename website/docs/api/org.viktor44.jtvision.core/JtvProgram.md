---
title: "JtvProgram"
sidebar_label: "JtvProgram"
---

Class JtvProgram
================

```java
public abstract class JtvProgram
extends JtvGroup
```


Abstract base class for JT Vision applications.



`JtvProgram` is the top-level group that fills the entire screen and owns three
standard subviews: a [JtvMenuBar](../org.viktor44.jtvision.menus/JtvMenuBar.md) at the top, a [JtvDesktop](JtvDesktop.md) in the middle, and a
[JtvStatusLine](../org.viktor44.jtvision.views/JtvStatusLine.md) at the bottom.  These are created via injectable factory functions so
that applications can supply custom implementations without subclassing the factories.



The event loop (inherited from `TGroup.execute()`) runs until `endModal` is
called.  `TProgram.getEvent()` polls the platform event queue, dispatches key events
to the status line, and calls [idle()](#idle) when no events are waiting.



Applications should normally subclass [JtvApplication](JtvApplication.md) rather than `TProgram`
directly, since `TApplication` also initialises the platform Screen and EventQueue
subsystems.

org.viktor44.jtvision.core.JtvProgram

## Summary

### Constructors

| Visibility | Signature                   |
| ---------- | --------------------------- |
| public     | [JtvProgram()](#jtvprogram) |

### Fields

| Modifiers                | Field name                                                              | Type                                      |
| ------------------------ | ----------------------------------------------------------------------- | ----------------------------------------- |
| **private static**       | [statusLine](#orgviktor44jtvisionviewsjtvstatusline-statusline)         | org.viktor44.jtvision.views.JtvStatusLine |
| **private static**       | [menuBar](#orgviktor44jtvisionmenusjtvmenubar-menubar)                  | org.viktor44.jtvision.menus.JtvMenuBar    |
| **private static**       | [desktop](#orgviktor44jtvisioncorejtvdesktop-desktop)                   | org.viktor44.jtvision.core.JtvDesktop     |
| **private static**       | [application](#orgviktor44jtvisioncorejtvprogram-application)           | org.viktor44.jtvision.core.JtvProgram     |
| **private static final** | [clipboard](#javaawtdatatransferclipboard-clipboard)                    | java.awt.datatransfer.Clipboard           |
| **public static**        | [appPalette](#int-apppalette)                                           | int                                       |
| **public static**        | [eventTimeoutMs](#int-eventtimeoutms)                                   | int                                       |
| **protected**            | [pending](#orgviktor44jtvisioncorejtvevent-pending)                     | org.viktor44.jtvision.core.JtvEvent       |
| **private static final** | [cpAppColor](#orgviktor44jtvisioncorejtvpalette-cpappcolor)             | org.viktor44.jtvision.core.JtvPalette     |
| **private static final** | [cpAppBlackWhite](#orgviktor44jtvisioncorejtvpalette-cpappblackwhite)   | org.viktor44.jtvision.core.JtvPalette     |
| **private static final** | [cpAppMonochrome](#orgviktor44jtvisioncorejtvpalette-cpappmonochrome)   | org.viktor44.jtvision.core.JtvPalette     |
| **private static**       | [customAppPalette](#orgviktor44jtvisioncorejtvpalette-customapppalette) | org.viktor44.jtvision.core.JtvPalette     |

### Methods

| Modifiers          | Method signature                                                                   | Return type                               |
| ------------------ | ---------------------------------------------------------------------------------- | ----------------------------------------- |
| **public**         | [canMoveFocus()](#canmovefocus)                                                    | boolean                                   |
| **public**         | [executeDialog(JtvDialog pD, Object data)](#executedialogjtvdialog-pd-object-data) | int                                       |
| **public**         | [getEvent(JtvEvent event)](#geteventjtvevent-event)                                | org.viktor44.jtvision.core.JtvEvent       |
| **public**         | [getPalette()](#getpalette)                                                        | org.viktor44.jtvision.core.JtvPalette     |
| **public static**  | [setAppPalette(JtvPalette palette)](#setapppalettejtvpalette-palette)              | void                                      |
| **public static**  | [getClipboard()](#getclipboard)                                                    | java.awt.datatransfer.Clipboard           |
| **private static** | [initClipboard()](#initclipboard)                                                  | java.awt.datatransfer.Clipboard           |
| **public**         | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                          | void                                      |
| **public**         | [idle()](#idle)                                                                    | void                                      |
| **public**         | [initScreen()](#initscreen)                                                        | void                                      |
| **public**         | [putEvent(JtvEvent event)](#puteventjtvevent-event)                                | void                                      |
| **public**         | [run()](#run)                                                                      | void                                      |
| **public**         | [setScreenMode(int mode)](#setscreenmodeint-mode)                                  | void                                      |
| **public**         | [validView(JtvView p)](#validviewjtvview-p)                                        | org.viktor44.jtvision.views.JtvView       |
| **public**         | [insertWindow(JtvWindow pWin)](#insertwindowjtvwindow-pwin)                        | org.viktor44.jtvision.views.JtvWindow     |
| **protected**      | [initDesktop(JtvRect r)](#initdesktopjtvrect-r)                                    | org.viktor44.jtvision.core.JtvDesktop     |
| **protected**      | [initMenuBar(JtvRect r)](#initmenubarjtvrect-r)                                    | org.viktor44.jtvision.menus.JtvMenuBar    |
| **protected**      | [initStatusLine(JtvRect r)](#initstatuslinejtvrect-r)                              | org.viktor44.jtvision.views.JtvStatusLine |
| **public static**  | [getStatusLine()](#getstatusline)                                                  | org.viktor44.jtvision.views.JtvStatusLine |
| **public static**  | [getMenuBar()](#getmenubar)                                                        | org.viktor44.jtvision.menus.JtvMenuBar    |
| **public static**  | [getDesktop()](#getdesktop)                                                        | org.viktor44.jtvision.core.JtvDesktop     |
| **public static**  | [getApplication()](#getapplication)                                                | org.viktor44.jtvision.core.JtvProgram     |



## Constructors

### `JtvProgram()`

Constructs a `TProgram`.  Creates the standard subviews by calling
[initDesktop(JtvRect)](#initdesktopjtvrect-r), [initStatusLine(JtvRect)](#initstatuslinejtvrect-r), and
[initMenuBar(JtvRect)](#initmenubarjtvrect-r), which subclasses may override to supply custom
implementations.  A method may return `null` to omit that subview.



## Fields

### `org.viktor44.jtvision.views.JtvStatusLine statusLine`

The current application's status line, or `null` if none.


### `org.viktor44.jtvision.menus.JtvMenuBar menuBar`

The current application's menu bar, or `null` if none.


### `org.viktor44.jtvision.core.JtvDesktop desktop`

The current application's desktop, or `null` if none.


### `org.viktor44.jtvision.core.JtvProgram application`

Reference to the running `TProgram` instance (set in the constructor).


### `java.awt.datatransfer.Clipboard clipboard`

Shared clipboard: system clipboard when available, local fallback otherwise.


### `int appPalette`

The active color palette mode.  One of the `ap*` constants from
[ViewFlags](ViewFlags.md) (e.g. `apColor`, `apBlackWhite`, `apMonochrome`).


### `int eventTimeoutMs`

Maximum milliseconds to wait for a platform event before calling [idle()](#idle).
Default is `20` ms.


### `org.viktor44.jtvision.core.JtvEvent pending`

Buffered event injected by [putEvent](#puteventjtvevent-event) and consumed on the next [getEvent](#geteventjtvevent-event) call.


### `org.viktor44.jtvision.core.JtvPalette cpAppColor`

Master application palette — 135 entries for color mode.


### `org.viktor44.jtvision.core.JtvPalette cpAppBlackWhite`

Master application palette — 135 entries for black-and-white mode.


### `org.viktor44.jtvision.core.JtvPalette cpAppMonochrome`

Master application palette — 135 entries for monochrome mode.


### `org.viktor44.jtvision.core.JtvPalette customAppPalette`

User-supplied palette that overrides `cpAppColor` when non-`null`.


## Methods

### `canMoveFocus()`

Returns `true` if keyboard focus can be moved away from the currently focused window
(i.e. the desktop accepts a `cmReleasedFocus` validation).

**Returns**

`true` if focus can be transferred



### `executeDialog(JtvDialog pD, Object data)`

Executes a dialog modally on the desktop.  Optionally transfers data to the dialog before
execution and retrieves the result afterward.

**Parameters**

- `pD` - the dialog to execute
- `data` - optional data object passed to `setDataFrom` / `getDataTo`, may be `null`

**Returns**

the command code that dismissed the dialog (e.g. `cmOK`, `cmCancel`)



### `getEvent(JtvEvent event)`

Retrieves the next event from the platform queue (or from the [pending](#orgviktor44jtvisioncorejtvevent-pending) buffer).



If no event is available, [idle()](#idle) is called.  Key events and status-line
mouse clicks are pre-processed by the status line before being returned.

**Parameters**

- `event` - the event object to fill

**Returns**

the filled `event`



### `getPalette()`

Returns the application-wide color palette.  Returns the custom palette if one was set
via [setAppPalette](#setapppalettejtvpalette-palette), otherwise selects among the three built-in 135-entry palettes
based on [appPalette](#int-apppalette) (`apColor`, `apBlackWhite`, or `apMonochrome`).

**Returns**

the application palette



### `setAppPalette(JtvPalette palette)`

Replaces the application-wide color palette.  Pass `null` to restore the built-in
default palette.

**Parameters**

- `palette` - new palette, or `null` to use the default



### `getClipboard()`

Shared clipboard: system clipboard when available, local fallback otherwise.



### `initClipboard()`

*No method description provided*



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvGroup`

Handles application-level events.



Intercepts Alt+`1`–`9` to broadcast `cmSelectWindowNum`, and
`cmQuit` to end the modal event loop.  All other events are passed to the
inherited group handler.

**Parameters**

- `event` - the event to handle



### `idle()`

Called by the event loop when no event is pending.



Updates the status line, broadcasts `cmCommandSetChanged` if the command set
has changed since the last idle, and checks for terminal resize events.
Override (calling `super.idle()`) to perform background work such as enabling or
disabling commands based on application state.



### `initScreen()`

Initialises screen-mode variables: shadow size (2 columns × 1 row), markers, and
application palette mode.  Called from the constructor and from [setScreenMode](#setscreenmodeint-mode).



### `putEvent(JtvEvent event)`

Queues `event` so it will be returned by the next [getEvent](#geteventjtvevent-event) call.
Only one event can be buffered at a time; a second call overwrites the first.

**Parameters**

- `event` - the event to buffer



### `run()`

Starts the application's modal event loop by calling [execute()](../org.viktor44.jtvision.views/JtvGroup.md#execute).



### `setScreenMode(int mode)`

Changes the video mode, reinitialises the screen variables, updates the screen buffer
reference, resizes the program group to match the new dimensions, and redraws everything.

**Parameters**

- `mode` - the new screen mode (passed to the platform `Screen.setVideoMode`)



### `validView(JtvView p)`

Validates a newly constructed view.  If `p` is `null` or fails
`cmValid` validation, `null` is returned; otherwise
`p` is returned unchanged.

**Parameters**

- `p` - the view to validate

**Returns**

`p` if valid, `null` otherwise



### `insertWindow(JtvWindow pWin)`

Validates and inserts `pWin` into the desktop.
Returns `null` if it fails validation or if focus
cannot be moved ([canMoveFocus()](#canmovefocus) returns `false`).

**Parameters**

- `pWin` - the window to insert

**Returns**

`pWin` on success, `null` on failure



### `initDesktop(JtvRect r)`

Creates the desktop subview.  Override to supply a custom desktop.
Return `null` to omit the desktop entirely.

**Parameters**

- `r` - the full screen rectangle

**Returns**

a new desktop view, or `null`



### `initMenuBar(JtvRect r)`

Creates the menu bar subview.  Override to supply a custom menu bar.
Return `null` to omit the menu bar entirely.

**Parameters**

- `r` - the full screen rectangle

**Returns**

a new menu bar view, or `null`



### `initStatusLine(JtvRect r)`

Creates the status line subview.  Override to supply a custom status line.
Return `null` to omit the status line entirely.

**Parameters**

- `r` - the full screen rectangle

**Returns**

a new status line view, or `null`



### `getStatusLine()`

The current application's status line, or `null` if none.



### `getMenuBar()`

The current application's menu bar, or `null` if none.



### `getDesktop()`

The current application's desktop, or `null` if none.



### `getApplication()`

Reference to the running `TProgram` instance (set in the constructor).



