package templates.instantiate_via_import.pkg;

struct Test<T>
{
    T value;
};

instantiate Test<uint32> U32;
