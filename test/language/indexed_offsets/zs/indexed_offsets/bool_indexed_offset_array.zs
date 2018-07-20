package indexed_offsets.bool_indexed_offset_array;

const uint8 NUM_BOOL_ELEMENTS = 5;

struct BoolIndexedOffsetArray
{
    uint32  offsets[NUM_BOOL_ELEMENTS];
    bit:1   spacer;

offsets[@index]:
    bool    data[NUM_BOOL_ELEMENTS];
};
