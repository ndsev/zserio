package duplicated_indexed_offset_via_parameter_error;

struct Holder
{
    uint32 offsets1[];
    uint32 offsets2[];
};

struct Parameterized(Holder holder)
{
holder.offsets1[@index]:
    uint32 fields1[];
holder.offsets2[@index]: // ok, another field
    string fields2[];
holder.offsets1[@index]: // same offset field used again!
    float32 fields3[];
};

struct Container
{
    Holder holder;
    Parameterized(holder) param;
};
