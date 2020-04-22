package clashing_identifiers.const_structure_name_conflict_error;

const int32 Test = 13;

struct Test // Test is already defined!
{
    string field;
};

