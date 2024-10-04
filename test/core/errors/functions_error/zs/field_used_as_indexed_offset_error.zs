package field_used_as_indexed_offset_error;

struct Holder
{
    uint32 offsets[];

    function uint32 getFirstOffset()
    {
        return offsets[0];
    }
};

struct Container
{
    Holder holder;
holder.offsets[@index]:
    string fields[];
};
