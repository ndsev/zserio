package ternary_incompatible_compound_types_in_argument_error;

struct Param1
{
    varsize len;
};

struct Param2
{
    string text;
};

struct Parameterized(Param1 param)
{
    uint32 array[param.len];
};

struct TernaryIncompatibleCompoundTypesInArgumentError
{
    Param1 param1;
    Param2 param2;
    bool ok;
    Parameterized(ok ? param1 : param2) parameterized;
};
