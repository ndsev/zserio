package packed_array_union_in_structure_error;

struct PackableStruct
{
    packable uint8 field;
};

union TestUnion
{
    PackableStruct packableStruct;
};

struct TestStructure
{
    TestUnion testUnion;
};

struct PackedArrayUnionInStructureError
{
    packed TestStructure array[];
};
