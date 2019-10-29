package instantiate_duplicated_via_import_error.pkg;

struct Test<T>
{
    T value;
};

instantiate Test<uint32> UTest;
