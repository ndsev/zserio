package instantiation_name_clash_with_type_error;

struct Test_uint32
{
    uint32 value;
};

struct Test<T>
{
    T value;
};

struct InstantiationNameClashWithTypeError
{
    Test<uint32> value;
};
