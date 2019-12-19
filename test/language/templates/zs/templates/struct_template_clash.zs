package templates.struct_template_clash;

struct A_B
{
    uint32 value;
};

struct B_C
{
    string value;
};

struct A
{
    uint32 value;
};

struct C
{
    bool value;
};

struct Template<T1, T2>
{
    T1 value1;
    T2 value2;
};

struct TestStruct<T>
{
    T value;
    Template<A_B, C> t1;
    Template<A, B_C> t2;
};

struct InstantiationNameClash
{
    TestStruct<uint32> test;
};
