package out_of_range_enum_value_error;

enum uint8 OutOfRangeValueEnum
{
    NONE       = 000b,
    DARK_RED   = 001b,
    DARK_BLUE  = 255,
    DARK_BLACK // calculated to 256
};
