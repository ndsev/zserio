package choice_types.enum_with_removed_item_param_choice;

enum uint16 Selector
{
    BLACK = 0xfff,
    WHITE = 0x000,
    @removed GREY = 0xaaa
};

choice EnumWithRemovedItemParamChoice(Selector selector) on selector
{
    case GREY:
        uint16 greyData;
    default:
        uint8 data;
};

