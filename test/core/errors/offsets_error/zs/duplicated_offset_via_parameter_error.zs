package duplicated_offset_via_parameter_error;

struct Holder
{
    uint32 offset1;
    uint32 offset2;
};

struct Parameterized(Holder holder)
{
holder.offset1:
    uint32 field1;
holder.offset2: // ok, another field
    string field2;
holder.offset1: // same offset field used again!
    float32 field3;
};

struct Container
{
    Holder holder;
    Parameterized(holder) param;
};
