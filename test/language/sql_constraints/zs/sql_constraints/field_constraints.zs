package sql_constraints.field_constraints;

sql_table FieldConstraintsTable
{
    int32               primaryKey               sql "PrImArY  KEY NOT NULL"; // spaces and cases are intended
    int32               withoutSql;
    int32               sqlNotNull               sql "NOT  NulL"; // spaces and cases are intended
    int32               sqlDefaultNull           sql "DEFAULT NULL";
    uint8               sqlCheckUnicodeEscape    sql "CHECK(sqlCheckUnicodeEscape == \u0031)";
    uint8               sqlCheckHexEscape        sql "CHECK(sqlCheckHexEscape == \x32)";
    uint8               sqlCheckOctalEscape      sql "CHECK(sqlCheckOctalEscape == \063)";
};
