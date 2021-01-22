package errors.sql_database_public_method_property_clash_error;

sql_table TestTable
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    string text;
};

sql_database TestDatabase
{
    TestTable fromFile; // clashes with generated API
};
