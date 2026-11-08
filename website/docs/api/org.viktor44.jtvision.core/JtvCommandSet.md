---
title: "JtvCommandSet"
sidebar_label: "JtvCommandSet"
---

Class JtvCommandSet
===================

```java
public class JtvCommandSet
```


A compact set of up to 256 command codes, stored as a 256-bit array (32 bytes).



Every JT Vision view owns a reference to the application-wide current command set
(`JtvView.curCommandSet`).  Commands whose bit is *clear* are disabled, so their
associated menu items and buttons appear greyed out.  Only command codes in the range
`0..255` can be tracked; codes outside that range are silently ignored.



Example — disable a group of window commands:


```
TCommandSet windowCmds = new TCommandSet();
windowCmds.enableCmd(cmNext);
windowCmds.enableCmd(cmPrev);
windowCmds.enableCmd(cmZoom);
windowCmds.enableCmd(cmResize);
windowCmds.enableCmd(cmClose);
disableCommands(windowCmds);
```



org.viktor44.jtvision.core.JtvCommandSet

## Summary

### Constructors

| Visibility | Signature                                                               |
| ---------- | ----------------------------------------------------------------------- |
| public     | [JtvCommandSet()](#jtvcommandset)                                       |
| public     | [JtvCommandSet(JtvCommandSet other)](#jtvcommandsetjtvcommandset-other) |

### Fields

| Modifiers         | Field name         | Type   |
| ----------------- | ------------------ | ------ |
| **private final** | [cmds](#byte-cmds) | byte[] |

### Methods

| Modifiers  | Method signature                                                  | Return type                              |
| ---------- | ----------------------------------------------------------------- | ---------------------------------------- |
| **public** | [has(int cmd)](#hasint-cmd)                                       | boolean                                  |
| **public** | [enableCmd(int cmd)](#enablecmdint-cmd)                           | void                                     |
| **public** | [disableCmd(int cmd)](#disablecmdint-cmd)                         | void                                     |
| **public** | [enableCmd(JtvCommandSet other)](#enablecmdjtvcommandset-other)   | void                                     |
| **public** | [disableCmd(JtvCommandSet other)](#disablecmdjtvcommandset-other) | void                                     |
| **public** | [isEmpty()](#isempty)                                             | boolean                                  |
| **public** | [and(JtvCommandSet other)](#andjtvcommandset-other)               | org.viktor44.jtvision.core.JtvCommandSet |
| **public** | [or(JtvCommandSet other)](#orjtvcommandset-other)                 | org.viktor44.jtvision.core.JtvCommandSet |
| **public** | [assign(JtvCommandSet other)](#assignjtvcommandset-other)         | void                                     |
| **public** | [equals(Object obj)](#equalsobject-obj)                           | boolean                                  |
| **public** | [hashCode()](#hashcode)                                           | int                                      |



## Constructors

### `JtvCommandSet()`

Constructs an empty command set (all commands disabled).



### `JtvCommandSet(JtvCommandSet other)`

Copy constructor.

**Parameters**

- `other` - the command set to copy



## Fields

### `byte[] cmds`

*No description provided*


## Methods

### `has(int cmd)`

Returns `true` if the given command is present (enabled) in this set.

**Parameters**

- `cmd` - command code in the range `0..255`

**Returns**

`true` if the bit for `cmd` is set



### `enableCmd(int cmd)`

Adds a single command to this set.

**Parameters**

- `cmd` - command code in the range `0..255`



### `disableCmd(int cmd)`

Removes a single command from this set.

**Parameters**

- `cmd` - command code in the range `0..255`



### `enableCmd(JtvCommandSet other)`

Adds all commands from `other` to this set (bitwise OR in place).

**Parameters**

- `other` - the commands to enable



### `disableCmd(JtvCommandSet other)`

Removes all commands that are present in `other` from this set (bitwise AND-NOT in place).

**Parameters**

- `other` - the commands to disable



### `isEmpty()`

Returns `true` if no command bits are set.

**Returns**

`true` if the set is empty



### `and(JtvCommandSet other)`

Returns a new command set containing only the commands present in both this set and `other`.

**Parameters**

- `other` - the set to intersect with

**Returns**

bitwise AND of the two sets



### `or(JtvCommandSet other)`

Returns a new command set containing all commands present in either this set or `other`.

**Parameters**

- `other` - the set to union with

**Returns**

bitwise OR of the two sets



### `assign(JtvCommandSet other)`

Copies the contents of `other` into this set, replacing all current bits.

**Parameters**

- `other` - source command set



### `equals(Object obj)`

*No method description provided*

**Parameters**

- `obj`


### `hashCode()`

**Overrides:**

`hashCode()` in `java.lang.Object`

*No method description provided*



