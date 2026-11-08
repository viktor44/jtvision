---
title: "JtvNode"
sidebar_label: "JtvNode"
---

Class JtvNode
=============

```java
public class JtvNode
```


A general-purpose tree node used by [JtvOutline](JtvOutline.md) and
[JtvOutlineViewer](JtvOutlineViewer.md).



JtvNode stores a display label and links to a sibling (`next`) and a
child list (`childList`). Children are themselves TNode instances,
linked through their own `next` pointers. The `expanded` flag
controls whether children are visible in the outline viewer.

**Tree structure example**



```
root ──next──► sibling
 │
childList
 │
 child1 ──next──► child2
```


Traversal: start at the root, follow `childList` for depth-first
descent, follow `next` for sibling iteration.

org.viktor44.jtvision.views.JtvNode

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                  |
| ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvNode(String aText)](#jtvnodestring-atext)                                                                                                              |
| public     | [JtvNode(String aText, JtvNode aChildren, JtvNode aNext, boolean initialState)](#jtvnodestring-atext-jtvnode-achildren-jtvnode-anext-boolean-initialstate) |

### Fields

| Modifiers   | Field name                                              | Type                                |
| ----------- | ------------------------------------------------------- | ----------------------------------- |
| **private** | [next](#orgviktor44jtvisionviewsjtvnode-next)           | org.viktor44.jtvision.views.JtvNode |
| **private** | [text](#string-text)                                    | String                              |
| **private** | [childList](#orgviktor44jtvisionviewsjtvnode-childlist) | org.viktor44.jtvision.views.JtvNode |
| **private** | [expanded](#boolean-expanded)                           | boolean                             |

### Methods

| Modifiers  | Method signature                                                  | Return type                         |
| ---------- | ----------------------------------------------------------------- | ----------------------------------- |
| **public** | [getNext()](#getnext)                                             | org.viktor44.jtvision.views.JtvNode |
| **public** | [setNext(JtvNode next)](#setnextjtvnode-next)                     | void                                |
| **public** | [getText()](#gettext)                                             | String                              |
| **public** | [setText(String text)](#settextstring-text)                       | void                                |
| **public** | [getChildList()](#getchildlist)                                   | org.viktor44.jtvision.views.JtvNode |
| **public** | [setChildList(JtvNode childList)](#setchildlistjtvnode-childlist) | void                                |
| **public** | [isExpanded()](#isexpanded)                                       | boolean                             |
| **public** | [setExpanded(boolean expanded)](#setexpandedboolean-expanded)     | void                                |



## Constructors

### `JtvNode(String aText)`

Constructs a leaf node with the given label, no children, no sibling,
and `expanded` set to `true`.

**Parameters**

- `aText` - the display label for this node



### `JtvNode(String aText, JtvNode aChildren, JtvNode aNext, boolean initialState)`

Constructs a fully specified node.

**Parameters**

- `aText` - the display label
- `aChildren` - the first child node, or `null` for a leaf
- `aNext` - the next sibling node, or `null`
- `initialState` - `true` if the node starts expanded



## Fields

### `org.viktor44.jtvision.views.JtvNode next`

The next sibling of this node at the same tree level, or `null` if this is the last sibling.


### `String text`

The text label displayed in the outline viewer for this node.


### `org.viktor44.jtvision.views.JtvNode childList`

The first child of this node, or `null` if the node is a leaf.
Remaining children are reached by following `next` on this node.


### `boolean expanded`

Whether this node's children are currently expanded (visible) in the outline. `true` by default.


## Methods

### `getNext()`

The next sibling of this node at the same tree level, or `null` if this is the last sibling.



### `setNext(JtvNode next)`

The next sibling of this node at the same tree level, or `null` if this is the last sibling.

**Parameters**

- `next`


### `getText()`

The text label displayed in the outline viewer for this node.



### `setText(String text)`

The text label displayed in the outline viewer for this node.

**Parameters**

- `text`


### `getChildList()`

The first child of this node, or `null` if the node is a leaf.
Remaining children are reached by following `next` on this node.



### `setChildList(JtvNode childList)`

The first child of this node, or `null` if the node is a leaf.
Remaining children are reached by following `next` on this node.

**Parameters**

- `childList`


### `isExpanded()`

Whether this node's children are currently expanded (visible) in the outline. `true` by default.



### `setExpanded(boolean expanded)`

Whether this node's children are currently expanded (visible) in the outline. `true` by default.

**Parameters**

- `expanded`


