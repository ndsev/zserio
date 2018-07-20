package database_array_field_error;

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

sql_table DatabaseArrayFieldError
{
    int32           size;
    // This must cause an error. SQL database cannot be instantiated.
    TestDatabase    testDatabaseArray[size];
};
