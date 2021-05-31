package array_types.packed_arrays_mapping;

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

bitmask uint16 TestBitmask
{
    MASK1,
    MASK2 = 0x80,
    MASK3
};

struct PackedArraysMapping
{
    // packed unsigned integer arrays
    packed uint8            uint8Array[5];
    packed uint16           uint16Array[5];
    packed uint32           uint32Array[5];
    packed uint64           uint64Array[5];

    // packed signed integer arrays
    packed int8             int8Array[5];
    packed int16            int16Array[5];
    packed int32            int32Array[5];
    packed int64            int64Array[5];

    // packed unsigned bitfield arrays
    packed bit:8            bitfield8Array[5];
    packed bit:16           bitfield16Array[5];
    packed bit:32           bitfield32Array[5];
    packed bit:63           bitfield63Array[5];
    uint8                   uint8Value;
    packed bit<uint8Value>  variableBitfieldLongArray[5];

    // packed signed bitfield arrays
    packed int:8            intfield8Array[5];
    packed int:16           intfield16Array[5];
    packed int:32           intfield32Array[5];
    packed int:64           intfield64Array[5];
    packed int<uint8Value>  variableIntfieldLongArray[5];

    // packed variable unsigned integer arrays
    packed varuint16        varuint16Array[5];
    packed varuint32        varuint32Array[5];
    packed varuint64        varuint64Array[5];
    packed varuint          varuintArray[5];
    packed varsize          varsizeArray[5];

    // packed variable signed integer arrays
    packed varint16         varint16Array[5];
    packed varint32         varint32Array[5];
    packed varint64         varint64Array[5];
    packed varint           varintArray[5];

    // packed compound array
    packed TestStructure    compoundArray[5];

    // packed enumeration array
    packed TestEnum         enumArray[5];

    // packed bitmask array
    packed TestBitmask      bitmaskArray[5];
};
