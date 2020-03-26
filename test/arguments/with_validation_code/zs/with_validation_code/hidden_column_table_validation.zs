package with_validation_code.hidden_column_table_validation;

sql_table HiddenColumnTable using fts4
{
    sql_virtual int64       docId sql "NOT NULL";
    sql_virtual varuint16   languageCode sql "NOT NULL";

    string                  substitutionId sql "NOT NULL";
    string                  searchTags;
    uint32                  frequency;

    sql "languageid='languageCode', notindexed='substitutionId', notindexed='frequency'";
};

sql_database HiddenColumnTableValidationDb
{
    HiddenColumnTable hiddenColumnTable;
};
