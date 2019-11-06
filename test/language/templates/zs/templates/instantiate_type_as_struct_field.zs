package templates.instantiate_type_as_struct_field;

struct Test<T>
{
    T value;
};

struct InstantiateTypeAsStructField
{
    Test32 test;
};

// define at the end to check correct instantiate type resolution
instantiate Test<uint32> Test32;
