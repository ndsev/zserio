package indexed_offsets.packed_auto_indexed_offset_array;

struct AutoIndexedOffsetArray
{
    uint32  offsets[];
    bit:3  spacer;

offsets[@index]:
    packed bit:5 data[];
};
