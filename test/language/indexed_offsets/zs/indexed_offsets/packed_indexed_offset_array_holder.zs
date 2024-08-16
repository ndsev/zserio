package indexed_offsets.packed_indexed_offset_array_holder;

struct OffsetArray
{
    packed OffsetHolder offsetHolders[];
};

struct OffsetHolder
{
    uint32 offset1;    // this is unpackable because it stores offset
    uint32 offset2;    // this is unpackable because it stores offset
    uint32 value;      // this is packable integer
};

struct AutoIndexedOffsetArray
{
    OffsetArray offsetArray;

offsetArray.offsetHolders[@index].offset1:
    int32 data1[];

offsetArray.offsetHolders[@index].offset2:
    int32 data2[];
};
