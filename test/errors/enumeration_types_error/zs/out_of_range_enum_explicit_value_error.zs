package out_of_range_enum_explicit_value_error;

enum uint8 OutOfRangeValueEnum
{
    NONE       = 000b,
    DARK_RED   = 001b,
    DARK_BLUE  = 256,
    DARK_BLACK = 111b
};
