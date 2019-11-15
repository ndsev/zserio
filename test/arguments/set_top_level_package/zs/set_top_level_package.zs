package set_top_level_package;

subtype uint8 U8;

const uint8 CONST = 13;

enum uint8 Enumeration
{
    ITEM_MIN,

    STRING,
    FLOAT32,
    INT32,
    EMPTY,

    ITEM_MAX = 42
};

choice SimpleChoice(Enumeration e) on e
{
    case STRING:
        string stringField;
    case Enumeration.FLOAT32:
        float32 float32Field;
    case set_top_level_package.Enumeration.INT32:
        int32 int32Field;
    default: ; // empty
};

struct SimpleStructure
{
    bit:3           numberA;
    U8              numberB = CONST : numberB > valueof(Enumeration.ITEM_MIN) &&
                                            numberB > valueof(set_top_level_package.Enumeration.ITEM_MAX);
    bit:7           numberC;
    Enumeration     e = Enumeration.EMPTY;
    SimpleChoice(e) simpleChoice;
};
