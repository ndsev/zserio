package generated_symbols.structure_is_set_indicator_function_clash_error;

struct TestStructure
{
    optional uint32 field;

    function bool isFieldSet() // clashes with generated indicator method
    {
        return false;
    }
};
