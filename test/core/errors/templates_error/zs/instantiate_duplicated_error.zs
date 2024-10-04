package instantiate_duplicated_error;

struct Test<T>
{
    T value;
};

instantiate Test<uint32> UTest;
instantiate Test<uint32> U32Test;
