package hashed_template_name_clash_error;

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

struct HashedTemplateNameClashError
{
    Test_A<uint32> t1;
    Test<A_uint32> t2;
};

// This will clash with hashed Test<A_uint32> instantiation name.
struct Test_A_uint32_F2945EA9
{
    bool value;
};
