package subtyped_table_field_error;

sql_table TestTable
{
    int32       schoolId;
    int32       classId;
    int32       studentId;

    sql "PRIMARY KEY (schoolId)";
};

subtype TestTable SubtypedTable;

sql_table TableFieldError
{
    // This must cause an error. SQL database cannot be instantiated.
    SubtypedTable subtypedTestTable;
};

