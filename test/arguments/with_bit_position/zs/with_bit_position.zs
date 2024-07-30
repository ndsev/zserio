package with_bit_position;

struct Item
{
    uint32        value;
    optional int8 opt;
};

struct ItemHolder
{
    uint16 size;
    Item   items[size];
};
