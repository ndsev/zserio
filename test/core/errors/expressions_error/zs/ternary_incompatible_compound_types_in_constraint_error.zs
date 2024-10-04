package ternary_incompatible_compound_types_in_constraint_error;

struct Compound1
{
    uint32 len;
};

struct Compound2
{
    uint32 len;
};

struct TernaryIncompatibleCompoundTypesInConstraintError
{
    Compound1 compound1;
    Compound2 compound2;
    bool useCompound1;
    uint32 array[] : lengthof(array) == (useCompound1 ? compound1 : compound2).len;
};
