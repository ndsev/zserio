package with_reflection_code;

const uint8 BitmaskLen = 6;

bitmask bit<BitmaskLen> Bitmask
{
    FLAG1,
    FLAG2,
    FLAG3
};

enum int:5 SelectorEnum
{
    STRUCT = -5,
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
    bool hasNicknames;
    string nicknames[] if hasNicknames;
};

struct Parameterized(uint8 param)
{
    uint8 array[param];
};

struct Struct
{
    Empty empty;
    optional Child child;
    Child childArray[];
align(8):
    uint8 param;
    Parameterized(param) parameterized;
    varsize len : len > 0 && len < 1000;
    uint32 offsets[len];
offsets[@index]:
    Parameterized(param) parameterizedArray[len];
    Bitmask bitmaskField;
    Bitmask bitmaskArray[];
    SelectorEnum enumField;
    packed Selector enumArray[];
    bit<param> dynamicBitField if param < 64;
    bit<param> dynamicBitFieldArray[];
    int<param> dynamicIntField if param < 4;
    int<4> dynamicIntFieldArray[4] if param < 64;
    bool boolArray[];
    extern externField;
    extern externArray[];
    bytes bytesField;
    bytes bytesArray[];

    function Selector getEnumField()
    {
        return enumField;
    }
};

union Union
{
    Child childArray[];
    Parameterized(5) parameterized;
    Struct structField;
    Bitmask bitmaskField;

    function uint8 getStructFieldParam()
    {
        return structField.param;
    }
};

const string EmptyString = "";

choice Choice(Selector selector) on selector
{
    case STRUCT:
        Struct structField;
    case UNION:
        Union unionField;
    case BITMASK:
        Bitmask bitmaskField;

    function string getFirstChildName()
    {
        return selector == Selector.STRUCT ? structField.childArray[0].name : EmptyString;
    }

    function Bitmask getBitmaskFromUnion()
    {
        return unionField.bitmaskField;
    }
};
