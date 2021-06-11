package packed_array_struct_has_no_packable_field_error;

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

struct StructWithoutPackable
{
    float32 field1;
    extern field2;
    uint32 offset; // offset is always unpackable
offset:
    string field3;
    uint32 offsets[]; // offsets are always unpackable
offsets[@index]:
    bool array1[];
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

struct PackedArrayStructHasNoPackableFieldError
{
    packed StructWithPackable array1[];
    packed UnionWithPackableField array2[];
    packed StructWithPackableArray array3[];
    packed StructWithoutPackable array4[];
};
