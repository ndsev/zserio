package with_type_info_code.introspection;

const uint8 BitmaskLen = 6;

bitmask bit<6> Bitmask
{
    FLAG1,
    FLAG2,
    FLAG3
};

enum int:5 SelectorEnum
{
    STRUCTURE = -5,
    UNION,
    BITMASK
};

subtype SelectorEnum Selector;

struct Empty
{
};

struct Child
{
    uint32 id;
    string name;
};

struct ParameterizedChild(uint8 param)
{
    uint8 array[param];
};

struct Structure
{
    Empty empty;
    optional Child child;
    Child childArray[];
align(8):
    uint8 param;
    ParameterizedChild(param) parameterized;
    varsize len : len > 0 && len < 1000;
    uint32 offsets[len];
offsets[@index]:
    ParameterizedChild(param) parameterizedArray[len];
    Bitmask bitmaskField;
    Bitmask bitmaskArray[];
    bit<param> dynamicBitField if param < 64;
};

union Union
{
    Child childArray[];
    ParameterizedChild(5) parameterized;
    Structure structure;
    Bitmask bitmaskField;
};

choice Choice(Selector selector) on selector
{
    case STRUCTURE:
        Structure structureField;
    case UNION:
        Union unionField;
    case BITMASK:
        Bitmask bitmaskField;
};
