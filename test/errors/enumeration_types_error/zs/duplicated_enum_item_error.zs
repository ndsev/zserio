package duplicated_enum_item_error;

enum uint8 DuplicatedItemEnum
{
    NONE       = 000b,
    DARK_RED   = 001b,
    DARK_RED   = 010b,
    DARK_BLACK = 111b
};
