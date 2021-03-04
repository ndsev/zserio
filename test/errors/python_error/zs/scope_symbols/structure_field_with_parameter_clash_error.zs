package scope_symbols.structure_field_with_parameter_clash_error;

struct TestStruct(bool someIdentifier)
{
    uint32 some_identifier;
};
