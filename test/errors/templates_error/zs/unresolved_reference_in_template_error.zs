package unresolved_reference_in_template_error;

struct TestStruct<T>
{
    Unresolved value;
};

struct UnresolvedReferenceInTemplate
{
    TestStruct<uint32> test;
};
