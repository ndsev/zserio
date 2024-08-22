package duplicated_indexed_offset_field_error;

struct Holder
{
    uint32 offsets[];
};

struct Container
{
    Holder holder1;
    Holder holder2;
holder1.offsets[@index]:
    string fields1[];
holder2.offsets[@index]: // no problem, another holder
    string fields2[];
holder1.offsets[@index]: // duplicated use of the offset field!
    string fields3[];
};
