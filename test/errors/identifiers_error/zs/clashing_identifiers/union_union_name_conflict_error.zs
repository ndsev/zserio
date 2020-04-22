package clashing_identifiers.union_union_name_conflict_error;

union Test
{
    string fieldString;
    uint8 feildCharArray[255];
};

union Test // Test is already defined!
{
    int32 field32;
    int64 field64;
};
