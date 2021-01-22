package errors.structure_function_property_clash_error;

struct TestStructure
{
    string funcTest; // clashes with generated function method

    function bool test()
    {
        return true;
    }
};
