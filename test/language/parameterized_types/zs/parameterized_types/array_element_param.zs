package parameterized_types.array_element_param;

struct Database
{
    uint16                          numBlocks;
    BlockHeader                     headers[numBlocks];
    Block(headers[@index])          blocks[numBlocks];
};

struct BlockHeader
{
    uint16  numItems;
    uint32  offset;
};

struct Block(BlockHeader header)
{
header.offset:
    int64   items[header.numItems];
};
