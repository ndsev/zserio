package sql_tables.multiple_pk_table;

sql_table MultiplePkTable
{
    int32       blobId sql "NOT NULL";
    int32       age sql "NOT NULL";
    string      name;

    sql         "PRIMARY KEY(blobId, age)";
};
