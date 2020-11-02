package comments_warning.unresolved_see_tag_reference;

// '@see' is used for contants as well intentionally to check if it works even if constants do not have scope.

/**
 * Constant for number 10.
 *
 * @see "Wrong link to unexisting type." Unexisting
 */
const int32 VALUE = 10;

/**
 * Test structure.
 *
 * @see "Wrong link to unexisting symbol." Table.Unexisting
 */
struct Test
{
    int32 test;
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
