---
title: "JtvChangeDirDialog"
sidebar_label: "JtvChangeDirDialog"
---

Class JtvChangeDirDialog
========================

```java
public class JtvChangeDirDialog
extends JtvDialog
```


A dialog box for changing the current working directory.



TChDirDialog presents a resizable "Change directory" dialog that contains:

- A directory name input line with a history list.
- A directory tree list box showing the current disk hierarchy.
- OK, Chdir, and Revert buttons (and optionally a Help button).

The dialog is controlled by option flags passed to the constructor:

- `cdHelpButton` — adds a Help button.
- `cdNoLoadDir` — skips loading the initial directory on construction
      (useful when the dialog is restored from a stream).

On OK, [valid(int)](#validint-command) verifies that the text in the input line names a valid
existing directory and, if so, updates `System.getProperty("user.dir")`.

org.viktor44.jtvision.dialogs.JtvChangeDirDialog

## Summary

### Constructors

| Visibility | Signature                                                                          |
| ---------- | ---------------------------------------------------------------------------------- |
| public     | [JtvChangeDirDialog(int opts, int histId)](#jtvchangedirdialogint-opts-int-histid) |

### Fields

| Modifiers                | Field name                                                      | Type                                        |
| ------------------------ | --------------------------------------------------------------- | ------------------------------------------- |
| **private static final** | [changeDirTitle](#string-changedirtitle)                        | String                                      |
| **private static final** | [dirNameText](#string-dirnametext)                              | String                                      |
| **private static final** | [dirTreeText](#string-dirtreetext)                              | String                                      |
| **private static final** | [okText](#string-oktext)                                        | String                                      |
| **private static final** | [chdirText](#string-chdirtext)                                  | String                                      |
| **private static final** | [revertText](#string-reverttext)                                | String                                      |
| **private static final** | [helpText](#string-helptext)                                    | String                                      |
| **private static final** | [invalidText](#string-invalidtext)                              | String                                      |
| **private**              | [dirInput](#orgviktor44jtvisiondialogsjtvinputline-dirinput)    | org.viktor44.jtvision.dialogs.JtvInputLine  |
| **private**              | [dirList](#orgviktor44jtvisiondialogsjtvdirlistbox-dirlist)     | org.viktor44.jtvision.dialogs.JtvDirListBox |
| **private**              | [okButton](#orgviktor44jtvisiondialogsjtvbutton-okbutton)       | org.viktor44.jtvision.dialogs.JtvButton     |
| **private**              | [chDirButton](#orgviktor44jtvisiondialogsjtvbutton-chdirbutton) | org.viktor44.jtvision.dialogs.JtvButton     |

### Methods

| Modifiers   | Method signature                                              | Return type                             |
| ----------- | ------------------------------------------------------------- | --------------------------------------- |
| **public**  | [getDataTo(Object rec)](#getdatatoobject-rec)                 | void                                    |
| **public**  | [setDataFrom(Object rec)](#setdatafromobject-rec)             | void                                    |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)     | void                                    |
| **private** | [setUpDialog()](#setupdialog)                                 | void                                    |
| **private** | [trimEndSeparator(String path)](#trimendseparatorstring-path) | String                                  |
| **public**  | [valid(int command)](#validint-command)                       | boolean                                 |
| **public**  | [getChDirButton()](#getchdirbutton)                           | org.viktor44.jtvision.dialogs.JtvButton |



## Constructors

### `JtvChangeDirDialog(int opts, int histId)`

Creates the change-directory dialog.

**Parameters**

- `opts` - option flags: `cdHelpButton` to include a Help button,
                 `cdNoLoadDir` to skip initial directory loading
- `histId` - the history list ID for the directory name input line



## Fields

### `String changeDirTitle`

*No description provided*


### `String dirNameText`

*No description provided*


### `String dirTreeText`

*No description provided*


### `String okText`

*No description provided*


### `String chdirText`

*No description provided*


### `String revertText`

*No description provided*


### `String helpText`

*No description provided*


### `String invalidText`

*No description provided*


### `org.viktor44.jtvision.dialogs.JtvInputLine dirInput`

The input line where the user types or views the current directory path.


### `org.viktor44.jtvision.dialogs.JtvDirListBox dirList`

The directory tree list box.


### `org.viktor44.jtvision.dialogs.JtvButton okButton`

The OK button that confirms the directory change.


### `org.viktor44.jtvision.dialogs.JtvButton chDirButton`

The Chdir button that navigates to the directory currently selected in
the tree list box. Exposed so that [JtvDirListBox](JtvDirListBox.md) can toggle its
default state when the list box receives or loses focus.


## Methods

### `getDataTo(Object rec)`

Does nothing — data transfer is not implemented for this dialog.

**Parameters**

- `rec` - unused



### `setDataFrom(Object rec)`

Does nothing — data transfer is not implemented for this dialog.

**Parameters**

- `rec` - unused



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.dialogs.JtvDialog`

Handles `cmRevert` and `cmChangeDir` commands.



`cmRevert` restores the current working directory.
`cmChangeDir` navigates to the directory indicated by the
[JtvDirEntry](JtvDirEntry.md) carried in the event.

**Parameters**

- `event` - the incoming event



### `setUpDialog()`

Loads the initial directory tree and sets the input line to the current working directory.



### `trimEndSeparator(String path)`

Removes trailing path separators from `path`, leaving at least one character.

**Parameters**

- `path`


### `valid(int command)`

**Overrides:**

`valid(int command)` in `org.viktor44.jtvision.dialogs.JtvDialog`

Validates the dialog before it is closed with `cmOK`.



Checks that the directory named in the input line exists and is a directory.
If it is valid, sets `System.setProperty("user.dir")` to the absolute path.
On failure, displays an error message box and returns `false`.
All other commands always return `true`.

**Parameters**

- `command` - the command that is attempting to close the dialog

**Returns**

`true` if the dialog may close; `false` if validation failed



### `getChDirButton()`

The Chdir button that navigates to the directory currently selected in
the tree list box. Exposed so that [JtvDirListBox](JtvDirListBox.md) can toggle its
default state when the list box receives or loses focus.



