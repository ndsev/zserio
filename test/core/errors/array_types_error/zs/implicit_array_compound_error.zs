package implicit_array_compound_error;

struct Compound
{
    uint8 data;
};

struct ImplicitArrayCompoundError
{
    implicit Compound array[];
};
