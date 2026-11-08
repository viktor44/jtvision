---
title: "JtvDialog"
sidebar_label: "JtvDialog"
---

Class JtvDialog
===============

```java
public class JtvDialog
extends JtvWindow
```


A descendant of [JtvWindow](../org.viktor44.jtvision.views/JtvWindow.md) used to create modal and non-modal dialog boxes.



Dialog boxes are windows that display a temporary interface for gathering user
input. TDialog adds keyboard handling for Tab/Shift+Tab focus cycling, Escape
(generates `cmCancel`), and Enter (broadcasts `cmDefault`).
When modal, the dialog ends its modal state on `cmOK`, `cmCancel`,
`cmYes`, or `cmNo` commands.



Three palette styles are available: gray (default), blue, and cyan, selected via
the `palette` field inherited from `TWindow`.

**Colour palette**

Each of the three 32-entry palettes (`cpGrayDialog`, `cpBlueDialog`,
`cpCyanDialog`) maps dialog positions 1–32 to the application palette:

1. Passive frame.
2. Active frame / title.
3. Frame icons.
4. Scroll bar page area.
5. Scroll bar arrows.
6. Static text.
7. Label normal text.
8. Label highlighted text.
9. Label shortcut character.
10. Button normal text.
11. Button default text.
12. Button selected text.
13. Button disabled text.
14. Button shortcut character.
15. Button shadow.
16. Cluster item normal text.
17. Cluster item selected text.
18. Cluster item shortcut character.
19. Input line passive text.
20. Input line selected text.
21. Input line scroll arrow.
22. History icon normal colour.
23. History icon shortcut colour.
24. Reserved.
25. Reserved.
26. Reserved.
27. Reserved.
28. Reserved.
29. Reserved.
30. File info pane text.
31. Cluster disabled item text.
32. Reserved.



org.viktor44.jtvision.dialogs.JtvDialog

## Summary

### Constructors

| Visibility | Signature                                                                        |
| ---------- | -------------------------------------------------------------------------------- |
| public     | [JtvDialog(JtvRect bounds, String title)](#jtvdialogjtvrect-bounds-string-title) |

### Fields

| Modifiers                | Field name                                                      | Type                                  |
| ------------------------ | --------------------------------------------------------------- | ------------------------------------- |
| **private static final** | [cpGrayDialog](#orgviktor44jtvisioncorejtvpalette-cpgraydialog) | org.viktor44.jtvision.core.JtvPalette |
| **private static final** | [cpBlueDialog](#orgviktor44jtvisioncorejtvpalette-cpbluedialog) | org.viktor44.jtvision.core.JtvPalette |
| **private static final** | [cpCyanDialog](#orgviktor44jtvisioncorejtvpalette-cpcyandialog) | org.viktor44.jtvision.core.JtvPalette |

### Methods

| Modifiers  | Method signature                                          | Return type                           |
| ---------- | --------------------------------------------------------- | ------------------------------------- |
| **public** | [getPalette()](#getpalette)                               | org.viktor44.jtvision.core.JtvPalette |
| **public** | [handleEvent(JtvEvent event)](#handleeventjtvevent-event) | void                                  |
| **public** | [valid(int command)](#validint-command)                   | boolean                               |



## Constructors

### `JtvDialog(JtvRect bounds, String title)`

Creates a dialog box with the given bounds and title.



Initialises with no window number, grow mode 0, move and close flags,
and the default gray palette.

**Parameters**

- `bounds` - the bounding rectangle of the dialog
- `title` - the title bar string



## Fields

### `org.viktor44.jtvision.core.JtvPalette cpGrayDialog`

Gray dialog 32-entry colour palette, mapping positions 1–32 to application
palette entries `0x20`–`0x3F`:

1. `0x20` — passive frame.
2. `0x21` — active frame / title.
3. `0x22` — frame icons.
4. `0x23` — scroll bar page area.
5. `0x24` — scroll bar arrows.
6. `0x25` — static text.
7. `0x26` — label normal text.
8. `0x27` — label highlighted text.
9. `0x28` — label shortcut character.
10. `0x29` — button normal text.
11. `0x2A` — button default text.
12. `0x2B` — button selected text.
13. `0x2C` — button disabled text.
14. `0x2D` — button shortcut character.
15. `0x2E` — button shadow.
16. `0x2F` — cluster item normal text.
17. `0x30` — cluster item selected text.
18. `0x31` — cluster item shortcut character.
19. `0x32` — input line passive text.
20. `0x33` — input line selected text.
21. `0x34` — input line scroll arrow.
22. `0x35` — history icon normal colour.
23. `0x36` — history icon shortcut colour.
24. `0x37` — reserved.
25. `0x38` — reserved.
26. `0x39` — reserved.
27. `0x3A` — reserved.
28. `0x3B` — reserved.
29. `0x3C` — reserved.
30. `0x3D` — file info pane text.
31. `0x3E` — cluster disabled item text.
32. `0x3F` — reserved.




### `org.viktor44.jtvision.core.JtvPalette cpBlueDialog`

Blue dialog 32-entry colour palette, mapping positions 1–32 to application
palette entries `0x40`–`0x5F`:

1. `0x40` — passive frame.
2. `0x41` — active frame / title.
3. `0x42` — frame icons.
4. `0x43` — scroll bar page area.
5. `0x44` — scroll bar arrows.
6. `0x45` — static text.
7. `0x46` — label normal text.
8. `0x47` — label highlighted text.
9. `0x48` — label shortcut character.
10. `0x49` — button normal text.
11. `0x4A` — button default text.
12. `0x4B` — button selected text.
13. `0x4C` — button disabled text.
14. `0x4D` — button shortcut character.
15. `0x4E` — button shadow.
16. `0x4F` — cluster item normal text.
17. `0x50` — cluster item selected text.
18. `0x51` — cluster item shortcut character.
19. `0x52` — input line passive text.
20. `0x53` — input line selected text.
21. `0x54` — input line scroll arrow.
22. `0x55` — history icon normal colour.
23. `0x56` — history icon shortcut colour.
24. `0x57` — reserved.
25. `0x58` — reserved.
26. `0x59` — reserved.
27. `0x5A` — reserved.
28. `0x5B` — reserved.
29. `0x5C` — reserved.
30. `0x5D` — file info pane text.
31. `0x5E` — cluster disabled item text.
32. `0x5F` — reserved.




### `org.viktor44.jtvision.core.JtvPalette cpCyanDialog`

Cyan dialog 32-entry colour palette, mapping positions 1–32 to application
palette entries `0x60`–`0x7F`:

1. `0x60` — passive frame.
2. `0x61` — active frame / title.
3. `0x62` — frame icons.
4. `0x63` — scroll bar page area.
5. `0x64` — scroll bar arrows.
6. `0x65` — static text.
7. `0x66` — label normal text.
8. `0x67` — label highlighted text.
9. `0x68` — label shortcut character.
10. `0x69` — button normal text.
11. `0x6A` — button default text.
12. `0x6B` — button selected text.
13. `0x6C` — button disabled text.
14. `0x6D` — button shortcut character.
15. `0x6E` — button shadow.
16. `0x6F` — cluster item normal text.
17. `0x70` — cluster item selected text.
18. `0x71` — cluster item shortcut character.
19. `0x72` — input line passive text.
20. `0x73` — input line selected text.
21. `0x74` — input line scroll arrow.
22. `0x75` — history icon normal colour.
23. `0x76` — history icon shortcut colour.
24. `0x77` — reserved.
25. `0x78` — reserved.
26. `0x79` — reserved.
27. `0x7A` — reserved.
28. `0x7B` — reserved.
29. `0x7C` — reserved.
30. `0x7D` — file info pane text.
31. `0x7E` — cluster disabled item text.
32. `0x7F` — reserved.




## Methods

### `getPalette()`

**Overrides:**

`getPalette()` in `org.viktor44.jtvision.views.JtvWindow`

Returns the color palette appropriate for the current palette style
(`dpGrayDialog`, `dpBlueDialog`, or `dpCyanDialog`).

**Returns**

the dialog's color palette



### `handleEvent(JtvEvent event)`

**Overrides:**

`handleEvent(JtvEvent event)` in `org.viktor44.jtvision.views.JtvWindow`

Handles keyboard and command events for the dialog.



Tab and Shift+Tab cycle focus among the dialog's controls. Escape
generates a `cmCancel` command; Enter broadcasts `cmDefault`.
When the dialog is modal, `cmOK`, `cmCancel`, `cmYes`,
and `cmNo` commands call `endModal`.

**Parameters**

- `event` - the event to handle



### `valid(int command)`

Always returns `true` for `cmCancel`; delegates to the
parent for all other commands.

**Parameters**

- `command` - the command to validate

**Returns**

`true` if the dialog may close for the given command



