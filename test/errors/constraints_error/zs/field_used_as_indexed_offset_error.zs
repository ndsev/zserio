package field_used_as_indexed_offset_error;

struct Container
{
    uint32 offsets[];
offsets[@index]:
    string fields[] : lengthof(offsets) > 0;
};

