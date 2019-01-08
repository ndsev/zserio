package subtypes.param_structure_subtype;

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
