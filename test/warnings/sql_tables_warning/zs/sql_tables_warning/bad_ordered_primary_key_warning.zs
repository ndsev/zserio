package sql_tables_warning.bad_ordered_primary_key_warning;

sql_table BadOrderedPrimaryKeyTable
{
    int32       schoolId;
    int32       classId;
    int32       studentId;

    sql "PRIMARY KEY (classId)";
};

sql_database BadOrderedPrimaryKeyDatabase
{
    BadOrderedPrimaryKeyTable   badOrderedPrimaryKeyTable;
};
