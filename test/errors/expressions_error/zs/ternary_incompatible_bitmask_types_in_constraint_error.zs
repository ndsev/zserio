package ternary_incompatible_bitmask_types_in_constraint_error;

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

struct TernaryIncompatibleBitmaskTypesInConstraintError
{
    Bitmask1 bitmask1;
    Bitmask2 bitmask2;
    bool useBitmask1;
    uint32 array[] : lengthof(array) == numbits((useBitmask1 ? bitmask1 : bitmask2));
};
