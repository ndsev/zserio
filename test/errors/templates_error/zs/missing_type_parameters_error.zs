package missing_type_parameters_error;

struct TestStruct<T>
{
    T value;
};

struct Parameterized(uint32 param)
{
    uint32 array[param];
};

struct MissingTypeParameters
{
    TestStruct<Parameterized> test;
};
