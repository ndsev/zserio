package parameterized_compound_type_error;

struct Compound
{
    uint32 value;
};

struct TestStruct<T>
{
    uint32   param;
    T(param) parameterized;
};

struct ParameterizedCompoundTypeError
{
    TestStruct<Compound> test;
};
