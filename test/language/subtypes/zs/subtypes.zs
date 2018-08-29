package subtypes;

subtype uint16 Identifier;

struct TestStructure
{
    Identifier  identifier;
    string      name;
};

subtype TestStructure Student;

struct SubtypeStructure
{
    Student     student;
};

sql_table TestTable
{
    int32               id
            sql "PRIMARY KEY";
    SubtypeStructure    student;
};

subtype TestTable SubtypedTable;

sql_database Database
{
    SubtypedTable students;
};

// check C++ type mapping - we want to use the subtype as a typedef
struct ParameterizedStruct(uint32 length)
{
    int32 array[length];
};

subtype ParameterizedStruct ParameterizedSubtype;

struct ParameterizedSubtypeStruct
{
    int32                           length;
    ParameterizedSubtype(length)    parameterizedSubtype;
    ParameterizedSubtype(length)    parameterizedSubtypeArray[];
};
