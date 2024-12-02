# Zserio Language Overview

This document contains a detailed specification of the zserio schema language. The Zserio Language Overview
document is targeted for developers who write zserio schema definitions.

This document does not describe the details how Zserio encodes data to the binary stream. Encoding details
are described in [Zserio Encoding Guide](ZserioEncodingGuide.md#variable-integer-types).

Zserio is a serialization schema language for modeling binary data types, bitstreams or file formats. Based
on the zserio language it is possible to automatically generate encoders and decoders for a given schema
in various target languages (e.g. Java, C++, Python).

Zserio is similar to other serialization mechanism like
[*Google's Protocol Buffers*](https://github.com/google/protobuf) but does not use what is called
a "wire-format". Zserio therefore gives full control to the developers and comes with no serialization overhead.
It is a WYSIWYG serialization mechanism.

Zserio also features an extension for SQLite databases. With that extension it is possible to use SQLite
as a backend store for data defined with the zserio language. SQLite tables, columns and BLOBs can be all
described in zserio, giving the developer overall control of the data schema used in SQLite databases.

## Language Guide

[Literals](#literals)

[Built-in Types](#built-in-types)

[Constants](#constants)

[Enumeration Types](#enumeration-types)

[Bitmask Types](#bitmask-types)

[Compound Types](#compound-types)

[Array Types](#array-types)

[Alignment and Offsets](#alignment-and-offsets)

[Expressions](#expressions)

[Member Access](#member-access)

[Parameterized Types](#parameterized-types)

[Subtypes](#subtypes)

[Comments](#comments)

[Rules](#rules)

[Packages and Imports](#packages-and-imports)

[Templates](#templates)

[Service Types](#service-types)

[Pubsub Types](#pubsub-types)

[SQLite Extension](#sqlite-extension)

[Annotations](#annotations)

[Compiler Directives](#compiler-directives)

[Background & History](#background--history)

[License & Copyright](#license--copyright)

[*Quick Reference*](ZserioQuickReference.md)

## Literals

The zserio syntax for literal values is similar to the Java syntax. There are no character literals, only
string literals with the usual escape syntax. Integer literals can use decimal, hexadecimal, octal or
binary notation.

Type        | Value
----------- | --------------------------------
Boolean     | `true`, `false`
Decimal     | `100`, `4711`, `255`, `-3`, `+2`
Hexadecimal | `0xCAFEBABE`, `0Xff`, `-0xEF`
Octal       | `044`, `0377`, `-010`
Binary      | `111b`, `110b`, `001B`, `-1010b`
Float16     | `3.14f`, `31.4e-1f`, `314e-2f`
Float32     | `3.14f`, `31.4e-1f`, `314e-2f`
Float64     | `3.14`, `0.314e+1`, `0.0314e2`
String      | `"You"`

Hexadecimal digits and the `x` prefix as well as the `b`, `e` and 'f' suffixes are case-insensitive.
Signing literals can be defined by `-` or `+` prefix. Signs are not applicable for string literals.

[top](#language-guide)

## Built-in Types

### Integer Built-in Types

Zserio supports the following integer built-in types

Sign     | Types
-------- | -------------------------------------
unsigned | `uint8`, `uint16`, `uint32`, `uint64`
signed   | `int8`, `int16`, `int32`, `int64`


These types correspond to unsigned or signed integers represented as sequences of 8, 16, 32 or 64 bits,
respectively.

### Bit Field Types

#### Unsigned Bit Field Types

An unsigned bit field type is denoted by `bit:1`, `bit:2`, ...

The colon must be followed by a positive integer literal, which indicates the length of the type in bits.
The length should not exceed 64 bits. An unsigned bit field type corresponds to an unsigned integer of the
given length. Thus, `bit:16` and `uint16` are equivalent. The value range of `bit:n` is 0..2<sup>n</sup>-1.

Unsigned bitfield types of variable length (dynamic unsigned bitfield types) can be specified as `bit<expr>`,
where `expr` is an expression of integer type to be evaluated at run-time and shall be in range `[1, 64]` bits.

#### Signed Bit Field Types

A signed bit field type is denoted by `int:1`, `int:2`, ...

The colon must be followed by a positive integer literal, which indicates the length of the type in bits.
The length should not exceed 64 bits. A signed bit field type corresponds to a signed integer of the given
length. Thus, `int:16` and `int16` are equivalent. The value range of `int:n`
is -2<sup>n-1</sup>..2<sup>n-1</sup>-1.

Signed bitfield types of variable length (dynamic signed bitfield types) can be specified as `int<expr>`,
where `expr` is an expression of integer type to be evaluated at run-time and shall be in range `[1, 64]` bits.

### Floating Point Types

Floating point types are modeled after the IEEE 754 specification. The following types are supported:

* `float16` - Half-precision floating-point format stored in 16 bits using 1 bit for the sign,
5 bits for the exponent and 10 bits for the significand.

* `float32` - Single-precision floating-point format stored in 32 bits using 1 bit for the sign,
8 bits for the exponent and 23 bits for the significand.

* `float64` - Double-precision floating-point format stored in 64 bits using 1 bit for the sign,
11 bits for the exponent and 52 bits for the significand.

### Variable Integer Types

Variable integer types store integer values but the number of bytes used is dependent on the actual value stored
in the data type. The supported types are `varint16`, `varint32`, `varint64` and `varint` for
the signed values and `varuint16`, `varuint32`, `varuint64`, `varuint` and `varsize` for the unsigned
signed values. This is a special type of integer that uses only the bytes needed to store the value.

The value ranges of the variable integer types are:

Data Type    | Value Range                                        | Max Value | Max Bytes
------------ | -------------------------------------------------- | --------- |----------
varint16     | `-16383 to 16383`                                  | `2^14-1` | `2`
varint32     | `-268435455 to 268435455`                          | `2^28-1` | `4`
varint64     | `-72057594037927935 to 72057594037927935`          | `2^56-1` | `8`
varint       | `-9223372036854775808 to 9223372036854775807`      | `2^63-1`| `9`
varuint16    | `0 to 32767`                                       | `2^15-1` | `2`
varuint32    | `0 to 536870911`                                   | `2^29-1` | `4`
varuint64    | `0 to 144115188075855871`                          | `2^57-1` | `8`
varuint      | `0 to 18446744073709551615`                        | `2^64-1` | `9`
varsize      | `0 to 2147483647`                                  | `2^31-1` | `5`

> Note that `varint` and `varuint` can handle all `int64` and `uint64` values respectively.

> Note that `varsize` is designed for array size type to allow compatibility across languages. Because of that
> its range is limited to the biggest value of 32-bit signed integer and does not cover all available bits.

> Note that `varsize` is available since `2.0.0`.

### Boolean Type

In zserio, booleans are denoted by `bool`. Both `true` and `false` are available as built-in keywords.

**Example**
```
struct TestStructure
{
    bool hasValue;
    int16 value if hasValue == true;
};
```

### String Type

A String type is denoted by `string`. It is represented by a length field followed by a sequence of bytes
in UTF-8 encoding. The string type allows a reader to skip the byte sequence since its length is known upfront.

**Example**
```
struct TestStructure
{
    string textField;
};
```

### Extern Type

External type is a zserio built-in type which format is not known by zserio. It is handled as arbitrary bit
sequence which is passed to application for further processing. It is denoted by zserio keyword `extern`. It is
represented by a number of bits followed by a bit sequence.

**Example**
```
struct StructureWithExternalField
{
    bit:3   numberA;
    extern  blob;
    bit:7   numberC;
};
```
> Since `1.4.0`

[top](#language-guide)

### Bytes Type

Bytes type is a zserio built-in type for raw bytes sequences. It is denoted by zserio keyword `bytes`. It is
represented by a number of bytes followed by a byte sequence.

**Example**
```
struct StructureWithBytesField
{
    string text;
    bytes  data;
};
```

> Since `2.9.0`

[top](#language-guide)

## Constants

A constant is an immutable named value. The syntax and behavior is similar to C or C++. Their syntax is as
follows:

```
const built-in-type NAME = literal;
```

**Example**

```
const bit:1 FALSE = 0;
const bit:1 TRUE = 1;
const int16 i = 1234;
const int32 j = -5678;
```

[top](#language-guide)

## Enumeration Types

An enumeration type has a base type which is an integer type or a bit field type. The items of an enumeration
have a name and a value which may be assigned explicitly or implicitly. An item that does not have
an initializer gets assigned the value of its predecessor incremented by 1, or the value 0 if it is the first
item.

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

In the example above, `BLUE` has the value 3. When decoding an item of type `Color`, the decoder will read
3 bits from the stream and report an error when the integer value of these 3 bits is not one of 0, 2, 3 or 7.

An enumeration type provides its own lexical scope, similar to Java and dissimilar to C++. The item names
must be unique within each enumeration type, but may be reused in other contexts with different meanings.
Referring to the example, any other enumeration type `Foo` may also contain an item named `NONE`.

In expressions outside of the defining type, enumeration items must always be prefixed by the type name
and a dot, e.g. `Color.NONE`.

The enumeration value represented by integer type can be referenced as `valueof(enumeration)`,
see [valueof Operator](#valueof-operator).

Enumeration items can be [`@deprecated`](#deprecated-annotation) or [`@removed`](#removed-annotation).

[top](#language-guide)

## Bitmask Types

A bitmask type has a base type which is an unsigned integer or unsigned bit field type. The values of a bitmask
have a name and a value which may be assigned explicitly or implicitly. A value that does not have
an initializer gets assigned the value calculated from its predecessor by finding the first unused bit.
If the unspecified value is the first one, it will be assigned to 1.

**Example**

```
bitmask uint8 Permission
{
    EXECUTABLE,
    READABLE = 0x02,
    WRITABLE
};
```

In the example above, `EXECUTABLE` is auto-assigned to 1, `READABLE` is manually assigned to 2 and the
`WRITABLE` is assigned by finding the first unused bit to value 4.

A bitmask type provides its own lexical scope. The value names must be unique within each bitmask type.

In expressions outside of the defining type, bitmask values must always be prefixed by the type name and a dot,
e.g. `Permission.WRITABLE`. The [isset](#isset-operator) operator is the exception to this rule where
its type name with a dot can be omitted.

Bitmasks support all basic bit operations:
* `&` bitwise and,
* `|` bitwise or,
* `^` bitwise xor,
* `~` bitwise complement.

**Example**

```
bitmask bit:2 Availability
{
    VERSION_NUMBER,
    VERSION_STRING
};

struct Version(Availability availability)
{
    uint32 versionNumber if (availability & Availability.VERSION_NUMBER) == Availability.VERSION_NUMBER;
    string versionString if (availability & Availability.VERSION_STRING) == Availability.VERSION_STRING;
};
```

In the example above, the `availability` parameter defines available version formats. `versionNumber` and
`versionString` are present only if the `availability` contains the `VERSION_NUMBER` and `VERSION_STRING` masks
respectively.

>Note that the bitmask can contain a value which is manually assigned to 0 and which represents an empty
bitmask (e.g. `NONE = 0`). Such `NONE` mask can be useful in expressions.

The bitmask value represented by integer type can be referenced as `valueof(Permission.EXECUTABLE)`,
see [valueof Operator](#valueof-operator).

> Since `2.0.0`

[top](#language-guide)

## Compound Types

### Structure Types

A structure type is the concatenation of its members. There is no padding or alignment between members.

**Example**
```
struct MyStructure
{
    bit:4 a;
    uint8 b;
    bit:4 c;
};
```

### Choice Types

A choice type depends on a _selector expression_ following the `on` keyword. Each branch of the choice type is
preceded by one or more case labels with a literal value. After evaluating the selector expression, the decoder
will directly select the branch labeled with a literal value equal to the selector value.

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

In the example above, the selector expression refers to a parameter `width` of a `uint8` type (see
[Parameterized Types](#parameterized-types)).

A given branch of a choice may have more than one case label. In this case, the branch is selected when
the selector value is equal to any of the case label values. A choice type may have a default branch which is
selected when no case label matches the selector value. The decoder will throw an exception when there is no
default branch and the selector does not match any case label. Any branch, including the default branch, may be
empty, with a terminating semicolon directly following the label. It is good practice to insert a comment in
this case. When the selector expression has an enumeration type, the enumeration type prefix may be omitted
from the case label literals.

**Example**
```
choice AreaAttributes(AreaType type) on type
{
    case AreaType.COUNTRY: // prefix "AreaType." is optional
    case STATE:
    case CITY:
        RegionAttributes regionAttr;
    case MAP:
        /* empty */ ;
    case ROAD:
        RoadAttributes roadAttr;
    default:
        DefaultAttributes defaultAttr;
};
```

### Union Types

An union type corresponds to exactly one of its members, which are also called branches. Union type is
an automatic choice type, which automatically handles the selector according to the last used branch
(i.e. last called setter). The selector is stored in a bitstream automatically before the union branch
data to allow selection of the proper branch during parsing.

When a specific handling of selector is needed (e.g. when the selector is already known in a parent),
it might be better to use choice types instead of unions.

**Example**
```
union SimpleUnion
{
    uint8   value8;
    uint16  value16;
};
```

In this example, the union `SimpleUnion` has two branches `value8` and `value16`. The syntax of a member
definition is the same as in structure types.

### Extended Members

Any **top-level** structure type can have extended members. Extended members can be only at the end of the
structure and allows to extend schema while preserves ability to read data created by older schemas
(backward compatibility).

**Example**
```
struct TopLevelBlob
{
    uint32 data[];
    extend ExtendedData extendedData;
};

struct ExtendedData
{
    varint additionalData[];
};
```

Extended member is defined by `extend` keyword which must be before the field definition. Extended member does
not have any additional footprint in the bitstream. The only difference is that generators know that the parsed
bitstream can terminate before any of the extended field.

Multiple extended fields are allowed but once used, all other consecutive fields must also be marked as
extended. It's up to user's responsibility that extended fields are not placed before fields which were
extended in previous versions.

**Example**
```
struct TopLevelBlob
{
    uint32 data[];                    // last field in version 1
    extend ExtendedData extendedData; // added in version 2
    extend bool additionalFlag;       // added in version 3
};

struct ExtendedData
{
    varint additionalData[];
};
```

> Since `2.12.0`

### Constraints

A constraint may be specified for any member of a compound type. After decoding a member with a constraint,
the decoder checks the constraint and reports an error if the constraint is not satisfied.

**Example**
```
struct GraphicControlExtension
{
    uint8 byteCount       : byteCount == 4;
    uint8 blockTerminator : blockTerminator == 0;
};

choice ChoiceConstraints(bool selector) on selector
{
    case true:
        uint8  value8  : value8 != 0;

    case false:
        uint16 value16 : value16 > 255;
};
```

Because constraint is a boolean expression, the following example is valid:

**Example**
```
struct TestStructure
{
    bool    isValueValid;
    uint16  value : isValueValid;
};
```

### Optional Members

A structure type may have optional members.

**Example**
```
struct ItemCount
{
    uint8   count8;
    uint16  count16 if count8 == 0xFF;
};
```

An optional member has an `if` clause with a boolean expression. The member will be decoded only if
the expression evaluates to true at run-time.

Because optional member has an `if` clause with a boolean expression, the following example is valid:

**Example**
```
struct TestStructure
{
    bool    hasValue;
    uint16  value if hasValue;
};
```

An optional member can be defined without `if` clause. In this case, a keyword `optional` must be used before
the field definition:

**Example**
```
struct Container
{
    int32           nonOptionalInt;
    optional int32  autoOptionalInt;
};
```

An optional member defined by the keyword `optional` will be decoded only if the member has been set.

### Functions

A compound type may contain functions:

**Example**
```
struct ItemCount
{
    uint8   count8;
    uint16  count16 if count8 == 0xFF;

    function uint16 getValue()
    {
        return (count8 == 0xFF) ? count16 : count8;
    }
};
```

The return type of a function has to be a standard integer or compound type, and the function parameter list
must be empty. The function body may contain nothing but a return statement with an expression matching
the return type.

Functions are intended to provide no more than simple expression semantics. There are no plans to add more
advanced type conversion or even procedural logic to zserio.

### Default Values

A structure type may contain default values for fields which are not arrays or compound types:

**Example**
```
enum uint8 BasicColor
{
    BLACK,
    WHITE,
    RED
};

struct StructureDefaultValues
{
    bool        boolValue = true;
    bit:4       bit4Value = 0x0F if boolValue == true;
    int16       int16Value = 0x0BEE;
    float16     float16Value = 1.23f;
    float32     float32Value = 1.234f;
    float64     float64Value = 1.2345;
    string      stringValue = "string";
    BasicColor  enumValue = BasicColor.BLACK;
};
```

The default values are used by the encoder if the  value of corresponding field has not been set. So, there will
be always a value written to the stream. This is in contrast to other serialization mechanisms where
the decoders would generate the default value. Reason for this is the missing "wire format" in zserio which
would add additional information in the stream for identifying fields and whether they are set or not.

[top](#language-guide)

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

An array type may have an implicit length indicated by an `implicit` keyword and an empty pair of brackets. Implicit arrays must be at the end of the BLOB. It might also be the complete BLOB.

For encoding details see [encoding guide](ZserioEncodingGuide.md#implicit-array-types).

**Example**
```
struct ImplicitArray
{
    implicit Element list[];
};
```

The length of the `list` array can be referenced as `lengthof(list)`, see
[lengthof Operator](#lengthof-operator).

> Deprecated since `2.4.0`

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

The length of the `list` array can be referenced as `lengthof(list)`, see
[lengthof Operator](#lengthof-operator).

Auto length arrays might be particularly useful if variable array length expression is a single field:

**Example**
```
struct AutoArrayCandidate
{
    uint32  numElements;
    Element list[numElements];
};
```

### Packed Arrays

An array type can be packed indicated by a `packed` keyword. The only supported packing algorithm is
delta compression which is applied for packed arrays of:

- all integer types (uint32, varuint, ...)
- enumeration types
- bitmask types
- compound types which contain integer types, enumeration, bitmask types or inner packable arrays
- all union types (even if do not contain any packable fields because selector is always packable integer type)

**Example**
```
struct PackedArrays
{
    packed uint8   header[256];
    packed int16   items[];
    uint16         numItems;
    packed Element elements[numItems];
};
```

> Note: Packed implicit length arrays and packed arrays which are using as offsets are forbidden.

> Note: Fields which are used as offsets will be never packed because it would break offsets feature.

> Since `2.4.0`

[top](#language-guide)

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

> Fields which are used as offsets cannot be used in [expressions](#expressions).

> Each field can be used only once as an offset.

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

> Fields which are used as indexed offsets cannot be used in [expressions](#expressions).

> Each field can be used only once as an indexed offset.

[top](#language-guide)

## Expressions

The semantics of expression and the precedence rules for operators is the same as in Java, except where stated
otherwise. Zserio has a number of special operators which will be explained in detail below.

The following Java operators have no counterpart in zserio: `++`, `--`, `>>>` and `instanceof`.

### Unary Operators

#### Boolean Negation

The negation operator `!` is defined for boolean expressions.

#### Integer Operators

For integer expressions, there are `+` (unary plus) and `-` (unary minus).

#### Bitwise Complement

The bitwise complement `~` is defined for integer and bitmask expressions.

#### lengthof Operator

The `lengthof` operator may be applied to array members, bytes types or strings and returns the actual length
(i.e. number of elements of an array). Thus, given `int32 a[5]`, the expression `lengthof` a evaluates to `5`.
This is not particularly useful for fixed or variable length arrays, but it is the only way to refer to
the length of an auto length array.

> The `lengthof` function returns the number of bytes in a string, encoded using UTF-8.
> For example, the length of the string "€" is `3`, because it is encoded as three bytes in UTF-8.

**Example**
```
struct LengthOfOperator
{
    uint8 autoArray[];

    function uint32 getLengthOfAutoArray()
    {
        return lengthof(autoArray);
    }
};
```

#### valueof Operator

The `valueof` operator may be applied to an enumeration or bitmask type and returns its actual value as
an integer value.

**Example**
```
struct ValueOfOperator
{
    Color  color;

    function uint8 getValueOfColor()
    {
        return valueof(color);
    }
};

enum uint8 Color
{
    WHITE = 1,
    BLACK = 2
};
```

#### numbits Operator

The `numbits(numValues)` operator is defined for unsigned integers as minimum number of bits required to encode
`numValues` different values. The returned number is of type `uint8`. The `numbits` operator returns `0` if
applied to value `0`.

The following table shows the results of the `numbits` operator applied some common values:

```
numbits(0) = 0
numbits(1) = 1
numbits(2) = 1
numbits(3) = 2
numbits(4) = 2
numbits(8) = 3
numbits(16) = 4
```

**Example**
```
struct NumBitsOperator
{
    uint8   value8;

    function uint8 getNumBits8()
    {
        return numbits(value8);
    }
};
```

### Binary Operators

#### Arithmetic Operators

The integer arithmetic operations include `+` (addition), `-` (subtraction), `*` (multiplication),
`/` (division), `%` (modulo). In addition, zserio also supports shift operators `<<` and `>>`.

#### Relational Operators

The following relational operators for integer expressions are supported: `==` (equal to),
`!=` (not equal to), `<` (less than), `<=` (less than or equal), `>` (greater than),
`>=` (greater than or equal).

The equality operators `==` and `!=` may be applied to any type.

#### Boolean Operators

The boolean operators `&&` (and) and `||` (or) may be applied to boolean expressions.

#### Bit Operators

The bit operators `&` (bitwise and), `|` (bitwise or), `^` (bitwise exclusive or) may be applied to integer
types.

#### Postfix Operators

The postfix operators include `[]` (array index), `()` (instantiation with argument list or function call) and
`.` (member access).

#### isset Operator

Operator `isset` accepts bitmask and bitmask value. It returns true if bitmask contains given bitmask
value, i.e. if bitwise and (`&`) of bitmask and bitmask value is equal to bitmask value.

**Example**
```
bitmask uint8 TestBitmask
{
    INT,
    STRING
};

struct IsSetOperator
{
    TestBitmask testBitmask;

    function bool hasInt()
    {
        return isset(testBitmask, INT);
    }

    function bool hasString()
    {
        return isset(testBitmask, TestBitmask.STRING);
    }

    function bool hasBoth()
    {
        return isset(testBitmask, INT | STRING);
    }
};
```

The `isset` operator in previous example can be rewritten using bitwise and operator with the completely
same behavior:

```
struct IsSetOperator
{
    TestBitmask testBitmask;

    function bool hasInt()
    {
        return ((testBitmask & TestBitmask.INT) == TestBitmask.INT);
    }

    function bool hasString()
    {
        return ((testBitmask & TestBitmask.STRING) == TestBitmask.STRING);
    }

    function bool hasBoth()
    {
        return ((testBitmask & (TestBitmask.INT | TestBitmask.STRING)) ==
                (TestBitmask.INT | TestBitmask.STRING));
    }
};
```

> Note that the bitmask type used in the `isset` operator can be deduced, so it's allowed to discard
the type name and a dot, e.g. `isset(testBitmask, INT)` instead of
`isset(testBitmask, TestBitmask.INT)`.

> Since `2.8.0`

### Ternary Operators

A conditional expression `booleanExpr ? expr1 : expr2` has the value of `expr1` when `booleanExpr` is true.
Otherwise, it has the value of `expr2`.

### Operator Precedence

In the following list, operators are grouped by precedence in descending order. Operators on the  line
have the highest precedence and are evaluated first. All operators on the same line have the same precedence
and are evaluated left to right, except ternary operator which are evaluated right to left.

- `()`, `[]`, `.`
- `lengthof` `valueof` `numbits`
- unary `+` `-` `~` `!`
- `*` `/` `%`
- `+` `-`
- `<<` `>>`
- `<` `>` `<=` `>=`
- `==` `!=`
- `&`
- `^`
- `|`
- `&&`
- `||`
- `?` `:`

[top](#language-guide)

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

[top](#language-guide)

## Parameterized Types

The definition of a compound type may be augmented with a parameter list, similar to a parameter list in a Java
method declaration. Each item of the parameter list has a type and a name. Within the body of the compound type
definition, parameter names may be used as expressions of the corresponding type.

To use a parameterized type as a field type in another compound type, the parameterized type must be
instantiated with an argument list matching the types of the parameter list.

**Example**
```
struct Header
{
    uint32 version;
    uint16 numItems;
};

struct Message
{
    Header       header;
    Item(header) items[header.numItems];
};

struct Item(Header header)
{
    uint16 param;
    uint32 ExtraParam if header.version >= 10;
};

```

When the element type of an array is parameterized, a special notation can be used to pass different arguments
to each element of the array:

**Example**
```
struct Database
{
    uint16                  numBlocks;
    BlockHeader             headers[numBlocks];
    Block(headers[@index])  blocks[numBlocks];
};

struct BlockHeader
{
    uint16 numItems;
    uint32 offset;
};

struct Block(BlockHeader header)
{
    header.offset:
    int64 items[header.numItems];
};
```

The `@index` denotes the current index of the `blocks` array. The use of this expression in the argument list
for the `Block` reference indicates that the `i`-th element of the `blocks` array is of type `Block`
instantiated with the `i`-th header `headers[i]`.

[top](#language-guide)

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

[top](#language-guide)

## Comments

There are two types of comments in zserio language:

- Standard Comments
- Documentation Comments

Standard comments are intended only as the comments of zserio schema and are filtered out during compilation.
Thus, generators have no access to these comments.

Documentation comments are not filtered out during compilation and they can be used by extensions
(e.g. for generation of zserio schema documentation or for documentation of generated code).
These comments might be associated to the subsequent type or field definition.

Documentation comments are further divided to two types:

- Classic Comments
- Markdown Comments

### Standard Comments

Zserio supports the standard comment syntax of Java or C++. Single line comments start with `//` and extend
to the end of the line. A comments starting with `/*` is terminated by the next occurrence of `*/`, which may
or may not be on the same line.

**Example**
```
// This is a single-line comment.

/* This is an example
    of a multi-line comment
    spanning three lines. */
```

### Classic Documentation Comments

To support generation of documentation, multi-line comments starting with `/**` are treated as special
classic documentation comments. The idea and syntax is borrowed from Java(doc). A classic documentation comment
is associated to the following type or field definition. The classic documentation comment and the corresponding
definition may only be separated by whitespace.

**Example**

```
/**
* Traffic flow on roads.
*/
enum bit:2 Direction
{
    /** No traffic flow allowed. */
    NONE,

    /** Traffic allowed from start to end node. */
    POSITIVE,

    /** Traffic allowed from end to start node. */
    NEGATIVE,

    /** Traffic allowed in both directions. */
    BOTH
};
```

The classic documentation comments can contain special tags which is shown by the following example:

```
/**
* The tile contains a number of different elements
* grouped by feature classes, e.g. intersections, roads and
* so on...
*
* The presence of these members is indicated by the content
* mask in the header (please have a look at the
* following @see "documentation" headerDefinition page).
*
* @see headerDefinition
*
* @param level level number
* @param width width for the current tile
*
* @todo Update this comment.
*
* @deprecated
*/
```
The content of a classic documentation comment, excluding its delimiters, is parsed line by line. Each line is
stripped of leading whitespace, a sequence of asterisks (`*`), and more whitespace, if present. After stripping,
a comment is composed of one or more paragraphs, followed by zero or more tag blocks. Paragraphs are separated
by blank lines. The text in paragraphs can contain HTML formatting tags like `<ul>`, `<li>` or `</br>`
directly.

A line starting with whitespace and a keyword preceded by an at-sign (`@`) is the beginning of a tag.

The following sections describe all supported tags in the classic documentation comments in detail.

#### See Tag

The `see` tag defines the link in generated documentation and has the following format:

```
@see "TEXT_ALIAS" TYPE.FIELD
```

The `TEXT_ALIAS` is text which will be shown in the generated documentation instead of the reference
`TYPE.FIELD`. This alias text is optional and can be omitted.

The `TYPE.FIELD` must be the valid reference to the field of the zserio type. The `FIELD` definition is
optional and can be omitted.

The `see` tag is the only tag which does not have to defined at the beginning of the line and can be embedded
directly in the comment text.

**Example**

```
/**
* Please see @see "black color" ColorEnumerationType.BLACK definition for more description.
*/
```

#### Param Tag

The `param` tag is used for documenting the arguments of a parameterized type. This tag has the following
format:

```
@param PARAM_NAME PARAM_DESCRIPTION
```

The `PARAM_NAME` defines the parameter name.

The `PARAM_DESCRIPTION` contains the parameter description. This description can be defined on multiple lines.

**Example**

```
/**
 * This type takes two arguments.
 *
 * @param arg1 The first argument.
 * @param arg2 The second argument.
 */
struct ParamType(Foo arg1, Bar arg2)
{
...
};
```

#### Todo Tag

The `todo` tag is used for documenting the action which should be done in the future. This tag has
the following format:

```
@todo ACTION_DESCRIPTION
```

The `ACTION_DESCRIPTION` contains arbitrary text. This text can be defined on multiple lines.

**Example**

```
/**
 * The text of the comment.
 *
 * @todo Don't forget to update this comment!
 */
```

#### Deprecated Tag

This tag assigns the documented zserio type as deprecated which means that this type is going to be invalid in
future versions of the schema. It has the following format:

```
@deprecated
```

### Markdown Documentation Comments

To support generation of documentation, multi-line comments starting with `/*!` are treated as special
markdown documentation comments. It's recommended to terminate markdown comments with `!*/` and Zserio
fires a warning when the comment is terminated differently.

**Example**

```
/*!

# Title for this section

The tile contains a number of different elements grouped by feature classes, e.g. intersections,
roads and so on...

This module defined the following structure:

Name        | Description
------------| ----------------------------------------------
Structure1  | This describes Structure1 in detail.
Structure2  | This describes Structure2 in detail.

!*/
```

If whole markdown documentation comment is indented according to the starting `/*!` which is single at separate
line, it is rendered without indentation.

**Example**

```
    /*!

    # Title for this section

    The tile contains a number of different elements grouped by feature classes, e.g. intersections,
    roads and so on...

    This module defined the following structure:

    Name        | Description
    ------------| ----------------------------------------------
    Structure1  | This describes Structure1 in detail.
    Structure2  | This describes Structure2 in detail.

    !*/
```

> Since `2.1.0`

[top](#language-guide)

## Rules

Zserio also provides a way how to organize semantic restrictions for the schema which cannot be easily defined
using constraints. Rules are defined within rule groups and are described using
[documentation comments](#comments). Each rule have it's own identifier which is standard Zserio identifier
extended by `-`, `:` and `.` characters. The rule ID must be unique globally (using case insensitive
comparison) within all Zserio packages.

**Example**
```
rule_group SchemaRules
{
    /*!
    Description of the semantic rule.

    ![Rule 01](../assets/rule-01-picture.png)
    !*/
    rule "rule-01";

    /*!
    Another rule.
    !*/
    rule "rule-02";
};
```

> Since `2.4.0`

[top](#language-guide)

## Packages and Imports

Complex zserio specifications should be split into multiple packages stored in separate source files. Every
user-defined type belongs to a unique package. For backward compatibility, there is an unnamed default package
used for files without an explicit package declaration. It is strongly recommended to use a package declaration
in each zserio source file.

A package provides a lexical scope for types. Type names must be unique within a package, but a given type name
may be defined in more than one package. If a type named `Coordinate` is defined in package `com.acme.foo`, the
type can be globally identified by its fully qualified name `com.acme.foo.Coordinate`, which is obtained by
prefixing the type name with the name of the defining package, joined by a dot. Another package
`com.acme.bar` may also define a type named `Coordinate`, having the fully qualified name
`com.acme.bar.Coordinate`.

By default, types from other packages are not visible in the current package, unless they are imported
explicitly. The package and import syntax and semantics follow the Java example.

**Example**

```
package map;

import common.geometry.*;
import common.featuretypes.*;
```

Import declarations only have any effect when there is a reference to a type or symbol not defined in the
current package. If package map defines its own `Coordinate` type, any reference to that within package `map`
will be resolved to the local type `map.Coordinate`, even when one or more of the imported packages also define
a type named `Coordinate`.

On the other hand, if package `map` references a `Coordinate` type but does not define it, the import
declarations are used to resolve that type in one of the imported packages. In that case, the referenced type
must be matched by exactly one of the imported packages. It is obviously a semantic error if the type name is
defined in none of the packages. It is also an error if the type name is defined in two or more of the imported
packages. The order of the import declarations does not matter.

It is always possible to use the fully qualified name of a type, e.g. `com.acme.bar.Coordinate`. This makes it
possible to import a type with the same name (e.g. `Coordinate`) from more than one package or to import a type
with the same name as a type defined locally.

Individual types or symbols can be imported using their fully qualified name:

```
import common.geometry.Geometry;
```

This single import has precedence over any wildcard import. It prevents an ambiguity with
`common.featuretypes.Geometry`. It is possible to import the same type from different packages but then
each usage of such type must be fully qualified. Using the unqualified name in this situation results in
a compilation error as the type is ambiguous.

### Packages and Files

Package and file names are closely related. Each package must be located in a separate file. The above example
declares a package `map` stored in a source file `map.zs`. The import declarations direct the parser to locate
and parse source files `common/geometry.zs` and `common/featuretypes.zs`.

Imported files may again contain import declarations. Cyclic import relations between packages are supported
but should be avoided. The zserio parser takes care to parse each source file just once.

[top](#language-guide)

## Templates

### Motivation

Although basic zserio language features provides a strong tool for binary data modeling, there might be
situations when reducing duplications using generic programming would bring high benefits for the users.

The basic stone of generic programming in zserio are templates that allow zserio compound types (structure
types, choice types, union types) to operate with generic types. Such generic types are called template
parameters and they will be specified later during template instantiation as template arguments.

Because zserio must check correctness of all template instantiations and because zserio should support
generators to almost any kind of programming language (even to language which does not support templates),
the template instantiations must be resolved during zserio compilation.

Zserio templates got inspiration from C++ class templates concept and borrowed syntax from Java generics.

### Compound Type Templates

The compound type template is defined by normal compound type declaration together with template parameters
denoted by signs `<>`:

**Example**

```
struct Field<T>
{
    T value;
};
```

This example defines structure type template with one template parameter called `T`. The template
parameter can be used anywhere as a generic type within lexical scope of the structure. This generic type will
be specified during template instantiation as follows:

**Example**

```
struct StructTemplatedField
{
    Field<uint32> uint32Field;
};
```

This example instantiates the template `Field` using argument `uint32`. From the zserio language point of view,
the previous structure type template example is actually the same as the following "no-generics-programming"
example:

**Example**

```
struct Field_uint32
{
    uint32 value;
};

struct StructTemplatedField
{
    Field_uint32 uint32Field;
};
```

### Parameterized Type Templates

Parameterized types templates are supported with the very similar syntax. The parameterized types parameters
can be template instantiations as well. The following example shows intuitive syntax for parameterized type
templates:

**Example**

```
struct ParamHolder<T>
{
    T param;
};

struct Parameterized<T>(ParamHolder<T> paramHolder)
{
    string  description;
paramHolder.param:
    uint32  id;
};

struct StructTemplatedTypeArgument
{
    ParamHolder<uint32>                 paramHolder;
    Parameterized<uint32>(paramHolder)  parameterized;
};
```

### Templated Template Arguments

Templated template arguments are supported sa well. In another words, template arguments can be another
template instantiations. The following example shows syntax for templated template arguments:

**Example**

```
struct Field<T>
{
    T value;
};

struct Compound<T>
{
    T value;
};

struct StructTemplatedTemplateArgument
{
    Field<Compound<uint32>> compoundField;
};
```

### Template Subtypes

Subtypes can be used for aliasing template types similarly to normal zserio types. The following example shows
the usage:

**Example**

```
struct TestStructure<T>
{
    T value;
};

subtype TestStructure<uint32> TestStructureSubtype;
```

### Template Instantiations

The zserio template instantiations are generated by default in the package where the corresponding template is
declared. This ensures that only one template instantiation is generated if it is used multiple times from
different packages. This default behavior is necessary because template instantiations can be used as
parameters across different packages.

However, there might be the situations where forcing package of the template instantiation would be beneficial.
Because of that, a new keyword `instantiate` has been added to the zserio language.

**Example**

```
package template_declaration;

struct TestStructure<T>
{
    T value;
};

package template_instantiation;

import template_declaration.*;

instantiate TestStructure<uint32> TestStructure32;
```

The `instantiate` command from previous example means

* that the `TestStructure<uint32>` instantiation will be generated in the package `template_instantiation`
  instead of `template_declaration` package and
* that the `TestStructure<uint32>` instantiation will be named as `TestStructure32` and
* that new zserio type `TestStructure32` will be defined in the package `template_instantiation`.

The instantiate types can be imported as other normal types. Thus, all imported instantiate types are visible
in the package.

It's forbidden to have `instantiate` command (specified or imported) to the same template instantiation
multiple times in the same package.

However, it's allowed to do so in the different packages (which are not imported). In this case, the same
template will be instantiated as different type in the other package. Because of that, the `instantiate`
command should be considered very carefully and used only if it's really necessary.

> Since `1.4.0`

[top](#language-guide)

## Service Types

Binary data streams defined by zserio are also good candidates to be used in RPC systems.
Zserio introduces generic services directly in the language. A service type contains definitions of
service methods.

**Example**
```
struct UserId
{
    uint32 id;
};

struct User
{
    uint32 id;
    string name;
    string surname;
    string phoneNumber;
};

service Users
{
    User getUser(UserId);
};
```

A service method must have a single response and single request type. When no response or
request type is needed, an empty structure can be used. Currently only simple unary calls are supported
by zserio.

### Request and Response Types

The types must be non-parameterized compound types or `bytes`. Parameterized types are not allowed since
the parameters are not stored in the bit stream. However parameterized types can be still used in the response
or request types' subtree. The `bytes` type can be used when raw data format is needed - e.g. when the response
is an image.

> Since `2.0.0`

[top](#language-guide)

## Pubsub Types

Aside of services, zserio also supports publish-subscribe messaging pattern. The pubsub type defines a Pub/Sub
client.

**Example**
```
pubsub WeatherProvider
{
    publish topic("weather/warnings") WeatherWarning warnings;
};

pubsub WeatherClient
{
    subscribe topic("weather/warnings") WeatherWarning weatherWarnings;
};

struct WeatherWarning
{
    string warningMessage;
};
```

The pubsub defines messages which can be either published or subscribed (or both). The example above defines a
`WeatherProvider` pubsub type which defines a single message `warnings`, which is published under the topic
named "weather/warnings" and the type of the message is `WeatherWarnings` structure. Then it defines
a `WeatherClient` pubsub type which has a single subscription `weatherWarnings` for messages published under
topic "weather/warnings". The client expects that the type of messages arriving to `weatherWarnings`
subscription (i.e. published under the defined topic) are of the type `WeatherWarning`.

Pubsub type can define a message in three ways:
   * `topic("topic/definition") Type message` to both publish and subscribe a message,
   * `publish topic("topic/definition") Type message` to publish a message,
   * `subscribe topic("topic/definition") Type message` to subscribe a message.

### Topic Definition

In the Pub/Sub pattern, it is common to use wildcards for topic definitions in subscriptions. The wildcards
format depends on a particular implementation. Zserio only provides a generic definition of Pub/Sub clients
and doesn't manipulate with the topic definition string. It therefore depends on the particular Pub/Sub backend
whether the wildcards are supported and how. See the
[MQTT standard](https://docs.oasis-open.org/mqtt/mqtt/v5.0/os/mqtt-v5.0-os.html#_Topic_Names_and)
as an example of a concrete Pub/Sub pattern specification.

### Message Types

Message type must be a non-parameterized compound type or `bytes`. Parameterized types are not allowed since
the parameters are not stored in the bit stream. However parameterized types can be still used in the types'
subtree. The `bytes` type can be used when raw data format is needed - e.g. when the message is an image.

> Since `2.0.0`

[top](#language-guide)

## SQLite Extension

### Motivation

With its basic language features presented in the previous sections, zserio provides a rich language for
modeling binary data streams, which are intended to be parsed sequentially. Direct access to members in the
stream is usually not possible, except for specifying the offset of a given member. Navigation between
semantically related members at different positions in the stream cannot be expressed at the stream level.
Member insertions or updates are not supported.

All in all, the stream model is not an adequate approach for updatable databases in the gigabyte size range
with lots of internal cross-references where fast access to individual members is required. In a desktop or
server environment, it would be a natural approach to model such a database as a relational database using SQL.
However, in an embedded environment with limited storage space and processing resources, a full-fledged
relational schema is too heavy-weight. To have the best of both worlds, i.e. compact storage on the one hand
and direct member access including updates on the other hand, one can adopt a hybrid data model: In this hybrid
model, the high-level access structures are strictly relational, but most of the low-level data are stored in
binary large objects (BLOBs), where the internal structure of each BLOB is modeled with zserio.

For example, we can model a digital map database as a collection of tiles resulting from a rectangular grid
where the tiles are numbered row-wise. The database has a rather trivial schema:

```
CREATE TABLE europe (tileNum INTEGER PRIMARY KEY, tile BLOB NOT NULL);
```

Accessing or updating any given tile can simply be delegated to the relational DBMS, in case of this zserio
extension, [*SQLite*](https://www.sqlite.org).

Assuming that the tile BLOBs have a reasonable size, each tile can be decoded on the fly to access
the individual members within the tile. For seamless modelling of this hybrid approach, we decided to add
relational extensions for SQLite to zserio. Some SQL concepts have been translated to zserio, others are
transparent to zserio and can be embedded as literal strings to be passed to the SQLite engine.

### SQLite Tables

#### SQLite Table Types and Instances

An SQL table type is a special case of a compound type, where the members of the type correspond to the
columns of a relational table.

In zserio it is possible to express the above example as follows:

**Example**

```
sql_table GeoMap
{
    int32   tileId sql "PRIMARY KEY NOT NULL";
    Tile    tile sql "NOT NULL";
};

GeoMap europe;
GeoMap america;
```

It is important to note that the `GeoMap` is a table type and not a table. A table is defined by the instance
`europe` of type `GeoMap`. Table types have no direct equivalent in SQL. They can be used to create tables
with identical structure and column names. Each instance of an `sql_table` type in zserio translates to an
SQLite SQL table where the table name in the SQL schema is equal to the instance name in zserio. A member
definition may include an SQL constraint introduced by the keyword `sql`, followed by a literal string which is
then passed to the SQLite engine.

Thus, the zserio instance `america` results in the following SQL table:

```
CREATE TABLE america (tileNum INT PRIMARY KEY NOT NULL, tile BLOB NOT NULL);
```

It is also possible to use the zserio keyword `sql` directly inside the table definition. The main use for this
syntax is to define a primary key spanning multiple fields.

**Example**

```
sql_table BusinessLocationTable
{
    BusinessId  businessId sql "NOT NULL";
    CategoryId  catId sql "NOT NULL";
    Position    position sql "UNIQUE";
    int8        hasIcon;

    sql "PRIMARY KEY(businessId, catId)";
};
```

SQL table types can be templated using the same syntax as other zserio compound types, see
[templates](#templates).

For the mapping of zserio types to SQL types, refer to [SQL Types Mapping](#sqlite-types-mapping).

### SQLite Virtual Tables

Virtual tables in zserio are an extension to the `sql_table`. The following paragraph gives a definition
of a virtual table from the SQLite website:

_A virtual table is an interface to an external storage or computation engine that appears
to be a table but does not actually store information in the database file. In general, you
can do anything with a virtual table that can be done with an ordinary table, except that
you cannot create indices or triggers on a virtual table. Some virtual table implementations
might impose additional restrictions. For example, many virtual tables are read-only._

The syntax is modeled as an extension to the `sql_table`, where an optional module can be specified:

```
sql_table <tablename> using <modulename>
```

The following example creates a virtual table using SQLite's FTS5 module:

```
sql_table Pages using fts5
{
    string title;
    string body;
};
```

The following example creates a virtual table using the RTREE module:

```
sql_table TestTable using rtree
{
    int32 id;
    int32 minX;
    int32 maxX;
    int32 minY;
    int32 maxY;
};
```

#### SQLite Virtual Columns

When Virtual tables are used in zserio some columns are automatically defined by the module used after the
`using` keyword. The keyword `sql_virtual` allows to add generated columns to documentation (HTML) so it will
be possible to find what type the column belongs or to what features it references. There will be no code
generated for these columns.

The table generation code will not contain these columns. This keyword is not limited to virtual tables only
but it makes the most sense to just use it in virtual tables.

The syntax is modeled as an extension to the `sql_table` column definition:

```
sql_virtual <type name> <column name>;
```

The following example creates a virtual table using the SQLite FTS5 module. It will omit to create the content
column but allow to read or write to it.

**Example**

```
sql_table Pages using fts5
{
    sql_virtual string content;
};
```

### Explicit Parameters

When a  `sql_table` member is an instance of a parameterized type, the application may want to derive
the parameter values from the context (e.g. other table columns), which is not available to the zserio decoder.
In this case, the type arguments shall be marked with the keyword `explicit` to indicate that these values will
be set explicitly be the application. Otherwise, the decoder would complain about not being able to evaluate
the type arguments.

**Example**
```
struct Tile(uint8 level, uint8 width)
{
    ...
};

sql_table TileTable
{
    uint32 tileId;
    uint32 version;
    Tile(explicit level, explicit width) tile;
};
```

### SQLite WITHOUT ROWID Tables

To support the `WITHOUT ROWID` optimization in SQLite, the `sql_without_rowid` keyword is used in zserio.
A `sql_without_rowid` keyword is always a part of the `sql_table` type:

**Example**

```
sql_table WithoutRowIdTable
{
    string  word sql "PRIMARY KEY NOT NULL";
    uint32  count;

    sql_without_rowid;
};
```

A `sql_without_rowid` keyword must be defined after all possible fields and SQL constraints inside the SQL
table.

A `sql_without_rowid` keyword specified in `sql_table` type causes to create a corresponding SQLite table
with omitting the special "rowid" column. This may bring space and performance advantages.

Creating a SQL table using the `WITHOUT ROWID` optimization without specifying the primary key is considered
a compilation error.

### SQLite Databases

Since an SQL table is always contained in an SQL database, we introduce a `sql_database` type in zserio to
model databases. `sql_table` instances may only be created as members of an `sql_database`.

**Example**

```
sql_table GeoMap
{
    // see above
};

sql_database TheWorld
{
    GeoMap europe;
    GeoMap america;
    ...
    ..
};
```

### SQLite Types Mapping

Zserio type                              | SQLite type
---------------------------------------- | ------------
uint8, uint16, uint32, uint64            | `INTEGER`
int8, int16, int32, int64                | `INTEGER`
bit:n (n < 64)                           | `INTEGER`
int:n (n <= 64)                          | `INTEGER`
float16, float32, float64                | `REAL`
varuint16, varuint32, varuint64, varuint | `INTEGER`
varint16, varint32, varint64, varint     | `INTEGER`
bool                                     | `INTEGER`
string                                   | `TEXT`
enum                                     | `INTEGER`
bitmask                                  | `INTEGER`
struct                                   | `BLOB`
choice                                   | `BLOB`
union                                    | `BLOB`

[top](#language-guide)

## Annotations

Zserio annotations are syntactic metadata that can be added to Zserio sources. Annotations usually have no
direct effect but can be used by generators to modify generated code appropriately. Annotations starts with `@`
and are placed before a language element.

### Deprecated Annotation

Annotation `@deprecated` denotes that the language element is being deprecated and should not be used anymore.
Generators are allowed to generate a code which will fire a warning when an appropriate generated element is
used by an application.

```
enum uint8 Traffic
{
    NONE = 0,
    @deprecated HEAVY,
    LIGHT
    MID
};
```

> Since `2.12.0` (supported only by enumeration items).

### Removed Annotation

Annotation `@removed` denotes that the language element was removed from the schema and is left there only for
the sake of backward compatibility. Generators are allowed to omit (or hide) an appropriate generated element
in a way that it will be still possible to read old BLOBS, while it should be forbidden to use such elements
during serialization.

```
enum uint8 Traffic
{
    NONE = 0,
    @removed HEAVY,
    LIGHT
    MID
};
```

> Since `2.12.0` (supported only by enumeration items).

## Compiler Directives

### Compatibility Check

Although zserio tries to stay compatible with older versions, it may happen that it's necessary to
break the schema compatibility in order to improve the language or some feature. For these cases zserio provides
special language directive `zserio_compatibility_version` which takes the requested version string as an
argument. The directive must be first command (except comments) in the source file.

```
zserio_compatibility_version("2.4.2");

package compatibility;
```

When the `zserio_compatibility_version` directive is specified, zserio will check that all used features are
still available and fire an error in case that the current version of language is not compatible anymore.

Furthermore, the extensions can apply additional compatibility checks
(e.g. that the generated code still produce binary compatible BLOBs).

> Since `2.5.0`

## Background & History

Zserio is based on the work of
[*Godmar Back*](http://people.cs.vt.edu/~gback/papers/gback-datascript-gpce2002.pdf) and was called DataScript
at that time.

His work was taken up by the members of the
[*Navigation Data Standard Association*](https://www.nds-association.org) (an industry consortium of companies
from the digital maps business) and had been developed internally until 2018.

While Back's reference implementation provided a great start, some language extensions were added to better
suit the requirements of the NDS members.

As a major addition to the DataScript language, a relational extension had been introduced, which permits
the definition of hybrid data models, where the high-level access structures are implemented by relational
tables and indices, whereas the bulk data are stored in single columns as BLOBs with a format defined in
DataScript, hence then named Relational DataScript. Since the Relational DataScript was used on top of
a SQLite database also some SQLite specific language elements had been added during that time.

By 2018 the NDS consortium decided to open source the work done since they forked off from Godmar Back's
reference implementation.

Since the name DataScript already was used by other projects for different purposes and the fact that it has
never really been a script language anyhow, a new name needed to be found: zserio. An acronym for zero
serialization overhead and pronounced with a silent "s".

[top](#language-guide)

## License & Copyright

The original reference implementation from which we derived zserio was using the BSD 3-clause license. This
is the reason why all of the work described above is also released under BSD-3 license (see LICENSE.md file
in root directory of this repo).

Copyright remains at Godmar Back and Navigation Data Standard e.V.

[top](#language-guide)
