---
title: "CommandCodes"
sidebar_label: "CommandCodes"
---

Class CommandCodes
==================

```java
public final class CommandCodes
```


Standard command codes, matching JT Vision conventions.

org.viktor44.jtvision.core.CommandCodes

## Summary

### Constructors

| Visibility | Signature                       |
| ---------- | ------------------------------- |
| private    | [CommandCodes()](#commandcodes) |

### Fields

| Modifiers               | Field name                                                | Type |
| ----------------------- | --------------------------------------------------------- | ---- |
| **public static final** | [cmValid](#int-cmvalid)                                   | int  |
| **public static final** | [cmQuit](#int-cmquit)                                     | int  |
| **public static final** | [cmError](#int-cmerror)                                   | int  |
| **public static final** | [cmMenu](#int-cmmenu)                                     | int  |
| **public static final** | [cmClose](#int-cmclose)                                   | int  |
| **public static final** | [cmZoom](#int-cmzoom)                                     | int  |
| **public static final** | [cmResize](#int-cmresize)                                 | int  |
| **public static final** | [cmNext](#int-cmnext)                                     | int  |
| **public static final** | [cmPrev](#int-cmprev)                                     | int  |
| **public static final** | [cmHelp](#int-cmhelp)                                     | int  |
| **public static final** | [cmOK](#int-cmok)                                         | int  |
| **public static final** | [cmCancel](#int-cmcancel)                                 | int  |
| **public static final** | [cmYes](#int-cmyes)                                       | int  |
| **public static final** | [cmNo](#int-cmno)                                         | int  |
| **public static final** | [cmDefault](#int-cmdefault)                               | int  |
| **public static final** | [cmCut](#int-cmcut)                                       | int  |
| **public static final** | [cmCopy](#int-cmcopy)                                     | int  |
| **public static final** | [cmPaste](#int-cmpaste)                                   | int  |
| **public static final** | [cmUndo](#int-cmundo)                                     | int  |
| **public static final** | [cmClear](#int-cmclear)                                   | int  |
| **public static final** | [cmTile](#int-cmtile)                                     | int  |
| **public static final** | [cmCascade](#int-cmcascade)                               | int  |
| **public static final** | [cmRedo](#int-cmredo)                                     | int  |
| **public static final** | [cmNew](#int-cmnew)                                       | int  |
| **public static final** | [cmOpen](#int-cmopen)                                     | int  |
| **public static final** | [cmSave](#int-cmsave)                                     | int  |
| **public static final** | [cmSaveAs](#int-cmsaveas)                                 | int  |
| **public static final** | [cmSaveAll](#int-cmsaveall)                               | int  |
| **public static final** | [cmChDir](#int-cmchdir)                                   | int  |
| **public static final** | [cmDosShell](#int-cmdosshell)                             | int  |
| **public static final** | [cmCloseAll](#int-cmcloseall)                             | int  |
| **public static final** | [cmReceivedFocus](#int-cmreceivedfocus)                   | int  |
| **public static final** | [cmReleasedFocus](#int-cmreleasedfocus)                   | int  |
| **public static final** | [cmCommandSetChanged](#int-cmcommandsetchanged)           | int  |
| **public static final** | [cmScrollBarChanged](#int-cmscrollbarchanged)             | int  |
| **public static final** | [cmScrollBarClicked](#int-cmscrollbarclicked)             | int  |
| **public static final** | [cmSelectWindowNum](#int-cmselectwindownum)               | int  |
| **public static final** | [cmListItemSelected](#int-cmlistitemselected)             | int  |
| **public static final** | [cmScreenChanged](#int-cmscreenchanged)                   | int  |
| **public static final** | [cmTimerExpired](#int-cmtimerexpired)                     | int  |
| **public static final** | [cmRecordHistory](#int-cmrecordhistory)                   | int  |
| **public static final** | [cmGrabDefault](#int-cmgrabdefault)                       | int  |
| **public static final** | [cmReleaseDefault](#int-cmreleasedefault)                 | int  |
| **public static final** | [cmColorForegroundChanged](#int-cmcolorforegroundchanged) | int  |
| **public static final** | [cmColorBackgroundChanged](#int-cmcolorbackgroundchanged) | int  |
| **public static final** | [cmColorSet](#int-cmcolorset)                             | int  |
| **public static final** | [cmNewColorItem](#int-cmnewcoloritem)                     | int  |
| **public static final** | [cmNewColorIndex](#int-cmnewcolorindex)                   | int  |
| **public static final** | [cmSaveColorIndex](#int-cmsavecolorindex)                 | int  |
| **public static final** | [cmFind](#int-cmfind)                                     | int  |
| **public static final** | [cmReplace](#int-cmreplace)                               | int  |
| **public static final** | [cmSearchAgain](#int-cmsearchagain)                       | int  |
| **public static final** | [cmOutlineItemSelected](#int-cmoutlineitemselected)       | int  |
| **public static final** | [cmCharLeft](#int-cmcharleft)                             | int  |
| **public static final** | [cmCharRight](#int-cmcharright)                           | int  |
| **public static final** | [cmWordLeft](#int-cmwordleft)                             | int  |
| **public static final** | [cmWordRight](#int-cmwordright)                           | int  |
| **public static final** | [cmLineStart](#int-cmlinestart)                           | int  |
| **public static final** | [cmLineEnd](#int-cmlineend)                               | int  |
| **public static final** | [cmLineUp](#int-cmlineup)                                 | int  |
| **public static final** | [cmLineDown](#int-cmlinedown)                             | int  |
| **public static final** | [cmPageUp](#int-cmpageup)                                 | int  |
| **public static final** | [cmPageDown](#int-cmpagedown)                             | int  |
| **public static final** | [cmTextStart](#int-cmtextstart)                           | int  |
| **public static final** | [cmTextEnd](#int-cmtextend)                               | int  |
| **public static final** | [cmNewLine](#int-cmnewline)                               | int  |
| **public static final** | [cmBackSpace](#int-cmbackspace)                           | int  |
| **public static final** | [cmDelChar](#int-cmdelchar)                               | int  |
| **public static final** | [cmDelWord](#int-cmdelword)                               | int  |
| **public static final** | [cmDelStart](#int-cmdelstart)                             | int  |
| **public static final** | [cmDelEnd](#int-cmdelend)                                 | int  |
| **public static final** | [cmDelLine](#int-cmdelline)                               | int  |
| **public static final** | [cmInsMode](#int-cminsmode)                               | int  |
| **public static final** | [cmStartSelect](#int-cmstartselect)                       | int  |
| **public static final** | [cmHideSelect](#int-cmhideselect)                         | int  |
| **public static final** | [cmIndentMode](#int-cmindentmode)                         | int  |
| **public static final** | [cmUpdateTitle](#int-cmupdatetitle)                       | int  |
| **public static final** | [cmSelectAll](#int-cmselectall)                           | int  |
| **public static final** | [cmDelWordLeft](#int-cmdelwordleft)                       | int  |
| **public static final** | [cmEncoding](#int-cmencoding)                             | int  |
| **public static final** | [cmFileFocused](#int-cmfilefocused)                       | int  |
| **public static final** | [cmFileDoubleClicked](#int-cmfiledoubleclicked)           | int  |
| **public static final** | [cmFileOpen](#int-cmfileopen)                             | int  |
| **public static final** | [cmFileReplace](#int-cmfilereplace)                       | int  |
| **public static final** | [cmFileClear](#int-cmfileclear)                           | int  |
| **public static final** | [cmFileInit](#int-cmfileinit)                             | int  |
| **public static final** | [cmChangeDir](#int-cmchangedir)                           | int  |
| **public static final** | [cmRevert](#int-cmrevert)                                 | int  |



## Constructors

### `CommandCodes()`

*No method description provided*



## Fields

### `int cmValid`

Passed to `valid()` to check whether a newly instantiated view is valid.


### `int cmQuit`

Terminates the application by calling `endModal(cmQuit)` on the application object.


### `int cmError`

Never handled by any object; can represent an unimplemented or unsupported command.


### `int cmMenu`

Causes a menu view to call `execView()` on itself to begin a menu-selection process.


### `int cmClose`

Closes a window. If the window is modal a `cmCancel` command is generated via
`putEvent()`; otherwise the window's `close()` method is called directly.


### `int cmZoom`

Causes a zoomable window to call its `zoom()` method.


### `int cmResize`

Causes a resizable window to call `dragView()` on itself so the user can resize it.


### `int cmNext`

Selects the next window on the desktop.


### `int cmPrev`

Selects the previous window on the desktop.


### `int cmHelp`

Opens the context-sensitive help system for the currently focused view.


### `int cmOK`

The OK button was pressed; the dialog should accept its data and close.


### `int cmCancel`

The Cancel button, close icon, or Escape key was used; the dialog should discard its data and close.


### `int cmYes`

The Yes button was pressed in a message box.


### `int cmNo`

The No button was pressed in a message box.


### `int cmDefault`

The default button in a dialog was activated.


### `int cmCut`

Cut the selected text and place it on the clipboard.


### `int cmCopy`

Copy the selected text to the clipboard without removing it.


### `int cmPaste`

Paste the clipboard contents at the current insertion point.


### `int cmUndo`

Undo the last editing operation.


### `int cmClear`

Delete the selected text without copying it to the clipboard.


### `int cmTile`

Tile all tileable windows on the desktop.


### `int cmCascade`

Cascade all tileable windows on the desktop.


### `int cmRedo`

Redo the last undone editing operation.


### `int cmNew`

Open a new, empty document (File | New).


### `int cmOpen`

Open an existing file from disk (File | Open).


### `int cmSave`

Save the current document to its existing file (File | Save).


### `int cmSaveAs`

Save the current document under a new name (File | Save As).


### `int cmSaveAll`

Save all open documents (File | Save All).


### `int cmChDir`

Change the current working directory (File | Change Dir).


### `int cmDosShell`

Suspend the application and shell to the operating system (File | DOS Shell).


### `int cmCloseAll`

Close all open documents (File | Close All).


### `int cmReceivedFocus`

Broadcast sent by `setState()` to the view's owner whenever `sfFocused` is set,
so that peer views (e.g. labels) can update their highlighted state.


### `int cmReleasedFocus`

Broadcast sent by `setState()` to the view's owner whenever `sfFocused` is cleared,
so that peer views can remove their highlighted state.


### `int cmCommandSetChanged`

Broadcast sent by the application's `idle()` method whenever it detects a change in
the set of currently enabled commands, allowing views to enable or disable themselves.


### `int cmScrollBarChanged`

Broadcast sent by a scroll bar to its owner whenever the scroll bar's value changes,
so the linked view can scroll its content accordingly.


### `int cmScrollBarClicked`

Broadcast sent by a scroll bar to its owner when the user clicks any part of the scroll bar,
allowing the owner to update its display interactively.


### `int cmSelectWindowNum`

Causes a window to select (bring to front) itself if the event's `infoInt` field
matches the window's `number` field; used to implement numbered window selection.


### `int cmListItemSelected`

Broadcast sent by a list viewer to its owner whenever the user selects an item in the list,
allowing the owner to react to the new selection.


### `int cmScreenChanged`

Broadcast sent when the screen dimensions or mode have changed and views should redraw themselves.


### `int cmTimerExpired`

Broadcast sent when a timer expires, allowing views to perform periodic updates.


### `int cmRecordHistory`

Broadcast sent by a button to its owner when the button is pressed, causing any linked
history object to record the current contents of the associated input line.


### `int cmGrabDefault`

Broadcast sent by a default-capable button when it gains focus, asking its owner to
designate it as the current default button.


### `int cmReleaseDefault`

Broadcast sent by a default-capable button when it loses focus, asking its owner to
remove its default-button designation.


### `int cmColorForegroundChanged`

Broadcast sent when the user selects a new foreground color in the color editor.


### `int cmColorBackgroundChanged`

Broadcast sent when the user selects a new background color in the color editor.


### `int cmColorSet`

Broadcast sent to apply the current color selection to the target palette entry.


### `int cmNewColorItem`

Broadcast sent when the user navigates to a new color item in the color group list.


### `int cmNewColorIndex`

Broadcast sent when the selected palette index changes in the color item list.


### `int cmSaveColorIndex`

Broadcast sent to persist the current palette index back to the color item list.


### `int cmFind`

Open the Find (text search) dialog.


### `int cmReplace`

Open the Replace (search and replace) dialog.


### `int cmSearchAgain`

Repeat the most recent search operation.


### `int cmOutlineItemSelected`

Broadcast sent by an outline viewer when the user selects an item in the outline tree.


### `int cmCharLeft`

Move the cursor one character to the left.


### `int cmCharRight`

Move the cursor one character to the right.


### `int cmWordLeft`

Move the cursor one word to the left.


### `int cmWordRight`

Move the cursor one word to the right.


### `int cmLineStart`

Move the cursor to the start of the current line.


### `int cmLineEnd`

Move the cursor to the end of the current line.


### `int cmLineUp`

Move the cursor up one line.


### `int cmLineDown`

Move the cursor down one line.


### `int cmPageUp`

Scroll the view up one page and move the cursor accordingly.


### `int cmPageDown`

Scroll the view down one page and move the cursor accordingly.


### `int cmTextStart`

Move the cursor to the very beginning of the text.


### `int cmTextEnd`

Move the cursor to the very end of the text.


### `int cmNewLine`

Insert a new line at the current cursor position.


### `int cmBackSpace`

Delete the character immediately before the cursor (Backspace).


### `int cmDelChar`

Delete the character at the cursor position (Delete).


### `int cmDelWord`

Delete from the cursor to the end of the current word.


### `int cmDelStart`

Delete from the cursor back to the start of the current line.


### `int cmDelEnd`

Delete from the cursor to the end of the current line.


### `int cmDelLine`

Delete the entire current line.


### `int cmInsMode`

Toggle between insert and overwrite mode.


### `int cmStartSelect`

Begin (or anchor) a keyboard selection at the current cursor position.


### `int cmHideSelect`

Collapse/hide the current selection without deleting it.


### `int cmIndentMode`

Toggle automatic indentation mode.


### `int cmUpdateTitle`

Request that the editor window update its title bar (e.g. to reflect modified state).


### `int cmSelectAll`

Select all text in the editor.


### `int cmDelWordLeft`

Delete from the cursor back to the start of the previous word.


### `int cmEncoding`

Change the character encoding of the document.


### `int cmFileFocused`

Broadcast sent by a file list box to its owner (`TFileDialog`) when the focused
file entry changes, so that `TFileInputLine` and `TFileInfoPane` can refresh.


### `int cmFileDoubleClicked`

Broadcast sent by a file list box when the user double-clicks a file entry.


### `int cmFileOpen`

Returned by `TFileDialog` when the user presses the Open button.


### `int cmFileReplace`

Returned by `TFileDialog` when the user presses the Replace button.


### `int cmFileClear`

Returned by `TFileDialog` when the user presses the Clear button.


### `int cmFileInit`

Sent internally to initialise a file dialog after it has been constructed or restored from a stream.


### `int cmChangeDir`

Sent internally when the user confirms a directory change in the change-directory dialog.


### `int cmRevert`

Revert the current document to the last saved version on disk.


