package parameterized_types.parameterized_nested_in_array;

struct Parameterized(uint8 param)
{
    uint32 field : field > param;
};

struct Element
{
    Parameterized(5) parameterized;
};

struct Holder
{
    Element elementArray[];
    packed Element elementPackedArray[];
};
