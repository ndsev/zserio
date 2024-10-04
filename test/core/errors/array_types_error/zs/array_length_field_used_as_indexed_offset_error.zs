package array_length_field_used_as_indexed_offset_error;

struct Holder
{
    uint32 offsets[];
};

struct Parameterized(Holder holder)
{
    uint32 array[holder.offsets[0]];
};

struct Container
{
    Holder holder;
holder.offsets[@index]:
    Parameterized(holder) parameterized[];
};
