package packed_array_unpackable_struct_error;

struct UnpackableStruct
{
    uint32 field;
};

struct PackedArrayUnpackableStructError
{
    packed UnpackableStruct array[];
};
