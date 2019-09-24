package templates.templated_field;

struct Field<T>
{
    T value;
};

struct Compound
{
    uint32 value;
};

struct TemplatedField
{
    Field<uint32>   uint32Field;
    Field<Compound> compoundField;
    Field<string>   stringField;
};
