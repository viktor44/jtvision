---
title: "JtvHelpViewer"
sidebar_label: "JtvHelpViewer"
---

Class JtvHelpViewer
===================

```java
public class JtvHelpViewer
extends JtvScroller
```


A scrollable viewer that renders a single [JtvHelpTopic](JtvHelpTopic.md).



JtvHelpViewer extends [JtvScroller](../org.viktor44.jtvision.views/JtvScroller.md) to display the word-wrapped text of a
help topic and to highlight any embedded cross-references (hyperlinks).
It is the primary content view inside a [JtvHelpWindow](JtvHelpWindow.md).

**Cross-reference navigation**

Cross-references are rendered with a distinct colour. The user can cycle
through them with Tab (forward) and Shift+Tab (backward). Pressing Enter
on the selected cross-reference, or clicking one with the mouse, calls
[switchToTopic(int)](#switchtotopicint-keyref) with the linked context ID, replacing the
displayed topic in place.

**Keyboard shortcuts**

- **Tab** — select next cross-reference.
- **Shift+Tab** — select previous cross-reference.
- **Enter** — follow the currently selected cross-reference.
- **Escape** — synthesises `cmClose` to close the help window.

**Colour palette**

The three-entry palette `cpHelpViewer` maps to:

1. Normal text colour.
2. Cross-reference (keyword) colour.
3. Selected cross-reference colour.



org.viktor44.jtvision.help.JtvHelpViewer

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                                                                 |
| ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvHelpViewer(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, JtvHelpFile aHelpFile, int context)](#jtvhelpviewerjtvrect-bounds-jtvscrollbar-ahscrollbar-jtvscrollbar-avscrollbar-jtvhelpfile-ahelpfile-int-context) |

### Fields

| Modifiers                | Field name                                                      | Type                                    |
| ------------------------ | --------------------------------------------------------------- | --------------------------------------- |
| **private static final** | [cpHelpViewer](#orgviktor44jtvisioncorejtvpalette-cphelpviewer) | org.viktor44.jtvision.core.JtvPalette   |
| **private**              | [hFile](#orgviktor44jtvisionhelpjtvhelpfile-hfile)              | org.viktor44.jtvision.help.JtvHelpFile  |
| **private**              | [topic](#orgviktor44jtvisionhelpjtvhelptopic-topic)             | org.viktor44.jtvision.help.JtvHelpTopic |
| **private**              | [selected](#int-selected)                                       | int                                     |

### Methods

| Modifiers   | Method signature                                            | Return type                           |
| ----------- | ----------------------------------------------------------- | ------------------------------------- |
| **public**  | [changeBounds(JtvRect bounds)](#changeboundsjtvrect-bounds) | void                                  |
| **public**  | [draw()](#draw)                                             | void                                  |
| **public**  | [getPalette()](#getpalette)                                 | org.viktor44.jtvision.core.JtvPalette |
| **private** | [makeSelectVisible(int idx)](#makeselectvisibleint-idx)     | void                                  |
| **public**  | [switchToTopic(int keyRef)](#switchtotopicint-keyref)       | void                                  |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)   | void                                  |



## Constructors

### `JtvHelpViewer(JtvRect bounds, JtvScrollBar aHScrollBar, JtvScrollBar aVScrollBar, JtvHelpFile aHelpFile, int context)`

Constructs a help viewer, loads the initial topic for `context`,
and configures the scroll limits.



Sets `ofSelectable` so the viewer can receive focus and keyboard
input. If `aHelpFile` is `null`, an empty topic is shown.

**Parameters**

- `bounds` - the bounding rectangle of the viewer
- `aHScrollBar` - the horizontal scroll bar, or `null`
- `aVScrollBar` - the vertical scroll bar, or `null`
- `aHelpFile` - the help file to load topics from, or `null`
- `context` - the initial help-context ID to display



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpHelpViewer`

Default colour palette for the help viewer:

1. `0x13` — normal text.
2. `0x14` — cross-reference (keyword) text.
3. `0x15` — selected cross-reference text.




### `org.viktor44.jtvision.help.JtvHelpFile hFile`

The help file used to look up topics when following cross-references.


### `org.viktor44.jtvision.help.JtvHelpTopic topic`

The topic currently being displayed.


### `int selected`

1-based index of the currently highlighted cross-reference.
A value of `1` selects the first cross-reference.


## Methods

### `changeBounds(JtvRect bounds)`

**Overrides:**

`changeBounds(JtvRect bounds)` in `org.viktor44.jtvision.views.JtvScroller`

Adjusts the topic word-wrap width to the new view width and resets the
scroll limits whenever the viewer's bounds change.

**Parameters**

- `bounds` - the new bounding rectangle



### `draw()`

Renders the visible portion of the current topic.



Each row displays the corresponding word-wrapped line of the topic,
scrolled by the current `delta` offset. After drawing normal text,
the method overlays the cross-reference colour attributes: unselected
cross-references use the keyword colour; the selected one uses the
selected-keyword colour.



### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvScroller`

Returns the colour palette `cpHelpViewer`.

**Returns**

the help viewer's colour palette



### `makeSelectVisible(int idx)`

Scrolls the view so that the cross-reference at zero-based index
`idx` is fully visible. If the reference is already in view,
no scrolling occurs.

**Parameters**

- `idx` - zero-based index of the cross-reference to reveal



### `switchToTopic(int keyRef)`

Navigates to the topic identified by `keyRef`, replacing the
currently displayed topic.



The scroll position is reset to the top-left, the display width is
applied to the new topic, the scroll limits are updated, and the view
is redrawn.

**Parameters**

- `keyRef` - the help-context ID of the topic to display



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvScroller`

Handles keyboard, mouse, and command events for the help viewer.

- **Tab** — advance `selected` to the next cross-reference
      (wrapping around), scroll it into view, and redraw.
- **Shift+Tab** — move `selected` to the previous
      cross-reference (wrapping around), scroll it into view, and redraw.
- **Enter** — follow the currently selected cross-reference by
      calling [switchToTopic(int)](#switchtotopicint-keyref).
- **Escape** — synthesises a `cmClose` command event.
- **Mouse click** — if the click lands within a cross-reference
      span, selects and follows that cross-reference.
- **`cmClose` / `cmCancel`** — ends the modal state
      when the owning group is modal.



**Parameters**

- `event` - the incoming event



