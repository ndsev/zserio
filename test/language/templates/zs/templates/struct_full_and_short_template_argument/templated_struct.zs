package templates.struct_full_and_short_template_argument.templated_struct;

struct Storage
{
    string data;
};

struct TemplatedStruct<T>
{
    T storage;
};

struct StructShortNameTemplateArgument
{
    TemplatedStruct<Storage> structInternal;
};
