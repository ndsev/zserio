package valueof_operators.enum_type_error;

struct EnumTypeError
{
    Colour  colour;
    int32   field if valueof(Colour) == 0;
};

enum uint8 Colour
{
    WHITE,
    BLACK,
    RED
};
