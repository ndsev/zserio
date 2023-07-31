package extended_members.extended_indexed_offsets;

struct Original
{
    uint32 offsets[];
};

struct Extended
{
    uint32 offsets[];
offsets[@index]:
    extend string array[];
};
