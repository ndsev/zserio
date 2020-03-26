package with_validation_code.virtual_table_validation;

sql_table VirtualTable using fts4aux
{
    sql_virtual string term sql "NOT NULL";
    sql_virtual string col sql "NOT NULL";
    sql_virtual uint32 documents sql "NOT NULL";
    sql_virtual uint32 occurrences sql "NOT NULL";

    sql "VirtualTable";
};

sql_database VirtualTableValidationDb
{
    VirtualTable virtualTable;
};
