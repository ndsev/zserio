package ternary_incompatible_bitmask_types_in_argument_error;

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
    uint32 one if (param & Bitmask1.ONE) == Bitmask1.ONE;
    string two if (param & Bitmask1.TWO) == Bitmask1.TWO;
};

struct TernaryIncompatibleBitmaskTypesInArgumentError
{
    Bitmask1 param1;
    Bitmask2 param2;
    bool ok;
    Parameterized(ok ? param1 : param2) parameterized;
};
