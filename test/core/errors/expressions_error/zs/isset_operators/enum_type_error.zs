package isset_operators.enum_type_error;

enum uint8 Enumeration
{
    ONE, TWO
};

struct EnumTypeError
{
    Enumeration enumField : isset(enumField, Enumeration.ONE);
};
