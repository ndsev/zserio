package clashing_identifiers.clashing_sql_database_table_names_error;

sql_table Table
{
    uint8 pk sql "PRIMARY KEY NOT NULL";
};

sql_database Db
{
    Table tbl_x;
    Table tBl_x;
};
