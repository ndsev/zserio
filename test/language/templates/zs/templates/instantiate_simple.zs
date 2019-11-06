package templates.instantiate_simple;

struct Test<T>
{
    T value;
};

struct InstantiateSimple
{
    Test<uint32> test;
};

instantiate Test<uint32> U32;
