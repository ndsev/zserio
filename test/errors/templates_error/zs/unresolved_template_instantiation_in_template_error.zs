package unresolved_template_instantiation_in_template_error;

struct InnerStruct<T>
{
    Unresolved<T> value;
};

struct TestStruct<T>
{
    InnerStruct<T> value;
};

struct UnresolvedTemplateInstantiationInTemplate
{
    TestStruct<uint32> test;
};
