package module_names.instantiate_type_with_structure_clash_error;

struct Some_Name
{
    uint32 field;
};

struct Other<T>
{
    T field;
};

instantiate Other<string> SomeName;
