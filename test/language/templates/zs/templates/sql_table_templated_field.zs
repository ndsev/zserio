package templates.sql_table_templated_field;

sql_table TemplatedTable<T>
{
    uint32  id      sql "PRIMARY KEY NOT NULL";
    Data<T> data;
};

struct Data<T>
{
    T plainData;
};

union Union
{
    uint32  value32;
    uint64  value64;
    string  valueString;
};

sql_database SqlTableTemplatedFieldDb
{
    TemplatedTable<uint32>  uint32Table;
    TemplatedTable<Union>   unionTable;
};
