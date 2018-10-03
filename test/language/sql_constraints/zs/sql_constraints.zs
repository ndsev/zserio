package sql_constraints;

enum uint8 ConstraintsEnum
{
    VALUE1,
    VALUE2
};

const uint16 ConstraintsConstant = 123;

sql_table ConstraintsTable
{
    int32           primaryKey              sql "PRIMARY KEY";
    int32           withoutSql;
    int32           sqlNotNull              sql "NOT NULL";
    int32           sqlDefaultNull          sql "DEFAULT NULL";
    int32           sqlNull                 sql "NULL";
    uint16          sqlCheckConstant        sql "CHECK(sqlCheckConstant < @ConstraintsConstant)";
    ConstraintsEnum sqlCheckEnum            sql "CHECK(sqlCheckEnum == @ConstraintsEnum.VALUE1)";
    uint8           sqlCheckUnicodeEscape   sql "CHECK(sqlCheckUnicodeEscape == \u0031)";
    uint8           sqlCheckHexEscape       sql "CHECK(sqlCheckHexEscape == \x32)";
    uint8           sqlCheckOctalEscape     sql "CHECK(sqlCheckOctalEscape == \063)";
};

sql_database TestDb
{
    ConstraintsTable        constraintsTable;
};
