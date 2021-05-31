package packed_offset_array_error;

struct OffsetArray
{
    packed uint32 offsets[20];
};

struct PackedOffsetArrayError
{
    OffsetArray offsetArray;
    uint32 offsetIndexShift1;
    uint32 offsetIndexShift2;

offsetArray.offsets[offsetIndexShift1 + @index + offsetIndexShift2]:
    int32 fields[10];
};
