package cyclic_definition_using_enum_value_error;

enum int32 Colors
{
    WHITE = 0,
    BLACK = 1,
    RED = RGBColors.RED
};

enum int32 RGBColors
{
    RED = Colors.RED // cycle!
};
