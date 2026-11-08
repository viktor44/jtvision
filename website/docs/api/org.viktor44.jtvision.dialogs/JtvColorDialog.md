---
title: "JtvColorDialog"
sidebar_label: "JtvColorDialog"
---

Class JtvColorDialog
====================

```java
public class JtvColorDialog
extends JtvDialog
```


A full-featured colour selection dialog box.



TColorDialog gives users a visual interface for changing any combination of
foreground and background colours in the application's palette. It is titled
"Colors" and contains:

- A colour-group list showing top-level categories.
- A colour-item list showing the individual palette entries in the selected group.
- Foreground and background [JtvColorSelector](JtvColorSelector.md) grids.
- A [JtvColorDisplay](JtvColorDisplay.md) preview showing sample text in the selected colours.
- A hidden [JtvMonoSelector](JtvMonoSelector.md) shown on monochrome displays.
- OK and Cancel buttons.

The palette being edited is passed to the constructor and a copy is held in
`pal`. [getDataTo(Object)](#getdatatoobject-rec) and [setDataFrom(Object)](#setdatafromobject-rec) transfer
the palette to/from a caller-supplied [JtvPalette](../org.viktor44.jtvision.core/JtvPalette.md) record.



The static `colorIndexes` field remembers the last focused group and
per-group item selections across dialog invocations.

org.viktor44.jtvision.dialogs.JtvColorDialog

## Summary

### Constructors

| Visibility | Signature                                                                                                                                                                                |
| ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| public     | [JtvColorDialog(JtvPalette aPalette, List&lt;org.viktor44.jtvision.dialogs.JtvColorGroup> aGroups)](#jtvcolordialogjtvpalette-apalette-listorgviktor44jtvisiondialogsjtvcolorgroup-agroups) |

### Fields

| Modifiers                | Field name                                                            | Type                                            |
| ------------------------ | --------------------------------------------------------------------- | ----------------------------------------------- |
| **private static**       | [colorIndexes](#orgviktor44jtvisiondialogsjtvcolorindex-colorindexes) | org.viktor44.jtvision.dialogs.JtvColorIndex     |
| **private static final** | [colors](#string-colors)                                              | String                                          |
| **private static final** | [groupText](#string-grouptext)                                        | String                                          |
| **private static final** | [itemText](#string-itemtext)                                          | String                                          |
| **private static final** | [forText](#string-fortext)                                            | String                                          |
| **private static final** | [bakText](#string-baktext)                                            | String                                          |
| **private static final** | [textText](#string-texttext)                                          | String                                          |
| **private static final** | [colorText](#string-colortext)                                        | String                                          |
| **private static final** | [okText](#string-oktext)                                              | String                                          |
| **private static final** | [cancelText](#string-canceltext)                                      | String                                          |
| **private**              | [pal](#orgviktor44jtvisioncorejtvpalette-pal)                         | org.viktor44.jtvision.core.JtvPalette           |
| **private**              | [display](#orgviktor44jtvisiondialogsjtvcolordisplay-display)         | org.viktor44.jtvision.dialogs.JtvColorDisplay   |
| **private**              | [groups](#orgviktor44jtvisiondialogsjtvcolorgrouplist-groups)         | org.viktor44.jtvision.dialogs.JtvColorGroupList |
| **private**              | [forLabel](#orgviktor44jtvisiondialogsjtvlabel-forlabel)              | org.viktor44.jtvision.dialogs.JtvLabel          |
| **private**              | [forSel](#orgviktor44jtvisiondialogsjtvcolorselector-forsel)          | org.viktor44.jtvision.dialogs.JtvColorSelector  |
| **private**              | [bakLabel](#orgviktor44jtvisiondialogsjtvlabel-baklabel)              | org.viktor44.jtvision.dialogs.JtvLabel          |
| **private**              | [bakSel](#orgviktor44jtvisiondialogsjtvcolorselector-baksel)          | org.viktor44.jtvision.dialogs.JtvColorSelector  |
| **private**              | [monoLabel](#orgviktor44jtvisiondialogsjtvlabel-monolabel)            | org.viktor44.jtvision.dialogs.JtvLabel          |
| **private**              | [monoSel](#orgviktor44jtvisiondialogsjtvmonoselector-monosel)         | org.viktor44.jtvision.dialogs.JtvMonoSelector   |
| **private**              | [groupIndex](#int-groupindex)                                         | int                                             |

### Methods

| Modifiers   | Method signature                                          | Return type |
| ----------- | --------------------------------------------------------- | ----------- |
| **public**  | [handleEvent(JtvEvent event)](#handleeventjtvevent-event) | void        |
| **public**  | [getDataTo(Object rec)](#getdatatoobject-rec)             | void        |
| **public**  | [setDataFrom(Object rec)](#setdatafromobject-rec)         | void        |
| **private** | [setIndexes()](#setindexes)                               | void        |
| **private** | [getIndexes()](#getindexes)                               | void        |



## Constructors

### `JtvColorDialog(JtvPalette aPalette, List<org.viktor44.jtvision.dialogs.JtvColorGroup> aGroups)`

Creates the colour dialog box.



Builds and inserts a 61×18 centred dialog with a group list, item list,
foreground/background colour selectors, a preview display, a hidden monochrome
selector, and OK/Cancel buttons. If `aPalette` is non-null, the dialog
is pre-populated via [setDataFrom(Object)](#setdatafromobject-rec).

**Parameters**

- `aPalette` - the application palette to edit; a copy is kept internally
- `aGroups` - the list of `JtvColorGroup` objects defining the palette categories



## Fields

### `org.viktor44.jtvision.dialogs.JtvColorIndex colorIndexes`

Static storage for the per-group selected-item indices, shared across
successive openings of the colour dialog.


### `String colors`

*No description provided*


### `String groupText`

*No description provided*


### `String itemText`

*No description provided*


### `String forText`

*No description provided*


### `String bakText`

*No description provided*


### `String textText`

*No description provided*


### `String colorText`

*No description provided*


### `String okText`

*No description provided*


### `String cancelText`

*No description provided*


### `org.viktor44.jtvision.core.JtvPalette pal`

Working copy of the application palette being edited.


### `org.viktor44.jtvision.dialogs.JtvColorDisplay display`

Preview view showing text in the selected colours.


### `org.viktor44.jtvision.dialogs.JtvColorGroupList groups`

List viewer for colour group names.


### `org.viktor44.jtvision.dialogs.JtvLabel forLabel`

Label for the foreground colour selector.


### `org.viktor44.jtvision.dialogs.JtvColorSelector forSel`

Foreground colour selector grid.


### `org.viktor44.jtvision.dialogs.JtvLabel bakLabel`

Label for the background colour selector.


### `org.viktor44.jtvision.dialogs.JtvColorSelector bakSel`

Background colour selector grid.


### `org.viktor44.jtvision.dialogs.JtvLabel monoLabel`

Label for the monochrome attribute selector (shown only on monochrome displays).


### `org.viktor44.jtvision.dialogs.JtvMonoSelector monoSel`

Monochrome attribute selector (hidden on colour displays).


### `int groupIndex`

Index of the most recently focused colour group.
Persisted via the static `colorIndexes` field.


## Methods

### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.dialogs.JtvDialog`

Extends the inherited event handler to:

- Save the focused group index on `cmNewColorItem` broadcasts.
- Update the colour display on `cmNewColorIndex` broadcasts.



**Parameters**

- `event` - the incoming event



### `getDataTo(Object rec)`

Copies the current (edited) palette into the caller-supplied [JtvPalette](../org.viktor44.jtvision.core/JtvPalette.md) record
after saving the current group/item selection indices to the static
`colorIndexes`.

**Parameters**

- `rec` - a `JtvPalette` to receive the edited palette data



### `setDataFrom(Object rec)`

Loads a new palette from `rec`, restores the saved group/item selection
indices, and updates the colour display.

**Parameters**

- `rec` - a `JtvPalette` containing the palette to edit



### `setIndexes()`

Restores the per-group item selection indices from the static
`colorIndexes` into the group list.



### `getIndexes()`

Saves the current per-group item selection indices into the static
`colorIndexes` for use in subsequent dialog openings.



