package sql_without_rowid_tables.rowid_and_without_rowid_tables;

// This example contains mixture of without_rowid and ordinary rowid tables. This caused an compilation
// error in old version of Zserio due to the bug in generated SQL databases which called createOrdinaryRowIdTable
// method on ordinary rowid tables.

sql_table WithoutRowIdTable
{
    string      word sql "PRIMARY KEY";
    uint32      count;

    sql_without_rowid;
};

sql_table OrdinaryRowIdTable
{
    string      word sql "PRIMARY KEY";
    uint32      count;
};

sql_database RowIdAndWithoutRowIdDb
{
    WithoutRowIdTable   withoutRowIdTable;
    OrdinaryRowIdTable  ordinaryRowIdTable;
};
