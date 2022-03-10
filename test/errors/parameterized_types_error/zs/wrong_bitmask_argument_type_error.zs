package wrong_bitmask_argument_type_error;

bitmask uint8 Bitmask1
{
    ONE,
    TWO
};

bitmask uint8 Bitmask2
{
    ONE,
    TWO
};

struct Parameterized(Bitmask1 param)
{
    uint32 array[valueof(param)];
};

struct WrongBitmaskArgumentTypeError
{
    Bitmask1 param1;
    Bitmask2 param2;
    Parameterized(param1) parameterizedOk;
    Parameterized(param2) parameterizedError;
};
