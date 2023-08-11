package array_types.packed_auto_array_empty_compounds;

struct EmptyStruct
{};

union EmptyUnion
{};

choice EmptyChoice(uint32 param) on param
{};

struct Main
{
    EmptyStruct emptyStruct;
    EmptyUnion emptyUnion;
    uint32 param; // packable
    EmptyChoice(param) emptyChoice;
};

struct PackedAutoArray
{
    packed EmptyStruct emptyStructArray[];
    packed EmptyUnion emptyUnionArray[];
    packed EmptyChoice(0) emptyChoiceArray[];
    packed Main mainArray[];
};
