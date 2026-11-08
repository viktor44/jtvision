---
title: "JtvFileDialog"
sidebar_label: "JtvFileDialog"
---

Class JtvFileDialog
===================

```java
public class JtvFileDialog
extends JtvDialog
```


A standard file-open / file-save dialog box.



TFileDialog presents a resizable dialog that lets the user browse and select files.
It contains:

- A filename input line ([JtvFileInputLine](JtvFileInputLine.md)) with a history list.
- A file list box ([JtvFileList](JtvFileList.md)) showing the current directory.
- A file information pane ([JtvFileInfoPane](JtvFileInfoPane.md)) at the bottom.
- Configurable action buttons controlled by the `aOptions` flag.

The set of buttons is controlled by `aOptions` (see `fdXXXX` constants):
`fdOpenButton`, `fdOKButton`, `fdReplaceButton`, `fdClearButton`,
`fdHelpButton`. A Cancel button is always present.



When an action button is pressed, the dialog ends its modal state with the
corresponding command (`cmFileOpen`, `cmFileReplace`, `cmFileClear`).
A double-click in the file list is treated as `cmOK`.



[valid(int)](#validint-command) checks the filename input line before closing:

- A wildcard updates the directory/filter and reloads the list without closing.
- A directory path navigates into that directory without closing.
- A plain filename closes the dialog.

After the dialog closes, the fully-qualified selected file path can be retrieved
via [getFileName(StringBuilder)](#getfilenamestringbuilder-out).

org.viktor44.jtvision.dialogs.JtvFileDialog

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                           |
| ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvFileDialog(String aWildCard, String aTitle, String inputName, int aOptions, int histId)](#jtvfiledialogstring-awildcard-string-atitle-string-inputname-int-aoptions-int-histid) |

### Fields

| Modifiers                | Field name                                                       | Type                                           |
| ------------------------ | ---------------------------------------------------------------- | ---------------------------------------------- |
| **private static final** | [filesText](#string-filestext)                                   | String                                         |
| **private static final** | [openText](#string-opentext)                                     | String                                         |
| **private static final** | [okText](#string-oktext)                                         | String                                         |
| **private static final** | [replaceText](#string-replacetext)                               | String                                         |
| **private static final** | [clearText](#string-cleartext)                                   | String                                         |
| **private static final** | [cancelText](#string-canceltext)                                 | String                                         |
| **private static final** | [helpText](#string-helptext)                                     | String                                         |
| **private static final** | [invalidDriveText](#string-invaliddrivetext)                     | String                                         |
| **private static final** | [invalidFileText](#string-invalidfiletext)                       | String                                         |
| **private**              | [fileName](#orgviktor44jtvisiondialogsjtvfileinputline-filename) | org.viktor44.jtvision.dialogs.JtvFileInputLine |
| **private**              | [fileList](#orgviktor44jtvisiondialogsjtvfilelist-filelist)      | org.viktor44.jtvision.dialogs.JtvFileList      |
| **private**              | [wildCard](#string-wildcard)                                     | String                                         |
| **private**              | [directory](#string-directory)                                   | String                                         |

### Methods

| Modifiers   | Method signature                                                                                             | Return type                                    |
| ----------- | ------------------------------------------------------------------------------------------------------------ | ---------------------------------------------- |
| **private** | [addButton(JtvRect r, String title, int cmd, int flags)](#addbuttonjtvrect-r-string-title-int-cmd-int-flags) | void                                           |
| **public**  | [getFileName(StringBuilder out)](#getfilenamestringbuilder-out)                                              | void                                           |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                                    | void                                           |
| **public**  | [readDirectory()](#readdirectory)                                                                            | void                                           |
| **public**  | [setDataFrom(Object rec)](#setdatafromobject-rec)                                                            | void                                           |
| **public**  | [getDataTo(Object rec)](#getdatatoobject-rec)                                                                | void                                           |
| **private** | [checkDirectory(String str)](#checkdirectorystring-str)                                                      | boolean                                        |
| **public**  | [valid(int command)](#validint-command)                                                                      | boolean                                        |
| **public**  | [getFileName()](#getfilename)                                                                                | org.viktor44.jtvision.dialogs.JtvFileInputLine |
| **public**  | [getWildCard()](#getwildcard)                                                                                | String                                         |
| **public**  | [getDirectory()](#getdirectory)                                                                              | String                                         |



## Constructors

### `JtvFileDialog(String aWildCard, String aTitle, String inputName, int aOptions, int histId)`

Creates the file dialog.



The dialog is initially centred and sized for the current screen, expanding
further if the screen is large enough. Unless `fdNoLoadDir` is set in
`aOptions`, the file list is loaded immediately with the current directory.

**Parameters**

- `aWildCard` - the initial wildcard filter (e.g., `"*.java"`); defaults
                    to `"*"` if `null`
- `aTitle` - the dialog title bar text
- `inputName` - the label string for the file-name input line
- `aOptions` - combination of `fdXXXX` flag constants controlling
                    which buttons appear
- `histId` - the history list ID for the filename input line



## Fields

### `String filesText`

*No description provided*


### `String openText`

*No description provided*


### `String okText`

*No description provided*


### `String replaceText`

*No description provided*


### `String clearText`

*No description provided*


### `String cancelText`

*No description provided*


### `String helpText`

*No description provided*


### `String invalidDriveText`

*No description provided*


### `String invalidFileText`

*No description provided*


### `org.viktor44.jtvision.dialogs.JtvFileInputLine fileName`

The filename input line.
Exposed so callers can read or set the filename programmatically.


### `org.viktor44.jtvision.dialogs.JtvFileList fileList`

The file list box that displays directory contents.


### `String wildCard`

The current wildcard filter (e.g., `"*.java"`).
Updated when the user types a wildcard pattern in the input line.


### `String directory`

The absolute path of the directory currently displayed, always ending with a file separator character.


## Methods

### `addButton(JtvRect r, String title, int cmd, int flags)`

Creates and inserts a growing button with the given parameters.

**Parameters**

- `r`
- `title`
- `cmd`
- `flags`


### `getFileName(StringBuilder out)`

Builds the fully-qualified path of the selected file and appends it to
`out`. If the filename in the input line is relative, it is resolved
against the current `directory`.

**Parameters**

- `out` - the `StringBuilder` to receive the absolute file path



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.dialogs.JtvDialog`

Handles `cmFileOpen`, `cmFileReplace`, and `cmFileClear`
commands by ending the modal state. Handles `cmFileDoubleClicked`
broadcasts from the file list by converting them to a `cmOK` command.

**Parameters**

- `event` - the incoming event



### `readDirectory()`

Reloads the file list from the current JVM working directory using the
active `wildCard` filter.



### `setDataFrom(Object rec)`

Handles data transfer from the dialog. If the record is a string that
contains a wildcard, re-initialises the file list filter.

**Parameters**

- `rec` - the source data record



### `getDataTo(Object rec)`

Writes the selected file path into `rec` if it is a `StringBuilder`.

**Parameters**

- `rec` - a `StringBuilder` to receive the selected file path



### `checkDirectory(String str)`

Shows an "invalid directory" message box and refocuses the filename input line.

**Parameters**

- `str` - the directory path that failed validation

**Returns**

`true` if the directory exists and is valid



### `valid(int command)`

**Overrides:**

`valid(int command)` in `org.viktor44.jtvision.dialogs.JtvDialog`

Validates the dialog before closing.

- Zero or `cmCancel` / `cmFileClear` — always valid.
- Wildcard in the input line — updates the filter and reloads the list,
      returns `false` to keep the dialog open.
- Directory path in the input line — navigates to that directory,
      returns `false` to keep the dialog open.
- Plain filename — returns `true` allowing the dialog to close.



**Parameters**

- `command` - the command that is attempting to close the dialog

**Returns**

`true` if the dialog may close; `false` otherwise



### `getFileName()`

The filename input line.
Exposed so callers can read or set the filename programmatically.



### `getWildCard()`

The current wildcard filter (e.g., `"*.java"`).
Updated when the user types a wildcard pattern in the input line.



### `getDirectory()`

The absolute path of the directory currently displayed, always ending with a file separator character.



