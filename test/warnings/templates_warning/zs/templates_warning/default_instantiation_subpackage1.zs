package templates_warning.default_instantiation_subpackage1;

struct Subpackage1Template<T>
{
    T field;
};

instantiate Subpackage1Template<uint32> Subpackage1TemplateU32;
