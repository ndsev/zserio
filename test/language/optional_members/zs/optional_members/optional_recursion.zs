package optional_members.optional_recursion;

struct Block(uint8 byteCount)
{
    uint8   dataBytes[byteCount];
    uint8   blockTerminator;

    Block(blockTerminator) nextData if blockTerminator > 0;
};
