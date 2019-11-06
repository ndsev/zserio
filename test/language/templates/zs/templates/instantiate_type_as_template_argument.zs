package templates.instantiate_type_as_template_argument;

struct Test<T>
{
    T value;
};

struct Other<T>
{
    T value;
};

struct InstantiateTypeAsTemplateArgument
{
    Other<Str> other;
};

// define at the end to check correct template argument resolution
instantiate Test<string> Str;
