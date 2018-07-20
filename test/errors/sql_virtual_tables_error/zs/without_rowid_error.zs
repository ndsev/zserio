package without_rowid_error;

sql_table TestTable using fts4
{
    int32       schoolId;
    int32       classId;
    int32       studentId;

    sql "PRIMARY KEY (schoolId)";

    // this is forbidden in virtual tables
    sql_without_rowid;
};
