package arrays_warning.compound_has_no_packable_field_warning;

struct StructWithPackable
{
    string field1;
    uint32 field2; // packable
};

struct StructWithoutPackable
{
    float32 field1;
    extern field2;
};

enum uint8 TestEnum
{
    ONE,
    TWO
};

union UnionWithPackableField
{
    float32 field1;
    TestEnum field2;
};

choice ChoiceWithoutPackableField(bool selector) on selector
{
    case true:
        float32 field1;
    case false:
        extern field2;
};

struct CompoundHasNoPackableFieldWarning
{
    packed StructWithPackable array1[];
    packed StructWithoutPackable array2[];
    packed UnionWithPackableField array3[];
    packed ChoiceWithoutPackableField(true) array4[];
};
