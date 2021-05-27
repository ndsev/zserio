package array_types.packed_variable_array_struct_recursion;

struct Block(uint8 byteCount)
{
    uint8   dataBytes[byteCount];
    uint8   blockTerminator;

    Block(blockTerminator) nextData if blockTerminator > 0; 
};

struct PackedVariableArray
{
    uint8 byteCount;
    varsize numElements;
    packed Block(byteCount) packedBlocks[numElements];
};
