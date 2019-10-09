package templates.struct_templated_template_argument;

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
