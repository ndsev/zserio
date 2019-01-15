package parameterized_table_error;

sql_table TestTable(bool badParameter)
{
    int32       schoolId;
    int32       classId;
    int32       studentId;

    sql "PRIMARY KEY (schoolId)";
};
