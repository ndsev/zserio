package symbol_with_template_parameter_clash_error;

struct TestStruct<T>
{
    uint32 T;
};

struct SymbolWithTemplateParameterClash
{
    TestStruct<uint32> test;
};
