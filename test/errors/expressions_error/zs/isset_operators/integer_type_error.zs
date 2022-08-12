package isset_operators.integer_type_error;

bitmask uint8 Bitmask
{
    READ, WRITE
};

struct EnumTypeError
{
    uint8 field;
    uint8 anotherField if isset(field, Bitmask.READ);
};
