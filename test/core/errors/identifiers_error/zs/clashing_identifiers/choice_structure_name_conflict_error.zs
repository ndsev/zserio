package clashing_identifiers.choice_structure_name_conflict_error;

choice Test(bool variant) on variant
{
    case true:
        int32 field32;
    default:
        int16 field16;
};

struct Test // Test is already defined!
{
    string field;
};
