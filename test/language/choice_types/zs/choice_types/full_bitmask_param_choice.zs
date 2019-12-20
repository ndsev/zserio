package choice_types.full_bitmask_param_choice;

bitmask uint8 Selector
{
    BLACK,
    WHITE,
    BLACK_AND_WHITE = 11b
};

choice FullBitmaskParamChoice(Selector selector) on selector
{
    case Selector.BLACK:
        uint8 black;

    case Selector.WHITE:
        uint8 white;

    case Selector.BLACK | Selector.WHITE:
        uint16 blackAndWhite;
};
