package templates.instantiate_sql_table;

sql_table TestTable<T>
{
    T id sql "PRIMARY KEY";
    string info;
};

instantiate TestTable<uint32> U32Table;
