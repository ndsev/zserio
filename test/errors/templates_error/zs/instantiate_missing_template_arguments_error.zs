package instantiate_missing_template_arguments_error;

struct Test<T>
{
    T value;
};

instantiate Test U32;
