---
title: "JtvHelpTopic"
sidebar_label: "JtvHelpTopic"
---

Class JtvHelpTopic
==================

```java
public class JtvHelpTopic
```


A single help topic containing formatted text and optional cross-references.



A help topic stores its content as a list of raw text lines and a parallel
list of word-wrapped lines computed at the current display `width`.
Wrapped lines are recomputed whenever the width changes via
[setWidth(int)](#setwidthint-w).



Cross-references (hyperlinks to other topics) are stored as
[CrossRef](JtvHelpTopic.md) records. Each cross-reference identifies a rectangular
span within the word-wrapped view — a column, a 1-based line number, a
display length, and the target help-context ID — so that
[JtvHelpViewer](JtvHelpViewer.md) can highlight the spans and navigate between them.



Topics are normally created via [fromText(String)](#fromtextstring-text) or loaded
through [JtvHelpFile](JtvHelpFile.md). The copy constructor creates an independent
deep copy of all mutable state.

org.viktor44.jtvision.help.JtvHelpTopic

## Summary

### Constructors

| Visibility | Signature                                                           |
| ---------- | ------------------------------------------------------------------- |
| public     | [JtvHelpTopic()](#jtvhelptopic)                                     |
| public     | [JtvHelpTopic(JtvHelpTopic other)](#jtvhelptopicjtvhelptopic-other) |

### Fields

| Modifiers         | Field name                                                    | Type                                                   |
| ----------------- | ------------------------------------------------------------- | ------------------------------------------------------ |
| **private**       | [rawLines](#liststring-rawlines)                              | List&lt;String>                                           |
| **private**       | [wrapped](#liststring-wrapped)                                | List&lt;String>                                           |
| **private final** | [refs](#listorgviktor44jtvisionhelpjtvhelptopiccrossref-refs) | List&lt;org.viktor44.jtvision.help.JtvHelpTopic.CrossRef> |
| **private**       | [width](#int-width)                                           | int                                                    |

### Methods

| Modifiers         | Method signature                                                                             | Return type                                            |
| ----------------- | -------------------------------------------------------------------------------------------- | ------------------------------------------------------ |
| **public static** | [fromText(String text)](#fromtextstring-text)                                                | org.viktor44.jtvision.help.JtvHelpTopic                |
| **public**        | [setWidth(int w)](#setwidthint-w)                                                            | void                                                   |
| **public**        | [longestLineWidth()](#longestlinewidth)                                                      | int                                                    |
| **public**        | [numLines()](#numlines)                                                                      | int                                                    |
| **public**        | [getLine(int oneBased)](#getlineint-onebased)                                                | String                                                 |
| **public**        | [getNumCrossRefs()](#getnumcrossrefs)                                                        | int                                                    |
| **public**        | [getCrossRef(int idx)](#getcrossrefint-idx)                                                  | org.viktor44.jtvision.help.JtvHelpTopic.CrossRef       |
| **public**        | [addCrossRef(int x, int y, int length, int ref)](#addcrossrefint-x-int-y-int-length-int-ref) | void                                                   |
| **public**        | [crossRefs()](#crossrefs)                                                                    | List&lt;org.viktor44.jtvision.help.JtvHelpTopic.CrossRef> |
| **private**       | [rewrap()](#rewrap)                                                                          | void                                                   |



## Constructors

### `JtvHelpTopic()`

Creates an empty help topic with no text and no cross-references.



### `JtvHelpTopic(JtvHelpTopic other)`

Copy constructor — creates an independent deep copy of `other`.
The raw lines, wrapped lines, cross-references, and width are all
copied; subsequent modifications to either topic do not affect the
other.

**Parameters**

- `other` - the topic to copy



## Fields

### `List<String> rawLines`

The original unmodified lines of the topic text, split on newlines.
Used as the source for re-wrapping when the display width changes.


### `List<String> wrapped`

The word-wrapped lines computed from `rawLines` at the current
`width`. These are the lines rendered by [JtvHelpViewer](JtvHelpViewer.md).


### `List<org.viktor44.jtvision.help.JtvHelpTopic.CrossRef> refs`

Cross-references (hyperlinks) embedded in this topic's text.


### `int width`

The current word-wrap column width.
Lines wider than this value are split at word boundaries.
Defaults to `60`.


## Methods

### `fromText(String text)`

Creates a new topic by splitting `text` on line boundaries and
computing an initial word-wrap at the default width of 60 columns.
`null` is treated as an empty string.

**Parameters**

- `text` - the plain-text content of the topic

**Returns**

a new `THelpTopic` populated from `text`



### `setWidth(int w)`

Sets the word-wrap width and immediately recomputes the wrapped lines.
The minimum accepted width is 1.

**Parameters**

- `w` - the new column width; clamped to a minimum of `1`



### `longestLineWidth()`

Returns the length of the longest wrapped line, in characters.
Used by [JtvHelpViewer](JtvHelpViewer.md) to set the horizontal scroll limit.

**Returns**

the maximum wrapped-line length, or `0` if there are
        no lines



### `numLines()`

Returns the total number of word-wrapped lines in this topic.
Used by [JtvHelpViewer](JtvHelpViewer.md) to set the vertical scroll limit.

**Returns**

the wrapped line count; always at least `1`



### `getLine(int oneBased)`

Returns the wrapped line at the given 1-based line number.
Returns an empty string if `oneBased` is out of range.

**Parameters**

- `oneBased` - 1-based line index (line 1 is the first line)

**Returns**

the wrapped line text, or `""` if out of range



### `getNumCrossRefs()`

Returns the number of cross-references registered in this topic.

**Returns**

the cross-reference count



### `getCrossRef(int idx)`

Returns the cross-reference at the given zero-based index, or
`null` if `idx` is out of range.

**Parameters**

- `idx` - zero-based cross-reference index

**Returns**

the `CrossRef`, or `null` if out of range



### `addCrossRef(int x, int y, int length, int ref)`

Appends a new cross-reference to this topic.

**Parameters**

- `x` - 0-based column of the link text in the wrapped view
- `y` - 1-based line number of the link text in the wrapped view
- `length` - display width of the link text in columns
- `ref` - help-context ID of the target topic



### `crossRefs()`

Returns an unmodifiable view of all cross-references in this topic.

**Returns**

an unmodifiable list of `CrossRef` objects



### `rewrap()`

Recomputes `wrapped` from `rawLines` using the current
`width`. Lines that fit within the width are kept as-is; longer
lines are split at word boundaries (the last space at or before the
wrap column), falling back to a hard split if no space is found.
The result always contains at least one (possibly empty) line.



