package clashing_identifiers.structure_union_name_conflict_error;

struct Test
{
    string field;
};

union Test // Test is already defined!
{
    int32 field32;
    int64 field64;
};
