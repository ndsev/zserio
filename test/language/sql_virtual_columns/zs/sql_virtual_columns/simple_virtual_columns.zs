package sql_virtual_columns.simple_virtual_columns;

sql_table SimpleVirtualColumnsTable using fts3
{
    sql_virtual string  content;
};

sql_database SimpleVirtualColumnsDb
{
    SimpleVirtualColumnsTable simpleVirtualColumnsTable;
};
