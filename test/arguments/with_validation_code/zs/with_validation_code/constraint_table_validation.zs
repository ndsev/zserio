package with_validation_code.constraint_table_validation;

// This checks that if some constraints fail in validated table, validating will continue without any exception.

struct Blob
{
    uint8   value : value > 0 && value < 10;
};

sql_table ConstraintTable
{
    uint32  id sql "PRIMARY KEY NOT NULL";
    Blob    blob sql "NOT NULL";
};

sql_database ConstraintTableValidationDb
{
    ConstraintTable constraintTable;
};
