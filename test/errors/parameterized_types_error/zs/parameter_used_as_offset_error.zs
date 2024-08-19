package parameter_used_as_offset_error;

struct Holder
{
    uint32 offset;
};

struct Parameterized(uint32 param)
{
    uint32 array[param];
};

struct Container
{
    Holder holder;
holder.offset:
    Parameterized(holder.offset) parameterized;
};
