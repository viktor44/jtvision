---
title: "JtvColorItem"
sidebar_label: "JtvColorItem"
---

Class JtvColorItem
==================

```java
public class JtvColorItem
```


A named colour-palette item within a [JtvColorGroup](JtvColorGroup.md).



TColorItem represents a single named entry within a [JtvColorGroup](JtvColorGroup.md).
Each item has a display name and a palette index that identifies the
specific colour entry it controls in the application palette.

org.viktor44.jtvision.dialogs.JtvColorItem

## Summary

### Constructors

| Visibility | Signature                                                          |
| ---------- | ------------------------------------------------------------------ |
| public     | [JtvColorItem(String nm, int idx)](#jtvcoloritemstring-nm-int-idx) |

### Fields

| Modifiers   | Field name           | Type   |
| ----------- | -------------------- | ------ |
| **private** | [name](#string-name) | String |
| **private** | [index](#int-index)  | int    |

### Methods

| Modifiers  | Method signature        | Return type |
| ---------- | ----------------------- | ----------- |
| **public** | [getName()](#getname)   | String      |
| **public** | [getIndex()](#getindex) | int         |



## Constructors

### `JtvColorItem(String nm, int idx)`

Creates a colour item.

**Parameters**

- `nm` - the display name
- `idx` - the palette index this item controls



## Fields

### `String name`

The display name shown in the colour item list.


### `int index`

The index into the application palette that this item controls.


## Methods

### `getName()`

The display name shown in the colour item list.



### `getIndex()`

The index into the application palette that this item controls.



