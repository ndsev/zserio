package extended_members.extended_compound_field;

struct Original
{
    uint32 value;
};

struct Compound
{
    uint32 array[];
};

struct Extended
{
    uint32 value;
    extend Compound extendedValue;
};
