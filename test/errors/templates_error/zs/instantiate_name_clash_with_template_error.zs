package instantiate_name_clash_with_template_error;

struct Template<T>
{
    T field;
};

instantiate Template<uint32> Template;
