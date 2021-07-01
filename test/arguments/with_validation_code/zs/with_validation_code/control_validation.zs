package with_validation_code.control_validation;

sql_table Table1
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    varint16 field sql "NOT NULL";
};

struct Blob
{
    varint32 field;
};

sql_table Table2
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    Blob blob sql "NOT NULL";
};

sql_table Table3
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    varint64 field sql "NOT NULL";
};

sql_database ControlValidationDb
{
    Table1 table1;
    Table2 table2;
    Table3 table3;
};
