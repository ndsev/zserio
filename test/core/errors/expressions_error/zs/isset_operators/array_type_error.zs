package isset_operators.array_type_error;

bitmask uint8 Bitmask
{
    READ, WRITE
};

struct ArrayTypeError
{
    Bitmask bitmaskArray[];
    uint8 extraField if isset(bitmaskArray, Bitmask.WRITE);
};
