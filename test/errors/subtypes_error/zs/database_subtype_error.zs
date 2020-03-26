package database_subtype_error;

sql_table TestTable
{
    int32 field sql "PRIMARY KEY NOT NULL"; 
};

sql_database TestDatabase
{
    TestTable table;
};

subtype TestDatabase SubtypedSqlDatabase; // SQL Database cannot be used as a type!
