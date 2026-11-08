---
title: "JtvMemo"
sidebar_label: "JtvMemo"
---

Class JtvMemo
=============

```java
public class JtvMemo
extends JtvEditor
```


A multi-line text memo field for use inside JT Vision dialog boxes and forms.



`JtvMemo` extends [JtvEditor](JtvEditor.md) with data-transfer support ([getDataTo](#getdatatoobject-rec),
[setDataFrom](#setdatafromobject-rec), `dataSize`) so that it can participate in the standard dialog
data-record mechanism.  It is typically embedded in a dialog box alongside other controls.



The memo field treats its data as a simple string.  [setDataFrom](#setdatafromobject-rec) replaces the
entire editor buffer with the supplied text; [getDataTo](#getdatatoobject-rec) copies the current text
back into the caller's `StringBuilder`.

org.viktor44.jtvision.editors.JtvMemo

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                          |
| ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvMemo(JtvRect bounds, JtvScrollBar hScrollBar, JtvScrollBar vScrollBar, JtvIndicator indicator)](#jtvmemojtvrect-bounds-jtvscrollbar-hscrollbar-jtvscrollbar-vscrollbar-jtvindicator-indicator) |

### Methods

| Modifiers  | Method signature                                  | Return type |
| ---------- | ------------------------------------------------- | ----------- |
| **public** | [getDataTo(Object rec)](#getdatatoobject-rec)     | void        |
| **public** | [setDataFrom(Object rec)](#setdatafromobject-rec) | void        |



## Constructors

### `JtvMemo(JtvRect bounds, JtvScrollBar hScrollBar, JtvScrollBar vScrollBar, JtvIndicator indicator)`

Constructs a memo field.

**Parameters**

- `bounds` - the bounding rectangle for this view
- `hScrollBar` - optional horizontal scroll bar, or `null`
- `vScrollBar` - optional vertical scroll bar, or `null`
- `indicator` - optional position indicator, or `null`
- `bufferSize` - maximum buffer size in characters



## Methods

### `getDataTo(Object rec)`

Copies the current editor content into `rec`.
If `rec` is a `StringBuilder`, it is cleared and filled with the buffer text.

**Parameters**

- `rec` - target data record; must be a `StringBuilder` to receive text



### `setDataFrom(Object rec)`

Loads the editor buffer from `rec`.
If `rec` is a `CharSequence`, its content replaces the current buffer,
the cursor is moved to the start, and the modification flag is cleared.

**Parameters**

- `rec` - source data record; must be a `CharSequence`



