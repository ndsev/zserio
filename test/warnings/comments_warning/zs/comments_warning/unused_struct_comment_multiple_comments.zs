package comments_warning.unused_struct_comment_multiple_comments;

/** Unused struct comment. */
/** Used struct comment. */
struct Test
{
    int32 field;
};

sql_table Table
{
    int32 id sql "PRIMARY KEY";
    Test test;
};

sql_database Database
{
    Table table;
};
