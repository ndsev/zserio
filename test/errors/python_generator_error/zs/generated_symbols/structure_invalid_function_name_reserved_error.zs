package generated_symbols.structure_invalid_function_name_reserved_error;

struct TestStructure
{
    uint32 field;

    function uint32 __eq__() // starts with '_'
    {
        return field;
    }
};
