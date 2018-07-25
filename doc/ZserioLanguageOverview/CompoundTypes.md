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

This type has a total length of 16 bits or 2 bytes. As a bit stream, bits 0-3 correspond to member `a`,
bits 4-11 represent an unsigned integer `b`, followed by member `c` in bits 12-15. Note that member `b`
overlaps a byte boundary, when the entire type is byte aligned. But `MyStructure` may also be embedded into
another type where it may not be byte-aligned.

### Choice Types

A choice type depends on a _selector expression_ following the `on` keyword. Each branch of the choice type is
preceded by one or more case labels with a literal value. After evaluating the selector expression, the decoder
will directly select the branch labeled with a literal value equal to the selector value.

**Example**
```
choice VarCoordXY(uint8 width) on width
{
    case  8: CoordXY8  coord8;
    case 16: CoordXY16 coord16;
    case 24: CoordXY24 coord24;
    case 32: CoordXY32 coord32;
};
```

In the example above, the selector expression refers to a parameter `width` of a `uint8` type (see
[Parametrized Types](ParametrizedTypes.md#parametrized-types)).

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
data to allow selection of the proper branch during parsing. Position of the selector in bitstream is
implementation defined.

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
    float16     float16Value = 1.23;
    string      stringValue = "string";
    BasicColor  enumValue = BasicColor.BLACK;
};
```

The default values are used by the encoder if the  value of corresponding field has not been set. So, there will
be always a value written to the stream. This is in contrast to other serialization mechanisms where
the decoders would generate the default value. Reason for this is the missing "wire format" in zserio which
would add additional information in the stream for identifying fields and whether they are set or not.

[\[top\]](ZserioLanguageOverview.md#language-guide)
