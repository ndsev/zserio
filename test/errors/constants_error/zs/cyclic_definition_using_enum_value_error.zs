package cyclic_definition_using_enum_value_error;

enum int32 Widths
{
    THIN = DEFAULT_WIDTH,
    THICK = 1
};

const Widths DEFAULT_WIDTH = 1;
const Widths THICK_WIDTH = Widths.THICK; // OK, Widths already defined

const Colors DEFAULT_COLOR = Colors.WHITE;

enum int32 Colors
{
    WHITE = DEFAULT_COLOR, // cycle!
    BLACK
};
