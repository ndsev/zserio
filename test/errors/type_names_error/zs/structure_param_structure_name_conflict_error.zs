package structure_param_structure_name_conflict_error;

struct Test
{
    string field;
};

struct Test(uint32 lenght) // Test is already defined!
{
    int32 array[length];
};
