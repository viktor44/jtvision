---
title: "ViewFlags"
sidebar_label: "ViewFlags"
---

Class ViewFlags
===============

```java
public final class ViewFlags
```


View state, option, grow mode, drag mode, and other flag constants.

org.viktor44.jtvision.core.ViewFlags

## Summary

### Constructors

| Visibility | Signature                 |
| ---------- | ------------------------- |
| private    | [ViewFlags()](#viewflags) |

### Fields

| Modifiers               | Field name                                | Type |
| ----------------------- | ----------------------------------------- | ---- |
| **public static final** | [sfVisible](#int-sfvisible)               | int  |
| **public static final** | [sfCursorVis](#int-sfcursorvis)           | int  |
| **public static final** | [sfCursorIns](#int-sfcursorins)           | int  |
| **public static final** | [sfShadow](#int-sfshadow)                 | int  |
| **public static final** | [sfActive](#int-sfactive)                 | int  |
| **public static final** | [sfSelected](#int-sfselected)             | int  |
| **public static final** | [sfFocused](#int-sffocused)               | int  |
| **public static final** | [sfDragging](#int-sfdragging)             | int  |
| **public static final** | [sfDisabled](#int-sfdisabled)             | int  |
| **public static final** | [sfModal](#int-sfmodal)                   | int  |
| **public static final** | [sfDefault](#int-sfdefault)               | int  |
| **public static final** | [sfExposed](#int-sfexposed)               | int  |
| **public static final** | [ofSelectable](#int-ofselectable)         | int  |
| **public static final** | [ofTopSelect](#int-oftopselect)           | int  |
| **public static final** | [ofFirstClick](#int-offirstclick)         | int  |
| **public static final** | [ofFramed](#int-offramed)                 | int  |
| **public static final** | [ofPreProcess](#int-ofpreprocess)         | int  |
| **public static final** | [ofPostProcess](#int-ofpostprocess)       | int  |
| **public static final** | [ofBuffered](#int-ofbuffered)             | int  |
| **public static final** | [ofTileable](#int-oftileable)             | int  |
| **public static final** | [ofCenterX](#int-ofcenterx)               | int  |
| **public static final** | [ofCenterY](#int-ofcentery)               | int  |
| **public static final** | [ofCentered](#int-ofcentered)             | int  |
| **public static final** | [ofValidate](#int-ofvalidate)             | int  |
| **public static final** | [gfGrowLoX](#int-gfgrowlox)               | int  |
| **public static final** | [gfGrowLoY](#int-gfgrowloy)               | int  |
| **public static final** | [gfGrowHiX](#int-gfgrowhix)               | int  |
| **public static final** | [gfGrowHiY](#int-gfgrowhiy)               | int  |
| **public static final** | [gfGrowAll](#int-gfgrowall)               | int  |
| **public static final** | [gfGrowRel](#int-gfgrowrel)               | int  |
| **public static final** | [gfFixed](#int-gffixed)                   | int  |
| **public static final** | [dmDragMove](#int-dmdragmove)             | int  |
| **public static final** | [dmDragGrow](#int-dmdraggrow)             | int  |
| **public static final** | [dmDragGrowLeft](#int-dmdraggrowleft)     | int  |
| **public static final** | [dmLimitLoX](#int-dmlimitlox)             | int  |
| **public static final** | [dmLimitLoY](#int-dmlimitloy)             | int  |
| **public static final** | [dmLimitHiX](#int-dmlimithix)             | int  |
| **public static final** | [dmLimitHiY](#int-dmlimithiy)             | int  |
| **public static final** | [dmLimitAll](#int-dmlimitall)             | int  |
| **public static final** | [hcNoContext](#int-hcnocontext)           | int  |
| **public static final** | [hcDragging](#int-hcdragging)             | int  |
| **public static final** | [sbLeftArrow](#int-sbleftarrow)           | int  |
| **public static final** | [sbRightArrow](#int-sbrightarrow)         | int  |
| **public static final** | [sbPageLeft](#int-sbpageleft)             | int  |
| **public static final** | [sbPageRight](#int-sbpageright)           | int  |
| **public static final** | [sbUpArrow](#int-sbuparrow)               | int  |
| **public static final** | [sbDownArrow](#int-sbdownarrow)           | int  |
| **public static final** | [sbPageUp](#int-sbpageup)                 | int  |
| **public static final** | [sbPageDown](#int-sbpagedown)             | int  |
| **public static final** | [sbIndicator](#int-sbindicator)           | int  |
| **public static final** | [sbHorizontal](#int-sbhorizontal)         | int  |
| **public static final** | [sbVertical](#int-sbvertical)             | int  |
| **public static final** | [sbHandleKeyboard](#int-sbhandlekeyboard) | int  |
| **public static final** | [wfMove](#int-wfmove)                     | int  |
| **public static final** | [wfGrow](#int-wfgrow)                     | int  |
| **public static final** | [wfClose](#int-wfclose)                   | int  |
| **public static final** | [wfZoom](#int-wfzoom)                     | int  |
| **public static final** | [noMenuBar](#int-nomenubar)               | int  |
| **public static final** | [noDeskTop](#int-nodesktop)               | int  |
| **public static final** | [noStatusLine](#int-nostatusline)         | int  |
| **public static final** | [noBackground](#int-nobackground)         | int  |
| **public static final** | [noFrame](#int-noframe)                   | int  |
| **public static final** | [noViewer](#int-noviewer)                 | int  |
| **public static final** | [noHistory](#int-nohistory)               | int  |
| **public static final** | [wnNoNumber](#int-wnnonumber)             | int  |
| **public static final** | [wpBlueWindow](#int-wpbluewindow)         | int  |
| **public static final** | [wpCyanWindow](#int-wpcyanwindow)         | int  |
| **public static final** | [wpGrayWindow](#int-wpgraywindow)         | int  |
| **public static final** | [bfNormal](#int-bfnormal)                 | int  |
| **public static final** | [bfDefault](#int-bfdefault)               | int  |
| **public static final** | [bfLeftJust](#int-bfleftjust)             | int  |
| **public static final** | [bfBroadcast](#int-bfbroadcast)           | int  |
| **public static final** | [bfGrabFocus](#int-bfgrabfocus)           | int  |
| **public static final** | [apColor](#int-apcolor)                   | int  |
| **public static final** | [apBlackWhite](#int-apblackwhite)         | int  |
| **public static final** | [apMonochrome](#int-apmonochrome)         | int  |
| **public static final** | [dpBlueDialog](#int-dpbluedialog)         | int  |
| **public static final** | [dpCyanDialog](#int-dpcyandialog)         | int  |
| **public static final** | [dpGrayDialog](#int-dpgraydialog)         | int  |
| **public static final** | [mfWarning](#int-mfwarning)               | int  |
| **public static final** | [mfError](#int-mferror)                   | int  |
| **public static final** | [mfInformation](#int-mfinformation)       | int  |
| **public static final** | [mfConfirmation](#int-mfconfirmation)     | int  |
| **public static final** | [mfYesButton](#int-mfyesbutton)           | int  |
| **public static final** | [mfNoButton](#int-mfnobutton)             | int  |
| **public static final** | [mfOKButton](#int-mfokbutton)             | int  |
| **public static final** | [mfCancelButton](#int-mfcancelbutton)     | int  |
| **public static final** | [mfYesNoCancel](#int-mfyesnocancel)       | int  |
| **public static final** | [mfOKCancel](#int-mfokcancel)             | int  |
| **public static final** | [fdOKButton](#int-fdokbutton)             | int  |
| **public static final** | [fdOpenButton](#int-fdopenbutton)         | int  |
| **public static final** | [fdReplaceButton](#int-fdreplacebutton)   | int  |
| **public static final** | [fdClearButton](#int-fdclearbutton)       | int  |
| **public static final** | [fdHelpButton](#int-fdhelpbutton)         | int  |
| **public static final** | [fdNoLoadDir](#int-fdnoloaddir)           | int  |
| **public static final** | [cdNormal](#int-cdnormal)                 | int  |
| **public static final** | [cdNoLoadDir](#int-cdnoloaddir)           | int  |
| **public static final** | [cdHelpButton](#int-cdhelpbutton)         | int  |



## Constructors

### `ViewFlags()`

*No method description provided*



## Fields

### `int sfVisible`

The view is visible on its owner. Set by default; `show()` and `hide()` modify it.


### `int sfCursorVis`

The view's cursor is visible. `showCursor()` and `hideCursor()` modify it.


### `int sfCursorIns`

The cursor is a solid block insert cursor; when clear the cursor is the default underline.


### `int sfShadow`

The view has a shadow displayed beneath it.


### `int sfActive`

The view is the active window, or a subview within that active window.


### `int sfSelected`

The view is the currently selected subview within its owner group.


### `int sfFocused`

The view has the input focus — it is selected and on the focused chain from the Application object.


### `int sfDragging`

The view is currently being dragged by the user.


### `int sfDisabled`

The view is disabled and ignores all events sent to it.


### `int sfModal`

The view is modal — it is the root of the active event tree until `endModal()` is called.


### `int sfDefault`

The view is the default button in its group.


### `int sfExposed`

The view is owned (directly or indirectly) by the Application object and therefore possibly visible on screen.


### `int ofSelectable`

The view can be selected, for example by a mouse click.


### `int ofTopSelect`

When selected, the view moves in front of all peer views (top-select behaviour).


### `int ofFirstClick`

A mouse click that selects the view is also delivered to the view as a normal click event.


### `int ofFramed`

A frame is drawn around the view.


### `int ofPreProcess`

The view receives focused keyboard events before they are sent to the focused subview (pre-process phase).


### `int ofPostProcess`

The view receives focused keyboard events after the focused subview if that subview did not consume them (post-process phase).


### `int ofBuffered`

For group objects: allocate a cache buffer so the group can be redrawn without re-drawing every child.


### `int ofTileable`

The desktop may tile or cascade this view; used only with window objects.


### `int ofCenterX`

The view is centered on the X-axis of its owner when drawn.


### `int ofCenterY`

The view is centered on the Y-axis of its owner when drawn.


### `int ofCentered`

The view is centered both horizontally and vertically within its owner (`ofCenterX | ofCenterY`).


### `int ofValidate`

`valid()` is called on the view before it loses focus, allowing it to reject the focus change.


### `int gfGrowLoX`

The left edge of the view maintains a constant distance from the left edge of its owner as the owner resizes.


### `int gfGrowLoY`

The top edge of the view maintains a constant distance from the top edge of its owner as the owner resizes.


### `int gfGrowHiX`

The right edge of the view maintains a constant distance from the right edge of its owner as the owner resizes.


### `int gfGrowHiY`

The bottom edge of the view maintains a constant distance from the bottom edge of its owner as the owner resizes.


### `int gfGrowAll`

All four edges track their respective owner edges — the view moves with the lower-right corner of its owner (`gfGrowLoX | gfGrowLoY | gfGrowHiX | gfGrowHiY`).


### `int gfGrowRel`

When used with window objects, the view changes size proportionally relative to the owner's size change.


### `int gfFixed`

The view has a fixed size and does not resize when its owner resizes.


### `int dmDragMove`

Dragging moves the view to a new position.


### `int dmDragGrow`

Dragging resizes the view by moving its lower-right corner.


### `int dmDragGrowLeft`

Dragging resizes the view by moving its left edge.


### `int dmLimitLoX`

The view's left edge cannot be dragged outside the limits rectangle.


### `int dmLimitLoY`

The view's top edge cannot be dragged outside the limits rectangle.


### `int dmLimitHiX`

The view's right edge cannot be dragged outside the limits rectangle.


### `int dmLimitHiY`

The view's bottom edge cannot be dragged outside the limits rectangle.


### `int dmLimitAll`

No part of the view can be dragged outside the limits rectangle (`dmLimitLoX | dmLimitLoY | dmLimitHiX | dmLimitHiY`).


### `int hcNoContext`

No help context is associated with the view.


### `int hcDragging`

Help context returned by `getHelpCtx()` while the view is being dragged.


### `int sbLeftArrow`

The left arrow of a horizontal scroll bar.


### `int sbRightArrow`

The right arrow of a horizontal scroll bar.


### `int sbPageLeft`

The left paging area (between the left arrow and the indicator) of a horizontal scroll bar.


### `int sbPageRight`

The right paging area (between the indicator and the right arrow) of a horizontal scroll bar.


### `int sbUpArrow`

The up arrow of a vertical scroll bar.


### `int sbDownArrow`

The down arrow of a vertical scroll bar.


### `int sbPageUp`

The upper paging area (between the up arrow and the indicator) of a vertical scroll bar.


### `int sbPageDown`

The lower paging area (between the indicator and the down arrow) of a vertical scroll bar.


### `int sbIndicator`

The position indicator (thumb) of a scroll bar.


### `int sbHorizontal`

Create a horizontal scroll bar.


### `int sbVertical`

Create a vertical scroll bar.


### `int sbHandleKeyboard`

The scroll bar responds to keyboard arrow and page keys in addition to mouse input.


### `int wfMove`

The user can move the window by dragging its title bar.


### `int wfGrow`

The user can resize the window by dragging its lower-right corner.


### `int wfClose`

The window frame contains a close icon that dismisses the window on click.


### `int wfZoom`

The window frame contains a zoom icon that toggles the window between normal and maximised size.


### `int noMenuBar`

Suppress the creation of the default menu bar.


### `int noDeskTop`

Suppress the creation of the default desktop.


### `int noStatusLine`

Suppress the creation of the default status line.


### `int noBackground`

Suppress the creation of the default background view.


### `int noFrame`

Suppress the creation of the default window frame.


### `int noViewer`

Suppress the creation of the default viewer.


### `int noHistory`

Suppress the creation of the default history list.


### `int wnNoNumber`

Passed to the TWindow constructor to indicate that no window number should be shown in the title bar.


### `int wpBlueWindow`

Window drawn with white text on a blue background.


### `int wpCyanWindow`

Window drawn with blue text on a cyan background.


### `int wpGrayWindow`

Window drawn with black text on a gray background.


### `int bfNormal`

Normal non-default button; no special behaviour.


### `int bfDefault`

The button is the default button in its group and is activated by the Enter key.


### `int bfLeftJust`

The button label is left-aligned; when clear the label is centred.


### `int bfBroadcast`

When pressed, the button sends a broadcast message to its owner rather than a command event.


### `int bfGrabFocus`

The button takes input focus when clicked with the mouse.


### `int apColor`

Use the full-colour palette for colour screen display.


### `int apBlackWhite`

Use the black-and-white palette for LCD or monochrome-capable colour screens.


### `int apMonochrome`

Use the monochrome palette for true monochrome screens.


### `int dpBlueDialog`

Dialog box drawn with a blue background.


### `int dpCyanDialog`

Dialog box drawn with a cyan background.


### `int dpGrayDialog`

Dialog box drawn with a gray background.


### `int mfWarning`

Display a Warning message box (default type).


### `int mfError`

Display an Error message box.


### `int mfInformation`

Display an Information message box.


### `int mfConfirmation`

Display a Confirmation message box.


### `int mfYesButton`

Include a Yes button in the message box.


### `int mfNoButton`

Include a No button in the message box.


### `int mfOKButton`

Include an OK button in the message box.


### `int mfCancelButton`

Include a Cancel button in the message box.


### `int mfYesNoCancel`

Convenience combination: Yes, No, and Cancel buttons (`mfYesButton | mfNoButton | mfCancelButton`).


### `int mfOKCancel`

Convenience combination: OK and Cancel buttons (`mfOKButton | mfCancelButton`).


### `int fdOKButton`

Include an OK button in the file dialog.


### `int fdOpenButton`

Include an Open button in the file dialog.


### `int fdReplaceButton`

Include a Replace button in the file dialog.


### `int fdClearButton`

Include a Clear button in the file dialog.


### `int fdHelpButton`

Include a Help button in the file dialog.


### `int fdNoLoadDir`

Do not load the current directory listing when the dialog is initialised (e.g. when restoring from a stream).


### `int cdNormal`

Create the change-directory dialog normally, including loading the current directory listing.


### `int cdNoLoadDir`

Initialise the dialog without loading the directory listing; used when restoring the dialog from a stream.


### `int cdHelpButton`

Include a Help button in the change-directory dialog.


