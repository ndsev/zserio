package array_types.variable_array_struct_cast_int8;

struct TestStructure
{
    uint32  id;
    string  name;
};

struct VariableArray
{
    // this is intentionally signed type to check if Zserio will handle casting correctly
    int8            numElements;
    TestStructure   compoundArray[numElements];
};
