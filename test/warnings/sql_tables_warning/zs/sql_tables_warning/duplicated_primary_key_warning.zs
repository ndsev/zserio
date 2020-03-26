package sql_tables_warning.duplicated_primary_key_warning;

sql_table DuplicatedPrimaryKeyTable
{
    int32       schoolId    sql "PRIMARY KEY NOT NULL";
    int32       classId     sql "PRIMARY KEY NOT NULL";
    int32       studentId;
};

sql_database DuplicatedPrimaryKeyDatabase
{
    DuplicatedPrimaryKeyTable   duplicatedPrimaryKeyTable;
};
