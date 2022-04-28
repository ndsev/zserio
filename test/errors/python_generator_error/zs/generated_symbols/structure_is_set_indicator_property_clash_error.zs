package generated_symbols.structure_is_set_indicator_property_clash_error;

struct TestStructure
{
    optional uint32 field;
    string isFieldSet; // clashes with generated indicator method
};
