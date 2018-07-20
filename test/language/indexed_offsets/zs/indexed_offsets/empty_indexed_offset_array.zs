package indexed_offsets.empty_indexed_offset_array;

struct EmptyIndexedOffsetArray
{
    uint32  offsets[0];
    bit:1   spacer;

offsets[@index]:
    bit:5   data[0];

    bit:6   field;
};
