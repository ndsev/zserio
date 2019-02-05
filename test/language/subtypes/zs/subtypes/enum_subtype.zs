package subtypes.enum_subtype;

subtype Color ColorSubtype;

enum int32 Color
{
    BLACK,
    WHITE
};

const ColorSubtype CONST_BLACK = ColorSubtype.BLACK;
