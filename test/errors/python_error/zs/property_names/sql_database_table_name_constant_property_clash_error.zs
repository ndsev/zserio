package property_names.sql_database_table_name_constant_property_clash_error;

sql_table TestTable
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    string text;
};

sql_database TestDatabase
{
    TestTable first;
    TestTable TABLE_NAME_first; // clashes with generated API
};
