package invalid_integer_constraint_error;

sql_table ConstraintsTable
{
    int32  primaryKey;
    uint16 sqlInvalidConstraint sql 10 + 10;
};
