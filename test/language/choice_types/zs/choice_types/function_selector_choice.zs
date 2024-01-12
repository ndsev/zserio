package choice_types.function_selector_choice;

struct Selector
{
    int8 numBits;

    function int8 getNumBits()
    {
        return numBits;
    }
};

choice TestChoice(Selector selector) on selector.getNumBits()
{
    case 8:
        int8    field8;
    case 16:
        int16   field16;
};
