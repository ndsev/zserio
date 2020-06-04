package invalid_string_constraint_error;

sql_table ConstraintsTable
{
    int32  primaryKey;
    string valueInString;
    uint16 sqlCheckConstant sql valueInString;
};
