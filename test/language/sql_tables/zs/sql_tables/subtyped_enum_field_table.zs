package sql_tables.subtyped_enum_field_table;

enum uint32 TestEnum
{
    ONE,
    TWO,
    THREE
};

subtype TestEnum SubtypedEnum;

sql_table SubtypedEnumFieldTable
{
    int32           id          sql "PRIMARY KEY";
    SubtypedEnum    enumField;
};
