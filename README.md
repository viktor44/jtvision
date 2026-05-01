# JT Vision

A Java text-based user interface framework inspired by the classic Turbo Vision library. Build terminal applications with windows, dialogs, menus, status lines, and full keyboard/mouse input — cross-platform on Windows, macOS, and Linux.

## Features

 * Multiple, resizeable, overlapping windows
 * Pull-down menus
 * Mouse support
 * Dialog boxes
 * Built-in color installation
 * Buttons, scroll bars, input boxes, check boxes, and radio buttons
 * Standard handling of keystrokes and mouse clicks
 * And more!

## Requirements

- Java 8+

## Maven

```xml
<dependency>
    <groupId>org.viktor44.jtvision</groupId>
    <artifactId>jtvision</artifactId>
    <version>1.0</version>
</dependency>
```

## Examples

Run scripts are in `./scripts/` — `.bat` for Windows, `.sh` for Unix/macOS.

### Hello World
```
scripts/run-hello
```
Minimal starter: greeting dialog, basic menu, and status line.

### Demo
```
scripts/run-demo-demo
```
Comprehensive showcase — puzzle, calendar, ASCII chart, calculator, file editor, color dialogs, mouse options, desktop state save/restore, and event viewer.

### Dir
```
scripts/run-demo-dir
```
Collapsible directory tree viewer with filesystem navigation.

### Editor
```
scripts/run-demo-edit
```
Multi-document text editor with cut/copy/paste, find/replace, file dialogs, and window management.

### Forms
```
scripts/run-demo-forms
```
Phone directory database — form-based data entry with file I/O and new/edit/delete record operations.

### Palette
```
scripts/run-demo-palette
```
Color palette customization demo showing per-view and per-window palette overrides.

### MultiMenu
```
scripts/run-demo-mmenu
```
Cycles through three different menu bar configurations at runtime.

## License

Apache License 2.0

## Issues

https://github.com/viktor44/jtvision/issues
