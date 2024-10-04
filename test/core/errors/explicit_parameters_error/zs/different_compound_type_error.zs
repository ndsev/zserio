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

struct OtherBlob(OtherHeader otherHeader)
{
    uint32 array[otherHeader.count];
};

sql_table Table
{
    uint32                          id sql "PRIMARY KEY NOT NULL";
    Blob(explicit headerParam)      blob1;
    Blob(explicit headerParam)      blob2;
    OtherBlob(explicit headerParam) otherBlob;
};

sql_database Database
{
    Table table;
};
