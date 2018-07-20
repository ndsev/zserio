package sql_tables_warning.no_primary_key_warning;

sql_table NoPrimaryKeyTable
{
    int32       schoolId;
    int32       classId;
    int32       studentId;
};

sql_database NoPrimaryKeyDatabase
{
    NoPrimaryKeyTable   noPrimaryKeyTable;
};
