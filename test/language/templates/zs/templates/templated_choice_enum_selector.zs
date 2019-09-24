package templates.templated_choice_enum_selector;

enum uint8 EnumFromZero
{
    ONE,
    TWO,
    THREE
};

enum uint32 EnumFromOne
{
    ONE=1,
    TWO,
    THREE
};

choice TemplatedChoice<T>(T selector) on selector
{
    case ONE:
        uint16 uint16Field;
    case T.TWO:
        uint32 uint32Field;
    case THREE:
        string stringField;
};

struct TemplatedChoiceEnumSelector
{
    EnumFromZero selectorFromZero;
    EnumFromOne selectorFromOne;
    TemplatedChoice<EnumFromZero>(selectorFromZero) fromZeroChoice;
    TemplatedChoice<EnumFromOne>(selectorFromOne) fromOneChoice;
};
