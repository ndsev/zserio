package generated_symbols.structure_is_present_indicator_function_clash_error;

struct TestStructure
{
    extend uint32 field;

    function bool isFieldPresent() // clashes with generated indicator method
    {
        return false;
    }
};
