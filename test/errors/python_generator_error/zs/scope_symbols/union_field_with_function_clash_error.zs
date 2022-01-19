package scope_symbols.union_field_with_function_clash_error;

union TestUnion
{
    uint32 some_field;

    function uint32 someField()
    {
        return some_field;
    }
};
