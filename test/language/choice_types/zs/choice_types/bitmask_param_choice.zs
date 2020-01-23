package choice_types.bitmask_param_choice;

bitmask uint8 Selector
{
    BLACK,
    WHITE,
    BLACK_AND_WHITE = 3
};

choice BitmaskParamChoice(Selector selector) on selector
{
    case BLACK:
        uint8 black;

    case WHITE:
        uint8 white;

    case BLACK | WHITE:
        uint16 blackAndWhite;
};
