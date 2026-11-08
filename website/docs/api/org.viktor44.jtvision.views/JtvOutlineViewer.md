---
title: "JtvOutlineViewer"
sidebar_label: "JtvOutlineViewer"
---

Class JtvOutlineViewer
======================

```java
public abstract class JtvOutlineViewer
extends JtvScroller
```





Abstract base class for tree-outline views.



JtvOutlineViewer extends [JtvScroller](JtvScroller.md) to display a hierarchical tree
structure, with each node rendered as a graph prefix (branch lines and
expand/collapse glyphs) followed by the node's text label.

**Tree model**

The concrete subclass supplies the tree by implementing the abstract
accessor methods ([getRoot()](#getroot), [getNext(JtvNode)](#getnextjtvnode-node),
[getChild(JtvNode, int)](#getchildjtvnode-node-int-i), [getNumChildren(JtvNode)](#getnumchildrenjtvnode-node),
[getText(JtvNode)](#gettextjtvnode-node), [hasChildren(JtvNode)](#haschildrenjtvnode-node),
[isExpanded(JtvNode)](#isexpandedjtvnode-node)). The viewer itself does not own the data.
Mutations (expand/collapse) are delegated back to the subclass via
[adjust(JtvNode, boolean)](#adjustjtvnode-node-boolean-expand).

**Rendering**

[update()](#update) traverses the visible (expanded) nodes into a flat list,
computes the maximum line width, and updates the scroll limit. [draw()](#draw)
renders the visible window using that flat list. Graph prefixes are produced
by [getGraph(int, long, int)](#getgraphint-level-long-lines-int-flags), which delegates to the lower-level
[createGraph(int, long, int, int, int, char[])](#creategraphint-level-long-lines-int-flags-int-levwidth-int-endwidth-char-chars) for the
actual ASCII-art tree connector rendering.

**Node flags**

- [ovExpanded](#int-ovexpanded) — the node is expanded (its children are visible).
- [ovChildren](#int-ovchildren) — the node has children.
- [ovLast](#int-ovlast) — the node is the last sibling at its level.

**Keyboard and mouse interaction**

Arrow keys, Page Up/Down, Home, End, Ctrl+Page Up/Down navigate the focus.
Enter double-click broadcasts `cmOutlineItemSelected`. The `+`,
`-`, and `*` keys expand, collapse, or recursively expand the
focused node. Clicking within the graph prefix area toggles expansion;
clicking on the text area moves the focus.

**Colour palette**

The four-entry palette `cpOutlineViewer` maps to:

1. `0x06` — normal text.
2. `0x07` — focused item.
3. `0x03` — selected item.
4. `0x08` — graph/connector colour.



org.viktor44.jtvision.views.JtvOutlineViewer

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                 |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvOutlineViewer(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar)](#jtvoutlineviewerjtvrect-bounds-jtvscrollbar-ahscrollbar-jtvscrollbar-avscrollbar) |

### Fields

| Modifiers                  | Field name                                                            | Type                                  |
| -------------------------- | --------------------------------------------------------------------- | ------------------------------------- |
| **protected static final** | [ovExpanded](#int-ovexpanded)                                         | int                                   |
| **protected static final** | [ovChildren](#int-ovchildren)                                         | int                                   |
| **protected static final** | [ovLast](#int-ovlast)                                                 | int                                   |
| **private static final**   | [cpOutlineViewer](#orgviktor44jtvisioncorejtvpalette-cpoutlineviewer) | org.viktor44.jtvision.core.JtvPalette |
| **protected**              | [foc](#int-foc)                                                       | int                                   |

### Methods

| Modifiers           | Method signature                                                                                                                                                                                                                                                       | Return type                                                    |
| ------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------- |
| **public abstract** | [adjust(JtvNode node, boolean expand)](#adjustjtvnode-node-boolean-expand)                                                                                                                                                                                             | void                                                           |
| **public abstract** | [getNext(JtvNode node)](#getnextjtvnode-node)                                                                                                                                                                                                                          | org.viktor44.jtvision.views.JtvNode                            |
| **public abstract** | [getChild(JtvNode node, int i)](#getchildjtvnode-node-int-i)                                                                                                                                                                                                           | org.viktor44.jtvision.views.JtvNode                            |
| **public abstract** | [getNumChildren(JtvNode node)](#getnumchildrenjtvnode-node)                                                                                                                                                                                                            | int                                                            |
| **public abstract** | [getRoot()](#getroot)                                                                                                                                                                                                                                                  | org.viktor44.jtvision.views.JtvNode                            |
| **public abstract** | [getText(JtvNode node)](#gettextjtvnode-node)                                                                                                                                                                                                                          | String                                                         |
| **public abstract** | [hasChildren(JtvNode node)](#haschildrenjtvnode-node)                                                                                                                                                                                                                  | boolean                                                        |
| **public abstract** | [isExpanded(JtvNode node)](#isexpandedjtvnode-node)                                                                                                                                                                                                                    | boolean                                                        |
| **public**          | [getPalette()](#getpalette)                                                                                                                                                                                                                                            | org.viktor44.jtvision.core.JtvPalette                          |
| **public**          | [getGraph(int level, long lines, int flags)](#getgraphint-level-long-lines-int-flags)                                                                                                                                                                                  | String                                                         |
| **protected**       | [createGraph(int level, long lines, int flags, int levWidth, int endWidth, char[] chars)](#creategraphint-level-long-lines-int-flags-int-levwidth-int-endwidth-char-chars)                                                                                             | String                                                         |
| **public**          | [focused(int i)](#focusedint-i)                                                                                                                                                                                                                                        | void                                                           |
| **public**          | [isSelected(int i)](#isselectedint-i)                                                                                                                                                                                                                                  | boolean                                                        |
| **public**          | [selected(int i)](#selectedint-i)                                                                                                                                                                                                                                      | void                                                           |
| **public**          | [update()](#update)                                                                                                                                                                                                                                                    | void                                                           |
| **public**          | [draw()](#draw)                                                                                                                                                                                                                                                        | void                                                           |
| **protected**       | [getNode(int position)](#getnodeint-position)                                                                                                                                                                                                                          | org.viktor44.jtvision.views.JtvNode                            |
| **protected**       | [adjustFocus(int newFocus)](#adjustfocusint-newfocus)                                                                                                                                                                                                                  | void                                                           |
| **public**          | [expandAll(JtvNode node)](#expandalljtvnode-node)                                                                                                                                                                                                                      | void                                                           |
| **public**          | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable)                                                                                                                                                                                             | void                                                           |
| **public**          | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                                                                                                                                                                                              | void                                                           |
| **private**         | [getGraphForPosition(int pos)](#getgraphforpositionint-pos)                                                                                                                                                                                                            | String                                                         |
| **private**         | [visibleNodes()](#visiblenodes)                                                                                                                                                                                                                                        | List&lt;org.viktor44.jtvision.views.JtvOutlineViewer.VisibleNode> |
| **private**         | [traverseInto(JtvNode cur, int level, long lines, boolean lastChild, List&lt;org.viktor44.jtvision.views.JtvOutlineViewer.VisibleNode> out)](#traverseintojtvnode-cur-int-level-long-lines-boolean-lastchild-listorgviktor44jtvisionviewsjtvoutlineviewervisiblenode-out) | void                                                           |



## Constructors

### `JtvOutlineViewer(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar)`

Constructs an outline viewer with the given bounds and optional scroll
bars. Sets `ofSelectable` so the viewer can receive focus.

**Parameters**

- `bounds` - the bounding rectangle
- `aHScrollBar` - the horizontal scroll bar, or `null`
- `aVScrollBar` - the vertical scroll bar, or `null`



## Fields

### `int ovExpanded`

Node flag: the node is currently expanded and its children are shown.


### `int ovChildren`

Node flag: the node has at least one child.


### `int ovLast`

Node flag: the node is the last sibling in its parent's child list.
When set, the connector uses a corner glyph (`\`) rather than
a T-junction (`+`).


### `org.viktor44.jtvision.core.JtvPalette cpOutlineViewer`

Four-entry colour palette:

1. `0x06` — normal text.
2. `0x07` — focused item.
3. `0x03` — selected item.
4. `0x08` — graph/connector colour.




### `int foc`

The index in the flat visible-node list of the currently focused node.


## Methods

### `adjust(JtvNode node, boolean expand)`

Expands or collapses `node` according to `expand`.
Subclasses implement this to mutate the underlying data model.

**Parameters**

- `node` - the node to adjust
- `expand` - `true` to expand, `false` to collapse



### `getNext(JtvNode node)`

Returns the next sibling of `node` at the same tree level,
or `null` if there is no next sibling.

**Parameters**

- `node` - the reference node

**Returns**

the next sibling, or `null`



### `getChild(JtvNode node, int i)`

Returns the `i`-th child of `node` (0-based index).

**Parameters**

- `node` - the parent node
- `i` - the zero-based child index

**Returns**

the child node at index `i`, or `null`



### `getNumChildren(JtvNode node)`

Returns the number of direct children of `node`.

**Parameters**

- `node` - the node to query

**Returns**

the child count



### `getRoot()`

Returns the root node of the tree. The outline traversal starts from
this node.

**Returns**

the root node, or `null` for an empty tree



### `getText(JtvNode node)`

Returns the display text for `node`.

**Parameters**

- `node` - the node to describe

**Returns**

the node's text label



### `hasChildren(JtvNode node)`

Returns `true` if `node` has at least one child.

**Parameters**

- `node` - the node to test

**Returns**

`true` if the node has children



### `isExpanded(JtvNode node)`

Returns `true` if `node`'s children are currently visible
(expanded).

**Parameters**

- `node` - the node to test

**Returns**

`true` if the node is expanded



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvScroller`

Returns the outline viewer's colour palette `cpOutlineViewer`.

**Returns**

the four-entry palette



### `getGraph(int level, long lines, int flags)`

Generates the ASCII-art graph prefix string for a node at the given
depth using default connector characters.



The character set is `{' ', '|', '+', '\\', '-', '-', '+', '-'}`
with a level width of 3 and an end section width of 3.

**Parameters**

- `level` - the zero-based depth of the node in the tree
- `lines` - a bitmask where bit `l` is set if level `l`
               has a continuing vertical line (i.e. the ancestor at that
               level is not the last sibling)
- `flags` - a combination of `#ovExpanded`, `#ovChildren`,
               and `#ovLast`

**Returns**

the graph prefix string



### `createGraph(int level, long lines, int flags, int levWidth, int endWidth, char[] chars)`

Builds the graph prefix string using the supplied character set and
width parameters.



The prefix has two parts:

1. **Level columns** (`level × levWidth` characters): for
      each ancestor level, a `chars[1]` (`|`) vertical bar
      if that level has more siblings, or `chars[0]` (space)
      otherwise, followed by spaces to fill `levWidth`.
2. **End section** (`endWidth` characters): a corner/T
      glyph, optional dashes, a child indicator, and an expand/collapse
      glyph.



**Parameters**

- `level` - the zero-based depth of the node
- `lines` - bitmask of levels with continuing vertical lines
- `flags` - `#ovExpanded`, `#ovChildren`, `#ovLast`
- `levWidth` - number of characters per ancestor level column
- `endWidth` - number of characters in the end (connector) section
- `chars` - eight-character glyph array:
                  `[space, vbar, tJunction, corner, dash, dash, closedBox, dash]`

**Returns**

the assembled graph prefix string



### `focused(int i)`

Sets the focused index to `i` without scrolling or redrawing.
Called internally during focus adjustments.

**Parameters**

- `i` - the new focused index in the flat visible-node list



### `isSelected(int i)`

Returns `true` if visible-node index `i` equals the current
focus index [foc](#int-foc).

**Parameters**

- `i` - the index to test

**Returns**

`true` if the node at `i` is focused



### `selected(int i)`

Broadcasts `cmOutlineItemSelected` with the node at position
`i` as the info pointer, notifying the owner that the user has
activated an item.

**Parameters**

- `i` - the visible-node index of the selected node



### `update()`

Rebuilds the flat visible-node list, recalculates the maximum line
width, updates the scroll limit via [setLimit(int, int)](JtvScroller.md#setlimitint-x-int-y), and
clamps the focus into the new range.



Must be called after any structural change to the tree (expand, collapse,
insert, or delete).



### `draw()`

Renders the visible rows of the outline.



For each visible row, computes the corresponding visible-node index as
`row + delta.y`. The graph prefix and text are drawn with a colour
determined by whether the node is focused (`sfFocused`), selected,
or normal. The horizontal scroll offset (`delta.x`) is applied
to both the graph and text portions.



### `getNode(int position)`

Returns the [JtvNode](JtvNode.md) at the given visible-node `position`,
or `null` if the position is out of range.

**Parameters**

- `position` - the zero-based index in the flat visible-node list

**Returns**

the node, or `null`



### `adjustFocus(int newFocus)`

Clamps `newFocus` to `[0, limit.y - 1]`, updates
[foc](#int-foc) via [focused(int)](#focusedint-i), and scrolls the viewport
vertically to ensure the focused row is visible.

**Parameters**

- `newFocus` - the desired focus index



### `expandAll(JtvNode node)`

Recursively expands `node` and all of its descendants.
After calling this method, [update()](#update) and [drawView()](JtvView.md#drawview)
should be called to refresh the display.

**Parameters**

- `node` - the root of the subtree to expand; `null` is a no-op



### `setState(int aState, boolean enable)`

**Overrides:**

`setState(int aState, boolean enable)` in `org.viktor44.jtvision.views.JtvScroller`

Propagates the state change and redraws when `sfFocused` changes,
so the focused-item highlight colour is updated immediately.

**Parameters**

- `aState` - the state bits to modify
- `enable` - `true` to set; `false` to clear



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvScroller`

Handles mouse clicks and keyboard navigation.



**Mouse (`evMouseDown`):** tracks the pointer with auto-scroll,
updating focus via [adjustFocus(int)](#adjustfocusint-newfocus). A double-click fires
[selected(int)](#selectedint-i). A single click within the graph prefix area
toggles the node's expand/collapse state via [adjust(JtvNode, boolean)](#adjustjtvnode-node-boolean-expand).



**Keyboard (`evKeyDown`):**

- Up/Left — move focus up one row.
- Down/Right — move focus down one row.
- Page Up/Down — move focus by `size.y - 1`.
- Home/End — move to first/last visible row.
- Ctrl+Page Up/Down — move to absolute first/last node.
- Enter — fire [selected(int)](#selectedint-i).
- `+`/`-` — expand or collapse focused node.
- `*` — recursively expand focused subtree.



**Parameters**

- `event` - the incoming event



### `getGraphForPosition(int pos)`

Returns the graph-prefix string for the visible node at `pos`.
Used during mouse handling to determine whether a click landed in the
graph area (triggering expand/collapse) or the text area.

**Parameters**

- `pos` - the visible-node index

**Returns**

the graph prefix string, or an empty string if out of range



### `visibleNodes()`

Builds the flat list of currently visible (i.e. reachable through
expanded ancestors) nodes by performing a depth-first traversal
starting from [getRoot()](#getroot).

**Returns**

ordered list of visible nodes from top to bottom



### `traverseInto(JtvNode cur, int level, long lines, boolean lastChild, List<org.viktor44.jtvision.views.JtvOutlineViewer.VisibleNode> out)`

Recursively adds `cur` and, if expanded, its subtree to
`out`. After processing the root's subtree, iterates over
the root's siblings (returned by [getNext(JtvNode)](#getnextjtvnode-node)) at the
same level.

**Parameters**

- `cur` - the node to visit
- `level` - the zero-based depth of `cur`
- `lines` - bitmask of ancestor levels with continuing vertical lines
- `lastChild` - `true` if `cur` is the last sibling
- `out` - the accumulator list



