package sql_constraints.field_constraints;

import sql_constraints.constraint_imports.*;

const string NOT_NULL_CONSTRAINT = "NOT NULL";
const string CONSTRAINTS_CONSTANT = "123";

sql_table FieldConstraintsTable
{
    int32               primaryKey               sql "PrImArY  key " + // spaces and cases are intended
                                                      NOT_NULL_CONSTRAINT;
    int32               withoutSql;
    int32               sqlNotNull               sql "not  nulL"; // spaces and cases are intended
    int32               sqlDefaultNull           sql "DEFAULT NULL";
    uint16              sqlCheckConstant         sql "CHECK(sqlCheckConstant < " + CONSTRAINTS_CONSTANT + ")";
    uint32              sqlCheckImportedConstant sql "CHECK(sqlCheckImportedConstant < " +
                                                      sql_constraints.constraint_imports.IMPORTED_CONSTRAINTS_CONSTANT +
                                                      ")";
    uint8               sqlCheckUnicodeEscape    sql "CHECK(sqlCheckUnicodeEscape == \u0031)";
    uint8               sqlCheckHexEscape        sql "CHECK(sqlCheckHexEscape == \x32)";
    uint8               sqlCheckOctalEscape      sql "CHECK(sqlCheckOctalEscape == \063)";
};
