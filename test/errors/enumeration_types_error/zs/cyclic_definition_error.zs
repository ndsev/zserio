package cyclic_definition_error;

enum uint8 CyclicDefinition
{
    NONE       = 000b,
    DARK_RED   = valueof(DARK_RED),
    DARK_BLACK = 111b
};
