---
title: "JtvDirEntry"
sidebar_label: "JtvDirEntry"
---

Class JtvDirEntry
=================

```java
public class JtvDirEntry
```


A data object representing a single entry in the directory tree list.



JtvDirEntry pairs a formatted display string (with indentation and prefix
characters that visualise the tree structure) with the absolute path of the
directory it represents. Instances are created internally by
[JtvDirListBox](JtvDirListBox.md) and passed as event payloads in `cmChangeDir`
command events.

org.viktor44.jtvision.dialogs.JtvDirEntry

## Summary

### Constructors

| Visibility | Signature                                                                |
| ---------- | ------------------------------------------------------------------------ |
| public     | [JtvDirEntry(String txt, String dir)](#jtvdirentrystring-txt-string-dir) |

### Fields

| Modifiers         | Field name                         | Type   |
| ----------------- | ---------------------------------- | ------ |
| **private final** | [displayText](#string-displaytext) | String |
| **private final** | [directory](#string-directory)     | String |

### Methods

| Modifiers  | Method signature                    | Return type |
| ---------- | ----------------------------------- | ----------- |
| **public** | [getDisplayText()](#getdisplaytext) | String      |
| **public** | [getDirectory()](#getdirectory)     | String      |



## Constructors

### `JtvDirEntry(String txt, String dir)`

Creates a directory entry with the given display text and path.

**Parameters**

- `txt` - the formatted display string (tree prefix + directory name)
- `dir` - the absolute directory path



## Fields

### `String displayText`

The formatted text shown in the directory list (includes tree-drawing prefix characters).


### `String directory`

The absolute path of the directory this entry represents.


## Methods

### `getDisplayText()`

The formatted text shown in the directory list (includes tree-drawing prefix characters).



### `getDirectory()`

The absolute path of the directory this entry represents.



