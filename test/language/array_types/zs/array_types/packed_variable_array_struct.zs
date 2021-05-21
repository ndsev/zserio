package array_types.packed_variable_array_struct;

choice TestChoice(uint32 selector) on selector
{
    case 0:
    case 2:
    case 4:
        uint16 value16;

    default:
        uint32 value32;
};

union TestUnion
{
    uint16 value16;
    uint32 value32;
};

enum uint8 TestEnum
{
    NONE       = 0,
    DARK_RED   = 1,
    DARK_BLUE  = 2,
    DARK_GREEN = 3
};

bitmask uint8 TestBitmask
{
    NONE   = 000b,
    READ   = 010b,
    WRITE  = 100b,
    CREATE = 111b
};

struct TestStructure
{
    uint32         id;
    string         name;
    TestChoice(id) testChoice;
    TestUnion      testUnion;
    TestEnum       testEnum;
    TestBitmask    testBitmask;
    optional uint8 testOptional;
    varsize        numValues;
    varuint        unpackedValues[numValues];
    packed varuint packedValues[numValues];
};

struct TestUnpackedArray(varsize numElements)
{
    TestStructure array[numElements];
};

struct TestPackedArray(varsize numElements)
{
    packed TestStructure array[numElements];
};

struct PackedVariableArray
{
    varsize numElements;
    TestUnpackedArray(numElements) testUnpackedArray;
    TestPackedArray(numElements) testPackedArray;
};
