package sql_types;

enum int8 TestEnum
{
    UNDEFINED   = 0,
    RED         = 1,
    BLUE        = 2,
    BLACK       = 3
};

bitmask uint8 TestBitmask
{
    READ,
    WRITE,
    CREATE = 3
};

struct TestStructure
{
    uint32  id;
    string  name;
};

choice TestChoice(bool selector) on selector
{
    case false:
        uint8   value8;

    case true:
        uint16  value16;
};

union TestUnion
{
    uint8   value8;
    uint16  value16;
};

sql_table SqlTypesTable
{
    // unsigned integer types
    uint8           uint8Type   sql "PRIMARY KEY NOT NULL";
    uint16          uint16Type;
    uint32          uint32Type;
    uint64          uint64Type;

    // signed integer types
    int8            int8Type;
    int16           int16Type;
    int32           int32Type;
    int64           int64Type;

    // unsigned bitfield types
    bit:8           bitfield8Type;
    bit<uint8Type>  variableBitfieldType;

    // signed bitfield types
    int:8           intfield8Type;
    int<uint8Type>  variableIntfieldType;

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

    // boolean type
    bool            boolType;

    // string types
    string          stringType;

    // enum type
    TestEnum        enumType;

    // bitmask type
    TestBitmask     bitmaskType;

    // structure type
    TestStructure   structureType;

    // choice type
    TestChoice(false)   choiceType;

    // union type
    TestUnion       unionType;
};

sql_database SqlTypesDb
{
    SqlTypesTable   sqlTypesTable;
};
