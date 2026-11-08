---
title: "JtvHelpWindow"
sidebar_label: "JtvHelpWindow"
---

Class JtvHelpWindow
===================

```java
public class JtvHelpWindow
extends JtvWindow
```


A centred, non-numbered help window that hosts a [JtvHelpViewer](JtvHelpViewer.md).



JtvHelpWindow is a standard [JtvWindow](../org.viktor44.jtvision.views/JtvWindow.md) descendant that wraps a
[JtvHelpViewer](JtvHelpViewer.md) together with keyboard-driven horizontal and vertical
scroll bars. It is the top-level container shown when context-sensitive
help is requested by the application.



The window is:

- Titled *"Help"*.
- Initially sized 50×18 characters and centred on screen
      (`ofCentered`).
- Numbered with `wnNoNumber` so no window number appears in
      the title bar.

**Colour palette**

The seven-entry palette `cpHelpWindow` overrides the default window
palette to use help-specific colours for the frame, title, drag bar,
close button, resize corner, scroll bars, and scroll arrows.

org.viktor44.jtvision.help.JtvHelpWindow

## Summary

### Constructors

| Visibility | Signature                                                                                    |
| ---------- | -------------------------------------------------------------------------------------------- |
| public     | [JtvHelpWindow(JtvHelpFile hFile, int context)](#jtvhelpwindowjtvhelpfile-hfile-int-context) |

### Fields

| Modifiers                | Field name                                                      | Type                                  |
| ------------------------ | --------------------------------------------------------------- | ------------------------------------- |
| **private static final** | [helpWinTitle](#string-helpwintitle)                            | String                                |
| **private static final** | [cpHelpWindow](#orgviktor44jtvisioncorejtvpalette-cphelpwindow) | org.viktor44.jtvision.core.JtvPalette |

### Methods

| Modifiers  | Method signature            | Return type                           |
| ---------- | --------------------------- | ------------------------------------- |
| **public** | [getPalette()](#getpalette) | org.viktor44.jtvision.core.JtvPalette |



## Constructors

### `JtvHelpWindow(JtvHelpFile hFile, int context)`

Constructs the help window, creates keyboard-controlled scroll bars,
and inserts a [JtvHelpViewer](JtvHelpViewer.md) that immediately displays the topic
for `context`.



The viewer bounds are inset two columns and one row from the window
interior to leave room for the frame.

**Parameters**

- `hFile` - the help file to load topics from; may be `null`,
                 in which case an empty topic is shown
- `context` - the initial help-context ID to display in the viewer



## Fields

### `String helpWinTitle`

The title bar text of the help window.


### `org.viktor44.jtvision.core.JtvPalette cpHelpWindow`

Default colour palette for the help window frame and controls:

1. `0x13` — frame (passive).
2. `0x13` — title.
3. `0x15` — frame (active).
4. `0x18` — drag bar.
5. `0x17` — close button.
6. `0x13` — scroll bar page area.
7. `0x14` — scroll bar arrows.




## Methods

### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvWindow`

Returns the help window's colour palette `cpHelpWindow`,
overriding the default window palette with help-specific colours.

**Returns**

the help window's colour palette



