---
title: "EventCodes"
sidebar_label: "EventCodes"
---

Class EventCodes
================

```java
public final class EventCodes
```


Event type codes and masks used in `JtvEvent.what`.
Additional constants are jtvision extensions
(mouse wheel directions and richer click flags).

org.viktor44.jtvision.core.EventCodes

## Summary

### Constructors

| Visibility | Signature                   |
| ---------- | --------------------------- |
| private    | [EventCodes()](#eventcodes) |

### Fields

| Modifiers               | Field name                                | Type |
| ----------------------- | ----------------------------------------- | ---- |
| **public static final** | [evMouseDown](#int-evmousedown)           | int  |
| **public static final** | [evMouseUp](#int-evmouseup)               | int  |
| **public static final** | [evMouseMove](#int-evmousemove)           | int  |
| **public static final** | [evMouseAuto](#int-evmouseauto)           | int  |
| **public static final** | [evMouseWheel](#int-evmousewheel)         | int  |
| **public static final** | [evKeyDown](#int-evkeydown)               | int  |
| **public static final** | [evPaste](#int-evpaste)                   | int  |
| **public static final** | [evCommand](#int-evcommand)               | int  |
| **public static final** | [evBroadcast](#int-evbroadcast)           | int  |
| **public static final** | [evNothing](#int-evnothing)               | int  |
| **public static final** | [evMouse](#int-evmouse)                   | int  |
| **public static final** | [evKeyboard](#int-evkeyboard)             | int  |
| **public static final** | [evMessage](#int-evmessage)               | int  |
| **public static final** | [mbLeftButton](#int-mbleftbutton)         | int  |
| **public static final** | [mbRightButton](#int-mbrightbutton)       | int  |
| **public static final** | [mbMiddleButton](#int-mbmiddlebutton)     | int  |
| **public static final** | [mwUp](#int-mwup)                         | int  |
| **public static final** | [mwDown](#int-mwdown)                     | int  |
| **public static final** | [mwLeft](#int-mwleft)                     | int  |
| **public static final** | [mwRight](#int-mwright)                   | int  |
| **public static final** | [meMouseMoved](#int-memousemoved)         | int  |
| **public static final** | [meDoubleClick](#int-medoubleclick)       | int  |
| **public static final** | [meTripleClick](#int-metripleclick)       | int  |
| **public static final** | [positionalEvents](#int-positionalevents) | int  |
| **public static final** | [focusedEvents](#int-focusedevents)       | int  |



## Constructors

### `EventCodes()`

*No method description provided*



## Fields

### `int evMouseDown`

Mouse button pressed.


### `int evMouseUp`

Mouse button released.


### `int evMouseMove`

Mouse changed location.


### `int evMouseAuto`

Periodic event while a mouse button is held.


### `int evMouseWheel`

jtvision extension for mouse wheel input.


### `int evKeyDown`

Key pressed.


### `int evPaste`

Bracketed paste event: the terminal delivered clipboard content
wrapped in `ESC[200~`…`ESC[201~`.  The pasted text
is carried in the event's [MessageEvent.getInfoPtr()](MessageEvent.md#getinfoptr) as a
`String`.


### `int evCommand`

Command event.


### `int evBroadcast`

Broadcast event.


### `int evNothing`

Event has already been handled; no further action is required.


### `int evMouse`

Mask for mouse-family events.



The base mouse mask is `0x000F`. jtvision extends it
with [evMouseWheel](#int-evmousewheel), resulting in `0x002F`.


### `int evKeyboard`

Keyboard event mask (includes [evKeyDown](#int-evkeydown) and [evPaste](#int-evpaste)).


### `int evMessage`

Message-family event mask (`0xFF00`): command, broadcast, or user-defined message.


### `int mbLeftButton`

Left mouse button state bit.


### `int mbRightButton`

Right mouse button state bit.


### `int mbMiddleButton`

Middle mouse button state bit.


### `int mwUp`

Wheel direction: up.


### `int mwDown`

Wheel direction: down.


### `int mwLeft`

Wheel direction: left.


### `int mwRight`

Wheel direction: right.


### `int meMouseMoved`

Pointer moved during the current interaction sequence.


### `int meDoubleClick`

Mouse event is a double click.


### `int meTripleClick`

Mouse event is a triple click.


### `int positionalEvents`

Events routed by pointer position.



This is the classic positional mouse set ([evMouseDown](#int-evmousedown),
[evMouseUp](#int-evmouseup), [evMouseMove](#int-evmousemove), [evMouseAuto](#int-evmouseauto)) and
intentionally excludes [evMouseWheel](#int-evmousewheel).


### `int focusedEvents`

Events routed through focused-event processing.



The default focused routing contains keyboard bits;
jtvision also includes [evCommand](#int-evcommand) for command-chain handling.


