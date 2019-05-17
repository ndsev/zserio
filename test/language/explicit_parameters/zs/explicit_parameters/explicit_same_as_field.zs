package explicit_parameters.explicit_same_as_field;

struct TestBlob(uint32 count)
{
    bit:3   values[count];
};

// this test checks that explicit parameter can have same name as a field and ensures that
// parameter provider is generated when explicit keyword is used
sql_table SameAsFieldTable
{
    uint32                     id          sql "PRIMARY KEY";
    string                     name;
    uint32                     count;
    TestBlob(count)            blob;
    TestBlob(explicit count)   blobExplicit;
};
