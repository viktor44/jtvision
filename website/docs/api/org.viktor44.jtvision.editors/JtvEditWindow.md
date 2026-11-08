---
title: "JtvEditWindow"
sidebar_label: "JtvEditWindow"
---

Class JtvEditWindow
===================

```java
public class JtvEditWindow
extends JtvWindow
```


A window containing a [JtvFileEditor](JtvFileEditor.md) together with horizontal and vertical scroll bars
and a [JtvIndicator](JtvIndicator.md).



`JtvEditWindow` constructs all required subviews in its constructor and associates
them with the editor.  The window title is derived from the editor's file name, or
`"Untitled"` when no file name has been set.



When the file name changes (because of a `cmSaveAs` operation) the editor
broadcasts `cmUpdateTitle`; this window's [handleEvent](#handleeventjtvevent-event) refreshes the
window frame title in response.

org.viktor44.jtvision.editors.JtvEditWindow

## Summary

### Constructors

| Visibility | Signature                                                                                                             |
| ---------- | --------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvEditWindow(JtvRect bounds, String fileName, int number)](#jtveditwindowjtvrect-bounds-string-filename-int-number) |

### Fields

| Modifiers                | Field name                                                | Type                                        |
| ------------------------ | --------------------------------------------------------- | ------------------------------------------- |
| **private static final** | [clipboardTitle](#string-clipboardtitle)                  | String                                      |
| **private static final** | [untitled](#string-untitled)                              | String                                      |
| **private**              | [editor](#orgviktor44jtvisioneditorsjtvfileeditor-editor) | org.viktor44.jtvision.editors.JtvFileEditor |

### Methods

| Modifiers  | Method signature                                          | Return type                                 |
| ---------- | --------------------------------------------------------- | ------------------------------------------- |
| **public** | [close()](#close)                                         | void                                        |
| **public** | [getTitle(int maxSize)](#gettitleint-maxsize)             | String                                      |
| **public** | [handleEvent(JtvEvent event)](#handleeventjtvevent-event) | void                                        |
| **public** | [getMinimumSize()](#getminimumsize)                       | org.viktor44.jtvision.core.JtvPoint         |
| **public** | [getEditor()](#geteditor)                                 | org.viktor44.jtvision.editors.JtvFileEditor |



## Constructors

### `JtvEditWindow(JtvRect bounds, String fileName, int number)`

Constructs an editor window, creating and inserting horizontal and vertical scroll bars,
a position indicator, and a [JtvFileEditor](JtvFileEditor.md) that covers the interior of the window
frame.  If `fileName` is non-empty the editor immediately loads that file.

**Parameters**

- `bounds` - the bounding rectangle for the window
- `fileName` - path of the file to edit, or empty/null for a new untitled file
- `number` - window number (displayed in the title bar and used by Alt+`n` switching)



## Fields

### `String clipboardTitle`

Window title used when the editor is acting as the clipboard (not used by default).


### `String untitled`

Window title shown when no file name has been assigned.


### `org.viktor44.jtvision.editors.JtvFileEditor editor`

The embedded file editor view.  All text editing is performed by this object.


## Methods

### `close()`

**Overrides:**

`close()` in `org.viktor44.jtvision.views.JtvWindow`

Closes the window only if the editor allows it (i.e. [JtvFileEditor.valid(int)](JtvFileEditor.md#validint-command)
returns `true`, which may prompt the user to save unsaved changes).



### `getTitle(int maxSize)`

**Overrides:**

`getTitle(int maxSize)` in `org.viktor44.jtvision.views.JtvWindow`

Returns the window title derived from the editor's file name, or `"Untitled"`
if no file name has been set.  The returned string is truncated to `maxSize`
characters if necessary.

**Parameters**

- `maxSize` - maximum length of the returned title string

**Returns**

the window title



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvWindow`

Handles events for the editor window.  Delegates to the inherited window handler,
then responds to the `cmUpdateTitle` broadcast by refreshing the window title
(emitted by the editor when the file name changes after a Save As operation).

**Parameters**

- `event` - the event to handle



### `getMinimumSize()`

Returns the minimum and maximum size constraints for this editor window.
The minimum is 30 columns × 10 rows to ensure the editor and scroll bars remain usable.

**Parameters**

- `min` - receives the minimum allowable size
- `max` - receives the maximum allowable size



### `getEditor()`

The embedded file editor view.  All text editing is performed by this object.



