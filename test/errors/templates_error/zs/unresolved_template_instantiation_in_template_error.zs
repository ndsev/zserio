package unresolved_template_instantiation_in_template_error;

struct TestStruct<T>
{
    Unresolved<T> value;
};

struct UnresolvedTemplateInstantiationInTemplate
{
    TestStruct<uint32> test;
};
