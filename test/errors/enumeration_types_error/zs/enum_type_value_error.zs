package enum_type_value_error;

enum uint8 BoolEnumValueEnum
{
    NONE       = 000b,
    DARK_RED   = 001b,
    DARK_BLUE  = RGBColors.BLUE,
    DARK_BLACK = 111b
};

enum uint8 RGBColors
{
    NONE,
    RED,
    BLUE
};
