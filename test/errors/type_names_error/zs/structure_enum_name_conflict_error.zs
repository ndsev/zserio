package structure_enum_name_conflict_error;

struct Test
{
    int32 field1;
};

enum int32 Test // Test is already defined!
{
    ITEM1 = 10
};
