package sql_tables.subtyped_bool_field_table;

subtype bool SubtypedBool;

sql_table SubtypedBoolFieldTable
{
    int32           id    sql "PRIMARY KEY";
    SubtypedBool    boolField;
};
