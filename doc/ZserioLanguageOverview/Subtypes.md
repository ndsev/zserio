## Subtypes

A subtype definition defines a new name for a given type. This is rather like a `typedef` command in C:

**Example**
```
subtype uint16 BlockIndex;

struct Block
{
    BlockIndex  blockIndex;
    uint32      data;
};
```

[\[top\]](ZserioLanguageOverview.md#language-guide)
