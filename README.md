# JT Vision

A Java TUI framework inspired by the classic Turbo Vision paradigm. Build terminal applications with windows, dialogs, menus, status lines, and full keyboard/mouse input — cross-platform on Windows, macOS, and Linux.

## Features

- **Double-buffered screen rendering** with dirty-cell optimization and ANSI SGR output
- **Mouse support** — position, buttons, wheel, modifiers via ANSI SGR mouse protocol
- **Cross-platform input** — Win32 console API (via Jansi/Kernel32) on Windows; raw `stty` + POSIX ioctl on Unix/macOS
- **Color palette** — 1-based indexed color array mapping logical colors to CGA 8-bit attributes
- **Command system** — compact 256-bit command set for enabling/disabling actions
- **Event model** — unified event records covering keyboard, mouse, commands, and broadcasts

## Requirements

- Java 8+
- Maven

## Maven

```xml
<dependency>
    <groupId>org.viktor44.jtvision</groupId>
    <artifactId>jtvision</artifactId>
    <version>1.0</version>
</dependency>
```

## License

Apache License 2.0

## Issues

https://github.com/viktor44/jtvision/issues
