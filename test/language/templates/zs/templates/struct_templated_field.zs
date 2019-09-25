package templates.struct_templated_field;

struct Field<T>
{
    T value;
};

struct Compound
{
    uint32 value;
};

struct StructTemplatedField
{
    Field<uint32>   uint32Field;
    Field<Compound> compoundField;
    Field<string>   stringField;
};
