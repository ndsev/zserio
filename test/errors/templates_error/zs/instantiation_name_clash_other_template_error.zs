package instantiation_name_clash_other_template_error;

struct Test_A<T>
{
    T value;
};

struct Test<T>
{
    T value;
};

struct A_uint32
{
    uint32 value;
};

struct InstantiationNameClashOtherTemplate
{
    Test_A<uint32> t1;
    Test<A_uint32> t2;
};
