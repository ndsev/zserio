package with_validation_code.virtual_table_validation;

sql_table TestTable using fts4
{
    string text;
};

sql_table VirtualTable using fts4aux
{
    sql_virtual string term sql "NOT NULL";
    sql_virtual string col sql "NOT NULL";
    sql_virtual uint32 documents sql "NOT NULL";
    sql_virtual uint32 occurrences sql "NOT NULL";

    sql "TestTable";
};

sql_database VirtualTableValidationDb
{
    TestTable    testTable;
    VirtualTable virtualTable;
};
