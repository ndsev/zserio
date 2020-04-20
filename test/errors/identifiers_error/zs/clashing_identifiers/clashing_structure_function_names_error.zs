package clashing_identifiers.clashing_structure_function_names_error;

struct Structure
{
    function uint8 func1()
    {
        return 10;
    }

    function uint8 Func1()
    {
        return 11;
    }
};
