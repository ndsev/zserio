package allow_implicit_arrays.table_with_implicit_array;

struct StructWithImplicit
{
    implicit uint32 array[];
};

sql_table TableWithImplicitArray
{
    uint32 id sql "PRIMARY KEY NOT NULL";
    StructWithImplicit structWithImplicit; // intentionally not last to check that it pass through check in core
    string text;
};

sql_database DbWithImplicitArray
{
    TableWithImplicitArray tableWithImplicitArray;
};
