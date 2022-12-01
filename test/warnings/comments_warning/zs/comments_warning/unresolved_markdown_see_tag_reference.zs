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

/*! One liner to check link position in [warning](unknown.zs). !*/
sql_table Table
{
    int32 id sql "PRIMARY KEY NOT NULL";
    Test test;
};

/*!
    Extra indent within markdown [Unknown](unknown.zs).
!*/
sql_database Database
{
    /*!
    Indented markdown [link](unknown.zs#Unknown).
    !*/
    Table table1;

    /*!
    Indent within the markdonw
  is [broken](unknown.zs#Unknown).
    !*/
    Table table2;

    /*!
        Additional [indent](unknown.zs) within indented markdown.
    !*/
    Table table3;

    /*! Indented markdown one-liner to check [link](unknown.zs#Unknown) position !*/
    Table table4;
};
