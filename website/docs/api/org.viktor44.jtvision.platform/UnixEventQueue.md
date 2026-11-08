---
title: "UnixEventQueue"
sidebar_label: "UnixEventQueue"
---

Class UnixEventQueue
====================

```java
abstract class UnixEventQueue
extends EventQueue
```


Unix/macOS base implementation of [EventQueue](EventQueue.md).



Two daemon threads cooperate:

1. `readerLoop()` — reads raw bytes from the terminal device
      (`/dev/tty` if available, otherwise `System.in`) and
      places them in `byteQueue`.
2. `inputLoop()` — consumes bytes from `byteQueue`,
      parses ANSI escape sequences (CSI / SS3 / SGR mouse), and pushes
      fully decoded [JtvEvent](../org.viktor44.jtvision.core/JtvEvent.md) objects into the shared event queue.

Raw mode is managed via `stty`. Subclasses: `LinuxEventQueue`
(no extra behaviour) and `MacosEventQueue` (Option-key mapping).

org.viktor44.jtvision.platform.UnixEventQueue

## Summary

### Constructors

| Visibility | Signature                           |
| ---------- | ----------------------------------- |
|            | [UnixEventQueue()](#unixeventqueue) |

### Fields

| Modifiers                | Field name                                                   | Type                                    |
| ------------------------ | ------------------------------------------------------------ | --------------------------------------- |
| **private static final** | [log](#orgslf4jlogger-log)                                   | org.slf4j.Logger                        |
| **private final**        | [byteQueue](#concurrentlinkedblockingqueueinteger-bytequeue) | concurrent.LinkedBlockingQueue&lt;Integer> |
| **private**              | [inputStream](#javaioinputstream-inputstream)                | java.io.InputStream                     |
| **private**              | [closeInputStreamOnStop](#boolean-closeinputstreamonstop)    | boolean                                 |
| **private static final** | [ttyDevice](#javaiofile-ttydevice)                           | java.io.File                            |
| **private**              | [savedTerminalState](#string-savedterminalstate)             | String                                  |
| **private volatile**     | [inBracketedPaste](#boolean-inbracketedpaste)                | boolean                                 |
| **private final**        | [pasteBuffer](#stringbuilder-pastebuffer)                    | StringBuilder                           |
| **private static final** | [ESC_SEQUENCE_TIMEOUT_MS](#int-esc_sequence_timeout_ms)      | int                                     |

### Methods

| Modifiers     | Method signature                                                                                           | Return type         |
| ------------- | ---------------------------------------------------------------------------------------------------------- | ------------------- |
| **protected** | [init()](#init)                                                                                            | void                |
| **protected** | [enableRawTerminalMode()](#enablerawterminalmode)                                                          | void                |
| **protected** | [disableRawTerminalMode()](#disablerawterminalmode)                                                        | void                |
| **protected** | [beforeStopThreads(boolean forceClose)](#beforestopthreadsboolean-forceclose)                              | void                |
| **protected** | [afterStopThreads()](#afterstopthreads)                                                                    | void                |
| **protected** | [openInputStream()](#openinputstream)                                                                      | java.io.InputStream |
| **private**   | [closeInputStream(boolean forceClose)](#closeinputstreamboolean-forceclose)                                | void                |
| **private**   | [runStty(String[] args)](#runsttystring-args)                                                              | boolean             |
| **private**   | [runSttyAndCapture(String[] args)](#runsttyandcapturestring-args)                                          | String              |
| **private**   | [runSttyInternal(boolean captureOutput, String[] args)](#runsttyinternalboolean-captureoutput-string-args) | String              |
| **private**   | [readerLoop()](#readerloop)                                                                                | void                |
| **private**   | [inputLoop()](#inputloop)                                                                                  | void                |
| **private**   | [printInput(int first)](#printinputint-first)                                                              | void                |
| **private**   | [processInput(int firstByte)](#processinputint-firstbyte)                                                  | void                |
| **protected** | [handleUtf8Input(int firstByte)](#handleutf8inputint-firstbyte)                                            | void                |
| **protected** | [macOptionCodeToAlt(int codePoint)](#macoptioncodetoaltint-codepoint)                                      | int                 |
| **private**   | [decodeUtf8Char(int firstByte)](#decodeutf8charint-firstbyte)                                              | String              |
| **private**   | [utf8ExtraByteCount(int firstByte)](#utf8extrabytecountint-firstbyte)                                      | int                 |
| **private**   | [parseCSI()](#parsecsi)                                                                                    | void                |
| **private**   | [csiModifierToAwtModifiers(String paramStr)](#csimodifiertoawtmodifiersstring-paramstr)                    | int                 |
| **private**   | [parseCsiTilde(String paramStr)](#parsecsitildestring-paramstr)                                            | void                |
| **private**   | [parseModifyOtherKeys(String paramStr)](#parsemodifyotherkeysstring-paramstr)                              | void                |
| **private**   | [parseCsiCaret(String paramStr)](#parsecsicaretstring-paramstr)                                            | void                |
| **private**   | [parseSS3()](#parsess3)                                                                                    | void                |
| **private**   | [readByteWithTimeout(int timeoutMs)](#readbytewithtimeoutint-timeoutms)                                    | int                 |
| **private**   | [parseSGRMouse(String params, char finalChar)](#parsesgrmousestring-params-char-finalchar)                 | void                |
| **private**   | [pushAltKey(int ch)](#pushaltkeyint-ch)                                                                    | void                |



## Constructors

### `UnixEventQueue()`

*No method description provided*



## Fields

### `org.slf4j.Logger log`

*No description provided*


### `concurrent.LinkedBlockingQueue<Integer> byteQueue`

Intermediate byte queue. `readerLoop()` offers raw bytes here;
`inputLoop()` polls them for escape-sequence parsing.


### `java.io.InputStream inputStream`

The terminal input stream; either `/dev/tty` or `System.in`,
opened by `openInputStream()`.


### `boolean closeInputStreamOnStop`

`true` if `inputStream` was opened by this class and must
be closed on shutdown (`/dev/tty` case).


### `java.io.File ttyDevice`

The Unix terminal device used for both raw input and `stty` commands.


### `String savedTerminalState`

Terminal state string captured by `stty -g` before raw mode is
enabled. Restored verbatim on shutdown.


### `boolean inBracketedPaste`

`true` while we are inside a bracketed-paste sequence
(`ESC[200~` received, waiting for `ESC[201~`).
Characters arriving in this state are collected in
`pasteBuffer` instead of being dispatched as key events.


### `StringBuilder pasteBuffer`

Accumulates the pasted text between the bracketed-paste start
marker (`ESC[200~`) and the end marker (`ESC[201~`).


### `int ESC_SEQUENCE_TIMEOUT_MS`

Maximum time in milliseconds to wait for the next byte after receiving
an ESC character (0x1B). If no byte arrives within this window, the ESC
is treated as a standalone Escape keystroke.


## Methods

### `init()`

**Overrides:**

`init()` in `org.viktor44.jtvision.platform.EventQueue`

*No method description provided*



### `enableRawTerminalMode()`

**Overrides:**

`enableRawTerminalMode()` in `org.viktor44.jtvision.platform.EventQueue`

*No method description provided*



### `disableRawTerminalMode()`

**Overrides:**

`disableRawTerminalMode()` in `org.viktor44.jtvision.platform.EventQueue`

*No method description provided*



### `beforeStopThreads(boolean forceClose)`

**Overrides:**

`beforeStopThreads(boolean forceClose)` in `org.viktor44.jtvision.platform.EventQueue`

*No method description provided*

**Parameters**

- `forceClose`


### `afterStopThreads()`

**Overrides:**

`afterStopThreads()` in `org.viktor44.jtvision.platform.EventQueue`

*No method description provided*



### `openInputStream()`

Opens the terminal input stream, preferring `/dev/tty` over
`System.in`. `/dev/tty` always refers to the controlling
terminal even when stdin is redirected.

**Returns**

the opened `InputStream`



### `closeInputStream(boolean forceClose)`

*No method description provided*

**Parameters**

- `forceClose`


### `runStty(String[] args)`

*No method description provided*

**Parameters**

- `args`


### `runSttyAndCapture(String[] args)`

*No method description provided*

**Parameters**

- `args`


### `runSttyInternal(boolean captureOutput, String[] args)`

*No method description provided*

**Parameters**

- `captureOutput`
- `args`


### `readerLoop()`

Unix reader thread body. Reads bytes from `inputStream` one at
a time and offers each to `byteQueue`. Exits when [running](EventQueue.md#boolean-running)
becomes `false` or the stream reaches EOF.



### `inputLoop()`

Unix input-decoding thread body. Polls `byteQueue` for bytes
(50 ms timeout) and passes each to `processInput`.



### `printInput(int first)`

*No method description provided*

**Parameters**

- `first`


### `processInput(int firstByte)`

Parses one input byte (or the start of a multi-byte sequence) and
pushes zero or more [JtvEvent](../org.viktor44.jtvision.core/JtvEvent.md) objects.



Dispatch:

- `0x1B` — ANSI escape sequence or plain Escape.
- `9` — Tab.
- `13` / `10` — Enter.
- `127` — Backspace.
- `< 32` (except 9, 10, 13) — Ctrl+letter.
- High bit set — UTF-8 multi-byte character.
- All other values — printable ASCII.



**Throws**

IOException: if reading continuation bytes fails

**Parameters**

- `firstByte` - the first byte of the input sequence



### `handleUtf8Input(int firstByte)`

Handles the first byte of a multi-byte UTF-8 sequence. Decodes the full
character and, if `macOptionCodeToAlt` returns a non-zero VK,
emits an Alt+key event instead.

**Throws**

IOException: if reading continuation bytes fails

**Parameters**

- `firstByte` - the high byte that signalled a UTF-8 sequence



### `macOptionCodeToAlt(int codePoint)`

Maps a Unicode code point to an AWT virtual-key code for macOS
Option+key combinations. Returns `0` on non-macOS platforms.
Overridden by `MacosEventQueue`.

**Parameters**

- `codePoint` - the Unicode code point to test

**Returns**

the AWT `VK_*` code, or `0`



### `decodeUtf8Char(int firstByte)`

*No method description provided*

**Parameters**

- `firstByte`


### `utf8ExtraByteCount(int firstByte)`

*No method description provided*

**Parameters**

- `firstByte`


### `parseCSI()`

Parses an ANSI CSI sequence (`ESC [`) and dispatches on the final
character. Supports cursor/function keys, VT tilde sequences, and SGR mouse.

**Throws**

IOException: if reading bytes fails



### `csiModifierToAwtModifiers(String paramStr)`

*No method description provided*

**Parameters**

- `paramStr`


### `parseCsiTilde(String paramStr)`

Handles VT tilde sequences (`ESC [ Pn ~`) for navigation and
function keys, with optional modifier field (`Pn;modifier~`).

**Parameters**

- `paramStr` - the parameter string before the `~` final byte



### `parseModifyOtherKeys(String paramStr)`

Handles xterm `modifyOtherKeys` sequences:
`ESC [ 27 ; modifier ; charCode ~`.



The modifier value follows the xterm convention (value − 1 is a
bitmask: bit 0 = Shift, bit 1 = Alt, bit 2 = Ctrl). The charCode is
the ASCII code of the key.

**Parameters**

- `paramStr` - the full parameter string, e.g. `"27;5;48"`



### `parseCsiCaret(String paramStr)`

Handles caret-terminated CSI sequences (`ESC [ Pn ^`).
Currently maps code `21` to F10.

**Parameters**

- `paramStr` - the numeric parameter before the `^` final byte



### `parseSS3()`

Parses an SS3 sequence (`ESC O <char>`) for function and cursor
keys. Also handles rxvt-style Ctrl+cursor: `ESC O A/B/C/D`.

**Throws**

IOException: if reading the final byte fails



### `readByteWithTimeout(int timeoutMs)`

Polls `byteQueue` for up to `timeoutMs` milliseconds.

**Throws**

IOException: never; declared for uniformity with callers

**Parameters**

- `timeoutMs` - maximum wait time in milliseconds

**Returns**

the byte value (0\u2013255), or `-1` on timeout/interrupt



### `parseSGRMouse(String params, char finalChar)`

Parses an SGR mouse event (`ESC [ < Pb ; Px ; Py M/m`).

**Parameters**

- `params` - parameter string after `<` (`"button;x;y"`)
- `finalChar` - `'M'` for press/move, `'m'` for release



### `pushAltKey(int ch)`

Converts a character received after `ESC` (Alt+key) to the
corresponding AWT virtual-key code and pushes an Alt+key event.

**Parameters**

- `ch` - the character byte following the ESC byte



