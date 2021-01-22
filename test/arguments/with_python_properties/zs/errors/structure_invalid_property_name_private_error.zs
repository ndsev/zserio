package errors.structure_invalid_property_name_private_error;

struct TestStructure
{
    uint32 field;
    uint32 _field; // starts with '_' (and yet clashes with private member for the first field)
};
