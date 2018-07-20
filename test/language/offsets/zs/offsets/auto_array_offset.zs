package offsets.auto_array_offset;

struct AutoArrayHolder
{
    uint32          autoArrayOffset;
    bit:1           forceAlignment;

autoArrayOffset:
    int:7           autoArray[];
};
