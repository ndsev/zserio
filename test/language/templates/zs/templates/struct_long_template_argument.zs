package templates.struct_long_template_argument;

struct ThisIsVeryVeryVeryLongNamedStructure
{
    string data;
};

struct TemplatedStruct<T, U, V>
{
    T field1;
    U field2;
    V field3;
};

struct StructLongTemplateArgument
{
    TemplatedStruct<ThisIsVeryVeryVeryLongNamedStructure,
                    ThisIsVeryVeryVeryLongNamedStructure,
                    ThisIsVeryVeryVeryLongNamedStructure> structNameOverflow;
};
