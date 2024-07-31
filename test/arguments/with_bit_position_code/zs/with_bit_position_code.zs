package with_bit_position_code;

struct Item(bool needsExtra)
{
    uint32 value;
    int8   extraValue if needsExtra;
};

choice ItemChoice(bool hasItem) on hasItem
{
    case true:
        Item(true) item;

    case false:
        bool boolValue;
};

union ValueUnion
{
    uint16  value16;
    uint32  value32;
};

struct SimpleStruct
{
    string stringValue;
    optional bool optionalValue;
};

struct Main
{
    ItemChoice(true) itemChoice;
    ValueUnion valueUnion;
    SimpleStruct simpleStruct;
    Item(false) item;
};
