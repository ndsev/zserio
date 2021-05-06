package packed_array_union_error;

struct PackableStruct
{
    packable uint8 field;
};

union TestUnion
{
    PackableStruct packableStruct;
};

struct PackedArrayUnionError
{
    packed TestUnion array[];
};
