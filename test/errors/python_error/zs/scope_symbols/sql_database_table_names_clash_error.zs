package scope_symbols.sql_database_table_names_clash_error;

sql_table TestTable
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    string text;
};

sql_database TestDatabase
{
    TestTable testTable;
    TestTable test_table;
};
