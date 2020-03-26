package sql_without_rowid_tables.simple_without_rowid_table;

sql_table SimpleWithoutRowIdTable
{
    string      word sql "PRIMARY KEY NOT NULL";
    uint32      count;

    sql_without_rowid;
};

sql_database SimpleWithoutRowIdDb
{
    SimpleWithoutRowIdTable simpleWithoutRowIdTable;
};
