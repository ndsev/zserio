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
    uint32                     id          sql "PRIMARY KEY";
    string                     name;
    TestBlob(explicit header)  blob1;
    TestBlob(explicit blob)    blob2;
    TestBlob(explicit header)  blob3; // header is reused here
};
