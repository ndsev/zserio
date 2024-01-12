package choice_types.function_returning_literal_selector_choice;

struct Selector(bool extended)
{
    function int8 getSelector()
    {
        return extended ? 16 : 8;
    }
};

choice TestChoice(Selector selector) on selector.getSelector()
{
    case 8:
        int8    field8;
    case 16:
        int16   field16;
};
