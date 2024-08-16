package packed_complex_offset_array_error;

struct PackedOffsets
{
    packed uint32 offsets[20];
};

struct PackedOffsetArrayError
{
    PackedOffsets packedOffsets;

packedOffsets.offsets[@index]:
    int32 fields[10];
};
