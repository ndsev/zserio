package comments_warning.unresolved_markdown_see_tag_reference;

/*!
Markdown link to zserio source within current source tree will be converted
to classic doc see tag, which should fire a warning during resolution.

See [Unknown](unknown.zs#Unknown).
!*/
struct Test
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
