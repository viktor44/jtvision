---
title: "WinEventQueue"
sidebar_label: "WinEventQueue"
---

Class WinEventQueue
===================

```java
class WinEventQueue
extends EventQueue
```


Windows implementation of [EventQueue](EventQueue.md).



A single daemon thread reads `INPUT_RECORD` structures via Jansi's
`Kernel32` binding and translates them into [JtvEvent](../org.viktor44.jtvision.core/JtvEvent.md) objects.
Raw mode is managed via `SetConsoleMode`.

**Console mode flags**

Raw mode clears `ENABLE_LINE_INPUT`, `ENABLE_ECHO_INPUT`,
`ENABLE_PROCESSED_INPUT`, and `ENABLE_QUICK_EDIT_MODE`;
sets `ENABLE_MOUSE_INPUT`, `ENABLE_EXTENDED_FLAGS`, and
`ENABLE_WINDOW_INPUT`.

org.viktor44.jtvision.platform.WinEventQueue

## Summary

### Constructors

| Visibility | Signature                         |
| ---------- | --------------------------------- |
|            | [WinEventQueue()](#wineventqueue) |

### Fields

| Modifiers                | Field name                                                                  | Type    |
| ------------------------ | --------------------------------------------------------------------------- | ------- |
| **private static final** | [WIN_ENABLE_PROCESSED_INPUT](#int-win_enable_processed_input)               | int     |
| **private static final** | [WIN_ENABLE_LINE_INPUT](#int-win_enable_line_input)                         | int     |
| **private static final** | [WIN_ENABLE_ECHO_INPUT](#int-win_enable_echo_input)                         | int     |
| **private static final** | [WIN_ENABLE_WINDOW_INPUT](#int-win_enable_window_input)                     | int     |
| **private static final** | [WIN_ENABLE_MOUSE_INPUT](#int-win_enable_mouse_input)                       | int     |
| **private static final** | [WIN_ENABLE_QUICK_EDIT_MODE](#int-win_enable_quick_edit_mode)               | int     |
| **private static final** | [WIN_ENABLE_EXTENDED_FLAGS](#int-win_enable_extended_flags)                 | int     |
| **private static final** | [WIN_ENABLE_VIRTUAL_TERMINAL_INPUT](#int-win_enable_virtual_terminal_input) | int     |
| **private**              | [savedWindowsInputMode](#int-savedwindowsinputmode)                         | int     |
| **private**              | [windowsInputModeSaved](#boolean-windowsinputmodesaved)                     | boolean |

### Methods

| Modifiers     | Method signature                                                                                | Return type |
| ------------- | ----------------------------------------------------------------------------------------------- | ----------- |
| **protected** | [init()](#init)                                                                                 | void        |
| **protected** | [enableRawTerminalMode()](#enablerawterminalmode)                                               | void        |
| **protected** | [disableRawTerminalMode()](#disablerawterminalmode)                                             | void        |
| **private**   | [enableWindowsRawInput()](#enablewindowsrawinput)                                               | boolean     |
| **private**   | [restoreWindowsInputMode()](#restorewindowsinputmode)                                           | boolean     |
| **private**   | [windowsEventLoop()](#windowseventloop)                                                         | void        |
|               | [handleWindowsKeyEvent(KEY_EVENT_RECORD ev)](#handlewindowskeyeventkey_event_record-ev)         | void        |
| **private**   | [winSpecialVkToAwtVk(int winVk)](#winspecialvktoawtvkint-winvk)                                 | int         |
| **private**   | [handleWindowsMouseEvent(MOUSE_EVENT_RECORD ev)](#handlewindowsmouseeventmouse_event_record-ev) | void        |



## Constructors

### `WinEventQueue()`

*No method description provided*



## Fields

### `int WIN_ENABLE_PROCESSED_INPUT`

*No description provided*


### `int WIN_ENABLE_LINE_INPUT`

*No description provided*


### `int WIN_ENABLE_ECHO_INPUT`

*No description provided*


### `int WIN_ENABLE_WINDOW_INPUT`

*No description provided*


### `int WIN_ENABLE_MOUSE_INPUT`

*No description provided*


### `int WIN_ENABLE_QUICK_EDIT_MODE`

*No description provided*


### `int WIN_ENABLE_EXTENDED_FLAGS`

*No description provided*


### `int WIN_ENABLE_VIRTUAL_TERMINAL_INPUT`

*No description provided*


### `int savedWindowsInputMode`

Console input mode saved before raw mode is enabled.


### `boolean windowsInputModeSaved`

`true` if `savedWindowsInputMode` holds a valid saved value.


## Methods

### `init()`

**Overrides:**

`init()` in `org.viktor44.jtvision.platform.EventQueue`

*No method description provided*



### `enableRawTerminalMode()`

**Overrides:**

`enableRawTerminalMode()` in `org.viktor44.jtvision.platform.EventQueue`

Saves the current console input mode and switches to raw mode via
`SetConsoleMode`.



### `disableRawTerminalMode()`

**Overrides:**

`disableRawTerminalMode()` in `org.viktor44.jtvision.platform.EventQueue`

Restores the console input mode saved by `enableRawTerminalMode()`.



### `enableWindowsRawInput()`

*No method description provided*



### `restoreWindowsInputMode()`

*No method description provided*



### `windowsEventLoop()`

Windows event-loop thread body. Waits on the console input handle via
`WaitForSingleObject` (50 ms timeout) and reads available
`INPUT_RECORD` structures. Dispatches each record to
`handleWindowsKeyEvent` or `handleWindowsMouseEvent`.



### `handleWindowsKeyEvent(KEY_EVENT_RECORD ev)`

Translates a Windows `KEY_EVENT_RECORD` into a JT Vision
`evKeyDown` event. Key-up records are silently discarded.



AltGr (LeftCtrl + RightAlt) is treated as a regular character modifier
and not as separate Alt/Ctrl presses.

**Parameters**

- `ev` - the Windows key event record



### `winSpecialVkToAwtVk(int winVk)`

Maps a Windows virtual-key code to the corresponding AWT `VK_*`
constant for navigation, function, and other named special keys.
Returns `0` for printable-character keys.

**Parameters**

- `winVk` - the Windows virtual-key code

**Returns**

the AWT `VK_*` constant, or `0` if unmapped



### `handleWindowsMouseEvent(MOUSE_EVENT_RECORD ev)`

Translates a Windows `MOUSE_EVENT_RECORD` into a JT Vision mouse
event. Classifies as wheel, move, down, or up based on flags and button
state transition relative to [lastMouse](EventQueue.md#orgviktor44jtvisioncoremouseevent-lastmouse).

**Parameters**

- `ev` - the Windows mouse event record



