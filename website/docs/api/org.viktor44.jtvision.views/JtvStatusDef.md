---
title: "JtvStatusDef"
sidebar_label: "JtvStatusDef"
---

Class JtvStatusDef
==================

```java
public class JtvStatusDef
```


A help-context-sensitive status line definition.



JtvStatusDef pairs a range of help context values (`min` to
`max`) with a list of [JtvStatusItem](JtvStatusItem.md) records. A
[JtvStatusLine](JtvStatusLine.md) holds an ordered list of TStatusDef records and
displays the items belonging to the *first* definition whose
range contains the current help context of the focused view.



**How context selection works**
[JtvStatusLine.update()](JtvStatusLine.md#update) queries the focused view's help context,
then walks the [JtvStatusLine](JtvStatusLine.md)'s definition list and selects the
first JtvStatusDef for which
`helpCtx >= min && helpCtx <= max`. The matched definition's
`items` list is then rendered on the status line.



A catch-all definition covering all contexts can be created with
`min = 0` and `max = Integer.MAX_VALUE`.



JtvStatusDef records are typically added to a status line in sequence:


```
JtvStatusLine line = new JtvStatusLine(bounds,
    new JtvStatusDef(hcNoContext, hcNoContext, defaultItems))
        .addDefinition(new JtvStatusDef(hcFileOpen, hcFileOpen, fileOpenItems));
```



org.viktor44.jtvision.views.JtvStatusDef

## Summary

### Constructors

| Visibility | Signature                                                      |
| ---------- | -------------------------------------------------------------- |
| public     | [JtvStatusDef(int min, int max)](#jtvstatusdefint-min-int-max) |
| public     | [JtvStatusDef()](#jtvstatusdef)                                |

### Fields

| Modifiers         | Field name                                                | Type                                            |
| ----------------- | --------------------------------------------------------- | ----------------------------------------------- |
| **private final** | [min](#int-min)                                           | int                                             |
| **private final** | [max](#int-max)                                           | int                                             |
| **private final** | [items](#listorgviktor44jtvisionviewsjtvstatusitem-items) | List&lt;org.viktor44.jtvision.views.JtvStatusItem> |

### Methods

| Modifiers  | Method signature                                          | Return type                                     |
| ---------- | --------------------------------------------------------- | ----------------------------------------------- |
| **public** | [addItem(JtvStatusItem item)](#additemjtvstatusitem-item) | org.viktor44.jtvision.views.JtvStatusDef        |
| **public** | [getMin()](#getmin)                                       | int                                             |
| **public** | [getMax()](#getmax)                                       | int                                             |
| **public** | [getItems()](#getitems)                                   | List&lt;org.viktor44.jtvision.views.JtvStatusItem> |



## Constructors

### `JtvStatusDef(int min, int max)`

Creates a status definition.

**Parameters**

- `min` - the minimum help context (inclusive)
- `max` - the maximum help context (inclusive)



### `JtvStatusDef()`

Creates a status definition.



## Fields

### `int min`

The minimum help context value (inclusive) for which this definition is selected.


### `int max`

The maximum help context value (inclusive) for which this definition is selected.


### `List<org.viktor44.jtvision.views.JtvStatusItem> items`

The status items to be displayed when this definition is active.
Items are rendered in list order.


## Methods

### `addItem(JtvStatusItem item)`

*No method description provided*

**Parameters**

- `item`


### `getMin()`

The minimum help context value (inclusive) for which this definition is selected.



### `getMax()`

The maximum help context value (inclusive) for which this definition is selected.



### `getItems()`

The status items to be displayed when this definition is active.
Items are rendered in list order.



