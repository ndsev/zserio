package comments.documentation.sql_database_comments;

/** Sql table comment. */
sql_table Table
{
    /** Id comment. */
    int32 id sql "PRIMARY KEY NOT NULL";
};

/** DB comment. */
sql_database Db
{
    /** Table field comment. */
    Table table;
};
