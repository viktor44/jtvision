---
title: "JtvFileEditor"
sidebar_label: "JtvFileEditor"
---

Class JtvFileEditor
===================

```java
public class JtvFileEditor
extends JtvEditor
```


A file-backed editor that loads and saves text files.



`JtvFileEditor` extends [JtvEditor](JtvEditor.md) with file I/O, search-and-replace dialogs,
and `cmSave`/`cmSaveAs` command handling.  It is normally hosted inside a
[JtvEditWindow](JtvEditWindow.md), which adds a window frame, scroll bars, and an indicator.



On construction the file named by `aFileName` is read into the edit buffer.
If the file does not exist the buffer starts empty (ready for a new file).
Before the editor closes, [valid(int)](#validint-command) prompts the user to save if the buffer
has been modified.

org.viktor44.jtvision.editors.JtvFileEditor

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                                                                       |
| ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvFileEditor(JtvRect bounds, JtvScrollBar hScrollBar, JtvScrollBar vScrollBar, JtvIndicator indicator, String fileName)](#jtvfileeditorjtvrect-bounds-jtvscrollbar-hscrollbar-jtvscrollbar-vscrollbar-jtvindicator-indicator-string-filename) |

### Fields

| Modifiers                | Field name                                  | Type             |
| ------------------------ | ------------------------------------------- | ---------------- |
| **private static final** | [log](#orgslf4jlogger-log)                  | org.slf4j.Logger |
| **private static final** | [efCaseSensitive](#int-efcasesensitive)     | int              |
| **private static final** | [efWholeWordsOnly](#int-efwholewordsonly)   | int              |
| **private static final** | [efPromptOnReplace](#int-efpromptonreplace) | int              |
| **private static final** | [efReplaceAll](#int-efreplaceall)           | int              |
| **private static final** | [efDoReplace](#int-efdoreplace)             | int              |
| **private static**       | [findText](#string-findtext)                | String           |
| **private static**       | [replaceText](#string-replacetext)          | String           |
| **private static**       | [editorFlags](#int-editorflags)             | int              |
| **private**              | [fileName](#string-filename)                | String           |

### Methods

| Modifiers          | Method signature                                                                          | Return type                             |
| ------------------ | ----------------------------------------------------------------------------------------- | --------------------------------------- |
| **public**         | [loadFile()](#loadfile)                                                                   | boolean                                 |
| **public**         | [save()](#save)                                                                           | boolean                                 |
| **public**         | [saveAs()](#saveas)                                                                       | boolean                                 |
| **public**         | [saveFile()](#savefile)                                                                   | boolean                                 |
| **private static** | [normalizeFileName(String name)](#normalizefilenamestring-name)                           | String                                  |
| **protected**      | [doFind()](#dofind)                                                                       | void                                    |
| **protected**      | [doReplace()](#doreplace)                                                                 | void                                    |
| **protected**      | [doSearchAgain()](#dosearchagain)                                                         | void                                    |
| **private**        | [doSearchReplace()](#dosearchreplace)                                                     | void                                    |
| **private**        | [search(String findStr, int opts)](#searchstring-findstr-int-opts)                        | boolean                                 |
| **private**        | [isWholeWord(String text, int start, int end)](#iswholewordstring-text-int-start-int-end) | boolean                                 |
| **private**        | [createFindDialog()](#createfinddialog)                                                   | org.viktor44.jtvision.dialogs.JtvDialog |
| **private**        | [createReplaceDialog()](#createreplacedialog)                                             | org.viktor44.jtvision.dialogs.JtvDialog |
| **public**         | [handleEvent(JtvEvent event)](#handleeventjtvevent-event)                                 | void                                    |
| **protected**      | [updateCommands()](#updatecommands)                                                       | void                                    |
| **public**         | [valid(int command)](#validint-command)                                                   | boolean                                 |
| **public**         | [getFileName()](#getfilename)                                                             | String                                  |



## Constructors

### `JtvFileEditor(JtvRect bounds, JtvScrollBar hScrollBar, JtvScrollBar vScrollBar, JtvIndicator indicator, String fileName)`

Constructs a file editor.  If `aFileName` is non-empty, the named file is
loaded into the buffer immediately via [loadFile()](#loadfile).

**Parameters**

- `bounds` - the bounding rectangle for this editor view
- `hScrollBar` - optional horizontal scroll bar, or `null`
- `vScrollBar` - optional vertical scroll bar, or `null`
- `indicator` - optional position indicator, or `null`
- `fileName` - path of the file to edit, or empty/null for a new untitled file



## Fields

### `org.slf4j.Logger log`

*No description provided*


### `int efCaseSensitive`

Search flag: match is case-sensitive.


### `int efWholeWordsOnly`

Search flag: match only whole words.


### `int efPromptOnReplace`

Search flag: prompt before replacing each occurrence.


### `int efReplaceAll`

Search flag: replace all occurrences without stopping.


### `int efDoReplace`

Internal flag: a replace (not just find) operation is in progress.


### `String findText`

Most recently used search string; shared across all `TFileEditor` instances.


### `String replaceText`

Most recently used replacement string; shared across all instances.


### `int editorFlags`

Current combination of `ef*` flags; shared across all instances.


### `String fileName`

The path of the file currently being edited.
Empty if editing an untitled (unsaved) file.


## Methods

### `loadFile()`

Reads the file named by `fileName` into the edit buffer.
If the file does not exist, the buffer is cleared (ready to create a new file)
and `true` is returned.  Displays an error message and returns `false`
on read failure.

**Returns**

`true` if the file was loaded (or does not yet exist); `false` on error



### `save()`

Saves the buffer.  If the file has no name yet, delegates to [saveAs()](#saveas);
otherwise delegates to [saveFile()](#savefile).

**Returns**

`true` if the save succeeded; `false` if cancelled or on error



### `saveAs()`

Presents a file-save dialog to let the user choose a file name, then saves the buffer.
Updates `fileName` and broadcasts `cmUpdateTitle` on success.

**Returns**

`true` if the file was saved; `false` if the dialog was cancelled or on error



### `saveFile()`

Writes the buffer to the file named by `fileName` using UTF-8 encoding.
Clears the [JtvEditor.modified](JtvEditor.md#boolean-modified) flag and redraws on success.
Displays an error message and returns `false` on write failure.

**Returns**

`true` if the file was written successfully; `false` on error



### `normalizeFileName(String name)`

*No method description provided*

**Parameters**

- `name`


### `doFind()`

**Overrides:**

`doFind()` in `org.viktor44.jtvision.editors.JtvEditor`

Opens the Find dialog to let the user enter a search string and options,
then performs the search.



### `doReplace()`

**Overrides:**

`doReplace()` in `org.viktor44.jtvision.editors.JtvEditor`

Opens the Replace dialog to let the user enter search and replacement strings and
options, then performs the search-and-replace.



### `doSearchAgain()`

**Overrides:**

`doSearchAgain()` in `org.viktor44.jtvision.editors.JtvEditor`

Repeats the most recent search/replace operation using the saved `findText`,
`replaceText`, and `editorFlags`.



### `doSearchReplace()`

*No method description provided*



### `search(String findStr, int opts)`

*No method description provided*

**Parameters**

- `findStr`
- `opts`


### `isWholeWord(String text, int start, int end)`

*No method description provided*

**Parameters**

- `text`
- `start`
- `end`


### `createFindDialog()`

*No method description provided*



### `createReplaceDialog()`

*No method description provided*



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.editors.JtvEditor`

Handles events by first delegating to the inherited [JtvEditor](JtvEditor.md) handler, then
intercepts `cmSave` and `cmSaveAs` commands to save the file.

**Parameters**

- `event` - the event to handle



### `updateCommands()`

**Overrides:**

`updateCommands()` in `org.viktor44.jtvision.editors.JtvEditor`

Extends the base command update to enable `cmSave` and `cmSaveAs` when
the editor is active, and disable them when it is not focused.



### `valid(int command)`

**Overrides:**

`valid(int command)` in `org.viktor44.jtvision.editors.JtvEditor`

Validates the editor for the given command.  For `cmValid`, returns
[JtvEditor.isValid](JtvEditor.md#boolean-isvalid).  For any other command (typically `cmClose`), if the
buffer has been modified, prompts the user to save, discard, or cancel.

**Parameters**

- `command` - the validation command

**Returns**

`true` if it is safe to proceed; `false` if the user cancelled



### `getFileName()`

The path of the file currently being edited.
Empty if editing an untitled (unsaved) file.



