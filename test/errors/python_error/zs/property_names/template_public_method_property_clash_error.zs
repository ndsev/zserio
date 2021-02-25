package property_names.template_public_method_property_clash_error;

choice TemplatedChoice<T>(uint8 write) on write // write clashes with generated API
{
    case 1:
        T data[];
    default:
        ;
};

struct Test
{
    TemplatedChoice<string>(1) writerChoice;
};
