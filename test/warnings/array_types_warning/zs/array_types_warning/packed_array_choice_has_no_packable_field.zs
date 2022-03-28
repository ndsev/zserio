package array_types_warning.packed_array_choice_has_no_packable_field;

struct StructWithPackable
{
    string field1;
    uint32 field2; // packable
};

struct StructWithPackableArray
{
    string field1;
    uint16 array1[]; // packable
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

struct PackedArrayChoiceHasNoPackableField
{
    packed StructWithPackable array1[]; // no warning
    packed UnionWithPackableField array2[]; // no warning
    packed StructWithPackableArray array3[]; // no warning
    packed ChoiceWithoutPackableField(true) array4[]; // warning!
};
