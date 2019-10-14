package array_types.subtyped_struct_variable_array;

struct TestStructure
{
    uint32  id;
    string  name;
};

subtype TestStructure ArrayElement;

struct SubtypedStructVariableArray
{
    uint8           numElements;
    ArrayElement    compoundArray[numElements];
};
