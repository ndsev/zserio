package sql_tables_warning.not_first_primary_key_warning;

sql_table NotFirstPrimaryKeyTable
{
    int32       schoolId;
    int32       classId sql "PRIMARY KEY NOT NULL";
    int32       studentId;
};

sql_database NotFirstPrimaryKeyDatabase
{
    NotFirstPrimaryKeyTable notFirstPrimaryKeyTable;
};
