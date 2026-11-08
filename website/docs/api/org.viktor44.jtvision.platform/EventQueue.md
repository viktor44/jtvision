---
title: "EventQueue"
sidebar_label: "EventQueue"
---

Class EventQueue
================

```java
public abstract class EventQueue
```


Platform-level input event queue (singleton).



The concrete implementation is selected at startup by [initInstance()](#initinstance):
`WinEventQueue` on Windows, `MacosEventQueue` on macOS, and
`LinuxEventQueue` on all other Unix platforms.

org.viktor44.jtvision.platform.EventQueue

## Summary

### Constructors

| Visibility | Signature                   |
| ---------- | --------------------------- |
| protected  | [EventQueue()](#eventqueue) |

### Fields

| Modifiers              | Field name                                                                             | Type                                                                |
| ---------------------- | -------------------------------------------------------------------------------------- | ------------------------------------------------------------------- |
| **private static**     | [INSTANCE](#orgviktor44jtvisionplatformeventqueue-instance)                            | org.viktor44.jtvision.platform.EventQueue                           |
| **private final**      | [eventQueue](#concurrentlinkedblockingqueueorgviktor44jtvisioncorejtvevent-eventqueue) | concurrent.LinkedBlockingQueue&lt;org.viktor44.jtvision.core.JtvEvent> |
| **protected**          | [inputThread](#thread-inputthread)                                                     | Thread                                                              |
| **protected**          | [readerThread](#thread-readerthread)                                                   | Thread                                                              |
| **protected volatile** | [running](#boolean-running)                                                            | boolean                                                             |
| **protected**          | [rawTerminalEnabled](#boolean-rawterminalenabled)                                      | boolean                                                             |
| **protected**          | [lastMouse](#orgviktor44jtvisioncoremouseevent-lastmouse)                              | org.viktor44.jtvision.core.MouseEvent                               |
| **protected**          | [lastClickTime](#long-lastclicktime)                                                   | long                                                                |
| **protected**          | [clickCount](#int-clickcount)                                                          | int                                                                 |
| **public**             | [doubleDelay](#int-doubledelay)                                                        | int                                                                 |

### Methods

| Modifiers              | Method signature                                                                                              | Return type                               |
| ---------------------- | ------------------------------------------------------------------------------------------------------------- | ----------------------------------------- |
| **public static**      | [initInstance()](#initinstance)                                                                               | void                                      |
| **static**             | [initInstance(EventQueue instance)](#initinstanceeventqueue-instance)                                         | void                                      |
| **public static**      | [getInstance()](#getinstance)                                                                                 | org.viktor44.jtvision.platform.EventQueue |
| **protected abstract** | [init()](#init)                                                                                               | void                                      |
| **protected abstract** | [enableRawTerminalMode()](#enablerawterminalmode)                                                             | void                                      |
| **protected abstract** | [disableRawTerminalMode()](#disablerawterminalmode)                                                           | void                                      |
| **protected**          | [beforeStopThreads(boolean forceClose)](#beforestopthreadsboolean-forceclose)                                 | void                                      |
| **protected**          | [afterStopThreads()](#afterstopthreads)                                                                       | void                                      |
| **public**             | [shutdown()](#shutdown)                                                                                       | void                                      |
| **public**             | [waitForEvents(int timeoutMs)](#waitforeventsint-timeoutms)                                                   | void                                      |
| **public**             | [getMouseEvent(JtvEvent event)](#getmouseeventjtvevent-event)                                                 | void                                      |
| **public**             | [getKeyEvent(JtvEvent event)](#getkeyeventjtvevent-event)                                                     | org.viktor44.jtvision.core.JtvEvent       |
| **public**             | [wakeUp()](#wakeup)                                                                                           | void                                      |
| **public**             | [suspend()](#suspend)                                                                                         | void                                      |
| **public**             | [resume()](#resume)                                                                                           | void                                      |
| **protected**          | [stopInputThreads(boolean forceCloseInputStream)](#stopinputthreadsboolean-forcecloseinputstream)             | void                                      |
| **private**            | [joinThread(Thread thread)](#jointhreadthread-thread)                                                         | void                                      |
| **private**            | [peekEvent(int mask)](#peekeventint-mask)                                                                     | org.viktor44.jtvision.core.JtvEvent       |
| **protected**          | [pushKeyEvent(int keyCode, int modifiers, char keyChar)](#pushkeyeventint-keycode-int-modifiers-char-keychar) | void                                      |
| **protected**          | [pushPasteEvent(String text)](#pushpasteeventstring-text)                                                     | void                                      |
| **protected**          | [offerEvent(JtvEvent event)](#offereventjtvevent-event)                                                       | void                                      |
| **protected**          | [toButtonMask(int sgrButton)](#tobuttonmaskint-sgrbutton)                                                     | int                                       |



## Constructors

### `EventQueue()`

*No method description provided*



## Fields

### `org.viktor44.jtvision.platform.EventQueue INSTANCE`

*No description provided*


### `concurrent.LinkedBlockingQueue<org.viktor44.jtvision.core.JtvEvent> eventQueue`

*No description provided*


### `Thread inputThread`

Event-decoding thread.


### `Thread readerThread`

Raw-byte reader thread (Unix) or combined reader+decoder (Windows).


### `boolean running`

`true` while the input threads are running.


### `boolean rawTerminalEnabled`

`true` after raw terminal mode has been successfully enabled.


### `org.viktor44.jtvision.core.MouseEvent lastMouse`

Mouse tracking state, shared by Unix SGR and Windows mouse handlers.


### `long lastClickTime`

*No description provided*


### `int clickCount`

*No description provided*


### `int doubleDelay`

Maximum time between two mouse-down events (milliseconds) to qualify
as a double-click. Defaults to 400 ms.


## Methods

### `initInstance()`

Initialises the singleton with the correct platform implementation.
Must be called before [getInstance()](#getinstance).



### `initInstance(EventQueue instance)`

Initialises the singleton with a custom instance, allowing tests to
substitute alternative implementations.

**Parameters**

- `instance` - the instance to use; must not be `null`



### `getInstance()`

Returns the singleton `EventQueue` instance.



### `init()`

Initialises the event queue and starts the input thread(s).



### `enableRawTerminalMode()`

Enables raw terminal input mode so that keystrokes arrive immediately.



Unix: saves terminal settings and applies `stty raw -echo`.
Windows: saves and changes console mode flags via `SetConsoleMode`.



### `disableRawTerminalMode()`

Restores the terminal to its pre-raw-mode state.



### `beforeStopThreads(boolean forceClose)`

Called by [stopInputThreads](#stopinputthreadsboolean-forcecloseinputstream) before threads are interrupted.
Subclasses should close streams or signal conditions that unblock blocked reads.

**Parameters**

- `forceClose` - whether to force-close any open stream



### `afterStopThreads()`

Called by [stopInputThreads](#stopinputthreadsboolean-forcecloseinputstream) after all threads have been joined.
Subclasses should clear any intermediate byte queues here.



### `shutdown()`

Shuts down the event queue and restores the terminal to its original state.



### `waitForEvents(int timeoutMs)`

Waits for at least one event to become available, up to `timeoutMs`
milliseconds. Always flushes pending screen updates first.

**Parameters**

- `timeoutMs` - maximum wait time in milliseconds



### `getMouseEvent(JtvEvent event)`

Dequeues the next mouse event into `event`, or sets
`event.what` to `evNothing` if no mouse event is queued.
Coalesces consecutive `evMouseMove` events.

**Parameters**

- `event` - the target event object to fill in



### `getKeyEvent(JtvEvent event)`

Dequeues the next keyboard event into `event`, or sets
`event.what` to `evNothing` if no keyboard event is queued.

**Parameters**

- `event` - the target event object to fill in

**Returns**

`event` for convenience



### `wakeUp()`

No-op wake-up hint. Input threads run continuously.



### `suspend()`

Suspends event processing and restores the terminal to cooked mode.
Call [resume()](#resume) to re-initialise afterwards.



### `resume()`

Re-initialises the event queue after a previous [suspend()](#suspend).



### `stopInputThreads(boolean forceCloseInputStream)`

Signals all input threads to stop, waits for them to finish, and
clears thread references.

**Parameters**

- `forceCloseInputStream` - passed to `#beforeStopThreads`



### `joinThread(Thread thread)`

*No method description provided*

**Parameters**

- `thread`


### `peekEvent(int mask)`

*No method description provided*

**Parameters**

- `mask`


### `pushKeyEvent(int keyCode, int modifiers, char keyChar)`

Constructs an `evKeyDown` event and places it in the queue.

**Parameters**

- `keyCode` - AWT virtual-key code
- `modifiers` - AWT modifier mask
- `keyChar` - Unicode character, or `java.awt.event.KeyEvent#CHAR_UNDEFINED`



### `pushPasteEvent(String text)`

Constructs an `evPaste` event carrying the pasted text and
places it in the queue.  Used by the bracketed-paste handler.

**Parameters**

- `text` - the clipboard text delivered by the terminal



### `offerEvent(JtvEvent event)`

Places a pre-constructed event in the event queue.

**Parameters**

- `event` - the event to enqueue



### `toButtonMask(int sgrButton)`

Converts an SGR mouse button index to the JT Vision button-mask constant.

**Parameters**

- `sgrButton` - SGR button index (0=left, 1=middle, 2=right)

**Returns**

`mbLeftButton`, `mbMiddleButton`, `mbRightButton`, or `0`



