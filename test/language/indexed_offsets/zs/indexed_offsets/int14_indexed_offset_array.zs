package indexed_offsets.int14_indexed_offset_array;

const uint8 NUM_INT14_ELEMENTS = 5;

struct Int14IndexedOffsetArray
{
    uint32  offsets[NUM_INT14_ELEMENTS];
    bit:1   spacer;

offsets[@index]:
    int<14> data[NUM_INT14_ELEMENTS];
};
