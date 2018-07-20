package cyclic_definition_using_constant_error;

const int32 BLACK_COLOR = 1;

enum int32 Colors
{
    BLACK = BLACK_COLOR, // OK, BLACK_COLOR defined
    WHITE = WHITE_COLOR
};

const Colors WHITE_COLOR = Colors.WHITE; // cycle!
