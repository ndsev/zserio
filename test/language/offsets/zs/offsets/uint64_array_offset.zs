package offsets.uint64_array_offset;

struct UInt64ArrayOffset
{
    uint64  offsets[];
    int8    array[];
offsets[@index]:
    int32   values[];
};
