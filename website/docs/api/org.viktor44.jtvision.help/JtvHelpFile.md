---
title: "JtvHelpFile"
sidebar_label: "JtvHelpFile"
---

Class JtvHelpFile
=================

```java
public class JtvHelpFile
```


A binary help-file loader and topic registry for the JTVision help system.



JtvHelpFile reads a file in the custom JTVHELP binary format, parses each
help topic together with its paragraph text and cross-references, and
stores them in an internal map keyed by integer help-context IDs.



The binary format is:

1. 8-byte magic marker `"JTVHELP\0"`.
2. 4-byte format version (must equal `1`).
3. 4-byte topic count.
4. For each topic:
  - 4-byte ID count followed by that many 4-byte context IDs
          (a single topic may respond to multiple context values).
  - 4-byte paragraph count; for each paragraph: a boolean flag,
          a 4-byte text length, ISO-8859-1-encoded paragraph bytes,
          a 4-byte reference count, and for each reference four 4-byte
          integers followed by a length-prefixed reference name.

Once loaded, individual topics are retrieved via [getTopic(int)](#gettopicint-id).
A synthetic "No help available" topic is returned for unknown context IDs.
Topics are always returned as defensive copies so callers cannot mutate
the internal state.



Topics can also be inserted programmatically via [putTopic(int, JtvHelpTopic)](#puttopicint-id-jtvhelptopic-topic).

org.viktor44.jtvision.help.JtvHelpFile

## Summary

### Constructors

| Visibility | Signature                     |
| ---------- | ----------------------------- |
| public     | [JtvHelpFile()](#jtvhelpfile) |

### Fields

| Modifiers                | Field name                                                      | Type                                                 |
| ------------------------ | --------------------------------------------------------------- | ---------------------------------------------------- |
| **private static final** | [magic](#byte-magic)                                            | byte[]                                               |
| **private static final** | [formatVersion](#int-formatversion)                             | int                                                  |
| **private final**        | [topics](#mapintegerorgviktor44jtvisionhelpjtvhelptopic-topics) | Map&lt;Integer,org.viktor44.jtvision.help.JtvHelpTopic> |

### Methods

| Modifiers         | Method signature                                                           | Return type                             |
| ----------------- | -------------------------------------------------------------------------- | --------------------------------------- |
| **public static** | [load(Path file)](#loadpath-file)                                          | org.viktor44.jtvision.help.JtvHelpFile  |
| **public**        | [putTopic(int id, JtvHelpTopic topic)](#puttopicint-id-jtvhelptopic-topic) | void                                    |
| **public**        | [getTopic(int id)](#gettopicint-id)                                        | org.viktor44.jtvision.help.JtvHelpTopic |



## Constructors

### `JtvHelpFile()`

*No method description provided*



## Fields

### `byte[] magic`

File signature written at the start of every JTVHELP file.
The eight bytes spell `"JTVHELP"` followed by a NUL terminator.


### `int formatVersion`

The only accepted help-file format version number.


### `Map<Integer,org.viktor44.jtvision.help.JtvHelpTopic> topics`

Internal map from help-context ID to the corresponding topic.


## Methods

### `load(Path file)`

Loads a help file from the given path and returns the populated
`THelpFile` instance.



The file must start with the JTVHELP magic signature and declare
version `1`. Each topic's paragraphs are concatenated with
newline separators and the assembled text is passed to
[JtvHelpTopic.fromText(String)](JtvHelpTopic.md#fromtextstring-text). The resulting topic is registered
under every context ID listed for that topic entry.

**Throws**

IOException: if the file cannot be read, the magic signature does
                    not match, the version is unsupported, or the file
                    structure is otherwise corrupt

**Parameters**

- `file` - path to the JTVHELP binary file

**Returns**

a fully loaded `THelpFile`



### `putTopic(int id, JtvHelpTopic topic)`

Registers a topic under the given help-context ID.
Any previously registered topic for `id` is replaced.

**Parameters**

- `id` - the integer help-context ID
- `topic` - the topic to associate with that ID



### `getTopic(int id)`

Returns a defensive copy of the topic registered for `id`.



If no topic has been registered for the requested ID, a synthetic
topic containing the text `"No help available for context <id>."`
is returned instead.

**Parameters**

- `id` - the help-context ID to look up

**Returns**

a copy of the matching topic, never `null`



