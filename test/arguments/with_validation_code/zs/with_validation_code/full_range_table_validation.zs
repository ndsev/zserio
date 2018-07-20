package with_validation_code.full_range_table_validation;

sql_table FullRangeTable
{
    int64   fullSigned      sql "PRIMARY KEY";
    uint64  fullUnsigned;
    string  message;
};

sql_database FullRangeTableValidationDb
{
    FullRangeTable fullRangeTable;
};
