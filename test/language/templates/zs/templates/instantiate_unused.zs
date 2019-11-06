package templates.instantiate_unused;

struct Test<T>
{
    T value;
};

instantiate Test<uint32> U32; // check that the instantiation is generated
