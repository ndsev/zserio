package sql_constraints.field_constraints;

import sql_constraints.constraint_imports.*;

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

sql_table FieldConstraintsTable
{
    int32               primaryKey               sql "PrImArY  KEY NOT NULL"; // spaces and cases are intended
    int32               withoutSql;
    int32               sqlNotNull               sql "NOT  NulL"; // spaces and cases are intended
    int32               sqlDefaultNull           sql "DEFAULT NULL";
    uint16              sqlCheckConstant         sql "CHECK(sqlCheckConstant < @ConstraintsConstant)";
    uint32              sqlCheckImportedConstant sql "CHECK(sqlCheckImportedConstant < @sql_constraints.constraint_imports.ImportedConstant)";
    ConstraintsEnum     sqlCheckEnum             sql "CHECK(sqlCheckEnum == @ConstraintsEnum.VALUE1)";
    ImportedEnum        sqlCheckImportedEnum     sql "CHECK(sqlCheckImportedEnum == @sql_constraints.constraint_imports.ImportedEnum.ONE)";
    ConstraintsBitmask  sqlCheckBitmask          sql "CHECK(sqlCheckBitmask == @sql_constraints.field_constraints.ConstraintsBitmask.MASK1)";
    ImportedBitmask     sqlCheckImportedBitmask  sql "CHECK(sqlCheckImportedBitmask == @ImportedBitmask.MASK1)";
    uint8               sqlCheckUnicodeEscape    sql "CHECK(sqlCheckUnicodeEscape == \u0031)";
    uint8               sqlCheckHexEscape        sql "CHECK(sqlCheckHexEscape == \x32)";
    uint8               sqlCheckOctalEscape      sql "CHECK(sqlCheckOctalEscape == \063)";
};
