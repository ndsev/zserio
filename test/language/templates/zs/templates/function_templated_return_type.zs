package templates.function_templated_return_type;

struct Holder<T>
{
    T value;
};

struct TestStructure<T>(bool hasHolder)
{
    T value if !hasHolder;
    Holder<T> holder if hasHolder;

    function T get()
    {
        return hasHolder ? holder.value : value;
    }
};

struct FunctionTemplatedReturnType
{
    bool                             hasHolder;
    TestStructure<uint32>(hasHolder) uint32Test;
    TestStructure<string>(hasHolder) stringTest;
};
