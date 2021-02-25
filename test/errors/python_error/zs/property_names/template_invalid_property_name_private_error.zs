package property_names.template_invalid_property_name_private_error;

struct TemplatedStruct<T>
{
    T _field;
};

instantiate TemplatedStruct<string> StringStruct;
