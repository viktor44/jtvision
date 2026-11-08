---
title: "JtvDesktop"
sidebar_label: "JtvDesktop"
---

Class JtvDesktop
================

```java
public class JtvDesktop
extends JtvGroup
```


The desktop group that acts as the container for all application windows.



`TDesktop` sits between the menu bar and the status line, occupying the
middle portion of the [JtvProgram](JtvProgram.md) screen.  Its back-most child is a
[JtvBackground](JtvBackground.md) that provides the characteristic textured fill.  All other
children are user windows.



The desktop handles the `cmNext` and `cmPrev` commands to cycle
window focus, and provides [tile](#tilejtvrect-r) and [cascade](#cascadejtvrect-r) operations that
arrange all [ViewFlags.ofTileable](ViewFlags.md#int-oftileable) windows within a given rectangle.

org.viktor44.jtvision.core.JtvDesktop

## Summary

### Constructors

| Visibility | Signature                                               |
| ---------- | ------------------------------------------------------- |
| public     | [JtvDesktop(JtvRect bounds)](#jtvdesktopjtvrect-bounds) |

### Fields

| Modifiers               | Field name                                                     | Type                                     |
| ----------------------- | -------------------------------------------------------------- | ---------------------------------------- |
| **public static final** | [defaultBkgrnd](#char-defaultbkgrnd)                           | char                                     |
| **private**             | [background](#orgviktor44jtvisioncorejtvbackground-background) | org.viktor44.jtvision.core.JtvBackground |
| **private**             | [tileColumnsFirst](#boolean-tilecolumnsfirst)                  | boolean                                  |

### Methods

| Modifiers          | Method signature                                          | Return type                              |
| ------------------ | --------------------------------------------------------- | ---------------------------------------- |
| **public**         | [handleEvent(JtvEvent event)](#handleeventjtvevent-event) | void                                     |
| **protected**      | [initBackground(JtvRect r)](#initbackgroundjtvrect-r)     | org.viktor44.jtvision.core.JtvBackground |
| **public**         | [cascade(JtvRect r)](#cascadejtvrect-r)                   | void                                     |
| **public**         | [tile(JtvRect r)](#tilejtvrect-r)                         | void                                     |
| **public**         | [tileError()](#tileerror)                                 | void                                     |
| **private static** | [tileable(JtvView v)](#tileablejtvview-v)                 | boolean                                  |
| **public**         | [getBackground()](#getbackground)                         | org.viktor44.jtvision.core.JtvBackground |



## Constructors

### `JtvDesktop(JtvRect bounds)`

Constructs a desktop covering `bounds`

**Parameters**

- `bounds` - the desktop area (normally the screen minus menu and status line rows)



## Fields

### `char defaultBkgrnd`

*No description provided*


### `org.viktor44.jtvision.core.JtvBackground background`

The background tile view inserted as the bottom-most child.


### `boolean tileColumnsFirst`

When `true`, tile columns are arranged before rows; default is `false`.


## Methods

### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvGroup`

Handles `cmNext` (cycle to next window) and `cmPrev` (cycle to previous
window) commands, then delegates all other events to the inherited handler.

**Parameters**

- `event` - the event to process



### `initBackground(JtvRect r)`

Default background factory: creates a [JtvBackground](JtvBackground.md) filled with
[defaultBkgrnd](#char-defaultbkgrnd) covering the full desktop rectangle.

**Parameters**

- `r` - the desktop extent

**Returns**

a new background view



### `cascade(JtvRect r)`

Arranges all tileable visible windows in a cascaded (staircase) pattern within `r`.
Each window is offset one row and one column from the previous.
Calls [tileError()](#tileerror) if the windows are too large to fit.

**Parameters**

- `r` - the rectangle within which to cascade windows



### `tile(JtvRect r)`

Arranges all tileable visible windows in a grid within `r`, dividing the area
as evenly as possible.  The number of columns and rows is derived from the window count.
Calls [tileError()](#tileerror) if the area is too small.

**Parameters**

- `r` - the rectangle within which to tile windows



### `tileError()`

Called when [tile](#tilejtvrect-r) or [cascade](#cascadejtvrect-r) cannot fit the windows in the available
space.  The default implementation is a no-op; override to show an error message.



### `tileable(JtvView v)`

Returns `true` if `v` is tileable and currently visible.

**Parameters**

- `v`


### `getBackground()`

The background tile view inserted as the bottom-most child.



