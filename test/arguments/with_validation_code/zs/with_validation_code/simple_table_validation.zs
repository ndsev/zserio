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
    uint64   rowid sql "PRIMARY KEY NOT NULL"; // this is aliased by SQLite to rowid
    bool     fieldBool sql "NOT NULL";
    bit:5    fieldNonBlob;
    RootStruct(explicit localCount1) fieldBlob sql "NOT NULL";
    TestEnum fieldEnum sql "NOT NULL";
};

sql_database SimpleTableValidationDb
{
    SimpleTable simpleTable;
};
