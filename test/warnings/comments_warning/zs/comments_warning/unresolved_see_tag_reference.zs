package comments_warning.unresolved_see_tag_reference;

/**
 * Test structure.
 *
 * @see "Wrong link to unexisting type" Unexisting.
 */
struct Test
{
    int32 test;
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
