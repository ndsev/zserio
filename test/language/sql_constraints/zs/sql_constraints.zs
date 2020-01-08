package sql_constraints;

import sql_constraints.constraints.*;

enum uint8 ConstraintsEnum
{
    VALUE1,
    VALUE2
};

bitmask uint16 ConstraintsBitmask
{
    MASK1,
    MASK2
};

const uint16 ConstraintsConstant = 123;

sql_table ConstraintsTable
{
    int32               primaryKey               sql "PRIMARY KEY";
    int32               withoutSql;
    int32               sqlNotNull               sql "NOT NULL";
    int32               sqlDefaultNull           sql "DEFAULT NULL";
    int32               sqlNull                  sql "NULL";
    uint16              sqlCheckConstant         sql "CHECK(sqlCheckConstant < @ConstraintsConstant)";
    uint32              sqlCheckImportedConstant sql "CHECK(sqlCheckImportedConstant <
                                                  @sql_constraints.constraints.ImportedConstant)";
    ConstraintsEnum     sqlCheckEnum             sql "CHECK(sqlCheckEnum == @ConstraintsEnum.VALUE1)";
    ImportedEnum        sqlCheckImportedEnum     sql "CHECK(sqlCheckImportedEnum ==
                                                        @sql_constraints.constraints.ImportedEnum.ONE)";
    ConstraintsBitmask  sqlCheckBitmask          sql "CHECK(sqlCheckBitmask ==
                                                        @sql_constraints.ConstraintsBitmask.MASK1)";
    ImportedBitmask     sqlCheckImportedBitmask  sql "CHECK(sqlCheckImportedBitmask == @ImportedBitmask.MASK1)";
    uint8               sqlCheckUnicodeEscape    sql "CHECK(sqlCheckUnicodeEscape == \u0031)";
    uint8               sqlCheckHexEscape        sql "CHECK(sqlCheckHexEscape == \x32)";
    uint8               sqlCheckOctalEscape      sql "CHECK(sqlCheckOctalEscape == \063)";
};

sql_database TestDb
{
    ConstraintsTable        constraintsTable;
};
