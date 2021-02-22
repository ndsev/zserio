package comments.markdown_doc.sql_table_comments;

/*!

**VirtualTable**

Virtual table comment.

!*/
sql_table VirtualTable using fts4aux
{
    /*! Virtual field comment. !*/
    sql_virtual string term;

    sql "VirtualTable";
};
