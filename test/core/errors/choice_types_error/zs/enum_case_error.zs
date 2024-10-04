package enum_case_error;

enum uint8 Selector
{
    BLACK,
    GREY,
    RED
};

enum uint16 OtherSelector
{
    LIGHT_GREY,
    LIGHT_RED
};

choice EnumParamChoice(Selector selector) on selector
{
    case enum_case_error.Selector.BLACK:
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
