package field_used_as_offset_error;

struct Holder
{
    uint32 offset;

    function uint32 getMovedOffset()
    {
        return offset + 1;
    }
};

struct Container
{
    Holder holder;
holder.offset:
    string field;
};
