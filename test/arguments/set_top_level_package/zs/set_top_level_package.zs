// Default package is intended to check that set top level package option works even for default packages.

import set_top_level_package.constant.CONST;
import set_top_level_package.enumeration.Enumeration;

subtype uint8 U8;

choice SimpleChoice(Enumeration value) on value
{
    case STRING:
        string stringField;
    case Enumeration.FLOAT32:
        float32 float32Field;
    case set_top_level_package.enumeration.Enumeration.INT32:
        int32 int32Field;
    default: ; // empty
};

struct SimpleTemplate<E>
{
    bool    boolField;
    bit:5   expressionField if valueof(E.ITEM_MIN) == 0;
};
struct SimpleStructure
{
    bit:3 numberA;
    U8 numberB = set_top_level_package.constant.CONST : numberB > valueof(Enumeration.ITEM_MIN) &&
                    numberB > valueof(set_top_level_package.enumeration.Enumeration.ITEM_MAX);
    bit:7 numberC;
    set_top_level_package.enumeration.Enumeration value = Enumeration.EMPTY;
    SimpleChoice(value) simpleChoice;
    SimpleTemplate<set_top_level_package.enumeration.Enumeration> simpleTemplate;
};
