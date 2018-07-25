## Base Types

### Integer Base Types

Zserio supports the following integer base types

Sign     | Types
-------- | -------------------------------------
unsigned | `uint8`, `uint16`, `uint32`, `uint64`
signed   | `int8`, `int16`, `int32`, `int64`


These types correspond to unsigned or signed integers represented as sequences of 8, 16, 32 or 64 bits,
respectively. Negative values are represented in two's complement, i.e. the hex byte FF is 255 as `uint8`
or -1 as `int8`.

The default byte order is big endian. Thus, for multi-byte integers, the most significant byte comes first.
Within each byte, the most significant bit comes first.


**Example**

The byte stream `02 01` (hex) interpreted as `int16` has the decimal value 513. As a bit stream, this looks like
`0000 0010 0000 0001`. Bit 0 is `0`, bit 15 is `1`.

### Bit Field Types

#### Unsigned Bit Field Types

An unsigned bit field type is denoted by `bit:1`, `bit:2`, ...

The colon must be followed by a positive integer literal, which indicates the length of the type in bits.
The length should not exceed 63 bits. An unsigned bit field type corresponds to an unsigned integer of the
given length. Thus, `bit:16` and `uint16` are equivalent. The value range of `bit:n` is 0..2<sup>n</sup>-1.

Unsigned bitfield types of variable length can be specified as `bit<expr>`, where `expr` is an expression
of integer type to be evaluated at run-time and should not exceed 63 bits.

#### Signed Bit Field Types

A signed bit field type is denoted by `int:1`, `int:2`, ...

The colon must be followed by a positive integer literal, which indicates the length of the type in bits.
The length should not exceed 64 bits. A signed bit field type corresponds to a signed integer of the given
length. Thus, `int:16` and `int16` are equivalent. The value range of `int:n`
is -2<sup>n-1</sup>..2<sup>n-1</sup>-1.

Signed bitfield types of variable length bit field types can be specified as `int<expr>`, where `expr` is
an expression of integer type to be evaluated at run-time and should not exceed 64 bits.

#### Floating Point Types

Floating point types are modeled after the IEEE 754-2008 specification. The only supported type is `float16`.
This is a 16bit wide floating point number using 1 bit for the sign, 5 bits for the exponent and 10 bits for
the mantissa.

#### Variable Integer Types

Variable integer types store integer values but the number of bytes used is dependent on the actual value stored
in the data type. The supported types are `varint16`, `varint32` and `varint64` for the signed values and
`varuint16`, `varuint32` and `varuint64` for the unsigned signed values. This is a special type of integer that
uses only the bytes needed to store the value. This is achieved by using one bit byte (except the last byte).

The internal layout of the variable integer types is:

Data Type    | Byte Layout
------------ | -----------------------------------------------------
varint16     | [byte1]: 1 bit has sign, 1 bit hasbyte2, 6 bits value
 <sup></sup> | [byte2]: value
varuint16    | [byte1]: 1 bit hasbyte2, 7 bits value
 <sup></sup> | [byte2]: value
varint32     | [byte1]: 1 bit has sign, 1 bit hasbyte2, 6 bits value
 <sup></sup> | [byte2]: 1 bit hasbyte3, 7 bits value
 <sup></sup> | [byte3]: 1 bit hasbyte4, 7 bits value
 <sup></sup> | [byte4]: value
varuint32    | [byte1]: 1 bit hasbyte2, 7 bits value
 <sup></sup> | [byte2]: 1 bit hasbyte3, 7 bits value
 <sup></sup> | [byte3]: 1 bit hasbyte4, 7 bits value
 <sup></sup> | [byte4]: value
varint64     | [byte1]: 1 bit has sign, 1 bit hasbyte2, 6 bits value
 <sup></sup> | [byte2]: 1 bit hasbyte3, 7 bits value
 <sup></sup> | [byte3]: 1 bit hasbyte4, 7 bits value
 <sup></sup> | [byte4]: 1 bit hasbyte5, 7 bits value
 <sup></sup> | [byte5]: 1 bit hasbyte6, 7 bits value
 <sup></sup> | [byte6]: 1 bit hasbyte7, 7 bits value
 <sup></sup> | [byte7]: 1 bit hasbyte8, 7 bits value
 <sup></sup> | [byte8]: value
varuint64    | [byte1]: 1 bit hasbyte2, 7 bits value
 <sup></sup> | [byte2]: 1 bit hasbyte3, 7 bits value
 <sup></sup> | [byte3]: 1 bit hasbyte4, 7 bits value
 <sup></sup> | [byte4]: 1 bit hasbyte5, 7 bits value
 <sup></sup> | [byte5]: 1 bit hasbyte6, 7 bits value
 <sup></sup> | [byte6]: 1 bit hasbyte7, 7 bits value
 <sup></sup> | [byte7]: 1 bit hasbyte8, 7 bits value
 <sup></sup> | [byte8]: value

#### Boolean Type

In zserio, booleans are denoted by `bool`. A boolean is stored in a single bit. Both `true` and `false` are
available as built-in keywords that are stored as a 1 or 0, respectively.

**Example**
```
struct TestStructure
{
    bool hasValue;
    int16 value if hasValue == true;
};
```

#### String Type

A String type is denoted by `string`. It is represented by a length field (stored as a varuint64) followed by
a sequence of bytes (8 bits) in UTF-8 encoding. The string type allows a reader to skip the byte sequence since
its length is known upfront.

**Example**
```
struct TestStructure
{
    string textField;
};
```

[[top]](ZserioLanguageOverview.md#language-guide)
