package with_validation_code.simple_table_validation;

struct RootStruct(uint32 count)
{
    uint32  offsetEnd;
    uint8   filler[count];
offsetEnd:
    uint8   end;
};

enum int64 TestEnum
{
    NONE  = 1, // do not use 0 to check that NULL sqlite value is properly skipped during validation
    RED   = 2,
    BLUE  = 4,
    BLACK = 7
};

bitmask uint8 TestBitmask
{
    NONE  = 000b,
    READ  = 010b,
    WRITE = 100b,
    CREATE = 111b
};

sql_table SimpleTable
{
    uint64         rowid sql "PRIMARY KEY NOT NULL"; // this is aliased by SQLite to rowid
    bool           fieldBool sql "NOT NULL";
    bit:5          fieldBit5;
    bit<fieldBit5> fieldDynamicBit sql "NOT NULL";
    varint16       fieldVarInt16 sql "NOT NULL";
    string         fieldString sql "NOT NULL";
    RootStruct(explicit localCount1) fieldBlob sql "NOT NULL";
    TestEnum       fieldEnum;
    TestBitmask    fieldBitmask sql "NOT NULL";
};

sql_database SimpleTableValidationDb
{
    SimpleTable simpleTable;
};
