package too_many_arguments_error;

struct TestStruct<T1, T2>
{
    T1 value1;
    T2 value2;
};

struct OtherStruct<T>
{
    TestStruct<T, T, T> test;
};

struct WrongNumberOfArguments
{
    OtherStruct<uint32> test;
};
