package templates;

struct Field<T>
{
    T value;
};

struct Compound<T>
{
    T value;
};

struct StructTemplatedTemplateArgument
{
    Field<Compound<uint32>> compoundField;
};

