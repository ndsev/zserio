package duplicated_field_name_error;

struct TestStruct<T>
{
    T value;
    T value;
};

struct DuplicatedFieldName
{
    TestStruct<uint32> test;
};
