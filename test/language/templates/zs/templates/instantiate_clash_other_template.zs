package templates.instantiate_clash_other_template;

struct Test<T>
{
    T value;
};

// This should be the first one to check that Test<uint32> instantiation won't use hash name alternative.
instantiate Test<string> Test_uint32;

struct InstantiateClashOtherTemplate
{
    Test<uint32> test;
};
