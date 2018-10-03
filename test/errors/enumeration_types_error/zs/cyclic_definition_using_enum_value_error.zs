package cyclic_definition_using_enum_value_error;

enum int32 Colors
{
    WHITE = 0,
    BLACK = 1,
    RED = RGBColors.NEW_RED
};

enum int32 RGBColors
{
    NEW_RED = Colors.RED // cycle!
};
