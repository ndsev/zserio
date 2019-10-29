package instantiate_no_template_error;

struct Test
{
    uint32 value;
};

instantiate Test<uint32> U32Test;
