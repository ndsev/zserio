package errors.structure_indicator_property_clash_error;

struct TestStructure
{
    optional uint32 field;
    string isFieldSet; // clashes with generated indicator method
};
