package clashing_identifiers.clashing_sql_table_column_names_error;

sql_table Table
{
    uint8 fieldABC sql "PRIMARY KEY NOT NULL";
    uint8 fieldabc;
};
