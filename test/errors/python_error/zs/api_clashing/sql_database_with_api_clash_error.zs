package api_clashing.sql_database_with_api_clash_error;

sql_table TestTable
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    string text;
};

sql_database Api
{
    TestTable testTable;
};
