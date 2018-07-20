package indexed_offsets.varint32_indexed_offset_array;

const uint8 NUM_VARINT32_ELEMENTS = 5;

struct VarInt32IndexedOffsetArray
{
    uint32      offsets[NUM_VARINT32_ELEMENTS];
    bit:1       spacer;

offsets[@index]:
    varint32    data[NUM_VARINT32_ELEMENTS];
};
