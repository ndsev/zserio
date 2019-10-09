package templates.choice_templated_selector;

const uint16 SHIFT16 = 0;
const uint32 SHIFT32 = 1;

choice TemplatedChoice<T, C>(T selector) on selector + C // check template in selector expression
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
    TemplatedChoice<uint16, SHIFT16>(selector16) uint16Choice;
    TemplatedChoice<uint32, SHIFT32>(selector32) uint32Choice;
};
