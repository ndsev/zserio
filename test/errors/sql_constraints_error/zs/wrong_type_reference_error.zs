package wrong_type_reference_error;

struct ConstraintsConstant
{
    uint16  constraintsConstant;
};

sql_table ConstraintsTable
{
    int32       withoutSql;
    uint16      sqlCheckConstant sql "CHECK(sqlCheckConstant < @ConstraintsConstant)";
};
