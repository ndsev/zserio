package unknown_unique_column_error;

sql_table TestTable
{
    int32       schoolId    sql "PRIMARY KEY NOT NULL";
    int32       classId;
    int32       studentId;

    sql "UNIQUE (unknownColumn)";
};
