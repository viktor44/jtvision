---
title: "JtvKeyStroke"
sidebar_label: "JtvKeyStroke"
---

Class JtvKeyStroke
==================

```java
public class JtvKeyStroke
```




org.viktor44.jtvision.core.JtvKeyStroke

## Summary

### Constructors

| Visibility | Signature                                                                          |
| ---------- | ---------------------------------------------------------------------------------- |
| public     | [JtvKeyStroke(int keyCode, int modifiers)](#jtvkeystrokeint-keycode-int-modifiers) |

### Fields

| Modifiers   | Field name                  | Type |
| ----------- | --------------------------- | ---- |
| **private** | [keyCode](#int-keycode)     | int  |
| **private** | [modifiers](#int-modifiers) | int  |

### Methods

| Modifiers         | Method signature                                               | Return type                             |
| ----------------- | -------------------------------------------------------------- | --------------------------------------- |
| **public static** | [of(int keyCode, int modifiers)](#ofint-keycode-int-modifiers) | org.viktor44.jtvision.core.JtvKeyStroke |
| **public static** | [of(int keyCode)](#ofint-keycode)                              | org.viktor44.jtvision.core.JtvKeyStroke |
| **public**        | [getKeyStroke()](#getkeystroke)                                | int                                     |
| **public**        | [toString()](#tostring)                                        | String                                  |
| **public**        | [getKeyCode()](#getkeycode)                                    | int                                     |
| **public**        | [getModifiers()](#getmodifiers)                                | int                                     |



## Constructors

### `JtvKeyStroke(int keyCode, int modifiers)`

*No method description provided*

**Parameters**

- `keyCode`
- `modifiers`


## Fields

### `int keyCode`

AWT virtual-key code of the pressed key
(e.g. `KeyEvent.VK_F1`, `KeyEvent.VK_ESCAPE`).


### `int modifiers`

Modifier-key state at the time of the keystroke.



A bitmask of `InputEvent` extended modifier flags such as
`InputEvent.SHIFT_DOWN_MASK`, `InputEvent.CTRL_DOWN_MASK` and
`InputEvent.ALT_DOWN_MASK`.


## Methods

### `of(int keyCode, int modifiers)`

*No method description provided*

**Parameters**

- `keyCode`
- `modifiers`


### `of(int keyCode)`

*No method description provided*

**Parameters**

- `keyCode`


### `getKeyStroke()`

*No method description provided*



### `toString()`

**Overrides:**

`toString()` in `java.lang.Object`

*No method description provided*



### `getKeyCode()`

AWT virtual-key code of the pressed key
(e.g. `KeyEvent.VK_F1`, `KeyEvent.VK_ESCAPE`).



### `getModifiers()`

Modifier-key state at the time of the keystroke.



A bitmask of `InputEvent` extended modifier flags such as
`InputEvent.SHIFT_DOWN_MASK`, `InputEvent.CTRL_DOWN_MASK` and
`InputEvent.ALT_DOWN_MASK`.



