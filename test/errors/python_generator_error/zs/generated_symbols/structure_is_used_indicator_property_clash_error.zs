package generated_symbols.structure_is_used_indicator_property_clash_error;

struct TestStructure
{
    optional uint32 field;
    string isFieldUsed; // clashes with generated indicator method
};
