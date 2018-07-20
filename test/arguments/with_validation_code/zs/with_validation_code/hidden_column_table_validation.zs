package with_validation_code.hidden_column_table_validation;

sql_table HiddenColumnTable using fts4
{
    sql_virtual int64       docId;
    sql_virtual varuint16   languageCode;

    string                  substitutionId;
    string                  searchTags sql "NULL";
    uint32                  frequency sql "NULL";

    sql "languageid='languageCode', notindexed='substitutionId', notindexed='frequency'";
};

sql_database HiddenColumnTableValidationDb
{
    HiddenColumnTable hiddenColumnTable;
};
