package templates.subtype_template_with_builtin;

struct TestStructure<T>
{
    T value;
};

subtype TestStructure<uint32> TestStructureSubtype;

struct SubtypeTemplateWithBuiltin
{
    TestStructureSubtype test;
};
