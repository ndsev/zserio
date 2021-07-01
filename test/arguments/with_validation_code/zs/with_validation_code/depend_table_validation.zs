package with_validation_code.depend_table_validation;

// This checks that if some dependent column in validated table is null, validating will continue without
// any exception.

struct Blob(varsize size)
{
    uint32 array[size];
};

sql_table DependTable
{
    uint32       id sql "PRIMARY KEY NOT NULL";
    bit:3        numBits; // should be "NOT NULL"
    int<numBits> value sql "NOT NULL";
    varsize      size;
    Blob(size)   blob;
};

sql_database DependTableValidationDb
{
    DependTable dependTable;
};
