---
title: "SystemUtils"
sidebar_label: "SystemUtils"
---

Class SystemUtils
=================

```java
public class SystemUtils
```




org.viktor44.jtvision.util.SystemUtils

## Summary

### Constructors

| Visibility | Signature                     |
| ---------- | ----------------------------- |
| public     | [SystemUtils()](#systemutils) |

### Fields

| Modifiers               | Field name                              | Type    |
| ----------------------- | --------------------------------------- | ------- |
| **public static final** | [OS_NAME](#string-os_name)              | String  |
| **public static final** | [OS_ARCH](#string-os_arch)              | String  |
| **public static final** | [IS_OS_MAC](#boolean-is_os_mac)         | boolean |
| **public static final** | [IS_OS_WINDOWS](#boolean-is_os_windows) | boolean |

### Methods

| Modifiers          | Method signature                                                                                     | Return type |
| ------------------ | ---------------------------------------------------------------------------------------------------- | ----------- |
| **private static** | [isOsNameMatch(String osName, String osNamePrefix)](#isosnamematchstring-osname-string-osnameprefix) | boolean     |



## Constructors

### `SystemUtils()`

*No method description provided*



## Fields

### `String OS_NAME`

A constant for the System Property `os.name`. Operating system name.


### `String OS_ARCH`

A constant for the System Property `os.arch`. Operating system architecture.


### `boolean IS_OS_MAC`

The constant `true` if this is Mac.


### `boolean IS_OS_WINDOWS`

The constant `true` if this is Windows.


## Methods

### `isOsNameMatch(String osName, String osNamePrefix)`

Tests whether the operating system matches with a case-insensitive comparison.



This method is package private instead of private to support unit test invocation.


**Parameters**

- `osName` - the actual OS name.
- `osNamePrefix` - the prefix for the expected OS name.

**Returns**

true for a case-insensitive match, or false if not.



