package templates.instantiate_type_as_struct_array_field;

struct Test<T>
{
    T value;
};

instantiate Test<uint32> Test32;

struct InstantiateTypeAsStructArrayField
{
    Test32 test[];
};
