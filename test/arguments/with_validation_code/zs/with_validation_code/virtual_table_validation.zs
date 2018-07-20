package with_validation_code.virtual_table_validation;

sql_table VirtualTable using fts4aux
{
    sql_virtual string term;
    sql_virtual string col;
    sql_virtual uint32 documents;
    sql_virtual uint32 occurrences;

    sql "VirtualTable";
};

sql_database VirtualTableValidationDb
{
    VirtualTable virtualTable;
};
