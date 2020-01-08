package sql_tables.subtyped_bitmask_field_table;

bitmask uint32 TestBitmask
{
    ONE,
    TWO,
    THREE
};

subtype TestBitmask SubtypedBitmask;

sql_table SubtypedBitmaskFieldTable
{
    int32           id          sql "PRIMARY KEY";
    SubtypedBitmask    bitmaskField;
};
