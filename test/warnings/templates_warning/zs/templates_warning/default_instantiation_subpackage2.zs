package templates_warning.default_instantiation_subpackage2;

struct Subpackage2Template<T>
{
    T field;
};

instantiate Subpackage2Template<uint32> Subpackage2TemplateU32;
instantiate Subpackage2Template<string> Subpackage2TemplateSTR;
