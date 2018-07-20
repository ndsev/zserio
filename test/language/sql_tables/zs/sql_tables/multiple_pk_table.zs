package sql_tables.multiple_pk_table;

sql_table MultiplePkTable
{
    int32       id;
    int32       age;
    string      name;

    sql         "PRIMARY KEY(id, age)";
};
