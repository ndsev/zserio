package parameterized_types.compound_and_field_with_same_param;

subtype uint32 Param;

struct Field(Param param)
{
    uint32 value : value < param;
};

struct Compound(Param param)
{
    Field(param) field1;
    Field(param) field2;
};

struct SameParamTest
{
    // C++: Used constant literal to cause selection of field constructor instead of read constructor
    //      in case that the field constructor is not properly guarded by enable_if.
    //      Note that the literal will have type 'int' which differs from 'uint32_t' which is the param type.
    Compound(10) compound;
};
