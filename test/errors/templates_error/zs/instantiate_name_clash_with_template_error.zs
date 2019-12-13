package instantiate_name_clash_with_template_error;

struct Test<T>
{
    T value;
};

// This should be the first one to check that Test<uint32> instantiation won't use hash name alternative.
instantiate Test<string> Test_uint32;

struct InstantiateNameClashWithTemplateError
{
    Test<uint32> t;
};
