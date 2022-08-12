package isset_operators.wrong_bitmask_type_error;

bitmask uint8 Bitmask
{
    READ, WRITE
};

bitmask uint16 WrongBitmask
{
    READ, WRITE
};

struct WrongBitmaskTypeError
{
    Bitmask bitmaskField : isset(bitmaskField, READ);
    uint8 anotherField if isset(bitmaskField, WrongBitmask.READ);
};
