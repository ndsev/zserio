package cyclic_definition_using_constant_error;

const int32 BLACK_COLOR = 1;

enum int32 Colors
{
    BLACK = BLACK_COLOR, // OK, BLACK_COLOR defined
    WHITE = WHITE_COLOR
};

const int32 WHITE_COLOR = valueof(Colors.WHITE); // cycle!
