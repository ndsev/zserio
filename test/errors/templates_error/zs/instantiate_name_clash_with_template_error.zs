package instantiate_name_clash_with_template_error;

struct Test<T>
{
    T value;
};

struct InstantiateNameClashWithTemplateError
{
    Test<uint32> t;
};

instantiate Test<string> Test_uint32;
