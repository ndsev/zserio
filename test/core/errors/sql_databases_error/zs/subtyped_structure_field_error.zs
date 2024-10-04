package subtyped_structure_field_error;

struct TestStructure
{
    int32       schoolId;
    int32       classId;
    int32       studentId;
};

subtype TestStructure SubtypedStructure;

sql_database StructureFieldError
{
    // This must cause an error. Structure cannot be a field of SQL database.
    SubtypedStructure subtypedTestStructure;
};
