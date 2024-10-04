package database_field_error;

sql_table TestTable
{
    int32       schoolId;
    int32       classId;
    int32       studentId;

    sql "PRIMARY KEY (schoolId)";
};

sql_database TestDatabase
{
    TestTable   testTable;
};

struct DatabaseFieldError
{
    // This must cause an error. SQL database cannot be instantiated.
    TestDatabase    testDatabase;
};
