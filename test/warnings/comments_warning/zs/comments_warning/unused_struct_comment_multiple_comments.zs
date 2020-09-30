package comments_warning.unused_struct_comment_multiple_comments;

/** Used struct comment. */
/** Used struct comment. */
struct  /** Unused struct comment. */  Test
{
    int32 field;
};

sql_table Table
{
    int32 id sql "PRIMARY KEY NOT NULL";
    Test test;
};

sql_database Database
{
    Table table;
};
