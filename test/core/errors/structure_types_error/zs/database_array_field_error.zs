package database_array_field_error;

sql_table TestTable
{
    int32       schoolId;
    int32       classId;
    int32       studentId;

    sql "PRIMARY KEY (schoolId)";
};

sql_database TestDatabaseForArray
{
    TestTable   testTable;
};

struct DatabaseArrayFieldError
{
    // This must cause an error. SQL database cannot be instantiated.
    TestDatabaseForArray    testDatabaseArray[10];
};
