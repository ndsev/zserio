package templated_template_parameter_error;

struct TestStruct<T>
{
    T<uint32> value;
};

struct TemplatedTemplateParameterError
{
    TestStruct<string> test;
};
