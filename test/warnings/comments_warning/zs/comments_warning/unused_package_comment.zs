/** Unused package comment. */
package comments_warning.unused_package_comment;

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
