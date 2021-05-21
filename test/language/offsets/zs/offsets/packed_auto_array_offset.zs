package offsets.packed_auto_array_offset;

struct AutoArrayHolder
{
    uint32          autoArrayOffset;
    bit:1           forceAlignment;

autoArrayOffset:
    packed int:7    autoArray[];
};
