package templates.struct_long_template_argument_clash;

struct ThisIsVeryVeryVeryLongNamedStructure
{
    string data;
};

struct ThisIsVeryVeryVeryLongNamedStructure_
{
    int32 data;
};

struct TemplatedStruct<T, U, V>
{
    T field1;
    U field2;
    V field3;
};

struct StructLongTemplateArgumentClash
{
    TemplatedStruct<ThisIsVeryVeryVeryLongNamedStructure,
                    ThisIsVeryVeryVeryLongNamedStructure,
                    ThisIsVeryVeryVeryLongNamedStructure> structNameOverflow;
    TemplatedStruct<ThisIsVeryVeryVeryLongNamedStructure,
                    ThisIsVeryVeryVeryLongNamedStructure,
                    ThisIsVeryVeryVeryLongNamedStructure_> structNameClash;
};
