package templates.instantiate_type_as_sql_database_field;

sql_table Test<T>
{
    uint32 id sql "PRIMARY KEY";
    T data;
};

sql_database InstantiateTypeAsSqlDatabaseFieldDb
{
    StringTable stringTable;
    Test<string> otherStringTable;
};

instantiate Test<string> StringTable;
