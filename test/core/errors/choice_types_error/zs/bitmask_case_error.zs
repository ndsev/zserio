package bitmask_case_error;

bitmask uint8 Selector
{
    BLACK,
    GREY,
    RED
};

bitmask uint16 OtherSelector
{
    LIGHT_GREY,
    LIGHT_RED
};

choice BitmaskParamChoice(Selector selector) on selector
{
    case bitmask_case_error.Selector.BLACK:
        int8 black;

    case Selector.GREY:
        int16 grey;

    case RED:
        int32 red;

    case OtherSelector.LIGHT_RED:
        int32 lightRed;

    default:
        int64 other;
};
