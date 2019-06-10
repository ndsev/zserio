package sql_tables.complex_table;

struct TestBlob(uint32 count)
{
    uint32  offsetEnd;
    bit:3   values[count];

offsetEnd:
    bool    end;
};

enum int8 TestEnum
{
    UNDEFINED   = 0,
    RED         = 1,
    BLUE        = 2,
    BLACK       = 3
};

sql_table ComplexTable
{
    // uint64 type is important to test Java which has no 64-bits unsigned integer type
    uint64                      blobId  sql "PRIMARY KEY";
    // int64 type is important to test C++ which has special API for 64-bits types (e.g. sqlite3_column_int64)
    int64                       age     sql "NULL";
    string                      name    sql "NULL";
    bool                        isValid sql "NULL";
    float16                     salary  sql "NULL";
    float64                     bonus   sql "NULL";
    bit:5                       value   sql "NULL";
    TestEnum                    color   sql "NULL";
    TestBlob(explicit count)    blob    sql "NULL";
};
