---
title: "MessageBox"
sidebar_label: "MessageBox"
---

Class MessageBox
================

```java
public final class MessageBox
```


Static factory for JT Vision–style message boxes and input boxes.



This class corresponds to the JT Vision `MsgBox` unit and provides
four public factory methods:

- [messageBox(String, int)](#messageboxstring-msg-int-options) — a centred 40×9 notification dialog.
- [messageBox(JtvRect, String, int)](#messageboxjtvrect-r-string-msg-int-aoptions) — the same dialog at an
      explicit bounding rectangle.
- [inputBox(String, String, int)](#inputboxstring-title-string-label-int-maxlen) — a centred 40×8 single-line
      input dialog.
- [inputBox(JtvRect, String, String, int)](#inputboxjtvrect-r-string-title-string-label-int-maxlen) — the same input
      dialog at an explicit bounding rectangle.

**Options parameter encoding**
The `aOptions` parameter of [messageBox](#messageboxstring-msg-int-options) and
`messageBoxRect` is a bitmapped word whose low byte selects the
dialog icon/title and whose high byte selects the buttons:


```
  msb ─────────────────────────── lsb
  [ mfCancelButton | mfOKButton | mfNoButton | mfYesButton ][ mfConfirmation / mfInformation / mfError / mfWarning ]
```


**Icon/title constants (bits 0–1):**

- `mfWarning      = 0x0000` — "Warning" title.
- `mfError        = 0x0001` — "Error" title.
- `mfInformation  = 0x0002` — "Information" title.
- `mfConfirmation = 0x0003` — "Confirm" title.

**Button constants (bits 8–11):**

- `mfYesButton    = 0x0100` — add a Yes button.
- `mfNoButton     = 0x0200` — add a No button.
- `mfOKButton     = 0x0400` — add an OK button.
- `mfCancelButton = 0x0800` — add a Cancel button.
- `mfYesNoCancel  = 0x0B00` — Yes + No + Cancel.
- `mfOKCancel     = 0x0C00` — OK + Cancel.

**Return values**
[messageBox](#messageboxstring-msg-int-options) and `messageBoxRect` return the command code of
the button the user pressed: `cmYes`, `cmNo`, `cmOK`, or
`cmCancel`. If no application is running, `cmCancel` is returned.



[inputBox](#inputboxstring-title-string-label-int-maxlen) and `inputBoxRect` return the string entered by the
user, or `null` if the dialog was cancelled.

org.viktor44.jtvision.util.MessageBox

## Summary

### Constructors

| Visibility | Signature                   |
| ---------- | --------------------------- |
| private    | [MessageBox()](#messagebox) |

### Fields

| Modifiers                | Field name                            | Type     |
| ------------------------ | ------------------------------------- | -------- |
| **private static final** | [titles](#string-titles)              | String[] |
| **private static final** | [buttonText](#string-buttontext)      | String[] |
| **private static final** | [buttonCommands](#int-buttoncommands) | int[]    |
| **private static final** | [buttonFlags](#int-buttonflags)       | int[]    |

### Methods

| Modifiers         | Method signature                                                                                                       | Return type |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------- | ----------- |
| **public static** | [messageBox(String msg, int options)](#messageboxstring-msg-int-options)                                               | int         |
| **public static** | [messageBox(JtvRect r, String msg, int aOptions)](#messageboxjtvrect-r-string-msg-int-aoptions)                        | int         |
| **public static** | [inputBox(String title, String label, int maxLen)](#inputboxstring-title-string-label-int-maxlen)                      | String      |
| **public static** | [inputBox(JtvRect r, String title, String label, int maxLen)](#inputboxjtvrect-r-string-title-string-label-int-maxlen) | String      |



## Constructors

### `MessageBox()`

Prevents instantiation of this static utility class.



## Fields

### `String[] titles`

Window titles indexed by the icon-type nibble (`aOptions & 0x03`).



The four entries correspond to:
&lt;ol start="0">
- `mfWarning = 0x0000` — `"Warning"`
- `mfError = 0x0001` — `"Error"`
- `mfInformation = 0x0002` — `"Information"`
- `mfConfirmation = 0x0003` — `"Confirm"`




### `String[] buttonText`

Tilde-encoded button label strings, in button-index order.



Indices are fixed constants used by the button-building loop:

- 0 — `"~Y~es"`   (Yes button, hot key `Y`)
- 1 — `"~N~o"`    (No button, hot key `N`)
- 2 — `"~O~K"`    (OK button, hot key `O`)
- 3 — `"~C~ancel"`(Cancel button, hot key `C`)




### `int[] buttonCommands`

Command codes emitted when each button is activated, parallel to
`buttonText`.

- 0 — `cmYes`
- 1 — `cmNo`
- 2 — `cmOK`
- 3 — `cmCancel`




### `int[] buttonFlags`

Button-flag defaults parallel to `buttonText`.



The first button inserted into the dialog is always overridden to
`bfDefault` by the loop in `messageBoxRect`, regardless of
these values. The remaining entries use `bfNormal`.

- 0 (Yes)    — `bfNormal`
- 1 (No)     — `bfNormal`
- 2 (OK)     — `bfDefault` (pre-selected default)
- 3 (Cancel) — `bfNormal`




## Methods

### `messageBox(String msg, int options)`

Displays a 40-column, 9-line message dialog centred on the desktop.



Computes a centred [JtvRect](../org.viktor44.jtvision.core/JtvRect.md) of size 40×9 relative to
`JtvProgram.desktop` and delegates to
[messageBox(JtvRect, String, int)](#messageboxjtvrect-r-string-msg-int-aoptions).



Example usage:


```
int result = MessageBox.messageBox(
    "Shall I reformat your hard drive now?",
    mfConfirmation | mfYesButton | mfNoButton);
if (result == cmYes) { ... }
```



**Parameters**

- `msg` - the message string to display inside the dialog
- `options` - a bitmapped combination of one icon-type constant
                  (`mfWarning`, `mfError`, `mfInformation`,
                  or `mfConfirmation`) OR-ed with one or more button
                  constants (`mfYesButton`, `mfNoButton`,
                  `mfOKButton`, `mfCancelButton`)

**Returns**

the command code of the button that closed the dialog
        (`cmYes`, `cmNo`, `cmOK`, or `cmCancel`),
        or `cmCancel` if no application is available



### `messageBox(JtvRect r, String msg, int aOptions)`

Displays a message dialog at an explicit bounding rectangle.



Works exactly like [messageBox(String, int)](#messageboxstring-msg-int-options) but uses the caller-
supplied `r` instead of a fixed 40×9 centred box. Use this variant
when the message is too long for the default size.



The dialog title is selected from `titles` using bits 0–1 of
`aOptions`. Buttons are added in the order Yes → No → OK → Cancel
according to the button-mask in bits 8–11 of `aOptions`; if no
button flag is set, an OK button is added by default. The first button
inserted is always marked `bfDefault` (activated by Enter).

**Parameters**

- `r` - the bounding rectangle for the dialog (in desktop
                  coordinates)
- `msg` - the message string to display
- `aOptions` - bitmapped options \u2014 see `int)`
                  for the encoding

**Returns**

the command code of the activating button, or `cmCancel`
        if no application is available



### `inputBox(String title, String label, int maxLen)`

Displays a 40-column, 8-line single-line input dialog centred on the
desktop.



The dialog contains a static text label (`label`), a
[JtvInputLine](../org.viktor44.jtvision.dialogs/JtvInputLine.md) limited to `maxLen` characters, an OK button,
and a Cancel button.



Computes a centred 40×8 bounding rectangle relative to
`JtvProgram.desktop` and delegates to
[inputBox(JtvRect, String, String, int)](#inputboxjtvrect-r-string-title-string-label-int-maxlen).



Example usage:


```
String name = MessageBox.inputBox("Rename", "New file name:", 64);
if (name != null) { ... }
```



**Parameters**

- `title` - the dialog window title
- `label` - the descriptive text shown above the input field
- `maxLen` - the maximum number of characters the user may type

**Returns**

the string entered by the user, or `null` if the user
        pressed Cancel or no application is available



### `inputBox(JtvRect r, String title, String label, int maxLen)`

Displays a single-line input dialog at an explicit bounding rectangle.



Works exactly like [inputBox(String, String, int)](#inputboxstring-title-string-label-int-maxlen) but uses the
caller-supplied `r` instead of a fixed 40×8 centred box. Use this
variant when the label or expected input is too wide for the default size.



The layout within `r` is:

- Row 2 — static label text.
- Row 3 — the [JtvInputLine](../org.viktor44.jtvision.dialogs/JtvInputLine.md) (width = dialog width − 4 columns).
- Row 5–6 — centred OK (`bfDefault`) and Cancel buttons.

If the user closes the dialog with OK (or any non-Cancel command), the
text currently in the input line is returned. Otherwise `null` is
returned.

**Parameters**

- `r` - the bounding rectangle for the dialog (in desktop
                coordinates)
- `title` - the dialog window title
- `label` - the descriptive text shown above the input field
- `maxLen` - the maximum number of characters the user may type

**Returns**

the string entered by the user, or `null` if the dialog was
        cancelled or no application is available



