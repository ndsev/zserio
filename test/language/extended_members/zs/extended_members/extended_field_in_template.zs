package extended_members.extended_field_in_template;

struct Original
{
    uint32 value;
};

struct Compound
{
    uint32 field;
};

struct Extended<T>
{
    uint32 value;
    extend T extendedValue;
};

instantiate Extended<uint32> ExtendedSimple;
instantiate Extended<Compound> ExtendedCompound;
