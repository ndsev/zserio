package builtin_types.all_builtin_types;

struct ExternalStructure
{
    uint8           value;
    bit:2           rest;
};

struct AllBuiltInTypes
{
    // boolean type
    bool            boolType;

    // unsigned integer types
    uint8           uint8Type;
    uint16          uint16Type;
    uint32          uint32Type;
    uint64          uint64Type;

    // signed integer types
    int8            int8Type;
    int16           int16Type;
    int32           int32Type;
    int64           int64Type;

    // unsigned bitfield types
    bit:7           bitfield7Type;
    bit:8           bitfield8Type;
    bit:15          bitfield15Type;
    bit:16          bitfield16Type;
    bit:31          bitfield31Type;
    bit:32          bitfield32Type;
    bit:63          bitfield63Type;

    // bitfield type which has length upper bound equal to 255 and must be mapped to 64-bits native types
    // (Java = BigInteger, C++ = uint64_t)
    bit<uint8Type>  variableBitfieldType;

    // bitfield type which has length defined by constant
    bit<NUM_BITS>   variableBitfield8Type;

    // signed bitfield types
    int:8           intfield8Type;
    int:16          intfield16Type;
    int:32          intfield32Type;
    int:64          intfield64Type;

    // bitfield type which has length upper bound equal to (127 + 1) / 16 + 4 * 2 - 2 = 14 and must be mapped
    // to 16-bits native types (Java = short, C++ = int16_t)
    // (note: this field is optional to catch mapping error in Java)
    int<(bitfield7Type + 1) / 16 + 4 * 2 - 2> variableIntfieldType if boolType == true;

    // bitfield type which has length defined by constant
    int<NUM_BITS>   variableIntfield8Type;

    // float types
    float16         float16Type;
    float32         float32Type;
    float64         float64Type;

    // variable unsigned integer types
    varuint16       varuint16Type;
    varuint32       varuint32Type;
    varuint64       varuint64Type;
    varuint         varuintType;
    varsize         varsizeType;

    // variable signed integer types
    varint16        varint16Type;
    varint32        varint32Type;
    varint64        varint64Type;
    varint          varintType;

    // string types
    string          stringType;

    // extern type
    extern          externType;
};

const uint8 NUM_BITS = 8;
