package with_validation_code.simple_table_validation;

struct RootStruct(uint32 count)
{
    uint32  offsetEnd;
    uint8   filler[count];
offsetEnd:
    uint8   end;
};

const int32 N_ITEMS = 10;

enum int64 TestEnum
{
    NONE  = 0,
    RED   = 2,
    BLUE  = 4,
    BLACK = 7
};

sql_table SimpleTable
{
    uint64   rowid           sql "PRIMARY KEY"; // this is aliased by SQLite to rowid (NOT NULL is added by Zserio)
    bool     fieldBool;
    bit:5    fieldNonBlob    sql "NULL";
    RootStruct(explicit localCount1) fieldBlob;
    TestEnum fieldEnum;
};

sql_database SimpleTableValidationDb
{
    SimpleTable simpleTable;
};
