package sql_tables.multiple_pk_table;

sql_table MultiplePkTable
{
    int32       blobId;
    int32       age;
    string      name;

    sql         "PRIMARY KEY(blobId, age)";
};
