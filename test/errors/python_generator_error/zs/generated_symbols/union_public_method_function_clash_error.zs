package generated_symbols.union_public_method_function_clash_error;

union TestUnion
{
    string value1;

    function uint32 bitsizeof() // clashes with generated API
    {
        return 0;
    }
};
