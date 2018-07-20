package choice_types.enum_param_choice;

enum uint8 Selector
{
    BLACK,
    GREY,
    WHITE
};

subtype int8  Black;
subtype int16 Grey;
subtype int32 White;

choice EnumParamChoice(Selector selector) on selector
{
    case Selector.BLACK:
        Black black;

    case Selector.GREY:
        Grey grey;

    case WHITE:
        White white;
};
