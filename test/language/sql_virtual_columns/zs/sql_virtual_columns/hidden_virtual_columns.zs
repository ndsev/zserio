package sql_virtual_columns.hidden_virtual_columns;

sql_table HiddenVirtualColumnsTable using fts4
{
    sql_virtual int64       docId;
    sql_virtual varuint16   languageCode;

    string                  searchTags sql "NULL";
    uint32                  frequency sql "NULL";

    sql "languageid='languageCode', notindexed='frequency'";
};

sql_database HiddenVirtualColumnsDb
{
    HiddenVirtualColumnsTable hiddenVirtualColumnsTable;
};
