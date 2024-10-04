package missing_template_arguments_error;

struct TestStruct<T>
{
    T value;
};

struct MissingTemplateArgument
{
    TestStruct test;
};
