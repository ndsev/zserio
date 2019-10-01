package constant_used_as_type_error;

struct TestStruct<T>
{
    T value;
};

const uint32 CONST = 13;

struct ConstantUsedAsType
{
    TestStruct<CONST> test;
};
