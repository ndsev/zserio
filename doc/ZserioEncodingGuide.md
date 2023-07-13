# Zserio Encoding Guide

This document describes the Zserio wire format which defines the details how Zserio encodes data to the binary
stream.

You probably don’t need to understand this in your application, but it’s useful information for optimizations
(e.g. to minimize space on disk needed for encoding Zserio object).

[Simple Example](#simple-example)

[Built-in Types](#built-in-types)

[Enumeration Types](#enumeration-types)

[Bitmask Types](#bitmask-types)

[Structure Types](#structure-types)

[Choice Types](#choice-types)

[Union Types](#union-types)

[Optional Members](#optional-members)

[Array Types](#array-types)

[Alignment and Offsets](#alignment-and-offsets)

## Simple Example

Zserio wire format is mostly very easy and basically what you see is what you get.

Consider the following Zserio schema:

```
package tutorial;

struct Employee
{
    uint8   age;
    string  name;
    uint16  salary;
    Role    role;
};

enum uint8 Role
{
    DEVELOPER = 0,
    TEAM_LEAD = 1,
    CTO       = 2,
};
```

If we use the schema above and serialize one employee with

- age = 32
- name = Joe Smith
- salary = 5000
- role = DEVELOPER

the resulting bit stream looks like the following:

```
Offset   00 01 02 03 04 05 06 07 08 09 10 11 12 13

00000000 20 09 4A 6F 65 20 53 6D 69 74 68 13 88 00
```

Detailed description of bit stream:

Byte position | value             | value (hex)                | comment
------------- | ----------------- | -------------------------- | -----------------------
0             | 32 (age)          | 20                         | `uint8` is fixed size 8 bit value
1             | 9 (string length) | 09                         | string length is encoded in `varsize` field before actual string
2-10          | Joe Smith         | 4A 6F 65 20 53 6D 69 74 68 | UTF-8 encoded string
11-12         | 5000              | 13 88                      | `uint16` always uses 2 bytes
13            | 0                 | 00                         | enum is of size `uint8` so it uses 1 byte

## Built-in Types

All [integer built-in types](ZserioLanguageOverview.md#integer-built-in-types),
[bit field types](ZserioLanguageOverview.md#bit-field-types),
[floating point types](ZserioLanguageOverview.md#floating-point-types),
[variable integer types](ZserioLanguageOverview.md#variable-integer-types) and
[boolean type](ZserioLanguageOverview.md#boolean-type) are encoded as they are using big endian byte order.
Thus, for multi-byte integers, the most significant byte comes first.
Within each byte, the most significant bit comes first.

If the type size is not byte aligned, exact number of bits are encoded (e.g. `bit:2` is encoded in two bits).

**Example**

The decimal value `513` interpreted as `int16` is encoded as a hex byte stream `02 01`. As a bit stream, this
looks like `0000 0010 0000 0001`. Bit 0 is `1`, bit 15 is `0`.

### String Type

[String type](ZserioLanguageOverview.md#string-type) is encoded by a length field (stored as a `varsize`)
followed by a sequence of bytes (8 bits) in UTF-8 encoding.

**Example**

The string `Zserio is cool` is encoded as a hex byte stream
`14 5a 73 65 72 69 6f 20 69 73 20 63 6f 6f 6c` where first byte `14` denotes length of the string in
bytes (hex).

### Extern Type

[Extern type](ZserioLanguageOverview.md#extern-type) is encoded by a field which represents number of bits
(stored as a `varsize`) followed by a bit sequence.

**Example**

The bit sequence `1010 0101 11` is encoded as a hex byte stream `0A A5 C0` (last 6 bits from the last
byte is not used). As a bit stream, this looks like `0000 1010 1010 0101 11`.

### Bytes Type

[Bytes type](ZserioLanguageOverview.md#bytes-type) is encoded by a field which represents number of bytes
(stored as a `varsize`) followed by a byte sequence.

**Example**

The byte sequence `DE AD BE EF` is encoded as a hex byte stream `04 DE AD BE EF`.

## Enumeration Types

[Enumeration types](ZserioLanguageOverview.md#enumeration-types) are encoded as its underlying type.

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

The enumeration value `RED` is encoded as a bit stream `010`.

## Bitmask Types

[Bitmask types](ZserioLanguageOverview.md#bitmask-types) are encoded as its underlying type.

**Example**

```
bitmask uint8 Permission
{
    EXECUTABLE,
    READABLE = 0x02,
    WRITABLE
};
```

The bitmask value `READABLE` is encoded as a hex byte stream `02`.

## Structure Types

[Structure types](ZserioLanguageOverview.md#structure-types) are encoded field by field without any padding or
alignment between fields.

**Example**

```
struct MyStructure
{
    bit:4 a;
    uint8 b;
    bit:4 c;
};
```

This type has a total length of 16 bits or 2 bytes. As a bit stream, bit offsets 0-3 correspond to member `a`,
bit offsets 4-11 represent an unsigned integer `b`, followed by member `c` in bit offsets 12-15. Note that
member `b` overlaps a byte boundary, when the entire type is byte aligned. But `MyStructure` may also be
embedded into another type where it may not be byte-aligned.

## Choice Types

[Choice types](ZserioLanguageOverview.md#choice-types) are encoded as its selected branch.

**Example**

```
choice VarCoordXY(uint8 width) on width
{
    case  8: bit:8  coord8;
    case 16: bit:16 coord16;
    case 24: bit:24 coord24;
    case 32: bit:32 coord32;
};
```

If the selector `width` is `24` and `coord24` field is `BE DE AD`, then choice type will be encoded
as a hex byte stream `BE DE AD`.

## Union Types

[Union types](ZserioLanguageOverview.md#union-types) are encoded by a field which represents order number
of the selected branch (counted from zero from the beginning stored as a `varsize`) followed by a field from
selected branch.

**Example**

```
union SimpleUnion
{
    uint8   value8;
    uint16  value16;
};
```

If the selected branch is `value16` with the value `DE AD`, then union type will be encoded as a hex byte
stream `01 DE AD`.

Union types are an automatic choice types and can be always replaced by choice types with the selector field.
The following shows choice type which corresponds to the previous `SimpleUnion` example:

**Example**

```
struct SimpleUnion
{
    ChoiceTag   choiceTag;
    SimpleValue simpleValue(choiceTag);
};

enum varsize ChoiceTag
{
    TAG_VALUE8  = 0,
    TAG_VALUE16 = 1
};

choice SimpleValue(ChoiceTag choiceTag) on choiceTag
{
    case TAG_VALUE8:
        uint8   value8;

    case TAG_VALUE16:
        uint16  value16;
};
```

Both examples from above result in the exact same byte stream.

## Optional Members

[Optional members](ZserioLanguageOverview.md#optional-members) are encoded in the same way as other
fields except of the optional members which are defined without `if` clause using a keyword `optional`. Such
auto optional members are encoded by single bit which indicates if whether optional member is present or not
followed by encoded value.

**Example**

```
struct Container
{
    optional int32 autoOptionalInt;
};
```

If the `autoOptionalInt` values is set to the value `3E DE AD EF`, then this optional member will be encoded
as a hex byte stream `9F 6F 56 F7 80` (last 7 bits from the last byte is not used). As a bit stream, this
looks like `1001 1111 0110 1111 0101 0110 1111 0111 1`.

If the `autoOptionalInt` value is not set, then this optional member will be encoded as a single bit `0`.

Optional members defined using `optional` keyword are an automatic optional members and can be always replaced
by optional members with `if` clause. The following shows optional member with `if` clause which corresponds
to the previous `Container` example:

**Example**

```
struct Container
{
    bool  hasOptionalInt;
    int32 optionalInt if hasOptionalInt;
}
```

Both examples from above result in the exact same byte stream.

## Array Types

### Fixed and Variable Length Arrays

[Fixed and variable length arrays](ZserioLanguageOverview.md#fixed-and-variable-length-arrays) are encoded
element by element in the same way as other fields.

**Example**

```
struct ArrayExample
{
    uint8 header[2];
    int16 numItems;
    uint8 list[numItems];
};

```

If the `header[0]` is set to `BE` and the `header[1]` is set to `EB` and `numItems` to `02` and `list[0]`
to `AB` and `list[1]` to `BA`, then the `ArrayExample` structure will be encoded as a hex byte stream
`BE EB 00 02 AB BA`.

### Implicit Length Arrays

[Implicit length arrays](ZserioLanguageOverview.md#implicit-length-arrays) are encoded in the same way as fixed
or variable length arrays.

### Auto Length Arrays

[Auto length arrays](ZserioLanguageOverview.md#auto-length-arrays) are encoded by a field which represents
number of array elements (stored as a `varsize`) followed by array values encoded in the same way as variable
length arrays.

**Example**

```
struct AutoArray
{
    uint8 list[];
};
```

If the `list[0]` is set to `BE` and the `list[1]` is set to `EB`, then the `AutoArray` structure will be
encoded as a hex byte stream `02 BE EB`.

Auto length arrays are an automatic arrays and can be always replaced by variable length arrays. The following
shows variable length array which corresponds to the previous `AutoArray` example:

**Example**

```
struct AutoArray
{
    varsize numElements;
    uint8 list[numElements];
};
```

Both examples from above result in the exact same byte stream.

### Packed Arrays of Integers

[Packed arrays of integers](ZserioLanguageOverview.md#packed-arrays) are encoded by `PackingDescriptor`
followed by the first array element and by the deltas for each other elements (packing is implemented
by delta compression).

```
struct PackingDescriptor
{
    bool isPacked;
    bit:6 maxBitNumber if isPacked;
};
```

**Example**

```
struct PackedArray
{
    packed uint8 list[5];
};
```

Consider that the `list` array is filled by the following decimal values `11 12 15 22 23`. Then, the deltas
(differences between sequential elements) are `1 3 7 1`. Thus, the maximum delta is `7` which defines
the maximum bit number used for all deltas as `2` (`maxBitNumber` counted from zero). Then,
`PackingDescriptor` will be encoded as a 7-bit stream `1 000010` followed by the first array element
`0000 1011` followed by four 3-bit delta values `001 011 111 001`. Putting all together this packed
array of integers will be encoded as a bit stream `1000 0100 0001 0110 0101 1111 001` which is 27 bits
instead of original 40 bits (five `uint8` elements).

Now consider that the `list` array is filled by the decimal values `0 250 251 252 253`. Then, the deltas
are `250 1 1 1`. Thus, the number of bits necessary for deltas will be `8` which would leave length of
delta compressed array bigger than original array (because of `PackingDescriptor`). In this case, packed
array will be encoded by the single bit `0` (`isPacked` will be `false` followed by the uncompressed array
elements. Putting all together this packed array of integers will be encoded as bit stream
`0 0000 0000 1111 1010 1111 1011 1111 1100 1111 1101` which is 41 bits instead of original 40 bits.

Packed arrays of integers can be always replaced by normal schema without packed functionality.
The following shows packed array of integers which corresponds to the previous `PackedArray` example:

**Example**

```
struct PackingDescriptor
{
    bool isPacked;
    bit:6 maxBitNumber if isPacked;
};

struct DeltaPackedArray(bit:6 maxBitNumber)
{
    uint8 element0;
    int<maxBitNumber + 1> deltas[4];
};

struct PackedArray
{
    PackingDescriptor packingDescriptor;
    DeltaPackedArray(packingDescriptor.maxBitNumber) packedList if packingDescriptor.isPacked;
    uint8 unpackedList[5] if !packingDescriptor.isPacked;
};
```

Both examples from above result in the exact same byte stream.

### Packed Arrays of Compounds

[Packed arrays of compounds](ZserioLanguageOverview.md#packed-arrays) are encoded compound by compound.
If the compound field is packable than the first compound element will contain `PackingDescriptor` followed
by the first compound element field. Each other compound elements will contain delta.

**Example**

```
struct PackableStructure
{
    uint32 value;
    string text;
};

struct PackedArray
{
    packed PackableStructure list[5];
};
```

Packed arrays of compounds can be always replaced by normal schema without packed functionality.
The following shows packed array of compounds  which corresponds to the previous `PackedArray` example:

```
struct PackingDescriptor
{
    bool isPacked;
    bit:6 maxBitNumber if isPacked;
};

struct PackableStructureElement0
{
    PackingDescriptor valuePackingDescriptor;
    uint32 value;
    string text;
};

struct PackableStructureElementX(PackingDescriptor valuePackingDescriptor)
{
    int<valuePackingDescriptor.maxBitNumber + 1> valueDelta if valuePackingDescriptor.isPacked;
    uint32 value if !valuePackingDescriptor.isPacked;
    string text;
};

struct PackedArray
{
    PackableStructureElement0 element0;
    PackableStructureElementX(element0.valuePackingDescriptor) elements[4];
};
```

Both examples from above result in the exact same byte stream.

Packed arrays of choices and unions are encoding in the same way, meaning that all fields corresponded to the
same case are considered as an array. If such array is empty, nothing is encoded in the bit stream.

### Packed Arrays of Nested Compounds

[Packed arrays of nested compounds](ZserioLanguageOverview.md#packed-arrays) are encoded in the same way
as packed arrays of compounds.

**Example**

```
struct InnerStructure
{
    uint64 value64;
    uint16 value16;
};

struct PackableStructure
{
    uint32 value32;
    string text;
    InnerStructure innerStructure;
};

struct PackedArray
{
    packed PackableStructure list[5];
};
```

Packed arrays of nested compounds can be always replaced by normal schema without packed functionality.
The following shows packed array of nested compounds  which corresponds to the previous `PackedArray` example:

```
struct PackingDescriptor
{
    bool isPacked;
    bit:6 maxBitNumber if isPacked;
};

struct InnerStructureElement0
{
    PackingDescriptor value64PackingDescriptor;
    uint64 value64;
    PackingDescriptor value16PackingDescriptor;
    uint16 value16;
};

struct PackableStructureElement0
{
    PackingDescriptor value32PackingDescriptor;
    uint32 value32;
    string text;
    InnerStructureElement0 innerStructureElement0;
};

struct InnerStructureElementX(PackingDescriptor value64PackingDescriptor,
        PackingDescriptor value16PackingDescriptor)
{
    int<value64PackingDescriptor.maxBitNumber + 1> value64Delta if value64PackingDescriptor.isPacked;
    uint64 value64 if !value64PackingDescriptor.isPacked;
    int<value16PackingDescriptor.maxBitNumber + 1> value16Delta if value16PackingDescriptor.isPacked;
    uint16 value16 if !value16PackingDescriptor.isPacked;
};

struct PackableStructureElementX(PackingDescriptor value32PackingDescriptor,
        PackingDescriptor value64PackingDescriptor, PackingDescriptor value16PackingDescriptor)
{
    int<value32PackingDescriptor.maxBitNumber + 1> value32Delta if value32PackingDescriptor.isPacked;
    uint32 value32 if !value32PackingDescriptor.isPacked;
    string text;
    InnerStructureElementX(value64PackingDescriptor, value16PackingDescriptor) innerStructureElementX;
};

struct PackedArray
{
    PackableStructureElement0 element0;
    PackableStructureElementX(element0.value32PackingDescriptor,
            element0.innerStructureElement0.value64PackingDescriptor,
            element0.innerStructureElement0.value16PackingDescriptor) elements[4];
};
```

Both examples from above result in the exact same byte stream.

## Alignment and Offsets

### Alignment

[Alignment](ZserioLanguageOverview.md#alignment) is encoded by padding of zero bits between fields.

**Example**

```
struct AlignmentExample
{
    bit:11 a;

align(32):
    uint32 b;
};
```

This type has a total length 64 bits or 8 bytes. As a bit stream, bit offsets 0-10 correspond to member `a`,
bit offsets 11-31 are not used (padding), followed by member `b` in bit offsets 32-63. Note that without
the alignment modifier, the size of this type would be 43 bits.

### Offsets

[Offsets](ZserioLanguageOverview.md#offsets) might be encoded by padding of zero bits between fields if
a structure field with offset is not byte-aligned. Otherwise, offsets do not have any effect to encoding.

**Example**

```
struct OffsetExample
{
    uint32 offset;
    bit:11 a;

offset:
    uint16 b;
};
```

This type has a total length 64 bits or 8 bytes. As a bit stream, bit offsets 0-31 correspond to member
`offset`, bit offsets 32-42 correspond to member `a`, bit offsets 43-47 are not used (padding), followed by
member `b` in bit offsets 48-63. Note that without the offset modifier, the size of this type would be 59 bits.

### Indexed Offsets

[Indexed offsets](ZserioLanguageOverview.md#indexed-offsets) might be encoded by padding of zero bits before
array element if an array element with offset is not byte-aligned. Otherwise, indexed offsets do not have any
effect to encoding.

```
struct IndexedOffsetsExample
{
    uint32 offsets[2];
    bit:1  spacer;

offsets[@index]:
    bit:5  data[2];
};
```

This type has a total length 32+32+1+7+5+3+5=85 bits. The size of offset array `data` is 5+3+5=13 bits.
As a bit stream, bit offsets 0-31 correspond to member `offsets[0]`, bit offsets 32-63 correspond to member
`offsets[1]`, bit offset 64 correspond to member `spacer`, bit offsets 65-71 are not used (padding). Then,
it follows member `data[0]` in bit offsets 72-76, bit offsets 77-79 are not used (padding) and bit offsets
80-84 correspond to member `data[1]`.

[top](#zserio-encoding-guide)
