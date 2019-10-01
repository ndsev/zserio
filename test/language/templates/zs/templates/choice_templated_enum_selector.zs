package templates.choice_templated_enum_selector;

enum uint8 EnumFromZero
{
    ONE,
    TWO,
    THREE
};

enum uint32 EnumFromOne
{
    ONE = 1,
    TWO,
    THREE
};

choice TemplatedChoice<T>(T selector) on selector
{
    case ONE:
        uint16 uint16Field;
    case T.TWO: // check template in case expression
        uint32 uint32Field;
    case THREE:
        string stringField;
};

struct ChoiceTemplatedEnumSelector
{
    EnumFromZero selectorFromZero;
    EnumFromOne selectorFromOne;
    TemplatedChoice<EnumFromZero>(selectorFromZero) fromZeroChoice;
    TemplatedChoice<EnumFromOne>(selectorFromOne) fromOneChoice;
};
