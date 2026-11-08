---
title: "MessageEvent"
sidebar_label: "MessageEvent"
---

Class MessageEvent
==================

```java
public class MessageEvent
```


Payload of a message-family event (`evCommand`, `evBroadcast`, or a
user-defined message).



This class corresponds to the `evMessage` variant of the Pascal
`TEvent` record.  It is active when [JtvEvent.getWhat()](JtvEvent.md#getwhat) is in the
[EventCodes.evMessage](EventCodes.md#int-evmessage) mask.



The original Pascal record stored the extra payload as an overlay of several
types (`InfoPtr`, `InfoLong`, `InfoWord`, …).  Here the most
commonly used overlays are exposed as separate fields.

org.viktor44.jtvision.core.MessageEvent

## Summary

### Constructors

| Visibility | Signature                                                                                                                                  |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| public     | [MessageEvent(int command, Object infoPtr)](#messageeventint-command-object-infoptr)                                                       |
| public     | [MessageEvent()](#messageevent)                                                                                                            |
| public     | [MessageEvent(int command, Object infoPtr, int infoInt, long infoLong)](#messageeventint-command-object-infoptr-int-infoint-long-infolong) |

### Fields

| Modifiers   | Field name                 | Type   |
| ----------- | -------------------------- | ------ |
| **private** | [command](#int-command)    | int    |
| **private** | [infoPtr](#object-infoptr) | Object |
| **private** | [infoInt](#int-infoint)    | int    |
| **private** | [infoLong](#long-infolong) | long   |

### Methods

| Modifiers  | Method signature                                        | Return type |
| ---------- | ------------------------------------------------------- | ----------- |
| **public** | [getCommand()](#getcommand)                             | int         |
| **public** | [setCommand(int command)](#setcommandint-command)       | void        |
| **public** | [getInfoPtr()](#getinfoptr)                             | Object      |
| **public** | [setInfoPtr(Object infoPtr)](#setinfoptrobject-infoptr) | void        |
| **public** | [getInfoInt()](#getinfoint)                             | int         |
| **public** | [setInfoInt(int infoInt)](#setinfointint-infoint)       | void        |
| **public** | [getInfoLong()](#getinfolong)                           | long        |
| **public** | [setInfoLong(long infoLong)](#setinfolonglong-infolong) | void        |



## Constructors

### `MessageEvent(int command, Object infoPtr)`

Constructs a message event with a command and an object payload.

**Parameters**

- `command` - the command code
- `infoPtr` - the object payload (may be `null`)



### `MessageEvent()`

*No method description provided*



### `MessageEvent(int command, Object infoPtr, int infoInt, long infoLong)`

Creates a new `MessageEvent` instance.

**Parameters**

- `command` - The command code carried by this event.
  


  For `evCommand` events this is the integer constant bound to the
  menu item, status key, or hot key that generated the command.  For
  `evBroadcast` events it identifies the broadcast type (for example
  `cmCommandSetChanged`).  Standard command codes are defined in
  `CommandCodes`.
- `infoPtr` - Arbitrary object payload
  


  Views that generate events often place a reference to themselves or to
  some related object here so that the recipient can identify the source.
  `JtvEvent#clearEvent` sets this field to the view that consumed the
  event, which is used by `Message()` to return that view to the caller.
- `infoInt` - Integer payload
  


  Alternative numeric interpretation of the message payload, used when a
  signed integer value needs to be passed alongside the command code.
- `infoLong` - Long-integer payload
  
  

Alternative numeric interpretation of the message payload for cases that
  require a wider integer value than `#infoInt` can hold.



## Fields

### `int command`

The command code carried by this event.



For `evCommand` events this is the integer constant bound to the
menu item, status key, or hot key that generated the command.  For
`evBroadcast` events it identifies the broadcast type (for example
`cmCommandSetChanged`).  Standard command codes are defined in
[CommandCodes](CommandCodes.md).


### `Object infoPtr`

Arbitrary object payload



Views that generate events often place a reference to themselves or to
some related object here so that the recipient can identify the source.
`JtvEvent.clearEvent` sets this field to the view that consumed the
event, which is used by `Message()` to return that view to the caller.


### `int infoInt`

Integer payload



Alternative numeric interpretation of the message payload, used when a
signed integer value needs to be passed alongside the command code.


### `long infoLong`

Long-integer payload



Alternative numeric interpretation of the message payload for cases that
require a wider integer value than `infoInt` can hold.


## Methods

### `getCommand()`

The command code carried by this event.



For `evCommand` events this is the integer constant bound to the
menu item, status key, or hot key that generated the command.  For
`evBroadcast` events it identifies the broadcast type (for example
`cmCommandSetChanged`).  Standard command codes are defined in
[CommandCodes](CommandCodes.md).



### `setCommand(int command)`

The command code carried by this event.



For `evCommand` events this is the integer constant bound to the
menu item, status key, or hot key that generated the command.  For
`evBroadcast` events it identifies the broadcast type (for example
`cmCommandSetChanged`).  Standard command codes are defined in
[CommandCodes](CommandCodes.md).

**Parameters**

- `command`


### `getInfoPtr()`

Arbitrary object payload



Views that generate events often place a reference to themselves or to
some related object here so that the recipient can identify the source.
`JtvEvent.clearEvent` sets this field to the view that consumed the
event, which is used by `Message()` to return that view to the caller.



### `setInfoPtr(Object infoPtr)`

Arbitrary object payload



Views that generate events often place a reference to themselves or to
some related object here so that the recipient can identify the source.
`JtvEvent.clearEvent` sets this field to the view that consumed the
event, which is used by `Message()` to return that view to the caller.

**Parameters**

- `infoPtr`


### `getInfoInt()`

Integer payload



Alternative numeric interpretation of the message payload, used when a
signed integer value needs to be passed alongside the command code.



### `setInfoInt(int infoInt)`

Integer payload



Alternative numeric interpretation of the message payload, used when a
signed integer value needs to be passed alongside the command code.

**Parameters**

- `infoInt`


### `getInfoLong()`

Long-integer payload



Alternative numeric interpretation of the message payload for cases that
require a wider integer value than `infoInt` can hold.



### `setInfoLong(long infoLong)`

Long-integer payload



Alternative numeric interpretation of the message payload for cases that
require a wider integer value than `infoInt` can hold.

**Parameters**

- `infoLong`


