package parameterized_types.packed_array_element_param;

struct Database
{
    uint16                          numBlocks;
    BlockHeader                     headers[numBlocks];
    packed Block(headers[@index])   blocks[numBlocks];
};

struct BlockHeader
{
    uint16  numItems;
    uint32  offset;
};

struct Block(BlockHeader header)
{
    uint64 value;
header.offset:
    packed int64 items[header.numItems];
};
