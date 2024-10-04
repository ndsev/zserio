package clashing_identifiers.choice_union_name_conflict_error;

choice Test(bool variant) on variant
{
    case true:
        int32 field32;
    default:
        int16 field16;
};

union Test // Test is already defined!
{
    int32 field32;
    int64 field64;
};
