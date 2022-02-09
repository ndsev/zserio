package array_types.packed_variable_array_struct;

struct Value32
{
    uint32 value32;
};

choice TestChoice(uint32 selector) on selector
{
    case 0:
    case 2:
    case 4:
        uint16 value16;

    case 5:
        packed uint32 array32[2];

    default:
        Value32 value32;
};

union TestUnion
{
    uint16 value16;
    packed uint32 array32[2];
    Value32 value32;
};

enum uint8 TestEnum
{
    NONE       = 0,
    DARK_RED   = 1,
    DARK_BLUE  = 2,
    DARK_GREEN = 3
};

const bit:4 BITS_PER_BYTE = 8;

bitmask bit<BITS_PER_BYTE> TestBitmask
{
    NONE   = 000b,
    READ   = 010b,
    WRITE  = 100b,
    CREATE = 111b
};

subtype bit:15 Bit15;

struct Empty
{
};

struct TestStructure
{
    uint32          id;
    string          name;
    extern          data;
    TestChoice(id)  testChoice;
    TestUnion       testUnion;
    TestEnum        testEnum;
    TestBitmask     testBitmask;
    optional Bit15  testOptional;
    bit<length()>   testDynamicBitfield;
    varsize         numValues;
    varuint         unpackedValues[numValues];
    packed varuint  packedValues[numValues];
    packed Empty    packedEmpties[numValues];

    function bit:5 length()
    {
        // TODO[mikir]: Should be '%' or '&' as soon as it will work
        return id <= 385 ? (id / 0x07) + 9 : 64;
    }
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
