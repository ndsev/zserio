package comments.documentation.sql_table_comments;

/** Virtual table comment. */
sql_table VirtualTable using fts4aux
{
    /** Virtual field comment. */
    sql_virtual string term;

    sql "VirtualTable";
};

