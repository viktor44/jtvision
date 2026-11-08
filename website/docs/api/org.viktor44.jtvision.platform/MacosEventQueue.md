---
title: "MacosEventQueue"
sidebar_label: "MacosEventQueue"
---

Class MacosEventQueue
=====================

```java
class MacosEventQueue
extends UnixEventQueue
```


macOS implementation of [EventQueue](EventQueue.md).



On macOS the Option key acts as a full substitute for Alt: pressing
Option+letter or Option+digit (with or without Shift) is reported as the
corresponding `VK_*` with `java.awt.event.InputEvent.ALT_DOWN_MASK`
only, so `KeyDownEvent.isAltDown()` returns `true` and the
shortcut renders as `"Alt+..."`.



The OS delivers each Option-modified key as a UTF-8 sequence of the
Unicode glyph it would produce on the US layout (e.g. Option+X → ≈, U+2248).
`macOptionCodeToAlt(int)` reverses that mapping back to the original
AWT virtual-key code.

org.viktor44.jtvision.platform.MacosEventQueue

## Summary

### Constructors

| Visibility | Signature                             |
| ---------- | ------------------------------------- |
|            | [MacosEventQueue()](#macoseventqueue) |

### Methods

| Modifiers     | Method signature                                                      | Return type |
| ------------- | --------------------------------------------------------------------- | ----------- |
| **protected** | [macOptionCodeToAlt(int codePoint)](#macoptioncodetoaltint-codepoint) | int         |



## Constructors

### `MacosEventQueue()`

*No method description provided*



## Methods

### `macOptionCodeToAlt(int codePoint)`

**Overrides:**

`macOptionCodeToAlt(int codePoint)` in `org.viktor44.jtvision.platform.UnixEventQueue`

Maps a Unicode code point produced by macOS Option+key (US keyboard) to
the corresponding AWT virtual-key code. Both Option+key and Shift+Option+key
code points map to the same plain `VK_*` so that the resulting
event always reads as `Alt+key` regardless of Shift state.

**Parameters**

- `codePoint` - the Unicode code point to test

**Returns**

the AWT `VK_*` code, or `0` if not an Option+key glyph



