package array_types.arrays_mapping;

struct TestStructure
{
    uint32  id;
    string  name;
};

enum uint8 TestEnum
{
    VALUE1,
    VALUE2,
    VALUE3
};

struct ArraysMapping
{
    // unsigned integer arrays
    uint8                       uint8Array[5];
    uint16                      uint16Array[5];
    uint32                      uint32Array[5];
    uint64                      uint64Array[5];

    // signed integer arrays
    int8                        int8Array[5];
    int16                       int16Array[5];
    int32                       int32Array[5];
    int64                       int64Array[5];

    // unsigned bitfield arrays
    bit:8                       bitfield8Array[5];
    bit:16                      bitfield16Array[5];
    bit:32                      bitfield32Array[5];
    bit:63                      bitfield63Array[5];
    uint8                       uint8Value;
    bit<uint8Value>             variableBitfieldLongArray[5];
    // the following three fields check upper bound evaluation of expressions
    bit<getNumBitsForInt()>     variableBitfieldIntArray[5];
    bit<getNumBitsForShort()>   variableBitfieldShortArray[5];
    bit<getNumBitsForByte()>    variableBitfieldByteArray[5];

    // signed bitfield arrays
    int:8                       intfield8Array[5];
    int:16                      intfield16Array[5];
    int:32                      intfield32Array[5];
    int:64                      intfield64Array[5];
    int<uint8Value>             variableIntfieldLongArray[5];
    // the following three fields check upper bound evaluation of expressions
    int<getNumBitsForInt()>     variableIntfieldIntArray[5];
    int<getNumBitsForShort()>   variableIntfieldShortArray[5];
    int<getNumBitsForByte()>    variableIntfieldByteArray[5];

    // float array
    float16                     float16Array[5];

    // variable unsigned integer arrays
    varuint16                   varuint16Array[5];
    varuint32                   varuint32Array[5];
    varuint64                   varuint64Array[5];

    // variable signed integer arrays
    varint16                    varint16Array[5];
    varint32                    varint32Array[5];
    varint64                    varint64Array[5];

    // boolean array
    bool                        boolArray[5];

    // string arrays
    string                      stringArray[5];

    // compound array
    TestStructure                compoundArray[5];

    // enumeration array
    TestEnum                    enumArray[5];

    function uint8 getNumBitsForInt()
    {
        // upper bound is 18
        return (uint8Value == 0) ? 1 : uint8Value / 15 + 1;
    }

    function uint8 getNumBitsForShort()
    {
        // upper bound is 16
        return uint8Value / 15 - 1;
    }

    function uint8 getNumBitsForByte()
    {
        // upper bound is 7
        return numbits(uint8Value);
    }
};
