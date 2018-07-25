## Member Access

The dot operator can be used to access a member of a compound type.

The expression `f.m` is valid if

- `f` is a field of a compound type `C`
- The type `T` of `f` is a compound type
- `T` has a member named `m`

The value of the expression `f.m` can be evaluated at runtime only if the member `f` has been evaluated before.

**Example**
```
struct Header
{
    uint16 version;
    uint16 numSentences;
};

struct Message
{
    Header header;
    string sentences[header.numSentences];
};
```

Within the scope of the `Message` type, `header` refers to the field of type `Header`, and
`header.numSentences` is a member of that type.

[\[top\]](ZserioLanguageOverview.md#language-guide)
