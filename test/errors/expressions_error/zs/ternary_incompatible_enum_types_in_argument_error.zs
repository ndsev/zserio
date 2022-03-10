package ternary_incompatible_enum_types_in_argument_error;

enum uint8 Enum1
{
    ONE,
    TWO
};

enum uint8 Enum2
{
    ONE,
    TWO
};

struct Parameterized(Enum1 param)
{
    uint32 one if param == Enum1.ONE;
    string two if param == Enum1.TWO;
};

struct TernaryIncompatibleEnumTypesInArgumentError
{
    Enum1 param1;
    Enum2 param2;
    bool ok;
    Parameterized(ok ? param1 : param2) parameterized;
};
