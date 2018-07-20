package no_primary_key_column_error;

// Without rowid table without primary key column should be compilation error.
sql_table WrongWithoutRowIdTable
{
    string      word;
    uint32      count;

    sql_without_rowid;
};
