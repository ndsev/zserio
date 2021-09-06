package parameterized_types.compound_and_field_with_same_param;

subtype uint32 Param;

struct Field(Param param)
{
    uint32 value : value < param;
};

struct CompoundRead(Param param)
{
    Field(param) field1;
    Field(param) field2;
};

struct CompoundPacking(Param param)
{
    Field(param) field1;
    Field(param) field2;
    Field(param) field3;
};

struct CompoundReadTest
{
    // C++: Used constant literal to cause selection of field constructor instead of read constructor
    //      in case that the field constructor is not properly guarded by enable_if.
    //      Note that the literal will have type 'int' which differs from 'uint32_t' which is the param type.
    CompoundRead(10) compoundRead;
};

struct CompoundPackingTest
{
    // C++: Used constant literal to cause selection of field constructor instead of packing context constructor
    //      in case that the field constructor is not properly guarded by enable_if.
    //      Note that the literal will have type 'int' which differs from 'uint32_t' which is the param type.
    CompoundPacking(10) compoundPacking;
};
