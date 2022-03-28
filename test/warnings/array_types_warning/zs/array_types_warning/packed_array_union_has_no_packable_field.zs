package array_types_warning.packed_array_union_has_no_packable_field;

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

union UnionWithoutPackableField
{
    float32 field1;
    extern field2;
};

struct PackedArrayUnionHasNoPackableField
{
    packed StructWithPackable array1[]; // no warning
    packed StructWithPackableArray array2[]; // no warning
    packed UnionWithoutPackableField array3[]; // warning!
 };
