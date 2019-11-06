package templates.instantiate_nested_template;

struct Nested<T>
{
    T value;
};

struct Test<T>
{
    Nested<T> value;
};

struct InstantiateNestedTemplate
{
    Test<string> test;
};

instantiate Test<string> TStr;
instantiate Nested<string> NStr;
