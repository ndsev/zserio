package simple_database;

sql_table SimpleTable
{
    int32       schoolId    sql "PRIMARY KEY (schoolId)";
    int32       classId;
    int32       studentId;
};

sql_database SimpleDatabase
{
    SimpleTable simpleTable;
};
