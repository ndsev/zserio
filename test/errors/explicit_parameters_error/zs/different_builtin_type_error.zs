package different_builtin_type_error;

struct Blob(uint32 count)
{
    uint32 array[count];
};

subtype uint8 Count;

struct OtherBlob(Count count)
{
    uint32 array[count];
};

sql_table Table
{
    uint32                    id sql "PRIMARY KEY NOT NULL";
    Blob(explicit count)      blob1;
    Blob(explicit count)      blob2;
    OtherBlob(explicit count) otherBlob;
};

sql_database Database
{
    Table table;
};
