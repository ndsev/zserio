package indexed_offsets.bit5_indexed_offset_array;

const uint8 NUM_BIT5_ELEMENTS = 5;

struct Bit5IndexedOffsetArray
{
    uint32  offsets[NUM_BIT5_ELEMENTS];
    bit:1   spacer;

offsets[@index]:
    bit:5   data[NUM_BIT5_ELEMENTS];
};
