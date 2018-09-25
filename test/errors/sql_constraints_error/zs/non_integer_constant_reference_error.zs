package non_integer_constant_reference_error;

const float16 ConstraintsConstant = 12.3;

sql_table ConstraintsTable
{
    int32       withoutSql;
    uint16      sqlCheckConstant sql "CHECK(sqlCheckConstant < @ConstraintsConstant)";
};
