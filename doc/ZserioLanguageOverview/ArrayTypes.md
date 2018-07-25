## Array Types

### Fixed and Variable Length Arrays

An array type is like a sequence of members of the same type. The element type may be any other type, except
an array type. (Two dimensional arrays can be emulated by wrapping the element type in a structure type.)

The length of an array is the number of elements, which may be fixed (i.e. set at compile time) or variable
(set at runtime). The elements of an array have indices ranging from 0 to _n_-1, where _n_ is the array length.

The notation for array types and elements is similar to C:

**Example**
```
struct ArrayExample
{
    uint8   header[256];
    int16   numItems;
    Element list[numItems];
};
```

Field `header` is a fixed-length array of 256 bytes. Field `list` is an array with _n_ elements, where _n_ is
the value of `numItems`. Individual array elements may be referenced in expressions with the usual index
notation, e.g. `list[2]` is the third element of the `list` array.

### Implicit Length Arrays

An array type may have an implicit length indicated by an `implicit` keyword and an empty pair of brackets.
In this case, the decoder will continue matching instances of the element type until the end of the stream is
reached. Implicit arrays must be at the end of the BLOB. It might also be the complete BLOB.

**Example**
```
struct ImplicitArray
{
    implicit Element list[];
};
```

The length of the `list` array can be referenced as `lengthof list`, see
[lengthof Operator](Expressions.md#lengthof-operator).

### Auto Length Arrays

An array type may have an automatic length indicated by an empty pair of brackets. In this case, the encoder
will automatically store the array length into the bit stream.

**Example**
```
struct AutoArray
{
    Element list[];
};
```

The length of the `list` array can be referenced as `lengthof list`, see
[lengthof Operator](Expressions.md#lengthof-operator).

Auto length arrays might be particularly useful if variable array length expression is a single field:

**Example**
```
struct AutoArrayCandidate
{
    uint32  numElements;
    Element list[numElements];
};
```

[\[top\]](ZserioLanguageOverview.md#language-guide)
