## Alignment and Offsets

### Alignment

The `align(n)` modifier can be used to force the decoder to skip `0..n-1` bits so that the bit offset from
the beginning of the stream is divisible by `n`. `n` may be any integer literal.

Alignment modifiers may be used in any structure type:

**Example**
```
struct AlignmentExample
{
    bit:11  a;

align(32):
    uint32  b;
};
```

The size of the `AlignmentExample` type is 64 bits. Without the alignment modifier, the size would be 43 bits.

If a member with alignment is optional, alignment is optional as well:

**Example**
```
struct Example
{
    bool    hasOptional;
    
align(32):
    int32   myOptionalField if hasOptional == true;
    int32   myField;
};
```

In the above code example, if the member is optional (`hasOptional == false`) then no `align(32)` will be
executed and the size will be 33 bits.

### Offsets

The name of a member of integral type may be used as an offset on another member to indicate its byte offset
from the beginning of the stream:

**Example**
```
struct Tile
{
    TileHeader  header;
    uint32      stringOffset;
    uint16      numFeatures;

stringOffset:
    StringTable stringTable;
};
```

In this example, offset indicates that the value of `stringOffset` contains the byte offset of member
`stringTable` from the beginning of the stream.

Offsets are checked by decoders and encoders automatically. Offsets can be set before encoding automatically.

Since offsets always refer to byte offsets, a given member within a structure type cannot have an offset if it
is not guaranteed to be byte-aligned. To overcome this restriction, a byte alignment is inserted automatically:

**Example**
```
struct Tile
{
    TileHeader  header;
    uint32      stringOffset;
    uint16      numBits;
    bit:1       bits[numBits];

stringOffset: // also implies an align(8)
    StringTable stringTable;
};
```

If the member with offset is optional, the offset is optional as well:

**Example**
```
struct Example
{
    uint32  byteOffset;
    bool    hasOptional;

byteOffset:
    int32   myOptionalField if hasOptional == true;

    int32   myField;
};
```

In the code example above, if the member is optional (`hasOptional == false`) then no offset will be checked
and the size will be 65 bits.

### Indexed Offsets

When all elements in an array should have offsets, a special notation can be used:

**Example**
```
struct IndexedInt32Array
{
    uint32  offsets[10];
    bit:1   spacer;

offsets[@index]:
    int32   data[10];
};
```

In this example, `@index` denotes the current index of the `data array`. The use of this expression
in the array of `offsets` indicates that the _i_-th element of the array offsets contains a byte offset
of _i_-th element of member `data` calculated from the beginning of the stream.

Since offsets can refer only to byte offsets, each element of an array with indexed offset is automatically
byte-aligned:

**Example**
```
struct IndexedBit5Array
{
    uint32  offsets[2];
    bit:1   spacer;

offsets[@index]: // implies align(8) before each data[i]
    bit:5   data[2];
};
```

The size of the `IndexedBit5Array` type will be 64+1+7+5+3+5=85 bits. The size of offset array `data` will be
5+3+5=13 bits.

[\[top\]](ZserioLanguageOverview.md#language-guide)
