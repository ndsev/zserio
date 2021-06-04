package packed_simple_offset_array_error;

struct PackedOffsetArrayError
{
    packed uint32 offsets[10];

offsets[@index]:
    int32 fields[10];
};

// TODO[mikir] new test in offsets_error: offsets[0]:
