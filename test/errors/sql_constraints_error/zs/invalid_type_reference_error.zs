package invalid_type_reference_error;

const uint16 ConstraintsConstant = 123;

sql_table ConstraintsTable
{
    int32       withoutSql;
    uint16      sqlCheckConstant sql "CHECK(sqlCheckConstant < @ConstraintsConstant.wrongFieldName)";
};
