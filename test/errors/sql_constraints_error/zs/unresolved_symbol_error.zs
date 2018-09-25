package unresolved_symbol_error;

enum uint8 ConstraintsEnum
{
    VALUE1,
    VALUE2
};

sql_table ConstraintsTable
{
    int32           withoutSql;
    ConstraintsEnum sqlCheckEnum sql "CHECK(sqlCheckEnum == @ConstraintsEnum.VALUE3)";
};
