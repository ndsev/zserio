package array_types.packed_auto_array_union_has_no_packable_field;

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

union UnionWithoutPackableField // choiceTag is still packable
{
    float32 field1;
    extern field2;
};

struct PackedAutoArrayUnionHasNoPackableField
{
    packed StructWithPackable array1[];
    packed StructWithPackableArray array2[];
    packed UnionWithoutPackableField array3[];
};
