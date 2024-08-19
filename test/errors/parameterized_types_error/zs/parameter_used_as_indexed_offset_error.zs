package parameter_used_as_indexed_offset_error;

struct Holder
{
    uint32 offsets[];
};

struct Parameterized(uint32 param)
{
    uint32 array[param];
};

struct Container
{
    Holder holder;
holder.offsets[@index]:
    Parameterized(holder.offsets[@index]) parameterized[];
};
