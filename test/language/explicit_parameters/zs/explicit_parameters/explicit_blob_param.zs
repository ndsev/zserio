package explicit_parameters.explicit_blob_param;

struct Header
{
    uint32 count;
};

struct TestBlob(Header blob) // blob name used to check clashing with an internal variable in readRow
{
    bit:3   values[blob.count];
};

sql_table BlobParamTable
{
    uint32                          id      sql "PRIMARY KEY NOT NULL";
    string                          name;
    TestBlob(explicit headerParam)  blob1; // headerParam name used to check conversion to snake_case in Python
    TestBlob(explicit blob)         blob2;
    TestBlob(explicit headerParam)  blob3; // headerParam is reused here
};
