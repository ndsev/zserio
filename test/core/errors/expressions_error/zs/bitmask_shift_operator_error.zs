package bitmask_shift_operator_error;

bitmask uint8 Mask
{
    READ,
    WRITE,
    CREATE
};

struct BitmaskShiftOperatorError
{
    Mask mask = Mask.READ << Mask.CREATE;
};
