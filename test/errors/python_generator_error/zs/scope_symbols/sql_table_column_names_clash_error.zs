package scope_symbols.sql_table_column_names_clash_error;

sql_table TestTable
{
    uint32 someId sql "PRIMARY KEY NOT NULL";
    string some_id;
};
