package non_integer_enum_value_error;

enum uint8 WrongNonIntegerValueEnum
{
    NONE       = 000b,
    DARK_RED   = 001b,
    DARK_BLUE  = false,
    DARK_BLACK = 111b
};
