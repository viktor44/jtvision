---
title: "JtvKey"
sidebar_label: "JtvKey"
---

Class JtvKey
============

```java
public final class JtvKey
```


Keyboard utility methods for the JT Vision UI framework.



Use `java.awt.event.KeyEvent` `VK_*` constants for key codes
and `java.awt.event.InputEvent` `*_DOWN_MASK` constants for
modifier masks directly in event handling code. Compound keystrokes are
encoded as `(modifiers << 16) | keyCode`, matching the value returned
by [KeyDownEvent.getKeyStroke()](KeyDownEvent.md#getkeystroke).



This class retains only [getMetaKeyLabel()](#getmetakeylabel).

org.viktor44.jtvision.core.JtvKey

## Summary

### Constructors

| Visibility | Signature           |
| ---------- | ------------------- |
| private    | [JtvKey()](#jtvkey) |

### Methods

| Modifiers         | Method signature                      | Return type |
| ----------------- | ------------------------------------- | ----------- |
| **public static** | [getMetaKeyLabel()](#getmetakeylabel) | String      |



## Constructors

### `JtvKey()`

*No method description provided*



## Methods

### `getMetaKeyLabel()`

Returns the platform-appropriate label for the Alt/Option modifier key.
Returns `"Option"` on macOS and `"Alt"` on all other platforms.



