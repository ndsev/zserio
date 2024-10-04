package instantiate_name_clash_error;

struct Test<T>
{
    T value;
};

struct Other<T>
{
    T value;
};

instantiate Test<uint32> U32;
instantiate Other<uint32> U32;
