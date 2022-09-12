package comments.markdown_doc.sql_database_comments;

/*!

**Table**

Sql table comment.

!*/
sql_table Table
{
    /*! Id comment. !*/
    int32 id sql "PRIMARY KEY NOT NULL";
};

/*!

**Db**

DB comment.

!*/
sql_database Db
{
    /*! Table field comment. !*/
    Table table;

    /*! Another table field comment. !*/
    Table anotherTable;
};
