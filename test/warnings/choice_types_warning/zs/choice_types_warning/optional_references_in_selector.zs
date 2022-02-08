package choice_types_warning.optional_references_in_selector;

struct Selector
{
    optional int8 numBits;
};

choice TestChoice(Selector selector) on selector.numBits // warning
{
    case 8:
        int8    field8;
    case 16:
        int16   field16;
};
