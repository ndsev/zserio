package templates.instantiate_with_instantiate_template_argument;

struct Data<T>
{
    T field;
};

instantiate Data<uint8> Data8;
instantiate Data<uint32> Data32;

struct Other<T>
{
    T field;
};

// implemented to test the bugfix of https://github.com/ndsev/zserio/issues/372
// Other<Data8> must be correctly distinguished from Other<Data32>
instantiate Other<Data8> Other8;
instantiate Other<Data32> Other32;

struct InstantiateWithInstantiateTemplateArgument
{
    Other8 other8;
    Other32 other32;
};
