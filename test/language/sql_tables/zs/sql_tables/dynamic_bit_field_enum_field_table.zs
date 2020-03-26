package sql_tables.dynamic_bit_field_enum_field_table;

enum bit<3> TestEnum
{
    ONE,
    TWO,
    THREE
};

sql_table DynamicBitFieldEnumFieldTable
{
    uint32      id          sql "PRIMARY KEY NOT NULL";
    TestEnum    enumField;
};
