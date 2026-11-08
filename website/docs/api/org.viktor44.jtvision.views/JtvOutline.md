---
title: "JtvOutline"
sidebar_label: "JtvOutline"
---

Class JtvOutline
================

```java
public class JtvOutline
extends JtvOutlineViewer
```


A concrete [JtvOutlineViewer](JtvOutlineViewer.md) backed by a [JtvNode](JtvNode.md) linked tree.



JtvOutline wires the abstract tree-model methods of [JtvOutlineViewer](JtvOutlineViewer.md)
to the [JtvNode](JtvNode.md) data structure. The tree root is stored in
`root`; siblings are linked via [JtvNode.getNext()](JtvNode.md#getnext), and
children are linked via [JtvNode.getChildList()](JtvNode.md#getchildlist).



Expand/collapse state is maintained on each [JtvNode](JtvNode.md) directly through
[JtvNode.setExpanded(boolean)](JtvNode.md#setexpandedboolean-expanded) and [JtvNode.isExpanded()](JtvNode.md#isexpanded).



After construction, [JtvOutlineViewer.update()](JtvOutlineViewer.md#update) is called automatically
to compute the initial scroll limit and focus position.

org.viktor44.jtvision.views.JtvOutline

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                  |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| public     | [JtvOutline(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, JtvNode aRoot)](#jtvoutlinejtvrect-bounds-jtvscrollbar-ahscrollbar-jtvscrollbar-avscrollbar-jtvnode-aroot) |

### Fields

| Modifiers   | Field name                                    | Type                                |
| ----------- | --------------------------------------------- | ----------------------------------- |
| **private** | [root](#orgviktor44jtvisionviewsjtvnode-root) | org.viktor44.jtvision.views.JtvNode |

### Methods

| Modifiers  | Method signature                                                           | Return type                         |
| ---------- | -------------------------------------------------------------------------- | ----------------------------------- |
| **public** | [adjust(JtvNode node, boolean expand)](#adjustjtvnode-node-boolean-expand) | void                                |
| **public** | [getNumChildren(JtvNode node)](#getnumchildrenjtvnode-node)                | int                                 |
| **public** | [getNext(JtvNode node)](#getnextjtvnode-node)                              | org.viktor44.jtvision.views.JtvNode |
| **public** | [getChild(JtvNode node, int i)](#getchildjtvnode-node-int-i)               | org.viktor44.jtvision.views.JtvNode |
| **public** | [getText(JtvNode node)](#gettextjtvnode-node)                              | String                              |
| **public** | [isExpanded(JtvNode node)](#isexpandedjtvnode-node)                        | boolean                             |
| **public** | [hasChildren(JtvNode node)](#haschildrenjtvnode-node)                      | boolean                             |
| **public** | [getRoot()](#getroot)                                                      | org.viktor44.jtvision.views.JtvNode |



## Constructors

### `JtvOutline(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, JtvNode aRoot)`

Constructs a TOutline with the given bounds, scroll bars, and root node.
Calls [JtvOutlineViewer.update()](JtvOutlineViewer.md#update) to initialise the scroll limit.

**Parameters**

- `bounds` - the bounding rectangle
- `aHScrollBar` - the horizontal scroll bar, or `null`
- `aVScrollBar` - the vertical scroll bar, or `null`
- `aRoot` - the root of the node tree, or `null` for empty



## Fields

### `org.viktor44.jtvision.views.JtvNode root`

The root node of the displayed tree. The remaining top-level nodes are
reachable via successive calls to [JtvNode.getNext()](JtvNode.md#getnext).


## Methods

### `adjust(JtvNode node, boolean expand)`

**Overrides:**

`adjust(JtvNode node, boolean expand)` in `org.viktor44.jtvision.views.JtvOutlineViewer`

Expands or collapses `node` by calling
[JtvNode.setExpanded(boolean)](JtvNode.md#setexpandedboolean-expanded).

**Parameters**

- `node` - the node to adjust; `null` is a no-op
- `expand` - `true` to expand, `false` to collapse



### `getNumChildren(JtvNode node)`

**Overrides:**

`getNumChildren(JtvNode node)` in `org.viktor44.jtvision.views.JtvOutlineViewer`

Returns the number of direct children of `node` by walking
`node.childList` via [JtvNode.getNext()](JtvNode.md#getnext).

**Parameters**

- `node` - the node to query; `null` returns `0`

**Returns**

the child count



### `getNext(JtvNode node)`

**Overrides:**

`getNext(JtvNode node)` in `org.viktor44.jtvision.views.JtvOutlineViewer`

Returns the next sibling of `node` via [JtvNode.getNext()](JtvNode.md#getnext).

**Parameters**

- `node` - the reference node; `null` returns `null`

**Returns**

the next sibling, or `null`



### `getChild(JtvNode node, int i)`

**Overrides:**

`getChild(JtvNode node, int i)` in `org.viktor44.jtvision.views.JtvOutlineViewer`

Returns the `i`-th child of `node` by walking
`node.childList` `i` steps via [JtvNode.getNext()](JtvNode.md#getnext).

**Parameters**

- `node` - the parent node; `null` returns `null`
- `i` - the zero-based child index

**Returns**

the child at index `i`, or `null` if out of range



### `getText(JtvNode node)`

**Overrides:**

`getText(JtvNode node)` in `org.viktor44.jtvision.views.JtvOutlineViewer`

Returns the display text of `node` via [JtvNode.getText()](JtvNode.md#gettext).

**Parameters**

- `node` - the node to describe; `null` returns an empty string

**Returns**

the node's text label, or `""`



### `isExpanded(JtvNode node)`

**Overrides:**

`isExpanded(JtvNode node)` in `org.viktor44.jtvision.views.JtvOutlineViewer`

Returns `true` if `node` is expanded.

**Parameters**

- `node` - the node to test; `null` returns `false`

**Returns**

`true` if the node is expanded



### `hasChildren(JtvNode node)`

**Overrides:**

`hasChildren(JtvNode node)` in `org.viktor44.jtvision.views.JtvOutlineViewer`

Returns `true` if `node` has at least one child
(i.e. `node.childList` is non-`null`).

**Parameters**

- `node` - the node to test; `null` returns `false`

**Returns**

`true` if the node has children



### `getRoot()`

**Overrides:**

`getRoot()` in `org.viktor44.jtvision.views.JtvOutlineViewer`

The root node of the displayed tree. The remaining top-level nodes are
reachable via successive calls to [JtvNode.getNext()](JtvNode.md#getnext).



