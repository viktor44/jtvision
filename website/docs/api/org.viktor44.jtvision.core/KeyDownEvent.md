---
title: "KeyDownEvent"
sidebar_label: "KeyDownEvent"
---

Class KeyDownEvent
==================

```java
public class KeyDownEvent
```


Payload of a keyboard event (`evKeyDown`).



The `keyCode`, `modifiers` and `keyChar` fields carry
the same meaning and values as the corresponding fields of
`java.awt.event.KeyEvent`.

org.viktor44.jtvision.core.KeyDownEvent

## Summary

### Constructors

| Visibility | Signature                                                                                                     |
| ---------- | ------------------------------------------------------------------------------------------------------------- |
| public     | [KeyDownEvent()](#keydownevent)                                                                               |
| public     | [KeyDownEvent(int keyCode, int modifiers, char keyChar)](#keydowneventint-keycode-int-modifiers-char-keychar) |

### Fields

| Modifiers   | Field name                  | Type |
| ----------- | --------------------------- | ---- |
| **private** | [keyCode](#int-keycode)     | int  |
| **private** | [modifiers](#int-modifiers) | int  |
| **private** | [keyChar](#char-keychar)    | char |

### Methods

| Modifiers  | Method signature                            | Return type |
| ---------- | ------------------------------------------- | ----------- |
| **public** | [isShiftDown()](#isshiftdown)               | boolean     |
| **public** | [isCtrlDown()](#isctrldown)                 | boolean     |
| **public** | [isCtrlShiftDown()](#isctrlshiftdown)       | boolean     |
| **public** | [isCtrlAltShiftDown()](#isctrlaltshiftdown) | boolean     |
| **public** | [isAltDown()](#isaltdown)                   | boolean     |
| **public** | [isAltShiftDown()](#isaltshiftdown)         | boolean     |
| **public** | [getKeyStroke()](#getkeystroke)             | int         |
| **public** | [getKeyCode()](#getkeycode)                 | int         |
| **public** | [getModifiers()](#getmodifiers)             | int         |
| **public** | [getKeyChar()](#getkeychar)                 | char        |



## Constructors

### `KeyDownEvent()`

*No method description provided*



### `KeyDownEvent(int keyCode, int modifiers, char keyChar)`

Creates a new `KeyDownEvent` instance.

**Parameters**

- `keyCode` - AWT virtual-key code of the pressed key
  (e.g. `KeyEvent#VK_F1`, `KeyEvent#VK_ESCAPE`).
- `modifiers` - Modifier-key state at the time of the keystroke.
  
  

A bitmask of `InputEvent` extended modifier flags such as
  `InputEvent#SHIFT_DOWN_MASK`, `InputEvent#CTRL_DOWN_MASK` and
  `InputEvent#ALT_DOWN_MASK`.
- `keyChar` - Unicode character produced by the keystroke.
  
  

`KeyEvent#CHAR_UNDEFINED` for non-printable keys (function keys,
  cursor keys, etc.).



## Fields

### `int keyCode`

AWT virtual-key code of the pressed key
(e.g. `KeyEvent.VK_F1`, `KeyEvent.VK_ESCAPE`).


### `int modifiers`

Modifier-key state at the time of the keystroke.



A bitmask of `InputEvent` extended modifier flags such as
`InputEvent.SHIFT_DOWN_MASK`, `InputEvent.CTRL_DOWN_MASK` and
`InputEvent.ALT_DOWN_MASK`.


### `char keyChar`

Unicode character produced by the keystroke.



`KeyEvent.CHAR_UNDEFINED` for non-printable keys (function keys,
cursor keys, etc.).


## Methods

### `isShiftDown()`

Returns `true` if the Shift key was held at the time of the keystroke.



### `isCtrlDown()`

Returns `true` if the Ctrl key was held at the time of the keystroke.



### `isCtrlShiftDown()`

Returns `true` if the Ctrl+Shift keys was held at the time of the keystroke.



### `isCtrlAltShiftDown()`

Returns `true` if the Ctrl+Alt+Shift keys was held at the time of the keystroke.



### `isAltDown()`

Returns `true` if the Alt key was held at the time of the keystroke.



### `isAltShiftDown()`

Returns `true` if the Alt+Shift keys was held at the time of the keystroke.



### `getKeyStroke()`

Returns the combined keystroke encoding `(modifiers << 16) | keyCode`.



Useful for matching against menu / status-line shortcut codes that pack
a virtual key together with its required modifier mask in a single
integer (see [JtvKey](JtvKey.md)).

**Returns**

packed (modifiers, keyCode) value



### `getKeyCode()`

AWT virtual-key code of the pressed key
(e.g. `KeyEvent.VK_F1`, `KeyEvent.VK_ESCAPE`).



### `getModifiers()`

Modifier-key state at the time of the keystroke.



A bitmask of `InputEvent` extended modifier flags such as
`InputEvent.SHIFT_DOWN_MASK`, `InputEvent.CTRL_DOWN_MASK` and
`InputEvent.ALT_DOWN_MASK`.



### `getKeyChar()`

Unicode character produced by the keystroke.



`KeyEvent.CHAR_UNDEFINED` for non-printable keys (function keys,
cursor keys, etc.).



