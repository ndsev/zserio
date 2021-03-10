package scope_symbols.structure_function_names_clash_error;

struct TestStructure
{
    function bool some_name()
    {
        return true;
    }

    function bool someName()
    {
        return false;
    }
};
