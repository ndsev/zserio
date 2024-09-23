# Zserio Encoding Guide 1.0

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
Offset   00 01 02 03 04 05 06 07  08 09 10 11 12 13
00000000 20 09 4A 6F 65 20 53 6D  69 74 68 13 88 00
```

Detailed description of bit stream:

Byte position | Value             | Value (hex)                | Description
------------- | ----------------- | -------------------------- | -----------------------
0             | 32 (age)          | 20                         | `uint8` is fixed size 8 bit value
1             | 9 (string length) | 09                         | string length is encoded in `varsize` field before actual string
2-10          | Joe Smith         | 4A 6F 65 20 53 6D 69 74 68 | UTF-8 encoded string
11-12         | 5000              | 13 88                      | `uint16` always uses 2 bytes
13            | 0                 | 00                         | enum is of size `uint8` so it uses 1 byte

## Built-in Types

### Integer Built-in Types

All [integer built-in types](ZserioLanguageOverview.md#integer-built-in-types) are encoded as they are using
big endian byte order. Thus, for multi-byte integers, the most significant byte comes first.
Within each byte, the most significant bit comes first.

Negative values are represented in two's complement, i.e. the hex byte `FF` is `255` as `uint8` or
`-1` as `int8`.

**Example**

The decimal value `513` interpreted as `int16`:

```
Offset   00 01
00000000 02 01
```

Byte position | Value | Value (hex) | Value (bit)          | Description
------------- | ----- | ----------- | -------------------- | -------
0-1           | 513   | 02 01       | 0000 0010 0000 0001  | bit 0 is `1`, bit 15 is `0`

**Example**

The decimal value `-513` interpreted as `int16`:

```
Offset   00 01
00000000 FD FF
```

Byte position | Value | Value (hex) | Value (bit)          | Description
------------- | ----- | ----------- | -------------------- | -------
0-1           | -513  | FD FF       | 1111 1101 1111 1111  | The most significant bit (bit 15) is the first one

### Bit Field Types

All [bit field types](ZserioLanguageOverview.md#bit-field-types) are encoded as they are using big endian byte
order. Thus, for multi-byte integers, the most significant byte comes first.
Within each byte, the most significant bit comes first.

If the type size is not byte aligned, exact number of bits are encoded (e.g. `bit:2` is encoded in two bits).

**Example**

The decimal value `513` interpreted as `bit:12`:

```
Offset   00 01
00000000 20 10
```

Byte position | Value | Value (hex) | Value (bit)     | Description
------------- | ----- | ----------- | --------------- | -------
0-1           | 513   | 20 10       | 0010 0000 0001  | Only the first 4 bits of the second byte is used.

### Floating Point Types

All [floating point types](ZserioLanguageOverview.md#floating-point-types) are encoded as
16-bits/32-bits/64-bits integer numbers using their 16-bits/32-bits/64-bits floating point format defined
by IEEE 754 specification.

**Example**

The floating point value `8.0` interpreted as `float16`:

```
Offset   00 01
00000000 48 00
```

Byte position | Value | Value (hex) | Value (bit)          | Description
------------- | ----- | ----------- | -------------------- | -------
0-1           | 8.0   | 48 00       | 0100 1000 0000 0000  | The most significant bit (bit 15) is the first one

### Variable Integer Types

All [variable integer types](ZserioLanguageOverview.md#variable-integer-types) are encoded according to the
following table:

Data Type   | Byte | Description
----------- | ---- | -----------
varint16    | 0    | 1 bit sign, 1 bit has next byte, 6 bits value
<sup></sup> | 1    | 8 bits value
varuint16   | 0    | 1 bit has next byte, 7 bits value
<sup></sup> | 1    | 8 bits value
varint32    | 0    | 1 bit sign, 1 bit has next byte, 6 bits value
<sup></sup> | 1    | 1 bit has next byte, 7 bits value
<sup></sup> | 2    | 1 bit has next byte, 7 bits value
<sup></sup> | 3    | 8 bits value
varuint32   | 0    | 1 bit has next byte, 7 bits value
<sup></sup> | 1    | 1 bit has next byte, 7 bits value
<sup></sup> | 2    | 1 bit has next byte, 7 bits value
<sup></sup> | 3    | 8 bits value
varint64    | 0    | 1 bit sign, 1 bit has next byte, 6 bits value
<sup></sup> | 1    | 1 bit has next byte, 7 bits value
<sup></sup> | 2    | 1 bit has next byte, 7 bits value
<sup></sup> | 3    | 1 bit has next byte, 7 bits value
<sup></sup> | 4    | 1 bit has next byte, 7 bits value
<sup></sup> | 5    | 1 bit has next byte, 7 bits value
<sup></sup> | 6    | 1 bit has next byte, 7 bits value
<sup></sup> | 7    | 8 bits value
varuint64   | 0    | 1 bit has next byte, 7 bits value
<sup></sup> | 1    | 1 bit has next byte, 7 bits value
<sup></sup> | 2    | 1 bit has next byte, 7 bits value
<sup></sup> | 3    | 1 bit has next byte, 7 bits value
<sup></sup> | 4    | 1 bit has next byte, 7 bits value
<sup></sup> | 5    | 1 bit has next byte, 7 bits value
<sup></sup> | 6    | 1 bit has next byte, 7 bits value
<sup></sup> | 7    | 8 bits value
varint      | 0    | 1 bit sign, 1 bit has next byte, 6 bits value
<sup></sup> | 1    | 1 bit has next byte, 7 bits value
<sup></sup> | 2    | 1 bit has next byte, 7 bits value
<sup></sup> | 3    | 1 bit has next byte, 7 bits value
<sup></sup> | 4    | 1 bit has next byte, 7 bits value
<sup></sup> | 5    | 1 bit has next byte, 7 bits value
<sup></sup> | 6    | 1 bit has next byte, 7 bits value
<sup></sup> | 7    | 1 bit has next byte, 7 bits value
<sup></sup> | 8    | 8 bits value
varuint     | 0    | 1 bit has next byte, 7 bits value
<sup></sup> | 1    | 1 bit has next byte, 7 bits value
<sup></sup> | 2    | 1 bit has next byte, 7 bits value
<sup></sup> | 3    | 1 bit has next byte, 7 bits value
<sup></sup> | 4    | 1 bit has next byte, 7 bits value
<sup></sup> | 5    | 1 bit has next byte, 7 bits value
<sup></sup> | 6    | 1 bit has next byte, 7 bits value
<sup></sup> | 7    | 1 bit has next byte, 7 bits value
<sup></sup> | 8    | 8 bits value
varsize     | 0    | 1 bit has next byte, 2-7 bits value (*)
<sup></sup> | 1    | 1 bit has next byte, 7 bits value
<sup></sup> | 2    | 1 bit has next byte, 7 bits value
<sup></sup> | 3    | 1 bit has next byte, 7 bits value
<sup></sup> | 4    | 8 bits value

> Minimum size is always 1 byte, the other bytes are present only when previous *has next byte* bit is set
> to `1`

> (*) Maximum number stored in `varsize` is `2^31-1` which means `31` value bits. In this case, the first
> byte contains only 2 low significant bits. For example, the first encoded byte for `2^31-1` will be `0x83`.

### Boolean Type

[Boolean type](ZserioLanguageOverview.md#boolean-type) is encoded as a single bit. `true` is encoded
as a single bit `1` and `false` is encoded as a single bit `0`.

### String Type

[String type](ZserioLanguageOverview.md#string-type) is encoded by a length field
followed by a sequence of bytes (8 bits) in UTF-8 encoding.

| Byte position | Description |
| ------------- | ----------- |
| 0-4           | length of the string encoded as `varsize` |
| ~             | UTF-8 encoded string |

**Example**

The string *"Zserio is cool"* will be encoded as a bit stream:

```
Offset   00 01 02 03 04 05 06 07  08 09 10 11 12 13 14
00000000 0e 5a 73 65 72 69 6f 20  69 73 20 63 6f 6f 6c
```

| Byte position | Value              | Value (hex) | Description
| ------------- | ------------------ | ----------- | ------------
| 0             | 14                 | 0e          | length of the string encoded as `varsize`
| 1-14          | *"Zserio is cool"* | 5a 73 65 72 69 6f 20 69 73 20 63 6f 6f 6c | UTF-8 encoded string

### Extern Type

[Extern type](ZserioLanguageOverview.md#extern-type) is encoded by a field which represents number of bits followed by a bit sequence.

| Byte position | Description |
| ------------- | ----------- |
| 0-4           | number of bits encoded as `varsize` |
| ~             | bit sequence |

**Example**

The bit sequence `1010010111` will be encoded as a bit stream `00001010 101001011 11`:

```
Offset   00 01 02
00000000 0a a5 c0
```

| Byte position |  Value       | Value (hex) | Description |
| ------------- | ------------ | ----------- | ----------- |
| 0             | 10           | 0a          | number of bits encoded as `varsize` |
| 1-2           | `10100101 11` | a5 c0      | bit sequence (last 6 bits from the last byte is not used) |

### Bytes Type

[Bytes type](ZserioLanguageOverview.md#bytes-type) is encoded by a field which represents number of bytes followed by a byte sequence.

| Byte position | Description |
| ------------- | ----------- |
| 0-4           | number of bytes encoded as `varsize` |
| ~             | byte sequence |

**Example**

The byte sequence `de ad be ef` will be encoded as a bit stream:

```
Offset   00 01 02 03 04
00000000 04 de ad be ef
```

| Byte position | Value (hex) | Description |
| ------------- | ----------- | ----------- |
| 0             | 04          | number of bytes encoded as `varsize` |
| 1-4           | de ad be ef | byte sequence |

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

The enumeration value `RED` will be encoded as a bit stream `010`.

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

The bitmask value `READABLE` will be encoded as a bit stream `00000010`:

```
Offset   00
00000000 02
```

## Structure Types

[Structure types](ZserioLanguageOverview.md#structure-types) are encoded field by field without any padding or
alignment between fields.

**Example**

```
struct MyStructure
{
    bit:4 a = 7;
    uint8 b = 127;
    bit:4 c = 13;
};
```

The structure using the default values is encoded as a bit stream `01110111 11111101`:

```
Offset   00 01
00000000 77 fd
```

Byte position | Bit position | Value | Value (hex) | Description
------------- | ------------ | ----- | ----------- | -----------
0             | 0-3          | 7     | 7           | field `a`, 4 bits
0-1           | 4-11         | 127   | 7f          | field `b`, 8 bits (1 byte)
1             | 12-15        | 13    | d           | field `c`, 4 bits

> Note that member `b` overlaps a byte boundary, when the entire type is byte aligned. But `MyStructure` may
> also be embedded into another type where it may not be byte-aligned.

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

If the selector `width` is `24` and `coord24` field is `be de ad`, then the choice type will be encoded as
a bit stream `10111110 11011110 10101101`:

```
Offset   00 01 02
00000000 be de ad
```

> Note that parameter is not part of the choice payload.

## Union Types

[Union types](ZserioLanguageOverview.md#union-types) are encoded by a choice tag which represents order number
of the selected branch (counted from zero) followed by a field from selected branch.

Byte position | Description
------------- | -----------
0-4           | choice tag encoded as `varsize`
~             | field data from the selected branch

**Example**

```
union SimpleUnion
{
    uint8   value8;
    uint16  value16;
};
```

If the selected branch is `value16` with the value `de ad`, then the union type will be encoded as a bit stream
`00000001 11011110 10101101`:

```
Offset   00 01 02
00000000 01 DE AD
```

Byte position | Value | Value (hex) | Description
------------- | ----- | ----------- | -----------
0             | 1     | 01          | choice tag encoded as a `varsize`, value corresponds to the used field index counted from 0
1-2           | 57005 | de ad       | `uint16` always uses 2 bytes

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

Both examples from above result in the exact same bit stream.

## Optional Members

[Optional members](ZserioLanguageOverview.md#optional-members) are encoded in the same way as other
fields except of the optional members which are defined without `if` clause using a keyword `optional`. Such
auto optional members are encoded by single bit which indicates whether optional member is present or not
followed by encoded value.

Bit position | Description
------------ | -----------
1            | presence flag
~            | field data

**Example**

```
struct Container
{
    optional int32 autoOptionalInt;
};
```

If the `autoOptionalInt` values is set to the value `1054780911`, then the structure will be encoded
as a bit stream `10011111 01101111 01010110 11110111 1`:

```
Offset   00 01 02 03 04
00000000 9F 6F 56 F7 80
```

Byte position | Bit position | value      |  value (hex) | Description
------------- | ------------ | ---------- |  ----------- | -----------
0             | 0            | 1          |  1           | optional field is present
0-4           | 1-32         | 1054780911 |  3E DE AD EF | `int32` always uses 4 bytes, last 7 bits from the last byte are not used

> Thus the whole bit stream size is 33 bits.

If the `autoOptionalInt` value is not set, then the structure will be encoded as a single bit `0`.

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

Assuming the `ArrayExample` fields:
```
header = [be, eb]
numItems = 2
list = [ab, ba]
```

The structure will be encoded as a bit stream:
```
Offset   00 01 02 03 04 05
00000000 be eb 00 02 ab ba
```

Byte position | Value (hex) | Description
------------- | ----------- | -----------
0             | be          | `header[0]`
1             | eb          | `header[1]`
2-3           | 00 02       | `numItems`, `int16` always uses 2
4             | ab          | `list[0]`
5             | ba          | `list[1]`

> Note that arrays lengths don't need any additional payload.

### Implicit Length Arrays

[Implicit length arrays](ZserioLanguageOverview.md#implicit-length-arrays) are encoded in the same way as fixed
or variable length arrays. The decoder will continue matching instances of the element type until the end of the stream is
reached.

### Auto Length Arrays

[Auto length arrays](ZserioLanguageOverview.md#auto-length-arrays) are encoded by a field which represents
number of array elements followed by array values encoded in the same way as variable length arrays.

Byte position | Description
------------- | -----------
0-4           | number of array elements stored as a `varsize`
~             | array elements data

**Example**

```
struct AutoArray
{
    uint8 list[];
};
```

Assuming the `AutoArray` fields:
```
list = [be, eb]
```

The structure will be encoded as a bit stream:
```
Offset   00 01 02
00000000 02 BE EB
```

Byte position | Value (hex) | Description
------------- | ----------- | -----------
0             | 02          | array length encoded as `varsize`
1             | be          | list[0]
2             | eb          | list[1]

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

> `PackingDescriptor.maxBitNumber` is maximum bit number used for all deltas (counted from zero). Since delta
> can also be negative, the delta encoding needs an extra bit for the **sign**. Note that it's necessary to walk
> through the whole array to determine the proper `maxBitNumber` before writing.

**Example**

```
struct PackedArray
{
    packed uint8 list[5];
};
```

Assuming the `PackedArray` fields:
```
list = [11, 12, 15, 22, 23] # decimal values
```

The structure will be encoded as a bit stream `10000110 00010110 00100110 1110001`:
```
Offset  00 01 02 03
0000000 86 16 26 e2
```

Bit position | Value | Value (bin) | Description
------------ | ----- | ----------- | -----------
0            | 1     | 1           | `PackingDescriptor.isPacked` - packed, the deltas (differences between sequential elements) are `1, 3, 7, 1`, thus the maximum delta is `7`
1-6          | 3     | 000011      | `PackingDescriptor.maxBitNumber`, maximum delta `7` defines the maximum bit number used for all deltas as `3` (4 bits including sign)
7-14         | 11    | 00001011    | `list[0]` - first array element, `uint8` always uses 1 byte
15-17        | 1     | 0001         | 4-bit delta for the `list[1]`
18-20        | 3     | 0011         | 4-bit delta for the `list[2]`
21-23        | 7     | 0111         | 4-bit delta for the `list[3]`
24-26        | 1     | 0001         | 4-bit delta for the `list[4]`

> This is 31 bits instead of original 40 bits (five `uint8` elements).

Now consider another values:
```
list = [0, 250, 251, 252, 253]
```

The structure will be encoded as a bit stream `00000000 01111101 01111101 11111110 01111110 1`:
```
Offset   00 01 02 03 04 05
00000000 00 7d 7d fe 7e 80
```

Bit position | Value | Value (bin) | Description
------------ | ----- | ----------- | -----------
0            | 0     | 0           | `PackingDescriptor.isPacked` - not packed, the deltas are `250, 1, 1, 1`, thus the number of bits necessary for deltas will be `8` which would leave length of delta compressed array bigger than original array
1-8          | 0     | 00000000    | `list[0]`
9-16         | 250   | 11111010    | `list[1]`
17-24        | 251   | 11111011    | `list[2]`
25-32        | 252   | 11111100    | `list[3]`
33-40        | 253   | 11111101    | `list[3]`

> In this case, the packed array will be encoded by the single bit `0` (`isPacked` will be `false`) followed by the
> uncompressed array elements - it's 41 bits instead of original 40 bits.

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

Both examples from above result in the exact same bit stream.

### Packed Arrays of Compounds

[Packed arrays of compounds](ZserioLanguageOverview.md#packed-arrays) are encoded compound element by
compound element.

If the compound field is packable than the field in the first compound element will contain `PackingDescriptor`
followed by the field value. Each field in following compound elements will contain delta of the next
field value (if `PackingDescriptor.isPacked = 1`, otherwise it will contain the field value).

If the compound field is unpackable, it's value is simply written in the stream.

**Example**

```
struct PackableStructure
{
    uint32 value; // packable
    string text;  // unpackable
};

struct PackedArray
{
    packed PackableStructure list[5];
};
```

Assuming the `PackedArray` fields:
```
list = [
    PackableStructure(0, "a"),
    PackableStructure(10, "b"),
    PackableStructure(20, "c"),
    PackableStructure(30, "d")
    PackableStructure(40, "e")
]
```

The structure will be encoded as a bit stream:
```
Offset   00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15
00000000 88 00 00 00 00 02 c2 a0 16 25 00 b1 a8 05 91 40
00000010 2c a0
```

Bit position | Value | Value (hex, `bin`) | Description
------------ | ----- | ------------------ | -----------
0            | 1     | `1`                | `PackingDescriptor.isPacked` for `value` - packed, max delta is `10`
1-6          | 4     | 4                  | `PackingDescriptor.maxBitNumber` for `value` - maximum delta `10` defines maximum bit number as `4`
7-38         | 0     | 00 00 00 00        | `list[0].value` - first element, `uint32` always uses 4 bytes
39-46        | 1     | 01                 | length of `list[0].text` string, encoded as `varsize`
47-54        | "a"   | 61                 | UTF-8 encoded string "a"
55-59        | 10    | `01010`            | 5-bit delta for the `list[1].value`
60-67        | 1     | 01                 | length of `list[1].text` string, encoded as `varsize`
68-75        | "b"   | 62                 | UTF-8 encoded string "b"
76-80        | 10    | `01010`            | 5-bit delta for the `list[2].value`
81-88        | 1     | 01                 | length of `list[2].text` string, encoded as `varsize`
89-96        | "c"   | 63                 | UTF-8 encoded string "c"
99-101       | 10    | `01010`            | 5-bit delta for the `list[3].value`
102-109      | 1     | 01                 | length of `list[3].text` string, encoded as `varsize`
110-117      | "d"   | 64                 | UTF-8 encoded string "d"
118-122      | 10    | `01010`            | 5-bit delta for the `list[4].value`
123-130      | 1     | 01                 | length of `list[4].text` string, encoded as `varsize`
131-138      | "d"   | 65                 | UTF-8 encoded string "e"

> This is 139 bits instead of 240 bits which would be used without packing (`5 * (32 + 16`)).

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

Both examples from above result in the exact same bit stream.

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

Assuming the `PackedArray` fields:
```
list = [
    PackableStructure(10, "a", InnerStructure(1000, 65535)),
    PackableStructure(20, "b", InnerStructure(950, 0)),
    PackableStructure(30, "c", InnerStructure(1000, 65535)),
    PackableStructure(40, "d", InnerStructure(950, 0)),
    PackableStructure(50, "e", InnerStructure(1000, 65535)),
]
```

The structure will be encoded as a bit stream:
```
Offset   00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15
00000000 88 00 00 00 00 02 c3 18 00 00 00 00 00 00 0f a1
00000010 ff fe a0 16 29 c0 00 0a 01 63 65 ff fe a0 16 49
00000020 c0 00 0a 01 65 65 ff fe
```

Bit position | Value | Value (hex, `bin`) | Description
------------ | ----- | ------------------ | -----------
0            | 1     | `1`                | `PackingDescriptor.isPacked` for `value32` - packed, max delta is `10`
1-6          | 4     | 4                  | `PackingDescriptor.maxBitNumber` for `value32` - maximum delta `10` defines maximum bit number as `4`
7-38         | 0     | 00 00 00 00        | `list[0].value32` - first element, `uint32` always uses 4 bytes
39-46        | 1     | 01                 | length of `list[0].text` string, encoded as `varsize`
47-54        | "a"   | 61                 | UTF-8 encoded string "a"
55           | 1     | `1`                | `PackingDescriptor.isPacked` for `value64` - packed, max delta is `50`
56-61        | 6     | 6                  | `PackingDescriptor.maxBitNumber` for `value64` - maximum delta `50` defines maximum bit number as `6`
62-125       | 1000  | 3e8                | `list[0].innerStructure.value64` - first element, `uint64` always uses 8 bytes
126          | 0     | `0`                | `PackingDescriptor.isPacked` for `value16` - not packed, max delta is `65535`
127-142      | 65535 | ff ff              | `list[0].innerStructure.value16` - first element, `uint16` always uses 2 bytes
143-147      | 10    | `01010`            | 5-bit delta for the `list[1].value32`
148-155      | 1     | 01                 | length of `list[1].text` string, encoded as `varsize`
156-163      | "b"   | 62                 | UTF-8 encoded string "b"
164-170      | -50   | `1001110`          | 7-bit delta for `list[1].innerStructure.value64`
171-186      | 0     | 00 00              | `list[1].innerStructure.value16` - `uint16` always uses 2 bytes
187-191      | 10    | `01010`            | 5-bit delta for the `list[2].value32`
192-199      | 1     | 01                 | length of `list[2].text` string, encoded as `varsize`
200-207      | "b"   | 62                 | UTF-8 encoded string "b"
208-214      | -50   | `1001110`          | 7-bit delta for `list[2].innerStructure.value64`
215-230      | 0     | 00 00              | `list[2].innerStructure.value16` - `uint16` always uses 2 bytes
231-235      | 10    | `01010`            | 5-bit delta for the `list[3].value32`
236-243      | 1     | 01                 | length of `list[3].text` string, encoded as `varsize`
244-251      | "b"   | 62                 | UTF-8 encoded string "b"
252-258      | -50   | `1001110`          | 7-bit delta for `list[3].innerStructure.value64`
259-274      | 0     | 00 00              | `list[3].innerStructure.value16` - `uint16` always uses 2 bytes
275-279      | 10    | `01010`            | 5-bit delta for the `list[4].value32`
280-287      | 1     | 01                 | length of `list[4].text` string, encoded as `varsize`
288-295      | "b"   | 62                 | UTF-8 encoded string "b"
296-302      | -50   | `1001110`          | 7-bit delta for `list[4].innerStructure.value64`
303-318      | 0     | 00 00              | `list[4].innerStructure.value16` - `uint16` always uses 2 bytes

> This is 319 bits instead of 640 bits which would be used without packing (`5 * (32 + 16 + 64 + 16)`).
> Note that `value16` is not packed because it would be worse than

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

Bit position | Description
------------ | -----------
0-10         | member `a`
11-31        | unused padding (all bits set to `0`) caused by the alignment to 32-bits
32-63        | member `b`

> The total length is 64 bits (8 bytes).
> Note that without the alignment modifier, the size of this type would be 43 bits.

### Offsets

[Offsets](ZserioLanguageOverview.md#offsets) is encoded by automatic alignment of field with offset to 8 bits.

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

Bit position | Description
------------ | -----------
0-31         | member `offset`
32-42        | member `a`
43-47        | padding caused by alignment to 8-bits (caused by `offset:`)
48-63        | member `b`

> The total length 64 bits (8 bytes).
> Note that without the offset modifier, the size of this type would be 59 bits.

Offsets are checked by decoders and encoders automatically. Offsets can be set before encoding automatically.

### Indexed Offsets

[Indexed offsets](ZserioLanguageOverview.md#indexed-offsets) are encoded by automatic alignment of each
array element (of array which has indexed offsets) to 8 bits.

```
struct IndexedOffsetsExample
{
    uint32 offsets[2];
    bit:1  spacer;

offsets[@index]:
    bit:5  data[2];
};
```

Bit position | Description
------------ | -----------
0-31         | `offsets[0]`
32-63        | `offsets[1]`
64           | `spacer` (1-bit value)
65-71        | 7-bit padding caused by alignment to 8-bits
72-76        | `data[0]` (5-bits value)
77-79        | 3-bit padding caused by alignment to 8-bits
80-84        | `data[1]` (5-bits value)

Indexed offsets can be set before encoding automatically however an application is responsible to resize
correctly the array which holds indexed offsets.

[top](#zserio-encoding-guide)
