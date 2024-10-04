package instantiate_duplicated_name_error;

struct Test<T>
{
    T value;
};

instantiate Test<string> Str;
instantiate Test<uint8_t> Str;
