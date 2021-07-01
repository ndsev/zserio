package with_validation_code.full_range_table_validation;

sql_table FullRangeTable
{
    int64   fullSigned sql "PRIMARY KEY NOT NULL";
    uint64  fullUnsigned sql "NOT NULL";
    varint  fullVarSigned sql "NOT NULL";
    varuint fullVarUnsigned sql "NOT NULL"; 
    string  message sql "NOT NULL";
};

sql_database FullRangeTableValidationDb
{
    FullRangeTable fullRangeTable;
};
