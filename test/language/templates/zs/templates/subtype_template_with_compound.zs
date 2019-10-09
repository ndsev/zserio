package templates.subtype_template_with_compound;

struct Compound
{
    uint32 value;
};

struct TemplateCompound<T>
{
    T value;
};

struct TestStructure<T1, T2>
{
    T1 value1;
    T2 value2;
};

// not used in zserio to check that the template is instantiated just because of the subtype
subtype TestStructure<Compound, TemplateCompound<Compound>> SubtypeTemplateWithCompound;
