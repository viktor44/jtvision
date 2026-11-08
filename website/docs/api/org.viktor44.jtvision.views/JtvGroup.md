---
title: "JtvGroup"
sidebar_label: "JtvGroup"
---

Class JtvGroup
==============

```java
public class JtvGroup
extends JtvView
```


A container view that owns and manages an ordered list of child views.



JtvGroup extends [JtvView](JtvView.md) to hold a dynamically chained list of
related views. It handles:

- **Insertion and removal** — [insert(JtvView)](#insertjtvview-p),
      [remove(JtvView)](#removejtvview-p).
- **Event routing** — distributes events to children in three
      phases: `phPreProcess`, `phFocused`, `phPostProcess`.
- **Focus management** — [setCurrent(JtvView, int)](#setcurrentjtvview-p-int-mode),
      [resetCurrent()](#resetcurrent), [selectNext(boolean)](#selectnextboolean-forwards).
- **Modal execution** — [execute()](#execute) runs an event loop;
      [execView(JtvView)](#execviewjtvview-p) runs a child view modally.
- **Double-buffered drawing** — when the `ofBuffered` option
      is set (the default), the group draws all children into an off-screen
      [JtvDrawBuffer](../org.viktor44.jtvision.core/JtvDrawBuffer.md) and blits it to the screen in one pass.
- **Resize propagation** — [changeBounds(JtvRect)](#changeboundsjtvrect-bounds) calls
      [JtvView.calcBounds](JtvView.md#calcboundsjtvrect-bounds-jtvpoint-delta) and [JtvView.changeBounds](JtvView.md#changeboundsjtvrect-bounds) on every
      child according to its grow-mode flags.

The child list is a circular singly-linked ring maintained through
[JtvView.next](JtvView.md#orgviktor44jtvisionviewsjtvview-next). [last](#orgviktor44jtvisionviewsjtvview-last) points to the back of the list
(the view with the lowest z-order); [first()](#first) returns the
front (highest z-order). Views are inserted at the front by default.



Groups are created with `ofSelectable | ofBuffered` and an
[eventMask](JtvView.md#int-eventmask) of `0xFFFF` so they receive all event types.

org.viktor44.jtvision.views.JtvGroup

## Summary

### Constructors

| Visibility | Signature                                           |
| ---------- | --------------------------------------------------- |
| public     | [JtvGroup(JtvRect bounds)](#jtvgroupjtvrect-bounds) |

### Fields

| Modifiers                | Field name                                                | Type                                     |
| ------------------------ | --------------------------------------------------------- | ---------------------------------------- |
| **private static final** | [log](#orgslf4jlogger-log)                                | org.slf4j.Logger                         |
| **private static**       | [theTopView](#orgviktor44jtvisionviewsjtvview-thetopview) | org.viktor44.jtvision.views.JtvView      |
| **private**              | [current](#orgviktor44jtvisionviewsjtvview-current)       | org.viktor44.jtvision.views.JtvView      |
| **protected**            | [last](#orgviktor44jtvisionviewsjtvview-last)             | org.viktor44.jtvision.views.JtvView      |
| **protected**            | [phase](#int-phase)                                       | int                                      |
| **protected**            | [buffer](#orgviktor44jtvisioncorejtvdrawbuffer-buffer)    | org.viktor44.jtvision.core.JtvDrawBuffer |
| **protected**            | [lockFlag](#int-lockflag)                                 | int                                      |
| **protected**            | [endState](#int-endstate)                                 | int                                      |
| **protected**            | [clip](#orgviktor44jtvisioncorejtvrect-clip)              | org.viktor44.jtvision.core.JtvRect       |

### Methods

| Modifiers         | Method signature                                                                                                                                                                                                                                                                                       | Return type                         |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ----------------------------------- |
| **public**        | [awaken()](#awaken)                                                                                                                                                                                                                                                                                    | void                                |
| **public**        | [changeBounds(JtvRect bounds)](#changeboundsjtvrect-bounds)                                                                                                                                                                                                                                            | void                                |
| **public**        | [remove(JtvView p)](#removejtvview-p)                                                                                                                                                                                                                                                                  | void                                |
| **public**        | [draw()](#draw)                                                                                                                                                                                                                                                                                        | void                                |
| **protected**     | [drawSubViews(JtvView p, JtvView bottom)](#drawsubviewsjtvview-p-jtvview-bottom)                                                                                                                                                                                                                       | void                                |
| **public**        | [endModal(int command)](#endmodalint-command)                                                                                                                                                                                                                                                          | void                                |
| **public**        | [eventError(JtvEvent event)](#eventerrorjtvevent-event)                                                                                                                                                                                                                                                | void                                |
| **public**        | [execute()](#execute)                                                                                                                                                                                                                                                                                  | int                                 |
| **protected**     | [handleException(Throwable ex)](#handleexceptionthrowable-ex)                                                                                                                                                                                                                                          | void                                |
| **public**        | [execView(JtvView p)](#execviewjtvview-p)                                                                                                                                                                                                                                                              | int                                 |
| **public**        | [first()](#first)                                                                                                                                                                                                                                                                                      | org.viktor44.jtvision.views.JtvView |
| **public**        | [findNext(boolean forwards)](#findnextboolean-forwards)                                                                                                                                                                                                                                                | org.viktor44.jtvision.views.JtvView |
| **public**        | [focusNext(boolean forwards)](#focusnextboolean-forwards)                                                                                                                                                                                                                                              | boolean                             |
| **public**        | [firstMatch(int state, int aOptions)](#firstmatchint-state-int-aoptions)                                                                                                                                                                                                                               | org.viktor44.jtvision.views.JtvView |
| **public**        | [freeBuffer()](#freebuffer)                                                                                                                                                                                                                                                                            | void                                |
| **private**       | [initBuffer()](#initbuffer)                                                                                                                                                                                                                                                                            | void                                |
| **public**        | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                                                                                                                                                                                                                              | void                                |
| **private**       | [doHandleEvent(JtvView p, JtvEvent event)](#dohandleeventjtvview-p-jtvevent-event)                                                                                                                                                                                                                     | void                                |
| **private**       | [forEachHandleEvent(JtvEvent event)](#foreachhandleeventjtvevent-event)                                                                                                                                                                                                                                | void                                |
| **public**        | [insert(JtvView p)](#insertjtvview-p)                                                                                                                                                                                                                                                                  | void                                |
| **private**       | [insertBefore(JtvView p, JtvView target)](#insertbeforejtvview-p-jtvview-target)                                                                                                                                                                                                                       | void                                |
| **protected**     | [insertView(JtvView p, JtvView target)](#insertviewjtvview-p-jtvview-target)                                                                                                                                                                                                                           | void                                |
| **protected**     | [removeView(JtvView p)](#removeviewjtvview-p)                                                                                                                                                                                                                                                          | void                                |
| **protected**     | [lock()](#lock)                                                                                                                                                                                                                                                                                        | void                                |
| **protected**     | [unlock()](#unlock)                                                                                                                                                                                                                                                                                    | void                                |
| **public**        | [redraw()](#redraw)                                                                                                                                                                                                                                                                                    | void                                |
| **public**        | [resetCurrent()](#resetcurrent)                                                                                                                                                                                                                                                                        | void                                |
| **public**        | [resetCursor()](#resetcursor)                                                                                                                                                                                                                                                                          | void                                |
| **public**        | [selectNext(boolean forwards)](#selectnextboolean-forwards)                                                                                                                                                                                                                                            | void                                |
| **public**        | [selectView(JtvView p, boolean enable)](#selectviewjtvview-p-boolean-enable)                                                                                                                                                                                                                           | void                                |
| **public**        | [focusView(JtvView p, boolean enable)](#focusviewjtvview-p-boolean-enable)                                                                                                                                                                                                                             | void                                |
| **public**        | [setCurrent(JtvView p, int mode)](#setcurrentjtvview-p-int-mode)                                                                                                                                                                                                                                       | void                                |
| **public**        | [setDataFrom(Object rec)](#setdatafromobject-rec)                                                                                                                                                                                                                                                      | void                                |
| **public**        | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable)                                                                                                                                                                                                                             | void                                |
| **public**        | [valid(int command)](#validint-command)                                                                                                                                                                                                                                                                | boolean                             |
| **public**        | [getHelpCtx()](#gethelpctx)                                                                                                                                                                                                                                                                            | int                                 |
| **public**        | [forEach(function.Consumer&lt;org.viktor44.jtvision.views.JtvView> action)](#foreachfunctionconsumerorgviktor44jtvisionviewsjtvview-action)                                                                                                                                                               | void                                |
| **public**        | [firstThat(function.Predicate&lt;org.viktor44.jtvision.views.JtvView> test)](#firstthatfunctionpredicateorgviktor44jtvisionviewsjtvview-test)                                                                                                                                                             | org.viktor44.jtvision.views.JtvView |
| **public**        | [indexOf(JtvView p)](#indexofjtvview-p)                                                                                                                                                                                                                                                                | int                                 |
| **public**        | [at(int index)](#atint-index)                                                                                                                                                                                                                                                                          | org.viktor44.jtvision.views.JtvView |
| **protected**     | [writeClipped(JtvView target, int x1, int x2, int y, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset, boolean inShadow)](#writeclippedjtvview-target-int-x1-int-x2-int-y-orgviktor44jtvisioncorejtvscreencell-buf-int-bufoffset-boolean-inshadow)                                        | void                                |
| **private**       | [writeClippedAfter(JtvView after, JtvView target, int x1, int x2, int y, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset, boolean inShadow)](#writeclippedafterjtvview-after-jtvview-target-int-x1-int-x2-int-y-orgviktor44jtvisioncorejtvscreencell-buf-int-bufoffset-boolean-inshadow) | void                                |
| **protected**     | [writeFinal(int x1, int x2, int y, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset, boolean inShadow)](#writefinalint-x1-int-x2-int-y-orgviktor44jtvisioncorejtvscreencell-buf-int-bufoffset-boolean-inshadow)                                                                           | void                                |
| **public static** | [getTheTopView()](#getthetopview)                                                                                                                                                                                                                                                                      | org.viktor44.jtvision.views.JtvView |
| **public**        | [getCurrent()](#getcurrent)                                                                                                                                                                                                                                                                            | org.viktor44.jtvision.views.JtvView |



## Constructors

### `JtvGroup(JtvRect bounds)`

Constructs a group with the given bounds.



Initialises options to `ofSelectable | ofBuffered`,
event mask to `0xFFFF` (all events), phase to
[JtvView.phFocused](JtvView.md#int-phfocused), and clip to the full extent.

**Parameters**

- `bounds` - the initial bounding rectangle



## Fields

### `org.slf4j.Logger log`

*No description provided*


### `org.viktor44.jtvision.views.JtvView theTopView`

The current top-level modal view, or `null` when no modal
view is active. Set and restored by [JtvGroup.execView](#execviewjtvview-p).


### `org.viktor44.jtvision.views.JtvView current`

The currently focused (selected) child view, or `null` if no
child is selected. Set by [setCurrent(JtvView, int)](#setcurrentjtvview-p-int-mode).


### `org.viktor44.jtvision.views.JtvView last`

The last (back-most) view in the circular linked-list of children,
or `null` when the group is empty. The front-most child is
`last.next`.


### `int phase`

The current event-routing phase: [JtvView.phFocused](JtvView.md#int-phfocused),
[JtvView.phPreProcess](JtvView.md#int-phpreprocess), or [JtvView.phPostProcess](JtvView.md#int-phpostprocess).


### `org.viktor44.jtvision.core.JtvDrawBuffer buffer`

The off-screen draw buffer used when `ofBuffered` is set.
Allocated lazily when the group is first drawn; freed when the
group is hidden (`sfExposed` is cleared).


### `int lockFlag`

Lock counter used to defer [drawView()](JtvView.md#drawview) calls while subviews
are being moved or resized. The group is drawn once when this counter
drops back to zero.


### `int endState`

The command code that caused the current modal [execute()](#execute) loop
to exit. Set by [endModal(int)](#endmodalint-command).


### `org.viktor44.jtvision.core.JtvRect clip`

The clipping rectangle used during drawing. Normally equals the group's
extent, but is temporarily narrowed during occlusion-aware redraws.


## Methods

### `awaken()`

**Overrides:**

`awaken()` in `org.viktor44.jtvision.views.JtvView`

Calls [JtvView.awaken()](JtvView.md#awaken) on every child view. Overrides the
no-op in [JtvView](JtvView.md) to propagate the awaken notification
through the whole group hierarchy.



### `changeBounds(JtvRect bounds)`

**Overrides:**

`changeBounds(JtvRect bounds)` in `org.viktor44.jtvision.views.JtvView`

Resizes the group to the new bounds and proportionally adjusts every
child view according to its [JtvView.growMode](JtvView.md#int-growmode) flags.



If the size does not change the group is simply redrawn. If the size
changes, the group locks drawing, recalculates every child's bounds,
then unlocks (triggering a single redraw).

**Parameters**

- `bounds` - the new bounding rectangle



### `remove(JtvView p)`

Removes a child view from this group without destroying it.



The view is temporarily hidden, unlinked, its [JtvView.owner](JtvView.md#orgviktor44jtvisionviewsjtvgroup-owner)
and [JtvView.next](JtvView.md#orgviktor44jtvisionviewsjtvview-next) are cleared, and then it is made visible
again if it was visible before removal.

**Parameters**

- `p` - the child view to remove, or `null` (ignored)



### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the group by blitting its off-screen buffer (if available),
or by re-drawing all child views within the current clip rectangle.
If the buffer does not exist it is lazily allocated here.



### `drawSubViews(JtvView p, JtvView bottom)`

Calls [JtvView.drawView()](JtvView.md#drawview) on every child view from `p`
up to (but not including) `bottom`.

**Parameters**

- `p` - the first child to draw (in list order)
- `bottom` - the child at which to stop, or `null` to draw all



### `endModal(int command)`

**Overrides:**

`endModal(int command)` in `org.viktor44.jtvision.views.JtvView`

Ends the modal state for this group if it is currently modal,
or delegates to the parent via [JtvView.endModal(int)](JtvView.md#endmodalint-command).

**Parameters**

- `command` - the command code to return from `#execute()`



### `eventError(JtvEvent event)`

Called when an event is not consumed by any child view. Delegates
to the owner group, propagating unhandled events up the hierarchy.

**Parameters**

- `event` - the unhandled event



### `execute()`

**Overrides:**

`execute()` in `org.viktor44.jtvision.views.JtvView`

Runs the group's event loop until [valid(int)](#validint-command) returns
`true` for the exit command.



Each iteration fetches one event, routes it through
[handleEvent(JtvEvent)](#handleeventjtvevent-event), and passes unhandled events to
[eventError(JtvEvent)](#eventerrorjtvevent-event). Runtime exceptions are wrapped into
`cmError` command events via `handleRuntimeException`.

**Returns**

the command that caused the loop to exit



### `handleException(Throwable ex)`

Wraps an uncaught `RuntimeException` in a `cmError`
command event and delivers it to [eventError(JtvEvent)](#eventerrorjtvevent-event).
A second exception thrown from the error handler is silently swallowed.

**Parameters**

- `ex` - the runtime exception to report



### `execView(JtvView p)`

Runs a child view `p` as a modal subview of this group.



The method saves the current modal context (top-view, focused child,
command set), promotes `p` to modal, calls its
[JtvView.execute()](JtvView.md#execute) method, and then restores the saved context.
If `p` has no owner it is temporarily inserted into this group
and removed afterwards.

**Parameters**

- `p` - the view to execute modally, or `null` (returns
           `cmCancel` immediately)

**Returns**

the command that ended the modal session



### `first()`

Returns the first (front-most) child view in the group, or
`null` if the group is empty.

**Returns**

the first child view, or `null`



### `findNext(boolean forwards)`

Finds the next focusable sibling after (or before) `current`
in the direction indicated by `forwards`.



A view is focusable if it is visible, not disabled, and has
`ofSelectable` set. Returns `null` if no other focusable
sibling exists.

**Parameters**

- `forwards` - `true` to search forward; `false` backward

**Returns**

the next focusable sibling, or `null`



### `focusNext(boolean forwards)`

Focuses the next selectable sibling in the given direction by calling
[JtvView.focus()](JtvView.md#focus) on the result of [findNext(boolean)](#findnextboolean-forwards).

**Parameters**

- `forwards` - `true` to move focus forward; `false` backward

**Returns**

`true` if focus was successfully transferred or no change
        was needed



### `firstMatch(int state, int aOptions)`

Returns the first child view that has all bits in `aState` set
and all bits in `aOptions` set. Iterates the list from
[last](#orgviktor44jtvisionviewsjtvview-last) backwards. Returns `null` if none matches.

**Parameters**

- `state` - the state bits to require
- `aOptions` - the option bits to require

**Returns**

the first matching view, or `null`



### `freeBuffer()`

Releases the off-screen draw buffer if `ofBuffered` is set.
Called when the group is hidden so that memory is returned while the
group is not visible.



### `initBuffer()`

Allocates the off-screen draw buffer if the group is exposed and
`ofBuffered` is set. The buffer is sized to hold the entire
group (`width × height` cells) and pre-filled with the
group's default background colour (palette entry 1) so that any
cells not painted by child views render with the background
instead of uninitialised (black) cells.



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Routes `event` through the three-phase delivery mechanism:

1. [JtvView.phPreProcess](JtvView.md#int-phpreprocess) — views with `ofPreProcess`.
2. [JtvView.phFocused](JtvView.md#int-phfocused) — the current focused view for keyboard/
      command events, or the topmost view under the mouse for
      positional events.
3. [JtvView.phPostProcess](JtvView.md#int-phpostprocess) — views with `ofPostProcess`.

The base [JtvView.handleEvent](JtvView.md#handleeventjtvevent-event) is called first to handle
mouse-down focus transfers.

**Parameters**

- `event` - the event to route



### `doHandleEvent(JtvView p, JtvEvent event)`

Delivers `event` to a single child view, respecting phase and disable state.

**Parameters**

- `p`
- `event`


### `forEachHandleEvent(JtvEvent event)`

Iterates all children from back to front, calling `doHandleEvent` on each.

**Parameters**

- `event`


### `insert(JtvView p)`

Inserts `p` into this group as the front-most child view.
If `p` has the `ofCenterX` or `ofCenterY` option
set, its origin is adjusted to centre it within the group.

**Parameters**

- `p` - the view to insert; must not already have an owner



### `insertBefore(JtvView p, JtvView target)`

Inserts `p` immediately before `target` in the z-order.

**Parameters**

- `p`
- `target`


### `insertView(JtvView p, JtvView target)`

Low-level insertion: links `p` into the circular list
immediately before `target` (or at the end if `target`
is `null`), and sets [JtvView.owner](JtvView.md#orgviktor44jtvisionviewsjtvgroup-owner) to this group.

**Parameters**

- `p` - the view to link in
- `target` - the view to insert before, or `null` to append



### `removeView(JtvView p)`

Low-level removal: unlinks `p` from the circular list without
touching its owner or next pointers (those are cleared by
[remove(JtvView)](#removejtvview-p)).

**Parameters**

- `p` - the view to unlink



### `lock()`

Increments the draw lock counter, deferring any [drawView()](JtvView.md#drawview)
calls until [unlock()](#unlock) brings the counter back to zero.
Has no effect if neither a buffer nor an existing lock is present.



### `unlock()`

Decrements the draw lock counter. When it reaches zero, triggers a
deferred [drawView()](JtvView.md#drawview) to flush all buffered changes.



### `redraw()`

Redraws all child views by calling [drawSubViews](#drawsubviewsjtvview-p-jtvview-bottom) from
[first()](#first) to the end of the list.



### `resetCurrent()`

Selects the first visible, selectable child as the current focused
view by calling [setCurrent](#setcurrentjtvview-p-int-mode) with [JtvView.normalSelect](JtvView.md#int-normalselect).



### `resetCursor()`

**Overrides:**

`resetCursor()` in `org.viktor44.jtvision.views.JtvView`

Delegates the cursor-reset to the currently focused child.
If no child is focused the hardware cursor is not updated.



### `selectNext(boolean forwards)`

Moves focus to the next selectable sibling in the given direction.
Has no effect if no current view is focused.

**Parameters**

- `forwards` - `true` to move to the next sibling;
                  `false` to move to the previous sibling



### `selectView(JtvView p, boolean enable)`

Sets or clears the `sfSelected` state on view `p`.

**Parameters**

- `p` - the view to change, or `null` (ignored)
- `enable` - `true` to select; `false` to deselect



### `focusView(JtvView p, boolean enable)`

Sets or clears the `sfFocused` state on view `p`,
but only if this group itself is focused.

**Parameters**

- `p` - the view to change, or `null` (ignored)
- `enable` - `true` to focus; `false` to unfocus



### `setCurrent(JtvView p, int mode)`

Changes the currently focused child to `p` using the selection
mode `mode`. Handles transferring `sfSelected` and
`sfFocused` state bits between the old and new current views.

**Parameters**

- `p` - the new current view, or `null` to clear selection
- `mode` - `JtvView#normalSelect`, `JtvView#enterSelect`,
              or `JtvView#leaveSelect`



### `setDataFrom(Object rec)`

**Overrides:**

`setDataFrom(Object rec)` in `org.viktor44.jtvision.views.JtvView`

No-op data-import override. Subclasses may override to distribute
data records to child views.

**Parameters**

- `rec` - the source data record



### `setState(int aState, boolean enable)`

**Overrides:**

`setState(int aState, boolean enable)` in `org.viktor44.jtvision.views.JtvView`

Propagates state changes to all children and handles special cases:

- `sfActive` / `sfDragging` — forwarded to all children.
- `sfFocused` — forwarded to the current child.
- `sfExposed` — forwarded to all visible children; clearing
      it also frees the draw buffer.



**Parameters**

- `aState` - the state bits to modify
- `enable` - `true` to set; `false` to clear



### `valid(int command)`

**Overrides:**

`valid(int command)` in `org.viktor44.jtvision.views.JtvView`

Validates all child views for the given command. For
`cmReleasedFocus` only the current child (if it has
`ofValidate`) is checked; for all other commands every child
is validated.

**Parameters**

- `command` - the command requesting validation

**Returns**

`true` if all applicable children return `true`



### `getHelpCtx()`

**Overrides:**

`getHelpCtx()` in `org.viktor44.jtvision.views.JtvView`

Returns the effective help-context ID. Asks the current child first;
if it returns `hcNoContext`, falls back to the group's own
help context via the parent [JtvView.getHelpCtx()](JtvView.md#gethelpctx).

**Returns**

the current help-context ID



### `forEach(function.Consumer<org.viktor44.jtvision.views.JtvView> action)`

Applies `action` to every child view exactly once,
iterating the circular list safely even if `action` modifies
the list.

**Parameters**

- `action` - the consumer to invoke for each child view



### `firstThat(function.Predicate<org.viktor44.jtvision.views.JtvView> test)`

Returns the first child view for which `test` returns
`true`, or `null` if none matches.

**Parameters**

- `test` - the predicate to apply

**Returns**

the first matching child, or `null`



### `indexOf(JtvView p)`

Returns the 1-based index of view `p` within the circular
list (starting at 1 for [first()](#first)), or `0` if not found.

**Parameters**

- `p` - the view whose index to find

**Returns**

the 1-based position, or `0` if not a child



### `at(int index)`

Returns the child view at 1-based `index` in the circular list,
or `null` if the group is empty or the index is invalid.

**Parameters**

- `index` - 1-based position (1 = first/front-most)

**Returns**

the view at that position, or `null`



### `writeClipped(JtvView target, int x1, int x2, int y, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset, boolean inShadow)`

Writes cells into the group's buffer or screen, skipping over any
sibling views that occlude the target area and applying shadow colour
transformations where needed.

**Parameters**

- `target` - the child view that is writing (occlusion is checked
                   only for children in front of it)
- `x1` - start column (group-local, inclusive)
- `x2` - end column (group-local, exclusive)
- `y` - row (group-local)
- `buf` - source cell array
- `bufOffset` - starting index into `buf`
- `inShadow` - `true` if this segment should be rendered as shadow



### `writeClippedAfter(JtvView after, JtvView target, int x1, int x2, int y, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset, boolean inShadow)`

Continuation of [writeClipped](#writeclippedjtvview-target-int-x1-int-x2-int-y-orgviktor44jtvisioncorejtvscreencell-buf-int-bufoffset-boolean-inshadow) starting after view `after`.

**Parameters**

- `after`
- `target`
- `x1`
- `x2`
- `y`
- `buf`
- `bufOffset`
- `inShadow`


### `writeFinal(int x1, int x2, int y, org.viktor44.jtvision.core.JtvScreenCell[] buf, int bufOffset, boolean inShadow)`

Writes cells to the off-screen buffer (if any) and/or the screen,
applying shadow colour transformation when `inShadow` is true.
When the draw lock is held, only the buffer is written (no screen
output). When the group has no owner, writes directly to the screen.

**Parameters**

- `x1` - start column (inclusive)
- `x2` - end column (exclusive)
- `y` - row
- `buf` - source cell array
- `bufOffset` - starting index into `buf`
- `inShadow` - `true` to apply shadow colour transformation



### `getTheTopView()`

The current top-level modal view, or `null` when no modal
view is active. Set and restored by [JtvGroup.execView](#execviewjtvview-p).



### `getCurrent()`

The currently focused (selected) child view, or `null` if no
child is selected. Set by [setCurrent(JtvView, int)](#setcurrentjtvview-p-int-mode).



