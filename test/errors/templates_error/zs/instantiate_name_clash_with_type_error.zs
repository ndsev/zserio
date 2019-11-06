package instantiate_name_clash_with_type_error;

struct Other
{
    uint32 value;
};

struct Test<T>
{
    T value;
};

instantiate Test<uint32> Other;
