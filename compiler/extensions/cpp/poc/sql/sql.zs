struct Data
{
    uint32 len;
    uint32 array[len];
};

sql_table Table
{
    uint32  pk      sql "PRIMARY KEY";
    Data    data    sql "NULL";
};

sql_database Database
{
    Table tbl;
};
