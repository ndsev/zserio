package module_names.instantiation_with_structure_clash_error;

struct SomeName
{
    Some<Name> field;
};

struct Some<T>
{
    T field;
};

struct Name
{
    string name;
};
