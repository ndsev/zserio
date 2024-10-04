package field_not_available_in_function_error;

struct FieldNotAvailable<T>
{
    uint32 field1 : field1 < getField2();
    T field2;

    function uint32 getField2()
    {
        return field2;
    }
};

struct TestStruct<T>
{
    FieldNotAvailable<T> fieldNotAvailable;
};

struct FieldNotAvailableInFunctionError
{
    TestStruct<uint64> test;
};
