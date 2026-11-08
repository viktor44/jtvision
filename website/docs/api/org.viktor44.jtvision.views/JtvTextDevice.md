---
title: "JtvTextDevice"
sidebar_label: "JtvTextDevice"
---

Class JtvTextDevice
===================

```java
public abstract class JtvTextDevice
extends JtvScroller
```


Abstract base class for text-output views that extend [JtvScroller](JtvScroller.md).



JtvTextDevice provides a minimal streaming-text interface on top of a
scrollable viewport. Concrete subclasses implement [doSputn(String)](#dosputnstring-s)
to store and display the incoming text; JtvTextDevice supplies the higher-level
[write(String)](#writestring-s) entry point and the [overflow(int)](#overflowint-c) overflow
callback.



The primary concrete implementation is [JtvTerminal](JtvTerminal.md), which maintains
a line-oriented ring buffer and redraws itself as text arrives.

org.viktor44.jtvision.views.JtvTextDevice

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                           |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvTextDevice(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar)](#jtvtextdevicejtvrect-bounds-jtvscrollbar-ahscrollbar-jtvscrollbar-avscrollbar) |

### Methods

| Modifiers           | Method signature                      | Return type |
| ------------------- | ------------------------------------- | ----------- |
| **public abstract** | [doSputn(String s)](#dosputnstring-s) | int         |
| **public**          | [overflow(int c)](#overflowint-c)     | int         |
| **public**          | [write(String s)](#writestring-s)     | int         |



## Constructors

### `JtvTextDevice(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar)`

Constructs a text device with the given bounds and optional scroll bars.
Delegates directly to `JtvScroller.TScroller(JtvRect, JtvScrollBar, JtvScrollBar)`.

**Parameters**

- `bounds` - the bounding rectangle
- `aHScrollBar` - the horizontal scroll bar, or `null`
- `aVScrollBar` - the vertical scroll bar, or `null`



## Methods

### `doSputn(String s)`

Writes the string `s` to the underlying text store and updates
the display. This is the core output primitive that subclasses must
implement.

**Parameters**

- `s` - the string to output

**Returns**

the number of characters actually written



### `overflow(int c)`

Overflow callback invoked when the output buffer is full. If `c`
is non-negative it is converted to a character and written via
[doSputn(String)](#dosputnstring-s).

**Parameters**

- `c` - the character code to emit, or a negative value to skip output

**Returns**

`1` always



### `write(String s)`

Writes the string `s` via [doSputn(String)](#dosputnstring-s).
Returns `0` immediately for `null` or empty input.

**Parameters**

- `s` - the string to write

**Returns**

the number of characters written, or `0` if `s`
        is `null` or empty



