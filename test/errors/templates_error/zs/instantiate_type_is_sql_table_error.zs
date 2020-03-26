package instantiate_type_is_sql_table_error;

sql_table SqlTable<T>
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    T data;
};

instantiate SqlTable<string> StrTable;

struct InstantiateTypeIsSqlTable
{
    StrTable field;
};
