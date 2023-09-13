# Zserio Schema Evolution Guide

This document describes Zserio support of schema evolution.

Zserio does not add additional stuff to the binary stream like unique field numbers or field types. This
solution gives chance Zserio to outperform competitors in terms of data size but it avoids a good support of
schema evolution.

So schema evolution in Zserio is very limited and it is not fully supported.

Regarding schema evolution, we might distinguish two different compatibilities:

- Forward compatibility which allows usage of new schema for old applications
- Backward compatibility which allows usage of old schema for new applications

[Forward Compatible Changes](#forward-compatible-changes)

[Backward Compatible Changes](#backward-compatible-changes)

## Forward Compatible Changes

### Top Level Structure Extension

The forward compatibility of extension at the end of top level structures works automatically.
This is because the parsing always stops as soon as all fields are parsed even if there are some unread bits
left in the bit stream.

**Example**

```
struct TopLevelStructure
{
    uint32 fieldInVersion1;
    varuint newFieldInVersion2; // old appl will stop parsing here
};
```

Old applications will parse successfully new `TopLevelStructure` by reading only the first field
`fieldInVersion1` leaving field `newFieldInVersion2` unread in the bit stream.

## Backward Compatible Changes

### Top Level Structure Extension

The backward compatibility of extension at the end of top level structures is implemented by new language
keyword [`extend`](ZserioLanguageOverview.md#extended-members).

This keyword will indicate an optional extension which does not have to be encoded in the bit stream at all.

**Example**

```
struct TopLevelStructure
{
    uint32 fieldInVersion1;
    extend varuint newFieldInVersion2; // new appl will check end of stream here
};
```

New applications will parse successfully old `TopLevelStructure` by reading the first field
`fieldInVersion1`, accepting end of bit stream and leaving field `newFieldInVersion2` unused.
New applications will be responsible to check before each access of field `newFieldInVersion2` if it is
present or not.

### Choice Without Default Extension

The backward compatibility of adding a new case in choice without default case works automatically. This is
because all old cases in choice are known for a new application.

**Example**

```
choice UInt16Choice(uint16 selector) on selector
{
    case 1:
        VariantA a;

    case 2:
        VariantB b;

    case 3:
        VariantC newInVersion2; // new appl will never parse this case

    // default case must be either omitted or must be empty!
};
```

### Enumeration Extension

The backward compatibility of adding a new enumeration item without change of existed enumeration items works
automatically. This is because all old enumeration items are known for a new application.

**Example**

```
enum bit:8 DarkColor
{
    NONE,
    DARK_RED,
    DARK_BLUE,
    DARK_GREEN
    DARK_NEW_IN_VERSION2; // new appl will never parse this item
};
```

[top](#zserio-schema-evolution-guide)
