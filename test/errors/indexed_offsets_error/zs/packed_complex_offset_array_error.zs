package packed_complex_offset_array_error;

struct OffsetArray
{
    PackedOffsets packedOffsets;
};

struct PackedOffsets
{
    packed uint32 offsets[20];
};

struct PackedOffsetArrayError
{
    OffsetArray offsetArrays[2];
    uint32 offsetIndexShift1;
    uint32 offsetIndexShift2[2];

offsetArrays[0].packedOffsets.offsets[offsetIndexShift1 + @index + offsetIndexShift2[0]]:
    int32 fields[10];
};
