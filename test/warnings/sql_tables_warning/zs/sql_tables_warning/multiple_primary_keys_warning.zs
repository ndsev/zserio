package sql_tables_warning.multiple_primary_keys_warning;

sql_table MultiplePrimaryKeysTable
{
    int32       schoolId sql "PRIMARY KEY NOT NULL";
    int32       classId;
    int32       studentId;

    sql "PRIMARY KEY(schoolId)";
};

sql_database MultiplePrimaryKeysDatabase
{
    MultiplePrimaryKeysTable    multiplePrimaryKeysTable;
};
