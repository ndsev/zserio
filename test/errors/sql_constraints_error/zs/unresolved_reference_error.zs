package unresolved_reference_error;

import constraint_constant.SomeStructure;

sql_table ConstraintsTable
{
    int32       withoutSql;
    uint16      sqlCheckConstant sql "CHECK(sqlCheckConstant < @ConstraintsConstant)";
};
