package sql_tables_warning.not_null_primary_key_warning;

sql_table NotNullPrimaryKeyTable1
{
    int32       schoolId    sql "NULL";
    int32       classId;
    int32       studentId;

    sql "PRIMARY KEY (schoolId)";
};

sql_table NotNullPrimaryKeyTable2
{
    int32       schoolId    sql "PRIMARY KEY NULL";
    int32       classId;
    int32       studentId;
};

sql_database NotNullPrimaryKeyDatabase
{
    NotNullPrimaryKeyTable1 notNullPrimaryKeyTable1;
    NotNullPrimaryKeyTable2 notNullPrimaryKeyTable2;
};
