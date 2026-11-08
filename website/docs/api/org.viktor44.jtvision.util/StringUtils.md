---
title: "StringUtils"
sidebar_label: "StringUtils"
---

Class StringUtils
=================

```java
public final class StringUtils
```


String utility functions ported from JT Vision.

org.viktor44.jtvision.util.StringUtils

## Summary

### Constructors

| Visibility | Signature                     |
| ---------- | ----------------------------- |
| private    | [StringUtils()](#stringutils) |

### Methods

| Modifiers         | Method signature                          | Return type |
| ----------------- | ----------------------------------------- | ----------- |
| **public static** | [hotKey(String s)](#hotkeystring-s)       | char        |
| **public static** | [hotKeyStr(String s)](#hotkeystrstring-s) | String      |
| **public static** | [cstrLen(String s)](#cstrlenstring-s)     | int         |
| **public static** | [strWidth(String s)](#strwidthstring-s)   | int         |



## Constructors

### `StringUtils()`

*No method description provided*



## Methods

### `hotKey(String s)`

Extract the hotkey character from a string with ~ delimiters. e.g., "~F~ile" returns 'F'.

**Parameters**

- `s`


### `hotKeyStr(String s)`

Return the hotkey substring from a ~-delimited string

**Parameters**

- `s` - - string

**Returns**

the hotkey substring from a ~-delimited string



### `cstrLen(String s)`

Calculate the display width of a CStr (string with ~ hotkey markers removed).

**Parameters**

- `s`


### `strWidth(String s)`

Calculate the display width of a plain string.

**Parameters**

- `s`


