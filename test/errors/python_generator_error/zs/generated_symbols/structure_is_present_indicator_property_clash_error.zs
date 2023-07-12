package generated_symbols.structure_is_present_indicator_property_clash_error;

struct TestStructure
{
    extend uint32 field;
    extend string isFieldPresent; // clashes with generated indicator method
};
