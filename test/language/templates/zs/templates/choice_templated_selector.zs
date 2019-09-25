package templates.choice_templated_selector;

choice TemplatedChoice<T>(T selector) on selector
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
    TemplatedChoice<uint16>(selector16) uint16Choice;
    TemplatedChoice<uint32>(selector32) uint32Choice;
};
