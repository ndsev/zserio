package ternary_incompatible_enum_types_in_optional_error;

enum uint8 Enum1
{
    ONE,
    TWO
};

enum uint8 Enum2
{
    ONE,
    TwO
};

struct TernaryIncompatibleEnumTypesInOptionalError
{
    Enum1 enum1;
    Enum2 enum2;
    bool useEnum1;
    uint32 array[] if (useEnum1 ? enum1 : enum2) == Enum1.ONE;
};
