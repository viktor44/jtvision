---
title: "JtvStatusItem"
sidebar_label: "JtvStatusItem"
---

Class JtvStatusItem
===================

```java
public class JtvStatusItem
```


A single entry in a [JtvStatusLine](JtvStatusLine.md) item list.



JtvStatusItem combines a visual label, a keyboard hot key, and a command
into one record. Status items serve two distinct roles:

- **Visual label** — when `text` is non-`null` it is
      drawn on the status line. Clicking on the label area triggers the
      associated `command`.
- **Hot-key binding** — when a key event matching `keyCode`
      arrives, [JtvStatusLine.handleEvent](JtvStatusLine.md#handleeventjtvevent-event) converts it to an
      `evCommand` event with `command`, regardless of whether
      the item is visually shown. An item with a `null` text can
      therefore act as a silent hot-key-to-command mapping.

Items are stored in insertion order in [JtvStatusDef](JtvStatusDef.md) and rendered
by [JtvStatusLine](JtvStatusLine.md) in that same order.

org.viktor44.jtvision.views.JtvStatusItem

## Summary

### Constructors

| Visibility | Signature                                                                                                                             |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvStatusItem(String aText, JtvKeyStroke aKeyStroke, int aCommand)](#jtvstatusitemstring-atext-jtvkeystroke-akeystroke-int-acommand) |

### Fields

| Modifiers         | Field name                                                  | Type                                    |
| ----------------- | ----------------------------------------------------------- | --------------------------------------- |
| **private final** | [text](#string-text)                                        | String                                  |
| **private final** | [keyStroke](#orgviktor44jtvisioncorejtvkeystroke-keystroke) | org.viktor44.jtvision.core.JtvKeyStroke |
| **private final** | [command](#int-command)                                     | int                                     |

### Methods

| Modifiers  | Method signature                | Return type                             |
| ---------- | ------------------------------- | --------------------------------------- |
| **public** | [getText()](#gettext)           | String                                  |
| **public** | [getKeyStroke()](#getkeystroke) | org.viktor44.jtvision.core.JtvKeyStroke |
| **public** | [getCommand()](#getcommand)     | int                                     |



## Constructors

### `JtvStatusItem(String aText, JtvKeyStroke aKeyStroke, int aCommand)`

Creates a status item.

**Parameters**

- `aText` - the label string, or `null` for a hidden hot-key item
- `aKeyStroke` - the hot-key keystroke, or `null`
- `aCommand` - the command to generate on activation



## Fields

### `String text`

The label string displayed on the status line, such as `"~F1~ Help"`,
or `null` if this item is invisible (hot-key binding only).


### `org.viktor44.jtvision.core.JtvKeyStroke keyStroke`

The keystroke that triggers this item's command, or `null` if the item has no hot key.


### `int command`

The `cmXXXX` command constant posted as an `evCommand`
event when the label is clicked or the hot key is pressed.


## Methods

### `getText()`

The label string displayed on the status line, such as `"~F1~ Help"`,
or `null` if this item is invisible (hot-key binding only).



### `getKeyStroke()`

The keystroke that triggers this item's command, or `null` if the item has no hot key.



### `getCommand()`

The `cmXXXX` command constant posted as an `evCommand`
event when the label is clicked or the hot key is pressed.



