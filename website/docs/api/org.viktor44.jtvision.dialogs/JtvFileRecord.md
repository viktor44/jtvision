---
title: "JtvFileRecord"
sidebar_label: "JtvFileRecord"
---

Class JtvFileRecord
===================

```java
public class JtvFileRecord
```


An immutable snapshot of a single file-system entry used by [JtvFileList](JtvFileList.md).



JtvFileRecord holds the metadata for one file or directory: its display name,
absolute path, whether it is a directory, its size in bytes, and its last-modified
timestamp. The special name `".."` is used for the parent directory entry.



Instances are created via the static factory methods [fromFile(File)](#fromfilefile-file) and
[parent(String)](#parentstring-parentpath); the default no-arg constructor produces an empty record
suitable as a placeholder.

org.viktor44.jtvision.dialogs.JtvFileRecord

## Summary

### Constructors

| Visibility | Signature                         |
| ---------- | --------------------------------- |
| public     | [JtvFileRecord()](#jtvfilerecord) |

### Fields

| Modifiers   | Field name                           | Type    |
| ----------- | ------------------------------------ | ------- |
| **private** | [name](#string-name)                 | String  |
| **private** | [absolutePath](#string-absolutepath) | String  |
| **private** | [directory](#boolean-directory)      | boolean |
| **private** | [size](#long-size)                   | long    |
| **private** | [lastModified](#long-lastmodified)   | long    |

### Methods

| Modifiers         | Method signature                                      | Return type                                 |
| ----------------- | ----------------------------------------------------- | ------------------------------------------- |
| **public static** | [fromFile(File file)](#fromfilefile-file)             | org.viktor44.jtvision.dialogs.JtvFileRecord |
| **public static** | [parent(String parentPath)](#parentstring-parentpath) | org.viktor44.jtvision.dialogs.JtvFileRecord |
| **public**        | [getName()](#getname)                                 | String                                      |
| **public**        | [getAbsolutePath()](#getabsolutepath)                 | String                                      |
| **public**        | [isDirectory()](#isdirectory)                         | boolean                                     |
| **public**        | [getSize()](#getsize)                                 | long                                        |
| **public**        | [getLastModified()](#getlastmodified)                 | long                                        |



## Constructors

### `JtvFileRecord()`

Creates an empty file record. All fields are initialised to `null` / 0.
Useful as a placeholder before real data is assigned.



## Fields

### `String name`

The file or directory name (not the full path). `".."` denotes the parent directory.


### `String absolutePath`

The absolute path of this entry.


### `boolean directory`

`true` if this entry is a directory.


### `long size`

File size in bytes; 0 for directories.


### `long lastModified`

Last-modified timestamp (milliseconds since epoch); 0 for the parent entry.


## Methods

### `fromFile(File file)`

Creates a file record from an existing `File` object.

**Parameters**

- `file` - the file or directory to snapshot

**Returns**

a populated `JtvFileRecord`



### `parent(String parentPath)`

Creates a synthetic parent-directory record with the name `".."`.

**Parameters**

- `parentPath` - the absolute path of the parent directory

**Returns**

a `JtvFileRecord` representing the parent directory entry



### `getName()`

The file or directory name (not the full path). `".."` denotes the parent directory.



### `getAbsolutePath()`

The absolute path of this entry.



### `isDirectory()`

`true` if this entry is a directory.



### `getSize()`

File size in bytes; 0 for directories.



### `getLastModified()`

Last-modified timestamp (milliseconds since epoch); 0 for the parent entry.



