package templates.choice_templated_selector;

enum uint16 Shift16
{
    SHIFT = 0
};

enum uint32 Shift32
{
    SHIFT = 1
};

choice TemplatedChoice<T, E>(T selector) on selector + valueof(E.SHIFT) // check template in selector expression
{
    case 0:
        uint16 uint16Field;
    case 1:
        uint32 uint32Field;
    case 2:
        string stringField;
};

struct ChoiceTemplatedSelector
{
    uint16 selector16;
    uint32 selector32;
    TemplatedChoice<uint16, Shift16>(selector16) uint16Choice;
    TemplatedChoice<uint32, Shift32>(selector32) uint32Choice;
};
