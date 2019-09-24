package templates.template_in_template;

struct Field<T>
{
    Compound<T> value;
};

struct Compound<T>
{
    T value;
};

struct TemplateInTemplate
{
    Field<uint32> uint32Field;
    Field<string> stringField;
};
