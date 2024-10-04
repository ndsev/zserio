package field_used_as_indexed_offset_error;

struct Holder
{
    uint32 offsets[];
offsets[@index]:
    string fields[];
};

struct Container
{
    Holder holder;
    string field if lengthof(holder.offsets) > 0;
};
