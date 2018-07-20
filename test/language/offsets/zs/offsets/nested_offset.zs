package offsets.nested_offset;

struct NestedOffsetStructure
{
    uint32  dataOffset;

dataOffset:
    bit:31  data;
};

struct NestedOffsetArrayStructure
{
    uint8                 numElements;
    NestedOffsetStructure nestedOffsetStructureList[numElements];
};

union NestedOffsetUnion
{
    int8                        dummy;
    NestedOffsetArrayStructure  nestedOffsetArrayStructure;
};

choice NestedOffsetChoice(bool type) on type
{
    case false:
        uint8 dummy;

    default:
        NestedOffsetUnion nestedOffsetUnion;
};

struct NestedOffset
{
    uint32  terminatorOffset;
    bool    boolValue;

    NestedOffsetChoice(boolValue) nestedOffsetChoice;

terminatorOffset:
    bit:7   terminator;
};
