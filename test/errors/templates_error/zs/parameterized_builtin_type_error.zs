package parameterized_builtin_type_error;

struct TestStruct<T>
{
    uint32   param;
    T(param) parameterized;
};

struct ParameterizedBuiltinType
{
    TestStruct<uint32> test;
};
