package instantiate_subtype_error;

struct Test<T>
{
    T value;
};

subtype Test<uint32> T32;
instantiate T32 T;
