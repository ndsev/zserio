package different_compound_type_error;

struct Header
{
    uint32 count;
};

struct OtherHeader
{
    uint32 count;
};

struct Blob(Header header)
{
    uint32 array[header.count];
};

struct OtherBlob(OtherHeader header)
{
    uint32 array[header.count];
};

sql_table Table
{
    uint32                     id sql "PRIMARY KEY";
    Blob(explicit header)      blob1;
    Blob(explicit header)      blob2;
    OtherBlob(explicit header) otherBlob;
};

sql_database Database
{
    Table table;
};
