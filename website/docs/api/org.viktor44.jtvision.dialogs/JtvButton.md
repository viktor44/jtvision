---
title: "JtvButton"
sidebar_label: "JtvButton"
---

Class JtvButton
===============

```java
public class JtvButton
extends JtvView
```


A titled push-button view that generates a command event when pressed.



TButton is a terminal view most often found inside dialog boxes.
Users can activate a button by clicking it with the mouse, pressing its
highlighted shortcut letter (Alt+letter), pressing Space when the button has
focus, or pressing Enter when the button is the default button.



On color and black-and-white displays the button renders with a 3-D shadow;
on monochrome displays bracket markers are used instead.



There can be only one default button in a window at a time. Buttons negotiate
the default state by broadcasting `cmGrabDefault` and
`cmReleaseDefault`. Enabling or disabling the button's bound command
also enables or disables the button itself via `cmCommandSetChanged`.

**Colour palette**

The eight-entry palette `cpButton` maps to:

1. Normal button text.
2. Default button text.
3. Selected button text.
4. Disabled button text.
5. Shortcut key (normal state).
6. Shortcut key (default state).
7. Shortcut key (selected state).
8. Button shadow.



org.viktor44.jtvision.dialogs.JtvButton

## Summary

### Constructors

| Visibility | Signature                                                                                                                            |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| public     | [JtvButton(JtvRect bounds, String aTitle, int aCommand, int aFlags)](#jtvbuttonjtvrect-bounds-string-atitle-int-acommand-int-aflags) |

### Fields

| Modifiers                | Field name                                              | Type                                  |
| ------------------------ | ------------------------------------------------------- | ------------------------------------- |
| **private static final** | [cpButton](#orgviktor44jtvisioncorejtvpalette-cpbutton) | org.viktor44.jtvision.core.JtvPalette |
| **private static final** | [shadows](#char-shadows)                                | char[]                                |
| **private static final** | [markers](#char-markers)                                | char[]                                |
| **private static final** | [specialChars](#char-specialchars)                      | char[]                                |
| **private static final** | [animationDurationMs](#int-animationdurationms)         | int                                   |
| **private**              | [title](#string-title)                                  | String                                |
| **private**              | [command](#int-command)                                 | int                                   |
| **private**              | [flags](#int-flags)                                     | int                                   |
| **private**              | [defaultButton](#boolean-defaultbutton)                 | boolean                               |

### Methods

| Modifiers   | Method signature                                                                                                                                                                                         | Return type                           |
| ----------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------- |
| **public**  | [draw()](#draw)                                                                                                                                                                                          | void                                  |
| **public**  | [drawState(boolean down)](#drawstateboolean-down)                                                                                                                                                        | void                                  |
| **private** | [drawTitle(JtvDrawBuffer b, int s, int i, JtvColorAttr cButton, JtvColorAttr cButtonHot, boolean down)](#drawtitlejtvdrawbuffer-b-int-s-int-i-jtvcolorattr-cbutton-jtvcolorattr-cbuttonhot-boolean-down) | void                                  |
| **public**  | [getPalette()](#getpalette)                                                                                                                                                                              | org.viktor44.jtvision.core.JtvPalette |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                                                                                                                                | void                                  |
| **public**  | [makeDefault(boolean enable)](#makedefaultboolean-enable)                                                                                                                                                | void                                  |
| **public**  | [setState(int aState, boolean enable)](#setstateint-astate-boolean-enable)                                                                                                                               | void                                  |
| **private** | [animatePress()](#animatepress)                                                                                                                                                                          | void                                  |
| **public**  | [press()](#press)                                                                                                                                                                                        | void                                  |
| **public**  | [getTitle()](#gettitle)                                                                                                                                                                                  | String                                |
| **public**  | [getCommand()](#getcommand)                                                                                                                                                                              | int                                   |
| **public**  | [getFlags()](#getflags)                                                                                                                                                                                  | int                                   |
| **public**  | [isDefaultButton()](#isdefaultbutton)                                                                                                                                                                    | boolean                               |



## Constructors

### `JtvButton(JtvRect bounds, String aTitle, int aCommand, int aFlags)`

Creates a button with the given bounds, label, command, and flags.



If `aFlags` contains `bfDefault`, the button starts as the default.
If the bound command is currently disabled, the button is initialised in a disabled state.

**Parameters**

- `bounds` - the bounding rectangle of the button
- `aTitle` - the button label; enclose a character in tildes to make it a hot-key
- `aCommand` - the command generated when the button is pressed
- `aFlags` - combination of `bfXXXX` flag constants



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpButton`

Eight-entry colour palette:

1. `0x0A` — normal button text.
2. `0x0B` — default button text.
3. `0x0C` — selected button text.
4. `0x0D` — disabled button text.
5. `0x0E` — shortcut key (normal state).
6. `0x0E` — shortcut key (default state).
7. `0x0E` — shortcut key (selected state).
8. `0x0F` — button shadow.




### `char[] shadows`

Unicode block characters used to render the 3-D button shadow (lower-half, full, upper-half block).


### `char[] markers`

Bracket characters used in monochrome (`showMarkers`) mode to frame the button.


### `char[] specialChars`

Special edge characters used in monochrome mode to indicate selected, default, or normal state.


### `int animationDurationMs`

Duration of the press animation in milliseconds.


### `String title`

The text label of the button. Enclose a character in tildes to define a hot-key (e.g., `"~O~K"`).


### `int command`

The command code posted when the button is pressed.


### `int flags`

Bitmapped field controlling button behaviour and label alignment.
See `bfXXXX` flag constants (`bfDefault`, `bfLeftJust`,
`bfBroadcast`, `bfGrabFocus`).


### `boolean defaultButton`

`true` when this button is currently acting as the default button (pressed on Enter).


## Methods

### `draw()`

**Overrides:**

`draw()` in `org.viktor44.jtvision.views.JtvView`

Draws the button in its current (unpressed) state by delegating to [drawState(boolean)](#drawstateboolean-down).



### `drawState(boolean down)`

Draws the button in the pressed or unpressed visual state.



Selects color attributes based on enabled/disabled, active, selected, and default states.
When `down` is `true` the button renders as pressed (shifted shadow).

**Parameters**

- `down` - `true` to draw the button as pressed, `false` for normal



### `drawTitle(JtvDrawBuffer b, int s, int i, JtvColorAttr cButton, JtvColorAttr cButtonHot, boolean down)`

Renders the button's title string into the draw buffer, centred or left-justified
according to the `bfLeftJust` flag. In monochrome mode, adds state indicator
characters at the button edges.

**Parameters**

- `b`
- `s`
- `i`
- `cButton`
- `cButtonHot`
- `down`


### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvView`

Returns the default color palette `cpButton`.

**Returns**

the button's color palette



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvView`

Handles mouse, keyboard, and broadcast events.



Responds to:

- Mouse clicks — tracks drag-over-button state, calls [press()](#press) on release.
- Alt+hotkey or Space (when focused) — calls `animatePress()`.
- `cmDefault` broadcast — presses the button if it is the default.
- `cmGrabDefault`/`cmReleaseDefault` — updates default status.
- `cmCommandSetChanged` — enables/disables the button.



**Parameters**

- `event` - the incoming event



### `makeDefault(boolean enable)`

Makes this button the default (or releases it), unless the button already
owns the `bfDefault` flag (in which case it is permanently default).
Broadcasts `cmGrabDefault` or `cmReleaseDefault` to notify peers.

**Parameters**

- `enable` - `true` to grab the default, `false` to release it



### `setState(int aState, boolean enable)`

**Overrides:**

`setState(int aState, boolean enable)` in `org.viktor44.jtvision.views.JtvView`

Extends the inherited `setState` to redraw when selected or activated,
and to call [makeDefault(boolean)](#makedefaultboolean-enable) when the button gains or loses focus.

**Parameters**

- `aState` - the state flag(s) being changed
- `enable` - `true` to set the flag, `false` to clear it



### `animatePress()`

Briefly renders the button as pressed, flushes the screen, waits
\{@value #animationDurationMs\} ms, then restores the normal state and calls
[press()](#press).



### `press()`

Generates the event associated with pressing this button.



First broadcasts `cmRecordHistory` so that linked [JtvHistory](JtvHistory.md) views
record their input line contents. Then either broadcasts the button's command
(if `bfBroadcast` is set) or posts a `evCommand` event.



### `getTitle()`

The text label of the button. Enclose a character in tildes to define a hot-key (e.g., `"~O~K"`).



### `getCommand()`

The command code posted when the button is pressed.



### `getFlags()`

Bitmapped field controlling button behaviour and label alignment.
See `bfXXXX` flag constants (`bfDefault`, `bfLeftJust`,
`bfBroadcast`, `bfGrabFocus`).



### `isDefaultButton()`

`true` when this button is currently acting as the default button (pressed on Enter).



