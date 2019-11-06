package templates.instantiate_type_as_sql_table_field;

struct Test<T>
{
    T value;
};

sql_table Test32Table
{
    uint32 id sql "PRIMARY KEY";
    Test32 test;
};

instantiate Test<uint32> Test32;
