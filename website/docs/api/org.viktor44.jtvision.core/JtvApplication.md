---
title: "JtvApplication"
sidebar_label: "JtvApplication"
---

Class JtvApplication
====================

```java
public class JtvApplication
extends JtvProgram
```


The concrete top-level application class that initialises platform subsystems and runs
the event loop.



`TApplication` extends [JtvProgram](JtvProgram.md) by initialising the [Screen](../org.viktor44.jtvision.platform/Screen.md) and
[EventQueue](../org.viktor44.jtvision.platform/EventQueue.md) platform subsystems before constructing subviews, and shutting them down
after the event loop exits.  Most applications should subclass `TApplication` and
override [JtvProgram.initMenuBar](JtvProgram.md#initmenubarjtvrect-r), [JtvProgram.initStatusLine](JtvProgram.md#initstatuslinejtvrect-r), and/or
[JtvProgram.initDesktop](JtvProgram.md#initdesktopjtvrect-r) to supply custom subviews.



[idle()](#idle) is overridden to automatically enable or disable the `cmTile` and
`cmCascade` commands based on whether any [ViewFlags.ofTileable](ViewFlags.md#int-oftileable) windows are
currently visible on the desktop.

org.viktor44.jtvision.core.JtvApplication

## Summary

### Constructors

| Visibility | Signature                                                                     |
| ---------- | ----------------------------------------------------------------------------- |
| public     | [JtvApplication()](#jtvapplication)                                           |
| private    | [JtvApplication(boolean platformReady)](#jtvapplicationboolean-platformready) |

### Methods

| Modifiers          | Method signature                                          | Return type                        |
| ------------------ | --------------------------------------------------------- | ---------------------------------- |
| **private static** | [initPlatform()](#initplatform)                           | boolean                            |
| **public**         | [shutdown()](#shutdown)                                   | void                               |
| **public static**  | [shutdownPlatform()](#shutdownplatform)                   | void                               |
| **public**         | [suspend()](#suspend)                                     | void                               |
| **public**         | [resume()](#resume)                                       | void                               |
| **public**         | [cascade()](#cascade)                                     | void                               |
| **public**         | [tile()](#tile)                                           | void                               |
| **public**         | [getTileRect()](#gettilerect)                             | org.viktor44.jtvision.core.JtvRect |
| **public**         | [idle()](#idle)                                           | void                               |
| **public**         | [handleEvent(JtvEvent event)](#handleeventjtvevent-event) | void                               |
| **public**         | [run()](#run)                                             | void                               |



## Constructors

### `JtvApplication()`

Constructs a `TApplication`, initialising the platform Screen and EventQueue
before the parent constructor builds the subviews.



### `JtvApplication(boolean platformReady)`

Delegates to [JtvProgram.JtvProgram()](JtvProgram.md#jtvprogram) after the platform has been initialised.

**Parameters**

- `platformReady`


## Methods

### `initPlatform()`

Initialises the Screen and EventQueue platform subsystems.



### `shutdown()`

Shuts down the application: shuts down the Screen and EventQueue platform subsystems.



### `shutdownPlatform()`

Statically shuts down the platform subsystems (Screen and EventQueue).



This is useful when the application fails to start (e.g. during framework
initialisation) and no `JtvApplication` instance is available to call
[shutdown()](#shutdown) on.  Each subsystem's shutdown method is a no-op when
not initialised, so calling this is always safe.



### `suspend()`

Suspends the application for OS shell-out: pauses the EventQueue then the Screen.
Call [resume()](#resume) to restore normal operation.



### `resume()`

Resumes the application after a shell-out: restores the Screen then restarts the EventQueue.



### `cascade()`

Cascades all tileable visible windows within the tile rectangle returned by
[getTileRect()](#gettilerect).



### `tile()`

Tiles all tileable visible windows within the tile rectangle returned by
[getTileRect()](#gettilerect).



### `getTileRect()`

Returns the rectangle used for tile and cascade operations.
Default is the full desktop extent; override to restrict the area.

**Returns**

the tile/cascade boundary rectangle



### `idle()`

**Overrides:**

`idle()` in `org.viktor44.jtvision.core.JtvProgram`

Extends the base idle handler to enable `cmTile` and `cmCascade` when at
least one [ViewFlags.ofTileable](ViewFlags.md#int-oftileable) window is visible on the desktop, and to disable
them when none are.



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.core.JtvProgram`

Handles `cmCascade` and `cmTile` commands, delegating to [cascade()](#cascade)
and [tile()](#tile) respectively.  All other events are passed to the inherited handler.

**Parameters**

- `event` - the event to handle



### `run()`

**Overrides:**

`run()` in `org.viktor44.jtvision.core.JtvProgram`

Runs the application event loop and ensures [shutdown()](#shutdown) is called on exit,
even if an exception is thrown.



