package subtyped_table_field_error;

sql_table TestTable
{
    int32       schoolId;
    int32       classId;
    int32       studentId;

    sql "PRIMARY KEY (schoolId)";
};

subtype TestTable SubtypedTable;

struct TableFieldError
{
    // This must cause an error.
    SubtypedTable subtypedTestTable;
};
