package sql_without_rowid_tables_warning.integer_primary_key_warning;

sql_table WithoutRowIdTable
{
    int32       value sql "PRIMARY KEY";
    uint32      count;

    sql_without_rowid;
};

sql_database WithoutRowIdDatabase
{
    WithoutRowIdTable   withoutRowIdTable;
};
