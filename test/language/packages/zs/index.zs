package index;

// this test verifies that top level package can be safely named "index"
// - it causes a problem in C++ since gtest.h includes strings.h, which contains index global function
//   and therefore we have to use -setTopLevelPackage as a workaround
// - it seems dangerous in Java where in the ElementFactory_parameterizedArray.create there is a parameter
//   named "index", however it works
// - Python uses guarded parameters names (i.e. prefixed wtih "zserio_")

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
