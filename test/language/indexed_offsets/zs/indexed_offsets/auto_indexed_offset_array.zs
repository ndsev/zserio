package indexed_offsets.auto_indexed_offset_array;

struct AutoIndexedOffsetArray
{
    uint32  offsets[];
    bit:1   spacer;

offsets[@index]:
    bit:5   data[];
};
