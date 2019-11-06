package templates.instantiate_template_argument;

struct Test<T>
{
    T value;
};

struct Other<T>
{
    T value;
};

struct InstantiateTemplateArgument
{
    // note that Test<string> should be named as Str, therefor the generated name for Other<Test<string>>
    // should be Other_Str, not Other_Test_string
    Other<Test<string>> other;
};

// define at the end to check correct template argument resolution
instantiate Test<string> Str;
