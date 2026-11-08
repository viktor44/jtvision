---
title: "MouseEvent"
sidebar_label: "MouseEvent"
---

Class MouseEvent
================

```java
public class MouseEvent
```


Payload of a mouse-family event (`evMouseDown`, `evMouseUp`,
`evMouseMove`, `evMouseAuto`, or `evMouseWheel`).



This class corresponds to the `evMouse` variant of the Pascal
`TEvent` record.  It is active when [JtvEvent.getWhat()](JtvEvent.md#getwhat) matches
the [EventCodes.evMouse](EventCodes.md#int-evmouse) mask.



The original Pascal record defined three fields: `Buttons` (byte),
`Double` (Boolean), and `Where` (TPoint).  This Java port expands
that with `eventFlags` (which subsumes `Double`), plus
`modifiers` and `wheel` as jtvision extensions.

org.viktor44.jtvision.core.MouseEvent

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                         |
| ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [MouseEvent()](#mouseevent)                                                                                                                                       |
| public     | [MouseEvent(JtvPoint where, int eventFlags, int modifiers, int buttons, int wheel)](#mouseeventjtvpoint-where-int-eventflags-int-modifiers-int-buttons-int-wheel) |

### Fields

| Modifiers   | Field name                                      | Type                                |
| ----------- | ----------------------------------------------- | ----------------------------------- |
| **private** | [where](#orgviktor44jtvisioncorejtvpoint-where) | org.viktor44.jtvision.core.JtvPoint |
| **private** | [eventFlags](#int-eventflags)                   | int                                 |
| **private** | [modifiers](#int-modifiers)                     | int                                 |
| **private** | [buttons](#int-buttons)                         | int                                 |
| **private** | [wheel](#int-wheel)                             | int                                 |

### Methods

| Modifiers  | Method signature                  | Return type                         |
| ---------- | --------------------------------- | ----------------------------------- |
| **public** | [getWhere()](#getwhere)           | org.viktor44.jtvision.core.JtvPoint |
| **public** | [getEventFlags()](#geteventflags) | int                                 |
| **public** | [getModifiers()](#getmodifiers)   | int                                 |
| **public** | [getButtons()](#getbuttons)       | int                                 |
| **public** | [getWheel()](#getwheel)           | int                                 |



## Constructors

### `MouseEvent()`

*No method description provided*



### `MouseEvent(JtvPoint where, int eventFlags, int modifiers, int buttons, int wheel)`

Creates a new `MouseEvent` instance.

**Parameters**

- `where` - Mouse position in global (application-level) coordinates (`Where` in the original Pascal record).
  


  The position is expressed in the coordinate system of the top-level
  application view.  Use `TView.makeLocal` to convert to view-local
  coordinates before comparing against view bounds.
- `eventFlags` - Flags describing the nature of this mouse event (jtvision extension,
  replaces the Pascal `Double` boolean field).
  


  Bit values are defined in `EventCodes`:

- `EventCodes#meMouseMoved` \u2014 pointer moved during the current
        interaction sequence
- `EventCodes#meDoubleClick` \u2014 event is a double click
- `EventCodes#meTripleClick` \u2014 event is a triple click

In the original Pascal record only a single `Double: Boolean` field
  was available; check `EventCodes#meDoubleClick` to replicate that
  behaviour.
- `modifiers` - State of modifier keys at the time of the mouse event.
  


  Holds platform-specific modifier bits (Shift, Ctrl, Alt, etc.).
- `buttons` - State of the mouse buttons at the time of the event
  


  Bit values are defined in `EventCodes`:

- `EventCodes#mbLeftButton` (`$01`) \u2014 left button pressed
- `EventCodes#mbRightButton` (`$02`) \u2014 right button pressed
- `EventCodes#mbMiddleButton` (`$04`) \u2014 middle button pressed
        (jtvision extension)

Example: `(event.getMouse().getButtons() == mbLeftButton)`
- `wheel` - Mouse wheel direction and magnitude
  


  Non-zero only for `EventCodes#evMouseWheel` events.  Direction
  bits are defined in `EventCodes`: `EventCodes#mwUp`,
  `EventCodes#mwDown`, `EventCodes#mwLeft`,
  `EventCodes#mwRight`.
  Pascal record.



## Fields

### `org.viktor44.jtvision.core.JtvPoint where`

Mouse position in global (application-level) coordinates (`Where` in the original Pascal record).



The position is expressed in the coordinate system of the top-level
application view.  Use `TView.makeLocal` to convert to view-local
coordinates before comparing against view bounds.


### `int eventFlags`

Flags describing the nature of this mouse event (jtvision extension,
replaces the Pascal `Double` boolean field).



Bit values are defined in [EventCodes](EventCodes.md):

- [EventCodes.meMouseMoved](EventCodes.md#int-memousemoved) — pointer moved during the current
      interaction sequence
- [EventCodes.meDoubleClick](EventCodes.md#int-medoubleclick) — event is a double click
- [EventCodes.meTripleClick](EventCodes.md#int-metripleclick) — event is a triple click

In the original Pascal record only a single `Double: Boolean` field
was available; check [EventCodes.meDoubleClick](EventCodes.md#int-medoubleclick) to replicate that
behaviour.


### `int modifiers`

State of modifier keys at the time of the mouse event.



Holds platform-specific modifier bits (Shift, Ctrl, Alt, etc.).


### `int buttons`

State of the mouse buttons at the time of the event



Bit values are defined in [EventCodes](EventCodes.md):

- [EventCodes.mbLeftButton](EventCodes.md#int-mbleftbutton) (`$01`) — left button pressed
- [EventCodes.mbRightButton](EventCodes.md#int-mbrightbutton) (`$02`) — right button pressed
- [EventCodes.mbMiddleButton](EventCodes.md#int-mbmiddlebutton) (`$04`) — middle button pressed
      (jtvision extension)

Example: `(event.getMouse().getButtons() == mbLeftButton)`


### `int wheel`

Mouse wheel direction and magnitude



Non-zero only for [EventCodes.evMouseWheel](EventCodes.md#int-evmousewheel) events.  Direction
bits are defined in [EventCodes](EventCodes.md): [EventCodes.mwUp](EventCodes.md#int-mwup),
[EventCodes.mwDown](EventCodes.md#int-mwdown), [EventCodes.mwLeft](EventCodes.md#int-mwleft),
[EventCodes.mwRight](EventCodes.md#int-mwright).
Pascal record.


## Methods

### `getWhere()`

Mouse position in global (application-level) coordinates (`Where` in the original Pascal record).



The position is expressed in the coordinate system of the top-level
application view.  Use `TView.makeLocal` to convert to view-local
coordinates before comparing against view bounds.



### `getEventFlags()`

Flags describing the nature of this mouse event (jtvision extension,
replaces the Pascal `Double` boolean field).



Bit values are defined in [EventCodes](EventCodes.md):

- [EventCodes.meMouseMoved](EventCodes.md#int-memousemoved) — pointer moved during the current
      interaction sequence
- [EventCodes.meDoubleClick](EventCodes.md#int-medoubleclick) — event is a double click
- [EventCodes.meTripleClick](EventCodes.md#int-metripleclick) — event is a triple click

In the original Pascal record only a single `Double: Boolean` field
was available; check [EventCodes.meDoubleClick](EventCodes.md#int-medoubleclick) to replicate that
behaviour.



### `getModifiers()`

State of modifier keys at the time of the mouse event.



Holds platform-specific modifier bits (Shift, Ctrl, Alt, etc.).



### `getButtons()`

State of the mouse buttons at the time of the event



Bit values are defined in [EventCodes](EventCodes.md):

- [EventCodes.mbLeftButton](EventCodes.md#int-mbleftbutton) (`$01`) — left button pressed
- [EventCodes.mbRightButton](EventCodes.md#int-mbrightbutton) (`$02`) — right button pressed
- [EventCodes.mbMiddleButton](EventCodes.md#int-mbmiddlebutton) (`$04`) — middle button pressed
      (jtvision extension)

Example: `(event.getMouse().getButtons() == mbLeftButton)`



### `getWheel()`

Mouse wheel direction and magnitude



Non-zero only for [EventCodes.evMouseWheel](EventCodes.md#int-evmousewheel) events.  Direction
bits are defined in [EventCodes](EventCodes.md): [EventCodes.mwUp](EventCodes.md#int-mwup),
[EventCodes.mwDown](EventCodes.md#int-mwdown), [EventCodes.mwLeft](EventCodes.md#int-mwleft),
[EventCodes.mwRight](EventCodes.md#int-mwright).
Pascal record.



