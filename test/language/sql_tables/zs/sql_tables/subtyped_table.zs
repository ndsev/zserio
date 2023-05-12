package sql_tables.subtyped_table;

struct Student
{
    uint16      identifier;
    string      name;
};

sql_table TestTable
{
    Student     student sql "PRIMARY KEY NOT NULL";
};

subtype TestTable SubtypedTable;
