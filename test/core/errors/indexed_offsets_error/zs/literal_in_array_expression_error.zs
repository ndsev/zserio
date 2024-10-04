package literal_in_array_expression_error;

struct OffsetArray
{
    PackedOffsets packedOffsets;
};

struct PackedOffsets
{
    packed uint32 offsets[10];
};

struct PackedOffsetArrayError
{
    OffsetArray offsetArray;

offsetArray.packedOffsets.offsets[0]:
    int32 fields[10];
};
