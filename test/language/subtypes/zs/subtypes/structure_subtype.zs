package subtypes.structure_subtype;

struct TestStructure
{
    uint32      identifier;
    string      name;
};

subtype TestStructure Student;

struct SubtypeStructure
{
    Student     student;
};
