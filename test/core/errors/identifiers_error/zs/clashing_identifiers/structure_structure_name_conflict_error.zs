package clashing_identifiers.structure_structure_name_conflict_error;

struct Test
{
    int32 field1;
};

struct Test // Test is already defined!
{
    string field1;
};
