package clashing_identifiers.subtype_structure_name_conflict_error;

subtype int32 Test;

struct Test // Test is already defined!
{
    string field1;
};
