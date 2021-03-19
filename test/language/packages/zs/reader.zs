package reader;

// this test verifies that top level package can be safely named "reader"
// - it caused an error in python where it is problem when a method parameter has same name as the top
//   level package, in this case e.g. the read() method had a parameter "reader" and than the top level packages
//   was hidden by the parameter and it wasn't possible to access to a field type via fully specified qualifier

struct Element
{
    uint32 field;
};

struct ParameterizedElement(uint32 index)
{
    uint32 field : field == index;
};

struct Test
{
    uint32 indexes[];
indexes[@index]:
    Element array[];

    uint32 indexesForParameterized[];
indexesForParameterized[@index]:
    ParameterizedElement(@index) parameterizedArray[];
};
