package errors.union_function_property_clash_error;

union TestUnion
{
    string funcMyFunc; // clashes with generated function method

    function string myFunc()
    {
        return funcMyFunc;
    }
};
