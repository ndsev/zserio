package package_symbols.sql_database_with_sql_table_clash_error;

sql_table Some_Good_Name
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    string text;
};

sql_database SomeGoodName
{
    Some_Good_Name someGoodNameTable;
};
