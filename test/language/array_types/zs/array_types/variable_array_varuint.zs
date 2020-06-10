package array_types.variable_array_varuint;

struct TestStructure
{
    uint32  id;
    string  name;
};

struct VariableArray
{
    // this is intentionally varuint to check if Zserio will handle casting in Java correctly
    varuint         numElements;
    TestStructure   compoundArray[numElements];
};
