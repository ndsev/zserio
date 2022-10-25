package templates_warning.default_instantiation_subpackage3;

struct Subpackage3InnerTemplate<T>
{
    T field;
};

struct Subpackage3Template<T>
{
    Subpackage3InnerTemplate<T> field;
};

