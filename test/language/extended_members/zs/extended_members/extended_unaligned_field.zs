package extended_members.extended_unaligned_field;

struct Original
{
    bit:3 value;
};

struct Extended
{
    bit:3 value;
    extend uint64 extendedValue;
};
