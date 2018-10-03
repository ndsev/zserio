package unresolved_enum_item_error;

enum uint8 Selector
{
    BLACK,
    GREY,
    RED
};

choice EnumParamChoice(Selector selector) on selector
{
    case unresolved_enum_item_error.Selector.BLACK:
        int8 black;

    case Selector.GREY:
        int16 grey;

    case RED:
        Selector foo : foo == RED; // RED is not available here without 'Selector.' prefix

    default:
        int64 other;
};
