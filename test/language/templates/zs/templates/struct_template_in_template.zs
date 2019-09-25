package templates.struct_template_in_template;

struct Field<T>
{
    Compound<T> value;
};

struct Compound<T>
{
    T value;
};

struct StructTemplateInTemplate
{
    Field<uint32> uint32Field;
    Field<string> stringField;
};
