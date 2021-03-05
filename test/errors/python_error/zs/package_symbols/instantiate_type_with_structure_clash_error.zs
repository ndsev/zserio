package package_symbols.instantiate_type_with_structure_clash_error;

struct Some_Name
{
    uint32 field;
};

struct Some<T>
{
    T field;
};

instantiate Some<string> SomeName;
