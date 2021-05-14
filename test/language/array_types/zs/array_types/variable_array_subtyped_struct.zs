package array_types.variable_array_subtyped_struct;

struct TestStructure
{
    uint32  id;
    string  name;
};

subtype TestStructure ArrayElement;

struct VariableArray
{
    uint8           numElements;
    ArrayElement    compoundArray[numElements];
};
