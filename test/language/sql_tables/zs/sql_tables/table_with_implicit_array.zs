package sql_tables.table_with_implicit_array;

sql_database TestDB
{
    TableWithImplicit tableWithImplicit; // intentionally not last to check that it pass through check in core
    AnotherTable anotherTable;
};

sql_table TableWithImplicit
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    StructWithImplicit structWithImplicit; // intentionally not last to check that it pass through check in core
    string text;
};

sql_table AnotherTable
{
    uint32 id sql "PRIMARY KEY NOT NULL";
};

struct StructWithImplicit
{
    implicit uint32 array;
};
