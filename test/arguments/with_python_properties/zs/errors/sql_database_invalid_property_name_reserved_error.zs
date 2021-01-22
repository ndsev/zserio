package errors.sql_database_invalid_property_name_reserved_error;

sql_table TestTable
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    string text;
};

sql_database TestDatabase
{
    TestTable __init__; // starts with '_'
};
