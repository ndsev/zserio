package module_names.bitmask_with_enum_clash_error;

bitmask uint8 ColorInfo
{
    WHITE,
    BLACK
};

enum uint8 Color_Info
{
    WHITE,
    BLACK,
    BLACK_AND_WHITE
};
