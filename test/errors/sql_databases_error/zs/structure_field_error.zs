package structure_field_error;

sql_table TestTable
{
    int32       data sql "PRIMARY KEY NOT NULL";
};

struct TestStructure
{
    int32       schoolId;
    int32       classId;
    int32       studentId;
};

sql_database StructureFieldError
{
    TestTable   testTable; // OK
    // This must cause an error. Structure cannot be a field of SQL database.
    TestStructure    testStructure;
};
