package indexed_offsets.packed_indexed_offset_array_holder;

struct OffsetArray
{
    packed OffsetHolder offsetHolders[];
};

struct OffsetHolder
{
    uint32 offset; // this is unpackable because it stores offset
    uint32 value;  // this is packable integer
};

struct AutoIndexedOffsetArray
{
    OffsetArray offsetArray;

offsetArray.offsetHolders[@index + 1].offset:
    int32 data[];
};
