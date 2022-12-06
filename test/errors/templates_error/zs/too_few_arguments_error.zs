package too_few_arguments_error;

struct TestStruct<T1, T2>
{
    T1 value1;
    T2 value2;
};

struct WrongNumberOfArguments
{
    TestStruct<uint32> test;
};
