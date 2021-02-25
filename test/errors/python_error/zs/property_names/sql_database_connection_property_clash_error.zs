package property_names.sql_database_connection_property_clash_error;

sql_table TestTable
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    string text;
};

sql_database TestDatabase
{
    TestTable connection; // clashes with generated API
};
