package extended_members.multiple_extended_fields;

struct Original
{
    uint32 value;
};

struct Extended1
{
    uint32 value;
    extend bit:4 extendedValue1;
};

struct Extended2
{
    uint32 value;
    extend bit:4 extendedValue1;
    extend string extendedValue2 = "test";
};
