---
title: "JtvMenuPopup"
sidebar_label: "JtvMenuPopup"
---

Class JtvMenuPopup
==================

```java
public class JtvMenuPopup
extends JtvMenuBox
```


A standalone pop-up menu box with no parent menu.



JtvMenuPopup is a thin convenience subclass of [JtvMenuBox](JtvMenuBox.md) that sets
[JtvMenuView.parentMenu](JtvMenuView.md#orgviktor44jtvisionmenusjtvmenuview-parentmenu) to `null`. It is used when a context
menu or free-floating menu needs to be shown without being anchored to a
[JtvMenuBar](JtvMenuBar.md) or another [JtvMenuBox](JtvMenuBox.md).



Because there is no parent menu, pressing Escape or clicking outside the
box unconditionally dismisses it rather than returning focus to a parent
menu level.

org.viktor44.jtvision.menus.JtvMenuPopup

## Summary

### Constructors

| Visibility | Signature                                                                              |
| ---------- | -------------------------------------------------------------------------------------- |
| public     | [JtvMenuPopup(JtvRect bounds, JtvMenu menu)](#jtvmenupopupjtvrect-bounds-jtvmenu-menu) |



## Constructors

### `JtvMenuPopup(JtvRect bounds, JtvMenu menu)`

Constructs a pop-up menu with the given bounds and menu.
Passes `null` as the parent menu to
`JtvMenuBox.TMenuBox(JtvRect, JtvMenu, JtvMenuView)`.

**Parameters**

- `bounds` - the initial bounding rectangle (adjusted by
                `JtvMenuBox#adjustBounds()` before display)
- `menu` - the `JtvMenu` whose items to display



