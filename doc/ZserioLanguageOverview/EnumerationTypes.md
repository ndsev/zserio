## Enumeration Types

An enumeration type has a base type which is an integer type or a bit field type. The members of an enumeration
have a name and a value which may be assigned explicitly or implicitly. A member that does not have
an initializer gets assigned the value of its predecessor incremented by 1, or the value 0 if it is the first
member.

**Example**

```
enum bit:3 Color
{
    NONE = 000b,
    RED = 010b,
    BLUE,
    BLACK = 111b
};
```

In the example above, `BLUE` has the value 3. When decoding a member of type `Color`, the decoder will read
3 bits from the stream and report an error when the integer value of these 3 bits is not one of 0, 2, 3 or 7.

An enumeration type provides its own lexical scope, similar to Java and dissimilar to C++. The member names
must be unique within each enumeration type, but may be reused in other contexts with different meanings.
Referring to the example, any other enumeration type `Foo` may also contain a member named `NONE`.

In expressions outside of the defining type, enumeration members must always be prefixed by the type name
and a dot, e.g. `Color.NONE`.

[[top]](ZserioLanguageOverview.md#language-guide)
